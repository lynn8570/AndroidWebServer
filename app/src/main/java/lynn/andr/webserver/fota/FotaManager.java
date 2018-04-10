package lynn.andr.webserver.fota;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.fota.iport.MobAgentPolicy;
import com.fota.iport.inter.ICheckVersionCallback;
import com.fota.iport.inter.IOnDownloadListener;
import com.fota.iport.service.DLService;


import java.io.File;
import java.util.Date;

import lynn.andr.webserver.util.Log;
import lynn.andr.webserver.util.SaveFotaUtil;

public class FotaManager {

	private static final String TAG="FotaManager";
	public static String s_update_package_absolute_path;
	
	private static Context mContext;
	public FotaManager(Context context) {
		this.mContext=context;
		s_update_package_absolute_path= SaveFotaUtil.get_update_file_path();
		Log.i(TAG, "FotaManager.path="+s_update_package_absolute_path);
		fotaResult=SaveFotaUtil.getFotaResult(mContext);
		SaveFotaUtil.set_fota_log_path();
	}

	public enum OtaState{
		UNCHECK,
		CHECKING_VERSION,
		CHECK_SUCCESS,CHECK_FAIL,CHECK_IVAlIDATE,
		DOWNLOADING,DOWNLOAD_FINISH,DOWNLOAD_ERROR,
		REBOOT_UPGRADE,
		ERROR_STATE
	}
	
	public static class FotaResult{
		public OtaState otaState;
		public String msg;
		public int code;
		public Date operationTime;
		
		public void setFotaResult(OtaState otaState,String msg,int code,Date date){
			this.otaState=otaState;
			this.msg=msg;
			this.code=code;
			if(date!=null){
				this.operationTime=date;
			}
			if(otaState==OtaState.REBOOT_UPGRADE){
				SaveFotaUtil.clearFotaResult(mContext);
			}else{
				SaveFotaUtil.saveFotaResult(FotaManager.getFotaResult(),mContext);
			}
			
		}
		@Override
		public String toString() {
			return "FotaResult[otaState="+otaState+" msg="+msg+" operationTime="+operationTime+"]";
		}
	}
	
	private static  FotaResult fotaResult;
	public static FotaResult getFotaResult(){
		if(fotaResult!=null) {
			Log.i(TAG, fotaResult.toString());
		}else
		{
			Log.i(TAG, "fota result == null");
		}
		
		return fotaResult;
	}
	
	
	
