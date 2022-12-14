import re

path = 'server/app/'

common_passwords = set()
with open(path + "common_passwords.txt") as file:
    data = file.readlines()
    for d in data:
        common_passwords.add(d.strip())


def valid_id(id: str) -> bool:
    if not isinstance(id, str):
        return False
    if len(id) > 256:
        return False
    regex = re.compile(r"[a-zA-Z0-9]+")
    match = regex.fullmatch(str(id))
    return bool(match)


def valid_pwd(pwd: str) -> bool:
    return pwd not in common_passwords


def valid_actions(actions) -> bool:
    threshold = 100
    for step in actions.steps:
        action = step.split(" ")
        if not (0 <= int(action[1]) <= threshold):
            return False
        return True


def valid_delay(delay) -> bool:
    delay_threshold = 120

    if not (0 <= int(delay) <= delay_threshold):
        return False
    return True
