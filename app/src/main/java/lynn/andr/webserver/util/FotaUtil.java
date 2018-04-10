package lynn.andr.webserver.util;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

public class FotaUtil {
	public static String getRealContent(String strContent){
		try {
			JSONArray jsonarray= new JSONArray(strContent);
			JSONObject obj =jsonarray.getJSONObject(0);
			String realcontent = obj.getString("content");
			return realcontent;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
