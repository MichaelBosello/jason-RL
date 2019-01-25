package rl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.IntendedMeans;
import jason.asSemantics.Intention;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Plan;
import jason.asSyntax.PlanBody;
import jason.asSyntax.PlanBody.BodyType;
import jason.asSyntax.PlanBodyImpl;
import jason.asSyntax.Term;
import rl.algorithm.AlgorithmRL;
import rl.algorithm.Sarsa;
import rl.beliefbase.BeliefBaseRL;
import rl.component.Action;
import rl.component.ActionParameter;

public class execute extends DefaultInternalAction {

	public static final String GOAL_FUNCTOR = "rl_goal";
	public static final String ACTION_PARAM_FUNCTOR = "rl_param";
	public static final String PARAM_SET_FUNCTOR = "set";
	public static final String PARAM_REAL_FUNCTOR = "real";
	public static final String PARAM_INT_FUNCTOR = "int";

	private static final long serialVersionUID = 1L;

	@Override
	public Object execute(TransitionSystem ts, final Unifier un, final Term[] arg) throws Exception {
		BeliefBaseRL rlbb = (BeliefBaseRL) ts.getAg().getBB();
		if(arg.length != 1) {
			return false;
		}
		String goal = null;
		if(arg[0].isGround()) {
			goal = arg[0].toString();
		} else if (arg[0].isVar()) {
			goal = un.get(arg[0].toString()).toString();
		}
		if(goal == null) {
			return false;
		}
		
		Map<Term, Term> parameter = rlbb.getRlParameter();
		Set<Literal> observation = rlbb.getCurrentObservation(goal);
		double reward = rlbb.getCurrentReward(goal);
		boolean isTerminal = rlbb.isCurrentStateTerminal(goal);
		Set<Action> action = new HashSet<>();



		List<Plan> plans = ts.getAg().getPL().getPlans();
		for(Plan plan : plans) {
			if(plan.getLabel().getAnnot(GOAL_FUNCTOR) != null) {
				for(Term annotationGoal : plan.getLabel().getAnnot(GOAL_FUNCTOR).getTerms()) {
					if(annotationGoal.toString().equals(goal))
					if(plan.getContext() == null ||
					   plan.getContext().logicalConsequence(ts.getAg(), un).hasNext()) {
						String planName = plan.getTrigger().getLiteral().toString();
						Set<ActionParameter> parameters = new HashSet<>();
						if(plan.getLabel().getAnnot(ACTION_PARAM_FUNCTOR) != null)
						for(Term ap : plan.getLabel().getAnnot(ACTION_PARAM_FUNCTOR).getTerms()) {
							Literal actionParameter = (Literal) ap;
							String paramName = actionParameter.getFunctor();
							Literal paramTypeLit = (Literal) actionParameter.getTerm(0);
							String paramType = paramTypeLit.getFunctor();
							if(paramType.equals(PARAM_SET_FUNCTOR)) {
								Set<String> paramSet = new HashSet<>();
								for(Term paramElement : paramTypeLit.getTerms()) {
									paramSet.add(paramElement.toString());
								}
								parameters.add(new ActionParameter(paramName, paramSet));
							} else if(paramType.equals(PARAM_INT_FUNCTOR)) {
								int min = (int) ((NumberTerm)paramTypeLit.getTerm(0)).solve();
								int max = (int) ((NumberTerm)paramTypeLit.getTerm(1)).solve();
								parameters.add(new ActionParameter(paramName, min, max));
							} else if(paramType.equals(PARAM_REAL_FUNCTOR)) {
								double min = ((NumberTerm)paramTypeLit.getTerm(0)).solve();
								double max = ((NumberTerm)paramTypeLit.getTerm(1)).solve();
								parameters.add(new ActionParameter(paramName, min, max));
							}
						}
						action.add(new Action(planName, parameters));
					}
				}
			}
		}

		
		
		
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
