package com.cnnet.otc.health.managers;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.bean.MyBlueToothDevice;
import com.cnnet.otc.health.bean.data.BloodGlucoseData;
import com.cnnet.otc.health.bean.data.BloodPressureData;
import com.cnnet.otc.health.bean.data.LipidData;
import com.cnnet.otc.health.bean.data.TemperatureData;
import com.cnnet.otc.health.bean.data.UricacidData;
import com.cnnet.otc.health.bean.data.WeightData;
import com.cnnet.otc.health.bluetooth.DeviceDialog;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.events.BTConnetEvent;
import com.cnnet.otc.health.interfaces.MyCommData;
import com.cnnet.otc.health.services.BluetoothService;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.views.MyLineChartView;

import de.greenrobot.event.EventBus;

public class BtNormalManager {
	private static final String TAG = "BtNormalManager";
	private Activity mActivity = null;
	/** -1表示未连接，0表示正在连接，1表示成功，2表示失败 */
	public static int connectStatus = -1;
	/** 1表示已连接，0表示已断开 */
	public static int btConnectStatus = -1;
	/** 连接次数 */
	public static int touchCount;
	/** 是否自动连接 */
	private final boolean hasBackgroundConnect = true;

	private ConnectBackground connectBackground;
	private CuickConnect cuickConnect;
	/** 定时器延时启动时间 */
	private static int delay = 100; // 1s
	/** 定时器循环时间 */
	private static int period = 100; // 1s

	private MyLineChartView myLineView;
	private MyCommData myData;
	private long nativeRecordId;
	private String mUniqueKey = null;

	private Timer mTimer = null;
	private TimerTask mTimerTask = null;


