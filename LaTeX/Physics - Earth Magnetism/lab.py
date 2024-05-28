
import math
from numpy import array2string, ndarray
import scipy.io as scio;
import matplotlib.pyplot as plt;

import numpy as np
import pandas as pd
np.set_printoptions(threshold=np.inf, precision=4)

HELMHOLZT_RADIUS = 0.15 # in Meters
LOOPS_COUNT = 100
MU_0 = 4 * math.pi * 1e-7 # In Henry/Meter
PHI = math.radians(160) # In Radians

STUDENT_COEFF = 2.26



#NOTE: this method approximates function y=bx+a (see the coeff. names)
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

def leastSquareOffsetFree(x, y):

    x_avg = np.average(x, axis=0)
    y_avg = np.average(y, axis=0)

    b = np.sum(x * y, axis=0)/np.sum((x)**2, axis=0)
    a = y_avg - b * x_avg

    d = y - (a+b*x)
    D = np.sum((x-x_avg)**2, axis=0)
    S_b = np.sum(d**2)/(np.sum((x)**2)) * 1/(len(x)-1)
    absolute_diff =((2*S_b**0.5*x)**2)**0.5
    return b, absolute_diff, 2*S_b**0.5

def getMeasureError(V, measure_error):
    avg = np.average(V)
    avg_quadratic = (np.sum((V - avg)**2)/(np.size(V)*(np.size(V)-1)))**0.5
    trust_interval = STUDENT_COEFF * avg_quadratic
    return (trust_interval**2 + (2/3*measure_error)**2 )**0.5



Alpha, I1, I2, I3, I4 =  np.loadtxt('power_by_angle.csv', delimiter=',', dtype=float, skiprows=1, unpack=True)

Alpha_radians = np.array([math.radians(i) for i in Alpha])
I_mean = np.mean(np.vstack([I1, I2, I3, I4]), axis=0) * 1e-3

B = MU_0 * 0.8**(3/2) * I_mean * LOOPS_COUNT/HELMHOLZT_RADIUS
Sin_alpha = np.sin(Alpha_radians)
Phi_minus_alpha = PHI - Alpha_radians
Sin_phi_minus_alpha = np.sin(Phi_minus_alpha)
Sins_division = Sin_alpha/Sin_phi_minus_alpha

print(Alpha_radians)

print(Phi_minus_alpha)
print(Sin_alpha, Sin_phi_minus_alpha)


print(B, Sins_division)

(B_horizontal, _, B_diff) = leastSquareOffsetFree(Sins_division, B)

print(B_horizontal, B_diff)

print(f"Inteval: {B_horizontal-B_diff}; {B_horizontal+B_diff}")

#Table 1
f = open("table1.txt", 'w', encoding="utf-8")
f.write("\hline \n")
for i in range(np.size(Alpha)):
    f.write( f"{Alpha[i]:.0f}$^{{\circ}}$ & {I1[i]:.2f} & {I2[i]:.2f} & {I3[i]:.2f} & {I4[i]:.2f} & {I_mean[i]*1000:.2f}& {Sins_division[i]:.2f} & {B[i]*1e6:.2f}\\\\ \n")
    f.write("\hline \n")

np.savetxt(f"b_sin_division.csv",np.vstack([Sins_division.flatten(), (B*1e6).flatten()]).T, delimiter=',', fmt='%f',
comments='', header="SINS, B")

f = open("graph1.txt", 'w', encoding="utf-8")
f.write(f"\\addplot[blue1, domain=0:2] {{{B_horizontal*1e6}*x}}; \n")



# theor_quality = 1/full_resistance * math.sqrt(INDUCTIVNESS/C1_cond)

# print(f"Exp quality {exp_quality}")
# print(f"Theor quality {theor_quality}")

# #Graph 2
# np.savetxt(f"quality.csv",np.vstack([exp_quality.flatten(), full_resistance.flatten()]).T, delimiter=',', fmt='%f',
# comments='', header="Q, R")
# np.savetxt(f"quality_theor.csv",np.vstack([theor_quality.flatten(), full_resistance.flatten()]).T, delimiter=',', fmt='%f',
# comments='', header="Q, R")

# exp_crit_res = 900 + self_contour_res
# theor_exp_crit_res = 2 * math.sqrt(mean_induct/C1_cond)
# print(f"Exp and theor R_crit: {exp_crit_res} {theor_exp_crit_res}")

# C, T, n =  np.loadtxt('period_by_cap.csv', delimiter=',', dtype=float, skiprows=1, unpack=True)
# C *= 1e-6
# T /= n
# T *= 1e-6

# t_thomps = 2*math.pi / np.sqrt(1/(mean_induct * C))
# t_theor = 2*math.pi / np.sqrt(1/(mean_induct * C) - self_contour_res**2/(4*mean_induct**2))
# delta_t_relative = (T - t_theor)/t_theor * 100

# print(f"T_exp: {T}, t_theor: {t_theor},  delta_t: {delta_t_relative}")
# print(f"T_thomps: {t_thomps}, ")
# #Table 1
# f = open("table2.txt", 'w', encoding="utf-8")
# f.write("\hline \n")
# for i in range(np.size(C)):
#     f.write( f"{C[i]*1e6:.4f} & {T[i]*1e6:.3f} & {t_theor[i]*1e6:.3f} & {delta_t_relative[i]:.3f}\\\\ \n")
#     f.write("\hline \n")

# np.savetxt(f"period_exp.csv",np.vstack([(T*1e6).flatten(), (C*1e6).flatten()]).T, delimiter=',', fmt='%f',
# comments='', header="T, C")
# np.savetxt(f"period_ther.csv",np.vstack([(t_theor*1e6).flatten(), (C*1e6).flatten()]).T, delimiter=',', fmt='%f',
# comments='', header="T, C")
# np.savetxt(f"period_thomps.csv",np.vstack([(t_thomps*1e6).flatten(), (C*1e6).flatten()]).T, delimiter=',', fmt='%f',
# comments='', header="T, C")


# period = np.asarray([264, 178, 88])
# r = 40
# n_diff = np.asarray([3, 2, 1])
# t_diff = np.asarray([100-78, 108-63.5, 24-10.5])
# arg_exp_diff = 2 *math.pi * t_diff / (period/n_diff)
# arg_theor_diff = math.atan(-2 * math.sqrt(mean_induct/C[0] - r**2/4)/r) 
# print(f"Task 10 arg, exp: {arg_exp_diff}, theor:{arg_theor_diff}")
# fig=plt.figure()
# ax=fig.add_subplot(111,projection='3d')Gjuj
# surf=ax.plot_surface(xx, yy, vals, cmap="RdBu") 
# fig.set_size_inches(10,10) 
# plt.show()
# grid_old=(x,y)

# #grid new
# # the limits of the interpolated x and y val have to be less than the original grid
# x_new=np.arange(0.1,19.9,0.1)
# y_new=np.arange(0.1,28.9,0.1)
# grid_new = np.meshgrid(x_new, y_new)

