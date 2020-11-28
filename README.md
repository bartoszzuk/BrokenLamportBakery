# BrokenLamportBakery

Buggy implementation of Lamport's bakery algorithm with deterministic tests.  
Created to see [vmlens plugin](https://vmlens.com/) in action.

## Requirements

- Java SDK 1.8
- Maven

## Run

```sh
$ cd BrokenLamportBakery
$ mvn clean test
```

Check folder *target/interleave* for any interleavings which resulted in the failure of the tests.

## Bug

Each thread, roughly speaking, goes through four fazes. 

    1. Entering faze [E] - thread picks a ticket 
    2. Waiting faze [W] - thread waits for others if it needs to do so
    3. Critical section [C] - thread enters critical section
    4. Unlocking [U] - thread sets ticket to 0
    
The inserted bug causes threads to not wait for others to finish picking their tickets at the entering faze.
Here is a possible scenario were the critical section could be breached.  

| Action 	|      Thread 1     	|      Thread 2     	|                     Notes                    	|
|:------:	|:-----------------:	|:-----------------:	|:--------------------------------------------:	|
|    1   	| [E] not completed 	|         -         	|          Sees that current max is 0          	|
|    2   	|         -         	|   [E] completed   	|           Gets ticket with number 1          	|
|    3   	|         -         	|   [W] completed   	| **Doesn't wait for Thread 1 to pick ticket** 	|
|    4   	|         -         	| [C] not completed 	|                       -                      	|
|    5   	|   [E] completed   	|         -         	|           Gets ticket with number 1          	|
|    6   	|   [W] completed   	|         -         	|         Has lower id so doesn't wait         	|
|    7   	| [C] not completed 	|                   	|         **Critical section breached**        	|
|    8   	|   [U] completed   	|         -         	|                       -                      	|
|    9   	|         -         	|   [U] completed   	|                       -                      	|