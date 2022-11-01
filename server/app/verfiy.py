common_passwords = set()
with open("common_passwords.txt") as file:
    data = file.readlines()
    for d in data:
        common_passwords.add(d)


def valid_id(id: str) -> bool:
    return isinstance(id, str())


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
    delayThreshold = 120
    if not(0 <= int(delay) <= delayThreshold):
        return False
    return True