# grid_flattened = np.transpose(np.array([k.flatten() for k in grid_new]))
# #Interpolation onto a finer grid
# grid_interpol = RegularGridInterpolator(grid_old,vel,method='cubic')
# vel_interpol = grid_interpol(grid_flattened)

# #Unflatten the interpolated velocities and store into a new variable.
# index=0
# vel_new=np.zeros((len(x_new),len(y_new)))
# for i in  range(len(x_new)):
#     for j in range(len(y_new)):
#         vel_new[i,j] =vel_interpol[index]
#         index+=1



# #Semiconductor
# sem_T, sem_I, sem_U =  np.loadtxt('semiconductor.csv', delimiter=',', dtype=float, skiprows=1, unpack=True)
# sem_I = sem_I * 1e-6 #It must be microampers
# sem_R = sem_U/sem_I
# sem_R_ln = np.log(sem_R)
# sem_rev_T = 1000/sem_T

# sem_fz = []
# for i in range(np.size(sem_R)//2):
#     j = i + np.size(sem_R)//2
#     val = 2*BOLTZMAN* (sem_T[i]*sem_T[j])/(sem_T[j]-sem_T[i])* math.log(sem_R[i]/sem_R[j])
#     sem_fz.append(val)
# sem_fz_avg = np.average(sem_fz)
# sem_fz_err = getMeasureError(sem_fz, 0)
# print(f'fail zone = {sem_fz_avg}, err = {sem_fz_err}, fail_zone_EV={sem_fz_avg*DJOULE_TO_EV}, err_EV={sem_fz_err*DJOULE_TO_EV}')
# #Metal
# met_T, met_I, met_U =  np.loadtxt('metal.csv', delimiter=',', dtype=float, skiprows=1, unpack=True)
# met_I = met_I * 1e-6 #It must be in ampers
# met_T = met_T - 273 #Celsifying temperatur
# met_R = met_U/met_I
# print(met_R)
# # for i,j in zip(met_R, met_T):
# #     print(i, j)

# met_a = []
# print(met_R, met_T)
# for i in range(np.size(met_R)//2):
#     j = i + np.size(met_R)//2
#     val = (met_R[i] - met_R[j])/(met_R[j]*met_T[i] - met_R[i]*met_T[j])
#     met_a.append(val)
# met_a = np.array(met_a)
# met_a_avg = np.average(met_a)

# #NOTE!: Might be a bad way to calculate error
# met_a_err = getMeasureError(met_a, 0)
# print(f'met_a={met_a_avg}, met_err={met_a_err}')


# #Exporting plots
# np.savetxt(f"ln_res_by_rev_temp.csv",np.vstack([sem_R_ln.flatten(), (1/sem_T * 1000).flatten() ]).T, delimiter=',', fmt='%f',
#           comments='', header="RESISTANCE, TEMPERATURE,")
# np.savetxt(f"met_res_by_temp.csv",np.vstack([met_R.flatten(), (met_T).flatten() ]).T, delimiter=',', fmt='%f',
#           comments='', header="RESISTANCE, TEMPERATURE,")



# # circ_freq = np.average(np.vstack([circ_freq1, circ_freq2]), axis=0) / 60 * (2*math.pi)  #to Radians/Second

# # angle_coeffs = np.zeros((circ_freq.shape[0]//MEASURES_PER_INERTIA, 3))
# # moment_of_inertia_experimental = np.zeros((circ_freq.shape[0]//MEASURES_PER_INERTIA))
# # moment_of_inertia_experimental_diff = np.zeros((circ_freq.shape[0]//MEASURES_PER_INERTIA))
# # for i in range(0, circ_freq.shape[0]//MEASURES_PER_INERTIA):
# #    b, _, b_diff = leastSquareOffsetFree(circ_freq[i*MEASURES_PER_INERTIA:(i+1)*(MEASURES_PER_INERTIA)], period[i*MEASURES_PER_INERTIA:(i+1)*(MEASURES_PER_INERTIA)])
# #    angle_coeffs[i] = [b, b_diff, b_diff/b]
# #    #Exporting graphs data
# #    np.savetxt(f"period_by_freq{i+1}.csv",np.vstack([circ_freq[i*MEASURES_PER_INERTIA:(i+1)*(MEASURES_PER_INERTIA)].flatten(), period[i*MEASURES_PER_INERTIA:(i+1)*(MEASURES_PER_INERTIA)].flatten()]).T, delimiter=',', fmt='%f',
# #           comments='', header="FREQ, PERIOD")
# #    moment_of_inertia_experimental[i] = b * (LOAD_HOLDER_MASS + (i+1)*LOAD_MASS) * G * LEVER_LENGTH/(2*math.pi)
# #    moment_of_inertia_experimental_diff[i] = b_diff * (LOAD_HOLDER_MASS + (i+1)*LOAD_MASS) * G * LEVER_LENGTH/(2*math.pi)
# # print(angle_coeffs)

# # moment_of_inertia_theoretical = GYROSCOPE_MASS * GYROSCOPE_RADIUS**2 / 2

# # for i in range(3):
# #     print(f"{i+1} груз: A = {angle_coeffs[i][0]} +_ {angle_coeffs[i][1]} (epsilon = {angle_coeffs[i][2]}), I_exp={moment_of_inertia_experimental[i]} +- {moment_of_inertia_experimental_diff[i]} (epsilon= {moment_of_inertia_experimental_diff[i]/moment_of_inertia_experimental[i]})")
# # print(f"Average: {np.average(moment_of_inertia_experimental)} +- {np.average(moment_of_inertia_experimental_diff)}, (epsilon={np.average(moment_of_inertia_experimental_diff)/np.average(moment_of_inertia_experimental)})")
# # print(f"Theoretical: {moment_of_inertia_theoretical}")
# # print(f"Abs Difference: {moment_of_inertia_theoretical-np.average(moment_of_inertia_experimental)}")


# # Exporting to LaTex

# #Table 1
# f = open("table1.txt", 'w', encoding="utf-8")
# f.write("\hline \n")
# f.write( f"№ & $T$, К & $I$, мкА & $U$, В & $R$, Ом & $\\ln R$ & $\\frac{{10^3}}{{T}}$, $\\frac{{1}}{{K}}$ \\\\ \n")
# f.write("\hline \n")
# for i in range(np.size(sem_T)):
#     f.write( f"{i+1} & {(sem_T[i])} & {(sem_I[i]*1e6):.0f} & {(sem_U[i]):.3f} & {(sem_R[i]):.3f} & {(sem_R_ln[i]):.3f} & {(sem_rev_T[i]):.3f}\\\\ \n")
#     f.write("\hline \n")

