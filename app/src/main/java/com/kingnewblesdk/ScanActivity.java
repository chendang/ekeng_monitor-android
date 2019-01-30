package com.kingnewblesdk;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cnnet.otc.health.comm.CommConst;
import com.foxchen.ekengmonitor.R;
import com.foxchen.ekengmonitor.WebViewActivity;
import com.kitnew.ble.QNApiManager;
import com.kitnew.ble.QNBleApi;
import com.kitnew.ble.QNBleDevice;
import com.kitnew.ble.QNBleScanCallback;
import com.kitnew.ble.QNResultCallback;
import com.kitnew.ble.QNUser;
import com.kitnew.ble.utils.QNLog;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hdr on 15/12/9.
 */
public class ScanActivity extends AppCompatActivity implements View.OnClickListener , DatePicker.OnDateChangedListener{

    QNBleApi qnBleApi;

    RecyclerView recyclerView;
    Button scanBtn;
    TextView hisBtn;
    RadioGroup sexRg;
    RadioGroup scanModeRg;
    RadioGroup steadyEnableRg;
    RadioGroup unitRg;
    EditText idEt;
    EditText heightEt;
    TextView birthdayEt;
    private Context context;
    private StringBuffer date;
    private int year, month, day;
    CheckBox storageModeCb;

    final List<QNBleDevice> devices = new ArrayList<>();
    DeviceListAdapter deviceListAdapter;

    private NumberPicker numberPicker;
    private PopupWindow popupWindow;
    private View workingAge_view;
    private int height = 170;
    private Button submit_workingAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan);

        context =  this;
        /*android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }*/
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
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
        if (Build.VERSION.SDK_INT >= 23) {

            //校验是否已具有模糊定位权限

            if (ContextCompat.checkSelfPermission(ScanActivity.this,

                    Manifest.permission.ACCESS_COARSE_LOCATION)

                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(ScanActivity.this,  new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},  1);

            }

        }
        initNumberPicker();
        initViews();
        initApi();
        initDateTime();
//        QNUser user = this.buildUser();
//        QNBleDevice qnBleDevice = new QNBleDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice("FA:E5:83:E4:07:1F"));
//        startActivity(ConnectActivity.getCallIntent(this, user, qnBleDevice));
    }


    void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceListAdapter = new DeviceListAdapter();
        recyclerView.setAdapter(deviceListAdapter);

        scanBtn = (Button) findViewById(R.id.scanBtn);
        scanBtn.setOnClickListener(this);

        hisBtn = (TextView) findViewById(R.id.hisBtn);
        hisBtn.setOnClickListener(this);

        idEt = (EditText) findViewById(R.id.idEt);
        idEt.setText(getIntent().getStringExtra(CommConst.INTENT_EXTRA_KEY_MEMBER_UNIQUEKEY));

        heightEt = (EditText) findViewById(R.id.heightEt);
        heightEt.setText(height + "CM");

        birthdayEt = (TextView) findViewById(R.id.birthdayEt);
        date = new StringBuffer();
        birthdayEt.setOnClickListener(this);

        sexRg = (RadioGroup) findViewById(R.id.sexRG);
        scanModeRg = (RadioGroup) findViewById(R.id.scanMode);
        steadyEnableRg = (RadioGroup) findViewById(R.id.steadyEnable);

        unitRg = (RadioGroup) findViewById(R.id.measureUnit);

        storageModeCb = (CheckBox) findViewById(R.id.storageModeCb);

        int unit = QNApiManager.getApi(getBaseContext()).getWeightUnit();
        int resId = R.id.kg;
        switch (unit) {
            case QNBleApi.WEIGHT_UNIT_LB:
                resId = R.id.lb;
                break;
            case QNBleApi.WEIGHT_UNIT_JIN:
                resId = R.id.jin;
                break;
        }
        unitRg.check(resId);

        unitRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.kg:
                        QNApiManager.getApi(getBaseContext()).setWeightUnit(QNBleApi.WEIGHT_UNIT_KG);
                        break;
                    case R.id.lb:
                        QNApiManager.getApi(getBaseContext()).setWeightUnit(QNBleApi.WEIGHT_UNIT_LB);
                        break;
                    case R.id.jin:
                        QNApiManager.getApi(getBaseContext()).setWeightUnit(QNBleApi.WEIGHT_UNIT_JIN);
                        break;
                }
            }
        });

