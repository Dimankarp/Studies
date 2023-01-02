import re


unitTests = [
"А ты знал, что ВТ - лучшая кафедра в ИТМО?",
"ВТ когда-то была, конечно другой ИТМО",
"ИТМО для меня всегда ВТ",
"ВТ ли, КТ ли - ИТМО"]


p = re.compile(r'ВТ (\W*[-\w]+){,4} ИТМО', re.I)
notP = re.compile(r'[,.!?]|(\B-\B)')
for test in unitTests:
    print(f'Пример: {test}')
    mtch = p.search(test)
    if mtch:
        print(notP.sub('', mtch.group()))
while True:
    line = input("Хотите протестировать строчечку? Вводите:")
    mtch = p.search(line)
    if mtch:
        print(notP.sub('', mtch.group()))