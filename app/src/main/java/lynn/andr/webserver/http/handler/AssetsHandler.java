package lynn.andr.webserver.http.handler;

import android.content.Context;
import android.text.TextUtils;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandlerRegistry;

import java.io.IOException;
import java.io.InputStream;

/**
 * handle css,image,js res
 */
public class AssetsHandler extends RequestHandler {

    private static final String TAG = "ImagesHandler";

    private static final String IMAGES = "images";
    private static final String JS = "js";
    private static final String CSS = "css";
    private String assetFilename;

    public AssetsHandler(Context context, HttpRequestHandlerRegistry registry) {
        super(context, registry);
    }


    @Override
    public void initTargetList() {
        targetsList.add("*" + ".html");
        targetsList.add("/images/*");
        targetsList.add("/css/*");
        targetsList.add("/js/*");
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response,
                       HttpContext context) throws HttpException, IOException {
        String target = request.getRequestLine().getUri();
        String assetFilename = extractFilePathName(target);
        if (TextUtils.isEmpty(assetFilename))
            return;
        try {
            InputStream open = mContext.getAssets().open(assetFilename);
            BasicHttpEntity entity = new BasicHttpEntity();
            entity.setContent(open);
            response.setEntity(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String extractFilePathName(String url) {
        if (url == null) {
            return null;
        }
        if (url.contains(IMAGES)) {
            int indexOf = url.indexOf(IMAGES);
            return url.substring(indexOf);
        } else if (url.contains(JS)) {
            int indexOf = url.indexOf(JS);
            return url.substring(indexOf);
        } else if (url.contains(CSS)) {
            int start = url.indexOf(CSS);
            int end = url.indexOf(".css");
            return url.substring(start, end + 4);
        } else if (url.contains(".html")) {
            int start = url.lastIndexOf("\\");
            return url.substring(start);
        }
        return null;
    }

}
