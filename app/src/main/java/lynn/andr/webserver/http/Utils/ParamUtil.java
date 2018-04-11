package lynn.andr.webserver.http.Utils;

import android.text.TextUtils;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import lynn.andr.webserver.util.Log;

/**
 * Created by zowee-laisc on 2018/4/11.
 */

public class ParamUtil {
    public static final String TAG = "ParamUtil";

    public static Map<String, String> getParam(String uri) {
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


    public static Map<String, String> getpostParam(String str) {
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
}
