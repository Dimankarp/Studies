
import math
from numpy import array2string
import scipy.io as scio;
import matplotlib.pyplot as plt;

import numpy as np
import pandas as pd
np.set_printoptions(threshold=np.inf, precision=3 )

data = scio.wavfile.read("noise-vyazma.wav")
data = data[1].astype('int')[:5000,0]
f = open("measures.txt", "w")
f.write(array2string(data))
f.close()
#TABLE 1

cols = ["t_i", "t_i-(t)_N", "(t_i-(t)_N)^2"]

average = np.average(data, axis=0)

diffCol = (data - average)
diffSum = diffCol[:].sum()

squaredDiffCol = diffCol**2

averageQuadratic = (1/(data.size-1) * squaredDiffCol.sum())**0.5
maxVal = 1/(averageQuadratic * (2 * math.pi)**0.5)
print(f'Avg {average}, DifSumm {diffSum}, Quadro {averageQuadratic}, Pmax  {maxVal}')

table1 = pd.DataFrame(np.vstack([data, diffCol, squaredDiffCol]).T, columns=cols).round(3)
f = open("table1.txt", "w")
f.write(table1.to_string())
f.close()

#TABLE 2
denseHist, _ = np.histogram(data, bins=70, density=True)
hist, bind_edges = np.histogram(data, bins=70)

intervalsLabels = []
edgeCenters = []
bind_edges = bind_edges.round(2)
for i in range(bind_edges.size-1):
    intervalsLabels.append(f'[{bind_edges[i]};{bind_edges[i+1]}]')
    edgeCenters.append((bind_edges[i+1] - bind_edges[i])/2 + bind_edges[i])

intervalsLabels  =np.array(intervalsLabels)
edgeCenters = np.array(edgeCenters)
normalVals = np.copy(edgeCenters)

bottomTerm = 2 * averageQuadratic**2
getVals = lambda t: maxVal * math.exp(-((t-average)**2)/bottomTerm )
normalVals  = np.vectorize(getVals)(normalVals)
cols = ["Intervals", "delta N", "delta N / (N * delta t)", "t", "p"]


np.savetxt("normal.csv", np.vstack([edgeCenters, normalVals]).T, delimiter=",")
np.savetxt("hist.csv", np.vstack([edgeCenters, denseHist]).T, delimiter=",")

table2 = pd.DataFrame(np.vstack([intervalsLabels, hist, denseHist, edgeCenters, normalVals]).T, columns=cols).round(3)

f = open("table2.txt", "w")
f.write(table2.to_string(float_format=lambda x: '%0.4f' % x))
f.close()


#Printing confidence intervals
print("Confidence intervals")
for i in range(1,4):
    low, high = average - i * averageQuadratic, average + i * averageQuadratic
    print(f'{(low).round(3)} & {(high).round(3)}')
    count = np.count_nonzero( (low <= data) & (data<= high))
    print(f'Hits in interval {i}: {count} is {count*100/data.size} %')

quadraticDiffOfAverage = (1/((data.size-1)*data.size) * squaredDiffCol.sum())**0.5
print(f'Quadratic diff of average: {quadraticDiffOfAverage}')

studentsCoeff = 1.9604386466615242

confidenceInterval = studentsCoeff * quadraticDiffOfAverage

confidenceProbab =  np.count_nonzero((average -  confidenceInterval<= data) & (data<= average +  confidenceInterval))/data.size

print(f"Confidence Probability: {confidenceProbab}")

plt.hist(data, bins=70, density=True)
print("showing")
plt.show()