from flask import Flask, jsonify, request
from flask_restful import Resource, Api
import json

import numpy as np
from tf_agents.specs import array_spec

from dqn_agent import DqnAgent
from reinforce_agent import ReinforceAgent

app = Flask(__name__)
app.debug = False
app.testing = False
api = Api(app)

import logging
log = logging.getLogger('werkzeug')
log.setLevel(logging.ERROR)

agents = {}

class Env(Resource):
  def post(self, id):
    if not id in agents:
      json_data = request.get_json(force=True)
      print("##################################")
      print(json_data)
      if json_data['a_type'] == "int":
        a_type = np.int32
      if json_data['a_type'] == "float" or json_data['a_type'] == "double":
        a_type = np.float
      if json_data['o_type'] == "int":
        o_type = np.int32
      if json_data['o_type'] == "float" or json_data['o_type'] == "double":
        o_type = np.float
      if json_data['agent_type'] == "dqn":
        agent = DqnAgent(
          array_spec.BoundedArraySpec(
            shape=json_data['a_shape'], dtype=a_type,
            minimum=json_data['a_min'], maximum=json_data['a_max'], name='action'),
          array_spec.BoundedArraySpec(
            shape=json_data['o_shape'], dtype=o_type,
            minimum=json_data['o_min'], maximum=json_data['o_max'], name='observation'),
            np.array(json_data['init_state'], dtype=o_type),
          json_data['parameters'])
      if json_data['agent_type'] == "reinforce":
        agent = ReinforceAgent(
          array_spec.BoundedArraySpec(
            shape=json_data['a_shape'], dtype=a_type,
            minimum=json_data['a_min'], maximum=json_data['a_max'], name='action'),
          array_spec.BoundedArraySpec(
            shape=json_data['o_shape'], dtype=o_type,
            minimum=json_data['o_min'], maximum=json_data['o_max'], name='observation'),
            np.array(json_data['init_state'], dtype=o_type),
          json_data['parameters'])
      agents[id] = agent

class Action(Resource):
  def post(self, id, action_type):
    json_data = request.get_json(force=True)
    #print("##################################")
    #print(json_data)
    if action_type == "next_train_action":
      action_step = agents[id].get_train_action()
    if action_type == "next_best_action":
      action_step = agents[id].get_greedy_action()

    action = action_step.action.numpy()
    agents[id].update(np.array(json_data['state'], dtype=json_data['state_type']),
      json_data['reward'], json_data['is_terminal'], action_step)

    action_list = action.tolist()

    result = {'action': action_list}
    #print("##################################")
    #print(result)
    return jsonify(result)

api.add_resource(Env, '/agent/<string:id>')
api.add_resource(Action, '/agent/<string:id>/<string:action_type>')

if __name__ == '__main__':
     app.run(port='5002')