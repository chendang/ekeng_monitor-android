package com.cnnet.otc.health.events;


import com.cnnet.otc.health.comm.CommConst;

public class BleEvent {
	private int resultCode = CommConst.FLAG_CONNECT_EVENT_RESET;
	private int stateColor;
	private String stateStr = null;

	private byte[] datas;
	private float value;
	
	public BleEvent(int resultCode){
		this.resultCode = resultCode;
	}
	
	public BleEvent(int resultCode, String stateStr){
		this.resultCode = resultCode;
		this.stateStr = stateStr;
	}

	public BleEvent(int resultCode, byte[] datas){
		this.resultCode = resultCode;
		this.datas = datas;
	}

	public BleEvent(int resultCode, float value){
		this.resultCode = resultCode;
		this.value = value;
	}

	public BleEvent(int resultCode, String stateStr, int color){
		this.resultCode = resultCode;
		this.stateStr = stateStr;
		this.stateColor = color;
	}
	
	public int getBleEvent(){
		return resultCode;
	}
	
	public String getBlueStateStr() {
		return stateStr;
	}

	public byte[] getDatas() {
		return this.datas;
	}
	
	public int getStateColor() {
		return stateColor;
	}
}
