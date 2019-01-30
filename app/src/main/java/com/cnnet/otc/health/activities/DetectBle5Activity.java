package com.cnnet.otc.health.activities;


import android.Manifest;
import android.app.AlertDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.bean.data.BloodPressureData;
import com.cnnet.otc.health.bean.data.LipidData;
import com.cnnet.otc.health.bean.data.LipidTest;
import com.cnnet.otc.health.bean.data.OximetryData;
import com.cnnet.otc.health.bean.data.UricacidData;
import com.cnnet.otc.health.bean.data.WeightData;
import com.cnnet.otc.health.ble_middle.BleCfgFactory;
import com.cnnet.otc.health.ble_middle.BleController;
import com.cnnet.otc.health.bluetooth.DeviceDialog;
import com.cnnet.otc.health.bluetooth.TaixinDialog;
import com.cnnet.otc.health.comm.BaseActivity;
import com.cnnet.otc.health.comm.CheckType;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.db.DBHelper;
import com.cnnet.otc.health.events.BTConnetEvent;
import com.cnnet.otc.health.events.BleEvent;
import com.cnnet.otc.health.interfaces.SubmitServerListener;
import com.cnnet.otc.health.managers.BtNormalManager;
import com.cnnet.otc.health.services.BluetoothService;
import com.cnnet.otc.health.tasks.UploadAllNewInfoTask;
import com.cnnet.otc.health.util.DialogUtil;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.util.ToastUtil;
import com.cnnet.otc.health.views.MyLineChartView;
import com.cnnet.otc.health.views.adapter.DetectRecordListAdapter;
import com.foxchen.ekengmonitor.R;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by SZ512 on 2015/12/31.
 */
public class DetectBle5Activity extends BaseActivity implements OnChartValueSelectedListener, View.OnClickListener {
    private final String TAG = "DetectBlue5Activity";
    private static String[] devices = null;
    private TextView title=null;
    private Button manualInputBtn = null;  //人工输入按钮
    private boolean hasShowAlert=false;

    private MyLineChartView myLineView = null;

    private ListView listview;

    private AlertDialog lipidWDialog;

