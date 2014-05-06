import java.io.IOException;
import java.util.TreeMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class DataMiner extends Thread {

	private String url;
	static TreeMap<String, Boolean> visitedURLs = new TreeMap<String, Boolean>();

	public DataMiner(String url) {
		this.url = url;
		// System.out.println(url);
//		System.out.println(visitedURLs.toString());
	}

	@Override
	public void run() {

		try {
			Document doc = getDoc(url);
//			System.out.println("retrieved " + url);
			Elements list = doc.select("div.markdown-body, p.repo-description, a[href]");

			for (Element thing : list) {

				if (thing.tagName().equals("a")) {
					String newLink = thing.attr("href");
					if (newLink.charAt(0) == '/') {

						synchronized (this) {
							if (visitedURLs.containsKey(newLink)) {
								continue;
							} else {
								visitedURLs.put(newLink, true);
							}
						}

						RNGIdeas.URLQuene.add("https://github.com" + newLink);
					}
				} else {

					synchronized (this) {
						RNGIdeas.input.add(thing.text());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static synchronized Document getDoc(String url) throws IOException {
//		System.out.println("Fetching " + url);
		return Jsoup.connect(url).get();
	}
}