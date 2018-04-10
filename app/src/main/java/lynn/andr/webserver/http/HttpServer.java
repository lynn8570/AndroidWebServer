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
import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.NetUtil;

public class HttpServer {

	private static final String TAG = "HttpServer";

	public static String ipAddress = ""; // requester ip address

	public void startServer(int port, Context context) throws Exception {
		Thread t = new RequestListenerThread(port, context);
		t.setDaemon(false);
		t.start();

	}

	
	static class RequestListenerThread extends Thread {
		private final ServerSocket serversocket;
		private final HttpParams params; 
		private final HttpService httpService;

		public RequestListenerThread(int port, Context context)
				throws IOException {

			this.serversocket = new ServerSocket(port);

			HttpProcessor httpproc = new ImmutableHttpProcessor(
					new HttpResponseInterceptor[] { new ResponseDate(),
							new ResponseServer(), new ResponseContent(),
							new ResponseConnControl() });

			// HttpProcessor httpproc = HttpProcessorBuilder.create()
			// .add(new RequestContent()).add(new RequestTargetHost())
			// .add(new RequestConnControl()).add(new RequestUserAgent())
			// .add(new RequestExpectContinue(true)).build();

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
			LoginHandler loginHandler = new LoginHandler(context);
			ChangePwdHandler changePwdHandler = new ChangePwdHandler(context);
			SubmitHandler submitHandler = new SubmitHandler(context);
			SetTotalHandler setTotalHandler = new SetTotalHandler(context);
			BlockMacHandler blockMacHandler = new BlockMacHandler(context);
			WebServiceHandler webServiceHandler = new WebServiceHandler(context);
			ImagesHandler imagesHandler = new ImagesHandler(context);
			JSONHandler jsonHandler =new JSONHandler(context);
			reqistry.register("/login", loginHandler);
			reqistry.register("/login.json", loginHandler);
			reqistry.register("/submit", submitHandler);
			reqistry.register("/changepwd", changePwdHandler);
			reqistry.register("/changepwd.json", changePwdHandler);
			reqistry.register("/settotal", setTotalHandler);
			reqistry.register("/block", blockMacHandler);
			reqistry.register("/unblock", blockMacHandler);

			MifiPushTest mifipushtest = new MifiPushTest(context);
			reqistry.register("/live/*",mifipushtest);	
			
			reqistry.register("/json/*", jsonHandler);
			reqistry.register("*"+".json", jsonHandler);
			
			reqistry.register("/images/*", imagesHandler);
			reqistry.register("/css/*", imagesHandler);
			reqistry.register("/js/*", imagesHandler);
			reqistry.register("/logout", webServiceHandler);
			reqistry.register("/lang", webServiceHandler);
			reqistry.register("/chgmax", webServiceHandler);
			
			
			reqistry.register("/updateWare.html", webServiceHandler);
			reqistry.register("/index.html", webServiceHandler);
			reqistry.register("/login.html", webServiceHandler);
			reqistry.register("/", webServiceHandler);

			this.httpService = new HttpService(httpproc,
					new DefaultConnectionReuseStrategy(),
					new DefaultHttpResponseFactory());
			httpService.setParams(this.params);
			httpService.setHandlerResolver(reqistry);
			Log.i(TAG, "WebServiceHandler handle set params=" + params);
		}

		@Override
		public void run() {
			Log.i(TAG, "RequestListenerThread start listen port="
					+ this.serversocket.getLocalPort());
			Log.i(TAG,
					"RequestListenerThread start listen port="
							+ NetUtil.getLocalHostIp());
			SocketService.hostip=NetUtil.getLocalHostIp();

			Log.i(TAG,
					"RequestListenerThread start listen Thread.interrupted()="
							+ Thread.interrupted());
			while (!Thread.interrupted()) {
				try {
					Socket socket = this.serversocket.accept();
					DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
					ipAddress = socket.getInetAddress().getHostAddress();
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
