# Quick start
Use the eclipse JaCaMo plugin to execute gridworld.
<http://jacamo.sourceforge.net/doc/install.html>

# Configuration
## Gridworld configuration
Show/hide the Gridworld GUI with the `SHOW_VIEW` boolean in `env/gridworld.GridWorldEnv`

## Save simulation result
To log errors did in each episodes to a file change the boolean `SAVE_RESULT` in `env/simulation.EpisodicSimulation`

## Algorithm Parameters
You can change the parameters with the agent beliefs.

## Serialization
From `agt/rl.algorithm.BehaviourSerializer`:

To serialize learning progress to a file: `saveProgress = true`

To load learning progress from a file: `loadProgress = true;`

# Note on the Belief Base implementation
Originally, for performance purpose, the bb was tracking also the observations to keep from iteration of all the belief. Because of a problem with belif deletion, currently the bb is tracking only the "observe indication" and parameters, and it provide the observations through the bb iterable. To see the original implementation consult the differences in rl.beliefbase.BeliefBaserRL introduced with commit **86b23b7b6b8e1e73f2b88c823617d931b788ced5** at https://github.com/MichaelBosello/jacamo-rl/commit/86b23b7b6b8e1e73f2b88c823617d931b788ced5