package rl.component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jason.NoValueException;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Plan;
import jason.asSyntax.Term;

public class RelevantPlans {
	
	public static final String GOAL_FUNCTOR = "rl_goal";
	public static final String ACTION_PARAM_FUNCTOR = "rl_param";
	public static final String PARAM_SET_FUNCTOR = "set";
	public static final String PARAM_REAL_FUNCTOR = "real";
	public static final String PARAM_INT_FUNCTOR = "int";
	
	public static Set<Action> getActionsForGoalFromKB(
			TransitionSystem ts, Unifier un, String goal) throws NoValueException{
		List<Plan> plans = ts.getAg().getPL().getPlans();
		Set<Action> action = new HashSet<>();
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
		return action;
	}
}