# #Table 2
# f = open("table2.txt", 'w', encoding="utf-8")
# f.write("\hline \n")
# f.write( f"№ & $R_i$, Ом & $R_j$, Ом & $T_i$, K & $T_j$, K & $E_{{g_{{ij}}}}$, $10^{{-19}}$ Дж \\\\ \n")
# f.write("\hline \n")
# for i in range(np.size(sem_fz)):
#     f.write( f"{i+1} & {(sem_R[i]):.3f} & {(sem_R[i+np.size(sem_fz)]):.3f} & {(sem_T[i]):.0f} & {(sem_T[+np.size(sem_fz)]):.0f} & {(sem_fz[i]*10**19):.3f}\\\\ \n")
#     f.write("\hline \n")

# #Table 3
# f = open("table3.txt", 'w', encoding="utf-8")
# f.write("\hline \n")
# f.write( f"№ & $T$, К & $I$, мкА & $U$, В & $R$, кОм & $t$, $^\\circ C$\\\\ \n")
# f.write("\hline \n")
# for i in range(np.size(met_T)):
#     f.write( f"{i+1} & {(met_T[i]+273)} & {(met_I[i]*1e6):.0f} & {(met_U[i]):.3f} & {(met_R[i]*0.001):.3f} & {(met_T[i]):.0f}\\\\ \n")
#     f.write("\hline \n")
# #Table 4
# f = open("table4.txt", 'w', encoding="utf-8")
# f.write("\hline \n")
# f.write( f"№ & $R_i$, Ом & $R_j$, Ом & $t_i$, $^\circ C$ & $t_j$, $^\circ C$ & $\\alpha_{{ij}}$, $\\frac{{10^-3}}{{K}}$ \\\\ \n")
# f.write("\hline \n")
# for i in range(np.size(met_a)):
#     f.write( f"{i+1} & {met_R[i]:.3f} & {met_R[i+np.size(met_a)]:.3f} & {met_T[i]:.0f} & {met_T[i+np.size(met_a)]:.0f} & {met_a[i]*1000:.3f}\\\\ \n")
#     f.write("\hline \n")


# # # Export graph 1
# # f = open("graph.txt", 'w', encoding="utf-8")
# # for i in range(3):
# #     f.write((f"\\addplot[only marks, blue{i+1}, mark size =2pt, mark=square*, error bars/.cd, y dir=both, y explicit, x dir=both, x explicit, error mark options={{ blue{i+1},"
# #       f"mark size=0.4pt,"
# #      f" line width=4pt"
# #   f" }}, error bar style={{fill=blue{i+1},scale=2, line width=1pt}}] table [y = PERIOD, x = FREQ,  col sep=comma] {{../period_by_freq{i+1}.csv}}; \n"))
# #     f.write(f"\\addplot[blue{i+1}, domain=0:65] {{{angle_coeffs[i][0]}*x}};\n")




# # f = open("graph1.txt", 'w', encoding="utf-8")
# # f.write(f'\\addplot[blue1, domain=0:3] {{{str1_ang_coeff}*x}}; \n')
# # f.write(f'\\addplot[blue3, domain=0:3] {{{str2_ang_coeff}*x}}; \n')

# # #Real density

# # str1_len, str1_weight = np.loadtxt('str1_params.csv', delimiter=',', dtype=float, skiprows=1)
# # str1_weight /= 1000 #Grams to KGs
# # str1_density, str1_density_diff = get_str_density(str1_len, str1_weight)

# # str2_len, str2_weight = np.loadtxt('str2_params.csv', delimiter=',', dtype=float, skiprows=1)
# # str2_weight /= 1000 #Grams to KGs
# # str2_density, str2_density_diff = get_str_density(str2_len, str2_weight)


# # #Experimental density - Method 1

# # str1_load_to_freq = np.loadtxt('load_to_freq_str1.csv', delimiter=',', dtype=float, skiprows=1)
# # str1_freq_sqr, str1_tensions, str1_freq_diff = handle_load_to_freq(str1_load_to_freq)
# # str1_ang_coeff, _, str1_ang_coeff_diff = leastSquareOffsetFree(str1_tensions, str1_freq_sqr)

# # str1_exp_density = 4/(str1_ang_coeff * STRING_LENGTH**2)
# # str1_exp_density_diff = str1_exp_density*((str1_ang_coeff_diff/str1_ang_coeff)**2 + (2 * LENGTH_INSTRUMENTAL_DIFF/STRING_LENGTH)**2)**0.5

# # str2_load_to_freq = np.loadtxt('load_to_freq_str2.csv', delimiter=',', dtype=float, skiprows=1)
# # str2_freq_sqr, str2_tensions, str2_freq_diff = handle_load_to_freq(str2_load_to_freq)
# # str2_ang_coeff, _, str2_ang_coeff_diff = leastSquareOffsetFree(str2_tensions, str2_freq_sqr)

# # str2_exp_density = 4/(str2_ang_coeff * STRING_LENGTH**2)
# # str2_exp_density_diff = str2_exp_density*((str2_ang_coeff_diff/str2_ang_coeff)**2 + (2 * LENGTH_INSTRUMENTAL_DIFF/STRING_LENGTH)**2)**0.5

# # print(f"Angle coeff: 1-я: {str1_ang_coeff} +- {str1_ang_coeff_diff} | 2-я: {str2_ang_coeff} +- {str2_ang_coeff_diff}")
# # print(f"First Method: 1-я: {str1_exp_density} +- {str1_exp_density_diff} | 2-я: {str2_exp_density} +- {str2_exp_density_diff}")

# # #Experimental density - Method 2

# # str1_freq_by_harm= np.loadtxt('freq_by_harm_str1.csv', delimiter=',', dtype=float, skiprows=1)
# # str1_tensions_by_harm, str1_harmonics = handle_freq_by_harm(str1_freq_by_harm)

# # str1_harm_angle_coeff, str1_harm_angle_coeff_diff = get_harm_angle_coeff(str1_freq_by_harm, str1_harmonics)
# # str1_speeds = str1_harm_angle_coeff * 2 * STRING_LENGTH
# # str1_speeds_diff = (str1_harm_angle_coeff_diff**2 - (str1_speeds/(2*STRING_LENGTH**2) * LENGTH_INSTRUMENTAL_DIFF)**2   )**0.5 * 2 * STRING_LENGTH
# # str1_speeds_sq = str1_speeds**2
# # str1_speeds_sq_diff = 2 * str1_speeds * str1_speeds_diff
# # str1_inverted_dens, _,  str1_inverted_dens_diff= leastSquareOffsetFree(str1_tensions_by_harm, str1_speeds_sq)


# # str2_freq_by_harm= np.loadtxt('freq_by_harm_str2.csv', delimiter=',', dtype=float, skiprows=1)
# # str2_tensions_by_harm, str2_harmonics = handle_freq_by_harm(str2_freq_by_harm)

