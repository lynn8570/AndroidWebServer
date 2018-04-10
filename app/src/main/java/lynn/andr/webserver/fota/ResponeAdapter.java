package lynn.andr.webserver.fota;

import java.io.IOException;

import org.apache.http.HttpResponse;



public interface ResponeAdapter {
	public static final int HANDLING_CHECK=1;
	public static final int HANDLING_DOWNLOAD=2;
	public static final int HANDLING_UPGRADE=3;
	void onMessage(HttpResponse response, int resid, int code)throws IOException;
	void onMessage(HttpResponse response, String msg, int code)throws IOException;
	void inProgressing(HttpResponse response, int type, FotaManager.FotaResult fotaResult)throws IOException;
	void checksuccess(HttpResponse response)throws IOException;
	void downloadfinish(HttpResponse response)throws IOException;
}
