package lynn.andr.webserver.http;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;



import android.content.Context;

import lynn.andr.webserver.http.Utils.ParamUtil;
import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.NetUtil;

public class RequestHandler implements HttpRequestHandler {
	public static final String TAG = "RequestHandler";

	protected Context mContext;
	protected PageEntity mPageEntity;

	public RequestHandler(Context context) {
		mContext = context;
		mPageEntity = new PageEntity(context);
	}

	public boolean checkPermission(HttpRequest request,HttpResponse response) {
		Header[] headers = request.getAllHeaders();
		for (int i = 0; i < headers.length; i++) {
			Log.i(TAG, "headers[" + i + "] =" + headers[i]);
			if (headers[i] != null
					&& headers[i].getName().equalsIgnoreCase("Cookie")) {
				if (headers[i].getValue().contains("userID=admin")) {
					response.addHeader("Set-Cookie", "userID=admin; Max-Age="+ NetUtil.COOKIE_TIMEOUT);
					return true;
				}
				// Log.i(TAG, ".getName()" + headers[i].getName());
				// Log.i(TAG, ".getValue()" + headers[i].getValue());
				// HeaderElement[] elements = headers[i].getElements();
				// if (elements != null && elements.length > 0) {
				// for (int j = 0; j < elements.length; j++) {
				// Log.i(TAG,
				// ".elements.getName()" + elements[j].getName());
				// Log.i(TAG,
				// ".elements.getValue()" + elements[j].getValue());
				// }
				//
				// }
			}
		}
		return false;
	}
	
	public String parseTarget(HttpRequest request){
		String target = request.getRequestLine().getUri();
		Log.i(TAG, "target="+target);
		if(target.contains("?")){
			int indexOf = target.indexOf("?");
			target = target.substring(0, indexOf);
		}
		Log.i(TAG, "target="+target);
		return target;
	}
	
	public static Map<String, String> parseParameter(HttpRequest request) throws IOException{
		String method = request.getRequestLine().getMethod()
				.toUpperCase(Locale.ENGLISH);
		String target = request.getRequestLine().getUri();
		Log.i(TAG, "RequestHandler handle method=" + method);
		Log.i(TAG, "RequestHandler handle target=" + target);

		if ("GET".equalsIgnoreCase(method)) {
			return handleGetParam(request);

		} else if ("POST".equalsIgnoreCase(method)) {
			return handlePostParam(request);
		} else {
			return null;
		}
	}
	
//	public boolean responeJson(HttpRequest request)
//	{
//		String target = request.getRequestLine().getUri();
//		Log.i(TAG, "RequestHandler handle target=" + target);
//		if(target==null||target.isEmpty()){
//			return false;
//		}
//		return target.contains("JSON");
//	}

	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {

	}

	public void responeMessage(HttpResponse response,int resid) throws IOException {
		response.setStatusCode(HttpStatus.SC_OK);
		response.setEntity(mPageEntity.getMessagePage(resid));
	}

	public void responeMessage(HttpResponse response,String msg) throws IOException {
		response.setStatusCode(HttpStatus.SC_OK);
		response.setEntity(mPageEntity.getMessagePage(msg));
	}

	public void responeSuccess(HttpResponse response,String ssid,String pwd) throws IOException {
		response.setStatusCode(HttpStatus.SC_OK);
		response.setEntity(mPageEntity.getSuccessPage(ssid,pwd));
	}

	private static Map<String, String> handlePostParam(HttpRequest request) throws IOException {
		Log.d(TAG, "request =" + request);
		if (request instanceof BasicHttpEntityEnclosingRequest) {

			HttpEntity entity = ((BasicHttpEntityEnclosingRequest) request)
					.getEntity();

			byte[] data;
			if (entity == null) {
				data = new byte[0];
			} else {
				data = EntityUtils.toByteArray(entity);
			}
			
			String stringParam = new String(data);
			Log.i(TAG, "handlePostParam:stringParam=" +stringParam );
			//if(stringParam!=null&&stringParam.contains("http")){
			//	stringParam = URLDecoder.decode(stringParam);
			//	Log.i(TAG, "handlePostParam: decodeStr=" +stringParam );
			//}
			return ParamUtil.getpostParam(stringParam);

		}
		return null;
	}

	private static Map<String, String> handleGetParam(HttpRequest request) {
		String target = request.getRequestLine().getUri();
		return ParamUtil.getParam(target);
	}
	
	protected void responeLogin(HttpResponse response) throws IOException{
		response.setStatusCode(HttpStatus.SC_OK);
		response.setEntity(mPageEntity.getLoginPage());
	}
}
