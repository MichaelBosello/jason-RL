import numpy as np
from random import randint

from tf_agents.specs import array_spec

from generic_environment import GenericEnv
from dqn_agent import DqnAgent

#params
num_episode = 2000  # @param
board_size = 9

#env
dqn = DqnAgent(
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