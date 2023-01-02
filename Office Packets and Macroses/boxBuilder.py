import matplotlib.pyplot as plot
import csv
import pandas as pd
plotsData = dict()
ax = plot.axes()
ax.set_facecolor("#BDE0FE")

def colorBoxes(box):
    for i in range(len(box['boxes'])):
        box['boxes'][i].set_facecolor(fillColors[i%4])
        box['whiskers'][i].set_c(rimColors[i%4])
        box['medians'][i].set_c(rimColors[i%4])
        box['caps'][i].set_c(rimColors[i%4])
        box['fliers'][i].set_c(rimColors[i%4])


data = pd.read_csv('BoxDiagram.csv')
data = data.drop(["<TICKER>", "<PER>", "<TIME>", "<VOL>"], axis = 1)

boxes = []
dates =  ['07/09/2018', '09/10/2018', '07/11/2018', '07/12/2018']
for i in range(4):
    boxes.append(plot.boxplot(x = data.loc[data['DATE'] == dates[i%4]][['OPEN', 'MAX', 'MIN', 'CLOSED']],
                positions=[i*4, i*4+1, i*4+2, i*4+3],
                widths=0.6,
                patch_artist=True, 
                flierprops =  {'marker': '.', 'markersize': 2, 'linestyle' :'none'}))
    print("crested position")
fillColors = ["#1188FF","#00A7FA","#6F6FDB", "#5B5BFF"]
rimColors = ["#005EBC","#006699","#3333CC", "#000066"]

for box in boxes:
    colorBoxes(box)
plot.xticks([1.5,5.5,9.5, 13.5], ['07/09/2018', '09/10/2018', '07/11/2018', '07/12/2018'])
hO, = plot.plot([1,1],'#1188FF')
hMax, = plot.plot([1,1],"#00A7FA")
hMin, = plot.plot([1,1],"#6F6FDB")
hC, = plot.plot([1,1],"#5B5BFF")
ax.legend((hO, hMax, hMin, hC),('Открытие', "Максимум", "Минимум", "Закрытие"))
hO.set_visible(False)
hMax.set_visible(False)
hMin.set_visible(False)
hC.set_visible(False)
plot.ylim(103000, 119000)

plot.show()