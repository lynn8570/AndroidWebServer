package lynn.andr.webserver.http.biz;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import lynn.andr.webserver.R;
import lynn.andr.webserver.http.PageEntity;
import lynn.andr.webserver.http.handler.RequestHandler;
import lynn.andr.webserver.http.Utils.ParamUtil;
import lynn.andr.webserver.http.Utils.ResponseWapper;
import lynn.andr.webserver.util.Log;


public class MifiPushTest extends BizExecutor {

    private static final String TAG = "MifiPushTest";
    String param_toekn = "access_token";
    String param_imei = "imei";


    private JSONObject jsonObject;

    @Override
    public int doBiz(HttpRequest request) {

        String target = ParamUtil.parseTarget(request);
        Log.i(TAG, "MifiPushTest target=" + target);

        Map<String, String> parseParameter = ParamUtil.parseParameter(request);
        String token = parseParameter.get(param_toekn);
        String imei = parseParameter.get(param_imei);
        Log.i(TAG, "MifiPushTest token=" + token);
        Log.i(TAG, "MifiPushTest imei=" + imei);
        jsonObject = new JSONObject();

        JSONObject meta = new JSONObject();
        try {
            meta.put("error_type", 1);
            meta.put("code", 200);
            meta.put("error_message", "");
            jsonObject.put("meta", meta);
            if ("/live/mifipush".equalsIgnoreCase(target)) {
                jsonObject
                        .put("data",
                                "rtmp://w.gslb.lecloud.com/live/2016053030000027899?sign=d94c9c747febcb1cd6ae169d9969b331&tm=20160530181702");
            } else {
                jsonObject
                        .put("data", getRtmpdata());
            }

            return EXECUTE_RESULT_SUCCESS;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return EXECUTE_RESULT_ERROR;

    }

    @Override
    public void wrapResponseForJSON(HttpResponse response, int result) throws UnsupportedEncodingException {
        if (result == EXECUTE_RESULT_SUCCESS) {
            ResponseWapper.wapper(new StringEntity(jsonObject.toString()), response);
        }

    }

    @Override
    public void wrapResponseForPAGE(HttpResponse response, int result) throws IOException {

        if (result == EXECUTE_RESULT_SUCCESS) {

            //todo

        } else if (result == EXECUTE_RESULT_ERROR) {

            ResponseWapper.wapper(new PageEntity(mContext).getMessagePage(R.string.error), response);

        }
    }

    private int getRtmpdata() {
        int data = (int) (Math.random() * 4);
        return data;
    }
}
