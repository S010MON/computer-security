common_passwords = set()
with open("common_passwords.txt") as file:
    data = file.readlines()
    for d in data:
        common_passwords.add(d)


def valid_id(id: str) -> bool:
    return isinstance(id, str())


def valid_pwd(pwd: str) -> bool:
    return pwd not in common_passwords
