package lynn.andr.webserver.http.handler;

import android.content.Context;
import android.text.TextUtils;

import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lynn.andr.webserver.http.Utils.ParamUtil;
import lynn.andr.webserver.http.biz.BizExecutor;
import lynn.andr.webserver.util.Log;

public abstract class RequestHandler implements HttpRequestHandler {
    public static final String TAG = "RequestHandler";
    protected Context mContext;
    protected ArrayList<String> mTargetsList = new ArrayList<>();
    protected Map<String, Class> mTargetExecutorMap = new HashMap<String, Class>();

    public RequestHandler(Context context, HttpRequestHandlerRegistry registry) {
        mContext = context;
        initTargetList();
        initTargetMap();
        registerToServer(registry);

    }

    public abstract void initTargetList();

    public abstract void initTargetMap();

    public ArrayList<String> getTargetsList() {
        return mTargetsList;
    }

    public void setTargetsList(ArrayList<String> targetsList) {
        this.mTargetsList = targetsList;
    }

    public void registerToServer(HttpRequestHandlerRegistry registry) {

        if (mTargetsList == null || mTargetsList.isEmpty()) return;
        if (registry == null) return;
        for (int i = 0; i < mTargetsList.size(); i++) {

            registry.register(mTargetsList.get(i), this);
        }
    }

    public boolean requestMatch(HttpRequest request, String match) {
        String target = request.getRequestLine().getUri();
        if (TextUtils.isEmpty(target) || TextUtils.isEmpty(match)) {
            return false;
        }
        if (target.contains(match)) {
            return true;
        }
        return false;
    }


    public BizExecutor getBizExecutor(String target, int responseType) {
        Log.i(TAG, "getBizExecutor target="+target);
        Log.i(TAG, "getBizExecutor mTargetExecutorMap="+mTargetExecutorMap);
        Log.i(TAG, "getBizExecutor mTargetExecutorMap="+mTargetExecutorMap.get(target));

        if (mTargetExecutorMap != null && mTargetExecutorMap.containsKey(target)) {
            Class executorClass = mTargetExecutorMap.get(target);

            try {
                BizExecutor bizExecutor = (BizExecutor) executorClass.newInstance();
                bizExecutor.setmContext(mContext);
                bizExecutor.setResponseType(responseType);
                return bizExecutor;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
