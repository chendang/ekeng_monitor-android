package com.cnnet.otc.health.bean;

public class DeviceListItem {

	private String name;
	private String address;
	public String message;
	boolean isSiri;

	public DeviceListItem(String msg, boolean siri) {
		message = msg;
		isSiri = siri;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSiri() {
		return isSiri;
	}

	public void setSiri(boolean isSiri) {
		this.isSiri = isSiri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
