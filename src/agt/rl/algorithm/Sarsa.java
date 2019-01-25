package rl.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import rl.component.Action;
import rl.component.Action.ParameterType;
import rl.component.ActionParameter;

public class Sarsa implements AlgorithmRL{
	
	private double realStepForDiscretization = 100;
	private Random randomEGreedy = new Random();
	
	private double alpha = 0.8;
	private double gamma = 0.1;
	private double epsilon = 0.3;
	private double initialActionValue = 0.8;
	
	private Map<String, Map<Action, Double>> q = new HashMap<>();
	
	private String previousState = null;
	private Action previousAction = null;

	@Override
	public Action nextAction(
			Map<Term, Term> parameter,
			Set<Action> action,
			Set<Literal> observation,
			double reward,
			boolean isTerminal) {
		
		String state = observationToState(observation);
		List<Action> actions = discretizeAction(action);
		addNewActionToQ(state, actions);
		
		/*
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		System.out.println("reward " + reward);
		System.out.println(state);
		System.out.println(actions.toString());
		System.out.println(q.toString());
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		*/
		
		Action selectedAction = selectAction(state, actions);
		
		if(previousState != null && previousAction != null) {
			double qSA = q.get(previousState).get(previousAction);
			double qS1A1 = q.get(state).get(selectedAction);
			double q1 = qSA + alpha * (reward + gamma * (qS1A1 - qSA));
			q.get(previousState).put(previousAction, q1);
		}
		
		previousState = state;
		previousAction = selectedAction;
		return selectedAction;
	}
	
	private Action selectAction(String state, List<Action> actions) {
		if(randomEGreedy.nextDouble() < epsilon) {
			int randomSelected = randomEGreedy.nextInt(actions.size());
			return actions.get(randomSelected);
		}
		Action selectedAction = null;
		double actionValue = Double.MIN_VALUE;
		for(Entry<Action, Double> action : q.get(state).entrySet()) {
			if(action.getValue() > actionValue) {
				selectedAction = action.getKey();
				actionValue = action.getValue();
			}
		}
		return selectedAction;
	}
	
	private void addNewActionToQ(String state, List<Action> actions) {
		Map<Action, Double> actionValue = new HashMap<>();
		if(q.containsKey(state)) {
			actionValue = q.get(state);
		}
		for(Action action : actions) {
			if(!actionValue.containsKey(action)) {
				actionValue.put(action, initialActionValue);
			}
		}
		q.put(state, actionValue);
	}
	
	private String observationToState(Set<Literal> observations) {
		String state = "";
		for(Literal observation : observations) {
			state += observation.toString();
		}
		return state;
	}
	
	private List<Action> discretizeAction(Set<Action> parametrizedAction) {
		List<Action> discreteActions = new ArrayList<>();
		
		for(Action action : parametrizedAction) {
			Set<Action> discreteSet = new HashSet<>();
			discreteSet.add(action);
			for(ActionParameter param : action.getParameters()) {
				Set<Action> tmpSet = new HashSet<>();
				if(param.getType().equals(ParameterType.SET)) {
					for(String value : param.getSet()) {
						for(Action next : discreteSet) {
							Action nextDiscrete = new Action(next);
							for(ActionParameter paramToUpdate : nextDiscrete.getParameters()) {
								if(paramToUpdate.equals(param)) {
									paramToUpdate.setValue(value);
								}
							}
							tmpSet.add(nextDiscrete);
						}
					}
				} else if(param.getType().equals(ParameterType.INT)) {
					for(int value = (int) param.getMin(); value < param.getMax(); value++) {
						for(Action next : discreteSet) {
							Action nextDiscrete = new Action(next);
							for(ActionParameter paramToUpdate : nextDiscrete.getParameters()) {
								if(paramToUpdate.equals(param)) {
									paramToUpdate.setValue(String.valueOf(value));
								}
							}
							tmpSet.add(nextDiscrete);
						}
					}
				} else if(param.getType().equals(ParameterType.REAL)) {
					double step = (param.getMax() - param.getMin()) / realStepForDiscretization;
					for(double value = param.getMin(); value < param.getMax(); value+= step) {
						for(Action next : discreteSet) {
							Action nextDiscrete = new Action(next);
							for(ActionParameter paramToUpdate : nextDiscrete.getParameters()) {
								if(paramToUpdate.equals(param)) {
									paramToUpdate.setValue(String.valueOf(value));
								}
							}
							tmpSet.add(nextDiscrete);
						}
					}
				}
				
				discreteSet = tmpSet;
			}
			discreteActions.addAll(discreteSet);
		}
		
		return discreteActions;
	}

}
