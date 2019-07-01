package rl.algorithm.tf;

public class Reinforce extends TensorFlowAgent{
	public Reinforce(String goal) {
		super(goal);
	}

	protected String getMethod() { return "reinforce"; }
}
