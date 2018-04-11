package lynn.andr.webserver.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
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



    private static final String WIFI_AP_MAX_LIMIT = "wifi_ap_max_limit";
    private static final String DEVICE_PROP_FILE_NAME;

    public static final String KEY_SECURITY = "security";
    public static final String KEY_PWD = "password";
    public static final String KEY_SSID = "SSID";

    static {
        File dataDirectory = Environment.getDataDirectory();
        Log.i("NetUtil", "loadDevicesProp(dataDirectory=" + dataDirectory);
        DEVICE_PROP_FILE_NAME = dataDirectory.getPath() + "/devices.properties";
    }

    /**
     * 通过反射获取ap状态
     * @param context
     * @return
     */
    public static boolean isWifiApOpen(Context context) {
        try {
            WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            //通过放射获取 getWifiApState()方法
            Method method = manager.getClass().getDeclaredMethod("getWifiApState");
            //调用getWifiApState() ，获取返回值
            int state = (int) method.invoke(manager);
            //通过放射获取 WIFI_AP的开启状态属性
            Field field = manager.getClass().getDeclaredField("WIFI_AP_STATE_ENABLED");
            //获取属性值
            int value = (int) field.get(manager);
            //判断是否开启
            if (state == value) {
                return true;
            } else {
                return false;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 返回热点自身IP地址
     * @return
     */
    public static String getLocalHostIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()&& ip instanceof Inet4Address) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("NetUtil", e.toString());
        }
        return ipaddress;
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
                    ipMacPairs.put(mList.get(3), mList.get(0));
                }
            }
            Log.i(TAG, "ipMacPairs=" + ipMacPairs);
            return ipMacPairs;

        }
        return null;

    }

    //TODO: get from framework res
    private static final String wifi_tether_configure_ssid_default = "androidmifi";
    private static final String def_wifi_wifihotspot_pass = "admin123";

}
