package com.foxchen.ekengmonitor.health.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.foxchen.ekengmonitor.R;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cnnet.otc.health.managers.RequestManager;
import com.cnnet.otc.health.util.SHA1Util;
import com.cnnet.otc.health.util.ToastUtil;
import com.lifesense.ble.LsBleManager;
import com.lifesense.ble.OnDeviceUpgradeListener;
import com.lifesense.ble.OnSettingListener;
import com.lifesense.ble.ReceiveDataCallback;
import com.lifesense.ble.SearchCallback;
import com.lifesense.ble.bean.BloodPressureData;
import com.lifesense.ble.bean.LsDeviceInfo;
import com.lifesense.ble.bean.PedometerAlarmClock;
import com.lifesense.ble.bean.PedometerData;
import com.lifesense.ble.bean.PedometerHeartRateData;
import com.lifesense.ble.bean.PedometerHeartRateStatisticsData;
import com.lifesense.ble.bean.PedometerInfo;
import com.lifesense.ble.bean.PedometerOxygen;
import com.lifesense.ble.bean.PedometerRunningCalorieData;
import com.lifesense.ble.bean.PedometerRunningStatus;
import com.lifesense.ble.bean.PedometerSedentaryInfo;
import com.lifesense.ble.bean.PedometerSleepData;
import com.lifesense.ble.bean.SwimmingData;
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
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.dcloud.feature.internal.sdk.SDK;

/**
 * 1、乐心服务器获取mac信息
 * 2、提交设备绑定关系
 * 3、扫描当前设备
 * 4、获取当前设备检测数据信息
 * 5、提交检查数据
 */
public class LSBlueAddSyncActivity extends Activity {
    private TextView textView;
    private RotateLoading rotateLoading;
    private String appid= "x8e0vrtofpopbnj23tsypep16wlpdn0a43qaqtgm";
    private String appsecrect= "csfxmp991g2zeklogosboeyg976proaavx10bxlq";
    private String timestamp ;
    private String nonce ="";
    private String sn;
    private String mac;
    private String checksum ;
    private Context mContext;
    private LsBleManager lsBleManager;
    private LsDeviceInfo deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_lsblue_add_sync);
        mContext=this;


        init();
        initBletoothManager();
        initOptionalBletoothManager();

        sn=getIntent().getStringExtra("SN");


            if(!lsBleManager.isSupportLowEnergy())
            {
                //判断当前设备的手机是否支持蓝牙4.0
                ToastUtil.TextToast(getBaseContext(), R.string.device_not_surport_ble4, 5000);
                this.finish();
                return;

            }
            if(!lsBleManager.isOpenBluetooth())
            {
                //判断当前手机的蓝牙功能是否处于打开状态
                ToastUtil.TextToast(getBaseContext(), R.string.device_not_open_ble, 2000);
                Intent intent =  new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
            }else{
                if(getIntent().getStringExtra("MAC")==null)
                    getDeviceInfo();
                else
                    startScan(mac);
            }
