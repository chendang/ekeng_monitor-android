package com.cnnet.otc.health.events;


import com.cnnet.otc.health.comm.CommConst;

public class BTConnetEvent {
	private int resultCode = CommConst.FLAG_CONNECT_EVENT_RESET;

	private String message = null;

	public BTConnetEvent(int resultCode, String message){
		this.resultCode = resultCode;
		this.message = message;
	}
	public int getBTConnetEvent(){
		return resultCode;
	}

	public String getMessage() {
		return this.message;
	}
}
