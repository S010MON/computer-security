# Computer Security - Client/Server Project

## API Specification
There are 4 request URLs:
 - ![/auth]()
 - ![/increase]()
 - ![/decrease]()
 - ![/logout]()


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
