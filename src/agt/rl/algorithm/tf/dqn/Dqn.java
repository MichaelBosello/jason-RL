package rl.algorithm.tf.dqn;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import rl.algorithm.tf.rest.ActionRest;
import rl.algorithm.tf.rest.EnvironmentRest;
import rl.algorithm.tf.rest.StateRest;

import java.util.ArrayList;
import java.util.List;

public class Dqn {

    public static void main(String[] args){

        EnvironmentRest<Integer> environment = new EnvironmentRest<>();
        List<Integer> a_shape = new ArrayList<>();
        a_shape.add(1);
        environment.setA_shape(a_shape);
        environment.setA_type("int");
        List<Integer> a_min = new ArrayList<>();
        a_min.add(0);
        environment.setA_min(a_min);
        List<Integer> a_max = new ArrayList<>();
        a_max.add(3);
        environment.setA_max(a_max);
        List<Integer> o_shape = new ArrayList<>();
        o_shape.add(2);
        environment.setO_shape(o_shape);
        environment.setO_type("int");
        List<Integer> o_min = new ArrayList<>();
        o_min.add(0);
        environment.setO_min(o_min);
        List<Integer> o_max = new ArrayList<>();
        o_max.add(4);
        environment.setO_max(o_max);
        List<Integer> init_state = new ArrayList<>();
        init_state.add(0);
        init_state.add(0);
        environment.setInit_state(init_state);

        Client client = ClientBuilder.newClient();
        client.target("http://localhost:5002/env/dqn")
              .request(MediaType.APPLICATION_JSON)
              .post(Entity.entity(environment, MediaType.APPLICATION_JSON));


        StateRest<Integer> state = new StateRest<>();
        List<Integer> observations = new ArrayList<>();
        observations.add(1);
        observations.add(1);
        state.setState(observations);
        state.setState_type("int");
        state.setReward(0);
        state.setIs_terminal(false);

        Response response = client.target("http://localhost:5002/env/dqn/next_train_action")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(state, MediaType.APPLICATION_JSON));

        ActionRest action = response.readEntity(ActionRest.class);
        System.out.println(action.getAction());

    }
}
