package lynn.andr.webserver.http;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;



import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.telephony.TelephonyManager;

import lynn.andr.webserver.R;
import lynn.andr.webserver.fota.Constant;
import lynn.andr.webserver.fota.ErrorCode;
import lynn.andr.webserver.http.bean.AndroidAp;
import lynn.andr.webserver.http.bean.DataUsage;
import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.NetUtil;
import lynn.andr.webserver.util.SettingUtil;

public class JSONHandler extends RequestHandler {

	private static final String TAG = "JSON";
	private static final String URI_HOTSPOT = "/hotspot";
	private static final String URI_CONNECTIONS = "/connections";
	private static final String URI_BLACKLIST = "/blacklist";
	private static final String URI_DATA = "/data";
	private static final String URI_IMEI = "/imei";

	private static final String URI_BLOCK = "/block";
	private static final String URI_UNBLOCK = "/unblock";
	private static final String URI_SET_AP = "/setap";
	private static final String URI_SET_TOKEN = "/settoken";
	private static final String URI_GET_TOKEN = "/token";
	private static final String URI_UPDATE_LEHOST = "/setlehost";
	private static final String URI_GET_LEHOST = "/lehost";
	private static final String URI_GET_VERSION = "/version";
	private static final String KEY_LE_TOKEN = "token";
	private static final String KEY_LE_HOST = "lehost";
	private static final String KEY_VERSION = "version";

	public JSONHandler(Context context) {
		super(context);

	}

	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		super.handle(request, response, context);

		// int checkUser = checkUser(request);
		//
		// if (checkUser == CHECK_USER_OK) {

