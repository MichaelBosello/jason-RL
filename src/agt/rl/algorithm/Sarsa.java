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

public class Sarsa implements AlgorithmRL {

	BehaviourSerializer serializer = new BehaviourSerializer();
	AlgorithmParameter parameters = new AlgorithmParameter();

	private double realStepForDiscretization = 100;
	private Random randomEGreedy = new Random();

	private Map<String, Map<Action, Double>> q = new HashMap<>();

	private String previousState = null;
	private Action previousAction = null;

	@SuppressWarnings("unchecked")
	public Sarsa() {
		if (serializer.getBehaviour() != null) {
			q = (Map<String, Map<Action, Double>>) serializer.getBehaviour();
		}
	}

	@Override
	public double expectedReturn(Set<Action> action, Set<Literal> observation) {
		String state = observationToState(observation);
		Map<Action, Double> valueFunctionState = q.get(state);
		if (valueFunctionState != null) {
			List<Action> actions = discretizeAction(action);
			Action selectedAction = selectAction(state, actions);
			if (valueFunctionState.containsKey(selectedAction)) {
				double expecterReward = valueFunctionState.get(selectedAction);
				return expecterReward;
			}
		}
		return parameters.getInitialActionValue();
	}

	@Override
	public Action nextAction(Map<Term, Term> parameter, Set<Action> action, Set<Literal> observation, double reward,
			boolean isTerminal) {

		parameters.updateParameters(parameter);

		String state = observationToState(observation);
		List<Action> actions = discretizeAction(action);
		addNewActionToQ(state, actions);

		Action selectedAction = selectAction(state, actions);

		if (previousState != null && previousAction != null) {
			double qSA = q.get(previousState).get(previousAction);
			double qS1A1 = q.get(state).get(selectedAction);
			double q1 = qSA + parameters.getAlpha() * (reward + parameters.getGamma() * (qS1A1 - qSA));
			q.get(previousState).put(previousAction, q1);
		}

		previousState = state;
		previousAction = selectedAction;

		if (isTerminal) {
			previousState = null;
			previousAction = null;

			parameters.episodeEnd();
			serializer.episodeEnd(q);
		}

		return selectedAction;
	}

	private Action selectAction(String state, List<Action> actions) {
		if (parameters.getPolicy().equals(AlgorithmParameter.EGREEDY_POLICY)
				&& randomEGreedy.nextDouble() < parameters.getEpsilon()) {
			int randomSelected = randomEGreedy.nextInt(actions.size());
			return actions.get(randomSelected);
		}
		Action selectedAction = null;
		double actionValue = -Double.MAX_VALUE;
		for (Entry<Action, Double> action : q.get(state).entrySet()) {
			if (action.getValue() > actionValue) {
				selectedAction = action.getKey();
				actionValue = action.getValue();
			}
		}
		return selectedAction;
	}

	private void addNewActionToQ(String state, List<Action> actions) {
		Map<Action, Double> actionValue = new HashMap<>();
		if (q.containsKey(state)) {
			actionValue = q.get(state);
		}
		for (Action action : actions) {
			if (!actionValue.containsKey(action)) {
				actionValue.put(action, parameters.getInitialActionValue());
			}
		}
		q.put(state, actionValue);
	}

	private String observationToState(Set<Literal> observations) {
		String state = "";
		for (Literal observation : observations) {
			state += observation.toString();
		}
		return state;
	}

	private List<Action> discretizeAction(Set<Action> parametrizedAction) {
		List<Action> discreteActions = new ArrayList<>();

		for (Action action : parametrizedAction) {
			Set<Action> discreteSet = new HashSet<>();
			discreteSet.add(action);
			for (ActionParameter param : action.getParameters()) {
				Set<Action> tmpSet = new HashSet<>();
				if (param.getType().equals(ParameterType.SET)) {
					for (String value : param.getSet()) {
						for (Action next : discreteSet) {
							Action nextDiscrete = new Action(next);
							for (ActionParameter paramToUpdate : nextDiscrete.getParameters()) {
								if (paramToUpdate.equals(param)) {
									paramToUpdate.setValue(value);
								}
							}
							tmpSet.add(nextDiscrete);
						}
					}
				} else if (param.getType().equals(ParameterType.INT)) {
					for (int value = (int) param.getMin(); value < param.getMax(); value++) {
						for (Action next : discreteSet) {
							Action nextDiscrete = new Action(next);
							for (ActionParameter paramToUpdate : nextDiscrete.getParameters()) {
								if (paramToUpdate.equals(param)) {
									paramToUpdate.setValue(String.valueOf(value));
								}
							}
							tmpSet.add(nextDiscrete);
						}
					}
				} else if (param.getType().equals(ParameterType.REAL)) {
					double step = (param.getMax() - param.getMin()) / realStepForDiscretization;
					for (double value = param.getMin(); value < param.getMax(); value += step) {
						for (Action next : discreteSet) {
							Action nextDiscrete = new Action(next);
							for (ActionParameter paramToUpdate : nextDiscrete.getParameters()) {
								if (paramToUpdate.equals(param)) {
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
