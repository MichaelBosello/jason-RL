package rl;

import java.util.Set;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;
import rl.algorithm.AlgorithmRL;
import rl.beliefbase.BeliefBaseRL;
import rl.component.Action;
import rl.component.GoalRL;
import rl.component.PlanLibraryRL;

public class expected_return extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem transitionSystem, final Unifier unifier, final Term[] arguments) throws Exception {
		BeliefBaseRL rlBB = (BeliefBaseRL) transitionSystem.getAg().getBB();
		if(arguments.length != 2 || !arguments[1].isVar()) {
			return false;
		}
		String goal = GoalRL.extractGoal(arguments[0], unifier);
		if(goal == null) {
			return false;
		}
		Set<Literal> observation = rlBB.getCurrentObservation(goal);
		Set<Action> action = PlanLibraryRL.getActionsForGoal(transitionSystem, unifier, goal);
		AlgorithmRL rl = rlBB.getRLInstance(goal);
		
		double expectedReturn = rl.expectedReturn(action, observation);
		NumberTerm result = new NumberTermImpl(expectedReturn);
		return unifier.unifies(result, arguments[1]);
	}
}
