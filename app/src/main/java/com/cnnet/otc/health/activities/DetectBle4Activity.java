package com.cnnet.otc.health.activities;

import com.cnnet.otc.health.bean.MyBlueToothDevice;
import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.bean.data.OximetryData;
import com.cnnet.otc.health.ble_middle.BleCfgFactory;
import com.cnnet.otc.health.bluetooth.TaixinDialog;
import com.cnnet.otc.health.comm.BaseActivity;
import com.cnnet.otc.health.comm.CheckType;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.events.BleEvent;
import com.cnnet.otc.health.util.DialogUtil;
import com.cnnet.otc.health.views.MyLineChartView;
import com.cnnet.otc.health.views.adapter.DetectRecordListAdapter;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import java.util.Date;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnnet.otc.health.ble_middle.BleController;

import java.util.List;

import de.greenrobot.event.EventBus;

public class DetectBle4Activity extends BaseActivity implements OnChartValueSelectedListener, OnClickListener{

	private TextView title=null;   //标题
	private MyLineChartView myLineView = null;  //绘图View
	private Button sampleBtn = null;  //采集
    private boolean hasShowAlert=false;
	private ListView listview;
	/**
	 * 4.0蓝牙管理中心
	 */
	/**
	 * 红色数组（r）
	 */
	private int redCount=255;
	/**
	 * 颜色透明度
	 */
	private int alphaFlag = 0;

	private static String[] devices = null;

