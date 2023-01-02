import re

isuNum = 367597
print(f" ISU {isuNum}: %5 = {isuNum%5};")

unitTests = [
"MMMCMXCIX",
"Августейшая особа, почему у нас осталось всего V легионов и CCCM центурий? - Сколько центурий?!",
"Жили у бабуси\
\nII весёлых гуся,\
I - серый, другой - белый,\
\nII весёлых гуся.\
I - серый, другой - белый,\
\nII весёлых гуся!",
"MCI CDL LDC XMC XIC DLM - и прочие команлы БЭВМ",
"Список покупок на завтра:\
\nX яиц\
\nII бутылки молока\
\nIII-IV луковички\
\nIII килограмма картошки\
\nшоколадки, штуки II-MC\
\nсыра много понадобится,\
\nпрямо мого - III кусочка по CC грамм"]
#p = re.compile(r'\b(\w+)(\s+(\1)\b)+')
p = re.compile(r'\bM{0,3}(CM|CD|D?C{0,3})?(XC|XL|L?X{0,3})?(IX|IV|V?I{0,3})?\b')
for test in unitTests:
    print(f'Пример: {test}')
    print('Найдены следующие римские числа:')
    for i in re.finditer(p, test):
        if i.group():
            print(i.group())
    print()
while True:
    line = input("Хотите протестировать строчечку? Вводите:")
    print('Найдены следующие римские числа:')
    for i in re.finditer(p, line):
        if i.group():
            print(i.group())
    print()