package com.cnnet.otc.health.bluetooth;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.bean.MyBlueToothDevice;
import com.cnnet.otc.health.bean.OTCDeviceFilter;
import com.cnnet.otc.health.managers.BtNormalManager;
import com.cnnet.otc.health.services.BluetoothService;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.util.ToastUtil;
import com.cnnet.otc.health.views.MyLineChartView;
import com.cnnet.otc.health.bean.data.MyData;

public class DeviceDialog extends Dialog {
	
	private final String TAG = "DeviceDialog";
	
	public enum ServerOrCilent {
		NONE, SERVICE, CILENT
	};

	private ListView mListView;
	private ArrayList<MyBlueToothDevice> list;
	private Button seachButton;
	private DeviceListAdapter mAdapter;
	private Context mContext;
	//private OTCDeviceFilter dev_filter=null;

	private MyLineChartView myLineView;
	private MyData mydata;

	private ConnectBtThread connectBtThread;

	private MyBlueToothDevice btDevice = null;
	public static ServerOrCilent serviceOrCilent = ServerOrCilent.NONE;

	/* 取得默认的蓝牙适配器 */
	private BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
	public static ProgressDialog progressDialog;

	private boolean isDiscovery = false;

	public DeviceDialog(Context context, int theme, MyLineChartView myLineView,
						MyData mydata) {
		super(context, theme);
		setContentView(R.layout.devices);
		this.myLineView = myLineView;
		this.mydata = mydata;
		mContext = context;
		//this.dev_filter=new OTCDeviceFilter(mydata);
		init();

		// 使蓝牙可用
		if (mBtAdapter != null && !mBtAdapter.isEnabled()) {
			// mBtAdapter.enable();
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			((Activity) context).startActivityForResult(enableIntent, 3);
		}
		mBtAdapter.cancelDiscovery();
	}

	private void init() {
		list = new ArrayList<MyBlueToothDevice>();
		mAdapter = new DeviceListAdapter(getContext(), list);
		progressDialog = new ProgressDialog(getContext());
		progressDialog.setMessage(getContext().getString(R.string.CONNECTING));
		progressDialog.setCancelable(true);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setFastScrollEnabled(true);
		mListView.setOnItemClickListener(mDeviceClickListener);
		IntentFilter discoveryFilter = new IntentFilter(
				BluetoothDevice.ACTION_FOUND);
		getContext().registerReceiver(mReceiver, discoveryFilter);
		IntentFilter foundFilter = new IntentFilter(
				BluetoothDevice.ACTION_FOUND);
		getContext().registerReceiver(mReceiver, foundFilter);
		Log.d(TAG, "mBtAdapter=" + mBtAdapter);
		if (mBtAdapter == null) {
			ToastUtil.TextToast(getContext().getApplicationContext(),
					getContext().getString(R.string.NO_USABLE_DEVICE),
					Toast.LENGTH_SHORT);// 无可用设备
		} else {
			Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
			if (pairedDevices.size() > 0) {
				boolean bFound=false;
				for (BluetoothDevice device : pairedDevices) {
					MyBlueToothDevice dev=DeviceMapperManager.getInstance().filter(new MyBlueToothDevice(device.getName(), device.getAddress()));
					if(dev!=null) {
						bFound=true;
						list.add(dev);
						mAdapter.notifyDataSetChanged();
						mListView.setSelection(list.size() - 1);
					}
				}
				if(!bFound)
				{
					list.add(new MyBlueToothDevice(getContext().getString(R.string.NULL_DEVICE), ""));
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(list.size() - 1);
				}
			} else {
				list.add(new MyBlueToothDevice(getContext().getString(R.string.NULL_DEVICE), ""));
				mAdapter.notifyDataSetChanged();
				mListView.setSelection(list.size() - 1);
			}
		}
		seachButton = (Button) findViewById(R.id.start_seach);
		seachButton.setOnClickListener(seachButtonClickListener);
	}

