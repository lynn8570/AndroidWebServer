package lynn.andr.webserver.http.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import java.io.IOException;

import lynn.andr.webserver.http.PageEntity;
import lynn.andr.webserver.util.NetUtil;

/**
 * Created by zowee-laisc on 2018/4/11.
 */

public class ResponseWapper {

    public static final String COOKIE_TIMEOUT = "600";// cookie timeout for XX seconds


    public static void wapper(int stateCode, boolean setCookies, HttpEntity entity, HttpResponse response) {
        if (setCookies) {
            response.addHeader("Set-Cookie", "userID=admin; Max-Age=" + COOKIE_TIMEOUT);
        } else {
            response.addHeader("Set-Cookie", "userID=null; Max-Age=" + COOKIE_TIMEOUT);//clear userid
        }
        response.setStatusCode(stateCode);
        response.setEntity(entity);
    }

    public static void wapper(boolean setCookies, HttpEntity entity, HttpResponse response) {
        wapper(HttpStatus.SC_OK, setCookies, entity, response);
    }

    public static void wapper(HttpEntity entity, HttpResponse response) {
        wapper(HttpStatus.SC_OK, true, entity, response);
    }
}
