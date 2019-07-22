from tf_agents.agents.dqn import dqn_agent
from tf_agents.networks import q_network

from generic_tf_agent import GenericTfAgent

class DqnAgent(GenericTfAgent):
  def __init__(self, action_specification, observation_specification, initial_state, params={}):
    #params
    self.initial_collect_steps = int(params.get('initial_collect_steps', 1000))
    self.collect_steps_per_iteration = int(params.get('collect_steps_per_iteration', 1))
    self.batch_size = int(params.get('batch_size', 64))

    super(DqnAgent, self).__init__(action_specification, observation_specification, initial_state, params)

  def create_agent(self):
    q_net = q_network.QNetwork(
        self.env.observation_spec(),
        self.env.action_spec(),
        fc_layer_params=self.fc_layer_params)
    self.tf_agent = dqn_agent.DqnAgent(
        self.env.time_step_spec(),
        self.env.action_spec(),
        q_network=q_net,
        optimizer=self.optimizer,
        td_errors_loss_fn=dqn_agent.element_wise_squared_loss,
        train_step_counter=self.train_step_counter,
        gamma=self.gamma)

    self.init_steps = 0
    self.episode_steps = 0

  def update_network(self, traj):
    if self.init_steps == self.initial_collect_steps:
      self.dataset = self.replay_buffer.as_dataset(
        num_parallel_calls=3, sample_batch_size=self.batch_size, num_steps=2).prefetch(3)
      self.iterator = iter(self.dataset)
    elif self.init_steps >= self.initial_collect_steps:
      if self.episode_steps >= self.collect_steps_per_iteration - 1:
        experience, unused_info = next(self.iterator)
        train_loss = self.tf_agent.train(experience)
        #print('train loss ', train_loss.loss)
        self.episode_steps = 0
      else:
        self.episode_steps = self.episode_steps + 1
    if self.init_steps <= self.initial_collect_steps:
      self.init_steps = self.init_steps + 1

  def get_train_action(self):
    time_step = self.env.current_time_step()
    if self.init_steps < self.initial_collect_steps:
      return self.random_policy.action(time_step)
    else:
      return super(DqnAgent, self).get_train_action()