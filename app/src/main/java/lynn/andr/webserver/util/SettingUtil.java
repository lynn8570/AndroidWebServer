package lynn.andr.webserver.util;

import java.util.Locale;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.provider.Settings;
import android.telephony.TelephonyManager;

public class SettingUtil {
	private static final String DEFAULT_PWD = "adminpwd";//admin default password
	private static final String DEFAULT_ADMIN = "Admin";
	private static final String PREFERENCE_ADMIN = "admin";
	private static final String PREFERENCE_PASSWORD = "password";
	private static final String PREFERENCE_USERNAME = "username";
	
	private static final String SETINGS_TOKEN = "le_token";
	private static final String SETINGS_LE_HOST = "le_host";

	private static final String TAG = "SettingUtil";
	public static String getPassword(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCE_ADMIN, Context.MODE_PRIVATE);

		return preferences.getString(PREFERENCE_PASSWORD, DEFAULT_PWD);
	}
	
	public static void setLeHost(Context context, String host) {
		if(host==null||host.isEmpty()){
			Settings.System.putString(context.getContentResolver(),
					SETINGS_LE_HOST, "");
		}else{
			Settings.System.putString(context.getContentResolver(),
					SETINGS_LE_HOST, host);
		}
	}
	
	public static String getLeHost(Context context) {
		String lehost= Settings.System.getString(context.getContentResolver(),
				SETINGS_LE_HOST);
		if(lehost==null)lehost="";
		return lehost;
	}
	
	public static void setToken(Context context, String token) {
		if(token!=null&&!token.isEmpty()){
			Settings.System.putString(context.getContentResolver(),
					SETINGS_TOKEN, token);
		}
	}
	
	public static String getToken(Context context){
		return Settings.System.getString(context.getContentResolver(),
				SETINGS_TOKEN);
	}
	

	public static String getUsername(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCE_ADMIN, Context.MODE_PRIVATE);
		return preferences.getString(PREFERENCE_USERNAME, DEFAULT_ADMIN);
	}

	public static void clearPreference(Context context){
		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCE_ADMIN, Context.MODE_PRIVATE);
		Editor edit = preferences.edit();
		edit.clear();
		edit.commit();
	}
	public static boolean setAdmin(Context context, String pwd,String username) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCE_ADMIN, Context.MODE_PRIVATE);
		if (pwd != null && !pwd.isEmpty()
				&&username!=null&&!username.isEmpty()) {
			Editor edit = preferences.edit();
			edit.putString(PREFERENCE_PASSWORD, pwd);
			edit.putString(PREFERENCE_USERNAME, username);
			edit.commit();
			return true;
		}
		return false;
	}
	
	public static Locale getCurrentLocal(Context context){
		
		Configuration config = context.getResources().getConfiguration();  
		 Locale currentLocale = config.locale;
		 return currentLocale;
	}
	public static void setLocal(Context context,Locale setLocale){
		Configuration config = context.getResources().getConfiguration();  
		if(config.locale!=setLocale){
			config.locale=setLocale;
			context.getResources().updateConfiguration(config, null);  
		}
	}
}
