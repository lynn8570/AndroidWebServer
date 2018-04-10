package lynn.andr.webserver.http.bean;

import java.util.List;

public class AndroidAp {
	public static final int OPEN_INDEX = 0;
	public static final int WPA2_INDEX = 1;
	private String ssid;
	private int securityType;
	private String password;
	private List<MDevice> connectedList;
	private List<String> blackList;
	private DataUsage dataUsage;
	//TODO Add field for Camera connected?
	

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public int getSecurityType() {
		return securityType;
	}

	public void setSecurityType(int securityType) {
		this.securityType = securityType;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<MDevice> getConnectedList() {
		return connectedList;
	}

	public void setConnectedList(List<MDevice> connectedList) {
		this.connectedList = connectedList;
	}

	public DataUsage getDataUsage() {
		return dataUsage;
	}

	public void setDataUsage(DataUsage dataUsage) {
		this.dataUsage = dataUsage;
	}

	@Override
	public String toString() {
		return "AndroidAp{ssid=" + this.ssid + ", securityType="
				+ this.securityType + ", password=\'" + this.password + '\''
				+ ", connectedList=" + this.connectedList + ", dataUsage=\'"
				+ this.dataUsage + '}';
	}

	public List<String> getBlackList() {
		return blackList;
	}

	public void setBlackList(List<String> blackList) {
		this.blackList = blackList;
	}

}
