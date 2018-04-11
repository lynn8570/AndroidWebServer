package lynn.andr.webserver.http;


import android.content.Context;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpRequestHandler;

import lynn.andr.webserver.util.Log;

public abstract class RequestHandler implements HttpRequestHandler {
    public static final String TAG = "RequestHandler";
    protected Context mContext;

    public RequestHandler(Context context) {
        mContext = context;

    }

    /**
     * http://ip:port +target+？
     *
     * @param request
     * @return 截取 请求中的 /xxxx ,除去问号之后带的参数等
     */
    public String parseTarget(HttpRequest request) {
        String target = request.getRequestLine().getUri();
        Log.i(TAG, "request uri =" + target);
        if (target.contains("?")) {
            int indexOf = target.indexOf("?");
            target = target.substring(0, indexOf);
        }
        Log.i(TAG, "extract target=" + target);
        return target;
    }

    /**
     * @param request
     * @return
     */
    public boolean isRequestNeedUserLogin(HttpRequest request) {

        String target = parseTarget(request);
        return HttpServer.restrictTarget.contains(target);
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

//    @Override
//    public void handle(HttpRequest request, HttpResponse response,
//                       HttpContext context) throws HttpException, IOException {
//
//    }

}
