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
    public static final int RETURN_TYPE_PAGE = 1;
    public static final int RETURN_TYPE_JSON = 2;



    public static final int EXECUTE_RESULT_SUCCESS = 10;
    public static final int EXECUTE_RESULT_ERROR= 12;
    public static final int EXECUTE_RESULT_BAD_INPUT= 13;

    protected Context mContext;
    protected int responseType;

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public int getResponseType() {
        return responseType;
    }

    public void setResponseType(int responseType) {
        this.responseType = responseType;
    }



    public void handle(HttpRequest request, HttpResponse response) throws HttpException, IOException {

        int result = doBiz(request);
        if (responseType == RETURN_TYPE_JSON){
            wrapResponseForJSON(response,result);
        }else if (responseType == RETURN_TYPE_JSON){
            wrapResponseForPAGE(response,result);
        }

    }




}
