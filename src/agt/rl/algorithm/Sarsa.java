package rl.algorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import jason.asSemantics.Agent;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import rl.beliefbase.BeliefBaseRL;
import rl.component.Action;

public class Sarsa implements AlgorithmRL {

	BehaviourSerializer serializer = new BehaviourSerializer();
	AlgorithmParameter parameters = new AlgorithmParameter();

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
			List<Action> actions = Action.discretizeAction(action);
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
		List<Action> actions = Action.discretizeAction(action);
		addNewActionToQ(state, actions);

		Action selectedAction = selectAction(state, actions);

		if (!parameters.getPolicy().equals(AlgorithmParameter.ONLY_EXPLOIT_POLICY) &&
					previousState != null && previousAction != null) {
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

	@Override
	public void initialize(Agent agent, BeliefBaseRL bb) {}

}
