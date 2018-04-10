package lynn.andr.webserver.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.StatFs;

import com.fota.iport.Trace;

import java.io.File;
import java.util.Date;

import lynn.andr.webserver.fota.FotaManager;
import lynn.andr.webserver.fota.FotaManager.OtaState;

public class SaveFotaUtil {


	private static final String PREFERENCE_FOTA = "fotaResult";
	private static final String PREFERENCE_FOTA_STATE = "otaState";
	private static final String PREFERENCE_FOTA_MSG = "fotamsg";
	private static final String PREFERENCE_FOTA_CODE = "fotacode";
	private static final String PREFERENCE_FOTA_TIME = "operationTime";

	

	public static void saveFotaResult(FotaManager.FotaResult fotaResult, Context context) {
		if (fotaResult != null) {
			SharedPreferences preferences = context.getSharedPreferences(
					PREFERENCE_FOTA, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();

			editor.putString(PREFERENCE_FOTA_MSG, fotaResult.msg);
			editor.putInt(PREFERENCE_FOTA_CODE, fotaResult.code);
			// editor.putInt(PREFERENCE_FOTA_STATE, fotaResult.msg);
			if (fotaResult.operationTime != null) {
				editor.putLong(PREFERENCE_FOTA_TIME,
						fotaResult.operationTime.getTime());
			}
			editor.putInt(PREFERENCE_FOTA_STATE,
					getOtaStateInt(fotaResult.otaState));
			editor.commit();

		}
	}
	
	public static void clearFotaResult(Context context) {
			SharedPreferences preferences = context.getSharedPreferences(
					PREFERENCE_FOTA, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.clear();
			editor.commit();
	}

	public static FotaManager.FotaResult getFotaResult(Context context) {
		FotaManager.FotaResult fotaResult = new FotaManager.FotaResult();

		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCE_FOTA, Context.MODE_PRIVATE);
		fotaResult.msg = preferences.getString(PREFERENCE_FOTA_MSG, null);
		fotaResult.code = preferences.getInt(PREFERENCE_FOTA_CODE, 0);
		long timelong = preferences.getLong(PREFERENCE_FOTA_TIME, 0);
		if (timelong == 0) {
			fotaResult.operationTime = null;
		} else {
			fotaResult.operationTime = new Date(timelong);
		}
		fotaResult.otaState = getOtaState(preferences.getInt(
				PREFERENCE_FOTA_STATE, 0));
		return fotaResult;

	}

	private static int getOtaStateInt(OtaState otaState) {
		switch (otaState) {
		case UNCHECK:
			return 0;
		case CHECKING_VERSION:
			return 1;
		case CHECK_SUCCESS:
			return 2;
		case CHECK_FAIL:
			return 3;
		case CHECK_IVAlIDATE:
			return 4;
		case DOWNLOADING:
			return 5;
		case DOWNLOAD_FINISH:
			return 6;
		case DOWNLOAD_ERROR:
			return 7;
		case REBOOT_UPGRADE:
			return 8;
		case ERROR_STATE:
			return 9;
		default:
			break;
		}
		return 0;
	}

	private static OtaState getOtaState(int otaStatevalue) {
		switch (otaStatevalue) {
		case 0:
			return OtaState.UNCHECK;
		case 1:
			return OtaState.CHECKING_VERSION;
		case 2:
			return OtaState.CHECK_SUCCESS;
		case 3:
			return OtaState.CHECK_FAIL;
		case 4:
			return OtaState.CHECK_IVAlIDATE;
		case 5:
			return OtaState.DOWNLOADING;
		case 6:
			return OtaState.DOWNLOAD_FINISH;
		case 7:
			return OtaState.DOWNLOAD_ERROR;
		case 8:
			return OtaState.REBOOT_UPGRADE;
		case 9:
			return OtaState.ERROR_STATE;
		default:
			break;
		}
		return OtaState.UNCHECK;
	}

	public static boolean ExistSDCard() {
		File testfile = new File(SD_PATH + "/fotatest.txt");
		Log.d("linlian", " file :" + testfile.getPath());
		try {
			if (testfile.exists()) {
				testfile.delete();
			}
			Log.d("linlian", " testfile.exists():" + testfile.exists());
			if (!testfile.createNewFile()) {
				Log.d("linlian", " createNewFile file... false");
				return false;
			}
			Log.d("linlian", " checkExterSDPath ... true");
			return true;

		} catch (Exception e) {
			Log.d("linlian", " Exception :" + e);
			e.printStackTrace();
		}

		Log.d("linlian", " checkExterSDPath ... false");
		return false;

	}

	public static final String SD_PATH = "/storage/sdcard1";

	public static long getSDFreeSize(Context context) {
//		StorageManager mStorageManager = StorageManager.from(context);
//
//		final StorageVolume[] storageVolumes = mStorageManager.getVolumeList();
//		for (StorageVolume volume : storageVolumes) {
//
//			String path = volume.getPath();
//			boolean isPrimary = volume.isPrimary();
//			long getMaxFileSize = volume.getMaxFileSize();
//			boolean isEmulated = volume.isEmulated();
//			Log.i("linlian", "path=" + path);
//			Log.i("linlian", "isPrimary=" + isPrimary);
//			Log.i("linlian", "getMaxFileSize=" + getMaxFileSize);
//			Log.i("linlian", "isEmulated=" + isEmulated);
//		}
		try {
			StatFs sf = new StatFs(SD_PATH);
			long blockSize = sf.getBlockSize();
			long freeBlocks = sf.getAvailableBlocks();
			// return freeBlocks * blockSize; //Byte
			// return (freeBlocks * blockSize)/1024; //KB
			Log.i("linlian", "freeBlocks * blockSize=" + freeBlocks * blockSize);
			return (freeBlocks * blockSize) / 1024 / 1024; // MB

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String get_update_file_path() {
		String path = "";

		path = SD_PATH + "/adupsfota/update.zip";

		File file = new File(path);
		if (!file.getParentFile().exists()) {
			if (!file.getParentFile().mkdirs())
				path = "";
		}
		Log.i("SaveFotaUtil", "path=" + path);
		return path;
	}
	
	public static void set_fota_log_path(){
		String path = SD_PATH + "/iport_log.txt";
		Trace.log_path=path;
	}
	
}
