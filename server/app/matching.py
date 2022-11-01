import re

s = "hel1"
regex = re.compile(r"[a-zA-Z0-9]+")
match = regex.fullmatch(s)
print(match)
