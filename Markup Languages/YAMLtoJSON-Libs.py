import yaml
import json
def main():
    dataDict = {}

    f = open('itmo.yaml', encoding='utf8')
    dataDict = yaml.load(f, Loader=yaml.FullLoader)
    n = open('itmoByLib.json', 'w', encoding='utf8')
    (json.dump(dataDict, n, indent=4,  ensure_ascii=False))
