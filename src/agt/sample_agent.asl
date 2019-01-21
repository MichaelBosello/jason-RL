// Agent sample_agent in project jacamoRL

/* Initial beliefs and rules */
myobs(myprop).

rl_parameter(myparam, myvalue).

rl_observe(mygoal, myobs).

rl_reward(mygoal, 10) :- myobs(myprop).

rl_terminal(mygoal) :- myobs(myprop).

/* Initial goals */

!start.

/* Plans */

+!start : true <- .print("hello world."); rl.execute(mygoal).

@exe[rl_goal(mygoal)]
+!myplan : true <- .print("RL executed!").

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
