package gym.rest;

import java.util.List;

public class StateRest<T> {
    private List<T> state;
    private double reward;
    private boolean terminal;

    public List<T> getState() {
        return state;
    }

    public void setState(List<T> state) {
        this.state = state;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

	@Override
	public String toString() {
		return "StateRest [state=" + state + ", reward=" + reward + ", terminal=" + terminal + "]";
	}
}
