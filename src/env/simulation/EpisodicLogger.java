package simulation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EpisodicLogger {

	private int episodes = 0;
	private int episodesValue = 0;
	private double averageValue = 0;

	private final boolean saveResult;
	private final int saveResultAt;
	PrintWriter simultationResultsWriter;
	
	SimpleDateFormat sdf = new SimpleDateFormat("_yyyy:MM:dd_HH-mm-ss");

	public EpisodicLogger(String filename, boolean saveResult, int saveResultAt) {
		this.saveResultAt = saveResultAt;
		this.saveResult = saveResult;
		if (saveResult) {
			try {
				simultationResultsWriter = new PrintWriter(filename + sdf.format(new Date()) + ".txt", "UTF-8");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	
	public EpisodicLogger(boolean saveResult, int saveResultAt) {
		this("simulation.txt", saveResult, saveResultAt);
	}
	
	public EpisodicLogger(boolean saveResult) {
		this(saveResult, 1000);
	}
	
	public EpisodicLogger() {
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
			if (episodes <= saveResultAt) {
				simultationResultsWriter.println(value);
			} else if (episodes == saveResultAt + 1) {
				simultationResultsWriter.close();
			}
		}

		if (episodesValue == 100) {
			episodesValue = 0;
			averageValue = 0;
		}
	}

}
