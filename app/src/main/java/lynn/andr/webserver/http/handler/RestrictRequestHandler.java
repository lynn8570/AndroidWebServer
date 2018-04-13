package lynn.andr.webserver.http.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executor;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandlerRegistry;

import android.content.Context;
import android.text.TextUtils;

import lynn.andr.webserver.R;
import lynn.andr.webserver.http.PageEntity;
import lynn.andr.webserver.http.Utils.ParamUtil;
import lynn.andr.webserver.http.Utils.ResponseWapper;
import lynn.andr.webserver.http.biz.BizExecutor;
import lynn.andr.webserver.http.handler.RequestHandler;
import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.SettingUtil;

import static lynn.andr.webserver.http.biz.BizExecutor.RETURN_TYPE_PAGE;


/**
 * 处理需要用户登录后才能操作的请求
 */
public class RestrictRequestHandler extends RequestHandler {
    private static final String TAG = "RestrictRequestHandler";


    public RestrictRequestHandler(Context context, HttpRequestHandlerRegistry registry) {
        super(context, registry);
    }

    @Override
    public void initTargetList() {
        mTargetsList.add("/submit");
        mTargetsList.add("/changepwd");
        mTargetsList.add("/settotal");
        mTargetsList.add("/block");
        mTargetsList.add("/unblock");
        mTargetsList.add("/chgmax");
        mTargetsList.add("/index.html");
    }

    @Override
    public void initTargetMap() {

    }

    @Override
    public void handle(HttpRequest request, HttpResponse response,
                       HttpContext context) throws HttpException, IOException {

        boolean isLogin = checkUserIdCookie(request);
        if (!isLogin) {
            ResponseWapper.wapper(false, new PageEntity(mContext).getMessagePage(R.string.permission_deny), response);
            return;
        }

        //业务分配，通过target找到合适的业务执行者
        String target = ParamUtil.parseTarget(request);
        BizExecutor bizExecutor = getBizExecutor(target, BizExecutor.RETURN_TYPE_PAGE);
        if (bizExecutor != null) {
            bizExecutor.handle(request, response);
            return;
        }


        //default return index page
        ResponseWapper.wapper(true, new PageEntity(mContext).getIndexPage(), response);
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


}