	public void check_version() {
		if(fotaResult.otaState==OtaState.CHECKING_VERSION){
			Log.e(TAG, "check_version is already checking...");
			return;
		}
		
		fotaResult.setFotaResult(OtaState.CHECKING_VERSION, null, 0,new Date());
		new Thread(){
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				MobAgentPolicy.checkVersion(mContext,new ICheckVersionCallback() {
					
					@Override
					public void onInvalidDate() {
						Log.d(TAG, "onInvalidDate" );
						fotaResult.setFotaResult(OtaState.CHECK_IVAlIDATE, null, 0,null);
					
					}
					
					@Override
					public void onCheckSuccess(int i) {
						Log.d(TAG, "onCheckSuccess i="+i);
						fotaResult.setFotaResult(OtaState.CHECK_SUCCESS, null, i,null);
					}
					
					@Override
					public void onCheckFail(int i, String msg) {
						Log.d(TAG, "onCheckFail i="+i+" msg="+msg);
						fotaResult.setFotaResult(OtaState.CHECK_FAIL, msg, i,null);
						
					}
				});
			};
		}.start();
		
	}

	private static final String ACTION_DOWNLOAD="com.zowee.action.DOWNLOAD";
	private static final String DOWNLOAD_STATE="state";
	private static final int TYPE_DOWNLOAD_SUCESS=45;
	private static final int TYPE_DOWNLOAD_FAILED=46;
	private static final int TYPE_DOWNLOADING=47;
	private void sendDownloadBroadCast(int state){
		Intent intent = new Intent(ACTION_DOWNLOAD);
		intent.putExtra(DOWNLOAD_STATE, state);
		mContext.sendBroadcast(intent);
	}
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void download() {
    	if(fotaResult.otaState==OtaState.DOWNLOADING){
    		Log.e(TAG, " is downloading");
    		return;
    	}
    	
    	if(fotaResult.otaState!=OtaState.CHECK_SUCCESS&&
    			fotaResult.otaState!=OtaState.DOWNLOAD_ERROR){
    		Log.e(TAG, " OtaState error");
    		return;
    	}
    	fotaResult.setFotaResult(OtaState.DOWNLOADING, null, 0,null);
    	sendDownloadBroadCast(TYPE_DOWNLOADING);
    	Log.i(TAG, "s_update_package_absolute_path="+s_update_package_absolute_path);
    	if(s_update_package_absolute_path==null||s_update_package_absolute_path.isEmpty()){
    		s_update_package_absolute_path=SaveFotaUtil.get_update_file_path();	
    	}
    	
        File file = new File(s_update_package_absolute_path);
        Log.i(TAG, "s_update_package_absolute_path="+s_update_package_absolute_path);
        Log.i(TAG, "file.exists="+file.exists());
        boolean existSDCard = SaveFotaUtil.ExistSDCard();
        Log.i(TAG, "ExistSDCard"+existSDCard);
        long sdFreeSize = SaveFotaUtil.getSDFreeSize(mContext);
        Log.i(TAG, "sdFreeSize"+sdFreeSize); //MB
        if(!existSDCard||"".equals(s_update_package_absolute_path)){
        	m_downloadListener.onDownloadError(ErrorCode.DOWNLOAD_ERROR_SD_NOT_EXIT);
        	return ;
        }    
        
        int filesize = MobAgentPolicy.getVersionInfo().fileSize;
        long filesizeMB= filesize/1024/1024;
        if(sdFreeSize<filesizeMB+10){
        	 Log.i(TAG, "sdFreeSize"+sdFreeSize);
        	 Log.i(TAG, "filesizeMB"+filesizeMB);
        	 m_downloadListener.onDownloadError(ErrorCode.DOWNLOAD_ERROR_SD_SPACE_LOW);
        	 return;
        }
        Log.i(TAG, "mContex="+mContext);
        Log.i(TAG, "deltaUrl="+MobAgentPolicy.getVersionInfo().deltaUrl);
        Log.i(TAG, "file.getParentFile()="+file.getParentFile());
        Log.i(TAG, "file.getName()="+file.getName());
        Log.i(TAG, "m_downloadListener="+m_downloadListener);
        DLService.start(mContext, MobAgentPolicy.getVersionInfo().deltaUrl,
                file.getParentFile(), file.getName(), m_downloadListener);
    }
    
    
    public void reboot_upgrade() {
    	if(fotaResult.otaState==OtaState.REBOOT_UPGRADE){
    		Log.e(TAG, "is REBOOT_UPGRADE ing");
    		return;
    	}
    	if(fotaResult.otaState!=OtaState.DOWNLOAD_FINISH){
    		Log.e(TAG, "download not finish");
    		return;
    	}
    	 File file = new File(s_update_package_absolute_path);
    	 Log.i(TAG, "file.exists="+file.exists());
    	 if(file.exists()){
    		 fotaResult.setFotaResult(OtaState.REBOOT_UPGRADE, null, 0,new Date());
    	     MobAgentPolicy.rebootUpgrade(mContext, s_update_package_absolute_path);
    	 }else{
    		 fotaResult.setFotaResult(OtaState.ERROR_STATE, null, 0,new Date());
    	 }
    	
    }

    public void download_pause() {
        DLService.download_pause();
    }

    public void download_cancel() {
        DLService.download_cancel();
    }

    public void download_resume() {
        DLService.download_resume();
    }
    
    IOnDownloadListener m_downloadListener = new IOnDownloadListener() {
        @Override
        public void onDownloadProgress(String s, int i, int i1) {
            int progress = (int) (i1 * 100.0f / i);
//            m_view.show_downloading(progress);
            Log.i(TAG, "onDownloadProgress s="+s+" progress="+progress);
            fotaResult.setFotaResult(OtaState.DOWNLOADING, null, progress,null);
        }

        @Override
        public void onDownloadFinished(int i, File file) {
//            m_view.show_can_upgrade();
//            if (PolicyManager.is_auto_upgrade()) {
//                reboot_upgrade();
//            }
        	Log.i(TAG, "onDownloadFinished i="+i+" file="+file);
        	 fotaResult.setFotaResult(OtaState.DOWNLOAD_FINISH, null, i,null);
        	 sendDownloadBroadCast(TYPE_DOWNLOAD_SUCESS);
        }

        @Override
        public void onDownloadError(int i) {
//            m_view.show_can_download();
//            m_view.show_error("error code = " + i);
        	Log.i(TAG, "onDownloadError i="+i);
        	fotaResult.setFotaResult(OtaState.DOWNLOAD_ERROR, null, i,null);
        	sendDownloadBroadCast(TYPE_DOWNLOAD_FAILED);
        }
    };
}
