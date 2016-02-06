import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Page implements Runnable {
	private String imageUrl;
	private String pageNumber;
	private String destinationPath;
	private String imageLocation;

	Page(String destinationPath, String imageUrl, String pageNumber) {
		setImage(imageUrl);
		setPageNumber(pageNumber);
		setDestinationPath(destinationPath);
		System.out.println("Current page: "+getPageNumber()+" - "+getImageURL());
	}

	public String getImageURL() {
		return imageUrl;
	}

	public void setImage(String imageUrl) {
		Document doc = null;
		String imgUrl = "";
		int retry = 0;
		boolean success = false;

		while (retry < DownloaderConstants.MAX_NO_OF_RETRIES.getValue()) {
			try {
				doc = Jsoup.connect(imageUrl)
						.timeout(DownloaderConstants.MAX_TIMEOUT.getValue())
						.get();
				
//				System.out.println(doc.html());
				imgUrl = doc.select("div#viewer a > img").first().attr("src");

				success = true;
				break;
			} catch (SocketTimeoutException ex) {
				System.out.println("retrying... #" + retry + " -" + imageUrl);
			} catch (IOException e) {
			}
			retry++;
		}

		if (success) {
			this.imageUrl = imgUrl;
		}
	}

	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(String pageNumber) {
		int pgNumber = Integer.parseInt(pageNumber);

		if (pgNumber < 10) {
			pageNumber = "00" + pageNumber;
		} else if (pgNumber < 100) {
			pageNumber = "0" + pageNumber;
		} else {
			pageNumber = "" + pageNumber;
		}

		this.pageNumber = pageNumber;
	}

	public String getDestinationPath() {
		return destinationPath;
	}

	public void setDestinationPath(String destinationPath) {
		this.destinationPath = System.getProperty("user.home")
				+ "/Documents/Manga/" + destinationPath;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		File f = new File(getDestinationPath());
		if (!f.exists()) {
			f.mkdirs();
		}
		
		setImageLocation(getDestinationPath() + "/" + getPageNumber()+ ".jpg");

		saveImage();
	}

	public void saveImage() {
		InputStream is;
		boolean downloaded = false;
		while(downloaded == false){
			try {
				System.out.println("Downloading Page... -"+ getImageURL());
				URL url = new URL(getImageURL());
				is = url.openStream();

				int fileSize = url.openConnection().getContentLength();

				int partSize = Math
						.round((fileSize / DownloaderConstants.MAX_NO_OF_THREADS
								.getValue())
								/ DownloaderConstants.BYTE_SIZE.getValue())
						* DownloaderConstants.BYTE_SIZE.getValue();

				byte[] b = new byte[DownloaderConstants.BYTE_SIZE.getValue()];
				int length = 0;

				int startByte = 0;
				int endByte = partSize - 1;

				RandomAccessFile raf = new RandomAccessFile(getImageLocation(), "rw");

				if (raf.length() < fileSize) {

					while (endByte < fileSize && endByte != -1) {
						raf.seek(startByte);

						while ((length = is.read(b, 0,
								DownloaderConstants.BYTE_SIZE.getValue())) != -1) {
							raf.write(b, 0, length);
						}

						startByte = endByte + 1;
						endByte += partSize;

					}

					is.close();
					raf.close();

					System.out.println("saving images... - Done! "+ getImageLocation() + " - " + fileSize + " -- "+ partSize);
				} else {
					System.out.println("image Exists... - Skipping! "
							+ getImageLocation() + " - " + fileSize + " -- "
							+ partSize);
				}

				System.out
						.println("==========================================================================");
				downloaded = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				downloaded = false;
			}
		}
	}

	public String getImageLocation() {
		return imageLocation;
	}

	public void setImageLocation(String imageLocation) {
		this.imageLocation = imageLocation;
	}

}
