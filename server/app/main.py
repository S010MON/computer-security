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


class User:

    def __init__(self, id: int, jwt: str, server: Server, actions: Actions):
        self.id = id
        self.jwt = jwt
        self.counter = 0
        self.server = server
        self.actions = actions
        self.actions.steps.reverse()
        self.timeout = time.time() + len(actions.steps) * actions.delay + 1

    def verified(self, ip: str, port: int, jwt: str) -> bool:
        if self.server.ip != ip:
            return False

        # if self.server.port != port:
        #     return False

        if self.jwt != jwt:
            return False

        if self.timeout <= time.time():
            return False

        return True

    def increase(self, amount: int) -> bool:
        if len(self.actions.steps) == 0:
            return False

        top_element = str(self.actions.steps.pop())
        action = top_element.split(" ")

        if action[0] != "INCREASE" or int(action[1]) != amount:
            self.actions.steps.append(top_element)
            return False

        self.counter += amount
        logging.basicConfig(filename='log')
        logging.info(f"{self.id} - increased by: {amount} to {self.counter}")
        return True

    def decrease(self, amount: int):
        if len(self.actions.steps) == 0:
            return False

        top_element = str(self.actions.steps.pop())
        action = top_element.split(" ")

        if action[0] != "DECREASE" or int(action[1]) != amount:
            self.actions.steps.append(top_element)
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
    if id not in users:
        jwt = hash_user(id, password)  # TODO add proper hashing
        user = User(id, jwt, server, actions)
        users[id] = user
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
    return str(id) + password


if __name__ == '__main__':
    uvicorn.run(app, host="0.0.0.0", port=8000)
