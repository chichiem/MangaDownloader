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

public class Manga implements Runnable{
	public List<Chapter> mangaChapters = new ArrayList<Chapter>();
	private String mangaName;
	private String mangaUrl;
	private String mangaCoverImage;

	Manga(String mangaUrl) {
		this.setMangaUrl(mangaUrl);
	}

	public String getMangaName() {
		return mangaName;
	}

	public void setMangaName(String mangaName) {
		this.mangaName = mangaName;
	}

	public String getMangaUrl() {
		return mangaUrl;
	}

	public void setMangaUrl(String mangaUrl) {
		this.mangaUrl = "http://"+mangaUrl;
	}
	
	public String getMangaCoverImage() {
		return mangaCoverImage;
	}

	public void setMangaCoverImage(String mangaCoverImage) {
		this.mangaCoverImage = mangaCoverImage;
	}
	
	public List<Chapter> getAllChapters(){
		return this.mangaChapters;
	}
	
	public void addChapter(Chapter chapter){
		this.mangaChapters.add(chapter);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		setChapters();
	}
	
	public void setChapters(){
		Document doc = null;
		Element mangaCover = null;
		int retry = 0;
		boolean success = false;

		while (retry < DownloaderConstants.MAX_NO_OF_RETRIES.getValue()) {
			try {
				System.out.println("Downloading... -"+ getMangaUrl());
				doc = Jsoup.connect(getMangaUrl()).timeout(DownloaderConstants.MAX_TIMEOUT.getValue()).get();
				mangaCover = doc.select("div.cover > img").first();
				success = true;
				break;
			} catch (SocketTimeoutException ex) {
				System.out.println("retrying... #" + retry + " -" + getMangaUrl());
			} catch (IOException e) {
			}
			retry++;
		}

		if (success) {
			System.out.println("Success... -"+ getMangaUrl());
			// set manga properties
			setMangaName(mangaCover.attr("alt"));
			setMangaCoverImage(mangaCover.attr("src"));
			
			// set chapter properties
			Elements chapters = doc.select("ul.chlist a.tips");
			ExecutorService chapterList = Executors.newFixedThreadPool(DownloaderConstants.MAX_NO_OF_THREADS.getValue());
			for (int i = chapters.size()-1 ; i >= 0 ; i--){
				Element chapter = chapters.get(i);
				String chapterUrl = chapter.attr("href");
				String chapterTitle = chapter.text()+ " - " +chapter.siblingElements().select(".title").first().text();
				Chapter mangaChapter = new Chapter(getMangaName(),chapterTitle,chapterUrl);
				addChapter(mangaChapter);
				chapterList.execute(mangaChapter);
			}
			chapterList.shutdown();
			while (!chapterList.isTerminated()) {
			}
		}
	}

}
