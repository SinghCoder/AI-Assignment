import os, csv
command = ''

num_powers = 6
num_vals_in_each = 10

# alpha 0.2-0.8
# gamma 0.2-0.8

results_file = open('results.csv', 'w')
results_writer = csv.writer(results_file)
results_writer.writerow(['NumGamesPlayed', 'Alpha', 'Gamma', 'Score'])

# for i in range(num_powers+1):
#     multiplier = pow(10, i)
#     for j in range(1, num_vals_in_each+1):
#         val = multiplier*j
        # print('Number of games: {}'.format(val))
for alpha_int in range(2, 9,2):
    gamma = alpha_int * 0.1
    alpha = .2
    print("--num 100000 --agent QLearningAgent:true:q1.dat:{}:{} --agent QLearningAgent:false:q2.dat".format(round(alpha,2), round(gamma,2), alpha_int//2, round(alpha,2), round(gamma,2)))

for alpha_int in range(2, 9,2):
    alpha = alpha_int * 0.1
    gamma = .8
    print("--num 200000 --agent QLearningAgent:true:q1.dat:{}:{} --agent QLearningAgent:false:q2.dat".format(round(alpha,2), round(gamma,2), alpha_int//2, round(alpha,2), round(gamma,2)))