package rl.component;

import java.util.HashSet;
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
	
	public ActionParameter(ActionParameter copy) {
		type = copy.type;
		this.name = copy.name;
		this.min = copy.min;
		this.max = copy.max;
		if(copy.set != null) {
			this.set = new HashSet<>(copy.set);
		}
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(max);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(min);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((set == null) ? 0 : set.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		ActionParameter other = (ActionParameter) obj;
		if (Double.doubleToLongBits(max) != Double.doubleToLongBits(other.max))
			return false;
		if (Double.doubleToLongBits(min) != Double.doubleToLongBits(other.min))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (set == null) {
			if (other.set != null)
				return false;
		} else if (!set.equals(other.set))
			return false;
		if (type != other.type)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "ActionParameter [type=" + type + ", name=" + name + ", min=" + min + ", max=" + max + ", set=" + set
				+ ", value=" + value + "]";
	}
	
	
	
}