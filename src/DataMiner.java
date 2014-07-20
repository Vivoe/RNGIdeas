import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DataMiner {

	public static Stream<String> getData(String url) {
		ArrayList<String> newLinks = new ArrayList<>();
		try {
			Document doc = Jsoup.connect(url).get();
			Elements list = doc.select("div.markdown-body, p.repo-description, a[href]");

			for (Element thing : list) {

				if (thing.tagName().equals("a")) {
					String newLink = thing.attr("href");
					try{
					if (newLink.charAt(0) == '/') {

						if (RNGIdeas.visitedURLs.containsKey(newLink)) {
							continue;
						} else {
							RNGIdeas.visitedURLs.put(newLink, true);
						}

						newLinks.add("https://github.com" + newLink);
					}
					} catch (StringIndexOutOfBoundsException e){
						System.err.println("out of bounds "+ newLink);
					}
				} else {
					RNGIdeas.input.add(thing.text());
				}
			}
		} catch (IOException e) {
			System.err.println("Unsupported page "+ url);
		}
		
		return newLinks.stream();
	}

}