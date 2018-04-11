package lynn.andr.webserver.http;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;


import android.content.Context;
import android.content.SharedPreferences;

import lynn.andr.webserver.R;
import lynn.andr.webserver.fota.ErrorCode;
import lynn.andr.webserver.http.Utils.ParamUtil;
import lynn.andr.webserver.http.Utils.ResponseWapper;
import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.NetUtil;
import lynn.andr.webserver.util.SettingUtil;

public class LoginHandler extends RequestHandler {

    private static final String KEY_PWD = "password";
    private static final String KEY_USERNAME = "username";

    public static final int CHECK_USER_OK = 1;
    public static final int CHECK_USER_ERROR_PWD = 2;
    public static final int CHECK_USER_ERROR = 3;

    public LoginHandler(Context context) {
        super(context);

    }

    public int checkUser(HttpRequest request) {
        Map<String, String> parameter;

        parameter = ParamUtil.parseParameter(request);

        if (parameter != null && parameter.get(KEY_PWD) != null && parameter.get(KEY_USERNAME) != null) {
            String pwd = parameter.get(KEY_PWD);
            String username = parameter.get(KEY_USERNAME);
            if (pwd != null && username != null) {
                pwd = pwd.trim();
                username = username.trim();

            }
            Log.i(TAG, "pwd=" + pwd + " username=" + username);
            Log.i(TAG, "curpwd=" + SettingUtil.getPassword(mContext) + " username="
                    + SettingUtil.getUsername(mContext).equals(username));
            if (SettingUtil.getPassword(mContext).equals(pwd) && SettingUtil.getUsername(mContext).equals(username)) {

                return CHECK_USER_OK;
            } else {

                return CHECK_USER_ERROR_PWD;
            }
        }
        return CHECK_USER_ERROR;

    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context)
            throws HttpException, IOException {

        int checkUser = checkUser(request);
        String target = request.getRequestLine().getUri();

        Log.i("linlian", "LoginHandler.continue");

        if (target.contains(".json")) {
            if (checkUser == CHECK_USER_OK) {
                ResponseWapper.wapper(JSONEntity.getMessageEntity(true, ErrorCode.STATU_OK, R.string.json_sucess, mContext), response);
            } else {
                response.setEntity(
                        JSONEntity.getMessageEntity(false, ErrorCode.ERROR_PARAM, R.string.error_pwd, mContext));
            }
            return;
        } else {
            if (checkUser == CHECK_USER_OK) {
                ResponseWapper.wapper(new PageEntity(mContext).getIndexPage(), response);
            } else if (checkUser == CHECK_USER_ERROR_PWD) {
                ResponseWapper.wapper(false, new PageEntity(mContext).getMessagePage(R.string.error_pwd), response);
            } else {
                ResponseWapper.wapper(false, new PageEntity(mContext).getMessagePage(R.string.error), response);
            }

        }
    }

}
