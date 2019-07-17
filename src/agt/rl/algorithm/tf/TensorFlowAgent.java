package rl.algorithm.tf;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import jason.asSemantics.Agent;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import rl.algorithm.AlgorithmRL;
import rl.algorithm.tf.rest.ActionRest;
import rl.algorithm.tf.rest.EnvironmentRest;
import rl.algorithm.tf.rest.StateRest;
import rl.beliefbase.BeliefBaseRL;
import rl.component.Action;
import rl.component.Observation;
import rl.component.ObservationParameter;
import rl.component.PlanLibraryRL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class TensorFlowAgent implements AlgorithmRL{
	
	public static String TARGET = "http://localhost:5002/agent/";
	public static String COLLECT_POLICY = "/next_train_action";
	public static String GREEDY_POLICY = "/next_best_action";
	public static String POLICY_FUNCTOR = "policy";
	public static String GREEDY_POLICY_FUNCTOR = "greedy";
	
	public static int N_ACTION_REAL = 10;
	
	protected abstract String getMethod();
	private String goal;
	
	private List<Observation> observations;
	private Map<String, Observation> observationsNameMap = new HashMap<>();
	private List<Action> actions;
	
	private Client client = ClientBuilder.newClient();
	
	private double preActionReward = 0;
	
	public TensorFlowAgent(String goal) {
		super();
		this.goal = goal;
	}

	@Override
	public Action nextAction(Map<Term, Term> parameters, Set<Action> action, Set<Literal> currentObservation,
			double reward, boolean isTerminal) {
		
		String policy = COLLECT_POLICY;
		for(Entry<Term, Term> parameter : parameters.entrySet()) {
			if(parameter.getKey().toString().equals(POLICY_FUNCTOR)) {
				if(parameter.getValue().toString().equals(GREEDY_POLICY_FUNCTOR)) {
					policy = GREEDY_POLICY;
				}
			}
		}
		
		List<Double> currentState = observationsToTF(currentObservation);
		
        StateRest<Double> state = new StateRest<>();
        state.setState(currentState);
        state.setState_type("double");
        state.setReward(preActionReward);
        state.setIs_terminal(isTerminal);
        
        preActionReward = reward;

        Response response = client.target(TARGET + goal + policy)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(state, MediaType.APPLICATION_JSON));

        @SuppressWarnings("unchecked")
		ActionRest<List<Integer>> actionRest = response.readEntity(ActionRest.class);
		return actions.get(actionRest.getAction().get(0).get(0));
	}

	@Override
	public double expectedReturn(Set<Action> action, Set<Literal> observation) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void initialize(Agent agent, BeliefBaseRL bb) {
		
		EnvironmentRest<Integer, Double> environment = new EnvironmentRest<>();
		environment.setAgent_type(getMethod());
		//actions specification
		actions = Action.discretizeAction(PlanLibraryRL.getAllActionsForGoal(agent, goal));
        
        environment.setA_type("int");
        List<Integer> a_shape = new ArrayList<>();
    	List<Integer> a_min = new ArrayList<>();
    	List<Integer> a_max = new ArrayList<>();
    	a_shape.add(1);
    	a_min.add(0);
    	a_max.add(actions.size() - 1);
    	environment.setA_shape(a_shape);
    	environment.setA_min(a_min);
    	environment.setA_max(a_max);
        
        //observations specification
		Set<Term> observationsTerm = bb.getObservedList(goal);
		observations = new ArrayList<>();
		for(Term observation : observationsTerm) {
			Observation o = new Observation(observation);
			observations.add(o);
			observationsNameMap.put(o.getName(), o);
		}
        
		environment.setO_type("double");
        List<Integer> o_shape = new ArrayList<>();
    	List<Double> o_min = new ArrayList<>();
    	List<Double> o_max = new ArrayList<>();
		for(Observation observation : observations) {
			if(observation.getParameters().size() == 0) {
				o_min.add(0.0);
    			o_max.add(1.0);
			} else
        	for(ObservationParameter param : observation.getParameters()) {
        		if(param.getType() == Observation.ParameterType.REAL) {
        			o_min.add((double) param.getMin());
        			o_max.add((double) param.getMax());
        		} else if(param.getType() == Observation.ParameterType.INT) {
        			o_min.add((double) param.getMin());
        			o_max.add((double) param.getMax());
        		} else if(param.getType() == Observation.ParameterType.SET) {
        			o_min.add(0.0);
        			o_max.add((double) param.getSet().size() - 1);
        		}
        	}
    	}
		o_shape.add(o_min.size());
		environment.setO_shape(o_shape);
    	environment.setO_min(o_min);
    	environment.setO_max(o_max);
		
    	//initial state
        environment.setInit_state(observationsToTF(bb.getCurrentObservation(goal)));
		
		//parameters
		Map<Term, Term> parameters = bb.getRlParameter();
		Map<String, String> parametersString = new HashMap<>();
		for(Entry<Term, Term> param : parameters.entrySet()) {
			parametersString.put(param.getKey().toString(), param.getValue().toString());
		}
		environment.setParameters(parametersString);
		
		
		client.target(TARGET + goal)
        .request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(environment, MediaType.APPLICATION_JSON));
		
	}
	
	protected List<Double> observationsToTF(Set<Literal> observationsLiteral){
		List<Observation> currentGround = new ArrayList<>();
		for(Term observation : observationsLiteral) {
			String observationName = ((Literal) observation).getFunctor();
			Observation o = observationsNameMap.get(observationName);
			o.setParamValues(observation);
			if(o.getParameters().size() == 0) {
				currentGround.add(o);
			}
		}
        List<Double> stateTF = new ArrayList<>();
        for(Observation observation : observations) {
        	if(observation.getParameters().size() == 0) {
        		if(currentGround.contains(observation)){
        			stateTF.add(1.0);
        		} else {
        			stateTF.add(0.0);
        		}
			} else
        	for(ObservationParameter param : observation.getParameters()) {
        		if(param.getType() == Observation.ParameterType.REAL) {
        			stateTF.add(Double.parseDouble(param.getValue()));
        		} else if(param.getType() == Observation.ParameterType.INT) {
        			stateTF.add(Double.parseDouble(param.getValue()));
        		} else if(param.getType() == Observation.ParameterType.SET) {
        			stateTF.add((double) param.getSet().indexOf(param.getValue()));
        		}
        	}
        }
        return stateTF;
	} 
}
