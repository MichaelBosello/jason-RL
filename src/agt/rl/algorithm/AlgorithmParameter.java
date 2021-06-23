package rl.algorithm;

import java.util.Map;
import java.util.Map.Entry;

import jason.asSyntax.Term;

public class AlgorithmParameter {
	
	private static final String ALPHA_TERM = "alpha";
	private static final String GAMMA_TERM = "gamma";
	private static final String EPSILON_TERM = "epsilon";
	private static final String EPSILON_DECAY_TERM = "epsilon_decay";
	private static final String EPSILON_MIN_TERM = "epsilon_min";
	private static final String POLICY_TERM = "policy";
	public static final String ONLY_EXPLOIT_POLICY = "greedy";
	public static final String EGREEDY_POLICY = "egreedy";
	
	private double alpha = 0.5;
	private double gamma = 0.5;
	private double epsilon = 0;
	private double currentEpsilon = 0;
	private double epsilonDecay = 1;
	private double epsilonMin = 0;
	private double initialActionValue = 0;
	private String policy = EGREEDY_POLICY;
	private boolean dynamicEpsilon = true;
	
	private int episode = 1;
	
	public double getAlpha() { return alpha; }

	public double getGamma() { return gamma; }

	public double getEpsilon() { return currentEpsilon; }

	public double getInitialActionValue() { return initialActionValue; }

	public String getPolicy() { return policy; }

	public void updateParameters(Map<Term, Term> parameters) {
		beliefToParameters(parameters);
		updateEpsilon();
	}
	
	public void episodeEnd() {
		episode++;
		if(dynamicEpsilon) {
			currentEpsilon = (double) 1/episode;
		}
	}
	
	private void beliefToParameters(Map<Term, Term> parameters) {
		for(Entry<Term, Term> parameter : parameters.entrySet()) {
			String parameterKey = parameter.getKey().toString();
			String parameterValue = parameter.getValue().toString();
			
			if(parameterKey.equals(POLICY_TERM)) {
				policy = parameterValue;
			} else {
				if(parameterKey.equals(EPSILON_TERM) && parameterValue.equals("1/t")) {
					dynamicEpsilon = true;
				} else {
					double value = 0;
					try {
						 value = Double.parseDouble(parameterValue);
					} catch(Exception e) {}
					if(value >= 0) {
						if(parameterKey.equals(ALPHA_TERM)) {
							alpha = value;
						} else if(parameterKey.equals(GAMMA_TERM)) {
							gamma = value;
						} else if(parameterKey.equals(EPSILON_TERM)) {
							epsilon = value;
							dynamicEpsilon = false;
						} else if(parameterKey.equals(EPSILON_DECAY_TERM)) {
							epsilonDecay = value;
						} else if(parameterKey.equals(EPSILON_MIN_TERM)) {
							epsilonMin = value;
						}
					}
				}
			} 
		}
	}
	
	private void updateEpsilon() {
		if(currentEpsilon == 0) {
			currentEpsilon = epsilon;
		}
		currentEpsilon = currentEpsilon * epsilonDecay;
		if(currentEpsilon < epsilonMin) {
			currentEpsilon = epsilonMin;
		}
	}

}
