package lynn.andr.webserver;



import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import lynn.andr.webserver.http.HttpServer;
import lynn.andr.webserver.util.Log;

public class SocketService extends Service {

	public static final int PORT = 8080;
	public static String hostip;
	
	private static final String ACTION_F2_DOWN="com.zowee.action.F2_DOWN";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i("SocketService", "onCreate");
		
		
		HttpServer hs = new HttpServer();

		try {
			hs.startServer(PORT, getBaseContext());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
