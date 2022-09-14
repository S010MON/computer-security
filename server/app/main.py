from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import logging
import uvicorn
import time


class Server(BaseModel):
    ip: str
    port: str


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

    def increase(self, amount: int):
        line = str(self.actions.steps.pop())
        action = line.split(" ")
        if action[0] != "INCREASE":
            raise HTTPException(status_code=401, detail='Unauthorised')
        if int(action[1]) != amount:
            raise HTTPException(status_code=401, detail='Unauthorised')

        self.counter += amount
        logging.basicConfig(filename='app.log')
        logging.info(f"{self.id} - increased by: {amount} to {self.counter}")

    def decrease(self, amount: int):
        line = str(self.actions.steps.pop())
        action = line.split(" ")
        if action[0] != "INCREASE":
            raise HTTPException(status_code=401, detail='Unauthorised')
        if int(action[1]) != amount:
            raise HTTPException(status_code=401, detail='Unauthorised')

        self.counter -= amount
        logging.basicConfig(filename='app.log')
        logging.info(f"User{self.id} - decreased by: {amount} to {self.counter}")


app = FastAPI()
users = {}


@app.get("/", status_code=418)
async def root():
    return {"message": "I am a teapot"}


@app.post("/auth", status_code=201)
async def login(id: int, password: str, server: Server, actions: Actions):
    if id not in users:
        jwt = hash_user(id, password)       # TODO add proper hashing
        user = User(id, jwt, server, actions)
        users[id] = user
        return {'jwt': jwt}

    raise HTTPException(status_code=404, detail='User not found')


@app.post("/increase", status_code=200)
async def increase(id: int, jwt: str, amount: int):
    if id in users:
        user = users[id]
        if user.jwt == jwt:
            if time.time() > user.timeout:
                raise HTTPException(status_code=401, detail='Unauthorised')
            user.increase(amount)
            return {'counter': user.counter}
    raise HTTPException(status_code=404, detail='User not found')


@app.post("/decrease", status_code=200)
async def increase(id: int, jwt: str, amount: int):
    if id in users:
        user = users[id]
        if user.jwt == jwt:
            if time.time() > user.timeout:
                raise HTTPException(status_code=401, detail='Unauthorised')
            user.decrease(amount)
            return {'counter': user.counter}
    raise HTTPException(status_code=404, detail='User not found')


@app.post("/logout", status_code=200)
async def logout(id: int, jwt: str):
    if id not in users:
        raise HTTPException(status_code=404, detail='User not found')

    user = users[id]
    if user.jwt != jwt:
        raise HTTPException(status_code=404, detail='User not found')

    users.pop(id)


def hash_user(id: int, password: str) -> str:
    """
    This is a temporary measure for debugging - replace with secure hashing algorithm!
    """
    return str(id) + password


if __name__ == '__main__':
    uvicorn.run(app, host="0.0.0.0", port=8000)
