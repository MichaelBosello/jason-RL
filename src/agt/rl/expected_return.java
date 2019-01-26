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
import rl.component.Goal;
import rl.component.RelevantPlans;

public class expected_return extends DefaultInternalAction {
	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, final Unifier un, final Term[] arg) throws Exception {
		BeliefBaseRL rlbb = (BeliefBaseRL) ts.getAg().getBB();
		if(arg.length != 2 || !arg[1].isVar()) {
			return false;
		}
		String goal = Goal.extractGoal(arg[0], un);
		if(goal == null) {
			return false;
		}
		Set<Literal> observation = rlbb.getCurrentObservation(goal);
		Set<Action> action = RelevantPlans.getActionsForGoalFromKB(ts, un, goal);
		AlgorithmRL rl = rlbb.getRLInstance();
		
		double expectedReturn = rl.expectedReturn(action, observation);
		NumberTerm result = new NumberTermImpl(expectedReturn);
		return un.unifies(result, arg[1]);
	}
}
