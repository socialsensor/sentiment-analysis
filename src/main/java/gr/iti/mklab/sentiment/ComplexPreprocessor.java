package gr.iti.mklab.sentiment;

import java.util.StringTokenizer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class ComplexPreprocessor {
	
	/**Some common pre-processing stuff*/
	public String getProcessed(String str, MaxentTagger tagger){

		String toreturn = "";
		toreturn = getPOS(toreturn, tagger);
		return toreturn;
	}
	
	/**The only extra method compared to the text-based approach.*/
	private String getPOS(String sample, MaxentTagger tagger){
		String tagged = tagger.tagString(sample.trim().replaceAll(" +", " "));	
		StringTokenizer stk = new StringTokenizer(tagged);
		
		String output = "";
		while (stk.hasMoreTokens()){
			String tmp = stk.nextToken();
			String tmp2 = tmp.replaceAll("[^A-Za-z_0-9]", "");
			output = output+tmp2+" ";
			if (tmp.contains("."))
				output=output.concat(".");
			if (tmp.contains("!"))
				output=output.concat("!");
			if (tmp.contains(","))
				output=output.concat(",");	
			if (tmp.contains("?"))
				output=output.concat("?");			
		}
		return output;
	}
}