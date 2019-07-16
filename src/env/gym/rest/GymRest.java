package gym.rest;

import java.util.Map;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@SuppressWarnings("unchecked")
public class GymRest<T> {
	
	public static String TARGET = "http://localhost:5003/env/";
	
	private Client client = ClientBuilder.newClient();
	private String envName; 

	public StateRest<T> initialize(String envName, Map<String, String> parameters) {
		this.envName = envName;
		
		EnvironmentRest env = new EnvironmentRest();
		env.setName(envName);
		env.setParameters(parameters);
		
		Response response = client.target(TARGET + envName)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(env, MediaType.APPLICATION_JSON));

		return response.readEntity(StateRest.class);
	}
	
	public StateRest<T> step(int action){
		Response response = client.target(TARGET + envName + "/" + action)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(action, MediaType.APPLICATION_JSON));
		return response.readEntity(StateRest.class);
	}
	
	
}