	private String mUniqueKey = null;
	private long nativeRecordId = 0;
	private boolean isDetected = false;  //判断数据是否改变
	private boolean hasReal = false;  //判断是否存在实时数据
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(!checkBlue()){
			finish();
			return;
		}
		setContentView(R.layout.activity_detect);
		mUniqueKey = getIntent().getStringExtra(CommConst.INTENT_EXTRA_KEY_MEMBER_UNIQUEKEY);
		nativeRecordId = getIntent().getLongExtra(CommConst.INTENT_EXTRA_KEY_NATIVE_RECORD_ID, new Date().getTime());
		hasReal = getIntent().getBooleanExtra(CommConst.INTENT_EXTRA_KEY_HAS_REAL, true);
		initCheckType(Integer.parseInt(getIntent().getStringExtra(CommConst.INTENT_EXTRA_KEY_DEVICE_TYPE)));
		init();
	}
	private boolean checkBlue(){
		int currentapiVersion=android.os.Build.VERSION.SDK_INT;
		if(currentapiVersion<18){
			Toast.makeText( this , "该设备系统版本不支持" , Toast.LENGTH_LONG).show();

			return false;
		}

		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {

			Toast.makeText( this , "设备不支持蓝牙4.0" , Toast.LENGTH_LONG).show();

			return false;
		}
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "本机没有找到蓝牙硬件或驱动！", Toast.LENGTH_LONG).show();

			return false;
		}
		// 如果本地蓝牙没有开启，则开启
		if (!mBluetoothAdapter.isEnabled()) {
			// 我们通过startActivityForResult()方法发起的Intent将会在onActivityResult()回调方法中获取用户的选择，比如用户单击了Yes开启，
			// 那么将会收到RESULT_OK的结果，
			// 如果RESULT_CANCELED则代表用户不愿意开启蓝牙
			Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(mIntent, 1);
			// 用enable()方法来开启，无需询问用户(实惠无声息的开启蓝牙设备),这时就需要用到android.permission.BLUETOOTH_ADMIN权限。
			// mBluetoothAdapter.enable();
			// mBluetoothAdapter.disable();//关闭蓝牙
		}
		return true;
		/*BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(this.BLUETOOTH_SERVICE);
		BluetoothAdapter mBluetoothAdapter = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
			mBluetoothAdapter = bluetoothManager.getAdapter();
		}
		mBluetoothAdapter.enable();*/
	}
	private void init() {
		//注册EventBus
		EventBus.getDefault().register(this);

		if(devices == null) {
			devices = getResources().getStringArray(R.array.devices);
		}
		title = (TextView) findViewById(R.id.tv_detect_title);
		title.setText(devices[SysApp.check_type.ordinal()]);
		LinearLayout drawLinear = (LinearLayout) findViewById(R.id.detect_draw_linear);
		myLineView = new MyLineChartView(this, this);
		myLineView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		if(hasReal) {
			myLineView.refreshRealTimeByMP(null);
		}
		drawLinear.addView(myLineView);

		sampleBtn = (Button)findViewById(R.id.bt_click_sample);
		if(SysApp.check_type == CheckType.OXIMETRY) {
			sampleBtn.setOnClickListener(this);
			sampleBtn.setVisibility(View.VISIBLE);
		}

		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.bt_detect_connect).setOnClickListener(this);
		listview = (ListView) findViewById(R.id.listview);
		BleController ble_controller=BleController.getInstance();
		ble_controller.setmContext(this);
		ble_controller.setCfg(BleCfgFactory.createBleSO2Cfg());
		switch (SysApp.check_type) {
			case OXIMETRY:   //血氧仪
				ble_controller.setData(new OximetryData(this, myLineView,nativeRecordId,mUniqueKey));
				break;
		}
		setData();
	}

	int prev_state= -1;

	Handler refreshHandler=new Handler();
	Runnable refreshRunnable=null;

	void doFlash() {
		BleController ble_controller = BleController.getInstance();
		myLineView.setTitleTextColor();
		int cur_state = ble_controller.getConnStatus();
		if (cur_state != prev_state) {
			String state_title = "";
			switch (cur_state) {
				case BluetoothProfile.STATE_DISCONNECTED:
					state_title = SysApp.check_type.getTitle()+"未连接";
					break;
				case BluetoothProfile.STATE_CONNECTING:
					state_title = "请连接"+ SysApp.check_type.getTitle();
					break;
			}
			myLineView.setTitleText(state_title);
			prev_state = cur_state;
		}
	}

	private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;

	boolean checkLocationPermission()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果 API level 是大于等于 23(Android 6.0) 时
			//判断是否具有权限
			if (ContextCompat.checkSelfPermission(this,
					Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				//判断是否需要向用户解释为什么需要申请该权限
				if (ActivityCompat.shouldShowRequestPermissionRationale(this,
						Manifest.permission.ACCESS_COARSE_LOCATION)) {
					Toast.makeText(getContext(),R.string.explain_ble_requires_gps,Toast.LENGTH_SHORT);
				}
				//请求权限
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
						REQUEST_CODE_ACCESS_COARSE_LOCATION);
				return false;
			}
		}
		return true;
	}

	/**
	 * Location service if enable
	 *
	 * @param context
	 * @return location is enable if return true, otherwise disable.
	 */
	public static final boolean isLocationEnable(Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		boolean networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (networkProvider || gpsProvider) return true;
		return false;
	}

	private static final int REQUEST_CODE_LOCATION_SETTINGS = 2;

	private void setLocationService() {
		Intent locationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		this.startActivityForResult(locationIntent, REQUEST_CODE_LOCATION_SETTINGS);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				checkGPS();
			}
			else
			{
				Toast.makeText(this,R.string.can_not_search_ble,Toast.LENGTH_SHORT);
			}
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	void checkGPS()
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//如果 API level 是大于等于 23(Android 6.0) 时
			showScanDialog();
			return;
		}
		if(!isLocationEnable(this))
		{
			setLocationService();
		}
		else
		{
			showScanDialog();
		}
	}

	void showScanDialog()
	{
		BleController ble_controller=BleController.getInstance();
		ble_controller.pause_controller();
		TaixinDialog deviceDialog = new TaixinDialog(this, R.style.dialog, devices[SysApp.check_type.ordinal()]);
		deviceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				reinit();
			}
		});
		deviceDialog.show();
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_LOCATION_SETTINGS) {
			if (isLocationEnable(this)) {
				showScanDialog();
			}
			else
			{
				Toast.makeText(this,R.string.can_not_search_ble,Toast.LENGTH_SHORT);
			}
		}
		else super.onActivityResult(requestCode, resultCode, data);
	}

	private void setData() {
		BleController ble_controller=BleController.getInstance();
		List<RecordItem>[] list = ble_controller.getData().getRecordList(mUniqueKey);
		myLineView.addData(list, ble_controller.getData().getInsName());

		DetectRecordListAdapter listAdapter = new DetectRecordListAdapter(this,
				ble_controller.getData().getRecordAllList(mUniqueKey)
				, ble_controller.getData());
		listview.setAdapter(listAdapter);
	}

	private void refreshLineByData() {
		isDetected = true;
		setData();
		myLineView.invalidate();
	}

	void reinit() {

		/*plotManager.startRunable();
		if(!ble_controller.isConnected())
		{
			flashTimer.schedule(getFlashTask(),100,100);
		}*/
		BleController ble_controller = BleController.getInstance();
		ble_controller.run_controller(2000);
		if (refreshRunnable == null) {
			refreshRunnable = new Runnable() {
				@Override
				public void run() {
					BleController ble_controller = BleController.getInstance();
					if (!ble_controller.isConnected()) {
						doFlash();
					} else {
						if (hasReal && SysApp.check_type == CheckType.OXIMETRY) {
							OximetryData so2_data = (OximetryData) ble_controller.getData();
							myLineView.refreshRealTimeByMP(so2_data.getdata());
						}
					}
					refreshHandler.postDelayed(refreshRunnable, 100);
				}
			};
		}
		refreshHandler.removeCallbacks(refreshRunnable);
		refreshHandler.postDelayed(refreshRunnable, 100);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		BleController ble_controller = BleController.getInstance();
		if(ble_controller.isBleStoreEmpty()&&!hasShowAlert)
		{
			hasShowAlert=true;
			DialogUtil.Confirm(this, R.string.dialog_ble_empty_title, R.string.dialog_ble_empty,
					R.string.confirm, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(checkLocationPermission())
							{
								checkGPS();
							}
						}
					}, R.string.cancel, null);
		}
		else {
			reinit();
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(refreshRunnable!=null) {
			refreshHandler.removeCallbacks(refreshRunnable);
		}
		BleController ble_controller = BleController.getInstance();
		ble_controller.pause_controller();

	}
	
	public void onEventMainThread(BleEvent event) {
		switch (event.getBleEvent()) {
			case CommConst.FLAG_BLE_CONNECT_SCUESS:
				myLineView.setTitleText(SysApp.check_type.getTitle()+"已连接");
				myLineView.getTitleView().setTextColor(Color.GREEN);
				prev_state=BluetoothProfile.STATE_CONNECTED;
				myLineView.resetBarChart();
				break;
			default:
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finishAndSendBack();
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		BleController ble_controller=BleController.getInstance();
		switch (v.getId()) {
			case R.id.bt_detect_connect:
				if(checkLocationPermission())
				{
					checkGPS();
				}
				else
				{
					Toast.makeText(this,R.string.can_not_search_ble,Toast.LENGTH_SHORT);
				}
				break;
			case R.id.btn_back:
				finishAndSendBack();
				break;
			case R.id.bt_click_sample:
				boolean isSuccess= false;
				if(SysApp.check_type == CheckType.OXIMETRY) {

					isSuccess = ((OximetryData) ble_controller.getData()).saveSampleInfo();
				}
				if(isSuccess) {
					refreshLineByData();// 重新设置ListView的数据适配器
				}
				break;
		default:
			break;
		}
	}

	private void finishAndSendBack() {
		Intent intent = new Intent();
		intent.putExtra(CommConst.INTENT_EXTRA_KEY_IS_DETECTED, isDetected);
		setResult(CommConst.INTENT_REQUEST_DETECT, intent);
		this.finish();
	}

	@Override   //预留
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

	}

	@Override //预留
	public void onNothingSelected() {

	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		BleController ble_controller=BleController.getInstance();
		ble_controller.reset_controller();
		//反注册EventBus
		EventBus.getDefault().unregister(this);
	}
}
