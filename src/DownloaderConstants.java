
public enum DownloaderConstants {
	MAX_TIMEOUT(3000),
	MAX_NO_OF_THREADS(3),
	BYTE_SIZE(1024),
	MAX_NO_OF_RETRIES(10)
	;
	
	private final int value;
	
	private DownloaderConstants(int value){
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