# # str2_harm_angle_coeff, str2_harm_angle_coeff_diff = get_harm_angle_coeff(str2_freq_by_harm, str2_harmonics)
# # str2_speeds = str2_harm_angle_coeff * 2 * STRING_LENGTH
# # str2_speeds_diff = (str2_harm_angle_coeff_diff**2 - (str2_speeds/(2*STRING_LENGTH**2) * LENGTH_INSTRUMENTAL_DIFF)**2   )**0.5 * 2 * STRING_LENGTH
# # str2_speeds_sq = str2_speeds**2
# # str2_speeds_sq_diff = 2 * str2_speeds * str2_speeds_diff
# # str2_inverted_dens, _,  str2_inverted_dens_diff= leastSquareOffsetFree(str2_tensions_by_harm, str2_speeds_sq)

# # print(f"Second Method: 1-я: {1/str1_inverted_dens} +- {(1/str1_inverted_dens)**2  *str1_inverted_dens_diff} | 2-я: {1/str2_inverted_dens} +- {(1/str2_inverted_dens)**2  *str2_inverted_dens_diff}")

# # print(f"Str1: real: {str1_density}, exp1: {str1_exp_density}, exp2: {1/str1_inverted_dens}")
# # print(f"Str2: real: {str2_density}, exp1: {str2_exp_density}, exp2: {1/str2_inverted_dens}")


# # #Exporting to LaTex

# # #Table 1
# # f = open("table1.txt", 'w', encoding="utf-8")
# # f.write("\hline \n")
# # f.write( f"Струна & $l$, см & $m$, г & $\\rho_l$, г/см & $\Delta\\rho_l$, г/см \\\\ \n")
# # f.write("\hline \n")
# # f.write( f"Эластичная & {str1_len*100:.2f} & {str1_weight*1000:.2f} & {str1_density*10:.4f} & {str1_density_diff*10:.4f}"+ "\\\\ \n")
# # f.write("\hline \n")
# # f.write( f"Неэластичная & {str2_len*100:.2f} & {str2_weight*1000:.2f} & {str2_density*10:.4f} & {str2_density_diff*10:.4f}"+ "\\\\ \n")
# # f.write("\hline \n")

# # #Table 2
# # f = open("table2.txt", 'w', encoding="utf-8")
# # f.write("\hline \n")
# # f.write( f"\multicolumn{{4}}{{|c|}}{{Эластичная струна}} & \multicolumn{{4}}{{|c|}}{{Неэластичная струна}}\\\\ \n")
# # f.write("\hline \n")
# # f.write( f"$m$, г & $f$, Гц & $f^2$, $\\text{{Гц}}^2$ & $T$, Н & $m$, г & $f$, Гц & $f^2$, $\\text{{Гц}}^2$ & $T$, Н \\\\ \n")
# # f.write("\hline \n")
# # for i in range(str1_load_to_freq.shape[0]):
# #     currLoad = i * LOAD_MASS + LOAD_MASS + LOAD_HOLDER_MASS
# #     f.write( f"{currLoad*1000:.2f} & {str1_load_to_freq[i]:.2f} & {str1_freq_sqr[i]:.2f} & {str1_tensions[i]:.2f} & {currLoad*1000:.2f} & {str2_load_to_freq[i]:.2f} & {str2_freq_sqr[i]:.2f} & {str2_tensions[i]:.2f}" + "\\\\ \n")
# #     f.write("\hline \n")
# # f.write( f"\multicolumn{{4}}{{|c|}}{{$\\rho_l \pm \Delta \\rho_l = {str1_exp_density*10:.4f} \pm {str1_density_diff*10:.4f} $, г/см}} & \multicolumn{{4}}{{|c|}}{{$\\rho_l \pm \Delta \\rho_l = {str2_exp_density*10:.4f} \pm {str2_density_diff*10:.4f} $, г/см }} \\\\ \n")
# # f.write("\hline \n")

# # # Export graph 1
# # np.savetxt(f"freqsq_by_tension.csv",np.vstack([str1_freq_sqr.flatten(), str1_freq_diff.flatten(), str1_tensions.flatten(), str2_freq_sqr.flatten(), str2_freq_diff.flatten(), str2_tensions.flatten() ]).T, delimiter=',', fmt='%f',
# #           comments='', header="STR1_FREQSQ, STR1_FREQSQ_DIFF, STR1_TENS, STR2_FREQSQ, STR2_FREQSQ_DIFF, STR2_TENS,")

# # f = open("graph1.txt", 'w', encoding="utf-8")
# # f.write(f'\\addplot[blue1, domain=0:3] {{{str1_ang_coeff}*x}}; \n')
# # f.write(f'\\addplot[blue3, domain=0:3] {{{str2_ang_coeff}*x}}; \n')


# # #Table 3
# # f = open("table3.txt", 'w', encoding="utf-8")
# # f.write("\hline \n")
# # f.write( f"\multicolumn{{7}}{{|c|}}{{Эластичная струна}}\\\\ \n")
# # f.write("\hline \n")
# # f.write( f"n & $f_1$, Гц & $f_2$, Гц & $f_3$, Гц & $f_4$, Гц & $f_5$, Гц & $f_6$, Гц\\\\ \n")
# # f.write("\hline \n")
# # for i in range(str1_freq_by_harm.shape[0]):
# #     f.write( f"{i+1} & " + " & ".join(np.char.mod('%.2f', (str1_freq_by_harm[i, :]))) + "\\\\ \n")
# #     f.write("\hline \n")

# # masses_print = [f"$m_{i+1} = {(i * HARMONICS_LOAD_MASS + HARMONICS_STANDART_LOAD_MASS + LOAD_HOLDER_MASS)*1000:.2f}$" for i in range(str1_freq_by_harm.shape[1])]
# # tensions_print = [f"$T_{i+1} = {(i * HARMONICS_LOAD_MASS + HARMONICS_STANDART_LOAD_MASS + LOAD_HOLDER_MASS)*G:.2f}$" for i in range(str1_freq_by_harm.shape[1])]
# # speeds_print = [f"$u_{i+1} = {(str1_speeds[i]):.2f}$" for i in range(str1_freq_by_harm.shape[1])]
# # f.write( "г. & " + " & ".join(masses_print) + "\\\\ \n")
# # f.write("\hline \n")
# # f.write( "Н & " + " & ".join(tensions_print) + "\\\\ \n")
# # f.write("\hline \n")
# # f.write( "м/c & " + " & ".join(speeds_print) + "\\\\ \n")
# # f.write("\hline \n")


# # #Table 4
# # f = open("table4.txt", 'w', encoding="utf-8")
# # f.write("\hline \n")
# # f.write( f"\multicolumn{{7}}{{|c|}}{{Неэластичная струна}}\\\\ \n")
# # f.write("\hline \n")
# # f.write( f"n & $f_1$, Гц & $f_2$, Гц & $f_3$, Гц & $f_4$, Гц & $f_5$, Гц & $f_6$, Гц\\\\ \n")
# # f.write("\hline \n")
# # for i in range(str2_freq_by_harm.shape[0]):
# #     f.write( f"{i+1} & " + " & ".join(np.char.mod('%.2f', (str2_freq_by_harm[i, :]))) + "\\\\ \n")
# #     f.write("\hline \n")

