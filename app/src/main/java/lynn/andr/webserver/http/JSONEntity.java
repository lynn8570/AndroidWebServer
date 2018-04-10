package lynn.andr.webserver.http;

import android.content.Context;

import com.fota.iport.MobAgentPolicy;
import com.fota.iport.info.RelNotesInfo;
import com.fota.iport.info.VersionInfo;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import lynn.andr.webserver.R;
import lynn.andr.webserver.fota.ErrorCode;
import lynn.andr.webserver.http.bean.AndroidAp;
import lynn.andr.webserver.http.bean.DataUsage;
import lynn.andr.webserver.http.bean.MDevice;
import lynn.andr.webserver.util.Log;

public class JSONEntity {
	
	private static final String RESPONE_RESULT="result";
	private static final String RESPONE_MESSAGE="message";
	private static final String RESPONE_DATA="data";
	private static final String RESPONE_ERRORCODE="code";
	
	private static final String RESPONE_SSID="ssid";
	private static final String RESPONE_SECURITY="security";
	private static final String RESPONE_PWD="password";
	private static final String RESPONE_LIST="connectedlist";
	private static final String RESPONE_USAGE="datausage";
	
	private static final String DEVICE_NAME = "devicename";
	private static final String DEVICE_MAC = "MAC";
	private static final String DEVICE_IP = "IP";
	private static final String DEVICE_IS_LIVEMAN = "isliveman";
	
	private static final String DATA_TOTAL = "total";
	private static final String DATA_PERCENT = "percent";
	private static final String DATA_USED = "used";
	
	private static final String FOTA_NAME = "version";
	private static final String FOTA_PUBISH = "publish";
	private static final String FOTA_SIZE = "filesize";
	private static final String FOTA_CONTENT = "content";


