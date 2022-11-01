import re

common_passwords = set()
with open("common_passwords.txt") as file:
    data = file.readlines()
    for d in data:
        common_passwords.add(d.strip())


def valid_id(id: str) -> bool:
    if not isinstance(id, str):
        return False
    regex = re.compile("[a-zA-Z0-9]")
    match = regex.match(str(id))
    return bool(match)


def valid_pwd(pwd: str) -> bool:
    return pwd not in common_passwords
