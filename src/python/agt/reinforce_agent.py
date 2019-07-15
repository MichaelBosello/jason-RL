from tf_agents.networks import actor_distribution_network
from tf_agents.agents.reinforce import reinforce_agent

from generic_tf_agent import GenericTfAgent

class ReinforceAgent(GenericTfAgent):
  def create_agent(self):
    actor_net = actor_distribution_network.ActorDistributionNetwork(
      self.env.observation_spec(),
      self.env.action_spec(),
      fc_layer_params=self.fc_layer_params)
    self.tf_agent = reinforce_agent.ReinforceAgent(
      self.env.time_step_spec(),
      self.env.action_spec(),
      actor_network=actor_net,
      optimizer=self.optimizer,
      normalize_returns=True,
      train_step_counter=self.train_step_counter)

    if self.collect_steps_per_iteration < 2:
      self.collect_steps_per_iteration = 2
    self.episode_counter = 0

  def update_network(self, traj):
    if traj.is_boundary():
      self.episode_counter += 1
    if self.episode_counter >= self.collect_steps_per_iteration - 1:
        experience = self.replay_buffer.gather_all()
        train_loss = self.tf_agent.train(experience)
        self.replay_buffer.clear()
        self.episode_counter = 0