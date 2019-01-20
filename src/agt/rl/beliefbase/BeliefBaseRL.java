package rl.beliefbase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jason.asSyntax.ListTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import jason.bb.DefaultBeliefBase;

public class BeliefBaseRL extends DefaultBeliefBase {
	
	public static final String PARAMETER_FUNCTOR = "rl_parameter";
	public static final String OBSERVE_FUNCTOR = "rl_observe";
	
	Map<Term, Term> parameter = new HashMap<>();
	
	Map<String, Set<String>> observationGoal = new HashMap<>();//observation, goal list
	Map<String, Set<String>> goalObservation = new HashMap<>();//goal, observation list
	Map<String, Set<Literal>> currentObservation = new HashMap<>();//goal, observation list
	
	Set<String> trackAll = new HashSet<>();
	
	@Override 
	public boolean add(Literal bel) {
		String functor = bel.getFunctor();
		if(functor.equals(PARAMETER_FUNCTOR)) {
			if(bel.getArity() == 2) {
				parameter.put(bel.getTerm(0), bel.getTerm(1));
			}
		} else if (functor.equals(OBSERVE_FUNCTOR)) {
			if(bel.getArity() == 2) {
				Term goal = bel.getTerm(0);
				Term observe = bel.getTerm(1);
				
				if(observe.isVar()) {
					trackAll.add(goal.toString());
				} else if(observe.isList()) {
					((ListTerm) observe).forEach( o -> {
						putMapSet(observationGoal, o.toString(), goal.toString());
						putMapSet(goalObservation, goal.toString(), o.toString());
					});
				} else if(observe.isGround()) {
					putMapSet(observationGoal, observe.toString(), goal.toString());
					putMapSet(goalObservation, goal.toString(), observe.toString());
				}
			}
		} else {
			trackAll.forEach( goal -> {
				putMapSet(currentObservation, goal, bel);
			});
			if(observationGoal.containsKey(functor)) {
				observationGoal.get(functor).forEach( goal -> {
					putMapSet(currentObservation, goal, bel);
				});
			}
		} 
		
		return super.add(bel);
	}
	
	private <Key, Value> void putMapSet(Map<Key, Set<Value>> map, Key key, Value value) {
		if(!map.containsKey(key)) {
			map.put(key, new HashSet<>());
		}
		map.get(key).add(value);
	}
}
