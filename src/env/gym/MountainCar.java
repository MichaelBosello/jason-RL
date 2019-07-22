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

public class MountainCar extends Artifact{
	
	private static final boolean SHOW_VIEW = false;
	private static final int EVALUATION_INTERVAL = 100;
	private static final int EVALUATION_EPISODES = 100;
	
	GymRest<Double> mountainCar = new GymRest<>();
	
	private EpisodicLogger logger = new EpisodicLogger("MountainCar-v0", true, 15);
	private int trainEpisodeCount = 0;
	private int episodeReward = 0;
	private int episodeEvaluation = 0;
	private double evaluationRewards = 0;
	
	private int step = 0;
	
	@OPERATION
	public void init() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("show_gui", Boolean.toString(SHOW_VIEW));
		StateRest<Double> state = mountainCar.initialize("MountainCar-v0", parameters);
		defineObsProperty("position", state.getState().get(0));
		defineObsProperty("speed", state.getState().get(1));
		defineObsProperty("proximity", 0);
		
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
		if (move.equals("back")) {
			state = mountainCar.step(0);
		} else if (move.equals("forth")) {
			state = mountainCar.step(2);
		} else {
			state = mountainCar.step(1);
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
		ObsProperty position = getObsProperty("position");
		position.updateValue(0, state.getState().get(0));
		ObsProperty speed = getObsProperty("speed");
		speed.updateValue(0, state.getState().get(1));
		if (state.isTerminal()) {
			if (!hasObsProperty("on_top"))
				defineObsProperty("on_top");
		} else {
			try {
				removeObsProperty("on_top");
			} catch (IllegalArgumentException e) {}
		}
		double pos = state.getState().get(0);
		ObsProperty proximity = getObsProperty("proximity");
		double proximity_reward = 0;
		if(pos > -0.4) {
			proximity_reward = Math.pow(1 + pos, 2);
		}
		proximity.updateValue(0, proximity_reward);
	}
}
