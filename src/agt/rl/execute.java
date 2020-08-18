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
import rl.component.GoalRL;
import rl.component.PlanLibraryRL;

public class execute extends DefaultInternalAction {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("all")
	@Override
	public Object execute(TransitionSystem transitionSystem, final Unifier unifier, final Term[] argument) throws Exception {
		BeliefBaseRL rlBB = (BeliefBaseRL) transitionSystem.getAg().getBB();
		if(argument.length != 1) {
			return false;
		}
		String goal = GoalRL.extractGoal(argument[0], unifier);
		if(goal == null) {
			return false;
		}
		
		Map<Term, Term> parameter = rlBB.getRlParameter();
		Set<Literal> observation = rlBB.getCurrentObservation(goal);
		double reward = rlBB.getCurrentReward(goal);
		boolean isTerminal = rlBB.isCurrentStateTerminal(goal);
		Set<Action> action = PlanLibraryRL.getActionsForGoal(transitionSystem, unifier, goal);		
		
		Action rlResult = rlBB.getRLInstance(goal)
				.nextAction(parameter, action, observation, reward, isTerminal);
		String actionString = rlResult.getLiteralString();
		
		for(ActionParameter actionParameter : rlResult.getParameters()) {
			//find the parameter name (a variable need the first letter uppercase, not allowed in label) e.g. direction -> Direction
			String parameterName = actionParameter.getName();
			String variable = parameterName.substring(0, 1).toUpperCase();
			variable +=  parameterName.substring(1, parameterName.length());
			//replace the variable with its value
			actionString = actionString.replace(variable, actionParameter.getValue());
		}

		//the new plan based on rl: action + rl.execute 
		PlanBody rlPlanBody = new PlanBodyImpl();
		if(rlResult != null) {
			rlPlanBody.add(new PlanBodyImpl(BodyType.achieve, ASSyntax.parseTerm(actionString)));
		}
		if(!isTerminal) {
			String redoRL = "rl.execute(" + goal + ")";
			rlPlanBody.add(new PlanBodyImpl(BodyType.internalAction, ASSyntax.parseTerm(redoRL)));
		}

		//add the plan on top of current intention
		Intention currentIntention = transitionSystem.getC().getSelectedIntention();
		IntendedMeans currentMeans = currentIntention.pop();
		PlanBody currentPlan = currentMeans.getCurrentStep().clonePB();
        if(currentPlan.getPlanSize() > 1) {
            currentPlan.removeBody(0);
            rlPlanBody.add(currentPlan);
        }
		currentMeans.insertAsNextStep(rlPlanBody);
		currentIntention.push(currentMeans);
		
		return true;
	}
}
