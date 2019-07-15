import imageio
import IPython
import matplotlib
import matplotlib.pyplot as plt
import PIL.Image
import pyvirtualdisplay

from tf_agents.environments import suite_gym
from tf_agents.environments import tf_py_environment


class GenericTfEnv():
  def __init__(self, env_name, params={}):
    #params
    self.env_name = env_name
    self.eval_interval = int(params.get('eval_interval', 1000))
    self.num_eval_episodes = int(params.get('num_eval_episodes', 10))
    self.show_gui = params.get('show_gui', False)

    self.py_env = suite_gym.load(env_name)
    self.env = tf_py_environment.TFPyEnvironment(self.py_env)
    self.env.reset()

    #if self.show_gui:
    #  PIL.Image.fromarray(self.py_env.render())


  def step(self, action):
    return self.env.step(action)

  def get_current_time_step(self):
    return self.env.current_time_step()

  def get_observation_spec(self):
    return self.env.time_step_spec().observation

  def get_action_spec(self):
    return self.env.action_spec()