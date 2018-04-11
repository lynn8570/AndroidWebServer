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

import lynn.andr.webserver.http.bean.DataUsage;

public class SettingUtil {
    private static final String DEFAULT_PWD = "adminpwd";//admin default password
    private static final String DEFAULT_ADMIN = "Admin";
    private static final String PREFERENCE_ADMIN = "admin";
    private static final String PREFERENCE_PASSWORD = "password";
    private static final String PREFERENCE_USERNAME = "username";

    private static final String SETINGS_TOKEN = "le_token";
    private static final String SETINGS_LE_HOST = "le_host";

    //datausage
    public static final String AUTO_ADJUEST_CYCLE = "auto_adjuest_cycle";
    private static final String DATA_USAGE_START_DATE = "data_usage_start_date";
    private static final String DATA_USAGE_TOTAL_QUANTITY = "data_usage_total_quantity";
    private static final String DATA_USAGE_SCALE = "data_usage_scale";
    private static final String DATA_USAGE_LIMIT_TYPE = "data_usage_limit_type";
    public static final String DATA_USAGE_USE = "data_usage_use";

    private static final String TAG = "SettingUtil";

    public static String getPassword(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCE_ADMIN, Context.MODE_PRIVATE);

        return preferences.getString(PREFERENCE_PASSWORD, DEFAULT_PWD);
    }

    public static void setLeHost(Context context, String host) {
        if (host == null || host.isEmpty()) {
            Settings.System.putString(context.getContentResolver(),
                    SETINGS_LE_HOST, "");
        } else {
            Settings.System.putString(context.getContentResolver(),
                    SETINGS_LE_HOST, host);
        }
    }

    public static String getLeHost(Context context) {
        String lehost = Settings.System.getString(context.getContentResolver(),
                SETINGS_LE_HOST);
        if (lehost == null) lehost = "";
        return lehost;
    }

    public static void setToken(Context context, String token) {
        if (token != null && !token.isEmpty()) {
            Settings.System.putString(context.getContentResolver(),
                    SETINGS_TOKEN, token);
        }
    }

    public static String getToken(Context context) {
        return Settings.System.getString(context.getContentResolver(),
                SETINGS_TOKEN);
    }


    public static String getUsername(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCE_ADMIN, Context.MODE_PRIVATE);
        return preferences.getString(PREFERENCE_USERNAME, DEFAULT_ADMIN);
    }

    public static void clearPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCE_ADMIN, Context.MODE_PRIVATE);
        Editor edit = preferences.edit();
        edit.clear();
        edit.commit();
    }

    public static boolean setAdmin(Context context, String pwd, String username) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCE_ADMIN, Context.MODE_PRIVATE);
        if (pwd != null && !pwd.isEmpty()
                && username != null && !username.isEmpty()) {
            Editor edit = preferences.edit();
            edit.putString(PREFERENCE_PASSWORD, pwd);
            edit.putString(PREFERENCE_USERNAME, username);
            edit.commit();
            return true;
        }
        return false;
    }

    public static Locale getCurrentLocal(Context context) {

        Configuration config = context.getResources().getConfiguration();
        Locale currentLocale = config.locale;
        return currentLocale;
    }

    public static void setLocal(Context context, Locale setLocale) {
        Configuration config = context.getResources().getConfiguration();
        if (config.locale != setLocale) {
            config.locale = setLocale;
            context.getResources().updateConfiguration(config, null);
        }
    }

    /**
     * @param context
     * @param type    0 trun off; 1,2,3,4,5,6,7days
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

        int calitype = Settings.System.getInt(context.getContentResolver(),
                AUTO_ADJUEST_CYCLE, 0);

        long useddata = Settings.System.getLong(context.getContentResolver(),
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
}
