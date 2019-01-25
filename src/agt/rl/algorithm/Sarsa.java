package rl.algorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	
	private static final String ALPHA_TERM = "alpha";
	private static final String GAMMA_TERM = "gamma";
	private static final String EPSILON_TERM = "epsilon";
	private static final String POLICY_TERM = "policy";
	private static final String ONLY_EXPLOIT_POLICY = "exploit_only";
	private static final String EGREEDY_POLICY = "egreedy";
	
	private String value_function_directory = "valuefunction";
	private String value_function_filename = "/learnedvf";
	private String value_function_extension = ".sar";
	private String value_function_file = value_function_directory + value_function_filename + value_function_extension;
	
	private double realStepForDiscretization = 100;
	private Random randomEGreedy = new Random();
	private ObjectOutputStream outObject;
	private FileOutputStream outFile;
	private int episode = 1;
	private int episodeForSaving = 1;
	private int writeEveryNEpisode = 30;
	private boolean saveProgress = true;
	private boolean loadProgress = true;
	
	private double alpha = 0.5;
	private double gamma = 0.5;
	private double epsilon = 0.1;
	private double initialActionValue = 0.5;
	private String policy = EGREEDY_POLICY;
	private boolean dynamicEpsilon = true;
	
	private Map<String, Map<Action, Double>> q = new HashMap<>();
	
	private String previousState = null;
	private Action previousAction = null;
	
	@SuppressWarnings("unchecked")
	public Sarsa() {
		
		File directory = new File(value_function_directory);
	    if (saveProgress && !directory.exists()){
	        directory.mkdir();
	    }
	    
	    File file = new File(value_function_file);
	    if (file.isFile() && file.canRead()) {
	    	if(loadProgress) {
				try {
					FileInputStream fileIn = new FileInputStream(value_function_file);
					ObjectInputStream in = new ObjectInputStream(fileIn);
					q = (Map<String, Map<Action, Double>>) in.readObject();
					in.close();
					fileIn.close();
				} catch (IOException i) {
					System.out.println("Can't read value function file, start with new q");
				} catch (ClassNotFoundException c) {
					c.printStackTrace();
				}
			}
	    } else {
	    	if(saveProgress) {
		    	try {
					file.createNewFile();
				} catch (IOException e) { e.printStackTrace(); }
	    	}
	    }
		
		if(q == null) {
			q = new HashMap<>();
		}
	}

	@Override
	public Action nextAction(
			Map<Term, Term> parameter,
			Set<Action> action,
			Set<Literal> observation,
			double reward,
			boolean isTerminal) {
		
		updateParameters(parameter);
		
		if(dynamicEpsilon) {
			epsilon = 1/episode;
		}
		
		String state = observationToState(observation);
		List<Action> actions = discretizeAction(action);
		addNewActionToQ(state, actions);
		
		
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		System.out.println("alpha " + alpha + " gamma " + gamma +
				" epsilon " + epsilon + " policy " + policy);
		System.out.println("reward " + reward);
		System.out.println("State " + state);
		System.out.println("Actions " + actions.toString());
		//System.out.println("q " + q.toString());
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		
		
		Action selectedAction = selectAction(state, actions);
		
		if(previousState != null && previousAction != null) {
			double qSA = q.get(previousState).get(previousAction);
			double qS1A1 = q.get(state).get(selectedAction);
			double q1 = qSA + alpha * (reward + gamma * (qS1A1 - qSA));
			q.get(previousState).put(previousAction, q1);
		}
		
		previousState = state;
		previousAction = selectedAction;

		if(isTerminal) {
			previousState = null;
			previousAction = null;
			episode++;
			episodeForSaving++;
			if(saveProgress && episodeForSaving >= writeEveryNEpisode) {
				episodeForSaving = 0;
				System.out.println("Start writing progress..");
				try {
					outFile = new FileOutputStream(value_function_file, false);
					outObject = new ObjectOutputStream(outFile);
					outObject.writeObject(q);
					outObject.close();
					outFile.close();
				} catch (IOException i) { i.printStackTrace(); }
				System.out.println("..end writing");
			}
		}
		
		return selectedAction;
	}
	
	private Action selectAction(String state, List<Action> actions) {
		if(policy.equals(EGREEDY_POLICY) && randomEGreedy.nextDouble() < epsilon) {
			int randomSelected = randomEGreedy.nextInt(actions.size());
			return actions.get(randomSelected);
		}
		Action selectedAction = null;
		double actionValue = -Double.MAX_VALUE;
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
	
	private void updateParameters(Map<Term, Term> parameters) {
		for(Entry<Term, Term> parameter : parameters.entrySet()) {
			String parameterKey = parameter.getKey().toString();
			String parameterValue = parameter.getValue().toString();
			
			if(parameterKey.equals(POLICY_TERM)) {
				if(parameterValue.equals(ONLY_EXPLOIT_POLICY) || parameterValue.equals(EGREEDY_POLICY)) {
					policy = parameterValue;
				}
			} else {
				if(parameterKey.equals(EPSILON_TERM) && parameterValue.equals("1/t")) {
					dynamicEpsilon = true;
				} else {
					double value = 0;
					try {
						 value = Double.parseDouble(parameterValue);
					} catch(Exception e) {}
					if(value > 0) {
						if(parameterKey.equals(ALPHA_TERM)) {
							alpha = value;
						} else if(parameterKey.equals(GAMMA_TERM)) {
							gamma = value;
						} else if(parameterKey.equals(EPSILON_TERM)) {
							epsilon = value;
							dynamicEpsilon = false;
						}
					}
				}
			} 
		}
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
