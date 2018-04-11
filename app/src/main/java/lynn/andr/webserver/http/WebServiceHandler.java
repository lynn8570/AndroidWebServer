package lynn.andr.webserver.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;

import android.app.DownloadManager.Request;
import android.content.Context;
import android.text.TextUtils;

import lynn.andr.webserver.R;
import lynn.andr.webserver.http.Utils.ResponseWapper;
import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.NetUtil;
import lynn.andr.webserver.util.SettingUtil;


/**
 * cookie的检查和更新
 * 任务分配
 * 基本响应
 */
public class WebServiceHandler extends RequestHandler {
    private static final String TAG = "WebServiceHandler";


    public WebServiceHandler(Context context) {
        super(context);
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response,
                       HttpContext context) throws HttpException, IOException {

        boolean isLogin = checkUserIdCookie(request);
        if (isLogin) {//dipspatch task

        } else {//never log in or cookies time out
            if (isRequestNeedUserLogin(request)) {//uri need user login state, so response message to Login
                ResponseWapper.wapper(false, new PageEntity(mContext).getMessagePage(R.string.permission_deny), response);
                return;
            } else { //不需要登录即可操作的业务
                if (isChgLang(request)) {
                    changeLang(request);
                    return;
                }
                if (isLogout(request)) {
                    ResponseWapper.wapper(false, new PageEntity(mContext).getLoginPage(), response);
                    return;
                }

                ResponseWapper.wapper(false,new PageEntity(mContext).getLoginPage(),response);

            }
        }


    }

    /**
     * @param request
     * @return
     */
    public boolean checkUserIdCookie(HttpRequest request) {
        Header[] headers = request.getAllHeaders();
        for (int i = 0; i < headers.length; i++) {
            Log.i(TAG, "headers[" + i + "] =" + headers[i]);
            if (headers[i] != null
                    && headers[i].getName().equalsIgnoreCase("Cookie")) {
                if (headers[i].getValue().contains("userID=admin")) {
                    return true;
                }
            }
        }
        return false;
    }


    private void changeLang(HttpRequest request) {
        String target = request.getRequestLine().getUri();
        String curLang = SettingUtil.getCurrentLocal(mContext).getLanguage();
        Log.i(TAG, "curLang =" + curLang);
        Log.i(TAG, "target =" + target);
        if (!target.contains("curLang")) {//chage
            if (curLang.equalsIgnoreCase(Locale.ENGLISH.getLanguage())) {
                Log.i(TAG, "setLocal =" + Locale.CHINA.getLanguage());
                SettingUtil.setLocal(mContext, Locale.CHINA);
            } else {
                Log.i(TAG, "setLocal =" + Locale.ENGLISH.getLanguage());
                SettingUtil.setLocal(mContext, Locale.ENGLISH);
            }

        }
    }

    private boolean requestMatch(HttpRequest request, String match) {
        String target = request.getRequestLine().getUri();
        if (TextUtils.isEmpty(target) || TextUtils.isEmpty(match)) {
            return false;
        }
        if (target.contains(match)) {
            return true;
        }
        return false;
    }

    private boolean isUpdateWare(HttpRequest request) {
        return requestMatch(request, "updateWare");
    }

    private boolean isChgMax(HttpRequest request) {
        return requestMatch(request, "chgmax");
    }

    private boolean isChgLang(HttpRequest request) {

        return requestMatch(request, "lang");
    }

    private boolean isLogout(HttpRequest request) {
        return requestMatch(request, "logout");
    }


}
