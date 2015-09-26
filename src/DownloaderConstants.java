
public enum DownloaderConstants {
	MAX_TIMEOUT(10000),
	MAX_NO_OF_THREADS(16	),
	BYTE_SIZE(4096),
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
