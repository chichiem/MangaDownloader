import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Chapter implements Runnable{
	private List<Page> chapterPages = new ArrayList<Page>();
	private String chapterName;
	private String chapterUrl;
	private String destinationPath;

	Chapter(String mangaName, String chapterName, String chapterUrl) {
		setChapterName(chapterName);
		setChapterUrl(chapterUrl);
		setDestinationPath(mangaName+"/"+getChapterName());
		setPages();
		
//		System.out.println("Current chapter: "+ getChapterName());

	}

	public String getChapterName() {
		return chapterName;
	}

	public void setChapterName(String chapterName) {
		this.chapterName = chapterName;
	}

	public String getChapterUrl() {
		return chapterUrl;
	}

	public void setChapterUrl(String chapterUrl) {
		String[] url = chapterUrl.split("/");
		List<String> chapterParts = new ArrayList<String>(Arrays.asList(url));
		chapterParts.remove(chapterParts.size() - 1);

		this.chapterUrl = String.join("/", chapterParts);
	}
	
	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}

	public List<Page> getAllPages() {
		return this.chapterPages;
	}

	public void addPage(Page page) {
		this.chapterPages.add(page);
	}
	
	private void setPages() {
		Document doc = null;
		Elements pages = null;
		int retry = 0;
		boolean success = false;

		while (retry < DownloaderConstants.MAX_NO_OF_RETRIES.getValue()) {
			try {
				System.out.println("Downloading Chapter... -"+ getChapterUrl());
				doc = Jsoup.connect(getChapterUrl() + "/1.html").timeout(DownloaderConstants.MAX_TIMEOUT.getValue()).get();
				pages = doc.select("select.m").first().select("option:not(option[value=0]");
				success = true;
				break;
			} catch (SocketTimeoutException ex) {
				System.out.println("retrying... #" + retry + " -" + getChapterUrl());
			} catch (IOException e) {
			}
			retry++;
		}

		if (success) {
			ExecutorService pageList = Executors.newFixedThreadPool(DownloaderConstants.MAX_NO_OF_THREADS.getValue());
			//add to downloader 
			for (Element page : pages) {
				String pageUrl = getChapterUrl() + "/" + page.attr("value") + ".html";
				Page chapterPage = new Page(getDestinationPath(), pageUrl, page.attr("value"));
				System.out.println("Adding Page ... -"+ pageUrl);
				addPage(chapterPage);
//				pageList.execute(chapterPage);
			}
			pageList.shutdown();
			while (!pageList.isTerminated()) {
			}
			System.out.println("Success Chapter... -"+ getChapterUrl());
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		setPages();
	}

}
