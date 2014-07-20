import java.io.IOException;
import java.io.StringReader;

import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class FindWords extends Thread {

	String input;

	public FindWords(String input) {
		this.input = input;
//		System.out.println(input);
	}

	@Override
	public void run() {
		POSTaggerME tagger = new POSTaggerME(RNGIdeas.model);

		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(input));

		String line;
		try {
			while ((line = lineStream.read()) != null) {
				String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE.tokenize(line);
				String[] tags = tagger.tag(whitespaceTokenizerLine);
				POSSample sample = new POSSample(whitespaceTokenizerLine, tags);

				findWords(sample.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void findWords(String line) {
		String[] chunks = line.split(" ");

		for (String s : chunks) {
			if (s.contains("_NN")) {
				RNGIdeas.nouns.add(getWord(s));
			} else if (s.contains("_VB")) {
				if (!s.equals("is") || !s.equals("are")) {
					synchronized (this) {
						RNGIdeas.verbs.add(getWord(s));
					}
				}
			}
		}
	}

	private static String getWord(String word) {
		int cut = word.indexOf('_');
		char ch = word.charAt(cut - 1);
		if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
			return word.substring(0, cut);
		} else {
			return word.substring(0, cut - 1);
		}
	}

}
