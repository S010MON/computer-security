# Computer Security - Client/Server Project

## API Specification
There are 4 request URLs:
 - ![/auth](https://github.com/S010MON/computer-security/blob/main/README.md#auth)
 - ![/increase](https://github.com/S010MON/computer-security/blob/main/README.md#increase)
 - ![/decrease](https://github.com/S010MON/computer-security/blob/main/README.md#decrease)
 - ![/logout](https://github.com/S010MON/computer-security/blob/main/README.md#logout)

A System diagram is shown below:
![](https://github.com/S010MON/computer-security/blob/main/screenshots/action_diagram.jpg)

## /auth
Query:
```json
{
  "id": 1
  "password": "pass"
  "server": {
     "ip": "127.0.0.1",
     "port": 52660
  },
  "actions": {
    "delay": 100,
    "steps": [
      "INCREASE 1", "INCREASE 1", "INCREASE 1", "DECREASE 1", "INCREASE 1"
    ]
  }
}

```
Response:
```json
{
  "jwt": "1pass"
}
```

## /increase
```json
{
  "id": 1
  "jwt": "1pass"
  "amount": 1
}

```
Response:
```json
{
  "counter": 1
}
```

## /decrease
```json
{
  "id": 1
  "jwt": "1pass"
  "amount": 1
}

```
Response:
```json
{
  "counter": 1
}
```

## /logout
```json
{
  "id": 1
  "jwt": "1pass"
}

```
Response:
```json
{
  "id": 1
}
```
