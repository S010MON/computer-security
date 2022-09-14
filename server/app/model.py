from pydantic import BaseModel


class User(BaseModel):
    id: int
    password: str


class Server(BaseModel):
    ip: str
    port: str


class Actions(BaseModel):
    delay: int
    steps: list
