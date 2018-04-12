package lynn.andr.webserver.http.biz;

import android.content.Context;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

import java.io.IOException;

/**
 * Created by zowee-laisc on 2018/4/12.
 */

public abstract class BizExecutor implements BizExecute {
    protected Context mContext;

    public BizExecutor(Context context) {
        this.mContext = context;
    }


}
