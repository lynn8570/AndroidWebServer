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

import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.content.Context;

import lynn.andr.webserver.R;
import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.NetUtil;


public class SubmitHandler extends RequestHandler {

	private static final String TAG = "SubmitHandler";
	// security=2, password=12345678, SSID=AndroidAP


	public SubmitHandler(Context context) {
		super(context);
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		super.handle(request, response, context);

		if (!checkPermission(request, response)) {
			responeMessage(response, R.string.permission_deny);
			return;
		}
		final WifiConfiguration config = getConfig(request);
		Log.i(TAG, "config=" + config);
		if (config != null) {
			response.addHeader("Set-Cookie", "userID=null; Max-Age="
					+ NetUtil.COOKIE_TIMEOUT);
			responeSuccess(response, config.SSID, config.preSharedKey);
//			NetUtil.startSetWifi(mContext,config); to set ap settings
			
			
		} else {
			responeMessage(response, R.string.error);
		}

	}
	
	

	private WifiConfiguration getConfig(HttpRequest request) {
		Map<String, String> parameter = null;
		try {
			parameter = parseParameter(request);
//			return NetUtil.getConfig(parameter);
			return null;// todo return WifiConfiguration
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
