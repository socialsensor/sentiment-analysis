package gr.iti.mklab.sentiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class PolarityClassifier {

	BidiMap<String, Integer> tba;
	BidiMap<String, Integer> fba;
	BidiMap<String, Integer> cba;
	String folder;
	Classifier[] mnb_classifiers;
	LibSVM lexicon_classifier;
	Instances[] text;
	Instances[] feature;
	Instances[] complex;
	Instances lexicon_instances;
	Instances training_text;
	Instances training_feature;
	Instances training_complex;
	StringToWordVector stwv;
	

	/**Constructor of the class. "tba", "fba" and "cba" refer to the "attribute-->position" relations.*/
	public PolarityClassifier(String f, BidiMap<String, Integer> tb, BidiMap<String, Integer> fb, BidiMap<String, Integer> cb){
		folder = f;
		initializeAttributes(tb, fb, cb);
		text = new Instances[2];
		feature = new Instances[2];
		complex = new Instances[2];	
		initialiseTextFilter();
		initializeClassifiers();
	}
	
	
	
	
	
	/**Begins the algorithm.*/
	public String test(String dataset, Instances[] all){
		String output = "";
		try {
			text[0] = getText(all[0]);
			feature[0] = getFeature(all[1]);
			complex[0] = getComplex(all[2]);
			lexicon_instances = all[3];
		} catch (Exception e) {
			e.printStackTrace();
		}
		reformatText(text[0]);
		reformatFeature(feature[0]);
		reformatComplex(complex[0]);
		try {
			output = apply();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
	
	
	
	/**The main method that sets up all the processes of the Ensemble classifier. Returns the decision made by the two classifiers.*/
	private String apply() throws Exception{
		double[] hc = applyHC();		// applies the HC and returns the results
		double lc = applyLC();			// same for LC
		double content_pos_vals = (hc[0]+hc[2]+hc[4])/73.97;
		double content_neg_vals = (hc[1]+hc[3]+hc[5])/73.97;
		double hc_val = (1+content_pos_vals-content_neg_vals)/2;
		String output="";
		
		if ((hc_val<0.5) && (lc>0.5)){
			output = "negative";
		} else if ((hc_val>0.5) && (lc<0.5)){
			output = "positive";
		} else {
			output = "nan";
		}
		return output;
	}
	
	
	
	
	
	/**Applies the learned MNB models and returns the output of HC.*/
	private double[] applyHC() throws Exception{
		
		double[] scores = new double[6];
		for (int i=0; i<mnb_classifiers.length; i++){
			Instances test = null;
			if (i==0)
				test = text[1];
			else if (i==1)
				test = feature[1];
			else
				test = complex[1];
			test.setClassIndex(0);	
			
			double[] preds = mnb_classifiers[i].distributionForInstance(test.get(0));	// gets the probabilities for each class (positive/negative)
			if (i==0){
				scores[0] = preds[0]*31.07;
				scores[1] = preds[1]*31.07;
			} else if (i==1){
				scores[2] = preds[0]*11.95;
				scores[3] = preds[1]*11.95;
			} else if (i==2){
				scores[4] = preds[0]*30.95;
				scores[5] = preds[1]*30.95;
			}
		}
		return scores;
	}
	
	
	
	
	
	/**Applies the LC classifier (LBRepresentation)*/
	private double applyLC() throws Exception{
		lexicon_instances.setClassIndex(6);
		return lexicon_classifier.classifyInstance(lexicon_instances.instance(0));
	}
	
	
	
	
	/**Alters the order of the text representation's attributes according to the train files.*/
	private void reformatText(Instances text_test){	
		// remove the attributes from the test set that are not used in the train set
		String[] options = new String[2];
		options[0] = "-R";
		String opt = "";
		boolean found = false;
		for (int j=0; j<text_test.numAttributes(); j++){
			if (tba.get(text_test.attribute(j).name())==null){
				int pos = j+1;
				found = true;
				opt = opt+pos+",";
			} 
		}
		if (found==true)
			options[1] = opt.substring(0,opt.length()-1);
		else
			options[1] = "";
		Remove remove = new Remove();
		try {
			remove.setOptions(options);
			remove.setInputFormat(text_test);
			Instances newData = Filter.useFilter(text_test, remove);
			
			double[] values = new double[tba.size()];			
			for (int at=0; at<newData.numAttributes(); at++){			
				int pos  = tba.get(newData.attribute(at).name());		// get the index of this attribute in the train set
				values[pos] = newData.get(0).value(at);					// ...and its value
			}
			training_text.add(0, new SparseInstance(1.0, values));
			text[1] = new Instances(training_text,0,1);
			training_text.remove(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**Alters the order of the feature representation's attributes according to the train files.*/
	private void reformatFeature(Instances feature_test){
		
		// remove the attributes from the test set that are not used in the train set
		String[] options = new String[2];
		options[0] = "-R";
		String opt = "";
		boolean found = false;
		for (int j=0; j<feature_test.numAttributes(); j++){
			if (fba.get(feature_test.attribute(j).name())==null){
				int pos = j+1;
				found = true;
				opt = opt+pos+",";
			} 
		}
		if (found==true)
			options[1] = opt.substring(0,opt.length()-1);
		else
			options[1] = "";
		Remove remove = new Remove();
		try {
			remove.setOptions(options);
			remove.setInputFormat(feature_test);
			Instances newData = Filter.useFilter(feature_test, remove);	
			double[] values = new double[fba.size()];			
			for (int at=0; at<newData.numAttributes(); at++){
				int pos  = fba.get(newData.attribute(at).name());		// get the index of this attribute in the train set
				values[pos] = newData.get(0).value(at);					// ...and its value
			}
			training_feature.add(0, new SparseInstance(1.0, values));
			feature[1]= new Instances(training_feature,0,1);
			training_feature.remove(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**Alters the order of the complex representation's attributes according to the train files.*/
	private void reformatComplex(Instances complex_test){
		
		// remove the attributes from the test set that are not used in the train set
		String[] options = new String[2];
		options[0] = "-R";
		String opt = "";
		boolean found = false;
		for (int j=0; j<complex_test.numAttributes(); j++){
			if (cba.get(complex_test.attribute(j).name())==null){
				int pos = j+1;
				found = true;
				opt = opt+pos+",";
			} 
		}
		if (found==true)
			options[1] = opt.substring(0,opt.length()-1);
		else
			options[1] = "";
		Remove remove = new Remove();
		try {
			remove.setOptions(options);
			remove.setInputFormat(complex_test);
			Instances newData = Filter.useFilter(complex_test, remove);		
			double[] values = new double[cba.size()];			
			for (int at=0; at<newData.numAttributes(); at++){			
				int pos  = cba.get(newData.attribute(at).name());		// get the index of this attribute in the train set
				values[pos] = newData.get(0).value(at);					// ...and its value
			}
			training_complex.add(0, new SparseInstance(1.0, values));
			complex[1]= new Instances(training_complex,0,1);
			training_complex.remove(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	

	
	/**Returns the Text-based Representations.*/
	private Instances getText(Instances data){	
		data.setClassIndex(0);
		Instances newData=null;
		try {
			stwv.setInputFormat(data);
			newData = weka.filters.Filter.useFilter(data, stwv);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newData;
	}
	

	/**Returns the Feature-based Representations.*/
	private Instances getFeature(Instances data){

		data.setClassIndex(0);
		try {
			stwv.setInputFormat(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		NGramTokenizer tokenizer = new NGramTokenizer();
		tokenizer.setNGramMinSize(1);
		tokenizer.setNGramMaxSize(1);
		stwv.setTokenizer(tokenizer);	
		Instances newData = null;
		try {
			newData = weka.filters.Filter.useFilter(data, stwv);
		} catch (Exception e) {
			e.printStackTrace();
		}
		tokenizer.setNGramMinSize(2);
		tokenizer.setNGramMaxSize(2);
		stwv.setTokenizer(tokenizer);
		return newData;
	}
	
	/**Returns the Complex (text+POS) Representations.*/
	private Instances getComplex(Instances data){

		data.setClassIndex(0);
		Instances newData = null;
		try {
			stwv.setInputFormat(data);
			newData = weka.filters.Filter.useFilter(data, stwv);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return newData;
	}
	
	/**Initializes the StringToWordVector filter to be used in the representations.*/
	private void initialiseTextFilter(){
		stwv = new StringToWordVector();
		stwv.setLowerCaseTokens(true);
		stwv.setMinTermFreq(1);
		stwv.setUseStoplist(false);
		stwv.setTFTransform(false);
		stwv.setIDFTransform(false);		
		stwv.setWordsToKeep(1000000000);
		NGramTokenizer tokenizer = new NGramTokenizer();
		tokenizer.setNGramMinSize(2);
		tokenizer.setNGramMaxSize(2);
		stwv.setTokenizer(tokenizer);
	}
	
	/**Initializes the MNB and SVM classifiers, by loading the previously generated models.*/
	private void initializeClassifiers(){
		mnb_classifiers = new Classifier[3];
		try {
			mnb_classifiers[0] = (Classifier) weka.core.SerializationHelper.read(folder+"/models/text.model");
			mnb_classifiers[1] = (Classifier) weka.core.SerializationHelper.read(folder+"/models/feature.model");
			mnb_classifiers[2] = (Classifier) weka.core.SerializationHelper.read(folder+"/models/complex.model");
			lexicon_classifier = (LibSVM) weka.core.SerializationHelper.read(folder+"/models/lexicon.model");
			BufferedReader trdr = new BufferedReader(new FileReader(new File(folder+"/train/T.arff")));
			BufferedReader frdr = new BufferedReader(new FileReader(new File(folder+"/train/F.arff")));
			BufferedReader crdr = new BufferedReader(new FileReader(new File(folder+"/train/C.arff")));
			training_text = new Instances(trdr);
			training_feature = new Instances(frdr);
			training_complex = new Instances(crdr);
			trdr.close();
			frdr.close();
			crdr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**Initializes the BidiMaps.*/
	private void initializeAttributes(BidiMap<String, Integer> tb, BidiMap<String, Integer> fb, BidiMap<String, Integer> cb){
		tba = new DualHashBidiMap<String, Integer>();
		fba = new DualHashBidiMap<String, Integer>();
		cba = new DualHashBidiMap<String, Integer>();
		tba = tb;
		fba = fb;
		cba = cb;
	}
}