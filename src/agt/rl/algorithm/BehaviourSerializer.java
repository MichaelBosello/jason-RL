package rl.algorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BehaviourSerializer {
	
	private String value_function_directory = "valuefunction";
	private String value_function_filename = "/learnedvf";
	private String value_function_extension = ".sar";
	private String value_function_file = value_function_directory + value_function_filename + value_function_extension;
	
	private ObjectOutputStream outObject;
	private FileOutputStream outFile;
	private int episodeForSaving = 1;
	private int writeEveryNEpisode = 400;
	
	private boolean saveProgress = false;
	private boolean loadProgress = false;
	
	private Object behaviour = null;
	
	public BehaviourSerializer() {
		File directory = new File(value_function_directory);
	    if (saveProgress && !directory.exists()){
	        directory.mkdir();
	    }
	    
	    File file = new File(value_function_file);
	    if (file.isFile() && file.canRead()) {
	    	if(loadProgress) {
				try {
					FileInputStream fileIn = new FileInputStream(value_function_file);
					ObjectInputStream in = new ObjectInputStream(fileIn);
					behaviour = in.readObject();
					in.close();
					fileIn.close();
				} catch (IOException i) {
					System.out.println("Can't read value function file, start with new behaviour");
				} catch (ClassNotFoundException c) {
					c.printStackTrace();
				}
			}
	    } else {
	    	if(saveProgress) {
		    	try {
					file.createNewFile();
				} catch (IOException e) { e.printStackTrace(); }
	    	}
	    }
	}
	
	public void episodeEnd(Object policy) {
		episodeForSaving++;
		if(saveProgress && episodeForSaving >= writeEveryNEpisode) {
			episodeForSaving = 0;
			System.out.println("Start writing progress..");
			try {
				outFile = new FileOutputStream(value_function_file, false);
				outObject = new ObjectOutputStream(outFile);
				outObject.writeObject(policy);
				outObject.close();
				outFile.close();
			} catch (IOException i) { i.printStackTrace(); }
			System.out.println("..end writing");
		}
	}

	public Object getBehaviour() {
		return behaviour;
	}

	public void setSaveProgress(boolean saveProgress) {
		this.saveProgress = saveProgress;
	}

	public void setLoadProgress(boolean loadProgress) {
		this.loadProgress = loadProgress;
	}
	
}
