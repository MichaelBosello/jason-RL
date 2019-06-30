package rl.algorithm.tf;

public class Dqn extends TensorFlowAgent{
	public Dqn(String goal) {
		super(goal);
	}

	protected String getMethod() { return "dqn"; }
}
