package com.cnnet.otc.health.bluetooth;

public class TaixinTextandImage {
	
	private int taixin;
	private long DifferentTime;
	private String time;
	
	public TaixinTextandImage(int taixin,long differenttime,String time) {
		this.taixin = taixin;
		this.DifferentTime = differenttime;
		this.time = time;
	}

	public long getDifferentTime() {
		return DifferentTime;
	}

	public void setDifferentTime(int differentTime) {
		DifferentTime = differentTime;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getTaixin() {
		return taixin;
	}

	public void setTaixin(int taixin) {
		this.taixin = taixin;
	}
	

}
