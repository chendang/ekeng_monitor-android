package com.cnnet.otc.health.events;


import com.cnnet.otc.health.comm.CommConst;

public class LoginEvent {
	private int resultCode = CommConst.FLAG_CONNECT_EVENT_RESET;


	public LoginEvent(int resultCode){
		this.resultCode = resultCode;
	}
	public int getLoginEvent(){
		return resultCode;
	}

}
