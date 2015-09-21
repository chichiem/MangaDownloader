import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;

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
		saveImage(imageUrl, destinationFile);
	}

	public void saveImage(String imgUrl, String destinationFile) {
		InputStream is;
		try {

			URL url = new URL(imgUrl);
			is = url.openStream();

			int fileSize = url.openConnection().getContentLength();

			int partSize = Math
					.round((fileSize / MAX_NO_OF_THREAD) / BYTE_SIZE)
					* BYTE_SIZE;
			
			byte[] b = new byte[BYTE_SIZE];
			int length = 0;

			int startByte = 0;
			int endByte = partSize-1;

			RandomAccessFile raf = new RandomAccessFile(destinationFile, "rw");

			while (endByte < fileSize) {
				raf.seek(startByte);

				System.out.println("writing bytes ... " + destinationFile
						+ " - " + startByte + " -- " + endByte);

				while ((length = is.read(b, 0, BYTE_SIZE)) != -1) {
					raf.write(b, 0, length);
				}
				
				startByte = endByte + 1;
				endByte += partSize;

			}

			is.close();
			raf.close();

			System.out.println("saving images... - Done! " + destinationFile
					+ " - " + fileSize + " -- " + partSize);
			System.out
					.println("==========================================================================");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
