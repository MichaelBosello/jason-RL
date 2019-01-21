package rl.component;

import java.util.Set;

public class Action {
	
	public enum ParameterType {
	    SET, REAL, INT
	}
	
	private final String name;
	private final Set<ActionParameter> parameters;
	
	public Action(String name, Set<ActionParameter> parameters){
		this.name = name;
		this.parameters = parameters;
	}
	
	public Action(String name){
		this.name = name;
		this.parameters = null;
	}
	
	public String getName() { return name; }
	public Set<ActionParameter> getParameters() { return parameters; }

}
