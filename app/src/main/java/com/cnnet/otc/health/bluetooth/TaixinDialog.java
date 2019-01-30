package com.cnnet.otc.health.bluetooth;

import java.util.ArrayList;
import java.util.HashMap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.bean.MyBlueToothDevice;
import com.cnnet.otc.health.bean.OTCDeviceFilter;
import com.cnnet.otc.health.ble.BleScanner;
import com.cnnet.otc.health.ble.LeScanCallbackExt;
import com.cnnet.otc.health.ble_middle.BleController;
//import com.cnnet.otc.health.managers.WavePlotManager;
import com.cnnet.otc.health.util.StringUtil;

public class TaixinDialog extends Dialog implements
		View.OnClickListener {
	
	private final String TAG = "TaixinDialog";
	private Button seachButton, stopButton;

	private Context mContext;

	public ProgressDialog progressDialog;


	//private WavePlotManager mBleManager;
	private ListView mListView;
	private DeviceListAdapter mAdapter;
	private ArrayList<MyBlueToothDevice>list;
	private String Sharepare;
	//private OTCDeviceFilter dev_filter=new OTCDeviceFilter("Medical");
	public TaixinDialog(Context context, int theme,String sharepare) {
		super(context, theme);
		setContentView(R.layout.devices);
		mContext = context;
		Sharepare = sharepare;
		init();
	}

	private void init() {
		
		list = new ArrayList<MyBlueToothDevice>();

		mAdapter = new DeviceListAdapter(mContext, list);
		progressDialog = new ProgressDialog(getContext());
		progressDialog.setMessage(getContext().getString(R.string.CONNECTING));
		progressDialog.setCancelable(true);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(mAdapter);
		mListView.setFastScrollEnabled(true);
		mListView.setOnItemClickListener(mDeviceClickListener);
		seachButton = (Button) findViewById(R.id.start_seach);
		stopButton = (Button) findViewById(R.id.stop_seach);
		switchButton(false);
		seachButton.setOnClickListener(this);
		stopButton.setOnClickListener(this);
		setTitle(Sharepare);
		mHandlerslist.post(TimerProcess1);

	}

	void switchButton(boolean isSearching)
	{
		int search_bnt_color=isSearching? 0xFF808080:0xFFFFFFFF;
		int stop_bnt_color=isSearching?   0xFFFFFFFF:0xFF808080;
		seachButton.setEnabled(!isSearching);
		seachButton.setTextColor(search_bnt_color);
		stopButton.setEnabled(isSearching);
		stopButton.setTextColor(stop_bnt_color);
	}

	@Override
	public void onClick(View v) {
		BleScanner scanner=BleScanner.getInstance(getContext());
		switch (v.getId()) {
			case R.id.start_seach:
				list.clear();
				mAdapter.notifyDataSetChanged();
				switchButton(true);
				//mBleManager.startScan();
				scanner.setmCallerScanCallback(mScanCallbackExt);
                scanner.scan(15000);
				break;
			case R.id.stop_seach:
			    scanner.stop_scan();
				switchButton(false);
				break;

		}

	}

	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

			final MyBlueToothDevice item = list.get(arg2);
			if (StringUtil.isEmpty(item.getAddress())) {
				return;
			}

			AlertDialog.Builder StopDialog = new AlertDialog.Builder(mContext);// 定义一个弹出框对象
			StopDialog.setTitle(getContext().getString(R.string.CONNECT));// 标题
			StopDialog.setMessage(item.getName() + "\n" + item.getAddress());

			StopDialog.setPositiveButton(
					getContext().getString(R.string.CONNECT),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							BleController ble_controller=BleController.getInstance();
                            ble_controller.addBleDeviceToList(item);
							TaixinDialog.this.dismiss();
						}
					});

			StopDialog.setNegativeButton(getContext()
					.getString(R.string.CANCEL), null);
			StopDialog.show();
		}
	};

	Handler mHandlerslist = new Handler();
	private Runnable TimerProcess1 = new Runnable() {
		public void run() {
			//
			mAdapter.notifyDataSetChanged();
			mListView.setSelection(list.size() - 1);
			mHandlerslist.postDelayed(this, 1000);
		}
	};


	// 获取蓝牙设备列表
	@SuppressLint("NewApi")
	LeScanCallbackExt mScanCallbackExt=new LeScanCallbackExt()
	{
		@Override
		public void onLeScan(BluetoothDevice device, int i, byte[] bytes) {
			String address = device.getAddress();
            for(MyBlueToothDevice item:list)
			{
				if(item.getAddress().equals(address))
				{
					item.setName(device.getName());
					item.setDevice_name(device.getName());
					return;
				}
			}
			MyBlueToothDevice new_item = new MyBlueToothDevice(device.getName(),  device.getName());
			new_item.setName(device.getName());
			new_item.setAddress(device.getAddress());
			if(DeviceMapperManager.getInstance().filter(new_item)!=null) {
				list.add(new_item);
			}
		}

		@Override
		public void on_scan_finish() {
			switchButton(false);
		}
	};
}