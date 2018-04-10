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
import org.json.JSONException;
import org.json.JSONObject;

import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.content.Context;

import lynn.andr.webserver.R;
import lynn.andr.webserver.util.Log;


public class MifiPushTest extends RequestHandler {

	private static final String TAG = "MifiPushTest";
	String param_toekn="access_token";
	String param_imei="imei";
	public MifiPushTest(Context context) {
		super(context);
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		super.handle(request, response, context);

		String target = parseTarget(request);
		Log.i(TAG, "MifiPushTest target=" + target);

		Map<String, String> parseParameter = parseParameter(request);
		String token = parseParameter.get(param_toekn);
		String imei = parseParameter.get(param_imei);
		Log.i(TAG, "MifiPushTest token=" + token);
		Log.i(TAG, "MifiPushTest imei=" + imei);
		JSONObject jsonObject = new JSONObject();

		JSONObject meta = new JSONObject();
		try {
			meta.put("error_type", 1);
			meta.put("code", 200);
			meta.put("error_message", "");
			jsonObject.put("meta", meta);
				if("/live/mifipush".equalsIgnoreCase(target)){
			jsonObject
					.put("data",
							"rtmp://w.gslb.lecloud.com/live/2016053030000027899?sign=d94c9c747febcb1cd6ae169d9969b331&tm=20160530181702");
				}else{
					jsonObject
					.put("data",getRtmpdata());
				}
			response.setStatusCode(HttpStatus.SC_OK);
			response.setEntity(new StringEntity(jsonObject.toString()));
			return;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		responeMessage(response, R.string.error);
	}
	private int getRtmpdata(){
		int data = (int) (Math.random()*4);
		return data;
	}
}
