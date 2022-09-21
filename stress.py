from time import sleep
import requests
import json
import time

URL = 'https://cs-server-1.herokuapp.com/auth'


for i in range(100000):
    authrequest = {
        "id": i,
        "password": str(i*i),
        "server": {
            "ip": "127.0.0.1",
            "port": 0
        },
        "actions": {
            "delay": 0,
            "steps": [
                "INCREASE 1",
                "INCREASE 1",
                "INCREASE 1",
                "INCREASE 1",
                "INCREASE 1",
                "INCREASE 1",
                "INCREASE 1",
                "INCREASE 1",
                "DECREASE 1",
                "DECREASE 1",
                "DECREASE 1",
                "DECREASE 1",
                "DECREASE 1",
                "DECREASE 1",
                "INCREASE 1",
                "INCREASE 1",
                "INCREASE 1",
                "INCREASE 1",
                "INCREASE 1",
                "INCREASE 1",
                "INCREASE 1",
                "INCREASE 1",
                "DECREASE 1",
                "DECREASE 1",
                "DECREASE 1",
                "DECREASE 1",
                "DECREASE 1",
                "DECREASE 1"
            ]
        }
    }

    r = requests.post(url = URL, json=authrequest)
    print('r',r)
    print('count:', i)
    print('r2',r.text)
    if r.status_code == '404':
        break
    time.sleep(1)