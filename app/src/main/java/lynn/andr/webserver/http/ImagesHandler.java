package lynn.andr.webserver.http;

import android.content.Context;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;

import lynn.andr.webserver.util.Log;

public class ImagesHandler extends RequestHandler {

	private static final String TAG = "ImagesHandler";

	private static final String IMAGES="images";
	private static final String JS="js";
	private static final String CSS="css";
	public ImagesHandler(Context context) {
		super(context);

	}

	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		super.handle(request, response, context);
		String target = request.getRequestLine().getUri();
		Log.i(TAG, "target=" + target);
		
		if(target==null){
			
			return;
		}
		
		
		// if(target.contains(IMAGES)){
		// target = target.substring(8);
		// }else if(target.contains(JS)){
		//
		// target = target.substring(4);
		// }else if(target.contains(CSS)){
		// target = target.substring(5);
		// }
		//

//		if (target.startsWith("/")) {
//			target = target.substring(1);
//		}

		if (target.contains(IMAGES)) {
			int indexOf = target.indexOf(IMAGES);
			target = target.substring(indexOf);
		} else if (target.contains(JS)) {
			int indexOf = target.indexOf(JS);
			target = target.substring(indexOf);
		} else if (target.contains(CSS)) {
			int start = target.indexOf(CSS);
			int end = target.indexOf(".css");
			target = target.substring(start,end+4);
		}
		Log.i(TAG, "target2=" + target);
		try {
			InputStream open = mContext.getAssets().open(target);
			BasicHttpEntity entity = new BasicHttpEntity();
			entity.setContent(open);
			response.setEntity(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
