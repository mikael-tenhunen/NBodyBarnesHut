#!/bin/bash
# command line arguments:
#	1. number of time steps
#get number of processors
#NPROC=$(grep -c ^processor /proc/cpuinfo)
#if (($NPROC>8))
#then NPROC=8
#fi
timesteps=22750
far=0.9
#timesteps=$1
#far=$5
NPROC=4
graphics=no
echo $NPROC
nrtries=5
cd ./dist
printf "results\n----------------------------------------------------------\n" >../result.txt
#sequential execution
for size in 120 180 240
do
	printf " --------------Size: %02i--------------\n" $size >>../result.txt
	printf "Sequential execution: \n" >>../result.txt
	for (( i=1; i<=nrtries; i++ ))
	do
		java -jar NBodyBarnesHut.jar $size $timesteps $far 1 $graphics | grep seconds >>../result.txt
	done			
done		
#for all possible number of processors
for (( processors=2; processors<=NPROC; processors++ ))
do
	printf " =============Processors: %02i==============\n" $processors >>../result.txt
	#for different number of bodies
	for size in 120 180 240
	do
		printf " --------------Size: %02i--------------\n" $size >>../result.txt
		#try many times
		printf "Parallel execution: \n" >>../result.txt
		for (( i=1; i<=nrtries; i++ ))
		do
			 java -jar NBodyBarnesHut.jar $size $timesteps $far $processors $graphics | grep seconds >> ../result.txt
		done
	done
done



