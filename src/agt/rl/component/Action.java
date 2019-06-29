package rl.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Action implements Serializable{

	private static final long serialVersionUID = 1L;
	private static double realStepForDiscretization = 100;

	public enum ParameterType {
	    SET, REAL, INT
	}
	
	private final String name;
	private final List<ActionParameter> parameters;
	
	public Action(String name, List<ActionParameter> parameters){
		this.name = name;
		this.parameters = parameters;
	}
	
	public Action(String name){
		this.name = name;
		this.parameters = null;
	}
	
	public Action(Action copy){
		this.name = copy.name;
		this.parameters = new ArrayList<>();
		for(ActionParameter param : copy.parameters) {
			parameters.add(new ActionParameter(param));
		}
	}
	
	public String getLiteralString() { return name; }
	public List<ActionParameter> getParameters() { return parameters; }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Action other = (Action) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Action [name=" + name + ", parameters=" + parameters + "]";
	}
	
	public static List<Action> discretizeAction(Set<Action> parametrizedAction) {
		List<Action> discreteActions = new ArrayList<>();

		for (Action action : parametrizedAction) {
			Set<Action> discreteSet = new HashSet<>();
			discreteSet.add(action);
			for (ActionParameter param : action.getParameters()) {
				Set<Action> tmpSet = new HashSet<>();
				if (param.getType().equals(ParameterType.SET)) {
					for (String value : param.getSet()) {
						for (Action next : discreteSet) {
							Action nextDiscrete = new Action(next);
							for (ActionParameter paramToUpdate : nextDiscrete.getParameters()) {
								if (paramToUpdate.equals(param)) {
									paramToUpdate.setValue(value);
								}
							}
							tmpSet.add(nextDiscrete);
						}
					}
				} else if (param.getType().equals(ParameterType.INT)) {
					for (int value = (int) param.getMin(); value < param.getMax(); value++) {
						for (Action next : discreteSet) {
							Action nextDiscrete = new Action(next);
							for (ActionParameter paramToUpdate : nextDiscrete.getParameters()) {
								if (paramToUpdate.equals(param)) {
									paramToUpdate.setValue(String.valueOf(value));
								}
							}
							tmpSet.add(nextDiscrete);
						}
					}
				} else if (param.getType().equals(ParameterType.REAL)) {
					double step = (param.getMax() - param.getMin()) / realStepForDiscretization;
					for (double value = param.getMin(); value < param.getMax(); value += step) {
						for (Action next : discreteSet) {
							Action nextDiscrete = new Action(next);
							for (ActionParameter paramToUpdate : nextDiscrete.getParameters()) {
								if (paramToUpdate.equals(param)) {
									paramToUpdate.setValue(String.valueOf(value));
								}
							}
							tmpSet.add(nextDiscrete);
						}
					}
				}

				discreteSet = tmpSet;
			}
			discreteActions.addAll(discreteSet);
		}

		return discreteActions;
	}
}
