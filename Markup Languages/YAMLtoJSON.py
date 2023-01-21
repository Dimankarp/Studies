from yamlParser import ParseText


level = 0
def parseValue(val):
    if type(val) in [int, float]:
        return str(val)
    if val in (True, False, None):
        if val is True:
            return "true"
        elif val is False:
            return "false"
        else:
            return "null"
    else:
        return f'\"{val}\"'

def getJSON(root):
    global level
    json = ""

    # json += '{\n'
    # level+=1

    for i in  range(len(root.keys())):
        val = root[list(root.keys())[i]]
        json += '  ' * level + "\"" + str(list(root.keys())[i]) + "\"" + ": "
        if type(val) is dict:
            json += '{\n'
            level+=1

            json+=getJSON(val)

            json += '\n' +'  '*level + '}'
            level-=1
        elif type(val) is tuple:
            json += '[\n'
            level+=1
            for j in range(len(val)):
                item = val[j]
                if type(item) is dict:
                    json+= '  '*level+'{\n'
                    level+=1
                    json+=getJSON(item)
                    json+= '\n' + '  '*level+'}'
                    level-=1
                else:
                    json += '  '*level + parseValue(item)
                if j == len(val) -1:
                    json+='\n'
                else:
                    json+=',\n'
            json += '\n' +'  '*level + ']'
            level-=1
        else:
            json+=parseValue(val)
        if i != len(root.keys())-1:
            json+=',\n'
    return json

def main():
    level = 0
    file = open('itmo.yaml', encoding='utf8')
    rootDict = ParseText(file.read())
    json = '{\n'
    level+=1
    json+= getJSON(rootDict)
    json+= '\n}'

    f = open('itmoDefault.json', 'w', encoding='utf8')
    f.write(json)
