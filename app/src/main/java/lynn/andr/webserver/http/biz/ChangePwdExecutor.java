package lynn.andr.webserver.http.biz;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;


import android.content.Context;

import lynn.andr.webserver.R;
import lynn.andr.webserver.http.PageEntity;
import lynn.andr.webserver.http.handler.RequestHandler;
import lynn.andr.webserver.http.Utils.ParamUtil;
import lynn.andr.webserver.http.Utils.ResponseWapper;
import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.SettingUtil;


public class ChangePwdExecutor extends BizExecutor {

    private static final String TAG = "ChangePwdExecutor";

    private static final String KEY_OLD_PWD = "oldpwd";
    private static final String KEY_NEW_PWD = "newpwd";
    private static final String KEY_NEW_USERNAME = "username";


    @Override
    public int doBiz(HttpRequest request) {

        Map<String, String> parameter = ParamUtil.parseParameter(request);
        String newPwd = parameter.get(KEY_NEW_PWD);
        String oldPwd = parameter.get(KEY_OLD_PWD);
        String username = parameter.get(KEY_NEW_USERNAME);
        Log.i(TAG, "new newPwd=" + newPwd);
        Log.i(TAG, "new password=" + oldPwd);
        String curPwd = SettingUtil.getPassword(mContext);
        Log.i(TAG, "new curPwd=" + curPwd);
        String curUsername = SettingUtil.getUsername(mContext);
        Log.i(TAG, "new password=" + oldPwd);
        if (curPwd.equals(oldPwd)) {
            if (newPwd != null && !newPwd.isEmpty()
                    && username != null && !username.isEmpty()) {
                SettingUtil.setAdmin(mContext, newPwd, username);

                return EXECUTE_RESULT_SUCCESS;
            } else {
                return EXECUTE_RESULT_BAD_INPUT;
            }

        } else {
            //responeMessage(response, R.string.error_pwd);
            //responeChangePwd(request,response,false,R.string.error_pwd,ErrorCode.ERROR_CODE_NULL);
            return EXECUTE_RESULT_BAD_INPUT;
        }


    }


    @Override
    public void wrapResponseForJSON(HttpResponse response, int result) {

    }

    @Override
    public void wrapResponseForPAGE(HttpResponse response, int result) throws IOException {
        if (result == EXECUTE_RESULT_SUCCESS) {
            ResponseWapper.wapper(false, new PageEntity(mContext).getMessagePage(R.string.modify_pwd_success), response);
        } else if (result == EXECUTE_RESULT_BAD_INPUT) {
            ResponseWapper.wapper(false, new PageEntity(mContext).getMessagePage(R.string.error_pwd), response);
        }


    }


}
