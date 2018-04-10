package lynn.andr.webserver.util;

import android.content.Context;
import android.os.Environment;
import android.provider.Settings;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lynn.andr.webserver.http.bean.DataUsage;

public class NetUtil {

	private static final String TAG = "NetUtil";

	public static final String COOKIE_TIMEOUT = "600";// cookie time out
														// (seconds)
	private static final String DATA_USAGE_TOTAL_QUANTITY = "data_usage_total_quantity";
	private static final String DATA_USAGE_SCALE = "data_usage_scale";
	private static final String DATA_USAGE_START_DATE = "data_usage_start_date";
	private static final String DATA_USAGE_LIMIT_TYPE = "data_usage_limit_type";
	public static final String DATA_USAGE_USE = "data_usage_use";
	
	public static final String AUTO_ADJUEST_CYCLE= "auto_adjuest_cycle";

	private static final String WIFI_AP_MAX_LIMIT = "wifi_ap_max_limit";
	private static final String DEVICE_PROP_FILE_NAME;

	public static final String KEY_SECURITY = "security";
	public static final String KEY_PWD = "password";
	public static final String KEY_SSID = "SSID";
	static {
		File dataDirectory = Environment.getDataDirectory();
		Log.i("linlian", "loadDevicesProp(dataDirectory=" + dataDirectory);
		DEVICE_PROP_FILE_NAME = dataDirectory.getPath() + "/devices.properties";
	}

	public static String getLocalHostIp() {
		String ipaddress = "";
		try {
			Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface nif = en.nextElement();
				Enumeration<InetAddress> inet = nif.getInetAddresses();
//				while (inet.hasMoreElements()) {
//					InetAddress ip = inet.nextElement();
//					if (!ip.isLoopbackAddress()
//							&& InetAddressUtils.isIPv4Address(ip
//									.getHostAddress())) {
//						return ip.getHostAddress();
//					}
//				}
			}
		} catch (SocketException e) {
			Log.e("NetUtil", e.toString());
		}
		return ipaddress;
	}

	public static boolean isNotEmpty(String str) {
		if (str != null && !"".equals(str)) {
			return true;
		}
		return false;
	}

	public static Map<String, String> getParam(String uri) {
		Map<String, String> params = new HashMap<String, String>();
		try {
			if (isNotEmpty(uri)) {
				String subStr = uri.substring(uri.indexOf("?") + 1);
				String[] ary = subStr.split("&");
				for (int i = 0; i < ary.length; i++) {
					String[] temp = ary[i].split("=");
					if (temp.length < 2) {
						params.put(temp[0], "");
					} else {
						params.put(temp[0], temp[1]);
					}

				}
				return params;
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	
	
	

	public static Map<String, String> getpostParam(String str) {
		Log.i(TAG, "getpostParam str=" + str);
		Map<String, String> params = new HashMap<String, String>();
		String strSplitPatter = "\r\n";
		if (str.contains("&")) {
			strSplitPatter = "&";
		}else if(str.contains(",")){
			strSplitPatter = ",";
		}
		String[] split = str.split(strSplitPatter);

		for (int i = 0; i < split.length; i++) {
			Log.i(TAG, "getpostParam split[i]=" + split[i]);

			String[] pair = split[i].split("=");
			if (pair.length < 2) {
				params.put(pair[0].trim(), "");
			} else {
				Log.i(TAG, "handlePostParam: pair[1]=" +pair[1] );
				String decodeStr = URLDecoder.decode(pair[1]);
				Log.i(TAG, "handlePostParam: decodeStr=" +decodeStr );
				params.put(pair[0].trim(), decodeStr);
			}
		}
		Log.i(TAG, "getpostParam params=" + params);
		return params;
	}

	public static DataUsage getDataUsage(Context context) {
		DataUsage dataUsage = new DataUsage();
		long total = Settings.System.getLong(context.getContentResolver(),
				DATA_USAGE_TOTAL_QUANTITY, 1 * 100 * 1024);
		int usagePercent = Settings.System.getInt(context.getContentResolver(),
				DATA_USAGE_SCALE, 0);
		int cleanupDate = Settings.System.getInt(context.getContentResolver(),
				DATA_USAGE_START_DATE, 1);
		
		int limitType = Settings.System.getInt(context.getContentResolver(),
				DATA_USAGE_LIMIT_TYPE, DataUsage.LIMIT_TYPE_IGNORE);
		
		int calitype =Settings.System.getInt(context.getContentResolver(),
				AUTO_ADJUEST_CYCLE, 0);
		
		long useddata=Settings.System.getLong(context.getContentResolver(),
				DATA_USAGE_USE, 0);
		dataUsage.setTotal(total);
		dataUsage.setUsagePercent(usagePercent);
		dataUsage.setCleanupDate(cleanupDate);
		dataUsage.setLimit_type(limitType);
		dataUsage.setCali_type(calitype);
		dataUsage.setUseddata(useddata);
		return dataUsage;
	}

	public static void setDataUsageTotal(Context context, long total) {
		if (total <= 0)
			return;
		Settings.System.putLong(context.getContentResolver(),
				DATA_USAGE_TOTAL_QUANTITY, total);

	}
	
	public static void setLimitType(Context context, int type) {
		if (type < 1 || type > 3)
			return;
		Settings.System.putLong(context.getContentResolver(),
				DATA_USAGE_LIMIT_TYPE, type);
	}
	
	/**
	 * 
	 * @param context
	 * @param type 0 trun off; 1,2,3,4,5,6,7days
	 */
	public static void setCali(Context context, int type) {
		if (type < 0 || type > 7)
			return;
		Settings.System.putLong(context.getContentResolver(),
				AUTO_ADJUEST_CYCLE, type);
	}
	public static void setCleanupDate(Context context, int date) {
		if (date <= 0 || date > 31)
			return;
		Settings.System.putLong(context.getContentResolver(),
				DATA_USAGE_START_DATE, date);
	}





	private static List<String> readFileLines() {
		File file = new File("/proc/net/arp");
		BufferedReader reader = null;
		String tempString = "";
		List<String> mResult = new ArrayList<String>();
		try {

			reader = new BufferedReader(new FileReader(file));
			while ((tempString = reader.readLine()) != null) {
				Log.i(TAG, tempString);
				mResult.add(tempString);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return mResult;
	}

	public static Map<String, String> getMacIpPairs() {
		List<String> readFileLines = readFileLines();
		HashMap<String, String> ipMacPairs = new HashMap<String, String>();
		if (readFileLines != null && readFileLines.size() > 1) {
			for (int j = 1; j < readFileLines.size(); ++j) {
				String[] mType = readFileLines.get(j).split(" ");
				List<String> mList = new ArrayList<String>();
				for (int i = 0; i < mType.length; ++i) {
					if (mType[i] != null && mType[i].length() > 0)
						mList.add(mType[i]);
				}
				
				if (mList != null && mList.size() > 4) {
					ipMacPairs.put(mList.get(3),  mList.get(0));
				}
			}
			Log.i(TAG, "ipMacPairs="+ipMacPairs);
			return ipMacPairs;

		}
		return null;

	}
	//TODO: get from framework res
	private static final String wifi_tether_configure_ssid_default="androidmifi";
	private static final String def_wifi_wifihotspot_pass="admin123";
	
}
