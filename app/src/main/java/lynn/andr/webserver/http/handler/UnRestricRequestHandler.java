package lynn.andr.webserver.http.handler;

import android.content.Context;
import android.text.TextUtils;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandlerRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import lynn.andr.webserver.R;
import lynn.andr.webserver.http.PageEntity;
import lynn.andr.webserver.http.Utils.ResponseWapper;
import lynn.andr.webserver.http.handler.RequestHandler;
import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.SettingUtil;


/**
 * 处理无需用户登录，就可以操作的请求
 */
public class UnRestricRequestHandler extends RequestHandler {
    private static final String TAG = "RestrictRequestHandler";

    public UnRestricRequestHandler(Context context, HttpRequestHandlerRegistry registry) {
        super(context, registry);
    }

    @Override
    public void initTargetList() {
        targetsList.add("/");
        targetsList.add("/login");
        targetsList.add("/submit");
        targetsList.add("/logout");
        targetsList.add("/lang");
        targetsList.add("/logout");

    }

    @Override
    public void handle(HttpRequest request, HttpResponse response,
                       HttpContext context) throws HttpException, IOException {


        if (isChgLang(request)) {
            changeLang(request);
            ResponseWapper.wapper(false, new PageEntity(mContext).getLoginPage(), response);
            return;
        }
        if (isLogout(request)) {
            ResponseWapper.wapper(false, new PageEntity(mContext).getLoginPage(), response);
            return;
        }

        ResponseWapper.wapper(false, new PageEntity(mContext).getLoginPage(), response);


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
