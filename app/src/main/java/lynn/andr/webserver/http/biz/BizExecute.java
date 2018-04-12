package lynn.andr.webserver.http.biz;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Created by zowee-laisc on 2018/4/12.
 */

public interface BizExecute{

    void handle(HttpRequest request, HttpResponse response) throws HttpException, IOException;

}
