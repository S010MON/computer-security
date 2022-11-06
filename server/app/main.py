import hashlib
import hmac
import logging
import uvicorn
import time
import bcrypt
from fastapi import FastAPI, HTTPException, Request
from slowapi import Limiter, _rate_limit_exceeded_handler
from slowapi.util import get_remote_address
from slowapi.errors import RateLimitExceeded
from models import ChangeRequest, AuthRequest, User
from verify import valid_id, valid_pwd, valid_delay, valid_actions
from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_v1_5
from base64 import b64decode
from passlib.context import CryptContext

limiter = Limiter(key_func=get_remote_address)
app = FastAPI()
app.state.limiter = limiter
app.add_exception_handler(RateLimitExceeded, _rate_limit_exceeded_handler)


@app.get("/", status_code=418)
@limiter.limit("10/second")
async def root(request: Request):
    logActivity("I am a teapot")
    return {"message": "I am a teapot"}


@app.get("/public_key", status_code=200)
@limiter.limit("10/second")
async def give_public_key(request: Request):
    key = open("server/app/serverPublicKey.pem").read()
    key = key.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replace("\n", "")
    return {"public_key": key}


@app.post("/auth", status_code=201)
@limiter.limit("10/second")
async def login(authRequest: AuthRequest, request: Request):
    id = decrypt(authRequest.id)
    password = decrypt(authRequest.password)

    pwd_hash = hash_password(password, bcrypt.gensalt())

    if not valid_actions(authRequest.actions):
        logActivity(f"User id: {id} attempted actions exceeding threshold amount")
        raise HTTPException(status_code=403, detail='Change threshold breached')

    if not valid_delay(authRequest.actions.delay):
        logActivity(f"User id: {id} set delay exceeding threshold value")
        raise HTTPException(status_code=403, detail='Delay threshold breached')
    
    # If a new user
    if id not in users:
        
        if not valid_id(id):
            logActivity(f"Invalid id: {id}")
            raise HTTPException(status_code=403, detail="Invalid ID: must be combination of numbers and letters only")

        if not valid_pwd(password):
            logActivity(f"Password rejected for id: {id}. Not strong enough")
            raise HTTPException(status_code=403, detail="Password not secure enough, commonly used")
        
        jwt = hash_user(id, password)
        user = User(id, pwd_hash, jwt, authRequest.server, authRequest.actions)
        users[id] = user
        logActivity(f"New user created with id: {id}")
        return {'jwt': jwt}
    # If existing user
    else:
        user = users[id]
        if user.check_password(password, pepper):
            jwt = hash_user(id, password)
            user.new_client(jwt, authRequest.server, authRequest.actions)
            logActivity(f"User with id: {id} logged in")
            return {'jwt': jwt}

        # If user is not authorised
        user.failed_auth()
        if user.locked():
            logActivity(f"User not authorized. Account locked for {user.lock_time} minutes")
            raise HTTPException(status_code=401, detail=f"Account has been locked for {user.lock_time} mins")
        logActivity(f"User with id: {id} unauthorized")
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
async def logout(id: str, jwt: str, request: Request):
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
    hashed = pwd_context.hash(encoding)
    return hashed


def hash_password(password: str, salt):
    encoding = password.encode()
    pepperedPassword = hmac.new(pepper.encode(), encoding, hashlib.sha256).hexdigest()
    pwd_hash = bcrypt.hashpw(pepperedPassword.encode(), salt)
    return pwd_hash


def logActivity(message: str):
    logging.basicConfig(filename='history.log', encoding='utf-8', level=logging.INFO)
    logging.info(message)


# def decrypt(message):
#     return private_key.decrypt(
#         message,
#         padding.OAEP(
#             mgf=padding.MGF1(algorithm=hashes.SHA256()),
#             algorithm=hashes.SHA256(),
#             label=None
#         )
#     ).decode("utf-8")


def decrypt(encryption):
    key = open("server/app/serverPrivateKey.pem").read()
    key = key.replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "").replace("\n", "")
    key = b64decode(key)
    key = RSA.importKey(key)

    cipher = PKCS1_v1_5.new(key)

    plainText = cipher.decrypt(b64decode(encryption), "Error decrypting the input string!")
    plainText = str(plainText)
    plainText = plainText.replace('b\'', '').replace('\'','')

    return plainText



if __name__ == '__main__':
    users = {}
    pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")
    pepper = "breakitifyoucan"

    uvicorn.run(app, host="0.0.0.0", port=8000)