		String target = parseTarget(request);
		Log.i(TAG, "JSONHandler.handle target=" + target);
		if(target.contains("/json/")){
			target=target.substring(5);
		}else if(target.contains(".json")){
			int indexOf = target.indexOf(".json");
			target = target.substring(0,indexOf);
		}
		Log.i(TAG, "JSONHandler.handle target=" + target);
		switch (target) {
		case URI_HOTSPOT:
			handleHotspot(request, response);
			break;
		case URI_SET_AP:
			handleSetupAp(request,response);
			break;
		case URI_CONNECTIONS:
			handleConnection(request, response);
			break;
		case URI_DATA:
			handleDataUsage(request, response);
			break;
		case URI_IMEI:
			handleIMEI(request, response);
			break;

		case URI_BLOCK:
			handleBlock(request, response, true);
			break;
		case URI_UNBLOCK:
			handleBlock(request, response, false);
			break;
		case URI_BLACKLIST:
			handleBlackList(request, response);
			break;
		case URI_SET_TOKEN:
			handleToken(request,response,false);
			break;
		case URI_GET_TOKEN:
			handleToken(request,response,true);
			break;	
		case URI_UPDATE_LEHOST:
			handleLeHost(request, response, false);
			break;
		case URI_GET_LEHOST:
			handleLeHost(request, response, true);
			break;

		case URI_GET_VERSION:
			handVersion(request, response);
			break;
		default:
			break;
		}

	}
	private void handVersion(HttpRequest request, HttpResponse response) {
		if (Constant.version != null && !Constant.version.isEmpty()) {
			JSONObject versionobj = new JSONObject();
			try {
				versionobj.put(KEY_VERSION, Constant.version);
				response.setEntity(JSONEntity.getMessageEntity(true,
						ErrorCode.STATU_OK, R.string.json_sucess, versionobj,
						mContext));
				return;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		response.setEntity(JSONEntity.getMessageEntity(false,
				ErrorCode.ERROR_CODE_NULL, R.string.json_error,
				mContext));
	}
	private void handleLeHost(HttpRequest request, HttpResponse response,
			boolean get) {
		try {
			if (get) {
				String lehost = SettingUtil.getLeHost(mContext);
				JSONObject lehostobj = new JSONObject();
				try {
					lehostobj.put(KEY_LE_HOST, lehost);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				response.setEntity(JSONEntity.getMessageEntity(true,
						ErrorCode.STATU_OK, R.string.json_sucess, lehostobj,
						mContext));
			} else {
				Map<String, String> parseParameter = parseParameter(request);
				String lehost = parseParameter.get(KEY_LE_HOST);
				SettingUtil.setLeHost(mContext, lehost);
				JSONObject lehostobj = new JSONObject();
				lehostobj.put(KEY_LE_HOST, lehost);
				response.setEntity(JSONEntity.getMessageEntity(true,
						ErrorCode.STATU_OK, R.string.json_sucess, lehostobj,
						mContext));
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setEntity(JSONEntity.getMessageEntity(false,
					ErrorCode.ERROR_CODE_NULL, R.string.json_error, mContext));
		}
	}
	private void handleToken(HttpRequest request, HttpResponse response,boolean onlyGet) {
		try {
			if(onlyGet){
				String token = SettingUtil.getToken(mContext);
				if(token!=null&&!token.isEmpty()){
					JSONObject tokenobj=new JSONObject();
					try {
						tokenobj.put(KEY_LE_TOKEN, token);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					response.setEntity(JSONEntity.getMessageEntity(true, ErrorCode.STATU_OK,
							R.string.json_sucess,tokenobj, mContext));
				}else{
					response.setEntity(JSONEntity.getMessageEntity(false,
							ErrorCode.ERROR_CODE_NULL, R.string.json_error, mContext));
				}
			}else{
				Map<String, String> parseParameter = parseParameter(request);
				String token = parseParameter.get(KEY_LE_TOKEN);
				if (token == null || token.isEmpty()) {
					response.setEntity(JSONEntity.getMessageEntity(false,
							ErrorCode.ERROR_PARAM, R.string.json_error_param, mContext));
				} else {
					SettingUtil.setToken(mContext, token);
					JSONObject tokenobj=new JSONObject();
					try {
						tokenobj.put(KEY_LE_TOKEN, token);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					response.setEntity(JSONEntity.getMessageEntity(true, ErrorCode.STATU_OK,
							R.string.json_sucess,tokenobj, mContext));
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			response.setEntity(JSONEntity.getMessageEntity(false,
					ErrorCode.ERROR_CODE_NULL, R.string.json_error, mContext));
		}
	}
	
	private void handleBlock(HttpRequest request,HttpResponse response,boolean block){
		boolean result=false;
		
		String mac=null;
		try {
			mac = BlockMacHandler.getMac(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.i(TAG,"handleBlock mac="+mac);
		
		if (mac == null || mac.isEmpty()) {
			response.setEntity(JSONEntity.getMessageEntity(false,ErrorCode.ERROR_PARAM,
					R.string.json_error_param, mContext));
			return;
		}
		mac = mac.replaceAll("\"", "");
		Log.i(TAG,"handleBlock mac="+mac);
		boolean valid = false;
//		if (block) {
//			valid = NetUtil.listInConnList(mContext, mac);
//		} else {
//			valid = NetUtil.listInBlackList(mContext, mac);
//		}
		Log.i(TAG,"handleBlock valid="+valid);

		if (!valid) {
			response.setEntity(JSONEntity.getMessageEntity(false, ErrorCode.ERROR_PARAM,
					R.string.json_error_param, mContext));
			return;
		}

//		if (block) {
//			result = NetUtil.add2BlackList(mac, mContext);
//		} else {// unblock
//			result = NetUtil.deleteFromBlackList(mac, mContext);
//		}

		if (result) {
			JSONObject macobj=new JSONObject();
			try {
				macobj.put("mac", mac);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			response.setEntity(JSONEntity.getMessageEntity(true, ErrorCode.STATU_OK,
					R.string.json_sucess,macobj, mContext));
		} else {
			response.setEntity(JSONEntity.getMessageEntity(false,
					ErrorCode.ERROR_CODE_NULL, R.string.json_error, mContext));
		}
	}

	
	private void handleIMEI(HttpRequest request,HttpResponse response){
		Log.i(TAG,"handleIMEI");
		String imei = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE))
                .getDeviceId();
		 if(imei!=null&&!imei.isEmpty()){
			 JSONObject imeiobj=new JSONObject();
			 try {
				 if("0".equalsIgnoreCase(imei)){
					 Log.i(TAG,"handleIMEI user fake IMEI");
//					 imei="86130030000710";
					 imei=mContext.getString(R.string.json_unknown);
				 }
				imeiobj.put("imei", imei);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			 response.setEntity(JSONEntity.getMessageEntity(true,
					 ErrorCode.STATU_OK, R.string.json_sucess,imeiobj, mContext));
		 }else{
			 response.setEntity(JSONEntity.getMessageEntity(false,
					 ErrorCode.ERROR_CODE_NULL, R.string.json_error, mContext));
		 }
	}

	private void handleDataUsage(HttpRequest request, HttpResponse response) {
		DataUsage dataUsage = NetUtil.getDataUsage(mContext);
		Log.i(TAG, "dataUsage=" + dataUsage);
		if (dataUsage != null) {
			StringEntity stringEntity = JSONEntity.getDataUasage(dataUsage);
			Log.i(TAG, "stringEntity=" + stringEntity);
			if (stringEntity == null) {
				response.setEntity(JSONEntity.getMessageEntity(false,
						ErrorCode.ERROR_CODE_NULL, R.string.json_error, mContext));
			} else {
				response.setEntity(stringEntity);
			}
		} else {

			response.setEntity(JSONEntity.getMessageEntity(false,
					ErrorCode.ERROR_CODE_NULL, R.string.json_error, mContext));
		}
	}

	private void handleConnection(HttpRequest request, HttpResponse response) {
		StringEntity se = JSONEntity.getConnectedDevices(mContext);
		if (se == null) {
			response.setEntity(JSONEntity.getMessageEntity(false,
					ErrorCode.ERROR_CODE_NULL, R.string.json_error, mContext));
		} else {
			response.setEntity(se);
		}
	}
	private void handleBlackList(HttpRequest request,HttpResponse response){
		
		StringEntity blacklist = JSONEntity.getBlacklist(mContext);
		if(blacklist==null){
			response.setEntity(JSONEntity.getMessageEntity(false,
					ErrorCode.ERROR_CODE_NULL, R.string.json_error, mContext));
		}else{
			response.setEntity(blacklist);
		}
	}
	private void handleSetupAp(HttpRequest request, HttpResponse response){
		Map<String, String> parameter = null;
		WifiConfiguration config=null;
		try {
			parameter = parseParameter(request);
//			config = NetUtil.getConfig(parameter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(config!=null){
			
			JSONObject hotspotJson = JSONEntity.getHotspotJson(config.SSID,config.preSharedKey==null?0:1,config.preSharedKey);
			response.setEntity(JSONEntity.getMessageEntity(true, ErrorCode.STATU_OK,
					R.string.json_sucess,hotspotJson, mContext));
//			NetUtil.startSetWifi(mContext,config);
		}else{
			response.setEntity(JSONEntity.getMessageEntity(false,
					ErrorCode.ERROR_CODE_NULL, R.string.json_error, mContext));
		}
	}
	private void handleHotspot(HttpRequest request, HttpResponse response) {

//		NetUtil.loadDevicesProp();

		AndroidAp androidAp = null;//NetUtil.getAndroidAp(mContext);
		if (androidAp == null) {

			response.setEntity(JSONEntity.getMessageEntity(false,
					ErrorCode.ERROR_CODE_NULL, R.string.json_error, mContext));
			return;
		}
		StringEntity hotspot =null;// JSONEntity.getHotspot(androidAp);
		if (hotspot != null) {
			response.setEntity(hotspot);
		} else {
			response.setEntity(JSONEntity.getMessageEntity(false,
					ErrorCode.ERROR_CODE_NULL, R.string.json_error, mContext));
		}

	}

}
