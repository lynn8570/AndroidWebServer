package lynn.andr.webserver.http.bean;


import lynn.andr.webserver.util.Log;

public class MDevice {
	private String devicename;
	private String strIP;
	private String strMac;
	private boolean isLiveman;
	
	public MDevice(){
		
	}
	public MDevice(String devicename,String strMac){
		this.devicename=devicename;
		this.strMac=strMac;
		Log.i("MDevice", "new MDevice this.devicename="+this.devicename+" this.strMac="+this.strMac);
	}
	public String getDevicename() {
		return devicename;
	}
	public void setDevicename(String devicename) {
		this.devicename = devicename;
	}
	public String getStrIP() {
		return strIP;
	}
	public void setStrIP(String strIP) {
		this.strIP = strIP;
	}
	public String getStrMac() {
		return strMac;
	}
	public void setStrMac(String strMac) {
		this.strMac = strMac;
	}
	public boolean isLiveman() {
		return isLiveman;
	}
	public void setLiveman(boolean isLiveman) {
		this.isLiveman = isLiveman;
	}
	

	@Override
	public boolean equals(Object o) {
		if(o instanceof MDevice){
			if(strMac!=null&&strMac.trim().equalsIgnoreCase(((MDevice)o).getStrMac())){
				return true;
			}
		}
		
		return super.equals(o);
	}
}
