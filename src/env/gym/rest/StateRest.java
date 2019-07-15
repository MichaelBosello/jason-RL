package gym.rest;

import java.util.List;

public class StateRest<T> {
    private List<T> state;
    private double reward;
    private boolean is_terminal;

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

    public boolean isIs_terminal() {
        return is_terminal;
    }

    public void setIs_terminal(boolean is_terminal) {
        this.is_terminal = is_terminal;
    }
}
