package lynn.andr.webserver.http.biz;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;


import android.content.Context;

import lynn.andr.webserver.R;
import lynn.andr.webserver.fota.ErrorCode;
import lynn.andr.webserver.http.JSONEntity;
import lynn.andr.webserver.http.PageEntity;
import lynn.andr.webserver.http.handler.RequestHandler;
import lynn.andr.webserver.http.Utils.ParamUtil;
import lynn.andr.webserver.http.Utils.ResponseWapper;
import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.SettingUtil;

public class LoginExecutor extends BizExecutor {

    private static final String KEY_PWD = "password";
    private static final String KEY_USERNAME = "username";

    public static final int CHECK_USER_OK = 1;
    public static final int CHECK_USER_ERROR_PWD = 2;
    public static final int CHECK_USER_ERROR = 3;


    public int checkUser(HttpRequest request) {
        Map<String, String> parameter = ParamUtil.parseParameter(request);
        if (parameter != null && parameter.get(KEY_PWD) != null && parameter.get(KEY_USERNAME) != null) {
            String pwd = parameter.get(KEY_PWD);
            String username = parameter.get(KEY_USERNAME);
            if (pwd != null && username != null) {
                pwd = pwd.trim();
                username = username.trim();

            }
            if (SettingUtil.getPassword(mContext).equals(pwd) && SettingUtil.getUsername(mContext).equals(username)) {
                return CHECK_USER_OK;
            } else {
                return CHECK_USER_ERROR_PWD;
            }
        }
        return CHECK_USER_ERROR;

    }

    @Override
    public int doBiz(HttpRequest request) {

        int checkUser = checkUser(request);

        if (checkUser == CHECK_USER_OK) {
            return EXECUTE_RESULT_SUCCESS;
        } else if (checkUser == CHECK_USER_ERROR_PWD) {
            return EXECUTE_RESULT_BAD_INPUT;
        } else {
            return EXECUTE_RESULT_ERROR;
        }


    }

    @Override
    public void wrapResponseForJSON(HttpResponse response, int result) {
        if (result == EXECUTE_RESULT_SUCCESS) {
            ResponseWapper.wapper(JSONEntity.getMessageEntity(true, ErrorCode.STATU_OK, R.string.json_sucess, mContext), response);
        } else {
            ResponseWapper.wapper(false, JSONEntity.getMessageEntity(false, ErrorCode.ERROR_PARAM, R.string.error_pwd, mContext),
                    response);
        }
    }


    @Override
    public void wrapResponseForPAGE(HttpResponse response, int result) throws IOException {
        if (result == EXECUTE_RESULT_SUCCESS) {
            ResponseWapper.wapper(new PageEntity(mContext).getIndexPage(), response);
        } else if (result == EXECUTE_RESULT_BAD_INPUT) {
            ResponseWapper.wapper(false, new PageEntity(mContext).getMessagePage(R.string.error_pwd), response);
        } else {
            ResponseWapper.wapper(false, new PageEntity(mContext).getMessagePage(R.string.error), response);
        }

    }

}
