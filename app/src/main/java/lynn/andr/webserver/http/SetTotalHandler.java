package lynn.andr.webserver.http;

import android.content.Context;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.Map;

import lynn.andr.webserver.R;
import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.NetUtil;


public class SetTotalHandler extends RequestHandler {

	private static final String TAG = "SetTotalHandler";
	private static final String KEY_TOTAL="total";
	private static final String KEY_LIMIT="limit";
	private static final String KEY_CALI="calibration";

	
	
	public SetTotalHandler(Context context) {
		super(context);
	}

	@Override
	public void handle(HttpRequest request, HttpResponse response,
			HttpContext context) throws HttpException, IOException {
		super.handle(request, response, context);
		
		if(!checkPermission(request, response)){
			responeMessage(response, R.string.permission_deny);
			return;
		}
		
		Map<String, String> parameter = parseParameter(request); 
		String total = parameter.get(KEY_TOTAL);
		String limittype = parameter.get(KEY_LIMIT);
		String cali = parameter.get(KEY_CALI);
		Log.i(TAG, "total="+total);
		Log.i(TAG, "limittype="+limittype);
		Log.i(TAG, "cali="+cali);
		Integer integerTotal;
		Integer integerLimitType;
		Integer integerCali;
		try{
			
			integerTotal = Integer.parseInt(total);
			if(integerTotal>0){
				Log.i(TAG, "setDataUsageTotal longTotal="+integerTotal);
				NetUtil.setDataUsageTotal(mContext, integerTotal*1024);
			}
			
			integerLimitType = Integer.parseInt(limittype);
			if(integerLimitType>0&&integerLimitType<4){
				Log.i(TAG, "setDataUsageTotal limittype="+integerLimitType);
				NetUtil.setLimitType(mContext,integerLimitType);
			}
			
			integerCali = Integer.parseInt(cali);
			if(integerCali>=0&&integerCali<=7){
				Log.i(TAG, "setDataUsageTotal setCali="+integerCali);
				NetUtil.setCali(mContext,integerCali);
			}
			
			responeMessage(response, R.string.success_setting);
			return;
		}catch(NumberFormatException e){
			e.printStackTrace();
			responeMessage(response, R.string.error_input);
			return;
		}catch (Exception e) {
			e.printStackTrace();
			responeMessage(response, R.string.error);
			return;
		}
		
	}

	
}
