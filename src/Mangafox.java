import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Mangafox extends Manga {
	Mangafox(String mangaUrl) {
		super(mangaUrl);
		// TODO Auto-generated constructor stub
	}

	final int MAX_NO_OF_THREAD = 8;
	final int NO_OF_RETRIES = 5;
	final int MAX_TIMEOUT = 10 * 1000;

	public void downloadImages(List<String> pages, String destinationFolder) {
		Document doc = null;
		int pageNumber = 0;
		ExecutorService executor = Executors
				.newFixedThreadPool(MAX_NO_OF_THREAD);
		for (String page : pages) {
			int i = 0;
			boolean success = false;

			while (i < NO_OF_RETRIES) {
				try {
					doc = Jsoup.connect("http://" + page).timeout(MAX_TIMEOUT)
							.get();
					pageNumber++;
					success = true;
					break;
				} catch (SocketTimeoutException ex) {
					System.out.println("retrying... #" + i + " -" + page);
				} catch (IOException e) {
				}
				i++;
			}

			if (success) {
				String imgUrl = doc.select("div#viewer > a > img").first()
						.attr("src");
				System.out.println("getting Image url... - " + imgUrl);

				String imageNumber = "";
				if (pageNumber < 10) {
					imageNumber = "00" + pageNumber;
				} else if (pageNumber < 100) {
					imageNumber = "0" + pageNumber;
				} else {
					imageNumber = "" + pageNumber;
				}
//				 Runnable worker = new Downloader(imgUrl, destinationFolder+imageNumber + ".jpg");
//				 executor.execute(worker);
			}

		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		System.out.println("getting Image url... - Done!");
		System.out
				.println("==========================================================================");
	}

	public List<String> getChapterPages(String chapterUrl, Elements pages) {
		List<String> mangafoxPages = new ArrayList<String>();

		for (Element page : pages) {
			mangafoxPages.add(chapterUrl + "/" + page.attr("value") + ".html");
			System.out.println("getting pages... - " + chapterUrl + "/"
					+ page.attr("value") + ".html");
		}
		System.out.println("getting pages... - Done!");
		System.out
				.println("==========================================================================");
		return mangafoxPages;
	}

}
