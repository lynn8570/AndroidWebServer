package lynn.andr.webserver.http.biz;

import android.content.Context;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.Map;

import lynn.andr.webserver.R;
import lynn.andr.webserver.http.PageEntity;
import lynn.andr.webserver.http.handler.RequestHandler;
import lynn.andr.webserver.http.Utils.ParamUtil;
import lynn.andr.webserver.http.Utils.ResponseWapper;
import lynn.andr.webserver.util.Log;


public class BlockMacExecutor extends BizExecutor {

    private static final String TAG = "BlockMacHandler";
    private static final String KEY_TOTAL = "total";

    private static final String TAG_BLOCK = "/block";
    private static final String TAG_UNBLOCK = "/unblock";
    public static final String KEY_MAC = "macaddress";

    public BlockMacExecutor(Context context) {
        super(context);
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) throws HttpException, IOException {
//
//		if (!checkAndUpdateCookie(request, response)) {
////			responeMessage(response, R.string.permission_deny);
//			responeLogin(response);
//			return;
//		}

        String target = request.getRequestLine().getUri();
        String mac = getMac(request);

        if (target == null || target.isEmpty() || mac == null || mac.isEmpty()) {
            ResponseWapper.wapper(new PageEntity(mContext).getMessagePage(R.string.error), response);
            return;
        }
        boolean result = false;
        if (target.startsWith(TAG_BLOCK)) {
//			result = NetUtil.add2BlackList(mac, mContext);

        } else if (target.startsWith(TAG_UNBLOCK)) {
//			result =NetUtil.deleteFromBlackList(mac,mContext);
        }

        if (result) {

            ResponseWapper.wapper(new PageEntity(mContext).getIndexPage(), response);

        } else {
            ResponseWapper.wapper(new PageEntity(mContext).getMessagePage(R.string.error), response);
            return;
        }

    }

    public static String getMac(HttpRequest request) throws IOException {
        Map<String, String> parameter = ParamUtil.parseParameter(request);
        Log.i(TAG, " parameter=" + parameter);
        if (parameter == null) {
            Log.i(TAG, " parameter=" + parameter);
            return null;
        }
        String mac = parameter.get(KEY_MAC);
        Log.i(TAG, " mac" + mac);
        return mac;
    }

}
