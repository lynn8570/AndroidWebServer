package lynn.andr.webserver.http.biz;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

import android.net.wifi.WifiConfiguration;
import android.content.Context;

import lynn.andr.webserver.R;
import lynn.andr.webserver.http.PageEntity;
import lynn.andr.webserver.http.handler.RequestHandler;
import lynn.andr.webserver.http.Utils.ParamUtil;
import lynn.andr.webserver.http.Utils.ResponseWapper;
import lynn.andr.webserver.util.Log;


public class SubmitExecutor extends BizExecutor {

    private static final String TAG = "SubmitExecutor";

    // security=2, password=12345678, SSID=AndroidAP


    private  WifiConfiguration config;
    @Override
    public int doBiz(HttpRequest request) {

       config = getConfig(request);
        Log.i(TAG, "config=" + config);
        if (config != null) {

//			NetUtil.startSetWifi(mContext,config); to set ap settings
            return EXECUTE_RESULT_SUCCESS;

        } else {
            return EXECUTE_RESULT_ERROR;
        }

    }

    @Override
    public void wrapResponseForJSON(HttpResponse response, int result) {

    }

    @Override
    public void wrapResponseForPAGE(HttpResponse response, int result) throws IOException {
        if (result == EXECUTE_RESULT_SUCCESS) {
            ResponseWapper.wapper(false, new PageEntity(mContext).getSuccessPage(config.SSID, config.preSharedKey), response);
        } else if (result == EXECUTE_RESULT_ERROR) {
            ResponseWapper.wapper(false, new PageEntity(mContext).getMessagePage(R.string.error), response);
        }
    }


    private WifiConfiguration getConfig(HttpRequest request) {
        Map<String, String> parameter = null;
        try {
            parameter = ParamUtil.parseParameter(request);
//			return NetUtil.getConfig(parameter);
            return null;// todo return WifiConfiguration
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
