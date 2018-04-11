package lynn.andr.webserver.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.entity.BasicHttpEntity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.fota.iport.MobAgentPolicy;

import lynn.andr.webserver.R;
import lynn.andr.webserver.fota.Constant;
import lynn.andr.webserver.fota.FotaManager;
import lynn.andr.webserver.http.bean.AndroidAp;
import lynn.andr.webserver.http.bean.DataUsage;
import lynn.andr.webserver.http.bean.MDevice;
import lynn.andr.webserver.util.FotaUtil;
import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.SettingUtil;


public class PageEntity {
    private static final String TEMPLATE_RESULT = "result.html";
    private static final String TEMPLATE_SUCCESS = "success.html";
    private static final String KEY_MESSAGE = "$message$";
    private static final String KEY_SUMMARY = "$summary$";
    private static final String KEY_HOMEPAGE = "$homepage$";
    private static final String KEY_ADMIN_PW = "$adminpsw$";
    private static final String KEY_ALERT = "$alert$";
    private static final String KEY_LOGIN = "$login$";
    private static final String KEY_ADMIN = "$admin$";
    private static final String KEY_LIMIT = "$limit$";
    private static final String KEY_CALI = "$calibration$";

    private static final String KEY_LANGUAGE = "$language$";
    private static final String KEY_MAX = "$max$";
    private static final String KEY_MAX_NAME = "$maxname$";

    private static final String KEY_PRG_TITLE = "$progressTitle$";
    private static final String KEY_PRG_MSG = "$progressMsg$";

    private static final String KEY_FOTA_DESC = "$fotadesc$";
    private static final String KEY_FOTA_VERSION = "$fotaversion$";
    // private static final String KEY_FOTA_NAME = "$fotaactionname$";
    // private static final String KEY_FOTA_ACTION = "$fotaaction$";
    private static final String KEY_FOTA_COMFIRM = "$fotacomfirm$";
    private static final String KEY_FOTA_TARGET = "$fotatarget$";


    private Context mContext;

    public PageEntity(Context context) {
        this.mContext = context;

    }


    /**
     * @param htmlTemplet  目标html文件模版
     * @param replaceTable 包含要替换的目标字段 $key$ 和对应的 value
     * @return
     */
    public BasicHttpEntity generateHttpEntity(String htmlTemplet, HashMap<String, String> replaceTable) throws IOException {
        if (TextUtils.isEmpty(htmlTemplet)) {
            return null;
        }
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(generateHtml(htmlTemplet, replaceTable));
        return entity;

    }

    public BasicHttpEntity getFotaPage() throws IOException {
        String pagename = "fotadetail.html";
        HashMap<String, String> table = new HashMap<String, String>();
        generateFotaSnippet(table);
        table.put(KEY_HOMEPAGE, mContext.getString(R.string.back_to_homepage));
//		BasicHttpEntity entity = new BasicHttpEntity();
//		entity.setContent(generateHtml(pagename, table));
//		return entity;
        return generateHttpEntity(pagename, table);
    }

    public BasicHttpEntity getWaitingPage(String title, String msg, String reloadTarget) throws IOException {
        String pagename = "wait.html";
        HashMap<String, String> table = new HashMap<String, String>();
        table.put(KEY_PRG_TITLE, title);
        table.put(KEY_PRG_MSG, msg);
        table.put(KEY_FOTA_TARGET, reloadTarget);
        return generateHttpEntity(pagename, table);
    }

    public BasicHttpEntity getWaitingPage(int titleresid, int msgresid, String reloadTarget) throws IOException {
        String title = mContext.getResources().getString(titleresid);
        String msg = mContext.getResources().getString(msgresid);
        return getWaitingPage(title, msg, reloadTarget);
    }

    public BasicHttpEntity getLoginPage() throws IOException {
        String pagename;

        pagename = "login.html";
        HashMap<String, String> table = new HashMap<String, String>();
//		table.put(KEY_ALERT, mContext.getString(R.string.alert));
//		table.put(KEY_ADMIN_PW, mContext.getString(R.string.adminpw));
//		table.put(KEY_LOGIN, mContext.getString(R.string.login));
        table.put(KEY_ADMIN, SettingUtil.getUsername(mContext));

        Locale currentLocal = SettingUtil.getCurrentLocal(mContext);
        String language = currentLocal.getLanguage();
        Log.i("PageEntity", "getLoginPage()language=" + language);

        table.put(KEY_LANGUAGE, language);

        return generateHttpEntity(pagename, table);


    }

    public BasicHttpEntity getUpdateWare() throws IOException {
        String pagename;

        pagename = "updateWare.html";
//		InputStream open = mContext.getAssets().open(pagename);
        HashMap<String, String> table = new HashMap<String, String>();
        table.put("$curversion$", Constant.version);
        return generateHttpEntity(pagename, table);
    }


