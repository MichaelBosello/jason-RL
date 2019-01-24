package rl.component;

import java.util.Set;

import rl.component.Action.ParameterType;

public class ActionParameter {
	ParameterType type;
	String name;
	double min, max;
	Set<String> set;
	String value;
	
	public ActionParameter(String name, int min, int max){
		type = ParameterType.INT;
		this.name = name;
		this.min = min;
		this.max = max;
	}
	public ActionParameter(String name, double min, double max){
		type = ParameterType.REAL;
		this.name = name;
		this.min = min;
		this.max = max;
	}
	public ActionParameter(String name, Set<String> set){
		type = ParameterType.SET;
		this.name = name;
		this.set = set;
	}
	
	public String getName() { return name; }
	public double getMin() { return min; }
	public double getMax() { return max; }
	public Set<String> getSet() { return set; }
	public ParameterType getType() { return type; }
	
	public String getValue() { return value; }
	public void setValue(String value) {
		this.value = value;
	}
	
}