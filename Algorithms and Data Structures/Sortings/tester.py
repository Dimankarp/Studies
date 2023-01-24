import time, random

def getRandArr(length):
    return [random.randint(1, 1000) for i in range(length)]

def measure(func, arr):
    for i in range(10):
        tempArr = arr.copy()
        startT = time.time()
        func(tempArr)
        endT = time.time()
        print(endT-startT)
        #print(tempArr)

def main(len, *funcs):
    testArr = getRandArr(len)
    for func in funcs:
        print(func.__name__)
        measure(func, testArr)
