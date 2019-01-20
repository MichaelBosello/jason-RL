package rl.beliefbase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jason.asSyntax.Literal;
import jason.bb.DefaultBeliefBase;

public class BeliefBaseRL extends DefaultBeliefBase {
	
	public static final String PARAMETER_FUNCTOR = "rl_parameter";
	public static final String OBSERVE_FUNCTOR = "rl_observe";
	
	Map<String, String> parameter = new HashMap<>();
	
	Map<String, List<String>> observe = new HashMap<>();//observation, goal list
	Map<String, List<String>> currentObservation = new HashMap<>();//goal, observation list
	
	@Override 
	public boolean add(Literal bel) {
		if(bel.getFunctor().equals(PARAMETER_FUNCTOR)) {
			
		} else if (bel.getFunctor().equals(OBSERVE_FUNCTOR)) {
			
		} else if(observe.containsKey(bel.getFunctor())) {
			
		}
		
		return super.add(bel);
	}
}
