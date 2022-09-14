from pydantic import BaseModel
import logging
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
        self.timeout = time.time() + len(actions.steps) * actions.delay + 1

    def increase(self, amount: int):
        self.counter += amount
        logging.basicConfig(filename='app.log')
        logging.error(f"{self.id} - {self.counter}")

    def decrease(self, amount: int):
        self.counter -= amount
        logging.basicConfig(filename='app.log')
        logging.error(f"{self.id} - {self.counter}")
