package com.example.lsbluetoothdemo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.lifesense.ble.LsBleManager;
import com.lifesense.ble.OnDeviceUpgradeListener;
import com.lifesense.ble.OnSettingListener;
import com.lifesense.ble.ReceiveDataCallback;
import com.lifesense.ble.SearchCallback;
import com.lifesense.ble.bean.BloodPressureData;
import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ble.bean.PedometerAlarmClock;
import com.lifesense.ble.bean.PedometerSedentaryInfo;
import com.lifesense.ble.bean.WeightData_A2;
import com.lifesense.ble.bean.WeightData_A3;
import com.lifesense.ble.bean.constant.BroadcastType;
import com.lifesense.ble.bean.constant.DeviceConnectState;
import com.lifesense.ble.bean.constant.DeviceType;
import com.lifesense.ble.bean.constant.DeviceUpgradeStatus;
import com.lifesense.ble.bean.constant.GattServiceType;
import com.lifesense.ble.bean.constant.PacketProfile;
import com.lifesense.ble.bean.constant.VibrationMode;
import com.lifesense.ble.bean.constant.WeekDay;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

public class MainActivity extends Activity {

	private LsBleManager lsBleManager;
	private LsDeviceInfo deviceInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initBletoothManager();
		initOptionalBletoothManager();

