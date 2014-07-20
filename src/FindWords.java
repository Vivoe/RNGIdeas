import java.io.IOException;
import java.io.StringReader;

import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class FindWords {

	public static void tagString(String s){
		POSTaggerME tagger = new POSTaggerME(RNGIdeas.model);

		ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(s));

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

	private static void findWords(String line) {
		String[] chunks = line.split(" ");

		for (String s : chunks) {
			if (s.contains("_NN")) {
				RNGIdeas.nouns.add(getWord(s));
			} else if (s.contains("_VB")) {
				if (!s.equals("is") || !s.equals("are")) {
						RNGIdeas.verbs.add(getWord(s));
				}
			}
		}
	}

	private static String getWord(String word) {
		
		int cut = word.indexOf('_');
		if (cut < 1){
			System.err.println("kiwi");
			return "kiwi";
		}
		char ch = word.charAt(cut - 1);
		if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
			word = word.substring(0, cut);
		} else {
			word = word.substring(0, cut - 1);
		}
		
		if (word.equals("")){
			System.err.println("banana");
			return "banana";
		}
		
		if (word.charAt(0) == '('){
			return word.substring(1);
		} else {
			return word;
		}
	}

}
