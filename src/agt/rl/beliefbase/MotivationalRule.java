package rl.beliefbase;

import java.util.Iterator;

import jason.NoValueException;
import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.PredicateIndicator;
import jason.asSyntax.Rule;
import jason.asSyntax.Term;
import jason.asSyntax.VarTerm;
import jason.bb.BeliefBase;

public class MotivationalRule {

	public static final PredicateIndicator REWARD_INDICATOR = new PredicateIndicator("rl_reward", 2);

	public static double getCurrentReward(String goal, Agent agentReference, BeliefBase bb) {
		double totalReward = 0;
		Iterator<Literal> reward = bb.getCandidateBeliefs(REWARD_INDICATOR);

		while (reward.hasNext()) {
			Literal rw = reward.next();
			if (!rw.isRule()) {
				if (rw.getArity() == 2) {
					Term litGoal = rw.getTerm(0);
					Term litReward = rw.getTerm(1);
					if (litGoal.isGround() && litReward.isNumeric()) {
						if (litGoal.toString().equals(goal)) {
							try {
								totalReward += ((NumberTerm) litReward).solve();
							} catch (NoValueException e) {}
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

					Iterator<Unifier> candidateReward = rewardRule.getBody().logicalConsequence(agentReference, unifierGoal);
					//if rule hasn't variable in head and is valid
					if (candidateReward != null && goalValue != null &&
							rewardValue != null && candidateReward.hasNext()) {
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
						//try to bind goal and/or reward variables
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
										totalReward += Double.parseDouble(rewardValue);
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
}
