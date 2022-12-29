import math

def getDataBits(bits):
    parityBitsCount = math.ceil(math.log(len(bits)+ 1, 2))

    for i in range(parityBitsCount):
        bits.pop(2**i-1 - i)
    return bits

while True:
        bits = str(input("Введите биты сообщения! Вводите осторожно:")).strip()
        failed = False
        if len(bits) ==0:
            print("Ну введите хоть что-нибудь!")
            failed = True
            continue
        
        for i in bits:
                  if i not in {'0', '1'}:
                      print("Да вводите вы только нолики и единички!")
                      failed = True
                      break
        if failed:
            continue
        print("Хорошие циферки - начинаю работу!")

        parityBitsCount = math.ceil(math.log(len(bits)+ 1, 2))

        bits = list(map(int, bits))
        #Don't even ask where this comes from - my intuition!
        syndromes = [str(sum( [sum(bits[2**i-1 + k*2**i*2:2**i-1 + k*2**i*2 + 2**i]) for
         k in range(math.ceil(len(bits)/(2**i * 2)))] )%2) for i in range(parityBitsCount)]
        if not '1' in syndromes:
            print(f"Ошибок не обнаружено! Биты данных: {''.join(map(str, getDataBits()))}")
        else:
            errorBitIndex = int(''.join(syndromes[::-1]), 2) - 1
            #Bit fixing
            bits[errorBitIndex] = 1 - bits[errorBitIndex]
            errorBitIndex+=1
            if (errorBitIndex & errorBitIndex-1) == 0:
                print(f"Ошибка в бите чётности r{int(math.log2(errorBitIndex) + 1)}! Испрвленные биты данных: {''.join(map(str, getDataBits(bits)))}")
            else:
                print(f"Ошибка в бите данных i{errorBitIndex - ( int(math.log2(errorBitIndex)) + 1)}! Испрвленные биты данных: {''.join(map(str, getDataBits(bits)))}")