    private String mUniqueKey = null;
    private long nativeRecordId = 0;
    private boolean isDetected = false;  //判断数据是否改变
    private boolean hasReal = false;  //判断是否存在实时数据
    private boolean isInputHeight = false;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        if(!checkBlue()){
            finish();
            return;
        }
        this.context= this;
        mUniqueKey = getIntent().getStringExtra(CommConst.INTENT_EXTRA_KEY_MEMBER_UNIQUEKEY);
        nativeRecordId = getIntent().getLongExtra(CommConst.INTENT_EXTRA_KEY_NATIVE_RECORD_ID,  new Date().getTime());
        Log.d(TAG, "mUniqueKey = " + mUniqueKey);
        initCheckType( Integer.parseInt(getIntent().getStringExtra(CommConst.INTENT_EXTRA_KEY_DEVICE_TYPE)));
        try {
            initLineView();
            initBlue();
            // add data
            setData();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    private boolean checkBlue(){

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

    }
    private void initBlue() {
        //注册EventBus
        EventBus.getDefault().register(this);
        BleController ble_controller=BleController.getInstance();
        ble_controller.setmContext(this);
        ble_controller.setCfg(BleCfgFactory.createBleCardiacLipidCfg());
        switch (SysApp.check_type) {
            case LIPID:   //血脂仪
                ble_controller.setData(new LipidData(this, myLineView,nativeRecordId,mUniqueKey));
                break;
        }
        setData();
        findViewById(R.id.bt_detect_connect).setOnClickListener(this);
    }

    /**
     * 初始化线性
     */
    private void initLineView( ) {
        if(devices == null) {
            devices = getResources().getStringArray(R.array.devices);
        }
        title = (TextView) findViewById(R.id.tv_detect_title);
        title.setText(devices[SysApp.check_type.ordinal()]);
        LinearLayout drawLinear = (LinearLayout) findViewById(R.id.detect_draw_linear);
        myLineView = new MyLineChartView(this, this);
        myLineView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        drawLinear.addView(myLineView);
        findViewById(R.id.btn_back).setOnClickListener(this);

        listview = (ListView) findViewById(R.id.listview);

        //人工输入按钮
        manualInputBtn = (Button)findViewById(R.id.bt_click_sample);
        if( SysApp.check_type == CheckType.LIPID) {
            manualInputBtn.setText(R.string.manual_input);
            manualInputBtn.setOnClickListener(this);
            manualInputBtn.setVisibility(View.VISIBLE);
        }
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

    /**
     * 弹出对话框：手工输入血脂
     */
    private void openInputLipidDialog() {
        final LipidData lipidData = (LipidData) BleController.getInstance().getData();
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.input_lipid_dialog_view, null);
        final EditText lipidChol = (EditText) view.findViewById(R.id.dialog_lipid_chol);
        final EditText lipidTrig = (EditText) view.findViewById(R.id.dialog_lipid_trig);
        lipidWDialog = DialogUtil.myViewAlertDialogHasCancel(this, view, R.string.dialog_input_chol_trig_title,
                R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String cholStr = lipidChol.getText().toString();
                        if (StringUtil.isNotEmpty(cholStr)) {
                            float cholValue = Float.parseFloat(cholStr);
                            if (cholValue >= 0 && cholValue <= 50) {
                                String trigStr = lipidTrig.getText().toString();
                                if (StringUtil.isNotEmpty(trigStr)) {
                                    float trigValue = Float.parseFloat(trigStr.toString());
                                    if (trigValue >= 0 && trigValue <= 50) {
                                        nativeRecordId=new Date().getTime();
                                        SysApp.getMyDBManager().addWaitForInspector(nativeRecordId,mUniqueKey,mUniqueKey,mUniqueKey);
                                        SysApp.getMyDBManager().addRecordItem(nativeRecordId, LipidTest.DATA_CHOLESTEROL, cholValue, DBHelper.RI_SOURCE_MANAUAL, null, SysApp.check_type.ordinal());
                                        SysApp.getMyDBManager().addRecordItem(nativeRecordId, LipidTest.DATA_TRIGLYCERIDES, trigValue, DBHelper.RI_SOURCE_MANAUAL, null, SysApp.check_type.ordinal());
                                        setDialogState(lipidWDialog, true);
                                        UploadAllNewInfoTask.submitOneRecordInfo(context,mUniqueKey, nativeRecordId,
                                                new SubmitServerListener() {
                                                    @Override
                                                    public void onResult(int result) {
                                                        DialogUtil.cancelDialog();
                                                        if (result == 0) { //success
                                                        } else if(result == -2){
                                                            ToastUtil.TextToast(context.getApplicationContext(), "提交失败，请检查网络是否正常...", 2000);
                                                        } else {
                                                            ToastUtil.TextToast(context.getApplicationContext(), "提交失败，请检查网络是否正常...", 2000);
                                                        }
                                                    }
                                                });
                                        EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_RESET, null));
                                        return;
                                    } else {
                                        ToastUtil.TextToast(getContext(), R.string.lipid_is_fanwei, 2000);
                                    }
                                } else {
                                    ToastUtil.TextToast(getContext(), R.string.lipid_trig_not_null, 2000);
                                }
                            } else {
                                ToastUtil.TextToast(getContext(), R.string.lipid_is_fanwei, 2000);
                            }
                        } else {
                            ToastUtil.TextToast(getContext(), R.string.lipid_chol_not_null, 2000);
                        }
                        setDialogState(lipidWDialog, false);
                    }
                }, R.string.cancel, null);
        lipidWDialog.show();
    }

    private void setDialogState(AlertDialog dialog, boolean isClosed) {
        try {
            Field field = dialog.getClass().getSuperclass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, isClosed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshLineByData() {
        isDetected = true;
        setData();
        myLineView.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
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
            case R.id.bt_click_sample: {
                switch (SysApp.check_type) {
                    case LIPID:  //血脂
                        openInputLipidDialog();
                        break;
                }
                break;
            }
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

    private void finishAndSendBack() {
        Intent intent = new Intent();
        intent.putExtra(CommConst.INTENT_EXTRA_KEY_IS_DETECTED, isDetected);
        setResult(CommConst.INTENT_REQUEST_DETECT, intent);
        this.finish();
    }

    public void onEventMainThread(BleEvent event) {
        String msg = "onEventMainThread收到了消息：" + event.getBleEvent();
        switch (event.getBleEvent()) {
            case CommConst.FLAG_BLE_CONNECT_SCUESS:
                myLineView.setTitleText(SysApp.check_type.getTitle()+"已连接");
                myLineView.getTitleView().setTextColor(Color.GREEN);
                prev_state=BluetoothProfile.STATE_CONNECTED;
                break;
            case CommConst.FLAG_BLE_CONNECT_UPDATE_STATE:
                myLineView.getTitleView().setTextColor(Color.GREEN);
                myLineView.setTitleText(event.getBlueStateStr());
                break;
            case CommConst.FLAG_BLE_EVENT_RESET:
                this.refreshLineByData();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //反注册EventBus
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        BleController ble_controller=BleController.getInstance();
        ble_controller.reset_controller();
        //反注册EventBus
        EventBus.getDefault().unregister(this);
    }
}
