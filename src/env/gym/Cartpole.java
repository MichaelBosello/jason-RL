package gym;

import java.util.HashMap;
import java.util.Map;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.ObsProperty;
import gym.rest.GymRest;
import gym.rest.StateRest;
import simulation.EpisodicLogger;

import jason.asSyntax.Atom;

public class Cartpole extends Artifact{
	
	private static final boolean SHOW_VIEW = false;
	private static final int EVALUATION_INTERVAL = 50;
	private static final int EVALUATION_EPISODES = 100;
	
	GymRest<Double> cartpole = new GymRest<>();
	
	private EpisodicLogger logger = new EpisodicLogger("CartPole-v0", true, 15);;
	private int trainEpisodeCount = 0;
	private int episodeReward = 0;
	private int episodeEvaluation = 0;
	private double evaluationRewards = 0;
	
	private int step = 0;
	
	@OPERATION
	public void init() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("show_gui", Boolean.toString(SHOW_VIEW));
		StateRest<Double> state = cartpole.initialize("CartPole-v0", parameters);
		defineObsProperty("cart_position", state.getState().get(0));
		defineObsProperty("cart_velocity", state.getState().get(1));
		defineObsProperty("pole_position", state.getState().get(2));
		defineObsProperty("pole_velocity", state.getState().get(3));
		
		defineObsProperty("rl_parameter", new Atom("policy"), new Atom("egreedy"));
	}

	@OPERATION
	public void move(String move) {
		if (SHOW_VIEW) { 
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}

		StateRest<Double> state;
		if (move.equals("right")) {
			state = cartpole.step(0);
		} else {
			state = cartpole.step(1);
		}

		episodeReward += state.getReward();
		if(state.isTerminal()) {
			if(episodeEvaluation > 0) {
				episodeEvaluation--;
				evaluationRewards += episodeReward;
				if(episodeEvaluation == 0) {
					logger.episodeEnd(evaluationRewards/EVALUATION_EPISODES);
					evaluationRewards = 0;

					ObsProperty policy = getObsProperty("rl_parameter");
					policy.updateValue(1, new Atom("egreedy"));
				}
			} else {
				trainEpisodeCount++;
				if(trainEpisodeCount % EVALUATION_INTERVAL == 0) {
					ObsProperty policy = getObsProperty("rl_parameter");
					policy.updateValue(1, new Atom("greedy"));
					episodeEvaluation = EVALUATION_EPISODES;
					
					System.out.println("step " + step);
				}
			}
			episodeReward = 0;
		}
		
		step++;
		updatePercepts(state);
	}
	
	private void updatePercepts(StateRest<Double> state) {
		ObsProperty cart_position = getObsProperty("cart_position");
		cart_position.updateValue(0, state.getState().get(0));
		ObsProperty cart_velocity = getObsProperty("cart_velocity");
		cart_velocity.updateValue(0, state.getState().get(1));
		ObsProperty pole_position = getObsProperty("pole_position");
		pole_position.updateValue(0, state.getState().get(2));
		ObsProperty pole_velocity = getObsProperty("pole_velocity");
		pole_velocity.updateValue(0, state.getState().get(3));
		if (state.isTerminal()) {
			if (!hasObsProperty("gameover"))
				defineObsProperty("gameover");
		} else {
			try {
				removeObsProperty("gameover");
			} catch (IllegalArgumentException e) {}
		}
	}
}
