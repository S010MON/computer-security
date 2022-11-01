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
    return {"message": "I am a teapot"}


@app.post("/auth", status_code=201)
@limiter.limit("10/second")
async def login(authRequest: AuthRequest, request: Request):
    print(authRequest)
    pwd_hash = hash_password(authRequest.password)

    # Change limit verification
    threshold = 100
    for step in authRequest.actions.steps:
        action = step.split(" ")
        if not (0 <= int(action[1]) <= threshold):
            raise HTTPException(status_code=403, detail='Change value threshold breached')

    # If a new user
    if authRequest.id not in users:
        jwt = hash_user(authRequest.id, authRequest.password)
        user = User(authRequest.id, pwd_hash, jwt, authRequest.server, authRequest.actions)
        users[authRequest.id] = user
        return {'jwt': jwt}

    # If existing user
    else:
        user = users[authRequest.id]
        if user.check_password(pwd_hash):
            jwt = hash_user(authRequest.id, authRequest.password)
            user.new_client(jwt, authRequest.server, authRequest.actions)
            return {'jwt': jwt}

        # If user is not authorised
        user.failed_auth()
        if user.locked():
            raise HTTPException(status_code=401, detail=f"Account has been locked for {user.lock_time} mins")
        raise HTTPException(status_code=404, detail='User not found')


@app.post("/increase", status_code=200)
@limiter.limit("10/second")
async def increase(changeRequest: ChangeRequest, request: Request):
    ip = request.client.host
    port = request.client.port

    if changeRequest.id not in users:
        raise HTTPException(status_code=404, detail='Not found')

    user = users[changeRequest.id]

    if not user.verified(ip, port, changeRequest.jwt):
        raise HTTPException(status_code=401, detail='Unauthorised')

    result = user.increase(changeRequest.amount, changeRequest.jwt)
    if not result:
        raise HTTPException(status_code=401, detail='Unauthorised')

    return {'counter': user.counter}


@app.post("/decrease", status_code=200)
@limiter.limit("10/second")
async def decrease(changeRequest: ChangeRequest, request: Request):
    ip = request.client.host
    port = request.client.port

    if changeRequest.id not in users:
        raise HTTPException(status_code=404, detail='Not found')

    user = users[changeRequest.id]

    if not user.verified(ip, port, changeRequest.jwt):
        raise HTTPException(status_code=401, detail='Unauthorised')

    result = user.decrease(changeRequest.amount, changeRequest.jwt)
    if not result:
        raise HTTPException(status_code=401, detail='Unauthorised')

    return {'counter': user.counter}


@app.post("/logout", status_code=200)
@limiter.limit("10/second")
async def logout(id: int, jwt: str, request: Request):
    if id not in users:
        raise HTTPException(status_code=404, detail='User not found')

    user = users[id]
    if user.jwt != jwt:
        raise HTTPException(status_code=404, detail='User not found')

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


if __name__ == '__main__':
    users = {}
    uvicorn.run(app, host="0.0.0.0", port=8000)
