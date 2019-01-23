package rl.algorithm;

import java.util.Map;
import java.util.Set;

import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import rl.component.Action;
import rl.component.Action.ParameterType;
import rl.component.ActionParameter;

public class Sarsa implements AlgorithmRL{

	@Override
	public Action nextAction(
			Map<Term, Term> parameter,
			Set<Action> action,
			Set<Literal> observation,
			double reward,
			boolean isTerminal) {
		
		Action selectedActionString = null;
		for(Action a : action) {
			selectedActionString = a;
			for(ActionParameter param : a.getParameters()) {
				if(param.getType().equals(ParameterType.SET)) {
					
				} else if(param.getType().equals(ParameterType.INT)) {
					
				} else if(param.getType().equals(ParameterType.REAL)) {
					
				}
			}
		}
		
		return selectedActionString;
	}

}