// 选择身高
        heightEt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // 设置初始值
                numberPicker.setValue(height);

                // 强制隐藏键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                // 填充布局并设置弹出窗体的宽,高
                popupWindow = new PopupWindow(workingAge_view,
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                // 设置弹出窗体可点击
                popupWindow.setFocusable(true);
                // 设置弹出窗体动画效果
                //popupWindow.setAnimationStyle(R.style.AnimBottom);
                // 触屏位置如果在选择框外面则销毁弹出框
                popupWindow.setOutsideTouchable(true);
                // 设置弹出窗体的背景
                popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                popupWindow.showAtLocation(workingAge_view,
                        Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

                // 设置背景透明度
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.5f;
                getWindow().setAttributes(lp);

                // 添加窗口关闭事件
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

                    @Override
                    public void onDismiss() {
                        WindowManager.LayoutParams lp = getWindow().getAttributes();
                        lp.alpha = 1f;
                        getWindow().setAttributes(lp);
                    }

                });
            }

        });

        // 确定身高
        submit_workingAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                height = numberPicker.getValue();
                heightEt.setText(height + "CM");
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 初始化滚动框布局
     */
    private void initNumberPicker() {
        workingAge_view = LayoutInflater.from(this).inflate(R.layout.popupwindow, null);
        submit_workingAge = (Button) workingAge_view.findViewById(R.id.submit_workingAge);
        numberPicker = (NumberPicker) workingAge_view.findViewById(R.id.numberPicker);
        numberPicker.setMaxValue(240);
        numberPicker.setMinValue(80);
        numberPicker.setFocusable(false);
        numberPicker.setFocusableInTouchMode(false);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setNumberPickerDividerColor(numberPicker);
    }
    /**
     * 自定义滚动框分隔线颜色
     */
    private void setNumberPickerDividerColor(NumberPicker number) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(number, new ColorDrawable(ContextCompat.getColor(this, R.color.numberpicker_line)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void initApi() {
        QNLog.DEBUG = true;
        QNLog.log("application启动");

        //123456789是测试版的appid
        QNApiManager.getApi(getApplicationContext()).initSDK("123456789", new QNResultCallback() {
            @Override
            public void onCompete(int errorCode) {
                Log.i("hdr", "执行结果校验:" + errorCode);
            }
        });
        qnBleApi = QNApiManager.getApi(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scanBtn:
                if (qnBleApi.isScanning()) {
                    doStopScan();
                } else {
                    doStartScan();
                }
                break;
            case R.id.hisBtn:
                gotoH5();
                break;
            case R.id.birthdayEt:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (date.length() > 0) { //清除上次记录的日期
                            date.delete(0, date.length());
                        }
                        birthdayEt.setText(date.append(String.valueOf(year)).append("-").append(String.valueOf(month)).append("-").append(day));
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog = builder.create();
                View dialogView = View.inflate(context, R.layout.dialog_date, null);
                final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);

                dialog.setTitle("设置日期");
                dialog.setView(dialogView);
                dialog.show();
                //初始化日期监听事件
                datePicker.init(year, month - 1, day, this);
                break;
        }
    }
    /**
     * 获取当前的日期和时间
     */
    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

    }
    /**
     * 日期改变的监听事件
     *
     * @param view
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     */
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }
    @Override
    protected void onPause() {
        super.onPause();
        doStopScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qnBleApi.disconnectAll();
    }

    void doStopScan() {
        if (!qnBleApi.isScanning()) {
            return;
        }
        scanBtn.setText("开始扫描");
        qnBleApi.stopScan();

    }

    void doStartScan() {
        if (qnBleApi.isScanning()) {
            return;
        }
        scanBtn.setText("停止扫描");
        devices.clear();
        deviceListAdapter.notifyDataSetChanged();
        int scanMode = scanModeRg.getCheckedRadioButtonId() == R.id.scanModeFirst ? QNBleApi.SCAN_MODE_FIRST : QNBleApi.SCAN_MODE_ALL;
        //设置扫描模式，如无特殊需要，不需要设置
        qnBleApi.setScanMode(scanMode);
        boolean isEnable = steadyEnableRg.getCheckedRadioButtonId() == R.id.steadyOpen ? true : false;
        qnBleApi.setSteadyBodyfat(true);
        qnBleApi.startLeScan(null, null, new QNBleScanCallback() {
            @Override
            public void onCompete(int errorCode) {
                Log.i("hdr-ble", "执行结果:" + errorCode);
            }

            @Override
            public void onScan(QNBleDevice bleDevice) {
                QNLog.log("hdr-ble", "name:" + bleDevice.getDeviceName() + " mac:" + bleDevice.getMac()
                        + " model:" + bleDevice.getModel() + " 是否开机:" + bleDevice.getDeviceState());
                devices.add(bleDevice);
                deviceListAdapter.notifyItemInserted(devices.size() - 1);
            }
        });
        hideInput();

    }
    public void gotoH5() {
        Intent intent = new Intent( this, WebViewActivity.class);
        String _url = "BodyFatData.html";// 此处还可以在url里传递数据
        intent.putExtra("url", _url);
        intent.putExtra("username", idEt.getText().toString());
        this.startActivity(intent);
    }

    public void hideInput() { // 不显示键盘
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0); // 强制隐藏键盘
        }
    }

    class DeviceListAdapter extends RecyclerView.Adapter<ScanActivity.DeviceViewHolder> {

        LayoutInflater inflater;

        DeviceListAdapter() {
            inflater = LayoutInflater.from(ScanActivity.this);
        }

        @Override
        public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = inflater.inflate(R.layout.device_list_item, parent, false);
            return new DeviceViewHolder(v);
        }

        @Override
        public void onBindViewHolder(DeviceViewHolder holder, int position) {
            holder.init(devices.get(position));
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }
    }

    void doConnect(QNBleDevice qnBleDevice) {
        QNUser user = buildUser();
        if (user == null) {
            return;
        }
        startActivity(ConnectActivity.getCallIntent(this, user, qnBleDevice));
    }

    QNUser buildUser() {
        String id = idEt.getText().toString();
        String errorString = null;
        Date birthday = null;
        if (id.trim().equals("")) {
            errorString = "请填写有效的用户id";
        } else if (heightEt.getText().length() == 0) {
            errorString = "请填写有效的身高";
        } else {
            String birthdayString = birthdayEt.getText().toString();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
            try {
                birthday = dateFormat.parse(birthdayString);
            } catch (Exception e) {
                errorString = "请按照 yyyy-M-d 的格式输入生日";
            }
        }

        if (errorString != null) {
            Toast.makeText(this, errorString, Toast.LENGTH_SHORT).show();
            return null;
        }
        int height = Integer.parseInt(heightEt.getText().toString());
        int gender;
        if (sexRg.getCheckedRadioButtonId() == R.id.sexMan) {
            gender = 1;
        } else {
            gender = 0;
        }

        return new QNUser(id, height, gender, birthday);

    }

    class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameTv;
        TextView macTv;
        Button connectBtn;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            macTv = (TextView) itemView.findViewById(R.id.macTv);
            connectBtn = (Button) itemView.findViewById(R.id.connectBtn);
            connectBtn.setText("立即连接");
            connectBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int storageMode = storageModeCb.isChecked() ? QNBleApi.RECEIVE_STORAGE_DATA : QNBleApi.IGNORE_STORAGE_DATA;
            //设置是否接收存储数据，如无特殊需要，不需要设置
            qnBleApi.setReceiveOrIgnoreStorageData(storageMode);
            doStopScan();

            final QNBleDevice device = devices.get(getAdapterPosition());

            //停止扫描后，最后延时一会儿再做连接操作
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doConnect(device);
                }
            }, 150);
        }

        void init(QNBleDevice device) {
            nameTv.setText(device.getModel() + "  " + (device.getDeviceState() == QNBleDevice.DEVICE_STATE_ON ? "开机" : "关机"));
            macTv.setText(device.getMac());
        }
    }
}
