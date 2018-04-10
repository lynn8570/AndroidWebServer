package lynn.andr.webserver.fota;

public class ErrorCode {
	public static final int DOWNLOAD_ERROR_SD_NOT_EXIT=101;//SD read write error
	public static final int DOWNLOAD_ERROR_SD_SPACE_LOW=102;//SD space low
	public static final int ERROR_CODE_NULL = 103;//get info return null
	public static final int ERROR_PARAM = 104;//error parameter
	public static final int STATU_OK = 200;

	public static final int ERROR_CODE_NOT_EXIT = 1002;//The product is not exist
	public static final int ERROR_CODE_LEATEST_VERSION = 1004;
	public static final int ERROR_CODE_NOT_CONFIG = 1005;
	public static final int ERROR_CODE_NETWORK_ERROR = 1006;
	public static final int ERROR_CODE_ERROR_STATE = 1007;
}
