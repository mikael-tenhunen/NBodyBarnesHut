#!/bin/bash
# command line arguments:
#	1. number of time steps
#	2. min mass of a body
#	3. max mass of a body
#	4. max start velocity component (x and y)
#get number of processors
#NPROC=$(grep -c ^processor /proc/cpuinfo)
#if (($NPROC>8))
#then NPROC=8
#fi
timesteps=15000
minmass=100000
maxmass=100000000
maxvel=0
far=0,9
#timesteps=$1
#minmass=$2
#maxmass=$3
#maxvel=$4
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
		java -jar NBodyBarnesHut.jar $size $timesteps 1 $minmass $maxmass $maxvel $far $graphics | grep seconds >>../result.txt
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
			 java -jar NBodyBarnesHut.jar $size $timesteps $processors $minmass $maxmass $maxvel $far $graphics | grep seconds >> ../result.txt
		done
	done
done



