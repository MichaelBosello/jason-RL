rl_algorithm(cartpole, dqn).

rl_parameter(policy, egreedy).
rl_parameter(learning_rate, 0.001).
rl_parameter(gamma, 1).
rl_parameter(epsilon, 0.3).
rl_parameter(epsilon_decay, 0.99999).
rl_parameter(batch_size, 64).
rl_parameter(fc_layer_params, '(100, )').

rl_observe(cartpole, cart_position(real(-4.8000002, 4.8000002))).
rl_observe(cartpole, cart_velocity(real(-340282350000000000000000000000000000000, 340282350000000000000000000000000000000))).
rl_observe(cartpole, pole_position(real(-0.41887903, 0.41887903))).
rl_observe(cartpole, pole_velocity(real(-340282350000000000000000000000000000000, 340282350000000000000000000000000000000))).

rl_reward(cartpole, 1) :- not gameover.
rl_reward(cartpole, 0) :- gameover.

rl_terminal(cartpole) :- gameover.

!start.

+!start : true <- rl.execute(cartpole); !start. //!start in order to continue after the end of the episode

@action[rl_goal(cartpole), rl_param(direction(set(right, left)))]
+!move(Direction) <- move(Direction).

{ include("$jacamoJar/templates/common-cartago.asl") }
