package gym.rest;

import java.util.Map;

public class EnvironmentRest {
    private String name;
    private Map<String, String> parameters;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
}
