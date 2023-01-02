import re

isuNum = 367597
print(f" ISU {isuNum}: %6 = {isuNum%6}; %4 = {isuNum%4}; %7 = {isuNum%7}")
print(f"Ищем личико: ;<P")

unitTests = [
(";<P;<P;<P;<P;<P;<P;<P;<P;<P;<P;<P", 11),
(";Как-то раз некий ;<P вместе со своей подругой ;<P решили погостить у неприятного типа - -:-}P. С собой они взяли четырёхглазого сынишку :;<P и двуротую дочку ;<PO",4),
("X<P}", 0),
(":X8O=[;<P-{O/)O(;<P", 2),
("P>;OX:<PP:;<P", 1)]
p = re.compile(r';<P')
for test in unitTests:
    count = len(re.findall(p, test[0]))
    print(f"Тест: {test[0]} Предвосхищяем: {test[1]} Вывод: {count}")

while True:
    line = input("Хотите протестировать строчечку? Вводите:")
    count = len(re.findall(p, test[0]))
    print(f"Тест: {line}  Вывод: {count}")