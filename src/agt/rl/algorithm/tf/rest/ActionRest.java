package rl.algorithm.tf.rest;

import java.util.List;

public class ActionRest<T> {
    private List<T> action;

    public List<T> getAction() {
        return action;
    }

    public void setAction(List<T> action) {
        this.action = action;
    }
}
