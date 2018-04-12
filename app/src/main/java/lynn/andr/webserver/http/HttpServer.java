package lynn.andr.webserver.http;

import android.content.Context;


import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpServerConnection;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

import lynn.andr.webserver.SocketService;
import lynn.andr.webserver.http.handler.AssetsHandler;
import lynn.andr.webserver.http.handler.JSONHandler;
import lynn.andr.webserver.http.handler.RestrictRequestHandler;
import lynn.andr.webserver.http.handler.UnRestricRequestHandler;
import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.NetUtil;

public class HttpServer {

    private static final String TAG = "HttpServer";


    public void startServer(int port, Context context, SocketService.OnHttpServerStartListener listener) throws Exception {
        Thread t = new RequestListenerThread(port, context, listener);
        t.setDaemon(false);
        t.start();

    }


    static class RequestListenerThread extends Thread {
        private final ServerSocket serversocket;
        private final HttpParams params;
        private final HttpService httpService;
        private SocketService.OnHttpServerStartListener listener;

        public RequestListenerThread(int port, Context context, SocketService.OnHttpServerStartListener listener)
                throws IOException {

            this.serversocket = new ServerSocket(port);
            this.listener = listener;

            HttpProcessor httpproc = new ImmutableHttpProcessor(
                    new HttpResponseInterceptor[]{new ResponseDate(),
                            new ResponseServer(), new ResponseContent(),
                            new ResponseConnControl()});


            this.params = new BasicHttpParams();
            this.params
                    .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
                    .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE,
                            8 * 1024)
                    .setBooleanParameter(
                            CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
                    .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
                    .setParameter(CoreProtocolPNames.ORIGIN_SERVER,
                            "HttpComponents/1.1");

            HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();

            //all target register to RestrictRequestHandler
            new RestrictRequestHandler(context, reqistry);
            new UnRestricRequestHandler(context, reqistry);
            new JSONHandler(context, reqistry);
            new AssetsHandler(context, reqistry);


            this.httpService = new HttpService(httpproc,
                    new DefaultConnectionReuseStrategy(),
                    new DefaultHttpResponseFactory());
            httpService.setParams(this.params);
            httpService.setHandlerResolver(reqistry);
            Log.i(TAG, "RestrictRequestHandler handle set params=" + params);
        }

        @Override
        public void run() {
            Log.i(TAG, "RequestListenerThread start listen port="
                    + this.serversocket.getLocalPort());
            Log.i(TAG,
                    "RequestListenerThread start listen port="
                            + NetUtil.getLocalHostIp());
            //SocketService.mStrHostIp = NetUtil.getLocalHostIp();
            if (listener != null) {
                listener.onHttpServerStart(NetUtil.getLocalHostIp());
            }

            Log.i(TAG,
                    "RequestListenerThread start listen Thread.interrupted()="
                            + Thread.interrupted());
            while (!Thread.interrupted()) {
                try {
                    Socket socket = this.serversocket.accept();
                    DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                    //ipAddress = socket.getInetAddress().getHostAddress();
                    Log.i(TAG,
                            "Incoming connection from "
                                    + socket.getInetAddress());
                    conn.bind(socket, this.params);
                    // 开启工作线程
                    Thread t = new WorkerThread(this.httpService, conn);
                    t.setDaemon(true);
                    t.start();
                } catch (InterruptedIOException ex) {
                    Log.e(TAG, "InterruptedIOException=" + ex);
                    break;
                } catch (IOException e) {
                    Log.e(TAG,
                            "I/O error initialising connection thread: "
                                    + e.getMessage());
                    break;
                }
            }

        }

        static class WorkerThread extends Thread {

            private final HttpService httpservice;
            private final HttpServerConnection conn;

            public WorkerThread(final HttpService httpservice,
                                final HttpServerConnection conn) {
                super();
                this.httpservice = httpservice;
                this.conn = conn;
            }

            @Override
            public void run() {
                Log.i(TAG, "New WorkerThread thread");
                HttpContext context = new BasicHttpContext(null);
                try {
                    while (!Thread.interrupted() && this.conn.isOpen()) {
                        this.httpservice.handleRequest(this.conn, context);
                    }
                } catch (ConnectionClosedException ex) {
                    Log.e(TAG, "Client closed connection");
                } catch (IOException ex) {
                    Log.e(TAG, "I/O error: " + ex.getMessage());
                } catch (HttpException ex) {
                    Log.e(TAG,
                            "Unrecoverable HTTP protocol violation: "
                                    + ex.getMessage());
                } finally {
                    try {
                        this.conn.shutdown();
                    } catch (IOException ignore) {
                    }
                }
            }
        }

    }
}
