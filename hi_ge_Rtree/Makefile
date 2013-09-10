# Makefile for NestedLoopJoin both single machine and multi-nodes  
External_library=/home/liang/Desktop/parallel_prob_skyline/External

all: 
	jar -cvf ./probSkyline.jar -C com/ .     # for hadoop

clean:
	rm -rf probSkyline/
	rm probSkyline.jar

instance_generator:
	./bin/InstGenexe.sh

testAngleOverlap:
	./bin/testExe.sh
