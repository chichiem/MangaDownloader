import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Testing {
	final static int MAX_TIMEOUT = 10 * 1000;
	final static int MAX_NO_OF_THREAD = 8;

	public static void main(String[] args) {
//		oldTesting();
		Manga manga = new Manga("mangafox.me/manga/fairy_tail/");
		ExecutorService mangaList = Executors.newFixedThreadPool(DownloaderConstants.MAX_NO_OF_THREADS.getValue());
		mangaList.execute(manga);
		mangaList.shutdown();
		while (!mangaList.isTerminated()) {
		}
	}
	
	@SuppressWarnings("unused")
	private static void oldTesting(){
		Document doc;
		try {
			String firstPage = "mangafox.me/manga/fairy_tail/";// v01/c003/";
			doc = Jsoup.connect("http://" + firstPage).timeout(MAX_TIMEOUT)
					.get();

			Elements chapters = doc.select("ul.chlist a.tips");

			ExecutorService executor = Executors.newFixedThreadPool(MAX_NO_OF_THREAD);
			for (int i = chapters.size()-1 ; i >= 0 ; i--){
				Element chapter = chapters.get(i);
				String chapterUrl = chapter.attr("href").replaceFirst("http://", "");
				String[] url = chapterUrl.split("/");
				List<String> chapterParts = new ArrayList<String>(
						Arrays.asList(url));
				chapterParts.remove(chapterParts.size() - 1);

				String newChapterUrl = String.join("/", chapterParts);
				chapterParts.remove(0);
				chapterParts.remove(0);
				String desinationFolder = System.getProperty("user.home") + "/Documents/Manga/"+String.join("/", chapterParts) + "/";
				System.out.println("current chapter: " + desinationFolder + " - "+ newChapterUrl);

				Runnable worker = new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						Document docPages;
						try {
							docPages = Jsoup.connect("http://" + newChapterUrl)
									.timeout(MAX_TIMEOUT).get();
							Elements pages = docPages.select("select.m").first().select("option:not(option[value=0]");
							Mangafox mf = new Mangafox("Fairy Tail");
							List<String> newPages = mf.getChapterPages(
									newChapterUrl, pages);

							File f = new File(desinationFolder);
							if (!f.exists()) {
								f.mkdirs();
							}

							mf.downloadImages(newPages, desinationFolder);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				};

				executor.execute(worker);

			}
			executor.shutdown();
			while (!executor.isTerminated()) {
			}
			System.out.println("Finished all threads");
			System.out
					.println("==========================================================================");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}
}