# # masses_print = [f"$m_{i+1} = {(i * HARMONICS_LOAD_MASS + HARMONICS_STANDART_LOAD_MASS + LOAD_HOLDER_MASS)*1000:.2f}$" for i in range(str2_freq_by_harm.shape[1])]
# # tensions_print = [f"$T_{i+1} = {(i * HARMONICS_LOAD_MASS + HARMONICS_STANDART_LOAD_MASS + LOAD_HOLDER_MASS)*G:.2f}$" for i in range(str2_freq_by_harm.shape[1])]
# # speeds_print = [f"$u_{i+1} = {(str2_speeds[i]):.2f}$" for i in range(str2_freq_by_harm.shape[1])]
# # f.write( "г. & " + " & ".join(masses_print) + "\\\\ \n")
# # f.write("\hline \n")
# # f.write( "Н & " + " & ".join(tensions_print) + "\\\\ \n")
# # f.write("\hline \n")
# # f.write( "м/c & " + " & ".join(speeds_print) + "\\\\ \n")
# # f.write("\hline \n")


# # # Export graph 2
# # np.savetxt(f"freq_by_harm.csv",np.concatenate([str1_freq_by_harm, str2_freq_by_harm, np.arange(1,6).reshape(5, 1)], axis=1), delimiter=',', fmt='%f',
# #           comments='', header="STR1_T1, STR1_T2, STR1_T3, STR1_T4, STR1_T5, STR1_T6, STR2_T1, STR2_T2, STR2_T3, STR2_T4, STR2_T5, STR2_T6, N")

# # f = open("graph2.txt", 'w', encoding="utf-8")

# # for i in range(1, 7):
# #     f.write((f"\\addplot[only marks, blue{i}, mark size =2pt, mark=square*, error bars/.cd, y dir=both, y explicit, x dir=both, x explicit, error mark options={{ blue{i},"
# #       f"mark size=0.4pt,"
# #      f" line width=4pt"
# #   f" }}, error bar style={{fill=blue{i},scale=2, line width=1pt}}] table [y = STR1_T{i}, x = N,  col sep=comma] {{../freq_by_harm.csv}}; \n"))
# #     f.write(f"\\addplot[blue{i}, domain=1:6] {{{str1_harm_angle_coeff[i-1]}*x}};\n")

# # for i in range(1, 7):
# #     f.write((f"\\addplot[only marks, pink{i}, mark size =2pt, mark=square*, error bars/.cd, y dir=both, y explicit, x dir=both, x explicit, error mark options={{ pink{i},"
# #       f"mark size=0.4pt,"
# #      f" line width=4pt"
# #   f" }}, error bar style={{fill=pink{i},scale=2, line width=1pt}}] table [y = STR2_T{i}, x = N,  col sep=comma] {{../freq_by_harm.csv}}; \n"))
# #     f.write(f"\\addplot[pink{i}, domain=1:6] {{{str2_harm_angle_coeff[i-1]}*x}};\n")
# # legend_entries_str1 = ', ,'.join([f"$T_{{\\text{{эласт}} {i}}}$" for i in range(1,7)])
# # legend_entries_str2 = ', ,'.join([f"$T_{{\\text{{неэласт}} {i}}}$" for i in range(1,7)])
# # f.write(f"\\legend{{,{legend_entries_str1}, ,{legend_entries_str2}}}")

# # # Export graph 3
# # np.savetxt(f"speed_by_tension.csv",np.vstack([str1_speeds_sq.flatten(), str1_speeds_sq_diff.flatten(), str2_speeds_sq.flatten(), str2_speeds_sq_diff.flatten(), str1_tensions_by_harm.flatten()]).T, delimiter=',', fmt='%f',
# #           comments='', header="STR1_SPEED, STR1_SPEED_DIFF, STR2_SPEED, STR2_SPEED_DIFF, TENSION")

# # f = open("graph3.txt", 'w', encoding="utf-8")
# # f.write(f"\\addplot[blue1, domain=0:3] {{{str1_inverted_dens}*x}}; \n")
# # f.write(f"\\addplot[blue3, domain=0:3] {{{str2_inverted_dens}*x}}; \n")


# # \addplot[only marks, blue1, mark size =2pt, mark=square*, error bars/.cd, y dir=both, y explicit, x dir=both, x explicit, error mark options={
# #       blue1,
# #       mark size=0.4pt,
# #       line width=4pt
# #     }, error bar style={fill=blue1,scale=2, line width=1pt}] table [y = STR1_FREQSQ, y error = STR1_FREQSQ_DIFF, x = STR1_TENS,  col sep=comma] {../freqsq_by_tension.csv};

# # \addplot[only marks, blue3, mark size =2pt, mark=square*, error bars/.cd, y dir=both, y explicit, x dir=both, x explicit, error mark options={
# #       blue3,
# #       mark size=0.4pt,
# #       line width=4pt
# #     }, error bar style={fill=blue3,scale=2, line width=1pt}] table [y = STR2_FREQSQ, y error = STR2_FREQSQ_DIFF, x = STR2_TENS,  col sep=comma] {../freqsq_by_tension.csv};

# # \addplot[blue1, domain=0:3] {1004.1399453733086*x}; 
# # \addplot[blue3, domain=0:3] {2567.9533585591466*x};


# # f.write(f'\\addplot[blue1, domain=0:3] {{{str1_ang_coeff}*x}}; \n')
# # f.write(f'\\addplot[blue3, domain=0:3] {{{str2_ang_coeff}*x}}; \n')



# # ten_periods_arr = np.loadtxt('ten_periods.csv', delimiter=',', dtype=float, skiprows=1)
# # avg_ten_periods = np.average(ten_periods_arr, axis=0)
# # avg_period = avg_ten_periods/10
# # print("Avg_ten_periods:",avg_ten_periods, "Avg-Period:", avg_period)

# # ampl_dev_arr = np.loadtxt('ampl_dev_time.csv', delimiter=',', dtype=float, skiprows=1)
# # ampl_dev_avg = np.average(ampl_dev_arr, axis=0)
# # ampl_quadratic_avg = (np.sum((ampl_dev_arr - np.expand_dims(ampl_dev_avg, axis=0))**2, axis=0)/6)**0.5
# # ampl_diff = ((ampl_quadratic_avg*STUDENT_COEFF)**2 + (2/3 * TIME_INSTRUMENTAL_DIFF)**2)**0.5
# # print("Relative Diff of Ampl_Deviation:", ampl_diff/ampl_dev_avg * 100)
# # print(ampl_dev_avg)
# # dev_arr = [i*5 for i in range(5,0, -1)]
# # dev_arr = np.asarray(dev_arr)
# # np.savetxt(f"ampl_dev_graph.csv",np.vstack([ampl_dev_avg.flatten(), ampl_diff.flatten(), dev_arr.flatten(), np.full(ampl_dev_avg.shape, ANGLE_INSTRUMENTAL_DIFF).flatten()]).T, delimiter=',', fmt='%f',
# #            comments='', header="TIME, TIME_DIFF, DEV, DEV_DIFF")

