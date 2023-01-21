import YAMLParser

cols = dict()
file = open('table.yaml', encoding='utf8')
root = yamlParser.ParseText(file.read())

def findList(root):
    maxL = 0
    maxTuple = None
    if type(root) is tuple:
        return root
    else:
        for i in root.values():
            temp = findList(i)
            if temp is not None:
                if len(temp) > maxL:
                    maxTuple = temp
        return maxTuple


ls = findList(root)
print(findList(root))

for n in range(len(ls)):
    item = ls[n]
    if type(item) != dict:
        continue
    for key in item.keys():
        if key in cols.keys():
            cols[key].append(item[key])
        else:
            cols[key] = [None for i in range(n)]
            cols[key].append(item[key])
    for key in cols.keys():
        if key not in item.keys():
            cols[key].append(None)


file = open('table.csv', 'w', encoding='utf8')
file.write(', '.join(list(cols.keys())))

for i in range(len(cols[list(cols.keys())[0]])):
    file.write('\n')
    for j in range(len(list(cols.keys()))):
        if j  ==  len(list(cols.keys()))-1:
            file.write(str(cols[list(cols.keys())[j]][i]))
        else:
            file.write(str(cols[list(cols.keys())[j]][i]) + ', ')


