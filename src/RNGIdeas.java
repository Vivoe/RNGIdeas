import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;

import com.sun.media.sound.InvalidFormatException;

public class RNGIdeas {

	static TreeMap<String, Boolean> visitedURLs = new TreeMap<String, Boolean>();
	static ArrayList<String> urlQueue = new ArrayList<String>();
	static ArrayList<String> input = new ArrayList<String>();
	static ArrayList<String> nouns = new ArrayList<String>();
	static ArrayList<String> verbs = new ArrayList<String>();
	static final POSModel model = new POSModelLoader().load(new File("en-pos-maxent.bin"));
	static final int NUM_IDEAS = 100;
	static final int NUM_ROUNDS = 3;
	public static Stream<String> testy(String a) {
		ArrayList<String> c = new ArrayList<String>();
		c.add(a);
		return c.stream();
	}

	public static void main(String[] args) throws InvalidFormatException, IOException, InterruptedException {
		
		//Get starting points from text file.
		initQueue();

		// Getting data from github
		for (int i = 0; i < NUM_ROUNDS; i++) {
			urlQueue = (ArrayList<String>) urlQueue.stream().parallel().flatMap(DataMiner::getData)
					.collect(Collectors.toList());
		}

		// Processing words
		input.stream().parallel().forEach(FindWords::tagString);

		//Generating sequences
		for (int i = 0; i < NUM_IDEAS; i++) {
			genIdeas("VNN");
		}
	}

	private static void genIdeas(String types) throws InvalidFormatException {
		types = types.toUpperCase();
		String idea = "";
		for (int i = 0; i < types.length(); i++) {
			if (types.charAt(i) == 'N') {
				idea += nouns.get((int) (Math.random() * nouns.size())) + " ";
			} else if (types.charAt(i) == 'V') {
				idea += verbs.get((int) (Math.random() * verbs.size())) + " ";
			} else {
				InvalidFormatException e = new InvalidFormatException("Not V (verb) or N (noun)");
				throw e;
			}
		}
		System.out.println(idea);
	}

	private static void initQueue() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("urlList.txt"));

		{
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("//"))
					urlQueue.add(line);
			}
		}
		br.close();
	}
}