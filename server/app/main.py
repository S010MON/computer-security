import logging

from fastapi import FastAPI, HTTPException, Request
import uvicorn
import time
import hashlib
from slowapi import Limiter, _rate_limit_exceeded_handler
from slowapi.util import get_remote_address
from slowapi.errors import RateLimitExceeded
from app.models import ChangeRequest, AuthRequest, User

limiter = Limiter(key_func=get_remote_address)
app = FastAPI()
app.state.limiter = limiter
app.add_exception_handler(RateLimitExceeded, _rate_limit_exceeded_handler)


@app.get("/", status_code=418)
@limiter.limit("10/second")
async def root(request: Request):
    logActivity("I am a teapot")
    return {"message": "I am a teapot"}


@app.post("/auth", status_code=201)
@limiter.limit("10/second")
async def login(authRequest: AuthRequest, request: Request):
    print(authRequest)
    pwd_hash = hash_password(authRequest.password)

    # If a new user
    if authRequest.id not in users:
        jwt = hash_user(authRequest.id, authRequest.password)
        user = User(authRequest.id, pwd_hash, jwt, authRequest.server, authRequest.actions)
        users[authRequest.id] = user
        logActivity(f"New user created with id: {authRequest.id}")
        return {'jwt': jwt}

    # If existing user
    else:
        user = users[authRequest.id]
        if user.check_password(pwd_hash):
            jwt = hash_user(authRequest.id, authRequest.password)
            user.new_client(jwt, authRequest.server, authRequest.actions)
            logActivity(f"User with id: {authRequest.id} logged in")
            return {'jwt': jwt}

        # If user is not authorised
        user.failed_auth()
        if user.locked():
            logActivity(f"User not authorized. Account locked for {user.lock_time} minutes")
            raise HTTPException(status_code=401, detail=f"Account has been locked for {user.lock_time} mins")
        logActivity(f"User with id: {authRequest.id} unauthorized")
        raise HTTPException(status_code=404, detail='User not found')


@app.post("/increase", status_code=200)
@limiter.limit("10/second")
async def increase(changeRequest: ChangeRequest, request: Request):
    ip = request.client.host
    port = request.client.port

    if changeRequest.id not in users:
        logActivity(f"Increase for user id: {changeRequest.id} not authorized")
        raise HTTPException(status_code=404, detail='Not found')

    user = users[changeRequest.id]

    if not user.verified(ip, port, changeRequest.jwt):
        logActivity(f"Increase for user id: {changeRequest.id} not authorized")
        raise HTTPException(status_code=401, detail='Unauthorised')

    result = user.increase(changeRequest.amount, changeRequest.jwt)
    if not result:
        logActivity(f"Increase for user id: {changeRequest.id} not authorized")
        raise HTTPException(status_code=401, detail='Unauthorised')

    logActivity(f"User id: {user.id} counter INCREASED to: {user.counter}")
    return {'counter': user.counter}


@app.post("/decrease", status_code=200)
@limiter.limit("10/second")
async def decrease(changeRequest: ChangeRequest, request: Request):
    ip = request.client.host
    port = request.client.port

    if changeRequest.id not in users:
        logActivity(f"Decrease for user id: {changeRequest.id} not authorized")
        raise HTTPException(status_code=404, detail='Not found')

    user = users[changeRequest.id]

    if not user.verified(ip, port, changeRequest.jwt):
        logActivity(f"Decrease for user id: {changeRequest.id} not authorized")
        raise HTTPException(status_code=401, detail='Unauthorised')

    result = user.decrease(changeRequest.amount, changeRequest.jwt)
    if not result:
        logActivity(f"Decrease for user id: {changeRequest.id} not authorized")
        raise HTTPException(status_code=401, detail='Unauthorised')

    logActivity(f"User id: {user.id} counter DECREASED to: {user.counter}")
    return {'counter': user.counter}


@app.post("/logout", status_code=200)
@limiter.limit("10/second")
async def logout(id: int, jwt: str, request: Request):
    if id not in users:
        logActivity(f"User id: {id} not found to logout")
        raise HTTPException(status_code=404, detail='User not found')

    user = users[id]
    if len(user.clients) > 1:
        logActivity(f"User id: {id} failed logout. Logged in at another location")
        raise HTTPException(status_code=401, detail='Logged in, in another location')

    logActivity(f"User: {id} logged out")
    users.pop(id)


@app.post("/ip", status_code=200)
@limiter.limit("10/second")
def test_IP(request: Request):
    return {'ip address': request.client.host, 'port': request.client.port}


def hash_user(id: int, password: str) -> str:
    encoding = (str(id) + password + str(time.time())).encode()
    hashed = hashlib.sha3_512(encoding).hexdigest()
    return hashed


def hash_password(password: str):
    encoding = password.encode()
    pwd_hash = hashlib.sha3_512(encoding).hexdigest()
    return pwd_hash

def logActivity(message: str):
    logging.basicConfig(filename='history.log', encoding='utf-8', level=logging.INFO)
    logging.info(message)


if __name__ == '__main__':
    users = {}
    uvicorn.run(app, host="0.0.0.0", port=8000)
