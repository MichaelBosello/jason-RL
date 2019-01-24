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
		
		Action selectedAction = null;
		for(Action a : action) {
			selectedAction = a;
			for(ActionParameter param : a.getParameters()) {
				if(param.getType().equals(ParameterType.SET)) {
					param.setValue(param.getSet().toArray()[0].toString());
				} else if(param.getType().equals(ParameterType.INT)) {
					param.setValue(String.valueOf(param.getMin()));
				} else if(param.getType().equals(ParameterType.REAL)) {
					param.setValue(String.valueOf(param.getMin()));
				}
			}
		}
		
		return selectedAction;
	}

}