		if(!lsBleManager.isSupportLowEnergy())
		{
			//判断当前设备的手机是否支持蓝牙4.0
		}
		if(!lsBleManager.isOpenBluetooth())
		{
			//判断当前手机的蓝牙功能是否处于打开状态
		}else{
//			startScan();
		}
	}

	// 初始化
	private void initBletoothManager() {
		// 创建一个管理器对象实例,单例
		lsBleManager = LsBleManager.getInstance();
		// 对象实例初始化
		lsBleManager.initialize(getApplicationContext());
	}

	// 可选初始化
	private void initOptionalBletoothManager() {
		// 是否开启蓝牙调试日志
		lsBleManager.enableWriteDebugMessageToFiles(true,
				LsBleManager.PERMISSION_WRITE_LOG_FILE);
		// 设置蓝牙连接log信息写入的文件的路径
		lsBleManager.setBlelogFilePath(Environment
				.getExternalStorageDirectory().getPath()
				+ File.separator
				+ "LsBluetoothDemo\report", "+*", "2.0");
	}

	// 要扫描的设备类型
	private List<DeviceType> getDeviceTypes() {
		List<DeviceType> mScanDeviceType = null;

		mScanDeviceType = new ArrayList<DeviceType>();
		// 血压计
		mScanDeviceType.add(DeviceType.SPHYGMOMANOMETER);
		// 脂肪秤
		mScanDeviceType.add(DeviceType.FAT_SCALE);
		// 体重秤
		mScanDeviceType.add(DeviceType.WEIGHT_SCALE);
		// 体重秤
		mScanDeviceType.add(DeviceType.HEIGHT_RULER);
		// 计步器
		mScanDeviceType.add(DeviceType.PEDOMETER);
		// 厨房秤
		mScanDeviceType.add(DeviceType.KITCHEN_SCALE);

		return mScanDeviceType;
	}

	// 开始扫描
	private void startScan() {
		// 广播类型，一般选全部广播
		BroadcastType mBroadcastType = BroadcastType.ALL;
		lsBleManager.searchLsDevice(new SearchCallback() {
			@Override
			public void onSearchResults(LsDeviceInfo lsDevice) {
				// 扫描到的设备
				if (lsDevice.getMacAddress().equalsIgnoreCase(
						"dc:0e:ae:71:b3:a0")) {
					System.err.println("lsDevice:" + lsDevice);
					// 停止扫描
					stopScan();
					// 获取要连接设备
					deviceInfo = lsDevice;
					// 读取手环数据
					addReceiveDevice();
					startReceiveDate();
					// 手环ota
					// startOtaForPedometer();
				}
			}
		}, getDeviceTypes()/* 设备类型* */, mBroadcastType/* 广播类型* */);
	}

	// 停止扫描
	private void stopScan() {
		lsBleManager.stopSearch();
	}

	// 添加在接收数据的设备
	private void addReceiveDevice() {
		lsBleManager.addMeasureDevice(deviceInfo);
	}

	// 接收数据
	private void startReceiveDate() {
		lsBleManager.startDataReceiveService(new ReceiveDataCallback() {
			// 设备的连接状态
			@Override
			public void onDeviceConnectStateChange(
					final DeviceConnectState connectState, String broadcastId) {

				if (connectState == DeviceConnectState.CONNECTED_SUCCESS) {
					// 连接成功
//					setEncourage();
				} else if (connectState == DeviceConnectState.CONNECTED_FAILED
						|| connectState == DeviceConnectState.DISCONNECTED) {
					// 连接失败
				}

			}

			// 血压计测量数据
			@Override
			public void onReceiveBloodPressureData(
					final BloodPressureData bpData) {
			}

			// 体重秤数据
			@Override
			public void onReceiveWeightData_A3(WeightData_A3 arg0) {
				// TODO Auto-generated method stub
				super.onReceiveWeightData_A3(arg0);
			}
			@Override
			public void onReceiveWeightDta_A2(WeightData_A2 arg0) {
				// TODO Auto-generated method stub
				super.onReceiveWeightDta_A2(arg0);
			}

			// 接收手环返回的测量数据
			@Override
			public void onReceivePedometerMeasureData(final Object dataObject,
					final PacketProfile packetType, final String sourceData) {
				System.err.println("dataObject:" + dataObject);
			}
		});
	}

	// 停止接收设备数据
	private void stopReceiver() {
		lsBleManager.stopDataReceiveService();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopOtaForPedometer();
		stopReceiver();
	}

	// 升级手环固件
	private void startOtaForPedometer() {
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath() + File.separator + "ls405_B2_A05404_16042901.hex");
		if (!(file.exists() && file.isFile())) {
			System.err
					.println("文件不存在，把项目的ls405_B2_A05404_16042901.hex文件复制到手机的根目录下。");
		} else {
			lsBleManager.upgradeDeviceFirmware(deviceInfo.getMacAddress(),
					file, new OnDeviceUpgradeListener() {

						@Override
						public void onDeviceUpgradeProcess(int value) {
							// value为升级的进度，取值范围0-100
							System.err.println("value:" + value);
						}

						@Override
						public void onDeviceUpdradeStateChange(String address,
								DeviceUpgradeStatus upgradeStatus, int errorCode) {
							System.err
									.println("upgradeStatus:" + upgradeStatus);
							if (upgradeStatus == DeviceUpgradeStatus.UPGRADE_SUCCESS) {
								// 升级失败
							} else if (upgradeStatus == DeviceUpgradeStatus.UPGRADE_FAILURE) {
								// 升级成功
							} else if (upgradeStatus == DeviceUpgradeStatus.UPGRADING
									|| upgradeStatus == DeviceUpgradeStatus.ENTER_UPGRADE_MODE) {
								// 开始升级
							}
						}
					});
		}
	}

	// 取消手环升级
	private void stopOtaForPedometer() {
		lsBleManager.interruptUpgradeProcess(deviceInfo.getMacAddress());
	}

	// 设置手环的闹钟
	private void setAlarm() {
		List<PedometerAlarmClock> alarmList = new ArrayList<PedometerAlarmClock>();
		PedometerAlarmClock alarmClock = new PedometerAlarmClock();
		alarmClock.setDeviceId(deviceInfo.getDeviceId());
		// 闹钟开启
		alarmClock.setEnableSwitch(true);
		// 时间
		alarmClock.setTime("14:08");
		// 震动持续时间,振动持续时间,表示提醒持续总时长，最大值60s，设置15s
		alarmClock.setVibrationDuration(15);
		// 震动等级1,0~9
		alarmClock.setVibrationIntensity1(2);
		// 震动等级2,0~9
		alarmClock.setVibrationIntensity2(2);
		// 闹钟重复时间可选
		List<WeekDay> weekDays = new ArrayList<WeekDay>();
		weekDays.add(WeekDay.WEDNESDAY);
		alarmClock.setRepeatDay(weekDays);
		// 震动模式,持续震动
		alarmClock.setVibrationMode(VibrationMode.CONTINUOUS_VIBRATION);
		alarmList.add(alarmClock);
		lsBleManager.updatePedometerAlarmClock(deviceInfo.getMacAddress(),
				true/* 全部闹钟的开关 */, alarmList/*闹钟对象列表*/, new OnSettingListener() {

					@Override
					public void onSuccess(String macAddress) {
						//闹钟设置成功
						System.err.println("成功");
					}

					@Override
					public void onFailure(int errorCode) {
						//闹钟设置失败
					}
				});
	}
	
	//设置久坐
	private void setSedentary(){
		PedometerSedentaryInfo sedentaryInfo=new PedometerSedentaryInfo();
		//开启坐久
		sedentaryInfo.setEnableSedentaryReminder(true);
		//多久不动就提醒(单位：min)，最小6分钟
		sedentaryInfo.setEnableSedentaryTime(6);
		//久坐开始时间
		sedentaryInfo.setReminderStartTime("8:00");
		//结束时间
		sedentaryInfo.setReminderEndTime("20:00");
		//重复时间
		List<WeekDay> weekDays = new ArrayList<WeekDay>();
		weekDays.add(WeekDay.WEDNESDAY);
		sedentaryInfo.setRepeatDay(weekDays);
		//振动持续时间（单位：seconds）
		sedentaryInfo.setVibrationDuration(10);
		// 震动等级1,0~9
		sedentaryInfo.setVibrationIntensity1(5);
		// 震动等级2,0~9
		sedentaryInfo.setVibrationIntensity2(10);
		//震动模式，强度大小循环
		sedentaryInfo.setVibrationMode(VibrationMode.INTERMITTENT_VIBRATION4);

		List<PedometerSedentaryInfo> seList=new ArrayList<PedometerSedentaryInfo>();
		seList.add(sedentaryInfo);
		
		lsBleManager.updatePedometerSedentary(deviceInfo.getMacAddress(), true, seList, new OnSettingListener() {
			
			@Override
			public void onSuccess(String macAddress) {
				//设置成功
				System.err.println("成功");
			}

			@Override
			public void onFailure(int errorCode) {
				//设置失败
			}
		});

	}
	
	//设置鼓励
	private void setEncourage(){
		int step = 1000;//最少1000步，不然手环没效果
		lsBleManager.updatePedometerStepGoal(deviceInfo.getMacAddress(), true, step, new OnSettingListener() {
			
			@Override
			public void onSuccess(String macAddress) {
				//设置成功
				System.err.println("成功");
			}

			@Override
			public void onFailure(int errorCode) {
				//设置失败
			}
		});
	}
	
	//开启来电提醒
	private void enableCall(){
		lsBleManager.setEnableGattServiceType(deviceInfo.getMacAddress(), GattServiceType.ALL);
	}
	
	//关闭来电提醒
	private void disableCall(){
		lsBleManager.setEnableGattServiceType(deviceInfo.getMacAddress(), GattServiceType.USER_DEFINED);
	}
}
