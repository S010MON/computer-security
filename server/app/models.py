import hashlib
import hmac
from datetime import datetime, timedelta

import bcrypt
from passlib.context import CryptContext
from pydantic import BaseModel


class Server(BaseModel):
    ip: str
    port: int


class Actions(BaseModel):
    delay: int
    steps: list


class AuthRequest(BaseModel):
    id: str
    password: str
    server: Server
    actions: Actions


class ChangeRequest(BaseModel):
    id: str
    jwt: str
    amount: int


class Client:

    def __init__(self, jwt: str, server: Server, actions: Actions) -> None:
        self.jwt = jwt
        self.server = server
        self.actions = actions
        self.actions.steps.reverse()
        time_delta = len(actions.steps) * actions.delay + 1
        self.timeout = datetime.now() + timedelta(seconds=time_delta)


class User:

    def __init__(self, id: str, pwd_hash: str, jwt: str, server: Server, actions: Actions):
        self.id = id
        self.pwd_hash = pwd_hash
        self.pwd_attempts = 0
        self.unlock_time = datetime.now()
        self.counter = 0
        self.clients = {jwt: Client(jwt, server, actions)}
        self.lock_time = 2

    def verified(self, ip: str, port: int, jwt: str) -> bool:

        if jwt not in self.clients:
            return False

        client = self.clients[jwt]

        # if client.server.ip != ip:
        #     return False

        # if self.server.port != port:
        #     return False

        if client.jwt != jwt:
            return False

        if client.timeout <= datetime.now():
            return False

        return True

    def check_password(self, pwd, pepper):
        if self.unlock_time > datetime.now():
            return False
        pepperedPasswordAttempt = hmac.new(pepper.encode(), pwd.encode(), hashlib.sha256).hexdigest()
        return bcrypt.checkpw(pepperedPasswordAttempt.encode(), self.pwd_hash)

    def new_client(self, jwt: str, server: Server, actions: Actions):
        self.clients[jwt] = Client(jwt, server, actions)

    def increase(self, amount: int, jwt: str) -> bool:

        client = self.clients[jwt]

        if len(client.actions.steps) == 0:
            return False

        top_element = str(client.actions.steps.pop())
        action = top_element.split(" ")

        if action[0] != "INCREASE" or int(action[1]) != amount:
            client.actions.steps.append(top_element)
            return False

        self.counter += amount
        return True

    def decrease(self, amount: int, jwt: str):

        client = self.clients[jwt]

        if len(client.actions.steps) == 0:
            return False

        top_element = str(client.actions.steps.pop())
        action = top_element.split(" ")

        if action[0] != "DECREASE" or int(action[1]) != amount:
            client.actions.steps.append(top_element)
            return False

        self.counter -= amount
        return True

    def failed_auth(self):
        self.pwd_attempts += 1
        if self.pwd_attempts > 3:
            self.unlock_time = datetime.now() + timedelta(minutes=self.lock_time)
            self.lock_time *= 2

    def locked(self):
        return self.unlock_time > datetime.now()
