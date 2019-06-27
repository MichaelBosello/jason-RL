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
import rl.component.ActionParameter;
import rl.component.Observation;
import rl.component.ObservationParameter;
import rl.component.PlanLibraryRL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TensorFlowAgent implements AlgorithmRL{
	
	public static String TARGET = "http://localhost:5002/env/";
	public static String COLLECT_POLICY = "/next_train_action";
	public static String GREEDY_POLICY = "/next_best_action";
	public static String POLICY_FUNCTOR = "policy";
	public static String GREEDY_POLICY_FUNCTOR = "greedy";
	
	public static int N_ACTION_REAL = 10;
	
	protected String method = "dqn";
	private String goal;
	
	private List<Observation> observations;
	private Map<String, Observation> observationsNameMap = new HashMap<>();
	
	public TensorFlowAgent(String goal) {
		super();
		this.goal = goal;
	}

	Client client = ClientBuilder.newClient();

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
		
		List<List<Float>> currentState = observationsToTF(currentObservation);
		
        StateRest<List<Float>> state = new StateRest<>();
        state.setState(currentState);
        state.setState_type("float");
        state.setReward(reward);
        state.setIs_terminal(isTerminal);

        Response response = client.target(TARGET + goal + policy)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(state, MediaType.APPLICATION_JSON));

        @SuppressWarnings("rawtypes")
		ActionRest actionRest = response.readEntity(ActionRest.class);
        System.out.println(actionRest.getAction());
		return null;
	}

	@Override
	public double expectedReturn(Set<Action> action, Set<Literal> observation) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void initialize(Agent agent, BeliefBaseRL bb) {
		EnvironmentRest<List<Integer>, List<Float>> environment = new EnvironmentRest<>();
		environment.setAgentType(method);
		
		
		Set<Action> actions = PlanLibraryRL.getAllActionsForGoal(agent, goal);
        
        environment.setA_type("int");
        List<Integer> a_shape = new ArrayList<>();
    	List<List<Integer>> a_min = new ArrayList<>();
    	List<List<Integer>> a_max = new ArrayList<>();
    	
    	for(Action action : actions) {
    		a_shape.add(action.getParameters().size());
    		List<Integer> a_minList = new ArrayList<>();
    		List<Integer> a_maxList = new ArrayList<>();
        	for(ActionParameter param : action.getParameters()) {
        		if(param.getType() == Action.ParameterType.REAL) {
        			a_minList.add(0);
        			a_maxList.add(N_ACTION_REAL);
        		} else if(param.getType() == Action.ParameterType.INT) {
        			a_minList.add((int) param.getMin());
        			a_maxList.add((int) param.getMax());
        		} else if(param.getType() == Action.ParameterType.SET) {
        			a_minList.add(0);
        			a_maxList.add(param.getSet().size() - 1);
        		}
        	}
        	a_min.add(a_minList);
			a_max.add(a_maxList);
    	}
    	environment.setA_shape(a_shape);
    	environment.setA_min(a_min);
    	environment.setA_max(a_max);
        
        
		Set<Term> observationsTerm = bb.getObservedList(goal);
		observations = new ArrayList<>();
		for(Term observation : observationsTerm) {
			Observation o = new Observation(observation);
			observations.add(o);
			observationsNameMap.put(o.getName(), o);
		}
        
		environment.setO_type("float");
        List<Integer> o_shape = new ArrayList<>();
    	List<List<Float>> o_min = new ArrayList<>();
    	List<List<Float>> o_max = new ArrayList<>();
		for(Observation observation : observations) {
			o_shape.add(observation.getParameters().size());
    		List<Float> o_minList = new ArrayList<>();
    		List<Float> o_maxList = new ArrayList<>();
        	for(ObservationParameter param : observation.getParameters()) {
        		if(param.getType() == Observation.ParameterType.REAL) {
        			o_minList.add((float) param.getMin());
        			o_maxList.add((float) param.getMax());
        		} else if(param.getType() == Observation.ParameterType.INT) {
        			o_minList.add((float) param.getMin());
        			o_maxList.add((float) param.getMax());
        		} else if(param.getType() == Observation.ParameterType.SET) {
        			o_minList.add(0.0f);
        			o_maxList.add((float) param.getSet().size() - 1);
        		}
        	}
        	o_min.add(o_minList);
			o_max.add(o_maxList);
    	}
		environment.setO_shape(o_shape);
    	environment.setO_min(o_min);
    	environment.setO_max(o_max);
		
    	
        environment.setInit_state(observationsToTF(bb.getCurrentObservation(goal)));
		
		
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
	
	protected List<List<Float>> observationsToTF(Set<Literal> observationsLiteral){
		for(Term observation : observationsLiteral) {
			String observationName = ((Literal) observation).getFunctor();
			Observation o = observationsNameMap.get(observationName);
			o.setParamValues(observation);
		}
        List<List<Float>> stateTF = new ArrayList<>();
        for(Observation observation : observations) {
        	List<Float> obs = new ArrayList<>();
        	for(ObservationParameter param : observation.getParameters()) {
        		if(param.getType() == Observation.ParameterType.REAL) {
        			obs.add(Float.parseFloat(param.getValue()));
        		} else if(param.getType() == Observation.ParameterType.INT) {
        			obs.add(Float.parseFloat(param.getValue()));
        		} else if(param.getType() == Observation.ParameterType.SET) {
        			obs.add((float) param.getSet().indexOf(param.getValue()));
        		}
        	}
        	stateTF.add(obs);
        }
        return stateTF;
	} 
}
