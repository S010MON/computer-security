common_passwords = set()
with open("common_passwords.txt") as file:
    data = file.readlines()
    for d in data:
        common_passwords.add(d.strip())


def valid_id(id: str) -> bool:
    return isinstance(id, str())


def valid_pwd(pwd: str) -> bool:
    print(common_passwords)
    return pwd not in common_passwords
