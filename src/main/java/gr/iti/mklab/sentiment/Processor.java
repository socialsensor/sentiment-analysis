package gr.iti.mklab.sentiment;

import java.util.LinkedList;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public class Processor {

	// Alter those three parameters for testing:
	static String main_folder = "/home/atsak/Desktop/other/";	// the path to the (unzipped) "resources" folder 
	static String test_dataset = "Liebherr";						// available options for demo: goethe, Liebherr, Cisco
	static boolean useSlidingWindowForTraining = false;				// if set to "true", only the last 1,000 documents will be used for the training of the ensemble classifier
	
	public static void main(String[] args) throws Exception {
		
		LinkedList<String> lt = getData(test_dataset);				// read some data
		SentimentAnalyser analyser = new SentimentAnalyser(main_folder, useSlidingWindowForTraining, test_dataset);
		for (int i=0; i<lt.size(); i++){
			String out = analyser.getPolarity(lt.get(i));			// any text may be passed as an argument here
			//System.out.println(i+"\t"+out);
		}
	}
	
	private static LinkedList<String> getData(String f){
		LinkedList<String> all_tweets = new LinkedList<String>();
		DataSource ds;
		Instances data = null;
		try {
			ds = new DataSource(main_folder+"test_sets/"+f+".arff");
			data =  ds.getDataSet();
		} catch (Exception e) {
			System.out.println("File not found.");
		}
		for (int i=0; i<data.numInstances(); i++){
			all_tweets.add(data.get(i).stringValue(0));
		}
		return all_tweets;
	}
}