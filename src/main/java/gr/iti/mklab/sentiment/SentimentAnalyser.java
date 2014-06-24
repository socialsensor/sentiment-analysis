package gr.iti.mklab.sentiment;

import weka.core.Instances;


public class SentimentAnalyser {
	
	Trainer tr;
	PolarityClassifier pc;
	TweetPreprocessor tp;
	
	/**Constructor. "main_folder" is provided in order to define the initial directory to work on.*/
	public SentimentAnalyser(String main_folder){
		tr = new Trainer(main_folder);			//tr.train();
		pc = new PolarityClassifier(main_folder, tr.getTextAttributes(), tr.getFeatureAttributes(), tr.getComplexAttributes());
		tp = new TweetPreprocessor(main_folder);
	}

	/**Starts the whole process: preprocesses the given tweet, creates different representations
	 * of it (stored in "all[]" Instances) and tests each one of them in the PolarityClassifier class.*/
	public String getPolarity(String tweet){
		tp.setTweet(tweet);
		String dataset = tp.startProc();
		Instances[] all = tp.getAllInstances();
		return pc.test(dataset, all);
	}
}
