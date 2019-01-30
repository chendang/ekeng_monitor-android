package com.cnnet.otc.health.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.services.InstallSelfService;
import com.cnnet.otc.health.util.NetUtil;


/**
 * 
 * @author Administrator
 * 
 */
public class UpdateManager {

	private final static String TAG = "UpdateManager";

	/**
	 * 
	 * @author Administrator
	 * 
	 */
	public interface UpdateCallback {

		public void checkUpdateCompleted(Boolean hasUpdate,
										 CharSequence updateInfo, String changelog);

		public void downloadCanceled();

		public void downloadCompleted(Boolean sucess, CharSequence errorMsg);

		public void downloadProgressChanged(int progress);

		public void netError();

	}

	private static final int UPDATE_CHECKCOMPLETED = 1;
	private static final int UPDATE_DOWNLOAD_CANCELED = 5;
	private static final int UPDATE_DOWNLOAD_COMPLETED = 4;
	private static final int UPDATE_DOWNLOAD_ERROR = 3;
	private static final int UPDATE_DOWNLOADING = 2;
	private static final int UPDATE_NET_ERROR = 8;
	private UpdateCallback callback;
	private boolean canceled = false;
	// private String curVersion;
	private int curVersionCode;

	private boolean hasNewVersion = false;
	private Context mContext;
	private String newVersion;
	private int newVersionCode;
	private int progress;
	
	private String changelog ="";

	private String strApkDownUrl;

	@SuppressLint("HandlerLeak")
	Handler updateHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case UPDATE_CHECKCOMPLETED:
				callback.checkUpdateCompleted(hasNewVersion, newVersion,changelog);
				break;
			case UPDATE_DOWNLOADING:
				callback.downloadProgressChanged(progress);
				break;
			case UPDATE_DOWNLOAD_ERROR:
				callback.downloadCompleted(false, msg.obj.toString());
				break;
			case UPDATE_DOWNLOAD_COMPLETED:
				callback.downloadCompleted(true, "");
				break;
			case UPDATE_DOWNLOAD_CANCELED:
				callback.downloadCanceled();
				break;
			case UPDATE_NET_ERROR:
				callback.netError();
				break;
			default:
				break;
			}
		}
	};

	private String updateInfo;

	public UpdateManager(Context context, UpdateCallback updateCallback) {
		this.mContext = context;
		this.callback = updateCallback;

		getCurVersion();
	}

	public void cancelDownload() {
		canceled = true;
	}

	public void checkUpdate() {

		hasNewVersion = false;

		new Thread() {
			@Override
			public void run() {
				try {
					String verjson = NetUtil
							.httpStringGet(CommConst.UPDATE_CHECK_URL + "&lang=zh");
					Log.e(TAG,"verjson:"+verjson);
					JSONObject jsonObject = new JSONObject(verjson);
					JSONObject rootObject = jsonObject.getJSONObject("offers");
					newVersion = rootObject.getString("current");
					changelog = rootObject.getString("changelog");
					newVersionCode = Integer.parseInt(rootObject
							.getString("current_val"));
					strApkDownUrl = rootObject.getJSONObject("packages")
							.getJSONObject("full").getString("url");
					if (newVersionCode > curVersionCode) {
						hasNewVersion = true;
					}
					Log.d(TAG, "newVersionCode --------- " + newVersionCode);
					newVersionCode = -1;
					updateInfo = newVersion;

				} catch (Exception e) {
					updateHandler.sendEmptyMessage(UPDATE_NET_ERROR);
					return;
				}
				updateHandler.sendEmptyMessage(UPDATE_CHECKCOMPLETED);

			};
			// ***************************************************************
		}.start();
	}

	public void downloadPackage() {
		canceled = false;

		new Thread() {
			@Override
			public void run() {
				try {
					URL url = new URL(CommConst.SERVER_URL + strApkDownUrl);

					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.connect();
					int length = conn.getContentLength();
					InputStream is = conn.getInputStream();

					File ApkFile = new File(SysApp.LOCAL_ROOT_FLODER,
							CommConst.DOWN_APK_FILENAME);

					if (ApkFile.exists()) {
						ApkFile.delete();
					}

					FileOutputStream fos = new FileOutputStream(ApkFile);

					int count = 0;
					byte buf[] = new byte[512];

					do {

						int numread = is.read(buf);
						count += numread;
						progress = (int) (((float) count / length) * 100);

						updateHandler.sendMessage(updateHandler
								.obtainMessage(UPDATE_DOWNLOADING));
						if (numread <= 0) {

							updateHandler
									.sendEmptyMessage(UPDATE_DOWNLOAD_COMPLETED);
							break;
						}
						fos.write(buf, 0, numread);
					} while (!canceled);
					if (canceled) {
						updateHandler
								.sendEmptyMessage(UPDATE_DOWNLOAD_CANCELED);
					}
					fos.close();
					is.close();
				} catch (MalformedURLException e) {
					e.printStackTrace();

					updateHandler.sendMessage(updateHandler.obtainMessage(
							UPDATE_DOWNLOAD_ERROR, e.getMessage()));
				} catch (IOException e) {

					e.printStackTrace();

					updateHandler.sendMessage(updateHandler.obtainMessage(
							UPDATE_DOWNLOAD_ERROR, e.getMessage()));
				}

			}
		}.start();

	}

	private void getCurVersion() {
		try {
			PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0);
			// curVersion = pInfo.versionName;
			curVersionCode = pInfo.versionCode;
			/*String versionName = pInfo.versionName;
			int tenNum =Integer.parseInt(versionName.substring(0, versionName.indexOf(".")));
			int unitNum =Integer.parseInt(versionName.substring( versionName.indexOf(".")+1));
			curVersionCode = curVersionCode*100+tenNum*10+unitNum;*/
			Log.d(TAG, "curVersionCode --------- " + curVersionCode);
			
		} catch (NameNotFoundException e) {
			Log.e("update err:", e.getMessage());
			curVersionCode = 111;
		}
	}

	public String getNewVersionName() {
		return newVersion;
	}

	public String getUpdateInfo() {
		return updateInfo;
	}

	public void update() {

	//	mContext.startService(new Intent("com.cnnet.otc.health.APP_UPDATE_SERVICE"));
		mContext.startService(new Intent(mContext, InstallSelfService.class));
	}

}