# # log_dev_rel = np.log(dev_arr/INITIAL_DEVIATION)
# # print("Log_dev_rel:",log_dev_rel)
# # fading_coeff, abs_diff, fading_coeff_diff = leastSquareOffsetFree(ampl_dev_avg, log_dev_rel)
# # print("Fading coeff:", fading_coeff, fading_coeff_diff)
# # fading_time = 1/fading_coeff
# # print("Fading time:",-fading_time)  

# # ang_coeff, ang_offset, _, _, _ = leastSquare(ampl_dev_avg, dev_arr)
# # print("Ang_coeff:", ang_coeff, ang_offset)

# # stop_zone = ang_coeff*(10*avg_ten_periods)/(-4*10)
# # print("Stop zone:", stop_zone, "Stop oscillations:", INITIAL_DEVIATION/(4*stop_zone))

# # r_upper = np.asarray([FIRST_RISKA + LOAD_HEIGHT/2 for i in range(6)])
# # r_upper_diff = (FIRST_RISKA_DIFF**2 + (LOAD_HEIGHT_DIFF/2)**2)**0.5
# # r_lower = np.asarray([FIRST_RISKA + (6-1)*BETWEEN_RISKA + LOAD_HEIGHT/2 for i in range(6)])
# # r_lower_diff = (FIRST_RISKA_DIFF**2 + (5*BETWEEN_RISKA_DIFF)**2 + (LOAD_HEIGHT_DIFF/2)**2)**0.5
# # r_side = np.asarray([FIRST_RISKA + (i-1)*BETWEEN_RISKA + LOAD_HEIGHT/2 for i in range(1, 7)])
# # r_side_diff = np.asarray([(FIRST_RISKA_DIFF**2 + ((i-1)*BETWEEN_RISKA_DIFF)**2 + (LOAD_HEIGHT_DIFF/2)**2)**0.5 for i in range(1, 7)])

# # print(np.column_stack([r_side.flatten(), r_upper.flatten(), r_lower.flatten()]))
# # loads_moment_of_inertia = np.fromfunction(lambda i, j : LOAD_MASS*(r_upper[i]**2 + 2*r_side[i]**2 + r_lower[i]**2), (6, 1), dtype=int)
# # loads_moment_of_inertia_diff = np.fromfunction(lambda i, j : ( (LOAD_MASS_DIFF*(r_upper[i]**2 + 2*r_side[i]**2 + r_lower[i]**2))**2 +
# #                                                                 (2 * LOAD_MASS * r_upper[i] * r_upper_diff)**2 +
# #                                                                  (2 * LOAD_MASS * r_lower[i] * r_lower_diff)**2 +
# #                                                                 (4 * LOAD_MASS * r_side[i] * r_side_diff[i])**2)**0.5, (6, 1), dtype=int)
# # full_moment_of_inertia = DEFAULT_INERTIA + loads_moment_of_inertia



# # ten_periods_arr = np.loadtxt('load_pos_time.csv', delimiter=',', dtype=float, skiprows=1)
# # ten_periods_arr_avg = np.average(ten_periods_arr, axis=0)
# # ten_periods_quadratic_avg = (np.sum((ten_periods_arr - np.expand_dims(ten_periods_arr_avg, axis=0))**2, axis=0)/6)**0.5
# # ten_periods_diff = ((ten_periods_quadratic_avg*STUDENT_COEFF)**2 + (2/3 * TIME_INSTRUMENTAL_DIFF)**2)**0.5
# # period_arr = ten_periods_arr_avg/10
# # period_squared = period_arr**2

# # period_squared_diff = np.fromfunction(lambda i, j: (2*ten_periods_arr_avg[i])/10780 * ten_periods_diff[i], (6, 1), dtype=int) 
# # print("period squared:", period_squared.flatten(), "inertia", full_moment_of_inertia.flatten())
# # print("Period:", period_squared**0.5, "Period diff:", period_squared_diff**0.5)

# # period_coeff, _, _, period_coeff_diff, _ = leastSquare(full_moment_of_inertia.flatten(), period_squared.flatten())

# # print("Period coeff and diff:", period_coeff, period_coeff_diff)

# # ml = (4*math.pi**2/(G*period_coeff))
# # print("ml:", ml)
# # print("l:",ml/4*LOAD_MASS)
# # print(r_lower[0], r_upper[0])
# # l_theoretical = (ml)/(4*LOAD_MASS)#(r_lower[0]-r_upper[0])/4
# # print("l_theoretical:", l_theoretical)
# # l_normalize_exp = period_squared*G/(4*math.pi**2)
# # print("l_normalize_exp:", l_normalize_exp)
# # l_normalize_theor =  full_moment_of_inertia/(4*LOAD_MASS*l_theoretical)
# # print("l_normalize_theor:",l_normalize_theor)

# # #Latex tabeling and graphing

# # f = open("table2.txt", 'w', encoding="utf-8")
# # f.write("\hline \n")
# # f.write( f"\diagbox{{Время}}{{Амплитуда откл.}} & $25^\circ$ &  $20^\circ$ & $15^\circ$ & $10^\circ$ & $5^\circ$\\\\ \n")
# # f.write("\hline \n")
# # for i in range(ampl_dev_arr.shape[0]):
# #     f.write( f"$t_{i+1}$, с & "+ " & ".join(np.char.mod('%.2f', ampl_dev_arr[i])) + "\\\\ \n")
# #     f.write("\hline \n")
# # f.write( f"$t_{{\\text{{ср}}}}$, с &"+ " & ".join(np.char.mod('%.2f', ampl_dev_avg)) + "\\\\ \n")
# # f.write("\hline \n")
# # f.write( f"$\Delta {{t}}$, с &"+ " & ".join(np.char.mod('%.2f', ampl_diff)) + "\\\\ \n")
# # f.write("\hline \n")



# # f = open("table3.txt", 'w', encoding="utf-8")
# # f.write("\hline \n")
# # f.write( f"Число рисок & $t_1$, с & $t_2$, с  & $t_3$, с & $t_{{\\text{{ср}}}}$, c  & $T$, с  & $\Delta T$, с\\\\ \n")
# # f.write("\hline \n")
# # for i in range(ten_periods_arr.shape[1]):
# #     f.write( f"${i+1}$ & "+ " & ".join(np.char.mod('%.2f', ten_periods_arr.T[i])) + f" & {ten_periods_arr_avg[i]:.4f} " + f" & {period_arr[i]:.4f} " + f" & {ten_periods_diff[i]/10:.4f} " + "\\\\ \n")
# #     f.write("\hline \n")

