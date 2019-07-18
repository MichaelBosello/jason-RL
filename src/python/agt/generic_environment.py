import tensorflow as tf
import numpy as np

from tf_agents.environments import py_environment
from tf_agents.specs import array_spec
from tf_agents.trajectories import time_step as ts

class GenericEnv(py_environment.PyEnvironment):

  def __init__(self, actions, observations, initial_state):
    self._action_spec = actions
    self._observation_spec = observations
    self._initial_state = initial_state
    self._state = initial_state
    self._is_episode_end = False
    self._episode_ended = False

  def action_spec(self):
    return self._action_spec

  def observation_spec(self):
    return self._observation_spec

  def _reset(self):
    self._episode_ended = False
    self._is_episode_end = False
    return ts.restart(self._state)

  def set_next(self, state, reward, is_episode_end):
      self._state = state
      self._reward = reward
      self._is_episode_end = is_episode_end

  def _step(self, action):

    if self._episode_ended:
      # The last action ended the episode. Ignore the current action and start a new episode.
      return self.reset()

    # Make sure episodes don't go on forever.
    if self._is_episode_end:
      self._episode_ended = True
    
    if self._episode_ended:
      return ts.termination(self._state, self._reward)
    else:
      return ts.transition(self._state, reward=self._reward, discount=1.0)