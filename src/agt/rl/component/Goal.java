package rl.component;

import jason.asSemantics.Unifier;
import jason.asSyntax.Term;

public class Goal {
	public static String extractGoal(Term term, Unifier un ) {
		String goal = null;
		if(term.isGround()) {
			goal = term.toString();
		} else if (term.isVar()) {
			goal = un.get(term.toString()).toString();
		}
		return goal;
	}
}