# # log_dev_diff = INITIAL_DEVIATION/dev_arr * ANGLE_INSTRUMENTAL_DIFF
# # np.savetxt(f"angle_coeff_graph.csv",np.vstack([log_dev_rel.flatten(), ampl_dev_avg.flatten(), log_dev_diff.flatten(), ampl_diff.flatten()]).T, delimiter=',', fmt='%f',
# #            comments='', header="LOG, TIME, LOG_DIFF, TIME_DIFF")

# # f = open("graph2.txt", 'w', encoding="utf-8")
# # f.write( f"\\addplot[blue2, domain=0:330] {{{fading_coeff}*x}};")


# # f = open("table4.txt", 'w', encoding="utf-8")
# # f.write("\hline \n")
# # f.write( f"Риски & 1 &  2 & 3 & 4 & 5 & 6\\\\ \n")
# # f.write("\hline \n")
# # f.write( f"$R_\\text{{верх}}$, мм & \multicolumn{{6}}{{c|}}{{{r_upper[0]*1000:.2f}}}\\\\ \n")
# # f.write("\hline \n")
# # f.write( f"$\Delta R_\\text{{верх}}$, мм & \multicolumn{{6}}{{c|}}{{{r_upper_diff*1000:.2f}}}\\\\ \n")
# # f.write("\hline \n")
# # f.write( f"$R_\\text{{ниж}}$, мм & \multicolumn{{6}}{{c|}}{{{r_lower[0]*1000:.2f}}}\\\\ \n")
# # f.write("\hline \n")
# # f.write( f"$\Delta R_\\text{{ниж}}$, мм & \multicolumn{{6}}{{c|}}{{{r_lower_diff*1000:.2f}}}\\\\ \n")
# # f.write("\hline \n")
# # f.write( f"$R_\\text{{бок}}$, мм & "+ " & ".join(np.char.mod('%.2f', r_side*1000)) + "\\\\ \n")
# # f.write("\hline \n")
# # f.write( f"$\Delta R_\\text{{бок}}$, мм & "+ " & ".join(np.char.mod('%.2f', r_side_diff*1000)) + "\\\\ \n")
# # f.write("\hline \n")
# # f.write( f"$I_\\text{{гр}}$, $\\text{{кг}}*\\text{{м}}^2$ & "+ " & ".join(np.char.mod('%.4f', loads_moment_of_inertia.flatten())) + "\\\\ \n")
# # f.write("\hline \n")
# # f.write( f"$I$, $\\text{{кг}}*\\text{{м}}^2$ & "+ " & ".join(np.char.mod('%.4f', full_moment_of_inertia.flatten())) + "\\\\ \n")
# # f.write("\hline \n")
# # f.write( f"$\Delta I$, $\\text{{кг}}*\\text{{м}}^2$ & "+ " & ".join(np.char.mod('%.4f', loads_moment_of_inertia_diff.flatten())) + "\\\\ \n")
# # f.write("\hline \n")
# # f.write( f"$l_\\text{{пр эксп}}$, мм & "+ " & ".join(np.char.mod('%.2f', l_normalize_exp.flatten()*1000)) + "\\\\ \n")
# # f.write("\hline \n")
# # f.write( f"$l_\\text{{пр теор}}$, мм & "+ " & ".join(np.char.mod('%.2f', l_normalize_theor.flatten()*1000)) + "\\\\ \n")
# # f.write("\hline \n")


# # np.savetxt(f"period_sqr_graph.csv",np.vstack([period_squared.flatten(), period_squared_diff.flatten(), full_moment_of_inertia.flatten()*10**3, loads_moment_of_inertia_diff.flatten()*10**3]).T, delimiter=',', fmt='%f',
# #            comments='', header="PERIOD, PERIOD_DIFF, INERTIA, INERTIA_DIFF")
# # f = open("graph3.txt", 'w', encoding="utf-8")
# # f.write( f"\\addplot[blue1, domain=30:70] {{{period_coeff*10**-3}*x}};")



# # #  blocks = []
# # # for i in range(0, 3):
# # #     blocks.append(arr[i::3])
# # # stacked_blocks = np.dstack(blocks)
# # # arr_of_avrg = np.average(stacked_blocks, axis=2)
# # # diff_from_avg = (np.sum((stacked_blocks - np.expand_dims(arr_of_avrg, axis=2))**2, axis=2)/6)**0.5
# # # delta_interval = STUDENT_COEFF * diff_from_avg
# # # absolute_diff = (delta_interval**2 + ((2*0.01)/3)**2)**0.5
# # # acceleration = (2 * HEIGHT)/((arr_of_avrg)**2)
# # # acceleration_diff = (((-4 * HEIGHT)/(arr_of_avrg**3) * absolute_diff)**2)**0.5

# # # angular_accel = 2 * acceleration/DIAMETER
# # # angular_accel_diff = ( (2/DIAMETER * acceleration_diff)**2 + (-2*acceleration/(DIAMETER)**2 * DIAMETER_DIFF)**2   )**0.5
# # # load_mass = np.fromfunction(shape=(4,6), function=lambda x, y : MASS_SHAIBA*(x+1)+ MASS_PLATFORM)
# # # load_mass_diff = np.fromfunction(shape=(4,6), function=lambda x, y : MASS_SHAIBA_DIFF*(x+1)+ MASS_PLATFORM_DIFF)
# # # print(load_mass)
# # # force_momentum = load_mass * DIAMETER/2 * (G-acceleration)
# # # force_momentum_diff = (  (DIAMETER/2 * (G-acceleration) * load_mass_diff)**2 +
# # #                            (load_mass/2 * (G-acceleration) * DIAMETER_DIFF) **2 +
# # #                            (load_mass * DIAMETER/2 * acceleration_diff)**2)**0.5


# # # f = open("table1.txt", 'w')
# # # for i in range(arr.shape[0]):
# # #     f.write( f"{i+1} & ${i//3 + 1}*m $ &"+ " & ".join( np.char.mod('%.2f', arr[i])) + "\\\\ \n")
# # #     f.write("\hline \n")


# # # f = open("table2.txt", 'w', encoding="utf-8")
# # # for i in range(arr_of_avrg.shape[0]):
# # #     f.write( f"\multirow{{2}}{{*}}{{${i + 1} m $}} & $t_{{\\text{{ср}}}}$, c &"+ " & ".join(np.char.mod('%.2f', arr_of_avrg[i])) + "\\\\ \n")
# # #     f.write( f"&$\Delta t$, c &"+ " & ".join( np.char.mod('%.2f', absolute_diff[i])) + "\\\\ \n")
# # #     f.write("\hline \n")

