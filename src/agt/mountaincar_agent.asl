rl_algorithm(reach_top, dqn).

rl_parameter(learning_rate, 0.0008).
rl_parameter(gamma, 0.99).
rl_parameter(epsilon, 0.1).
rl_parameter(epsilon_decay, 1).
rl_parameter(fc_layer_params, '(256, 256)').
rl_parameter(batch_size, 64).
rl_parameter(replay_buffer_capacity, 100000).
rl_parameter(initial_collect_steps, 0).
rl_parameter(collect_steps_per_iteration, 0).

rl_observe(reach_top, position(real(-1.2, 0.6))).
rl_observe(reach_top, speed(real(-0.07, 0.07))).

rl_reward(reach_top, -1).

rl_terminal(reach_top) :- on_top.

!start.

+!start : true <- rl.execute(reach_top); !start. //!start in order to continue after the end of the episode

@action[rl_goal(reach_top), rl_param(direction(set(back, none, forth)))]
+!move(Direction) <- move(Direction).

{ include("$jacamoJar/templates/common-cartago.asl") }
