// Agent sample_agent in project jacamoRL

/* Initial beliefs and rules */
rl_parameter(policy, egreedy).
rl_parameter(alpha, 0.6).
rl_parameter(gamma, 0.9).
rl_parameter(epsilon, 0.3).

rl_observe(reach_finish, pos).

rl_reward(reach_finish, 100) :- finishline.
rl_reward(reach_finish, -1) :- not finishline.

rl_terminal(reach_finish) :- finishline.

/* Initial goals */

!start.

/* Plans */

+!start : true <- rl.execute(reach_finish); !start.

@exe[rl_goal(reach_finish), rl_param(direction(set(right, left, up, down)))]
+!move(Direction) : true <- move(Direction).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
