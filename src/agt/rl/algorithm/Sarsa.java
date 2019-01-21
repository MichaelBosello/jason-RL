package rl.algorithm;

import java.util.Map;
import java.util.Set;

import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import rl.component.Action;

public class Sarsa implements AlgorithmRL{

	@Override
	public String nextAction(
			Map<Term, Term> parameter,
			Set<Action> action,
			Set<Literal> observation,
			double reward,
			boolean isTerminal) {
		
		String selectedActionString = null;
		for(Action a : action) {
			selectedActionString = a.getName();
		}
		
		return selectedActionString;
	}

}
