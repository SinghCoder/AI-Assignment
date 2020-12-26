import subprocess, os
import time
import glob
import sys

start = 10000
end = 100000
args = []
for num_games in range(start, end+1, 10000):
    if num_games != 10000:
        break
    to_train_first = "true"
    to_train_second = "false"    
    # subprocess.run('cp q1.dat q1_copy.dat', shell=True)
    for alpha_num in range(0, 11):
        # if alpha_num != 1:
        #     break
        # with open('q1.dat', 'rb') as src, open('q1_copy.dat', 'wb') as dest:
        #     dest.write(src.read())
    
        file_name_first = "q1.dat"
        file_name_second = "part2_right.dat"
        args.extend(["java", "-jar", "ai.jar"])
        args.append("--num")
        args.append(str(num_games))
        args.append("--agent")
        # alpha = str(round(alpha_num * 0.2, 2))
        alpha = "0.2"
        gamma = "0.8"
        epsilon = str(round( alpha_num*0.1 ,2))
        alpha2 = "0.2"
        gamma2 = "0.2"
        epsilon2 = "0.2"
        args.append(f"QLearningAgent:{to_train_first}:{file_name_first}:{alpha}:{gamma}:{epsilon}")
        args.append("--agent")
        args.append(f"RandomAgent:{to_train_second}:{file_name_second}")
        # args.append(f"QLearningAgent:{to_train_second}:{file_name_second}")
        print(f"num_games: {num_games}, alpha: {alpha}, gamma: {gamma}, epsilon: {epsilon}", file=sys.stderr)
        try:
            print(args)
            subprocess.run(args)
            proc = subprocess.Popen(args, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            out, err = proc.communicate()
            print(out)
        except Exception as e:
            print(f"Error occured: {e.__str__()}")
        args.clear()
        # subprocess.run('ls -l *.dat', shell=True)
        if os.path.exists(file_name_first):
            # print(f"removing {file_name_first}")
            os.remove(file_name_first)
        if os.path.exists(file_name_second):
            # print(f"removing {file_name_second}")
            os.remove(file_name_second)
        # time.sleep(2)
        # try:
        #     subprocess.run('ls -l *.dat', shell=True)
        # except:
        #     pass