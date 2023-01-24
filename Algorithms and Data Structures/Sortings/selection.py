#Selection Sort


#O(n^2) but statistically better - Single Swap
def sort(arr):
    minInd = 0
    minEl = 0
    for i in range(len(arr)-1):
        minInd = i
        minEl = arr[i]
        for j in range(i+1, len(arr)):
            if arr[j] < minEl:
                minInd = j
                minEl = arr[j]
        arr[i], arr[minInd] = minEl, arr[i]

#O(n^2) - Multiple Swaps
def sort2(arr):
    for i in range(len(arr)-1):
        for j in range(i+1, len(arr)):
            if arr[j] < arr[i]:
                arr[j], arr[i] = arr[i], arr[j]        