    public BasicHttpEntity getIndexPage() throws IOException {
        String pagename = "index.html";
//		String local = mContext.getResources().getConfiguration().locale
//				.toString();
//		Log.i("PageEntity", "local=" + local);
//		if (local.contains("zh")) {
//			pagename = "index_zh.html";
//		} else {
//			pagename = "index.html";
//		}

        HashMap<String, String> table = new HashMap<String, String>();
        generateIndexTable(table);
        generateFotaSnippet(table);
        generateDeivcefoTable(table);
        return generateHttpEntity(pagename, table);

    }

    public BasicHttpEntity getMessagePage(int mesResid) throws IOException {
        return getMessagePage(mContext.getString(mesResid));
    }

    public BasicHttpEntity getMessagePage(String msg) throws IOException {
        HashMap<String, String> table = new HashMap<String, String>();
        table.put(KEY_MESSAGE, msg);
        table.put(KEY_HOMEPAGE, mContext.getString(R.string.back_to_homepage));
        return generateHttpEntity(TEMPLATE_RESULT, table);
    }

    public BasicHttpEntity getSuccessPage(String ssid, String pwd)
            throws IOException {
        HashMap<String, String> table = new HashMap<String, String>();
        String strSuccess = mContext.getString(R.string.message);
        String format = String.format(strSuccess, "<b>" + ssid + "</b>",
                pwd == null ? "<b>null</b>" : "<b>" + pwd + "</b>");
        table.put(KEY_MESSAGE, format);
        table.put(KEY_SUMMARY, mContext.getString(R.string.summary));
        table.put(KEY_HOMEPAGE, mContext.getString(R.string.back_to_homepage));
        return generateHttpEntity(TEMPLATE_RESULT, table);

    }

    private void generateDeivcefoTable(HashMap<String, String> table) {
        if (table == null) return;
        table.put("$curversion$", Constant.version);
        TelephonyManager manager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String deviceId = manager.getDeviceId();
        String number = manager.getLine1Number();
        table.put("$deivceid$", deviceId == null || deviceId.isEmpty() ?
                mContext.getString(R.string.unknown) : deviceId);
        String subscriberId = manager.getSubscriberId();
        //Log.i("PageEntity", "getSubscriberId"+subscriberId);
        table.put("$phonenumber$", number == null || number.isEmpty() ?
                mContext.getString(R.string.unknown) : number);
    }

    public void generateIndexTable(HashMap<String, String> table) {
//		AndroidAp androidAp = NetUtil.getAndroidAp(mContext);
        AndroidAp androidAp = new AndroidAp();//TODO
        if (androidAp == null) {
            table = null;
        } else {
            table.put("$SSID$", androidAp.getSsid());
            table.put("$security$", androidAp.getSecurityType() + "");
            table.put("$password$", androidAp.getPassword());

            int conectionlistSize = 0;
            if (androidAp.getConnectedList() != null
                    && !androidAp.getConnectedList().isEmpty()) {

                conectionlistSize = androidAp.getConnectedList().size();
                table.put("$connected$",
                        String.valueOf(conectionlistSize));
                String block = mContext.getString(R.string.block);
                table.put("$connectedlist$",
                        generateConnList(androidAp.getConnectedList(), block, mContext));
            } else {
                table.put("$connected$", "0");
                table.put("$connectedlist$", "");
            }

            if (androidAp.getBlackList() != null
                    && !androidAp.getBlackList().isEmpty()) {
                table.put("$block$",
                        String.valueOf(androidAp.getBlackList().size()));
                String unblock = mContext.getString(R.string.unblock);
                table.put("$blocklist$",
                        generateBlockList(androidAp.getBlackList(), unblock, mContext, conectionlistSize));
            } else {
                table.put("$block$", "0");
                table.put("$blocklist$", "");
            }

            DataUsage dataUsage = androidAp.getDataUsage();
            if (dataUsage != null) {
//				table.put("$data$", +dataUsage.getUsagePercent() + "%");
                int totalresult = (int) (dataUsage.getTotal() / 1024);
                int useddata = (int) (dataUsage.getUseddata() / 1024);
                table.put("$total$", totalresult + "");
                //table.put("$data$", (dataUsage.getUsagePercent()*totalresult/100)+"");
                table.put("$data$", useddata + "");
                table.put(KEY_LIMIT, dataUsage.getLimit_type() + "");
                table.put(KEY_CALI, dataUsage.getCali_type() + "");
            } else {
                table.put("$data$", "0%");
                table.put("$data$", "0");
                table.put(KEY_LIMIT, 1 + "");
                table.put(KEY_CALI, 0 + "");
            }

        }

        table.put(KEY_LANGUAGE, SettingUtil.getCurrentLocal(mContext)
                .getLanguage());
        int max = 5;// TODO NetUtil.getMax(mContext);
        table.put(KEY_MAX, max + "");
        String maxidname = "five";
        switch (max) {
            case 1:
                maxidname = "one";
                break;
            case 2:
                maxidname = "two";
                break;
            case 3:
                maxidname = "three";
                break;
            case 4:
                maxidname = "four";
                break;
            default:
                break;
        }
        table.put(KEY_MAX_NAME, maxidname);
        table.put(KEY_ADMIN, SettingUtil.getUsername(mContext));


        Log.i("table = ", table.toString());
    }

