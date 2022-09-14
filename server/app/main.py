from fastapi import FastAPI, Request
from model import User, Server, Actions
import uvicorn

app = FastAPI()


@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.post("/auth")
async def login(id: int, password: str, server: Server, actions: Actions):
    return f"client info:\n" \
           f"id: {id}\npassword {password}\n" \
           f"ip: {server.ip}\n" \
           f"port: {server.port}\n" \
           f"delay: {actions.delay}\n" \
           f"steps: {actions.steps} "


if __name__ == '__main__':
    uvicorn.run(app, host="0.0.0.0", port=8000)