	/**
	 * 后台连接handler工作
	 */
	private Handler myBackBTHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 1) {
				if (hasBackgroundConnect) {
					connectBackground = new ConnectBackground();
					connectBackground.start();
					Log.d(TAG, "connectBackground.start()");
				}
			}
		}
	};
	
	/**
	 * 蓝牙状态监听
	 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Log.d(TAG, "action ---------- " + action);
			if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
				System.out.println("ACTION_ACL_DISCONNECTED蓝牙断开连接。。。");
				connectStatus = 2;
				btConnectStatus = 0;
				touchCount = 8;
				sendHandlerStartThread();
				Log.d(TAG, "connectStatus=" + connectStatus);
			} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				Log.d(TAG, "ACTION_BOND_STATE_CHANGED蓝牙状态改变。。。");
			}
		}
	};
	
	/**
	 * 初始化
	 * @param mActivity
	 */
	public BtNormalManager(Activity mActivity, MyLineChartView myLineChartView, long navtiveRecordId,String mUniqueKey ) {
		this.mActivity = mActivity;
		this.myLineView = myLineChartView;
		this.mUniqueKey=mUniqueKey;
		touchCount = 8;
		this.nativeRecordId = navtiveRecordId;
		myData = getData();
		init();
		startTimer();
	}
	/**
	 * 初始化
	 */
	private void init() {
		/*if (connectBackground == null && hasBackgroundConnect) {
			connectBackground = new ConnectBackground();
			connectBackground.start();
			Log.d(TAG, "connectBackground.start()");
		}*/
		sendHandlerStartThread();
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		mActivity.registerReceiver(mReceiver, filter);
		filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		mActivity.registerReceiver(mReceiver, filter);
	}

	/**
	 * 获取解析数据Data类
	 * @return
	 */
	public MyCommData getData() {
		if(myData == null) {
			switch (SysApp.check_type) {
				case BLOOD_GLUCOSE:   //血糖仪
					myData = new BloodGlucoseData(mActivity, myLineView, nativeRecordId,mUniqueKey);
					break;
				case BLOOD_PRESSURE:   //血压计
					myData = new BloodPressureData(mActivity, nativeRecordId,mUniqueKey);
					break;
				case THERMOMETER:       //体温计
					myData = new TemperatureData(mActivity, myLineView, nativeRecordId,mUniqueKey);
					break;
				case URIC_ACID:  //尿酸
					myData = new UricacidData(mActivity, myLineView, nativeRecordId,mUniqueKey);
					break;
				case LIPID:  //血脂
					myData = new LipidData(mActivity, myLineView, nativeRecordId,mUniqueKey);
					break;
				case WEIGHT:  //体重
					myData = new WeightData(mActivity, myLineView, nativeRecordId,mUniqueKey);
					break;
			}

		}
		return myData;
	}

	public void destory() {
		if(cuickConnect != null) {
			cuickConnect.cancel();
		}
		if(myBackBTHandler != null) {
			myBackBTHandler.removeMessages(1);
			myBackBTHandler = null;
		}
		mActivity.unregisterReceiver(mReceiver);
		BluetoothService.disConnect();
		stopTimer();
		BluetoothAdapter mBtAdapter = BluetoothAdapter
				.getDefaultAdapter();
		mBtAdapter = null;
	}
	
	/**
	 * 开始定时器
	 */
	public void startTimer() {
		Log.d(TAG, "开始定时器...");
		// if (!isStop) {
		// return;
		// }
		// isStop = false;
		try {
			if (mTimer == null) {
				mTimer = new Timer();
			}

			if (mTimerTask == null) {
				mTimerTask = new TimerTask() {
					@Override
					public void run() {
					    EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE_SCAN, null));
					}
				};
			}

			if (mTimer != null && mTimerTask != null)
				mTimer.schedule(mTimerTask, delay, period);
		} catch (Exception e) {
			Log.d(TAG, "定时器已打开");
		}
	}
	
	/**
	 * 取消定时器
	 */
	public void stopTimer() {
		Log.d(TAG, "取消定时器");
		// isStop = true;
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}

		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
		Log.d(TAG, "BtNormalManager: onResume");
		Log.d(TAG, "connectStatus=" + connectStatus);

		EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE_STATE, null));
	}


	/**
	 * 后台连接蓝牙线程
	 */
	private class ConnectBackground extends Thread {
		//private boolean checked = true;
		@Override
		public void run() {
			super.run();

			//while (checked) {
				if (connectStatus == 1) {
					//continue;
					return;
				}
				if (touchCount == 8) {
						touchCount = 0;
						if(cuickConnect != null) {
							cuickConnect.cancel();
						}
						Log.d(TAG, "开始连接1111");
						while (connectStatus == 0) {
							try {
								Thread.sleep(100);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						cuickConnect = new CuickConnect();
						cuickConnect.start();
				} else if (connectStatus == 2) {
					touchCount++;
					sendHandlerStartThread();
				}
				// System.out.println(touchCount);
				/*try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}*/
			//}
		}
		/*public void cancel() {
			checked = false;
		}*/
	}

	/**
	 * 启动线程
	 */
	private void sendHandlerStartThread() {
		if(myBackBTHandler != null) {
			myBackBTHandler.removeMessages(1);
			myBackBTHandler.sendEmptyMessageDelayed(1, 1000);
		}
	}
	
	/**
	 * 快速连接蓝牙线程
	 * @author SZ512
	 *
	 */
	private class CuickConnect extends Thread {
		private boolean isRun = true;
		@Override
		public void run() {
			super.run();
			Looper.prepare();
			connectStatus = 0;
			Log.d(TAG, "connectStatus = 0");
			// DisplayView.text = DisplayView.fa.getString(R.string.CONNECTING);
			// DisplayView.fa.hander.sendEmptyMessage(5);
			EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE,
					mActivity.getString(R.string.CONNECTING)));
			try {
				List<MyBlueToothDevice> devices = SysApp.getMyDBManager().getConnectedBtList(SysApp.check_type.ordinal());
				Log.d(TAG, "读取数据库完毕");
				int size = 0;
				if (devices == null || (size = devices.size()) == 0) {
					connectStatus = 2;
					Log.d(TAG, "progressDialog.dismiss()111;  connectStatus = 2");
				} else {
					connectStatus = 0;
					Log.d(TAG, "connectStatus=" + connectStatus + ";  (connectStatus = 0)");
					/*DisplayView.text = mActivity.getString(R.string.CONNECTING);
					DisplayView.fa.hander.sendEmptyMessage(5);*/
					EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE,
							mActivity.getString(R.string.CONNECTING)));
					Log.d(TAG, "正在关闭连接");
					BluetoothService.disConnect();
					Log.d(TAG, "开始背景连接");

					BluetoothAdapter mBtAdapter = BluetoothAdapter
							.getDefaultAdapter();
					if (mBtAdapter != null) {

						// mBtAdapter.enable();
						Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						mActivity.startActivityForResult(enableIntent, 3);
						// 打开蓝牙
						Log.d(TAG, "打开蓝牙");
						int count = 0;
						while (!mBtAdapter.isEnabled() && isRun) {
							count++;
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if (count >= 100) {
								Log.d(TAG, "蓝牙无法打开");
								connectStatus = 2;
								Log.d(TAG, "connectStatus = 2");
								return;
							}
						}
						Log.d(TAG, "蓝牙已打开");

						int connectCount = 0;
						int index = 0;
						while (isRun && connectStatus != 1 && (index < size)) {
							DeviceDialog.serviceOrCilent = DeviceDialog.ServerOrCilent.CILENT;
							connectStatus = 0;
							Log.d(TAG, "connectStatus = 0");
							// DisplayView.text = DisplayView.fa
							// .getString(R.string.CONNECTING);
							// DisplayView.fa.hander.sendEmptyMessage(5);
							EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE,
									mActivity.getString(R.string.CONNECTING)));
							MyBlueToothDevice myBlueToothDevice = devices.get(index);
							index++;
							if (myBlueToothDevice != null && StringUtil.isNotEmpty(myBlueToothDevice.getAddress())) {
								Log.d(TAG, "connect thr mac: " + myBlueToothDevice.getAddress());
								connectCount++;
								if (connectCount > 3) {
									break;
								}
								new BluetoothService(mActivity, myBlueToothDevice, myData);
								// wait for connect
								while (connectStatus == 0) {
									try {
										Thread.sleep(100);
										// System.out.println("wait");
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							} else {
								// connectStatus = 2;
								// System.out.println("connectStatus = 2");
								// System.out.println("2222");
							}
							if (connectStatus != 1) {
								connectStatus = 2;
								Log.d(TAG, "connectStatus = 2");
							}
						}
					}
				}
			} catch (Exception e) {
				Log.d(TAG, "error=" + e.toString());
			}
			sendHandlerStartThread();
			Looper.loop();
		}

		public void cancel() {
			this.isRun = false;
		}
	}
	
}
