package rl.beliefbase;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jason.NoValueException;
import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.ListTerm;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Literal;
import jason.asSyntax.PredicateIndicator;
import jason.asSyntax.Rule;
import jason.asSyntax.Term;
import jason.asSyntax.VarTerm;
import jason.bb.DefaultBeliefBase;
import rl.algorithm.AlgorithmRL;
import rl.algorithm.Sarsa;

public class BeliefBaseRL extends DefaultBeliefBase {

	public static final String PARAMETER_FUNCTOR = "rl_parameter";
	public static final String OBSERVE_FUNCTOR = "rl_observe";

	public static final PredicateIndicator TERMINAL_INDICATOR = new PredicateIndicator("rl_terminal", 1);
	public static final PredicateIndicator REWARD_INDICATOR = new PredicateIndicator("rl_reward", 2);

	Agent agentRef;
	AlgorithmRL rl = new Sarsa();

	Map<Term, Term> parameter = new ConcurrentHashMap<>();// Parameter name -> parameter value
	Map<String, Set<String>> goalObservation = new ConcurrentHashMap<>();// goal, observation list
	Set<String> trackAll = new HashSet<>();

	@Override
	public void init(Agent ag, String[] args) {
		agentRef = ag;
		super.init(ag, args);
	}

	@Override
	public boolean add(Literal bel) {
		addObservation(bel);
		return super.add(bel);
	}

	@Override
	public boolean add(int index, Literal bel) {
		addObservation(bel);
		return super.add(index, bel);
	}
	
	public AlgorithmRL getRLInstance() {
		return rl;
	}

	private void addObservation(Literal belief) {
		String functor = belief.getFunctor();
		if (functor.equals(PARAMETER_FUNCTOR)) {
			if (belief.getArity() == 2) {
				Term parameterName = belief.getTerm(0);
				Term parameterValue = belief.getTerm(1);
				parameter.put(parameterName, parameterValue);
				System.out.println("Set parameter " + parameterName + " to " + parameterValue);
			}
		} else if (functor.equals(OBSERVE_FUNCTOR)) {
			if (belief.getArity() == 2) {
				String goal = belief.getTerm(0).toString();
				Term observe = belief.getTerm(1);

				trackAll.remove(goal);
				if (observe.isUnnamedVar()) {
					trackAll.add(goal);
					System.out.println("Goal " + goal + " tracks all");
				} else if (observe.isList()) {
					((ListTerm) observe).forEach(o -> {
						putMapSet(goalObservation, goal, o.toString());
						System.out.println("Observe " + o.toString() + " for goal " + goal);
					});
				} else if (observe.isGround()) {
					putMapSet(goalObservation, goal, observe.toString());
					System.out.println("Observe " + observe.toString() + " for goal " + goal);
				}
			}
		}
	}

	private <Key, Value> void putMapSet(Map<Key, Set<Value>> map, Key key, Value value) {
		if (!map.containsKey(key)) {
			map.put(key, new HashSet<>());
		}
		map.get(key).add(value);
	}

	public Set<Literal> getCurrentObservation(String goal) {
		Set<Literal> currentObservation = new HashSet<>();// goal, current observation list
		Set<String> observedByThisGoal = goalObservation.get(goal);
		this.forEach(belief -> {
			String functor = belief.getFunctor();
			Literal copy = belief.copy();
			copy.clearAnnots();
			if (trackAll.contains(goal)) {
				currentObservation.add(copy);
			} else if (observedByThisGoal.contains(functor)) {
				currentObservation.add(copy);
				//System.out.println("Add to current observation " + copy.toString() + " for goal " + goal.toString());
			}
		});
		return currentObservation;
	}

	public Map<Term, Term> getRlParameter() {
		return parameter;
	}

	public double getCurrentReward(String goal) {
		double totalReward = 0;
		Iterator<Literal> reward = this.getCandidateBeliefs(REWARD_INDICATOR);

		while (reward.hasNext()) {
			Literal rw = reward.next();
			if (!rw.isRule()) {
				if (rw.getArity() == 2) {
					Term litGoal = rw.getTerm(0);
					Term litReward = rw.getTerm(1);
					if (litGoal.isGround() && litReward.isNumeric()) {
						if (litGoal.toString().equals(goal)) {
							try {
								totalReward += ((NumberTerm) rw).solve();
							} catch (NoValueException e) {
							}
						}
					}
				}
			} else {
				Rule rewardRule = (Rule) rw;
				Literal head = rewardRule.getHead();
				if (head.getArity() == 2) {
					Term ruleGoal = head.getTerm(0);
					Term ruleReward = head.getTerm(1);

					String goalValue = null;
					String rewardValue = null;
					if (ruleGoal.isGround()) {
						goalValue = ruleGoal.toString();
					}
					if (ruleReward.isNumeric()) {
						rewardValue = ruleReward.toString();
					}

					Unifier unifierGoal = new Unifier();

					VarTerm goalVar = null;
					VarTerm rewardVar = null;
					if (ruleGoal.isVar()) {
						goalVar = (VarTerm) ruleGoal;
						unifierGoal.bind(goalVar, ASSyntax.createAtom(goal));
					}
					if (ruleReward.isVar()) {
						rewardVar = (VarTerm) ruleReward;
					}

					Iterator<Unifier> candidateReward = rewardRule.getBody().logicalConsequence(agentRef, unifierGoal);

					if (candidateReward != null)
						if (goalValue != null && rewardValue != null && candidateReward.hasNext()) {
							if (goalValue.equals(goal)) {
								try {
									totalReward += Integer.parseInt(rewardValue);
								} catch (Exception e) {
									if (rewardValue.length() > 2) {
										rewardValue = rewardValue.substring(1, rewardValue.length() - 1);
										try {
											totalReward += Integer.parseInt(rewardValue);
										} catch (Exception e2) {
										}
									}
								}
							}
						} else {
							if (candidateReward != null)
								while (candidateReward.hasNext()) {
									Unifier rewardUnifier = candidateReward.next();
									if (goalVar != null) {
										Term t = rewardUnifier.get(goalVar);
										if (t.isGround()) {
											goalValue = t.toString();
										}
									}
									if (rewardVar != null) {
										Term t = rewardUnifier.get(rewardVar);
										if (t.isNumeric()) {
											rewardValue = t.toString();
										}
									}

									if (goalValue != null && rewardValue != null) {
										if (goalValue.equals(goal)) {
											totalReward += Integer.parseInt(rewardValue);
										}
									}
									if (goalVar != null) {
										goalValue = null;
									}
									if (rewardVar != null) {
										rewardValue = null;
									}
								}
						}
				}
			}
		}

		return totalReward;
	}

	public boolean isCurrentStateTerminal(String goal) {
		Iterator<Literal> terminalCandidate = this.getCandidateBeliefs(TERMINAL_INDICATOR);
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

					Iterator<Unifier> terminalRuleUnifier = terminalRule.getBody().logicalConsequence(agentRef,
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
