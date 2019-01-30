package com.cnnet.otc.health.activities;


import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

import com.cnnet.otc.health.interfaces.SubmitServerListener;
import com.cnnet.otc.health.tasks.UploadAllNewInfoTask;
import com.cnnet.otc.health.util.DateUtil;
import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.bean.data.BloodPressureData;
import com.cnnet.otc.health.bean.data.LipidData;
import com.cnnet.otc.health.bean.data.LipidTest;
import com.cnnet.otc.health.bean.data.UricacidData;
import com.cnnet.otc.health.bean.data.WeightData;
import com.cnnet.otc.health.bluetooth.DeviceDialog;
import com.cnnet.otc.health.comm.BaseActivity;
import com.cnnet.otc.health.comm.CheckType;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.db.DBHelper;
import com.cnnet.otc.health.events.BTConnetEvent;
import com.cnnet.otc.health.managers.BtNormalManager;
import com.cnnet.otc.health.services.BluetoothService;
import com.cnnet.otc.health.util.DialogUtil;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.util.ToastUtil;
import com.cnnet.otc.health.views.MyLineChartView;
import com.cnnet.otc.health.views.adapter.DetectRecordListAdapter;
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
public class DetectBle3Activity extends BaseActivity implements OnChartValueSelectedListener, View.OnClickListener {
    private final String TAG = "DetectBlue3Activity";
    private static String[] devices = null;
    private TextView title=null;
    private Button manualInputBtn = null;  //人工输入按钮

    private DeviceDialog deviceDialog = null;

    private BtNormalManager btNormalManager = null;

    private MyLineChartView myLineView = null;

    private ListView listview;

    private AlertDialog hWDialog;
    private AlertDialog bpWDialog;
    private AlertDialog lipidWDialog;

    private String mUniqueKey = null;
    private long nativeRecordId = 0;
    private boolean isDetected = false;  //判断数据是否改变
    private boolean hasReal = false;  //判断是否存在实时数据
    private boolean isInputHeight = false;
    /*************当检测***************/
    private int mHeight = 0;  //获取身高
    private int mWaist = 0;  //获取腰围
    private String mSex = "";  //页面传过来的性别
    private String mBirthday = "";  //页面传过来的生日

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
        mSex = getIntent().getStringExtra("sex");
        mBirthday = getIntent().getStringExtra("birthday");
        //性别转换 1 男 2 女  转换为 1 男 0 女
        if("2".equals(mSex))
            mSex="0";
        nativeRecordId = getIntent().getLongExtra(CommConst.INTENT_EXTRA_KEY_NATIVE_RECORD_ID, new Date().getTime());
        Log.d(TAG, "mUniqueKey = " + mUniqueKey);
        hasReal = getIntent().getBooleanExtra(CommConst.INTENT_EXTRA_KEY_HAS_REAL, false);
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
        btNormalManager = new BtNormalManager(this, myLineView, nativeRecordId,mUniqueKey);

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
        if(hasReal) {
            myLineView.refreshRealTimeByMP(null);
        }
        drawLinear.addView(myLineView);
        findViewById(R.id.btn_back).setOnClickListener(this);

        listview = (ListView) findViewById(R.id.listview);

