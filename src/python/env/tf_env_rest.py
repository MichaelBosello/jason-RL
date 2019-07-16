from flask import Flask, jsonify, request
from flask_restful import Resource, Api
import json

from generic_tf_env import GenericTfEnv

app = Flask(__name__)
app.debug = False
app.testing = False
api = Api(app)

import logging
log = logging.getLogger('werkzeug')
log.setLevel(logging.ERROR)

envs = {}

class Env(Resource):
  def post(self, id):
    if not id in envs:
      json_data = request.get_json(force=True)
      print("##################################")
      print(json_data)
      env = GenericTfEnv(json_data['name'], json_data['parameters'])
      envs[id] = env

    result = {'state': envs[id].get_current_time_step().observation.tolist(),
              'reward' : envs[id].get_current_time_step().reward.tolist(),
              'terminal' : bool(envs[id].get_current_time_step().is_last())}
    print("##################################")
    print('starting state', result)
    return jsonify(result)

class Action(Resource):
  def post(self, id, action):
    json_data = request.get_json(force=True)
    #print("##################################")
    #print(json_data)
    next_step = envs[id].step(int(action))
    result = {'state': next_step.observation.tolist(),
              'reward' : next_step.reward.tolist(),
              'terminal' : bool(next_step.is_last())}
    #print("##################################")
    #print(result)
    return jsonify(result)

api.add_resource(Env, '/env/<string:id>')
api.add_resource(Action, '/env/<string:id>/<string:action>')

if __name__ == '__main__':
     app.run(port='5003')