# # # f = open("table3.txt", 'w', encoding="utf-8")
# # # for i in range(force_momentum.shape[0]):
# # #     f.write( f"\multirow{{6}}{{*}}{{${i + 1} m $}} & $a$, мм/$c^2$ &"+ " & ".join(np.char.mod('%.2f', acceleration[i])) + "\\\\[5pt] \n")
# # #     f.write("\cline{2-8} \n")
# # #     f.write( f"&$\Delta a$, мм/$c^2$&"+ " & ".join( np.char.mod('%.2f', ((acceleration_diff)[i]))) + "\\\\[5pt] \n")
# # #     f.write("\cline{2-8} \n")
# # #     f.write( f"& $\\varepsilon$, рад/$c^2$ &"+ " & ".join(np.char.mod('%.2f', angular_accel[i])) + "\\\\[5pt] \n")
# # #     f.write("\cline{2-8} \n")
# # #     f.write( f"&$\Delta \\varepsilon$, рад/$c^2$ &"+ " & ".join( np.char.mod('%.2f', angular_accel_diff[i])) + "\\\\[5pt] \n")
# # #     f.write("\cline{2-8} \n")
# # #     f.write( f"&$M$, Н*мм &"+ " & ".join( np.char.mod('%.2f', ((force_momentum/1000000)[i]))) + "\\\\[5pt] \n")
# # #     f.write("\cline{2-8} \n")
# # #     f.write( f"&$\Delta M$, Н*мм &"+ " & ".join( np.char.mod('%.2f', ((force_momentum_diff/1000000)[i]))) + "\\\\[5pt] \n")
# # #     f.write("\cline{2-8} \n")
# # #     f.write("\hline \n")


# # # inertia = np.zeros((arr.shape[1]))
# # # friction = np.zeros((arr.shape[1]))
# # # inertia_diff = np.zeros((arr.shape[1]))
# # # friction_diff = np.zeros((arr.shape[1]))
# # # for i in range(6):
# # #     b, a, diff, b_diff, a_diff  = leastSquare(angular_accel.flatten()[i::6], force_momentum.flatten()[i::6])
# # #     print(b_diff, a_diff)
# # #     inertia[i] = b
# # #     friction[i] = a
# # #     inertia_diff[i] = b_diff
# # #     friction_diff[i] = a_diff

# # # f = open("table4.txt", 'w', encoding="utf-8")
# # # for i in range(6):
# # #     f.write( f"{i+1} & "+ f"{inertia[i]/1000000:.2f} & {inertia_diff[i]/1000000:.2f} & {friction[i]/1000000:.2f} & {friction_diff[i]/1000000:.2f}" + "\\\\ \n")
# # #     f.write("\hline \n")

# # # FIRST_RISKA = 57.0
# # # FIRST_RISKA_DIFF = 0.5
# # # BETWEEN_RISKA = 25.0
# # # BETWEEN_RISKA_DIFF = 0.2
# # # LOAD_HEIGHT = 40.0
# # # LOAD_HEIGHT_DIFF = 0.5
# # # radius = np.zeros((arr.shape[1]))
# # # radius_diff = np.zeros((arr.shape[1]))
# # # for i in range(arr.shape[1]):
# # #     radius[i] = FIRST_RISKA + i * BETWEEN_RISKA + 1/2 * LOAD_HEIGHT
# # # print(radius)

# # # for i in range(6):
# # #     np.savetxt(f"momentum-accel{i+1}.csv",np.vstack([force_momentum.flatten()[i::6]/1000000, force_momentum_diff.flatten()[i::6]/1000000, angular_accel.flatten()[i::6], angular_accel_diff.flatten()[i::6]]).T, delimiter=',' )

# # # for i in range(6):
# # #     np.savetxt(f"inertia-radius{i+1}.csv",np.vstack([inertia.flatten()[i::6]/1000000, inertia_diff.flatten()[i::6]/1000000, (radius**2/1000).flatten()[i::6]]).T, delimiter=',' )


# # # f = open("table5.txt", 'w', encoding="utf-8")
# # # f.write( f"$I$, г*$\\text{{м}}^2$ &"+ " & ".join( np.char.mod('%.2f', ((inertia/1000000)))) + "\\\\ \n")
# # # f.write("\hline \n")
# # # f.write( f" $R$, $\\text{{мм}}$ &"+ " & ".join(np.char.mod('%d', radius)) + "\\\\ \n")
# # # f.write("\hline \n")
# # # f.write( f"$R^2$, $\\text{{мм}}$ &"+ " & ".join( np.char.mod('%d', radius**2)) + "\\\\ \n")
# # # f.write("\hline \n")

# # # f = open("graph1.txt", 'w', encoding="utf-8")
# # # for i in range(6):
# # #     f.write(f"""
# # # \\addplot[only marks,pink{i+1}, mark size =2pt, error bars/.cd, y dir=both, y explicit, x dir=both, x explicit, error mark options={{
# # #       pink{i+1},
# # #       mark size=0.4pt,
# # #       line width=4pt
# # #     }}, error bar style={{fill=pink{i+1},scale=2, line width=1pt}}] table [y = y, y error = y-err, x = x, x error = x-err,  col sep=comma] {{momentum-accel{i+1}.csv}};
# # # \n""")
# # #     f.write(f"""
# # # \\addplot[pink{i+1}, domain=0:10] {{ {inertia[i]/1000000}*x+{friction[i]/1000000}}};
# # # \n""")


# # # four_m_load = np.zeros((arr.shape[1]))
# # # four_m_load_diff = np.zeros((arr.shape[1]))
# # # inertia_zero = np.zeros((arr.shape[1]))
# # # inertia_zero_diff = np.zeros((arr.shape[1]))
# # # for i in range(6):
# # #     b, a, diff, b_diff, a_diff  = leastSquare(radius**2, inertia)
# # #     print(b_diff, a_diff)
# # #     four_m_load[i] = b
# # #     inertia_zero[i] = a
# # #     four_m_load_diff[i] = b_diff
# # #     inertia_zero_diff[i] = a_diff
# # # print(four_m_load, inertia_zero, four_m_load_diff, inertia_zero_diff)
# # # f = open("graph2.txt", 'w', encoding="utf-8")
# # # for i in range(6):
# # #     f.write(f"""
# # # \\addplot[only marks,blue{i+1}, mark size =2pt, error bars/.cd, y dir=both, y explicit, x dir=both, x explicit, error mark options={{
# # #       blue{i+1},
# # #       mark size=0.4pt,
# # #       line width=4pt
# # #     }}, error bar style={{fill=blue{i+1},scale=2, line width=1pt}}] table [y = y, y error = y-err, x = x,  col sep=comma] {{inertia-radius{i+1}.csv}};
# # # \n""")
# # # f.write(f"""
# # # \\addplot[pink{6}, domain=0:45] {{ {four_m_load[i]/1000}*x+{inertia_zero[i]/1000000}}};
# # # \n""")