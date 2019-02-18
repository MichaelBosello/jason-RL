package rl.beliefbase;

import java.util.Iterator;

import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.PredicateIndicator;
import jason.asSyntax.Rule;
import jason.asSyntax.Term;
import jason.asSyntax.VarTerm;
import jason.bb.BeliefBase;

public class TerminalRule {
	
	public static final PredicateIndicator TERMINAL_INDICATOR = new PredicateIndicator("rl_terminal", 1);
	
	public static boolean isCurrentStateTerminal(String goal, Agent agentReference, BeliefBase bb) {
		Iterator<Literal> terminalCandidate = bb.getCandidateBeliefs(TERMINAL_INDICATOR);
		if (terminalCandidate != null)
			while (terminalCandidate.hasNext()) {
				Literal terminal = terminalCandidate.next();
				if (!terminal.isRule()) {
					for (Term terminalGoal : terminal.getTerms()) {
						if (terminalGoal.isGround() && terminalGoal.toString().equals(goal)) {
							return true;
						}
					}
				} else {
					Rule terminalRule = (Rule) terminal;
					Term headGoal = terminalRule.getHead().getTerm(0);

					Unifier unifierGoal = new Unifier();
					if (headGoal.isVar()) {
						VarTerm goalVar = (VarTerm) headGoal;
						unifierGoal.bind(goalVar, ASSyntax.createAtom(goal));
					}

					Iterator<Unifier> terminalRuleUnifier = terminalRule.getBody().logicalConsequence(agentReference,
							unifierGoal);
					if (terminalRuleUnifier.hasNext()) {
						if (headGoal.isVar() || (headGoal.isGround() && headGoal.toString().equals(goal)))
							return true;
					}
				}
			}
		return false;
	}
}
