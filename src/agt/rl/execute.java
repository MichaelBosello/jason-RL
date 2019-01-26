package rl;

import java.util.Map;
import java.util.Set;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.IntendedMeans;
import jason.asSemantics.Intention;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.PlanBody;
import jason.asSyntax.PlanBody.BodyType;
import jason.asSyntax.PlanBodyImpl;
import jason.asSyntax.Term;
import rl.beliefbase.BeliefBaseRL;
import rl.component.Action;
import rl.component.ActionParameter;
import rl.component.Goal;
import rl.component.RelevantPlans;

public class execute extends DefaultInternalAction {

	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, final Unifier un, final Term[] arg) throws Exception {
		BeliefBaseRL rlbb = (BeliefBaseRL) ts.getAg().getBB();
		if(arg.length != 1) {
			return false;
		}
		String goal = Goal.extractGoal(arg[0], un);
		if(goal == null) {
			return false;
		}
		
		Map<Term, Term> parameter = rlbb.getRlParameter();
		Set<Literal> observation = rlbb.getCurrentObservation(goal);
		double reward = rlbb.getCurrentReward(goal);
		boolean isTerminal = rlbb.isCurrentStateTerminal(goal);
		Set<Action> action = RelevantPlans.getActionsForGoalFromKB(ts, un, goal);		
		
		Action rlResult = rlbb.getRLInstance()
				.nextAction(parameter, action, observation, reward, isTerminal);
		String actionString = rlResult.getLiteralString();
		for(ActionParameter actionParameter : rlResult.getParameters()) {
			String parameterName = actionParameter.getName();
			String variable = parameterName.substring(0, 1).toUpperCase();
			variable +=  parameterName.substring(1, parameterName.length());
			actionString = actionString.replace(variable, actionParameter.getValue());
		}

		PlanBody rlPlanBody = new PlanBodyImpl();
		if(rlResult != null) {
			rlPlanBody.add(new PlanBodyImpl(BodyType.achieve, ASSyntax.parseTerm(actionString)));
		}
		if(!isTerminal) {
			String redoIA = "rl.execute(" + goal + ")";
			rlPlanBody.add(new PlanBodyImpl(BodyType.internalAction, ASSyntax.parseTerm(redoIA)));
		}

		Intention currentIntention = ts.getC().getSelectedIntention();
		IntendedMeans currentMeans = currentIntention.pop();
		rlPlanBody.add(currentMeans.getCurrentStep());
		currentMeans.insertAsNextStep(rlPlanBody);
		currentIntention.push(currentMeans);
		return true;
	}
}
