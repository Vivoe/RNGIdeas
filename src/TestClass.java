import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TestClass {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {
		String url = "https://github.com/showcases/web-games";
		try {
			Document doc = Jsoup.connect(url).get();
			Elements list = doc.select("a[href]");
			for (Element l : list) {
				System.out.println(l.text());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
