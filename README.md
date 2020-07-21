# BDI-RL Framework Proof of Concept
This project is a PoC of the framework presented in 

***From Programming Agents to Educating Agents – A Jason-based Framework for Integrating Learning in the Development of Cognitive Agents***

Paper avaliable at: https://link.springer.com/chapter/10.1007/978-3-030-51417-4_9 (short freely accessible version [here](https://cgi.csc.liv.ac.uk/~lad/emas2019/accepted/EMAS2019_paper_33.pdf))

Slides of the presentation at EMAS: https://www.slideshare.net/MichaelBosello/emas-2019-from-programming-agents-to-educating-agents

This is an integration of BDI agents and Reinforcement Learning.
It is based on [Jason](http://jason.sourceforge.net/wp/) (Actually, it is a [JaCaMo](http://jacamo.sourceforge.net/) project).

The basic idea is that a developer could write some plans and let the agent itself learn other plans and use them in a seamless way. This is not only for a specific ad hoc problem but as a general feature of the agent platform.

In short, the aim of the framework is to enable the developer to define the learning components with high-level abstractions as the BDI ones are. Then, these informations injeced by the developer are used by the agent to learn itself how to fulfill some tasks. 

The work of the developer moves from write plans to define a learning phase.

As reference examples, we provide several agents and environments.
+ The Gridworld env and the SARSA algorithm are implemented in Java ex novo.
+ We implemented a RESTful service in Python that provides the capability of [TF-Agents](https://github.com/tensorflow/agents) to our framework. TF-Agents is a library that offers the core elements of RL and several RL algorithms, which is built on top of [TensorFlow](https://www.tensorflow.org/). A Java class that implements the interface requested by our framework consumes the above-mentioned service, completing the bridge between our framework and TF-Agents. We tested two algorithms offered by the library: DQN and REINFORCE.
+ We provide two classic control environments, namely CartPole and MountainCar. To obtain an ensemble of test tasks, we implemented a bridge – with a REST service, in the same way as for the algorithms – between the Jason environment and [Gym](https://github.com/openai/gym), a suite of RL tasks.

# Quick start
To run the agent system:

	./gradlew run

### Python algorithms 
If you use an algorithm implemented in python, you must run the python agent server _before_ the agent system

*Check the dependencies below*

You can run the python service with the apposite gradle task:

	./gradlew --stop
	./gradlew runPythonAgent

_or_ with the python command (from the directory src/python/agt):

	python3 tf_agent_rest.py

### Python environments 
If you use an environment implemented in python, you must run the python environment server _before_ the agent system

*Check the dependencies below*

You can run the python service with the apposite gradle task:

	./gradlew --stop
	./gradlew runPythonEnv

_or_ with the python command (from the directory src/python/env):

	python3 tf_env_rest.py

## Python dependencies
You must install python 3 (System tested on version 3.7.3) along with the following dependencies:

	pip3 install tf-agents-nightly
	pip3 install tensorflow==2.0.0-beta1
	pip3 install 'gym==0.10.11'
	pip3 install IPython
	pip3 install flask flask-jsonpify flask-restful
# Configuration
## Algorithm Parameters
You can change the parameters with the agent beliefs.

## Serialization
From `agt/rl.algorithm.BehaviourSerializer`:

To serialize learning progress to a file: `saveProgress = true`

To load learning progress from a file: `loadProgress = true;`

## Gridworld configuration
Show/hide the Gridworld GUI with the `SHOW_VIEW` boolean in `env/gridworld.GridWorldEnv`

## Save simulation results
To log errors did in each episodes to a file change the boolean `SAVE_RESULT` in `env/simulation.EpisodicSimulation`
