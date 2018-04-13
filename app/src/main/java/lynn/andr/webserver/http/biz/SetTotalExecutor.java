package lynn.andr.webserver.http.biz;

import android.content.Context;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.Map;

import lynn.andr.webserver.R;
import lynn.andr.webserver.http.PageEntity;
import lynn.andr.webserver.http.handler.RequestHandler;
import lynn.andr.webserver.http.Utils.ParamUtil;
import lynn.andr.webserver.http.Utils.ResponseWapper;
import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.SettingUtil;


public class SetTotalExecutor extends BizExecutor {

    private static final String TAG = "SetTotalExecutor";
    private static final String KEY_TOTAL = "total";
    private static final String KEY_LIMIT = "limit";
    private static final String KEY_CALI = "calibration";



    @Override
    public int doBiz(HttpRequest request) {

//		if(!checkAndUpdateCookie(request, response)){
//			responeMessage(response, R.string.permission_deny);
//			return;
//		}

        Map<String, String> parameter = ParamUtil.parseParameter(request);
        String total = parameter.get(KEY_TOTAL);
        String limittype = parameter.get(KEY_LIMIT);
        String cali = parameter.get(KEY_CALI);
        Log.i(TAG, "total=" + total);
        Log.i(TAG, "limittype=" + limittype);
        Log.i(TAG, "cali=" + cali);
        Integer integerTotal;
        Integer integerLimitType;
        Integer integerCali;
        try {

            integerTotal = Integer.parseInt(total);
            if (integerTotal > 0) {
                Log.i(TAG, "setDataUsageTotal longTotal=" + integerTotal);
                SettingUtil.setDataUsageTotal(mContext, integerTotal * 1024);
            }

            integerLimitType = Integer.parseInt(limittype);
            if (integerLimitType > 0 && integerLimitType < 4) {
                Log.i(TAG, "setDataUsageTotal limittype=" + integerLimitType);
                SettingUtil.setLimitType(mContext, integerLimitType);
            }

            integerCali = Integer.parseInt(cali);
            if (integerCali >= 0 && integerCali <= 7) {
                Log.i(TAG, "setDataUsageTotal setCali=" + integerCali);
                SettingUtil.setCali(mContext, integerCali);
            }

            return EXECUTE_RESULT_SUCCESS;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return EXECUTE_RESULT_ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            return EXECUTE_RESULT_BAD_INPUT;
        }

    }

    @Override
    public void wrapResponseForJSON(HttpResponse response, int result) {

    }

    @Override
    public void wrapResponseForPAGE(HttpResponse response, int result) throws IOException {
        if (result == EXECUTE_RESULT_SUCCESS) {
            ResponseWapper.wapper(new PageEntity(mContext).getMessagePage(R.string.success_setting), response);
        } else if (result == EXECUTE_RESULT_ERROR) {
            ResponseWapper.wapper(new PageEntity(mContext).getMessagePage(R.string.error_input), response);
        } else if (result == EXECUTE_RESULT_BAD_INPUT) {
            ResponseWapper.wapper(new PageEntity(mContext).getMessagePage(R.string.error), response);
        }
    }


}
