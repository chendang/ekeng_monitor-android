package com.cnnet.otc.health.services;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;


public class InstallSelfService extends Service {


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
	}

	/**
	 * 开启服务
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		Intent intent2 = new Intent(Intent.ACTION_VIEW);
		intent2.setDataAndType(Uri.fromFile(new File(SysApp.LOCAL_ROOT_FLODER,
				CommConst.DOWN_APK_FILENAME)),
				"application/vnd.android.package-archive");
		intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent2);
		
		
	}

}
