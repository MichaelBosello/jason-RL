package rl.component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jason.NoValueException;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;

public class Observation implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static final String PARAM_SET_FUNCTOR = "set";
	public static final String PARAM_REAL_FUNCTOR = "real";
	public static final String PARAM_INT_FUNCTOR = "int";

	public enum ParameterType {
	    SET, REAL, INT
	}
	
	private final String name;
	private final List<ObservationParameter> parameters;
	
	public Observation(Term observationTerm){
		parameters = new ArrayList<>();
		
		Literal observation = (Literal) observationTerm;
		name = observation.getFunctor();
		for(Term param : observation.getTerms()) {
			Literal paramTypeLit = (Literal) param;
			String paramType = paramTypeLit.getFunctor();
			if(paramType.equals(PARAM_SET_FUNCTOR)) {
				List<String> paramSet = new ArrayList<>();
				for(Term paramElement : paramTypeLit.getTerms()) {
					paramSet.add(paramElement.toString());
				}
				parameters.add(new ObservationParameter(paramSet));
			} else if(paramType.equals(PARAM_INT_FUNCTOR)) {
				try {
					int min = (int) ((NumberTerm)paramTypeLit.getTerm(0)).solve();
					int max = (int) ((NumberTerm)paramTypeLit.getTerm(1)).solve();
					parameters.add(new ObservationParameter(min, max));
				} catch (NoValueException e) {
					e.printStackTrace();
				}
			} else if(paramType.equals(PARAM_REAL_FUNCTOR)) {
				try {
					double min = ((NumberTerm)paramTypeLit.getTerm(0)).solve();
					double max = ((NumberTerm)paramTypeLit.getTerm(1)).solve();
					parameters.add(new ObservationParameter(min, max));
				} catch (NoValueException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public Observation(String name, List<ObservationParameter> parameters){
		this.name = name;
		this.parameters = parameters;
	}
	
	public Observation(String name){
		this.name = name;
		this.parameters = null;
	}
	
	public Observation(Observation copy){
		this.name = copy.name;
		this.parameters = new ArrayList<>();
		for(ObservationParameter param : copy.parameters) {
			parameters.add(new ObservationParameter(param));
		}
	}
	
	public String getName() { return name; }
	public List<ObservationParameter> getParameters() { return parameters; }
	
	public void setParamValues(Term observationTerm) {
		Literal observation = (Literal) observationTerm;
		int index = 0;
		for(Term param : observation.getTerms()) {
			parameters.get(index).setValue(param.toString());
			index++;
		}
	}
	
	public void clearValues() {
		for(ObservationParameter param : parameters) {
			param.setValue("");
		}
	}

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
		Observation other = (Observation) obj;
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
}
