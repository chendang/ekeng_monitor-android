package com.cnnet.otc.health.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.bean.MyBlueToothDevice;
import com.cnnet.otc.health.bean.data.WeightData;
import com.cnnet.otc.health.bluetooth.DeviceDialog;
import com.cnnet.otc.health.comm.CheckType;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.events.BTConnetEvent;
import com.cnnet.otc.health.managers.BtNormalManager;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.util.ToastUtil;
import com.cnnet.otc.health.bean.data.MyData;

import de.greenrobot.event.EventBus;

public class BluetoothService {
	
	private static final String TAG = "BluetoothService";
	/**
	 * 蓝牙socket连接对象
	 */
	public static BluetoothSocket socket;
	/**
	 * 当前连接蓝牙设备对象
	 */
	private BluetoothDevice device;
	/**
	 * 蓝牙信息输入对象
	 */
	private static InputStream mmInStream;
	/**
	 * 蓝牙信息输出对象
	 */
	private static OutputStream outStream;
	/**
	 * 获取扫描到的蓝牙adapter
	 */
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	private DeviceDialog deviceDialog;

	/**
	 * 当前蓝牙连接地址
	 */
	private MyBlueToothDevice currentBT = null;
	/**
	 * 前一个连接的蓝牙地址
	 */
	private MyBlueToothDevice oldBT = null;

	private static MyData myData = null;
	private boolean isQuick;
	private static boolean finishClose;
	private static Context context;
	public static final int SEND_TEST_CMD = 0;
	// public static boolean needSend = false;

	// 相关线程
	private ReadThread mReadThread;
	private static boolean readFlag = true;
	// private SendThread sendThread;
	private ConnectThread mConnectThread;

	private int mState;

	// 联接状态
	public static final int STATE_NONE = 0;
	public static final int STATE_LISTEN = 1;
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;

	/**
	 * 1、初始化服务，不自动连接
	 * @param context
	 * @param d
	 * @param btDevice
	 * @param data
	 */
	public BluetoothService(Context context, DeviceDialog d, MyBlueToothDevice btDevice, MyData data) {
		this.currentBT = btDevice;
		this.deviceDialog = d;
		this.myData = data;
		finishClose = true;
		this.context = context;
		isQuick = false;
		initBluetoothService(context);
	}

	/**
	 * 2、初始化服务，自动连接address相同的蓝牙
	 * @param context
	 * @param btDevice
	 * @param data
	 */
	public BluetoothService(Context context, MyBlueToothDevice btDevice, MyData data) {
		this.context = context;
		this.myData = data;
		finishClose = true;
		isQuick = true;
		this.currentBT = btDevice;
		initBluetoothService(context);
	}
	
