package gr.iti.mklab.sentiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.StringTokenizer;


public class TextPreprocessor {
	
	String tweet;
	String main_folder;
	Hashtable<String, String> abbreviations;
	LinkedList<String> happyEmo = new LinkedList<String>();
	LinkedList<String> sadEmo = new LinkedList<String>();
	
	/**Constructor*/
	public TextPreprocessor(String t){
		main_folder = t;
		abbreviations = getAbbreviations();
		try {
			happyEmo = getHappyEmoticons();
			sadEmo = getSadEmoticons();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/**Some common pre-processing stuff*/
	public String getProcessed(String str){

		StringTokenizer st = new StringTokenizer(str);
		String current;
		String toreturn = "";
		while (st.hasMoreTokens()){			
			current = st.nextToken();						
			current = replaceEmoticons(current);			// current is altered to "happy"/"sad"
			current = replaceTwitterFeatures(current);		// i.e. links, mentions, hash-tags
			current = replaceConsecutiveLetters(current);	// replaces more than 2 repetitive letters with 2
			current = replaceNegation(current);				// if current is a negation word, then current = "not"
			current = replaceAbbreviations(current);		// if current is an abbreviation, then replace it
			current = current.replaceAll("[^A-Za-z0-9]", " ");
			toreturn = toreturn.concat(" "+current);
		}
		return toreturn;
	}
	
	/**Replaces consecutive letters*/
	private String replaceConsecutiveLetters(String current){
		String tmp = current.replaceAll("[^A-Za-z]", "");
		if (tmp.length()>0 && containsRepetitions(tmp)){
			tmp = replaceRepetitions(tmp);
			return tmp;
		}
		return current;	
	}
	
	/**Check whether the given String contains consecutive letters*/
	private boolean containsRepetitions(String str){
		String toreturn=str.substring(0,1);
		char prev = str.charAt(0);
		int cnt = 0;
		
		for (int i=1; i<str.length(); i++){
			char current = str.charAt(i);
			toreturn = toreturn+current;
			if (current==prev){
				cnt++;
				if (cnt>=2)
					return true;
			}else
				cnt = 0;
			prev = str.charAt(i);
		}
		return false;
	}
	
	private String replaceRepetitions(String str){
		String toreturn=str.substring(0,1);
		char prev = str.charAt(0);
		boolean found = false;
		for (int i=1; i<str.length(); i++){
			char current = str.charAt(i);
			toreturn = toreturn+current;
			if (current==prev){
				if (found==true)
					toreturn = toreturn.substring(0,toreturn.length()-1);
				else
					found = true;
			}else if (found==true)
				found = false;
			prev = str.charAt(i);
		}
		return toreturn;
	}
	
	/**Replaces emoticons with their value: "feeling sad" vs "feeling happy"*/
	private String replaceEmoticons(String current){
		if ( happyEmo.contains(current))
			current = "feeling happy";
		else if (sadEmo.contains(current))
			current = "feeling sad";
		return current;
	}
	
	/**Replaces UserMentions, Hashtags, UrlLinks*/
	private String replaceTwitterFeatures(String current){
		if (current.contains("#"))
			current = current.replaceAll("#", " ");
		if (current.contains("@"))
			current = "usermentionsymbol";
		if (current.contains("http:") || current.contains("https:"))
			current = "urlinksymbol";
		return current;
	}
	
	/**Finds whether a negation occurs in a word and returns "not".*/
	private String replaceNegation(String current){
		String tmp1 = current.toLowerCase();
		if (tmp1.endsWith("n\'t")){
			tmp1 = tmp1.substring(0, tmp1.lastIndexOf("n\'t"));
			tmp1 = tmp1.concat(" not");
			if (tmp1.contains("wo "))
				tmp1 = "will not";
			else if (tmp1.contains("ca "))
				tmp1 = "can not";
			else if (tmp1.contains("ai "))
				tmp1 = "is not";

			return tmp1;
		}
		String tmp = current.replaceAll("[^A-Za-z0-9]", "").toLowerCase();
		if (tmp.equals("cannot") || tmp.equals("cant"))
			return "can not";
		if ( (tmp.equals("not")) || (tmp.equals("no")) || (tmp.equals("none")) || (tmp.equals("noone")) || tmp.equals("nobody") || tmp.equals("nothing") || tmp.equals("neither") || tmp.equals("nor") || tmp.equals("nowhere") || tmp.equals("never") || tmp.equals("nver") || tmp.equals("hardly") || tmp.equals("scarcely") || tmp.equals("barely") || tmp.equals("no1")){
			return "not";
		}
		return current;
	}
	
	/**Replaces abbreviations*/
	private String replaceAbbreviations(String current){
		String tmp = current.replaceAll("[^A-Za-z0-9\']", "").toLowerCase();
		if (abbreviations.get(tmp)!=null){
			tmp = abbreviations.get(tmp);
			return tmp;
		}
		return current;
	}
	
	/**Fetch the list of abbreviations and return its contents in a has-table.*/
	private Hashtable<String,String> getAbbreviations(){
		Hashtable<String, String> abbreviations = new Hashtable<String, String>();
		try {
			BufferedReader rdr = new BufferedReader(new FileReader(new File(main_folder+"datasets/abbreviations.txt")));
			String inline;
			while ((inline=rdr.readLine())!=null)
				abbreviations.put(inline.substring(0, inline.indexOf("=")), inline.substring(inline.indexOf("=")+1));
			rdr.close();
		} catch (FileNotFoundException e1) {e1.printStackTrace();} catch (IOException e) {e.printStackTrace();}
		return abbreviations;
	}
	
	/**Get the list of the happy emoticons*/
	private LinkedList<String> getHappyEmoticons() throws IOException{
		File happy = new File(main_folder+"datasets/happyEmoticons");
		BufferedReader brdr2 = new BufferedReader(new FileReader(happy));
		LinkedList<String> hemo = new LinkedList<String>();
		String line;
		while ((line=brdr2.readLine()) != null)
			hemo.add(line);
		brdr2.close();
		return hemo;
	}
	
	/**Get the list of the sad emoticons*/
	private LinkedList<String> getSadEmoticons() throws IOException{
		File happy = new File(main_folder+"datasets/sadEmoticons");
		BufferedReader brdr2 = new BufferedReader(new FileReader(happy));
		LinkedList<String> hemo = new LinkedList<String>();
		String line;
		while ((line=brdr2.readLine()) != null)
			hemo.add(line);
		brdr2.close();
		return hemo;
	}
}