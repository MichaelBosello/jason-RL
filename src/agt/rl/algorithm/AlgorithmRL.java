package rl.algorithm;

import java.util.Map;
import java.util.Set;

import jason.asSemantics.Agent;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import rl.beliefbase.BeliefBaseRL;
import rl.component.Action;

public interface AlgorithmRL {
	Action nextAction(
			Map<Term, Term> parameter,
			Set<Action> action,
			Set<Literal> observation,
			double reward,
			boolean isTerminal);
	
	double expectedReturn(Set<Action> action, Set<Literal> observation);
	
	void initialize(Agent agent, BeliefBaseRL bb);
}
