package rl.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import rl.component.Observation.ParameterType;

public class ObservationParameter implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private ParameterType type;
	private double min, max;
	private List<String> set;
	private String value;
	
	public ObservationParameter(int min, int max){
		type = ParameterType.INT;
		this.min = min;
		this.max = max;
	}
	public ObservationParameter(double min, double max){
		type = ParameterType.REAL;
		this.min = min;
		this.max = max;
	}
	public ObservationParameter(List<String> set){
		type = ParameterType.SET;
		this.set = set;
	}
	
	public ObservationParameter(ObservationParameter copy) {
		type = copy.type;
		this.min = copy.min;
		this.max = copy.max;
		if(copy.set != null) {
			this.set = new ArrayList<>(copy.set);
		}
	}
	
	public double getMin() { return min; }
	public double getMax() { return max; }
	public List<String> getSet() { return set; }
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
		ObservationParameter other = (ObservationParameter) obj;
		if (Double.doubleToLongBits(max) != Double.doubleToLongBits(other.max))
			return false;
		if (Double.doubleToLongBits(min) != Double.doubleToLongBits(other.min))
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
		return "ActionParameter [type=" + type + ", min=" + min + ", max=" + max + ", set=" + set
				+ ", value=" + value + "]";
	}
}