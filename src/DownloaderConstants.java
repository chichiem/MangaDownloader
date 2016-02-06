
public enum DownloaderConstants {
	MAX_TIMEOUT(3000),
	MAX_NO_OF_THREADS(8),
	BYTE_SIZE(2048),
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
