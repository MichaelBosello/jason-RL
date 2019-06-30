import IPython

import tensorflow as tf
import numpy as np

from tf_agents.agents.dqn import dqn_agent
from tf_agents.environments import suite_gym
from tf_agents.environments import tf_py_environment
from tf_agents.networks import q_network
from tf_agents.replay_buffers import tf_uniform_replay_buffer
from tf_agents.trajectories import trajectory
from tf_agents.utils import common
from tf_agents.specs import array_spec

from random import randint

from generic_environment import GenericEnv
from dqn import DQN

tf.compat.v1.enable_v2_behavior()

#params
num_episode = 2000  # @param
board_size = 9

#env
dqn = DQN(
    array_spec.BoundedArraySpec(
        shape=(), dtype=np.int32, minimum=0, maximum=3, name='action'),
    array_spec.BoundedArraySpec(
        shape=(2,), dtype=np.int32, minimum=0, maximum=board_size, name='observation'),
        np.array([0, 0], dtype=np.int32))

#[row, column]
state = np.array([0, 0], dtype=np.int32)

episode_count = 0
step_count = 0
while episode_count < num_episode:
  if episode_count < 1000:
    action_step = dqn.get_train_action()
  else:
    if episode_count == 1000:
      print("$$$$$$$ use greedy $$$$$$$")
    action_step = dqn.get_greedy_action()
  action = action_step.action.numpy()
  #up
  if action == 0 and state[0] > 0:
    state[0] = state[0] - 1
  #down
  elif action == 1 and state[0] < board_size:
    state[0] = state[0] + 1
  #left
  elif action == 2 and state[1] > 0:
    state[1] = state[1] - 1
  #right
  elif action == 3 and state[1] < board_size:
    state[1] = state[1] + 1

  if state[0] == board_size and state[1] == board_size:
    reward = 10
    is_terminal = True
    state = np.array([randint(0, board_size), randint(0, board_size)], dtype=np.int32)
  #elif step_count >= 600:
  #  reward = -1 
  #  is_terminal = True
  #  state = np.array([randint(0, board_size), randint(0, board_size)], dtype=np.int32)
  else:
    reward = -1
    is_terminal = False

  dqn.update(state, reward, is_terminal, action_step)
  
  if is_terminal:
    episode_count = episode_count + 1
    print(f"end episode in {step_count}")
    step_count = 0

  step_count = step_count + 1