package lynn.andr.webserver.http.Utils;

import android.text.TextUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lynn.andr.webserver.util.Log;

/**
 * Created by zowee-laisc on 2018/4/11.
 */

public class ParamUtil {
    public static final String TAG = "ParamUtil";

    /**
     * http://ip:port +target+？
     *
     * @param request
     * @return 截取 请求中的 /xxxx ,除去问号之后带的参数等
     */
    public static String parseTarget(HttpRequest request) {
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
     * 从get请求中获取 key-value 键值对参数
     *
     * @param uri
     * @return
     */
    private static Map<String, String> getParamFromUri(String uri) {
        Map<String, String> params = new HashMap<String, String>();
        try {
            if (!TextUtils.isEmpty(uri)) {
                String subStr = uri.substring(uri.indexOf("?") + 1);
                String[] ary = subStr.split("&");
                for (int i = 0; i < ary.length; i++) {
                    String[] temp = ary[i].split("=");
                    if (temp.length < 2) {
                        params.put(temp[0], "");
                    } else {
                        params.put(temp[0], temp[1]);
                    }

                }
                return params;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static Map<String, String> parseParameter(HttpRequest request) {
        String method = request.getRequestLine().getMethod()
                .toUpperCase(Locale.ENGLISH);
        String target = request.getRequestLine().getUri();
        Log.i(TAG, "RequestHandler handle method=" + method);
        Log.i(TAG, "RequestHandler handle target=" + target);

        if ("GET".equalsIgnoreCase(method)) {
            return getGetParamFromRequest(request);

        } else if ("POST".equalsIgnoreCase(method)) {
            return getPostParamFromRequest(request);
        } else {
            return null;
        }
    }

    private static Map<String, String> getpostParam(String str) {
        Log.i(TAG, "getpostParam str=" + str);
        Map<String, String> params = new HashMap<String, String>();
        String strSplitPatter = "\r\n";
        if (str.contains("&")) {
            strSplitPatter = "&";
        } else if (str.contains(",")) {
            strSplitPatter = ",";
        }
        String[] split = str.split(strSplitPatter);

        for (int i = 0; i < split.length; i++) {
            Log.i(TAG, "getpostParam split[i]=" + split[i]);

            String[] pair = split[i].split("=");
            if (pair.length < 2) {
                params.put(pair[0].trim(), "");
            } else {
                Log.i(TAG, "handlePostParam: pair[1]=" + pair[1]);
                String decodeStr = URLDecoder.decode(pair[1]);
                Log.i(TAG, "handlePostParam: decodeStr=" + decodeStr);
                params.put(pair[0].trim(), decodeStr);
            }
        }
        Log.i(TAG, "getpostParam params=" + params);
        return params;
    }

    public static Map<String, String> getPostParamFromRequest(HttpRequest request) {
        Log.d(TAG, "request =" + request);
        if (request instanceof BasicHttpEntityEnclosingRequest) {
            HttpEntity entity = ((BasicHttpEntityEnclosingRequest) request)
                    .getEntity();

            byte[] data = new byte[0];
            ;
            if (entity == null) {
                return null;
            } else {
                try {
                    data = EntityUtils.toByteArray(entity);
                    String stringParam = new String(data);
                    Log.i(TAG, "handlePostParam:stringParam=" + stringParam);
                    //if(stringParam!=null&&stringParam.contains("http")){
                    //	stringParam = URLDecoder.decode(stringParam);
                    //	Log.i(TAG, "handlePostParam: decodeStr=" +stringParam );
                    //}
                    return ParamUtil.getpostParam(stringParam);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Map<String, String> getGetParamFromRequest(HttpRequest request) {
        String uri = request.getRequestLine().getUri();
        return ParamUtil.getParamFromUri(uri);
    }

}