//        this.finish();
        SDK.obtainCurrentApp();
        SDK.obtainCurrentRunnbingAppId();
        WebView webView =SDK.obatinFirstPage(SDK.obtainCurrentApp()).obtainWebview();
        webView.loadUrl("file:///android_asset/apps/qbsAPP/www/Login.html");
    }
    private Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            //搜索到设备
            if (msg.what == 000000) {
                textView.setText("已连接上设备,准备同步数据,请稍等．．．");
            }
            //成功连接到设备
            if (msg.what == 111111) {
                textView.setText("正在同步数据,请稍等．．．");
            }
            //成功获取到设备
            if(msg.what==666666){
                //激活定时器,隔两秒发现数据没有变化即认为已经读取完毕

            }
        }
    };
    private void init(){
        rotateLoading = (RotateLoading) findViewById(R.id.rotateloading);
        rotateLoading.start();
        textView= (TextView) findViewById(R.id.lsblueaddsyncres);
        textView.setText("连接设备同步数据开始，请稍等．．．");
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
    private  void getDeviceInfo(){

        timestamp = String.valueOf(new Date().getTime());
        Random random = new Random();
        for(int i=0;i<8;i++){
            nonce += String.valueOf(random.nextInt(10)) ;
        }

        Log.d("debug","Rand = "+nonce);

//        sn = "0301570101062540";
        sn=getIntent().getStringExtra("SN");
        checksum=appid+appsecrect+timestamp+nonce;
        try {
            Map parmMap= new HashMap<String,String>();
            parmMap.put("value",sn);
            parmMap.put("keytype","sn");
            RequestManager.getLSDeviceInfo(mContext,new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            Log.d("getLSDeviceInfo:", String.valueOf(jsonObject));
                            try {
                                JSONObject dataJson= jsonObject.getJSONObject("data");
                                 mac = dataJson.getString("mac");
                                textView.setText("远程核对信息无误．．．");
                                Log.d("getmacinfo:", transStrToMac(mac));
                                startScan(transStrToMac(mac));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.e("getLSDeviceInfo:", String.valueOf(volleyError));
                            //未取到设备信息--直接跳回h5处理
                            textView.setText("设备信息没有取到，请核对输入设备信息是否有误．．．");
                        }
                    },parmMap,appid,timestamp,nonce.toString(), SHA1Util.getSHA(checksum));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
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
    private void startScan(final String mac) {
        // 广播类型，一般选全部广播
        BroadcastType mBroadcastType = BroadcastType.ALL;
        textView.setText("正在搜索该设备．．．");
        lsBleManager.searchLsDevice(new SearchCallback() {
            @Override
            public void onSearchResults(LsDeviceInfo lsDevice) {
                // 扫描到的设备
                if (lsDevice.getMacAddress().equalsIgnoreCase(
                        mac)) {
                    //D9:72:83:71:33:3F dc:0e:ae:71:b3:a0
                        System.err.println("lsDevice:" + lsDevice);
                    //连接上上设备
                    handler.sendEmptyMessage(000000);//发送消息

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
                switch (packetType) {
                    case PEDOMETER_DEVIE_INFO:
                        //计步器当前用户信息;
                        PedometerInfo pedometerInfo = (PedometerInfo) dataObject;

                        break;
                    case PEDOMETER_DATA_C9:
                    case PEDOMETER_DATA_CA:
                    case PEDOMETER_DATA_8B:
                    case PEDOMETER_DATA_82:
                    case DAILY_MEASUREMENT_DATA:
                    case PER_HOUR_MEASUREMENT_DATA:
                        //计步器每日、每小时测量数据
                        List<PedometerData> pedometerDatas
                                = (List<PedometerData>) dataObject;
                        postDataToSrv("PER_HOUR_MEASUREMENT_DATA",toMap(pedometerDatas));
                        break;
                    case HEART_RATE_DATA:
                        //心率数据
                        PedometerHeartRateData heartRateData
                                = (PedometerHeartRateData) dataObject;
                        postDataToSrv("HEART_RATE_DATA",toMap(heartRateData));
                        break;
                    case PEDOMETER_DATA_83:
                    case PEDOMETER_DATA_CE:
                    case SLEEP_DATA:
                        //计步器睡眠压缩数据
                        PedometerSleepData sleepInfo = (PedometerSleepData) dataObject;
                        postDataToSrv("SLEEP_DATA",toMap(sleepInfo));
                        break;
                    case SWIMMING_LAPS:
                        //计步器游泳圈数
                        SwimmingData swimmingData = (SwimmingData) dataObject;
                        postDataToSrv("SWIMMING_LAPS",toMap(swimmingData));
                        break;
                    case BLOOD_OXYGEN_DATA:
                        //手环血氧数据
                        PedometerOxygen bloodOxygen = (PedometerOxygen) dataObject;
                        postDataToSrv("BLOOD_OXYGEN_DATA",toMap(bloodOxygen));
                        break;
                    case RUNNING_HEART_RATE_DATA:
                        //跑步心率数据
                        PedometerHeartRateData runningHeartRateData
                                = (PedometerHeartRateData) dataObject;
                        postDataToSrv("RUNNING_HEART_RATE_DATA",toMap(runningHeartRateData));
                        break;
                    case RUNNING_STATUS_DATA:
                        //跑步状态数据
                        PedometerRunningStatus runningStatus
                                = (PedometerRunningStatus) dataObject;
                        postDataToSrv("RUNNING_STATUS_DATA",toMap(runningStatus));
                        break;
                    case RUNNING_CALORIE_DATA:
                        //跑步卡路里
                        PedometerRunningCalorieData runingCalorie
                                = (PedometerRunningCalorieData) dataObject;
                        postDataToSrv("RUNNING_CALORIE_DATA",toMap(runingCalorie));
                        break;
                    case HEART_RATE_STATISTICS:
                        //心率数据
                        PedometerHeartRateStatisticsData heartRateStatistics
                                = (PedometerHeartRateStatisticsData) dataObject;
                        postDataToSrv("HEART_RATE_STATISTICS",toMap(heartRateStatistics));
                        break;
                    default:
                        //其它数据
                        break;
                }
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
        //stopOtaForPedometer();
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

    private String transStrToMac(String mac){
        String resMac="";
        for (int i = 0; i < mac.length(); i = i + 2) {
            resMac+=mac.substring(i,i+2);
            if(i+2 <mac.length())
                resMac+=":";
        }
        mac= resMac;
        return resMac;
    }
    private  void postDataToSrv(String recordType, Map data){
        data.put("RecordType",recordType);
        RequestManager.postLSRecordInfo(mContext,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                       Log.e("提交乐心运动记录信息成功!","");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("提交乐心运动记录信息失败","");
                    }
                },data);

    }

    /**
     * 将javaBean转换成Map
     *
     * @param javaBean javaBean
     * @return Map对象
     */
    public static Map<String, String> toMap(Object javaBean)
    {
        Map result = new HashMap ();
        if(javaBean instanceof java.util.List){
            List list=new ArrayList();
            for (int i = 0; i <((List)javaBean).size() ; i++) {
                Object oneItem =((List)javaBean).get(i);
                list.add(itemToMap(oneItem));
            }

            result.put("List",list);
        }else
            result=itemToMap(javaBean);
        return result;
    }
    public static Map<String, String> itemToMap(Object javaBean)
    {
        Map<String, String> result = new HashMap<String, String>();
        Method[] methods = javaBean.getClass().getDeclaredMethods();

        for (Method method : methods)
        {
            try
            {
                if (method.getName().startsWith("get"))
                {
                    String field = method.getName();
                    field = field.substring(field.indexOf("get") + 3);
                    field = field.toLowerCase().charAt(0) + field.substring(1);

                    Object value = method.invoke(javaBean, (Object[])null);
                    result.put(field, null == value ? "" : value.toString());
                }
            }
            catch (Exception e)
            {
            }
        }

        return result;
    }
}
