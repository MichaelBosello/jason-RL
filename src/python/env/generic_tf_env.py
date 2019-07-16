from tf_agents.environments import suite_gym

class GenericTfEnv():
  def __init__(self, env_name, params={}):
    #params
    self.env_name = env_name
    self.show_gui = params.get('show_gui', 'false') == 'true'

    self.env = suite_gym.load(env_name)
    self.env.reset()

    if self.show_gui:
      self.env.render()


  def step(self, action):
    step = self.env.step(action)
    if self.show_gui:
      self.env.render()
    return step

  def get_current_time_step(self):
    return self.env.current_time_step()

  def get_observation_spec(self):
    return self.env.time_step_spec().observation

  def get_action_spec(self):
    return self.env.action_spec()