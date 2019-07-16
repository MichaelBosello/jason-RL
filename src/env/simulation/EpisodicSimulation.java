package simulation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class EpisodicSimulation {

	private int episodes = 0;
	private int episodesValue = 0;
	private double averageValue = 0;

	private final boolean saveResult;
	private final static int SAVE_RESULT_AT = 6000;
	PrintWriter simultationResultsWriter;

	public EpisodicSimulation(String filename, boolean saveResult) {
		this.saveResult = saveResult;
		if (saveResult) {
			try {
				simultationResultsWriter = new PrintWriter(filename, "UTF-8");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
	public EpisodicSimulation(boolean saveResult) {
		this("simulation.txt", saveResult);
	}
	
	public EpisodicSimulation() {
		this(false);
	}

	public void episodeEnd(double value) {
		episodes++;
		episodesValue++;

		averageValue = (double) (averageValue + ((value - averageValue) / episodesValue));
		System.out.println(
				"episode " + episodes +
				" value: " + value +
				" - average value last 100 ep: " + averageValue);

		if (saveResult) {
			if (episodes <= SAVE_RESULT_AT) {
				simultationResultsWriter.println(value);
			} else if (episodes == SAVE_RESULT_AT + 1) {
				simultationResultsWriter.close();
			}
		}

		if (episodesValue == 100) {
			episodesValue = 0;
			averageValue = 0;
		}
	}

}
