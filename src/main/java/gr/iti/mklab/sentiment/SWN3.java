package gr.iti.mklab.sentiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SWN3 {

  private Map<String, Double> dictionary;

  public SWN3(String pathToSWN) throws IOException {
    // This is our main dictionary representation
    dictionary = new HashMap<String, Double>();
    HashMap<String, HashMap<Integer, Double>> tempDictionary = new HashMap<String, HashMap<Integer, Double>>();

    BufferedReader csv = null;
    try {
      csv = new BufferedReader(new FileReader(pathToSWN));
      int lineNumber = 0;
      String line;
      while ((line = csv.readLine()) != null) {
    	  lineNumber++;
    	  if (!line.trim().startsWith("#")) {
    		  String[] data = line.split("\t");
    		  String wordTypeMarker = data[0];
    		  if (data.length != 6) {
    			  throw new IllegalArgumentException( "Incorrect tabulation format in file, line: "+ lineNumber);
    		  }
    		  Double synsetScore = Double.parseDouble(data[2]) - Double.parseDouble(data[3]);
    		  String[] synTermsSplit = data[4].split(" ");
    		  for (String synTermSplit : synTermsSplit) {
    			  String[] synTermAndRank = synTermSplit.split("#");
    			  String synTerm = synTermAndRank[0] +"#"+ wordTypeMarker;
    			  int synTermRank = Integer.parseInt(synTermAndRank[1]);
    			  if (!tempDictionary.containsKey(synTerm)) {
    				  tempDictionary.put(synTerm,new HashMap<Integer, Double>());
    			  }
    			  tempDictionary.get(synTerm).put(synTermRank, synsetScore);
    		  }
    	  }
      }
      // Go through all the terms.
      for (Map.Entry<String, HashMap<Integer, Double>> entry : tempDictionary.entrySet()) {
    	  String word = entry.getKey();
    	  Map<Integer, Double> synSetScoreMap = entry.getValue();

	// Calculate weighted average. Weigh the synsets according to
	// their rank.
	// Score= 1/2*first + 1/3*second + 1/4*third ..... etc.
	// Sum = 1/1 + 1/2 + 1/3 ...
    	  double score = 0.0;
    	  double sum = 0.0;
    	  for (Map.Entry<Integer, Double> setScore : synSetScoreMap.entrySet()) {
    		  score += setScore.getValue() / (double) setScore.getKey();
    		  sum += 1.0 / (double) setScore.getKey();
    	  }
    	  score /= sum;
    	  dictionary.put(word, score);
      }
    } catch (Exception e) {e.printStackTrace();} finally {if (csv != null) {csv.close();}}
  }
  
  public double extract(String word, String pos) {
	  if (dictionary.containsKey(word+"#"+pos))
		  return dictionary.get(word + "#" + pos);
	  else
		  return 0.0;
  }
}