    private static final String HTML_VERSION = "<span  class=\"versionname\"><span class=\"versionlabel\">%1$s:</span>%2$s</span >";
    private static final String HTML_BUTTON = "<input type=\"button\" value=\"%1$s\" class=\"btnLogin\" onclick=\"%2$s;\"/>";


    private String getButtonHtml(int buttonResid, String action) {
        return String.format(HTML_BUTTON, mContext.getString(buttonResid), action);
    }

    private String getFotoInfoHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(HTML_VERSION,
                mContext.getString(R.string.fota_versionname),
                MobAgentPolicy.getRelNotesInfo().version));
        sb.append(String.format(HTML_VERSION,
                mContext.getString(R.string.fota_publish),
                MobAgentPolicy.getRelNotesInfo().publishDate));
        sb.append(String.format(HTML_VERSION,
                mContext.getString(R.string.fota_filesize),
                MobAgentPolicy.getVersionInfo().fileSize));
        String realContent = FotaUtil.getRealContent(MobAgentPolicy.getRelNotesInfo().content);
        sb.append(String.format(HTML_VERSION,
                mContext.getString(R.string.fota_content),
                realContent == null ? mContext.getString(R.string.unknown) : realContent));
        return sb.toString();
    }

    private String getFotoSnippetHtml(FotaManager.OtaState otaState) {
        StringBuilder sb = new StringBuilder();
        switch (otaState) {
            case DOWNLOAD_FINISH:

                sb.append(getFotoInfoHtml());
                sb.append(getButtonHtml(R.string.check_for_updates, "checkversion()"));
                sb.append(getButtonHtml(R.string.upgrade, "alertupgrade()"));
                break;

            case CHECK_SUCCESS:
            case DOWNLOADING:
            case DOWNLOAD_ERROR:
                sb.append(getFotoInfoHtml());
                sb.append(getButtonHtml(R.string.check_for_updates, "checkversion()"));
                sb.append(getButtonHtml(R.string.download, "download()"));
                break;

            case UNCHECK:
            case CHECKING_VERSION:
            case CHECK_FAIL:
            case CHECK_IVAlIDATE:
            case ERROR_STATE:
                if (FotaManager.getFotaResult().operationTime != null) {
                    sb.append(String.format(HTML_VERSION,
                            mContext.getString(R.string.latest_check_time),
                            getFormatTime(FotaManager.getFotaResult().operationTime)));
                } else {
                    sb.append("");
                }
                sb.append(getButtonHtml(R.string.check_for_updates, "checkversion()"));
                break;
            default:
                break;
        }
        return sb.toString();
    }

    private void generateFotaSnippet(Map<String, String> table) {

        if (table == null) return;
        FotaManager.FotaResult fotaResult = FotaManager.getFotaResult();
        if (fotaResult == null) {
            return;
        }
        switch (fotaResult.otaState) {
            case REBOOT_UPGRADE:
                table.put(KEY_FOTA_DESC,
                        mContext.getString(R.string.upgrade));
                table.put(KEY_FOTA_VERSION, mContext.getString(R.string.upgrading));
                table.put(KEY_FOTA_COMFIRM, "");
                break;
            case DOWNLOAD_FINISH:
                table.put(KEY_FOTA_DESC,
                        mContext.getString(R.string.download_success));
                table.put(KEY_FOTA_VERSION, getFotoSnippetHtml(fotaResult.otaState));
                String comfirmFormat = mContext.getString(R.string.comfirm);
                String comfirm = String.format(comfirmFormat,
                        MobAgentPolicy.getVersionInfo().versionName);
                table.put(KEY_FOTA_COMFIRM, comfirm);
                break;

            case CHECK_SUCCESS:
            case DOWNLOADING:
            case DOWNLOAD_ERROR:
                table.put(KEY_FOTA_DESC,
                        mContext.getString(R.string.found_new_version));
                table.put(KEY_FOTA_VERSION, getFotoSnippetHtml(fotaResult.otaState));
                // table.put(KEY_FOTA_NAME, mContext.getString(R.string.download));
                // table.put(KEY_FOTA_ACTION, "download();");
                table.put(KEY_FOTA_COMFIRM, "");
                break;

            case UNCHECK:
            case CHECKING_VERSION:
            case CHECK_FAIL:
            case CHECK_IVAlIDATE:
            case ERROR_STATE:
                table.put(KEY_FOTA_DESC,
                        mContext.getString(R.string.newest_version));
                table.put(KEY_FOTA_VERSION, getFotoSnippetHtml(fotaResult.otaState));
                table.put(KEY_FOTA_COMFIRM, "");
            default:
                break;
        }
        Log.i("table 2 = ", table.toString());
    }

    private static final String DATE_FORMAT = "yyyy-MM-dd   HH:mm:ss ";

    private String getFormatTime(Date date) {
        if (date == null) return null;
        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        String time = df.format(date);
        return time;
    }

    public static String generateConnList(List<MDevice> list, String block, Context context) {
        if (list == null || list.isEmpty()) {
            // return "<span class=\"textgrey\">0 connected</span>";
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            try {

                for (int i = 0; i < list.size(); i++) {
                    // sb.append("<li>");
                    // sb.append(list.get(i));
                    // sb.append("<input type=\"button\" id=\""
                    // + list.get(i)
                    // + "\" onclick=\"setBlock(this)\" value=\""+block
                    // +"\" class=\"btnlock\"/></li>\r\n");

                    MDevice mdevice = list.get(i);
                    String snapet = getSnapet("snippet/connect.txt", context);
                    if (mdevice != null) {
                        snapet = snapet.replace("$strdnum$", "0" + (i + 1));
                        snapet = snapet.replace("$strmac$", mdevice.getStrMac());
                        snapet = snapet.replace("$strid$", mdevice.getStrMac());
                        snapet = snapet.replace("$strdevice$", mdevice.getDevicename());
                        snapet = snapet.replace("$strip$", mdevice.getStrIP() == null ?
                                context.getString(R.string.unknown)
                                : mdevice.getStrIP());
                    }

                    sb.append(snapet);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sb.append("<li>Generate Connection List Error!!</li>");
            }

            return sb.toString();
        }
    }

    public static String generateBlockList(List<String> list, String unblock, Context context, int strnumbase) {
        if (list == null || list.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            try {

                for (int i = 0; i < list.size(); i++) {
//				sb.append("<li>");
//				sb.append(list.get(i));
//				sb.append("<input type=\"button\" id=\""
//						+ list.get(i)
//						+ "\" onclick=\"setUnBlock(this)\" value=\""+unblock+"\" class=\"btnlock\"/></li>\r\n");
                    String snapet = getSnapet("snippet/blacklist.txt", context);
                    snapet = snapet.replace("$strdnum$", "0" + (i + 1 + strnumbase));
                    snapet = snapet.replace("$strmac$", list.get(i));
                    snapet = snapet.replace("$strid$", list.get(i));
                    //snapet = snapet.replace("$strdevice$", list.get(i).getDevicename());
                    //snapet = snapet.replace("$strip$", list.get(i).getStrIP());
                    sb.append(snapet);
                }
            } catch (Exception e) {
                e.printStackTrace();
                sb.append("<li>Generate BlackList Error!!</li>");
            }
            return sb.toString();
        }
    }

    public static String getSnapet(String filename, Context context) {
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();

            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Convert the buffer into a string.
            String snapet = new String(buffer, "UTF-8");

            Log.i("PageEntity", "getSnapet=" + snapet);
            return snapet;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public InputStream generateHtml(String template,
                                    HashMap<String, String> table) throws IOException {
        InputStream inputStream = mContext.getAssets().open(template);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                inputStream));
        StringBuilder sb = new StringBuilder();
        String str;
        while ((str = reader.readLine()) != null) {
            List<String> variable = getVariable(str);
            if (variable != null) {
                Log.i("PageEntity", "variable = " + variable);
                for (Iterator iterator = variable.iterator(); iterator
                        .hasNext(); ) {
                    String var = (String) iterator.next();
                    if (table.containsKey(var)) {
                        String value = table.get(var);

                        str = str.replace(var, value == null ? "null" : value);

                    }

                }
            }
            Log.i("PageEntity", "str = " + str);
            sb.append(str + "\r\n");
        }
        //Log.w("html", sb.toString());
        InputStream is = new ByteArrayInputStream(sb.toString().getBytes(
                "UTF-8"));
        return is;
    }

    public static List<String> getVariable(String str) {
        List<String> result = null;
        Pattern p = Pattern.compile("\\$[a-zA-Z0-9]+\\$");
        Matcher matcher = p.matcher(str);

        while (matcher.find()) {
            if (result == null)
                result = new ArrayList<String>();

            result.add(matcher.group());

        }
        return result;
    }

}
