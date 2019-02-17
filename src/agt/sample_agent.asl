rl_parameter(policy, egreedy).
rl_parameter(alpha, 0.1).
rl_parameter(gamma, 0.9).
rl_parameter(epsilon, 1).
rl_parameter(epsilon_decay, 0.99996).
rl_parameter(epsilon_min, 0.01).

rl_observe(reach_finish, pos).

rl_reward(reach_finish, 30) :- finishline.
rl_reward(reach_finish, -2) :- not finishline.

rl_terminal(reach_finish) :- finishline.

!start.

//example of use of expected return
/* 
+!start : rl.expected_return(reach_finish,R) & R > 50 <- rl.execute(reach_finish); !start.

+!start <- !move(right); !move(down); rl.execute(reach_finish); !start.
*/

//without expected return
+!start : true <- rl.execute(reach_finish); !start. //!start in order to continue after the end of the episode

@exe[rl_goal(reach_finish), rl_param(direction(set(right, left, up, down)))]
+!move(Direction) <- move(Direction).

{ include("$jacamoJar/templates/common-cartago.asl") }
