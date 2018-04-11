package lynn.andr.webserver;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import lynn.andr.webserver.http.HttpServer;


public class SocketService extends Service {

    public static final int PORT = 8080;


    @Override
    public IBinder onBind(Intent intent) {

        return new SocketServiceBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }


    public void startHttpServer(OnHttpServerStartListener listener) {
        HttpServer hs = new HttpServer();
        try {
            hs.startServer(PORT, getBaseContext(), listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface OnHttpServerStartListener {
        void onHttpServerStart(String hostIp);
    }

    /**
     * used to communicate with activity, to feedback if the httpserver is started or not
     */
    public class SocketServiceBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }

    }

}
