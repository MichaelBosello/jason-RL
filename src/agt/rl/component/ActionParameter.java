package rl.component;

import java.util.Set;

import rl.component.Action.ParameterType;

public class ActionParameter {
	ParameterType type;
	String name;
	double min, max;
	Set<String> set;
	Object value;
	
	public ActionParameter(String name, int min, int max){
		type = ParameterType.INT;
		this.min = min;
		this.max = max;
	}
	public ActionParameter(String name, double min, double max){
		type = ParameterType.REAL;
		this.min = min;
		this.max = max;
	}
	public ActionParameter(String name, Set<String> set){
		type = ParameterType.SET;
		this.set = set;
	}
	
	public double getMin() { return min; }
	public double getMax() { return max; }
	public Set<String> getSet() { return set; }
	public ParameterType getType() { return type; }
	
	public Object getValue() { return value; }
	public void setValue(Object value) {
		this.value = value;
	}
	
}