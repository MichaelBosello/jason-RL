package simulation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class EpisodicSimulation {

	private int episodes = 0;
	private int episodesError = 0;
	private double averageError = 0;

	private final static boolean SAVE_RESULT = false;
	private final static int SAVE_RESULT_AT = 6000;
	PrintWriter simultationResultsWriter;

	public EpisodicSimulation() {
		if (SAVE_RESULT) {
			try {
				simultationResultsWriter = new PrintWriter("simulation.txt", "UTF-8");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	public void episodeEnd(int error) {
		episodes++;
		episodesError++;

		averageError = (double) (averageError + ((error - averageError) / episodesError));
		System.out.println(
				"episode " + episodes +
				" error: " + error +
				" - average error last 100 ep: " + averageError);

		if (SAVE_RESULT) {
			if (episodes <= SAVE_RESULT_AT) {
				simultationResultsWriter.println(error);
			} else if (episodes == SAVE_RESULT_AT + 1) {
				simultationResultsWriter.close();
			}
		}

		if (episodesError == 100) {
			episodesError = 0;
			averageError = 0;
		}
	}

}