        //人工输入按钮
        manualInputBtn = (Button)findViewById(R.id.bt_click_sample);
        if(SysApp.check_type == CheckType.BLOOD_PRESSURE || SysApp.check_type == CheckType.URIC_ACID
                || SysApp.check_type == CheckType.LIPID) {
            manualInputBtn.setText(R.string.manual_input);
            manualInputBtn.setOnClickListener(this);
            manualInputBtn.setVisibility(View.VISIBLE);
        }

    }

    private void setData() {
        List<RecordItem>[] list = btNormalManager.getData().getRecordList(mUniqueKey);
        myLineView.addData(list, btNormalManager.getData().getInsName());

        DetectRecordListAdapter listAdapter = new DetectRecordListAdapter(this,
                btNormalManager.getData().getRecordAllList(mUniqueKey)
                ,btNormalManager.getData());
        listview.setAdapter(listAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isInputHeight) {
            isInputHeight = true;
            if(SysApp.check_type == CheckType.WEIGHT) {
                openInputHeightDialog();
            }
        }
    }

    /**
     * 弹出对话框：输入身高和腰围
     */
    private void openInputHeightDialog() {

        if(mHeight <= 0) {
            //初始化身高，腰围用来做是否开启蓝牙检测功能
            WeightData weightData = (WeightData) btNormalManager.getData();
            RecordItem hegithItem = SysApp.getMyDBManager().getOneRecordItem2(mUniqueKey, weightData.DATA_HEIGHT);
            RecordItem waistItem = SysApp.getMyDBManager().getOneRecordItem2(mUniqueKey, weightData.DATA_WAIST);

            if(hegithItem != null && hegithItem.getValue1() > 0) {
                mHeight = (int)hegithItem.getValue1();
            }
            if(waistItem != null && waistItem.getValue1() > 0) {
                mWaist = (int)waistItem.getValue1();
            }
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.input_height_waist_dialog_view, null);
        final EditText height = (EditText) view.findViewById(R.id.dialog_height);
        if(mHeight > 0) {
            height.setText(String.valueOf(mHeight));
        }
        final EditText waist = (EditText) view.findViewById(R.id.dialog_waist);
        if(mWaist > 0) {
            waist.setText(String.valueOf(mWaist));
        }
        hWDialog = DialogUtil.myViewAlertDialog(this, view, R.string.dialog_input_height_waist_title,
                R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String hStr = height.getText().toString();
                        if (StringUtil.isNotEmpty(hStr)) {
                            int hValue = Integer.parseInt(hStr);
                            if (hValue >= 50 && hValue <= 255) {
                                String wStr = waist.getText().toString();
                                if (StringUtil.isNotEmpty(wStr)) {
                                    int wValue = Integer.parseInt(wStr.toString());
                                    WeightData weightData = (WeightData) btNormalManager.getData();
                                    SysApp.getMyDBManager().addRecordItem(nativeRecordId, weightData.DATA_HEIGHT, hValue, DBHelper.RI_SOURCE_MANAUAL, null, SysApp.check_type.ordinal());
                                    SysApp.getMyDBManager().addRecordItem(nativeRecordId, weightData.DATA_WAIST, wValue, DBHelper.RI_SOURCE_MANAUAL, null, SysApp.check_type.ordinal());
                                    if(mSex != null && !"".equals(mSex)  )
                                        SysApp.getMyDBManager().addRecordItem(nativeRecordId, weightData.DATA_SEX, Integer.parseInt(mSex), DBHelper.RI_SOURCE_MANAUAL, null, SysApp.check_type.ordinal());
                                    if(mBirthday != null  && !"".equals(mBirthday)  ){
                                        int age = DateUtil.getAgeByBirthDayStr(String.valueOf(mBirthday));
                                        SysApp.getMyDBManager().addRecordItem(nativeRecordId, weightData.DATA_BIRTHDAY, age, DBHelper.RI_SOURCE_MANAUAL, null, SysApp.check_type.ordinal());
                                    }

                                    mWaist = wValue;
                                    mHeight = hValue;
                                    setDialogState(hWDialog, true);
                                    return;
                                } else {
                                    ToastUtil.TextToast(getContext(), R.string.waist_not_null, 2000);
                                }
                            } else {
                                ToastUtil.TextToast(getContext(), R.string.height_is_fanwei, 2000);
                            }
                        } else {
                            ToastUtil.TextToast(getContext(), R.string.height_not_null, 2000);
                        }
                        setDialogState(hWDialog, false);
                    }
                });
        hWDialog.show();
    }

    /**
     * 弹出对话框：手工输入血压
     */
    private void openInputBloodPressureDialog() {
        final BloodPressureData bpData = (BloodPressureData) btNormalManager.getData();
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.input_blood_pressure_dialog_view, null);
        final EditText BPHigh = (EditText) view.findViewById(R.id.dialog_bp_height);
        final EditText BPLow = (EditText) view.findViewById(R.id.dialog_bp_low);
        bpWDialog = DialogUtil.myViewAlertDialogHasCancel(this, view, R.string.dialog_input_bp_title,
                R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String hStr = BPHigh.getText().toString();
                        if (StringUtil.isNotEmpty(hStr)) {
                            int hValue = Integer.parseInt(hStr);
                            if (hValue >= 0 && hValue <= 350) {
                                String lStr = BPLow.getText().toString();
                                if (StringUtil.isNotEmpty(lStr)) {
                                    int lValue = Integer.parseInt(lStr.toString());
                                    if (lValue >= 0 && lValue <= 350) {
                                        nativeRecordId= new Date().getTime();
                                        SysApp.getMyDBManager().addWaitForInspector(nativeRecordId,mUniqueKey,mUniqueKey,mUniqueKey);
                                        SysApp.getMyDBManager().addRecordItem(nativeRecordId, bpData.DATA_BP_HIGHT, hValue, DBHelper.RI_SOURCE_MANAUAL, null, SysApp.check_type.ordinal());
                                        SysApp.getMyDBManager().addRecordItem(nativeRecordId, bpData.DATA_BP_LOW, lValue, DBHelper.RI_SOURCE_MANAUAL, null, SysApp.check_type.ordinal());
//                                        SysApp.getMyDBManager().addRecordItem(nativeRecordId, bpData.DATA_BP_PULSE, highPressure-lowPressure, DBHelper.RI_SOURCE_DEVICE, SysApp.btDevice.getAddress(), SysApp.check_type.ordinal());
                                        SysApp.getMyDBManager().addRecordItem(nativeRecordId, bpData.DATA_BP_PR, 0, DBHelper.RI_SOURCE_MANAUAL, null, SysApp.check_type.ordinal());
//                                        if(hValue>lValue)
                                        {
                                            SysApp.getMyDBManager().addRecordItem(nativeRecordId, bpData.DATA_BP_PULSE,hValue- lValue, DBHelper.RI_SOURCE_MANAUAL, null, SysApp.check_type.ordinal());
                                        }
                                        setDialogState(bpWDialog, true);
                                        /*UploadAllNewInfoTask.submitOneRecordInfo(this,mUniqueKey, nativeRecordId,
                                                new SubmitServerListener() {
                                                    @Override
                                                    public void onResult(int result) {
                                                        DialogUtil.cancelDialog();
                                                        if (result == 0) { //success
                                                        } else if(result == -2){
                                                            ToastUtil.TextToast(ctx.getApplicationContext(), "提交失败，请检查网络是否正常...", 2000);
                                                        } else {
                                                            ToastUtil.TextToast(ctx.getApplicationContext(), "提交失败，请检查网络是否正常...", 2000);
                                                        }
                                                    }
                                                });*/
                                        EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_RESET, null));
                                        return;
                                    } else {
                                        ToastUtil.TextToast(getContext(), R.string.bp_is_fanwei, 2000);
                                    }
                                } else {
                                    ToastUtil.TextToast(getContext(), R.string.bp_low_not_null, 2000);
                                }
                            } else {
                                ToastUtil.TextToast(getContext(), R.string.bp_is_fanwei, 2000);
                            }
                        } else {
                            ToastUtil.TextToast(getContext(), R.string.bp_height_not_null, 2000);
                        }
                        setDialogState(bpWDialog, false);
                    }
                }, R.string.cancel, null);
        bpWDialog.show();
    }

    /**
     * 弹出对话框：手工输入血脂
     */
    private void openInputLipidDialog() {
        final LipidData lipidData = (LipidData) btNormalManager.getData();
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
    /**
     * 弹出对话框：手工输入尿酸值
     */
    private void openInputUricAcidDialog() {
        final UricacidData UAData = (UricacidData) btNormalManager.getData();
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.input_uric_acid_view, null);
        final EditText uricAcid = (EditText) view.findViewById(R.id.dialog_uric_acid);
        lipidWDialog = DialogUtil.myViewAlertDialogHasCancel(this, view, R.string.dialog_input_ua_title,
                R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String uaStr = uricAcid.getText().toString();
                        if (StringUtil.isNotEmpty(uaStr)) {
                            float uaValue = Float.parseFloat(uaStr);
                            if (uaValue >= 0 && uaValue <= 700) {
                                SysApp.getMyDBManager().addRecordItem(nativeRecordId, UAData.DATA_UA, uaValue, DBHelper.RI_SOURCE_MANAUAL, null, SysApp.check_type.ordinal());
                                setDialogState(lipidWDialog, true);
                                EventBus.getDefault().post(new BTConnetEvent(CommConst.FLAG_CONNECT_EVENT_RESET, null));
                                return;
                            } else {
                                ToastUtil.TextToast(getContext(), R.string.ua_is_fanwei, 2000);
                            }
                        } else {
                            ToastUtil.TextToast(getContext(), R.string.ua_not_null, 2000);
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
                if(deviceDialog == null) {
                    deviceDialog = new DeviceDialog(this, R.style.dialog, myLineView, btNormalManager.getData());
                }
                deviceDialog.show();
                break;
            case R.id.btn_back:
                finishAndSendBack();
                break;
            case R.id.bt_click_sample: {
                switch (SysApp.check_type) {
                    case BLOOD_PRESSURE:  //血压
                        openInputBloodPressureDialog();
                        break;
                    case URIC_ACID:   //尿酸
                        openInputUricAcidDialog();
                        break;
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
            if(btNormalManager != null) {
                btNormalManager.destory();
                btNormalManager = null;
            }
            if(deviceDialog != null) {
                deviceDialog.clear();
                deviceDialog = null;
            }
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

    public void onEventMainThread(BTConnetEvent event) {
        String msg = "onEventMainThread收到了消息：" + event.getBTConnetEvent();
        switch (event.getBTConnetEvent()) {
            case CommConst.FLAG_CONNECT_EVENT_RESET:
                refreshLineByData();// 重新设置ListView的数据适配器
                break;
            case CommConst.FLAG_CONNECT_EVENT_DISPLAY_DATA:  //重新显示检测的值
                //listView.setAdapter(sa);
                break;
            case CommConst.FLAG_CONNECT_EVENT_SAVE_RECORD_ITEM: // 保存完成当前检测项后显示
                break;
            case CommConst.FLAG_CONNECT_EVENT_DISCONNECT_BT:
                BluetoothService.disConnect();
                break;
            case CommConst.FLAG_CONNECT_EVENT_UPDATE:
                //text.setText(DisplayView.text);
                //text.fitTextView();
                myLineView.getTitleView().setTextColor(Color.GREEN);
                myLineView.setTitleText(event.getMessage());
                Log.d(TAG, "text --- 5");
                break;
            case CommConst.FLAG_CONNECT_EVENT_UPDATE_STATE:
                myLineView.getTitleView().setTextColor(Color.WHITE);
                break;
            case CommConst.FLAG_CONNECT_EVENT_UPDATE_SCAN: {
                Log.d(TAG, "设置闪烁");
                myLineView.setTitleTextColor();
            }
            break;
            case CommConst.FLAG_CONNECT_EVENT_UPDATE_VALUE:
                // 设置单位
                break;
            case CommConst.FLAG_SAVE_BLUETOOTH_ADDRESS:// 插入蓝牙MAC地址

                break;
            case CommConst.FLAG_CONNECTED_STOP_TIMER:  //连接成功关闭当前定时器
                btNormalManager.stopTimer();
                break;
            case CommConst.FLAG_DISCONNECT_START_TIMER:  //断开连接后，开启定时器
                if(btNormalManager != null) {
                    btNormalManager.startTimer();
                }
                break;
            case CommConst.FLAG_CLOSE_BT_DEVICE:  //关闭蓝牙设备
                BluetoothService.closeBTDevice();
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
        if(btNormalManager != null) {
            btNormalManager.destory();
            btNormalManager = null;
        }
        if(deviceDialog != null) {
            deviceDialog.clear();
            deviceDialog = null;
        }
    }
}
