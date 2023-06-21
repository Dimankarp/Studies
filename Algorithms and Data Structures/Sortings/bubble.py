#Bubble Sort

#O(n^2) - Optimized
def sort(arr):
    swaped = True
    i = 0
    while swaped:
        swaped = False
        for j in range(len(arr) - 1 - i):
            if arr[j] > arr[j+1]:
                arr[j+1], arr[j] = arr[j], arr[j+1]
                swaped = True
        i+=1

#O(n^2) - Unoptimized
def sort2(arr):
    for i in range(len(arr) - 1):
        for j in range(len(arr) - 1):
            if arr[j] > arr[j+1]:
                arr[j+1], arr[j] = arr[j], arr[j+1]