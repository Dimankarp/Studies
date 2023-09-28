
import math
from numpy import array2string
import scipy.io as scio;
import matplotlib.pyplot as plt;

import numpy as np
import pandas as pd
np.set_printoptions(threshold=np.inf, precision=2 )
STUDENT_COEFF = 4.30
HEIGHT = 700.0
DIAMETER = 46.0
G = 9806.0
DIAMETER_DIFF = 0.5
MASS_SHAIBA = 220.0
MASS_SHAIBA_DIFF = 0.5
MASS_PLATFORM = 47
MASS_PLATFORM_DIFF = 0.5

arr = np.loadtxt('measures.csv', delimiter=',', dtype=float, skiprows=1)
blocks = []
for i in range(0, 3):
    blocks.append(arr[i::3])
stacked_blocks = np.dstack(blocks)
arr_of_avrg = np.average(stacked_blocks, axis=2)
diff_from_avg = (np.sum((stacked_blocks - np.expand_dims(arr_of_avrg, axis=2))**2, axis=2)/6)**0.5
delta_interval = STUDENT_COEFF * diff_from_avg
absolute_diff = (delta_interval**2 + ((2*0.01)/3)**2)**0.5
acceleration = (2 * HEIGHT)/((arr_of_avrg)**2)
acceleration_diff = (((-4 * HEIGHT)/(arr_of_avrg**3) * absolute_diff)**2)**0.5

angular_accel = 2 * acceleration/DIAMETER
angular_accel_diff = ( (2/DIAMETER * acceleration_diff)**2 + (-2*acceleration/(DIAMETER)**2 * DIAMETER_DIFF)**2   )**0.5
load_mass = np.fromfunction(shape=(4,6), function=lambda x, y : MASS_SHAIBA*(x+1)+ MASS_PLATFORM)
load_mass_diff = np.fromfunction(shape=(4,6), function=lambda x, y : MASS_SHAIBA_DIFF*(x+1)+ MASS_PLATFORM_DIFF)
print(load_mass)
force_momentum = load_mass * DIAMETER/2 * (G-acceleration)
force_momentum_diff = (  (DIAMETER/2 * (G-acceleration) * load_mass_diff)**2 +
                           (load_mass/2 * (G-acceleration) * DIAMETER_DIFF) **2 +
                           (load_mass * DIAMETER/2 * acceleration_diff)**2)**0.5


f = open("table1.txt", 'w')
for i in range(arr.shape[0]):
    f.write( f"{i+1} & ${i//3 + 1}*m $ &"+ " & ".join( np.char.mod('%.2f', arr[i])) + "\\\\ \n")
    f.write("\hline \n")


f = open("table2.txt", 'w', encoding="utf-8")
for i in range(arr_of_avrg.shape[0]):
    f.write( f"\multirow{{2}}{{*}}{{${i + 1} m $}} & $t_{{\\text{{ср}}}}$, c &"+ " & ".join(np.char.mod('%.2f', arr_of_avrg[i])) + "\\\\ \n")
    f.write( f"&$\Delta t$, c &"+ " & ".join( np.char.mod('%.2f', absolute_diff[i])) + "\\\\ \n")
    f.write("\hline \n")

f = open("table3.txt", 'w', encoding="utf-8")
for i in range(force_momentum.shape[0]):
    f.write( f"\multirow{{6}}{{*}}{{${i + 1} m $}} & $a$, мм/$c^2$ &"+ " & ".join(np.char.mod('%.2f', acceleration[i])) + "\\\\[5pt] \n")
    f.write("\cline{2-8} \n")
    f.write( f"&$\Delta a$, мм/$c^2$&"+ " & ".join( np.char.mod('%.2f', ((acceleration_diff)[i]))) + "\\\\[5pt] \n")
    f.write("\cline{2-8} \n")
    f.write( f"& $\\varepsilon$, рад/$c^2$ &"+ " & ".join(np.char.mod('%.2f', angular_accel[i])) + "\\\\[5pt] \n")
    f.write("\cline{2-8} \n")
    f.write( f"&$\Delta \\varepsilon$, рад/$c^2$ &"+ " & ".join( np.char.mod('%.2f', angular_accel_diff[i])) + "\\\\[5pt] \n")
    f.write("\cline{2-8} \n")
    f.write( f"&$M$, Н*мм &"+ " & ".join( np.char.mod('%.2f', ((force_momentum/1000000)[i]))) + "\\\\[5pt] \n")
    f.write("\cline{2-8} \n")
    f.write( f"&$\Delta M$, Н*мм &"+ " & ".join( np.char.mod('%.2f', ((force_momentum_diff/1000000)[i]))) + "\\\\[5pt] \n")
    f.write("\cline{2-8} \n")
    f.write("\hline \n")

