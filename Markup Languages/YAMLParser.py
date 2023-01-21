'''
From Mitya with love
and bad readability

'''

def getLevel(str):
     return len(str) - len(str.lstrip())


def isChild(str1, str2):
    return getLevel(str1)< getLevel(str2)


def isItem(line):
    words = line.strip().split()
    return words[0] == '-'
    

def parseInt(text):
    if text.isnumeric():
        if text.count('.') != 0:
            return float(text)
        else:
            return int(text)
    return None
    
def parseSpecs(text):
    if text == 'true':
            return True
    elif text == 'false':
        return False
    return None

def parseText(text):
    if text[0] + text[-1] in ('\"\"', '\'\''):
        return text[1:-1]
    return text

def parseObj(text):
    a = parseSpecs(text)
    if a != None:
        return a
    a = parseInt(text)
    if a != None:
        return a
    return parseText(text)

def isSameLevel(line1, line2):
    return getLevel(line1.replace('-', ' ',1)) == getLevel(line2.replace('-', ' ',1))


def parse(root, level, lines):
    if len(lines) == 0:
        return

    line = lines.pop()
    words = line.strip().split()


    # - smth
    if words[0] == '-':
        # - smth:
        if words[1][-1] == ':':
            words = words[1:]
            # - smth: smth


            if len(words) != 1:
                newRoot = {(words[0][:-1]) : parseObj(' '.join(words[1:]))}
                if isSameLevel(line, lines[-1]) and lines[-1].rstrip()[0] != '-':
                    while len(lines) != 0 and isSameLevel(line, lines[-1]) and lines[-1].strip()[0] != '-':
                        parse(newRoot, getLevel(line), lines)
                    root.append(newRoot)
                else:
                    root.append(newRoot)
            # - smth: 
            elif isChild(line, lines[-1]):
                nextL = lines[-1]
                if isItem(nextL):
                    if len(lines) != 0 and isSameLevel(line, lines[-1]) and lines[-1].strip()[0] != '-':
                        tempRoot = list()
                        parse(tempRoot, getLevel(line), lines)
                        newRoot = {(words[0][:-1]) : tempRoot}
                        while len(lines) != 0 and isSameLevel(line, lines[-1]) and lines[-1].strip()[0] != '-':
                            parse(newRoot, getLevel(line), lines)
                        root.append(newRoot)
                    else:
                        newRoot = list()
                        parse(newRoot, getLevel(line), lines)
                        root.append({words[0][:-1] : tuple(newRoot)})
                else:
                    if len(lines) != 0 and isSameLevel(line, lines[-1]) and lines[-1].strip()[0] != '-':
                        tempRoot = dict()
                        parse(tempRoot, getLevel(line), lines)
                        newRoot = {(words[0][:-1]) : tempRoot}
                        while len(lines) != 0 and isSameLevel(line, lines[-1]) and lines[-1].strip()[0] != '-':
                            parse(newRoot, getLevel(line), lines)
                        root.append(newRoot)
                    else:
                        newRoot = dict()
                        parse(newRoot, getLevel(line), lines)
                        root.append({words[0][:-1] : newRoot})
        # - smth            
        else:
            root.append(parseObj(' '.join(words[1:])))
    # smth: 
    elif words[0][-1] == ':':

            if len(words) != 1:
                root[words[0][:-1]] = parseObj(' '.join(words[1:]))

            elif isChild(line, lines[-1]):
                nextL = lines[-1]
                if isItem(nextL):
                    newRoot = list()
                    parse(newRoot, getLevel(line), lines)
                    root[words[0][:-1]] = tuple(newRoot)
                else:
                    newRoot = dict()
                    parse(newRoot, getLevel(line), lines)
                    root[words[0][:-1]] = newRoot


    if len(lines) != 0 and level >= getLevel(lines[-1]):
        return
    parse(root, level, lines)    
            
    

def ParseText(text):

    # yamlLines = open("itmo.yaml", encoding="utf8").read().split('\n')
    yamlLines = text.split('\n')
    yamlLines = [i for i in yamlLines if len(i.lstrip()) != 0 and i.lstrip()[0] != '#' ]



    yamlLines = yamlLines[1:]
    yamlLines.reverse()


    fileRoot = dict()

    parse(fileRoot, 0 ,yamlLines)

    return fileRoot



