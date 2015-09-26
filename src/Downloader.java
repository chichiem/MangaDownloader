
public class Downloader implements Runnable {

	final int BYTE_SIZE = 4096;
	final int MAX_NO_OF_THREAD = 8;

	String imageUrl = "";
	String destinationFile = "";

	public Downloader(String imageUrl, String destinationFile) {
		this.imageUrl = imageUrl;
		this.destinationFile = destinationFile;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