def leastSquare(x, y):

    x_avg = np.average(x, axis=0)
    y_avg = np.average(y, axis=0)

    b = np.sum((x-x_avg) * (y-y_avg), axis=0)/np.sum((x-x_avg)**2, axis=0)
    a = y_avg - b * x_avg

    d = y - (a+b*x)
    D = np.sum((x-x_avg)**2, axis=0)
    S_b = np.sum(d**2)/D * 1/(len(x)-2)
    S_a = (1/len(x) + x_avg**2/D) * np.sum(d**2)/(len(x)-2)
    absolute_diff = ((2*S_a**0.5)**2 + (2*S_b**0.5*x)**2)**0.5
    return b, a, absolute_diff, 2*S_b**0.5, 2*S_a**0.5

inertia = np.zeros((arr.shape[1]))
friction = np.zeros((arr.shape[1]))
inertia_diff = np.zeros((arr.shape[1]))
friction_diff = np.zeros((arr.shape[1]))
for i in range(6):
    b, a, diff, b_diff, a_diff  = leastSquare(angular_accel.flatten()[i::6], force_momentum.flatten()[i::6])
    print(b_diff, a_diff)
    inertia[i] = b
    friction[i] = a
    inertia_diff[i] = b_diff
    friction_diff[i] = a_diff

f = open("table4.txt", 'w', encoding="utf-8")
for i in range(6):
    f.write( f"{i+1} & "+ f"{inertia[i]/1000000:.2f} & {inertia_diff[i]/1000000:.2f} & {friction[i]/1000000:.2f} & {friction_diff[i]/1000000:.2f}" + "\\\\ \n")
    f.write("\hline \n")

FIRST_RISKA = 57.0
FIRST_RISKA_DIFF = 0.5
BETWEEN_RISKA = 25.0
BETWEEN_RISKA_DIFF = 0.2
LOAD_HEIGHT = 40.0
LOAD_HEIGHT_DIFF = 0.5
radius = np.zeros((arr.shape[1]))
radius_diff = np.zeros((arr.shape[1]))
for i in range(arr.shape[1]):
    radius[i] = FIRST_RISKA + i * BETWEEN_RISKA + 1/2 * LOAD_HEIGHT
print(radius)

for i in range(6):
    np.savetxt(f"momentum-accel{i+1}.csv",np.vstack([force_momentum.flatten()[i::6]/1000000, force_momentum_diff.flatten()[i::6]/1000000, angular_accel.flatten()[i::6], angular_accel_diff.flatten()[i::6]]).T, delimiter=',' )

for i in range(6):
    np.savetxt(f"inertia-radius{i+1}.csv",np.vstack([inertia.flatten()[i::6]/1000000, inertia_diff.flatten()[i::6]/1000000, (radius**2/1000).flatten()[i::6]]).T, delimiter=',' )


f = open("table5.txt", 'w', encoding="utf-8")
f.write( f"$I$, г*$\\text{{м}}^2$ &"+ " & ".join( np.char.mod('%.2f', ((inertia/1000000)))) + "\\\\ \n")
f.write("\hline \n")
f.write( f" $R$, $\\text{{мм}}$ &"+ " & ".join(np.char.mod('%d', radius)) + "\\\\ \n")
f.write("\hline \n")
f.write( f"$R^2$, $\\text{{мм}}$ &"+ " & ".join( np.char.mod('%d', radius**2)) + "\\\\ \n")
f.write("\hline \n")

f = open("graph1.txt", 'w', encoding="utf-8")
for i in range(6):
    f.write(f"""
\\addplot[only marks,pink{i+1}, mark size =2pt, error bars/.cd, y dir=both, y explicit, x dir=both, x explicit, error mark options={{
      pink{i+1},
      mark size=0.4pt,
      line width=4pt
    }}, error bar style={{fill=pink{i+1},scale=2, line width=1pt}}] table [y = y, y error = y-err, x = x, x error = x-err,  col sep=comma] {{momentum-accel{i+1}.csv}};
\n""")
    f.write(f"""
\\addplot[pink{i+1}, domain=0:10] {{ {inertia[i]/1000000}*x+{friction[i]/1000000}}};
\n""")


four_m_load = np.zeros((arr.shape[1]))
four_m_load_diff = np.zeros((arr.shape[1]))
inertia_zero = np.zeros((arr.shape[1]))
inertia_zero_diff = np.zeros((arr.shape[1]))
for i in range(6):
    b, a, diff, b_diff, a_diff  = leastSquare(radius**2, inertia)
    print(b_diff, a_diff)
    four_m_load[i] = b
    inertia_zero[i] = a
    four_m_load_diff[i] = b_diff
    inertia_zero_diff[i] = a_diff
print(four_m_load, inertia_zero, four_m_load_diff, inertia_zero_diff)
f = open("graph2.txt", 'w', encoding="utf-8")
for i in range(6):
    f.write(f"""
\\addplot[only marks,blue{i+1}, mark size =2pt, error bars/.cd, y dir=both, y explicit, x dir=both, x explicit, error mark options={{
      blue{i+1},
      mark size=0.4pt,
      line width=4pt
    }}, error bar style={{fill=blue{i+1},scale=2, line width=1pt}}] table [y = y, y error = y-err, x = x,  col sep=comma] {{inertia-radius{i+1}.csv}};
\n""")
f.write(f"""
\\addplot[pink{6}, domain=0:45] {{ {four_m_load[i]/1000}*x+{inertia_zero[i]/1000000}}};
\n""")

