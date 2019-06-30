import IPython

import tensorflow as tf
import numpy as np
import random
import ast

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
  def __init__(self, action_specification, observation_specification, initial_state, params={}):
    #params
    self.replay_buffer_capacity = int(params.get('replay_buffer_capacity', 100000))
    self.fc_layer_params = ast.literal_eval(params.get('fc_layer_params', '(25, )'))
    self.initial_collect_steps = int(params.get('initial_collect_steps', 100))
    self.collect_steps_per_iteration = int(params.get('collect_steps_per_iteration', 0))
    self.batch_size = int(params.get('batch_size', 32))
    self.learning_rate = float(params.get('learning_rate', 1e-5))
    self.epsilon = float(params.get('epsilon', 0.3))
    self.epsilon_decay = float(params.get('epsilon_decay', 0.99999))
    self.gamma = float(params.get('gamma', 0.95))
    #env
    self.train_py_env = GenericEnv(action_specification, observation_specification, initial_state)
    self.env = tf_py_environment.TFPyEnvironment(self.train_py_env)
    #agent initialization
    q_net = q_network.QNetwork(
      self.env.observation_spec(),
      self.env.action_spec(),
      fc_layer_params=self.fc_layer_params)

    optimizer = tf.compat.v1.train.AdamOptimizer(learning_rate=self.learning_rate)
    train_step_counter = tf.compat.v2.Variable(0)

    self.tf_agent = dqn_agent.DqnAgent(
      self.env.time_step_spec(),
      self.env.action_spec(),
      q_network=q_net,
      optimizer=optimizer,
      td_errors_loss_fn=dqn_agent.element_wise_squared_loss,
      train_step_counter=train_step_counter,
      gamma=self.gamma)
    self.tf_agent.initialize()
    self.random_policy = random_tf_policy.RandomTFPolicy(time_step_spec=self.env.time_step_spec(), action_spec=self.env.action_spec())

    self.replay_buffer = tf_uniform_replay_buffer.TFUniformReplayBuffer(
      data_spec=self.tf_agent.collect_data_spec,
      batch_size=self.env.batch_size,
      max_length=self.replay_buffer_capacity)

    self.init_steps = 0
    self.episode_steps = 0

  def update(self, observation, reward, is_terminal, action_step):
    time_step = self.env.current_time_step()
    self.train_py_env.set_next(observation, reward, is_terminal)
    next_time_step = self.env.step(action_step.action)
    traj = trajectory.from_transition(time_step, action_step, next_time_step)
    # Add trajectory to the replay buffer
    self.replay_buffer.add_batch(traj)
    if self.init_steps == self.initial_collect_steps:
      self.dataset = self.replay_buffer.as_dataset(
        num_parallel_calls=3, sample_batch_size=self.batch_size, num_steps=2).prefetch(3)
      self.iterator = iter(self.dataset)
      # (Optional) Optimize by wrapping some of the code in a graph using TF function.
      self.tf_agent.train = common.function(self.tf_agent.train)
      # Reset the train step
      self.tf_agent.train_step_counter.assign(0)
    elif self.init_steps > self.initial_collect_steps:
      if self.episode_steps >= self.collect_steps_per_iteration:
        experience, unused_info = next(self.iterator)
        train_loss = self.tf_agent.train(experience)
        self.episode_steps = 0
      else:
        self.episode_steps = self.episode_steps + 1
    if self.init_steps <= self.initial_collect_steps:
      self.init_steps = self.init_steps + 1
    
  def get_train_action(self):
    time_step = self.env.current_time_step()
    if random.uniform(0, 1) < self.epsilon:
      action = self.tf_agent.policy.action(time_step)
    else:
      action = self.random_policy.action(time_step)
    self.epsilon *= self.epsilon_decay
    print(self.epsilon)
    return action
  def get_greedy_action(self):
    time_step = self.env.current_time_step()
    return self.tf_agent.policy.action(time_step)