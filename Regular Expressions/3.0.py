import re


unitTests = [
"students.spam@yandex.ru",
"example@example",
"example@example.com",
"mr.programmer@_server.su",
"yahoo.yahoo.yahoo",
'goo@google.go',
"v.putin@kremlin.ru",
'@@.gov',
'student@psj.net'
]

p = re.compile(r'[\w.]+@(?P<server>[a-z]+\.[a-z]+)', re.I)
for test in unitTests:
    print(f'Пример: {test}')
    mtch = p.fullmatch(test)
    if mtch:
        print("Почтовый сервер:", mtch.group('server'))
    else:
        print("Это совершенно не адрес!")
while True:
    line = input("Хотите протестировать строчечку? Вводите:")
    mtch = p.fullmatch(line)
    if mtch:
        print("Почтовый сервер:", mtch.group('server'))
    else:
        print("Это совершенно не адрес!")