package com.cnnet.otc.health.bluetooth;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class TaiXinDate	implements  com.example.taixin.MyData  {

	public List<Integer>  list = new ArrayList<Integer>();
	public byte Tdatas;
	public int State_Faile ;
	
	@Override
	public void todo(byte[] data) {
		// TODO �Զ����ɵķ������
		
		if(data.length==8){
			Tdatas = data[4];
			list.add( data[4]&0xff);
			Log.d("̥������������======����", "" + data[4] + "_" + data[5] + "_" + data[6]);
		}
		
	}

	@Override
	public void todoConnected() {
		// TODO �Զ����ɵķ������
		Log.d("onConnectionStateChange_mConnectionState","todoConnected");
	}

	@Override
	public void todoConnecting() {
		// TODO �Զ����ɵķ������
		Log.d("onConnectionStateChange_mConnectionState","todoConnecting");
	}

	@Override
	public void todoDisconnected() {
		// TODO �Զ����ɵķ������
		Log.d("onConnectionStateChange_mConnectionState","todoDisconnected");
	}

	@Override
	public void todoDisconnected_failed() {
		// TODO �Զ����ɵķ������
		
		State_Faile =3;
		Log.d("onConnectionStateChange_mConnectionState","todoDisconnected_failed");
	}
	public int getdisconnected_failed(){
		return State_Faile;		
	}
	
	public int getdata(){
		
		int i = Tdatas&0xff; 
		return i;
	}
	
	public List<Integer> getAlldata(){
		return list;
		
	}

}
