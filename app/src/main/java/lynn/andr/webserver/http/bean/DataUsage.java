package lynn.andr.webserver.http.bean;

public class DataUsage {

	public static final int LIMIT_TYPE_ALERT = 1;
	public static final int LIMIT_TYPE_TURN_OFF = 2;
	public static final int LIMIT_TYPE_IGNORE = 3;

	private long total;// unit KB 1 * 100 * 1024 ==100M
	private long useddata;
	private int usagePercent;// 0~100;

	private int cleanupDate;// 0~30; rarely modified

	private int limit_type;
	
	private int cali_type;

	public int getCali_type() {
		return cali_type;
	}

	public void setCali_type(int cali_type) {
		this.cali_type = cali_type;
	}

	public int getLimit_type() {
		return limit_type;
	}

	public void setLimit_type(int limit_type) {
		this.limit_type = limit_type;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public int getUsagePercent() {
		return usagePercent;
	}

	public void setUsagePercent(int usagePercent) {
		this.usagePercent = usagePercent;
	}

	public int getCleanupDate() {
		return cleanupDate;
	}

	public void setCleanupDate(int cleanupDate) {
		this.cleanupDate = cleanupDate;
	}

	public long getUseddata() {
		return useddata;
	}

	public void setUseddata(long useddata) {
		this.useddata = useddata;
	}
	
	
}
