package rl.algorithm.tf.rest;

import java.util.List;
import java.util.Map;

public class EnvironmentRest<ActionType, ObservationType> {
    private List<Integer> a_shape;
    private String a_type;
    private List<ActionType> a_min;
    private List<ActionType> a_max;
    private List<Integer> o_shape;
    private String o_type;
    private List<ObservationType> o_min;
    private List<ObservationType> o_max;
    private List<ObservationType> init_state;
    private String agent_type;
    private Map<String, String> parameters;

    public List<Integer> getA_shape() {
        return a_shape;
    }

    public void setA_shape(List<Integer> a_shape) {
        this.a_shape = a_shape;
    }

    public String getA_type() {
        return a_type;
    }

    public void setA_type(String a_type) {
        this.a_type = a_type;
    }

    public List<ActionType> getA_min() {
        return a_min;
    }

    public void setA_min(List<ActionType> a_min) {
        this.a_min = a_min;
    }

    public List<ActionType> getA_max() {
        return a_max;
    }

    public void setA_max(List<ActionType> a_max) {
        this.a_max = a_max;
    }

    public List<Integer> getO_shape() {
        return o_shape;
    }

    public void setO_shape(List<Integer> o_shape) {
        this.o_shape = o_shape;
    }

    public String getO_type() {
        return o_type;
    }

    public void setO_type(String o_type) {
        this.o_type = o_type;
    }

    public List<ObservationType> getO_min() {
        return o_min;
    }

    public void setO_min(List<ObservationType> o_min) {
        this.o_min = o_min;
    }

    public List<ObservationType> getO_max() {
        return o_max;
    }

    public void setO_max(List<ObservationType> o_max) {
        this.o_max = o_max;
    }

    public List<ObservationType> getInit_state() {
        return init_state;
    }

    public void setInit_state(List<ObservationType> init_state) {
        this.init_state = init_state;
    }

	public String getAgent_type() {
		return agent_type;
	}

	public void setAgent_type(String agent_type) {
		this.agent_type = agent_type;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
}
