package lynn.andr.webserver.http;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;

import android.app.DownloadManager.Request;
import android.content.Context;

import lynn.andr.webserver.R;
import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.NetUtil;
import lynn.andr.webserver.util.SettingUtil;


public class WebServiceHandler extends RequestHandler {
	private static final String TAG = "WebServiceHandler";
	private PageEntity mPageEntity;
	
	public WebServiceHandler(Context context) {
		super(context);
		mPageEntity = new PageEntity(context);
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		super.handle(request, response, context);

		if(isLogout(request))
		{
			response.addHeader("Set-Cookie", "userID=null; Max-Age="
					+ NetUtil.COOKIE_TIMEOUT);
			//responeMessage(response, R.string.log_out);	
			responeLogin(response);
			return;
		}
		
//		if(responeJson(request)){
//			AndroidAp androidAp = NetUtil.getAndroidAp(mContext);
//			if(androidAp!=null){
//				response.setStatusCode(HttpStatus.SC_OK);
//				response.setEntity(JSONEntity.getAndroidapEntity(androidAp));
//			}else{
//				response.setStatusCode(HttpStatus.SC_OK);
//				response.setEntity(JSONEntity.getMessageEntity(false,R.string.error,mContext));
//			}
//			
//			return;
//			
//		}
		
		
		if(isChgLang(request)){
			changeLang(request);
		}
		
		if(checkPermission(request,response)){
			if(isChgMax(request)){
//				if(!changeMax(request)){//change max connection error
				if(false) {
					responeMessage(response, R.string.error);
					return;
				}
			}
			
			if(isUpdateWare(request)){
				response.setStatusCode(HttpStatus.SC_OK);
				response.setEntity(mPageEntity.getUpdateWare());
				return;
			}
			
			response.setStatusCode(HttpStatus.SC_OK);
			response.setEntity(mPageEntity.getIndexPage());
			
		}else{
			responeLogin(response);
		}
		
		
	}
	
	private boolean isUpdateWare(HttpRequest request){
		String parseTarget = parseTarget(request);
		if(parseTarget.contains("updateWare")){
			return true;
		}
		return false;
	}
	
	
	
	private void changeLang(HttpRequest request){
		String target = request.getRequestLine().getUri();
		String curLang = SettingUtil.getCurrentLocal(mContext).getLanguage();
		Log.i(TAG, "curLang ="+curLang);
		Log.i(TAG, "target ="+target);
		if(!target.contains("curLang")){//chage
			if(curLang.equalsIgnoreCase(Locale.ENGLISH.getLanguage())){
				Log.i(TAG, "setLocal ="+Locale.CHINA.getLanguage());
				SettingUtil.setLocal(mContext,Locale.CHINA);
			}else{
				Log.i(TAG, "setLocal ="+Locale.ENGLISH.getLanguage());
				SettingUtil.setLocal(mContext,Locale.ENGLISH);
			}
			
		}
	}
	
	private boolean isChgMax(HttpRequest request){
		String target = request.getRequestLine().getUri();
		if(target!=null&&target.contains("chgmax")){
			return true;
		}
		return false;
	}
	
	private boolean isChgLang(HttpRequest request){
		String target = request.getRequestLine().getUri();
		if(target!=null&&target.contains("lang")){
			return true;
		}
		return false;
	}
	
	private boolean isLogout(HttpRequest request){
		String target = request.getRequestLine().getUri();
		if(target!=null&&target.contains("logout")){
			return true;
		}
		return false;
	}

}
