package lynn.andr.webserver.http.handler;

import android.content.Context;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import java.util.ArrayList;

public abstract class RequestHandler implements HttpRequestHandler {
    public static final String TAG = "RequestHandler";
    protected Context mContext;
    protected ArrayList<String> targetsList = new ArrayList<>();

    public RequestHandler(Context context, HttpRequestHandlerRegistry registry) {
        mContext = context;
        initTargetList();
        registerToServer(registry);

    }

    public abstract void initTargetList();


    public ArrayList<String> getTargetsList() {
        return targetsList;
    }

    public void setTargetsList(ArrayList<String> targetsList) {
        this.targetsList = targetsList;
    }

    public void registerToServer(HttpRequestHandlerRegistry registry) {

        if (targetsList == null || targetsList.isEmpty()) return;
        if (registry == null) return;
        for (int i = 0; i < targetsList.size(); i++) {

            registry.register(targetsList.get(i), this);
        }
    }



}
