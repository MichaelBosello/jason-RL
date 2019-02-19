package gridworld;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;
import jason.environment.grid.Location;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Random;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.ObsProperty;

import simulation.EpisodicSimulation;

public class GridWorldEnv extends Artifact {

	public static final int GRID_SIZE = 5;
	public static final int FINISH_LINE = 16; // finsh line code in grid model

	private static final boolean SHOW_VIEW = false;

	private GridModel model;
	private GridView view;
	Location finishline = new Location(GRID_SIZE - 1, GRID_SIZE - 1);

	//to track the performance
	private EpisodicSimulation simulation = new EpisodicSimulation();
	private int movesDone = 0;
	private int minNumberOfMoves;

	@OPERATION
	public void init() {
		model = new GridModel();
		if (SHOW_VIEW) {
			view = new GridView(model);
		}
		model.setView(view);
		defineObsProperty("pos", 0, 0);
		updatePercepts();
	}

	@OPERATION
	public void move(String move) {
		try {
			if (SHOW_VIEW) {
				Thread.sleep(50);
			}
		} catch (Exception e) {}

		movesDone++;

		try {
			if (move.equals("right")) {
				model.right();
			} else if (move.equals("left")) {
				model.left();
			} else if (move.equals("up")) {
				model.up();
			} else if (move.equals("down")) {
				model.down();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		updatePercepts();
	}

	void updatePercepts() {
		Location agentLocation = model.getAgPos(0);
		ObsProperty position = getObsProperty("pos");
		position.updateValue(0, agentLocation.x);
		position.updateValue(1, agentLocation.y);

		if (model.hasObject(FINISH_LINE, agentLocation)) {
			int error = movesDone - minNumberOfMoves;
			simulation.episodeEnd(error);
			movesDone = 0;

			if (!hasObsProperty("finishline"))
				defineObsProperty("finishline");
			model.reset();
			Location newAgentLocation = model.getAgPos(0);
			position.updateValue(0, newAgentLocation.x);
			position.updateValue(1, newAgentLocation.y);
		} else {
			try {
				removeObsProperty("finishline");
			} catch (IllegalArgumentException e) {}
		}
	}

	class GridModel extends GridWorldModel {
		Random rnd = new Random();

		private GridModel() {
			super(GRID_SIZE, GRID_SIZE, 2);
			add(FINISH_LINE, finishline);
			reset();
		}

		void right() throws Exception {
			Location r1 = getAgPos(0);
			if (r1.x < GRID_SIZE - 1)
				r1.x++;
			setAgPos(0, r1);
		}

		void left() throws Exception {
			Location r1 = getAgPos(0);
			if (r1.x > 0)
				r1.x--;
			setAgPos(0, r1);
		}

		void up() throws Exception {
			Location r1 = getAgPos(0);
			if (r1.y > 0)
				r1.y--;
			setAgPos(0, r1);
		}

		void down() throws Exception {
			Location r1 = getAgPos(0);
			if (r1.y < GRID_SIZE - 1)
				r1.y++;
			setAgPos(0, r1);
		}

		private void reset() {
			int x = rnd.nextInt(GRID_SIZE);
			int y = rnd.nextInt(GRID_SIZE);
			while (x == finishline.x && y == finishline.y) {
				x = rnd.nextInt(GRID_SIZE);
				y = rnd.nextInt(GRID_SIZE);
			}
			setAgPos(0, x, y);
			minNumberOfMoves = distance(getAgPos(0), finishline);
		}

		private int distance(Location agentLocation, Location target) {
			return Math.abs(agentLocation.x - target.x) + Math.abs(agentLocation.y - target.y);
		}
	}

	class GridView extends GridWorldView {

		private static final long serialVersionUID = 1L;

		public GridView(GridModel model) {
			super(model, "Grid World", 600);
			defaultFont = new Font("Arial", Font.BOLD, 18);
			setVisible(true);
			repaint();
		}

		@Override
		public void draw(Graphics g, int x, int y, int object) {
			switch (object) {
			case GridWorldEnv.FINISH_LINE:
				drawFinishLine(g, x, y);
				break;
			}
		}

		@Override
		public void drawAgent(Graphics g, int x, int y, Color c, int id) {
			String label = "A";
			c = Color.blue;
			super.drawAgent(g, x, y, c, -1);
			if (id == 0) {
				g.setColor(Color.black);
			}
			super.drawString(g, x, y, defaultFont, label);
			repaint();
		}

		public void drawFinishLine(Graphics g, int x, int y) {
			super.drawObstacle(g, x, y);
			g.setColor(Color.white);
			drawString(g, x, y, defaultFont, "F");
		}

	}
}