	public static StringEntity getDataUasage(DataUsage du){
		JSONObject datausage = new JSONObject();
		
		StringEntity se = null;
		try {
			int totalMB = (int)(du.getTotal() / 1024);
			int usedDataMB=(int)(du.getUseddata()/1024);
			datausage.put(DATA_TOTAL, totalMB+"MB");
//			datausage.put(DATA_USED, (int)(totalMB*du.getUsagePercent()/100)+"MB");
			datausage.put(DATA_USED, usedDataMB+"MB");
			datausage.put(DATA_PERCENT, du.getUsagePercent());
			se = new StringEntity(datausage.toString(),"UTF-8"); 
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return se;
	}
	
	public static StringEntity getHotspot(AndroidAp ap) {
		if(ap==null)
			return null;
		JSONObject jsonHotpot = getHotspotJson(ap.getSsid(),ap.getSecurityType(),ap.getPassword());
		StringEntity se = null;
		try {
			se = new StringEntity(jsonHotpot.toString(),"UTF-8"); 
		}catch(Exception e){
			e.printStackTrace();
		}
		return se;

	}
	
	public static JSONObject getHotspotJson(String ssid,int security,String pwd) {
		JSONObject jsonHotpot = new JSONObject();
		try {
			jsonHotpot.put(RESPONE_SSID,ssid);
			jsonHotpot.put(RESPONE_SECURITY, security);
			jsonHotpot.put(RESPONE_PWD, pwd);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonHotpot;
	}

	public static StringEntity getBlacklist(Context context){
		String[] blackList = null;//NetUtil.getBlackList(context);
		JSONArray jsonlist = new JSONArray();
		if(blackList==null||blackList.length<1){
			return null;
		}
		for(int i=0;i<blackList.length;i++){
			jsonlist.put(blackList[i]);
		}
		try{
			JSONObject json= new JSONObject();
			json.put("blackList", jsonlist);
			return getMessageEntity(true, ErrorCode.STATU_OK , R.string.json_sucess,json, context);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static StringEntity getConnectedDevices(Context context) {

		StringEntity se = null;

		JSONArray jsonlist = new JSONArray();
		
		List<MDevice> devices = new ArrayList<>();//NetUtil.getDevices(context);
		for (int i = 0; i < devices.size(); i++) {
			MDevice wifiDevice = devices.get(i);
			JSONObject device = new JSONObject();
			try {
				device.put(DEVICE_NAME, wifiDevice.getDevicename());
				device.put(DEVICE_MAC, wifiDevice.getStrMac());
				device.put(DEVICE_IP, wifiDevice.getStrIP());
				device.put(DEVICE_IS_LIVEMAN, wifiDevice.isLiveman());
				jsonlist.put(device);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		se= new StringEntity(jsonlist.toString(),"UTF-8");
		return se;
	}
	public static StringEntity getAndroidapEntity(AndroidAp ap){
		//JSONObject  jsonAndroidap =JSONObject.fromObject(ap);
		JSONObject jsonAndroidap=new JSONObject();
		StringEntity se=null;
		try {
			jsonAndroidap.put(RESPONE_SSID,ap.getSsid());
			jsonAndroidap.put(RESPONE_SECURITY, ap.getSecurityType());
			jsonAndroidap.put(RESPONE_PWD, ap.getPassword());
			List<MDevice> connectedList = ap.getConnectedList();
			if(connectedList!=null&&!connectedList.isEmpty()){
				JSONArray jsonlist= new JSONArray();
				for(int i=0;i<connectedList.size();i++){
					jsonlist.put(connectedList.get(i).getStrMac());
				}
				jsonAndroidap.put("connectedlist", jsonlist);
			}
			
			
			jsonAndroidap.put("datausage", ap.getDataUsage());
			//ap.put("battery", 50);//0~100,<0,errro;>100:charging;
			//ap.put("isCameraConneted", true);
			
			se = new StringEntity(jsonAndroidap.toString(),"UTF-8"); 
			System.out.println(jsonAndroidap.toString());
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return se;
	}
	
	
	public static StringEntity getMessageEntity(boolean result,int errorcode,String msg,JSONObject data,Context context)
	{
		Log.i("json", "msg="+msg);
		JSONObject jsonRespone=new JSONObject();
		StringEntity se=null;
		try {
			if(result){
				jsonRespone.put(RESPONE_RESULT, "true");
			}else{
				jsonRespone.put(RESPONE_RESULT, "false");
			}
			
			jsonRespone.put(RESPONE_MESSAGE, msg);
			if(data==null){
				jsonRespone.put(RESPONE_DATA, "");
			}else{
				jsonRespone.put(RESPONE_DATA, data);
			}
			jsonRespone.put(RESPONE_ERRORCODE, errorcode);
			Log.i("json", "jsonRespone.toString()="+jsonRespone.toString());
			se = new StringEntity(jsonRespone.toString(),"UTF-8");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return se;
	}
	
	public static StringEntity getMessageEntity(boolean result,int errorcode,int msgid,Context context){
		return getMessageEntity(result,errorcode,msgid,null,context);
	}
	public static StringEntity getMessageEntity(boolean result,int errorcode,String msg,Context context)
	{
		return getMessageEntity(result,errorcode,msg,null,context);
	}
	public static StringEntity getMessageEntity(boolean result,int errorcode,int msgid,JSONObject data,Context context){
		String msg = context.getString(msgid);
		return getMessageEntity(result,errorcode,msg,data,context);
	}

	public static StringEntity getVersionInfo(Context context) {
		JSONObject jsonRespone = new JSONObject();
		StringEntity se = null;
		try {
			jsonRespone.put(RESPONE_MESSAGE,
					context.getString(R.string.found_new_version));
			appendFotaInfo(jsonRespone);
			se = new StringEntity(jsonRespone.toString(),"UTF-8");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return se;
	}

	public static JSONObject appendFotaInfo(JSONObject jsonRespone) throws JSONException {
		RelNotesInfo relNotesInfo = MobAgentPolicy.getRelNotesInfo();
		VersionInfo versionInfo = MobAgentPolicy.getVersionInfo();
		jsonRespone.put(FOTA_NAME, relNotesInfo.version);
		jsonRespone.put(FOTA_PUBISH, relNotesInfo.publishDate);
		jsonRespone.put(FOTA_SIZE, versionInfo.fileSize);
		String contentStr = relNotesInfo.content;
		int start=contentStr.indexOf("{");
		int end=contentStr.lastIndexOf("}");
		Log.i("json", "lastIndexOf="+start);
		Log.i("json", "lastIndexOf="+end);
		contentStr = contentStr.substring(start,end+1);
		Log.i("json", "lastIndexOf="+contentStr);
		try{
			JSONObject content= new JSONObject(contentStr);
			jsonRespone.put(FOTA_CONTENT,content);
		}catch(Exception e ){
			e.printStackTrace();
			jsonRespone.put(FOTA_CONTENT,"Content JSON exception");
		}
		return jsonRespone;
	}

	public static StringEntity getDownloadInfo(Context context) {
		JSONObject jsonRespone = new JSONObject();

		StringEntity se = null;
		try {
			jsonRespone.put(RESPONE_MESSAGE,
					context.getString(R.string.download_success));
			appendFotaInfo(jsonRespone);
			se = new StringEntity(jsonRespone.toString(),"UTF-8");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return se;
	}

}
