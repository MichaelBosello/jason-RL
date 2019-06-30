rl_algorithm(reach_finish, dqn).

rl_parameter(policy, egreedy).
rl_parameter(learning_rate, 0.0001).
rl_parameter(gamma, 0.95).
rl_parameter(epsilon, 0.3).
rl_parameter(epsilon_decay, 0.99999).
rl_parameter(batch_size, 32).
rl_parameter(fc_layer_params, '(25, )').

rl_observe(reach_finish, pos(int(0, 9), int(0, 9))).

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
