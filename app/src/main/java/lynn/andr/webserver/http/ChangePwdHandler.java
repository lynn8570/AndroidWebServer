package lynn.andr.webserver.http;

import java.io.IOException;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;


import android.content.Context;

import lynn.andr.webserver.R;
import lynn.andr.webserver.fota.ErrorCode;
import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.NetUtil;
import lynn.andr.webserver.util.SettingUtil;


public class ChangePwdHandler extends RequestHandler {

	private static final String TAG = "ChangePwdHandler";
	
	private static final String KEY_OLD_PWD="oldpwd";
	private static final String KEY_NEW_PWD="newpwd";
	private static final String KEY_NEW_USERNAME="username";

	public ChangePwdHandler(Context context) {
		super(context);
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		super.handle(request, response, context);
		
		if(!checkPermission(request, response)){
			responeMessage(response, R.string.permission_deny);
			return;
		}
		Map<String, String> parameter = parseParameter(request); 
		String newPwd = parameter.get(KEY_NEW_PWD); 
		String oldPwd = parameter.get(KEY_OLD_PWD); 
		String username = parameter.get(KEY_NEW_USERNAME); 
		Log.i(TAG,"new newPwd="+newPwd);
		Log.i(TAG,"new password="+oldPwd);
		String curPwd = SettingUtil.getPassword(mContext);
		Log.i(TAG,"new curPwd="+curPwd);
		String curUsername = SettingUtil.getUsername(mContext);
		Log.i(TAG,"new password="+oldPwd);
		if(curPwd.equals(oldPwd)){
			if(newPwd!=null&&!newPwd.isEmpty()
					&&username!=null&&!username.isEmpty()){
				SettingUtil.setAdmin(mContext, newPwd,username);
				response.addHeader("Set-Cookie", "userID=null; Max-Age="+ NetUtil.COOKIE_TIMEOUT);
//				responeMessage(response, R.string.modify_pwd_success);
				responeChangePwd(request,response,true, R.string.modify_pwd_success, ErrorCode.STATU_OK);
			}else{
//				responeMessage(response, R.string.error_input);
				responeChangePwd(request,response,true,R.string.error_input,ErrorCode.ERROR_PARAM);
			}
			
		}else{
			//responeMessage(response, R.string.error_pwd);
			responeChangePwd(request,response,false,R.string.error_pwd,ErrorCode.ERROR_CODE_NULL);
		}
		
		
	}

	private void responeChangePwd(HttpRequest request, HttpResponse response,boolean result,int resid,int code) throws IOException{
		String target = request.getRequestLine().getUri();
		
			responeMessage(response, resid);
	}
	
}
