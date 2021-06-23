rl_algorithm(reach_finish, sarsa).

rl_parameter(policy, egreedy).
rl_parameter(alpha, 0.26).
rl_parameter(gamma, 0.9).
rl_parameter(epsilon, 0.4).
rl_parameter(epsilon_decay, 0.9999).
rl_parameter(epsilon_min, 0).

rl_observe(reach_finish, pos).

rl_reward(reach_finish, 10) :- finishline.
rl_reward(reach_finish, -1) :- not finishline.

rl_terminal(reach_finish) :- finishline.

!start.

//example of use of expected return
/* 
+!start : rl.expected_return(reach_finish,R) & R > 50 <- rl.execute(reach_finish); !start.

+!start <- !move(right); !move(down); rl.execute(reach_finish); !start.
*/

//without expected return
+!start : true <- rl.execute(reach_finish); !start. //!start in order to continue after the end of the episode

@action[rl_goal(reach_finish), rl_param(direction(set(right, left, up, down)))]
+!move(Direction) <- move(Direction).

{ include("$jacamoJar/templates/common-cartago.asl") }
