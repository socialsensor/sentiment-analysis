package gr.iti.mklab.sentiment;

public class Processor {

	static String main_folder = "/home/atsak/Desktop/Ensemble/";
	public static void main(String[] args) throws Exception {
		
		// Some data:
		String[] tweets = new String[30];	
		tweets[0] = "usermentionsymbol usermentionsymbol yes slime was actually my second choice, can not say what the first one was. ok we are rolling...";
		tweets[1] = "fuck this shit man. It simply sucks.";
		tweets[2] = "Great performance by all singers in the music festival";
		tweets[3] = "good morning from Berlin. Sadly, concerts are over now. ";
		tweets[4] = "The best shit ever man, trully amazed";
		tweets[5] = "Bricks and lines @ Berlin http://t.co/4JHXOOBuEd";
		tweets[6] = "we got new #sandy #wallets both in #light tan , #saffron and #tan #brown @bikiniberlin #money #berlin… http://t.co/ZWDVmfRzCO";
		tweets[7] = "thank you @richpage for this great overview of CRO and Analytics Tools #dahub http://t.co/dicqOAbsKt";
		tweets[8] = "I'm at Berlin Tegel Airport (TXL) - @berlinairport (Berlin) w/ 29 others http://t.co/dFySqGkeNt";
		tweets[9] = "After internet it is time to provide accelarators to #cleantech. With new expertises adapting path & pace to each sectors... #ECO14";
		tweets[10] = "I'm at Rathaus Steglitz (Berlin) http://t.co/l5xQV2FuKV";
		tweets[11] = "I'm at Berlin Hauptbahnhof (Berlin) w/ 13 others http://t.co/ZTsM4oWNin";
		tweets[12] = "Maschine boot camp with the boss himself - Huckaby...! @ Native Instruments, Berlin http://t.co/FYo3Rqt4hq";
		tweets[13] = "@jlkelly @sociomantic @dunnhumby thanks for the listening. go Minny!";
		tweets[14] = "#mobileoayrri DGComp say in 2016 there will be a European basic bank account so all European will have a debit card.";
		tweets[15] = "Dinner Table is set for tonight! @tomkirschbaum @carhartl @DonDahlmann @StefanKellner http://t.co/nZQBewq7un";
		tweets[16] = "#mobilepayrri DGInt confirm that payment accounts don't account so member states have to ensure all residents have a bank account (?)";
		tweets[17] = "can I request store credit if david brooks is wrong about something";
		tweets[18] = "@WiredUK: Check out #wiredmoney agenda, including @dgwbirch @brettking @ZappPayments @gogoDanae @noreenahertz: http://t.co/MnwLmQYiBW :)";
		tweets[19] = "How to save 12 to 18 month before leaving equity of your #startup to a VC? #crowdfunding #ECO14 http://t.co/aBUtwNJks1";
		tweets[20] = "Claus Lavendt argues privilege sets are not enough in #filemaker at #dotfmp http://t.co/jpyF697tAi";
		tweets[21] = "Time for a collage to remember this spectacular night! Schattenspiel by coraberlin33 at Extravaganza… http://t.co/gQbeuZkkfN";
		tweets[22] = "Spent today on a walking tour of Berlin. Best €12 I ever spent. @ Remains Of The Berlin Wall http://t.co/a5j12WtJlx";
		tweets[23] = "DE-News : The latest is a Berlin store called Original Unpacked (Original Unverpackt), now raising funds on the... http://t.co/B74kOmq07i";
		tweets[24] = "Current #BerlinWeather 21°C - Partly Cloudy #Berlin http://t.co/MnAHcsZouN http://t.co/UVSsczGJX2";
		tweets[25] = "The collection so far... @ Berlin - Germany http://t.co/OpDIQ6PAJs";
		tweets[26] = "Afternoon of looking round vintage shops and record stores, now sat soaking up the beaut weather on… http://t.co/Nx9nWgzxac";
		tweets[27] = "Home energy management from @lumenaza #ECO14 Berlin #smarthome http://t.co/B0qPUY1ekk";
		tweets[28] = "This phone is 14 years old and it works like a charm, I can also use it as a weapon. http://t.co/7zeUHCP7xu";
		tweets[29] = "Really impressed with @djht2014, it would be good to see this sort of event in the UK. #djht2014 http://t.co/GV2xnFLmvF";


		// Here we go:
		SentimentAnalyser analyser = new SentimentAnalyser(main_folder);	
		for (int i=0; i<tweets.length; i++)
			System.out.println(analyser.getPolarity(tweets[i]));
	}
}