	private View.OnClickListener seachButtonClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if (mBtAdapter == null) {
				ToastUtil.TextToast(getContext().getApplicationContext(),
						getContext().getString(R.string.NO_USABLE_DEVICE),
						Toast.LENGTH_SHORT);
				return;
			}
			if (isDiscovery) {
				mBtAdapter.cancelDiscovery();
				isDiscovery = false;
				seachButton.setText(getContext()
						.getString(R.string.REDISCOVERY));
			} else {
				isDiscovery = true;
				// FudakangActivity.connectStatus = 0;
				// System.out.println("connectStatus="+FudakangActivity.connectStatus);
				list.clear();
				mAdapter.notifyDataSetChanged();
				Set<BluetoothDevice> pairedDevices = mBtAdapter
						.getBondedDevices();
				if (pairedDevices.size() > 0) {
					boolean bFound=false;
					for (BluetoothDevice device : pairedDevices) {
						MyBlueToothDevice dev=DeviceMapperManager.getInstance().filter(new MyBlueToothDevice(device.getName(), device.getAddress()));
						if(dev!=null) {
							bFound=true;
							list.add(dev);
							mAdapter.notifyDataSetChanged();
							mListView.setSelection(list.size() - 1);
						}
					}
				} /*else {
					list.add(new MyBlueToothDevice(getContext().getString(R.string.NULL_DEVICE), ""));
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(list.size() - 1);
				}*/
				/* 开始搜索 */
				// if (FudakangActivity.connectStatus == 2) {
				// FudakangActivity.connectStatus = 0;
				// System.out.println("connectStatus="+FudakangActivity.connectStatus);
				// }
				mBtAdapter.startDiscovery();
				seachButton.setText(getContext().getString(
						R.string.STOP_DISCOVERY));
			}
		}
	};

	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			// Cancel discovery because it's costly and we're about to connect

			MyBlueToothDevice item = list.get(arg2);
			if (StringUtil.isEmpty(item.getAddress())) {
				return;
			}
			btDevice = item;

			AlertDialog.Builder StopDialog = new AlertDialog.Builder(mContext);// 定义一个弹出框对象
			StopDialog.setTitle(getContext().getString(R.string.CONNECT));// 标题
			StopDialog.setMessage(item.getName() + "\n" + item.getAddress());
			StopDialog.setPositiveButton(
					getContext().getString(R.string.CONNECT),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							BtNormalManager.touchCount = 0;
							mBtAdapter.cancelDiscovery();
							seachButton.setText(getContext().getString(
									R.string.REDISCOVERY));
							BluetoothService.disConnect();
							if (connectBtThread != null) {
								connectBtThread.cancel();
							}
							connectBtThread = new ConnectBtThread();
							connectBtThread.start();
						}
					});

			StopDialog.setNegativeButton(getContext()
					.getString(R.string.CANCEL),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							btDevice = null;
						}
					});
			StopDialog.show();
		}
	};

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				MyBlueToothDevice dev=DeviceMapperManager.getInstance().filter(new MyBlueToothDevice(device.getName(), device.getAddress()));
				try {
					if (dev != null) {
						boolean bFound = false;
						for (MyBlueToothDevice myDev : list) {
							if (myDev.getAddress().equals(dev.getAddress())) {
								bFound = true;
								myDev.setDevice_name(dev.getDevice_name() + "(在线)");
								break;
							}
						}
						if (!bFound) {
							dev.setDevice_name(dev.getDevice_name() + "(在线)");
							list.add(dev);
						}
						mAdapter.notifyDataSetChanged();
						mListView.setSelection(list.size() - 1);
					}
				}
				catch(Exception e)
				{
					System.out.println(e.getLocalizedMessage()+"\n");
					e.printStackTrace();
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				((Activity) getContext())
						.setProgressBarIndeterminateVisibility(false);
				if (mListView.getCount() == 0) {
					list.add(new MyBlueToothDevice(getContext().getString(R.string.NULL_DEVICE), ""));
					mAdapter.notifyDataSetChanged();
					mListView.setSelection(list.size() - 1);
				}
				seachButton.setText(getContext()
						.getString(R.string.REDISCOVERY));
			}
		}
	};

	public Handler hander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				ToastUtil.TextToast(getContext().getApplicationContext(),
						getContext().getString(R.string.CONNECTED),
						Toast.LENGTH_SHORT);
				break;
			case 1:
				ToastUtil.TextToast(getContext().getApplicationContext(),
						getContext().getString(R.string.CONNECT_ERROR),
						Toast.LENGTH_SHORT);
				break;
			case 2:
				progressDialog.show();
				break;
			default:
				break;
			}
		}
	};

	public class ConnectBtThread extends Thread {
		private boolean isRun = true;
		@Override
		public void run() {

			Log.d(TAG, "ConnectBtThread");
			
			int count = 0;
			if (mBtAdapter == null) {
				return;
			}
			while (isRun && !mBtAdapter.isEnabled()) {
				count++;
				Log.d(TAG, "count=" + count);
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (count >= 100) {
					System.out.println("蓝牙无法打开");
					super.run();
					return;
				}
			}
			if(isRun) {
				hander.sendEmptyMessage(2);
			}else {
				return;
			}
			Log.d(TAG, "蓝牙已打开。。。");
			while (isRun && BtNormalManager.connectStatus == 0) {
				Log.d(TAG, "count");
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(!isRun) {
				return;
			}
			serviceOrCilent = ServerOrCilent.CILENT;

			Looper.prepare();
			BluetoothService.disConnect();
			new BluetoothService(mContext, DeviceDialog.this, btDevice, mydata);

			super.run();
		}
		public void cancel() {
			isRun = false;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (BtNormalManager.connectStatus != 1) {
				BtNormalManager.connectStatus = 2;
				Log.d(TAG, "connectStatus="
						+ BtNormalManager.connectStatus);
			} else {
				BtNormalManager.connectStatus = 1;
				Log.d(TAG, "connectStatus=" + BtNormalManager.connectStatus);
			}
			onDestroy();
			dismiss();
		}
		return super.onKeyUp(keyCode, event);
	}

	public void clear() {
		try {
			getContext().unregisterReceiver(mReceiver);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onDestroy() {
		clear();
		if (mBtAdapter != null) {
			mBtAdapter.cancelDiscovery();
		}
	}

}
