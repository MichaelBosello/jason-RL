import IPython

import tensorflow as tf
import numpy as np
import random

from tf_agents.agents.dqn import dqn_agent
from tf_agents.environments import suite_gym
from tf_agents.environments import tf_py_environment
from tf_agents.networks import q_network
from tf_agents.replay_buffers import tf_uniform_replay_buffer
from tf_agents.trajectories import trajectory
from tf_agents.utils import common
from tf_agents.specs import array_spec
from tf_agents.policies import random_tf_policy

from generic_environment import GenericEnv

tf.compat.v1.enable_v2_behavior()

class DQN():
  def __init__(self, action_specification, observation_specification, initial_state):
    #params
    self._replay_buffer_capacity = 100000  # @param
    self._fc_layer_params = (25, )  # @param
    self._initial_collect_steps = 100  # @param
    self._collect_steps_per_iteration = 0  # @param
    self._batch_size = 32  # @param
    self._learning_rate = 1e-5  # @param
    self._epsilon=0.3
    self._epsilon_decay=0.99999
    #env
    self.train_py_env = GenericEnv(action_specification, observation_specification, initial_state)
    self.env = tf_py_environment.TFPyEnvironment(self.train_py_env)
    #agent initialization
    q_net = q_network.QNetwork(
      self.env.observation_spec(),
      self.env.action_spec(),
      fc_layer_params=self._fc_layer_params)

    optimizer = tf.compat.v1.train.AdamOptimizer(learning_rate=self._learning_rate)
    train_step_counter = tf.compat.v2.Variable(0)

    self.tf_agent = dqn_agent.DqnAgent(
      self.env.time_step_spec(),
      self.env.action_spec(),
      q_network=q_net,
      optimizer=optimizer,
      td_errors_loss_fn=dqn_agent.element_wise_squared_loss,
      train_step_counter=train_step_counter,
      gamma=0.95)
    self.tf_agent.initialize()
    self._random_policy = random_tf_policy.RandomTFPolicy(time_step_spec=self.env.time_step_spec(), action_spec=self.env.action_spec())

    self._replay_buffer = tf_uniform_replay_buffer.TFUniformReplayBuffer(
      data_spec=self.tf_agent.collect_data_spec,
      batch_size=self.env.batch_size,
      max_length=self._replay_buffer_capacity)

    self.init_steps = 0
    self.episode_steps = 0

  def update(self, observation, reward, is_terminal, action_step):
    time_step = self.env.current_time_step()
    self.train_py_env.set_next(observation, reward, is_terminal)
    next_time_step = self.env.step(action_step.action)
    traj = trajectory.from_transition(time_step, action_step, next_time_step)
    # Add trajectory to the replay buffer
    self._replay_buffer.add_batch(traj)
    if self.init_steps == self._initial_collect_steps:
      self._dataset = self._replay_buffer.as_dataset(
        num_parallel_calls=3, sample_batch_size=self._batch_size, num_steps=2).prefetch(3)
      self._iterator = iter(self._dataset)
      # (Optional) Optimize by wrapping some of the code in a graph using TF function.
      self.tf_agent.train = common.function(self.tf_agent.train)
      # Reset the train step
      self.tf_agent.train_step_counter.assign(0)
    elif self.init_steps > self._initial_collect_steps:
      if self.episode_steps >= self._collect_steps_per_iteration:
        experience, unused_info = next(self._iterator)
        train_loss = self.tf_agent.train(experience)
        self.episode_steps = 0
      else:
        self.episode_steps = self.episode_steps + 1
    if self.init_steps <= self._initial_collect_steps:
      self.init_steps = self.init_steps + 1
    
  def get_train_action(self):
    time_step = self.env.current_time_step()
    if random.uniform(0, 1) < self._epsilon:
      action = self.tf_agent.policy.action(time_step)
    else:
      action = self._random_policy.action(time_step)
    self._epsilon *= self._epsilon_decay
    print(self._epsilon)
    return action
  def get_greedy_action(self):
    time_step = self.env.current_time_step()
    return self.tf_agent.policy.action(time_step)