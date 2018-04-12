package lynn.andr.webserver.http.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

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
import lynn.andr.webserver.http.Utils.ResponseWapper;
import lynn.andr.webserver.http.handler.RequestHandler;
import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.SettingUtil;


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
        targetsList.add("/submit");
        targetsList.add("/changepwd");
        targetsList.add("/settotal");
        targetsList.add("/block");
        targetsList.add("/unblock");
        targetsList.add("/chgmax");
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response,
                       HttpContext context) throws HttpException, IOException {

        boolean isLogin = checkUserIdCookie(request);
        if(!isLogin){
            ResponseWapper.wapper(false, new PageEntity(mContext).getMessagePage(R.string.permission_deny), response);
            return;
        }

        //业务分配


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
