from fastapi import FastAPI, HTTPException, Request
from pydantic import BaseModel
import logging
import uvicorn
import time


class Server(BaseModel):
    ip: str
    port: int


class Actions(BaseModel):
    delay: int
    steps: list

class Client:

    def __init__(self, jwt: str, server: Server, actions: Actions) -> None:
        self.jwt = jwt
        self.server = server
        self.actions = actions
        self.actions.steps.reverse()
        self.timeout = time.time() + len(actions.steps) * actions.delay + 1


class User:

    def __init__(self, id: int, pwd_hash: str, jwt: str, server: Server, actions: Actions):
        self.id = id
        self.pwd_hash = pwd_hash
        self.counter = 0
        self.clients = {jwt:Client(jwt, server, actions)}


    def verified(self, ip: str, port: int, jwt: str) -> bool:
        if self.server.ip != ip:
            return False

        # if self.server.port != port:
        #     return False

        if jwt not in self.clients:
            return False
        
        client = self.clients[jwt]

        if client.jwt != jwt:
            return False

        if client.timeout <= time.time():
            return False

        return True
    
    def new_client(self, jwt: str, server: Server, actions: Actions):
        self.clients = {jwt:Client(jwt, server, actions)}
        pass

    def increase(self, amount: int, jwt: str) -> bool:
        if len(self.actions.steps) == 0:
            return False

        client = self.client[jwt]

        top_element = str(client.actions.steps.pop())
        action = top_element.split(" ")

        if action[0] != "INCREASE" or int(action[1]) != amount:
            client.actions.steps.append(top_element)
            return False

        self.counter += amount
        logging.basicConfig(filename='log')
        logging.info(f"{self.id} - increased by: {amount} to {self.counter}")
        return True

    def decrease(self, amount: int, jwt: str):
        if len(self.actions.steps) == 0:
            return False

        client = self.client[jwt]

        top_element = str(client.actions.steps.pop())
        action = top_element.split(" ")

        if action[0] != "DECREASE" or int(action[1]) != amount:
            client.actions.steps.append(top_element)
            return False

        self.counter -= amount
        logging.basicConfig(filename='log')
        logging.info(f"User{self.id} - decreased by: {amount} to {self.counter}")
        return True


app = FastAPI()
users = {}


@app.get("/", status_code=418)
async def root():
    return {"message": "I am a teapot"}


@app.post("/auth", status_code=201)
async def login(id: int, password: str, server: Server, actions: Actions):
    pwd_hash = password

    if id not in users:
        jwt = hash_user(id, password)  # TODO add proper hashing
        user = User(id, pwd_hash, jwt, server, actions)
        users[id] = user
        return {'jwt': jwt}

    elif users[id].pwd_hash == pwd_hash:
        jwt = hash_user(id, password)
        users[id].new_client(jwt, server, actions)
        return {'jwt': jwt}


    raise HTTPException(status_code=404, detail='User not found')


@app.post("/increase", status_code=200)
async def increase(id: int, amount: int, jwt: str, request: Request):
    ip = request.client.host
    port = request.client.port

    if id not in users:
        raise HTTPException(status_code=401, detail=f'Unauthorised')

    user = users[id]

    if not user.verified(ip, port, jwt):
        raise HTTPException(status_code=401, detail='Unauthorised')

    result = user.increase(amount)
    if not result:
        raise HTTPException(status_code=401, detail='Unauthorised')

    return {'counter': user.counter}


@app.post("/decrease", status_code=200)
async def decrease(id: int, amount: int, jwt: str, request: Request):
    ip = request.client.host
    port = request.client.port

    if not (id in users):
        raise HTTPException(status_code=401, detail='Unauthorised')

    user = users[id]

    if not user.verified(ip, port, jwt):
        raise HTTPException(status_code=401, detail='Unauthorised')

    result = user.decrease(amount)
    if not result:
        raise HTTPException(status_code=401, detail='Unauthorised')

    return {'counter': user.counter}


@app.post("/logout", status_code=200)
async def logout(id: int, jwt: str):
    if id not in users:
        raise HTTPException(status_code=404, detail='User not found')

    user = users[id]
    if user.jwt != jwt:
        raise HTTPException(status_code=404, detail='User not found')

    users.pop(id)


@app.post("/ip", status_code=200)
def test_IP(request: Request):
    return {'ip address': request.client.host, 'port': request.client.port, "users": users}


def hash_user(id: int, password: str) -> str:
    """
    This is a temporary measure for debugging - replace with secure hashing algorithm!
    """
    return str(id) + password + str(time.time())


if __name__ == '__main__':
    uvicorn.run(app, host="0.0.0.0", port=8000)
