import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;

import com.sun.media.sound.InvalidFormatException;

public class RNGIdeas {

	static ConcurrentLinkedQueue<String> URLQuene = new ConcurrentLinkedQueue<String>();
	static LinkedList<String> input = new LinkedList<String>();
	static LinkedList<String> nouns = new LinkedList<String>();
	static LinkedList<String> verbs = new LinkedList<String>();
	static final POSModel model = new POSModelLoader().load(new File("en-pos-maxent.bin"));
	static final int numIdeas = 100;
	static final int threadsPerRound = 2500;

	public static void main(String[] args) throws InvalidFormatException, IOException, InterruptedException {
		// Get urls to search
		System.out.println("GITGITGITGITGITGTI");
		

		ExecutorService exec;

		// Get info from site
		// Take 4 rounds of links
		int maxIterations = 2;
		int iterations = 0;
		while (iterations < maxIterations && (URLQuene.peek() != null)) {
			System.out.println("XXXXXXXXXXXX");

			exec = Executors.newCachedThreadPool();
			String url;
			int numThreads = 0;

			// Dispatch this round of links
			int cSize = URLQuene.size();
			for (int i = 0; i < cSize && i < threadsPerRound; i++) {
				url = URLQuene.poll();
				numThreads++;
				exec.execute(new DataMiner(url));
			}

			// Cleanup round
			System.out.println(numThreads + " threadcount");
			exec.shutdown();
			boolean timedOut = !exec.awaitTermination(numThreads * 3 + 10000, TimeUnit.MILLISECONDS);
			System.out.println("Success? " + !timedOut);
			iterations++;
			System.out.println(iterations + " Iterations ");
		}
		System.err.println(URLQuene.toString() + " Done!");

		// Get words from data
		exec = Executors.newCachedThreadPool();
		for (int i = 0; i < input.size(); i++) {
			exec.execute(new FindWords(input.get(i)));
		}
		exec.shutdown();
		exec.awaitTermination(180, TimeUnit.SECONDS);

		// generate ideas
		for (int i = 0; i < numIdeas; i++) {
			genIdeas("VNN");
		}
	}
	
	private static void getInitialUrls() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("urlList.txt"));

		{
			String line;
			while ((line = br.readLine()) != null) {
				if (line.matches("^//")){
					continue;
				}
				URLQuene.add(line);
			}
		}
		br.close();
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
}