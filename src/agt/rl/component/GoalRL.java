package rl.component;

import jason.asSemantics.Unifier;
import jason.asSyntax.Term;

public class GoalRL {
	public static String extractGoal(Term term, Unifier unifier) {
		String goal = null;
		if(term.isGround()) {
			goal = term.toString();
		} else if (term.isVar()) {
			goal = unifier.get(term.toString()).toString();
		}
		return goal;
	}
}