	/**
	 * 3、初始化连接服务，并判断是否开启连接
	 * @param context
	 */
	private void initBluetoothService(Context context) {
		BtNormalManager.connectStatus = 0;
		Log.d(TAG, "connectStatus=" + BtNormalManager.connectStatus);
		Log.d(TAG, "连接状态：--------------=" + context.getString(R.string.CONNECTING));
		ToastUtil.TextToast(context, R.string.CONNECTING, Toast.LENGTH_LONG);
		EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE, context.getString(R.string.CONNECTING)));
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (currentBT != null && StringUtil.isNotEmpty(currentBT.getAddress())) {
			device = mBluetoothAdapter.getRemoteDevice(currentBT.getAddress());
			new ConnectThread().start();
		}
	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or
	 * fails.
	 */
	private class ConnectThread extends Thread {

		public ConnectThread() {
			BluetoothSocket tmp = null;
			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				tmp = device.createInsecureRfcommSocketToServiceRecord (
						UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));

			} catch (IOException e) {
				Log.d(TAG, "create() failed");
			}
			socket = tmp;
		}

		@Override
		public void run() {
			setName("ConnectThread");
			// Always cancel discovery because it will slow down a connection
			mBluetoothAdapter.cancelDiscovery();

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				socket.connect();
				if (!isQuick) {
					DeviceDialog.progressDialog.dismiss();
					deviceDialog.dismiss();
				}

				connected(socket, device);
			} catch (IOException e) {
				// Close the socket
				Log.d(TAG, "error: " + e.getMessage());

				try {
					if(socket != null) {
						socket.close();
					}
				} catch (IOException e2) {
					Log.d(TAG, "unable to close() socket during connection failure");
				}
				// 连接失败
				connectionFailed();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothService.this) {
				mConnectThread = null;
			}

			// Start the connected thread

		}

		public void cancel() {
			try {
				socket.close();
			} catch (IOException e) {
				Log.d(TAG, "close() of connect socket failed");
			}
		}
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection
	 * 
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 * @throws IOException
	 */
	public synchronized void connected(BluetoothSocket socket,
			BluetoothDevice device) throws IOException {
		// if (D) Log.d(TAG, "connected, Socket Type:" + socketType);

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mReadThread != null) {
			mReadThread.cancel();
			mReadThread = null;
		}
		setState(STATE_CONNECTED);

		// Start the thread to manage the connection and perform transmissions
		System.out.println("成功，已经连上...............");
		//stopTimer();
		EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECTED_STOP_TIMER, null));
		oldBT = getMyBlueToothDeivce(device);
		SysApp.btDevice = oldBT;
		SysApp.getMyDBManager().saveBTDeviceInfo(oldBT, SysApp.check_type.ordinal());
		BtNormalManager.btConnectStatus = 1;
		BtNormalManager.connectStatus = 1;
		Log.d(TAG, "connectStatus=" + BtNormalManager.connectStatus);
		//DisplayView.fa.hander.sendEmptyMessage(6);
		EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE_STATE, null));
		// 插入蓝牙MAC地址
		if (SysApp.check_type == CheckType.BLOOD_GLUCOSE) {
			//DisplayView.fa.hander.sendEmptyMessage(2);
			Log.d(TAG, "插入血糖仪成功");
		} else if (SysApp.check_type == CheckType.BLOOD_PRESSURE) {
			//DisplayView.fa.hander.sendEmptyMessage(3);
			Log.d(TAG, "插入血压计成功");
		} else if (SysApp.check_type == CheckType.THERMOMETER) {
			//DisplayView.fa.hander.sendEmptyMessage(9);
			Log.d(TAG, "插入体温计成功");
		} else if(SysApp.check_type == CheckType.URIC_ACID) {
			Log.d(TAG, "接入尿酸仪成功");
		}
		// 启动接受数据
		if (SysApp.check_type == CheckType.BLOOD_PRESSURE || SysApp.check_type == CheckType.URIC_ACID
				|| SysApp.check_type ==CheckType.WEIGHT || SysApp.check_type == CheckType.LIPID) {
			Log.d(TAG, context.getString(R.string.TESTING));
			//DisplayView.text = context.getString(R.string.TESTING);
			//DisplayView.fa.hander.sendEmptyMessage(5);
			EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE, context.getString(R.string.TESTING)));
		}
		mReadThread = new ReadThread();
		mReadThread.start();
		// sendThread = new SendThread();
		// sendThread.init();
		outStream = socket.getOutputStream();
		sendInfoHandler.removeCallbacks(sendMessage);
		sendInfoHandler.post(sendMessage);

	}

	/**
	 * 初始化蓝牙bean
	 * @param device
	 * @return
	 */
	private MyBlueToothDevice getMyBlueToothDeivce(BluetoothDevice device) {
		MyBlueToothDevice bean = new MyBlueToothDevice();
		bean.setName(device.getName());
		bean.setAddress(device.getAddress());
		return bean;
	}

	public synchronized void connectionFailed() {
		if (!isQuick) {
			// System.out.println(e.getCause());
			// e.printStackTrace();
			DeviceDialog.progressDialog.dismiss();
			deviceDialog.dismiss();
		}
		System.out.println("失败");
		// DisplayView.fa.startTimer();

		if (BtNormalManager.btConnectStatus == 1) {
			if (currentBT != null && oldBT != null && oldBT.getAddress().equals(currentBT.getAddress())) {
				BtNormalManager.connectStatus = 1;
				Log.d(TAG, "connectStatus=" + BtNormalManager.connectStatus);
				Log.d(TAG, "重复连接相同设备失败");
			} else {
				BtNormalManager.connectStatus = 2;
				Log.d(TAG, "connectStatus=" + BtNormalManager.connectStatus);
				Log.d(TAG, "重复连接不同设备失败");
				Log.d(TAG, "正在取消连接");
				BluetoothService.disConnect();
				Log.d(TAG, "已经取消连接");
			}

		} else {
			BtNormalManager.connectStatus = 2;
			Log.d(TAG, "connectStatus=" + BtNormalManager.connectStatus);
		}

		if (BtNormalManager.connectStatus == 2) {
			String title = "";
			if (SysApp.check_type == CheckType.BLOOD_GLUCOSE) {
				title = context.getString(R.string.CONNECT_GLUCOSE);
			} else if (SysApp.check_type == CheckType.BLOOD_PRESSURE) {
				title = context.getString(R.string.CONNECT_PRESSURE);
			} else if (SysApp.check_type == CheckType.THERMOMETER) {
				title = context.getString(R.string.CONNECT_THERMOMETER);
			} else if(SysApp.check_type == CheckType.URIC_ACID ) {
				title = context.getString(R.string.CONNECT_URIC_ACID);
			} else if(SysApp.check_type ==CheckType.WEIGHT) {
				title = context.getString(R.string.CONNECT_WEIGHT);
			} else if(SysApp.check_type == CheckType.LIPID) {
				title = context.getString(R.string.CONNECT_LIPID);
			}
			Log.d(TAG, "连接失败设备：  " + title);
			//DisplayView.fa.hander.sendEmptyMessage(5);
			EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE, title));
		}
	}

	/**
	 * 丢失连接方法
	 */
	private void connectionLost() {
		BtNormalManager.connectStatus = 2;
		BtNormalManager.btConnectStatus = 0;
		String title = "";
		if (SysApp.check_type == CheckType.BLOOD_GLUCOSE) {
			title = context.getString(R.string.CONNECT_GLUCOSE);
		} else if (SysApp.check_type == CheckType.BLOOD_PRESSURE) {
			title = context.getString(R.string.CONNECT_PRESSURE);
		} else if (SysApp.check_type == CheckType.THERMOMETER) {
			title = context.getString(R.string.CONNECT_THERMOMETER);
		} else if(SysApp.check_type == CheckType.URIC_ACID ) {
			title = context.getString(R.string.CONNECT_URIC_ACID);
		} else if(SysApp.check_type ==CheckType.WEIGHT) {
			title = context.getString(R.string.CONNECT_WEIGHT);
		} else if(SysApp.check_type == CheckType.LIPID) {
			title = context.getString(R.string.CONNECT_LIPID);
		}
		Log.d(TAG, "丢失连接失败：  " + title);
		EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE, title));
		disConnect();
	}

	/**
	 * Stop all threads
	 */
	public synchronized void stop() {

		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		if (mReadThread != null) {
			mReadThread.cancel();
			mReadThread = null;
		}

		setState(STATE_NONE);
	}

	/**
	 * Set the current state of the chat connection
	 * 
	 * @param state
	 *            An integer defining the current connection state
	 */
	private synchronized void setState(int state) {
		System.out.println("setState() " + mState + " -> " + state);
		mState = state;
	}

	private static Handler sendInfoHandler = new Handler();
	private static Runnable sendMessage = new Runnable() {
		@Override
		public void run() {
			boolean flag = false;
			int delayTime = 0;
			switch (SysApp.check_type) {
				case URIC_ACID:
					flag = sendUricDetectCmd();
					delayTime = 1000;
					break;
				case WEIGHT:
					flag = sendWeightCmd();
					delayTime = 1000;
					break;
				case BLOOD_PRESSURE:
					sendRandomCmd();
					delayTime = 1000;
					break;
				default:
					flag = false;
					delayTime = 0;
					break;
			}
			if(flag && delayTime > 0) {
				sendInfoHandler.postDelayed(sendMessage, delayTime);
			}
		}
	};

	private static Handler closeDeviceHandler = new Handler();
	private static Runnable closeMessage = new Runnable() {
		@Override
		public void run() {
			switch (SysApp.check_type) {
				case WEIGHT:
					sendCloseWeightCmd();
					break;
			}
			closeDeviceHandler.postDelayed(closeMessage, 1000);

		}
	};

	/**
	 * 读取数据线程
	 * @author SZ512
	 *
	 */
	private class ReadThread extends Thread {
		private byte[] read = new byte[8];
		private int count = 0;

		public ReadThread() {
			readFlag = true;
		}

		@Override
		public void run() {

			int byteLength;

			try {
				mmInStream = socket.getInputStream();

			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.out.println("开始读数据");
			try {
				sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			Log.d(TAG, "x循环开始-----------------");

			byte[] buffer = null;
			while (readFlag) {
				// Log.d(TAG, "..................");
				try {
					if(SysApp.check_type == CheckType.URIC_ACID || SysApp.check_type ==CheckType.WEIGHT
							|| SysApp.check_type == CheckType.LIPID) {  //
						byte[] readBuf = new byte[50];
						if ((byteLength = mmInStream.read(readBuf)) > 0) {
							buffer = new byte[byteLength];
							for(int i = 0; i < byteLength; i++) {
								buffer[i] = readBuf[i];
							}
//							String hexString = StringUtil.byteToHexString(buffer);
//							Log.d(TAG, "hexString : " + hexString);
						}
					} else {
						buffer = new byte[8];
						if ((byteLength = mmInStream.read(buffer)) > 0) {
							Log.d(TAG, "......bytes = " + byteLength + ".........count = " + count);
							for (int i = 0; i < byteLength; i++) {
								if (count == 0) {
									if (buffer[i] == -2) {
										read[count] = buffer[i];
										count++;
									} else if (buffer[i] == -16) {
										read[count] = buffer[i];
										count++;
									}
								} else {
									if (count < 8) {
										read[count] = buffer[i];
									}
									count++;
								}

								if (read[0] == -2 && count == 8) {
									count = 0;
									String hexString = StringUtil.byteToHexString(read);
									//buffer = read;
									System.arraycopy(read,0,buffer,0,read.length);
									//DisplayView.req = hexString;
									Log.d(TAG, "msg : " + hexString);
								}
							}
						}
					}
					myData.todo(buffer);
				} catch (IOException e) {
					Log.d(TAG, "断开连接");
					connectionLost();
					break;
				}

				Log.d(TAG, "-----------------");
			}
		}

		public void cancel() {
			readFlag = false;
			try {
				socket.close();
			} catch (IOException e) {
				Log.d(TAG, "close() of connect socket failed");
			}
		}
	}

	private static boolean sendWeightCmd() {
		//FE 03 01 00 AA 19 01 B0
		//FE 01 01 00 AA 15 01 C2
		if(myData != null) {
			WeightData weightData = (WeightData)myData;
			/*byte[] send = new byte[8];
			send[0] = (byte) 0xFE;
			send[1] = (byte) 0x03;
			send[2] = (byte) 0x01;
			send[3] = (byte) 0x00;
			send[4] = (byte) 0xAA;
			send[5] = (byte) 0x19;
			send[6] = (byte) 0x01;
			send[7] = (byte) 0xB0;*/
			try {
				byte[] send = weightData.getSendWeightBytes();
				if(send != null) {
					outStream.write(send);
					Log.d(TAG, "sendWeightCmd ..........." + StringUtil.byteToHexString(send));
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private static boolean sendCloseWeightCmd() {
		//FD 35 00 00 00 00 00 35
		byte[] send = new byte[8];
		send[0] = (byte) 0xFD;
		send[1] = (byte) 0x35;
		send[2] = (byte) 0x00;
		send[3] = (byte) 0x00;
		send[4] = (byte) 0x00;
		send[5] = (byte) 0x00;
		send[6] = (byte) 0x00;
		send[7] = (byte) 0x35;
		try {
			outStream.write(send);
			Log.d(TAG, "sendCloseWeightCmd ..........." + StringUtil.byteToHexString(send));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			if(closeDeviceHandler != null) {
				closeDeviceHandler.removeCallbacks(closeMessage);
			}
		}
		return false;
	}

	private static boolean sendUricDetectCmd() {
		//0x24 0x50 0x43 0x4C 0x01 0x00 0x00 0x00 0x00 0x00 CS(CS为自定义值)
		// send 0x24 0x50 0x43 0x4C 0x01 0x00 0x00 0x00 0x00 0x00 0xE0
		//      24 50 43 4C 01 00 00 00 00 00 E0
		byte[] send = new byte[11];
		send[0] = (byte) 0x24;
		send[1] = (byte) 0x50;
		send[2] = (byte) 0x43;
		send[3] = (byte) 0x4C;
		send[4] = (byte) 0x01;
		send[5] = (byte) 0x00;
		send[6] = (byte) 0x00;
		send[7] = (byte) 0x00;
		send[8] = (byte) 0x00;
		send[9] = (byte) 0x00;
		send[10] = (byte) 0xE0;
		try {
			outStream.write(send);
			Log.d(TAG, "sendUricDetectCmd ..........." + StringUtil.byteToHexString(send));
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 发送温度计的连接命令
	 */
	private static void sendTempConnectCmd() {
		byte[] send = new byte[8];
		//0xFE 6A 72 5A 55 AA BB CS
		send[0] = (byte) 0xFE;
		send[1] = (byte) 0x6A;
		send[2] = (byte) 0x72;
		send[3] = (byte) 0x5A;
		send[4] = (byte) 0x55;
		send[5] = (byte) 0xAA;
		send[6] = (byte) 0xBB;
		send[7] = (byte) 0xF0;
		try {
			outStream.write(send);
			Log.d(TAG, "..........." + send);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 随机发送数据，以保持设备连接
	 */
	public static void sendRandomCmd() {
		byte[] send = new byte[2];
		send[0] = (byte) 0xFE;
		send[1] = (byte) 0x6A;
		try {
			outStream.write(send);
			Log.d(TAG, "..........." + StringUtil.byteToHexString(send));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendTestCmd() {
		byte[] send = new byte[8];
		// 0xFE 6A 72 5B 01 00 00 CS
		send[0] = (byte) 0xFE;
		send[1] = (byte) 0x6A;
		send[2] = (byte) 0x72;
		send[3] = (byte) 0x5B;
		send[4] = (byte) 0x1;
		send[5] = (byte) 0x0;
		send[6] = (byte) 0x0;
		send[7] = (byte) 0x38;
		try {
			outStream.write(send);
			Log.d(TAG, "..........." + send);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void closeBTDevice() {
		switch (SysApp.check_type) {
			/*case URIC_ACID:
				break;*/
			case WEIGHT:
//				sendCloseWeightCmd();
				closeDeviceHandler.post(closeMessage);
				break;
			default:
				break;
		}
	}

	/**
	 * 关闭连接
	 */
	public static void disConnect() {
		finishClose = false;
		if(sendInfoHandler != null) {
			sendInfoHandler.removeCallbacks(sendMessage);
		}
		if(closeDeviceHandler != null) {
			closeDeviceHandler.removeCallbacks(closeMessage);
		}
		myData = null;
		readFlag = false;
		Log.d(TAG, "mmInStream().close()");
		if (mmInStream != null) {
			try {
				mmInStream.close();
				mmInStream = null;
			} catch (IOException e) {
			}
		}

		Log.d(TAG, "socket().close()");
		if (socket != null) {
			try {
				socket.close();
				socket = null;
			} catch (IOException e) {
			}
		}

		finishClose = true;
		String info = "";
		Log.d(TAG, "重置信息");
		if(context != null) {
			if (SysApp.check_type == CheckType.BLOOD_GLUCOSE) {
				info = context.getString(R.string.CONNECT_GLUCOSE);
			} else if (SysApp.check_type == CheckType.BLOOD_PRESSURE) {
				info = context.getString(R.string.CONNECT_PRESSURE);
			}
		}
		//DisplayView.fa.hander.sendEmptyMessage(5);
		EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_UPDATE, info));
		//DisplayView.fa.startTimer();
		EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_DISCONNECT_START_TIMER, null));
		System.gc();
	}

}
