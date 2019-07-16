package gym;

import java.util.HashMap;
import java.util.Map;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.ObsProperty;
import gym.rest.GymRest;
import gym.rest.StateRest;

public class Cartpole extends Artifact{
	
	private static final boolean SHOW_VIEW = false;
	private static final int EVALUATION_INTERVAL = 1000;
	private static final int EVALUATION_EPISODES = 10;
	
	GymRest<Double> cartpole = new GymRest<>();
	
	@OPERATION
	public void init() {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("eval_interval", Integer.toString(EVALUATION_INTERVAL));
		parameters.put("num_eval_episodes", Integer.toString(EVALUATION_EPISODES));
		parameters.put("show_gui", Boolean.toString(SHOW_VIEW));
		StateRest<Double> state = cartpole.initialize("CartPole-v0", parameters);
		defineObsProperty("cart_position", state.getState().get(0));
		defineObsProperty("cart_velocity", state.getState().get(1));
		defineObsProperty("pole_position", state.getState().get(2));
		defineObsProperty("pole_velocity", state.getState().get(3));
	}

	@OPERATION
	public void move(String move) {

		if (SHOW_VIEW) {
			try {
				Thread.sleep(50);
			} catch (Exception e) {}
		}

		try {
			StateRest<Double> state;
			if (move.equals("right")) {
				state = cartpole.step(1);
			} else {
				state = cartpole.step(0);
			}
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
