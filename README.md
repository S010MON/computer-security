# Computer Security - Client/Server Project

A simple counter app where multiple users can login using a client and modify their counter by incrementing either up or down securely

![Screenshot from 2022-11-05 11-10-52](https://user-images.githubusercontent.com/10490509/204767089-3dc6a2f1-12c5-4daf-9f51-c9497a703b9d.png)

## API Specification
There are 4 request URLs:
 - ![/auth](https://github.com/S010MON/computer-security/blob/main/README.md#auth)
 - ![/increase](https://github.com/S010MON/computer-security/blob/main/README.md#increase)
 - ![/decrease](https://github.com/S010MON/computer-security/blob/main/README.md#decrease)
 - ![/logout](https://github.com/S010MON/computer-security/blob/main/README.md#logout)


## /auth
![](https://github.com/S010MON/computer-security/blob/main/screenshots/auth_.jpg)
Query:
```json
{
  "id": "username",
  "password": "pass",
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
![](https://github.com/S010MON/computer-security/blob/main/screenshots/increase.jpg)
Query:
```json
{
  "id": "username",
  "jwt": "1pass",
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
![](https://github.com/S010MON/computer-security/blob/main/screenshots/decrease.jpg)
Query:
```json
{
  "id": "username",
  "jwt": "1pass",
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
![](https://github.com/S010MON/computer-security/blob/main/screenshots/logout.jpg)
Query:
```json
{
  "id": "username",
  "jwt": "1pass"
}

```
Response:
```json
{
  "id": "username"
}
```
