package com.cnnet.otc.health.activities;


import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.bean.data.BloodGlucoseData;
import com.cnnet.otc.health.bean.data.GluTestCode;
import com.cnnet.otc.health.bluetooth.DeviceDialog;
import com.cnnet.otc.health.comm.BaseActivity;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.events.BTConnetEvent;
import com.cnnet.otc.health.managers.BtNormalManager;
import com.cnnet.otc.health.services.BluetoothService;
import com.cnnet.otc.health.util.DialogUtil;
import com.cnnet.otc.health.views.MyLineChartView;
import com.cnnet.otc.health.views.adapter.DetectGluListAdapter;
import com.cnnet.otc.health.views.adapter.GluDeleteEvent;
import com.cnnet.otc.health.views.adapter.GluDeleteEventListener;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import de.greenrobot.event.EventBus;


/**
 * Created by SZ512 on 2015/12/31.
 */
public class DetectBle2Activity extends BaseActivity implements OnChartValueSelectedListener, View.OnClickListener {
    private final String TAG = "DetectBlue3Activity";
    private static String[] devices = null;
    private TextView title=null;
    private Button manualInputBtn = null;  //人工输入按钮
    private BloodGlucoseData glu_data=null;

    private DeviceDialog deviceDialog = null;

    private BtNormalManager btNormalManager = null;

    private MyLineChartView myLineView = null;

    private ListView listview;

    private String mUniqueKey = null;
    private long nativeRecordId = 0;
    private boolean isDetected = false;  //判断数据是否改变

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);
        if(!checkBlue()){
            finish();
            return;
        }
        mUniqueKey = getIntent().getStringExtra(CommConst.INTENT_EXTRA_KEY_MEMBER_UNIQUEKEY);
        nativeRecordId = getIntent().getLongExtra(CommConst.INTENT_EXTRA_KEY_NATIVE_RECORD_ID, 0);
        Log.d(TAG, "mUniqueKey = " + mUniqueKey);
        initCheckType( Integer.parseInt(getIntent().getStringExtra(CommConst.INTENT_EXTRA_KEY_DEVICE_TYPE)));
        try {
            initLineView();
            initBlue();
            // add data
            setData();
            if(glu_data.getGlu_datas().size()==0)
            {
                glu_data.setCur_test_code(GluTestCode.GLU_EMPTY);
                manualInputBtn.setText(glu_data.getTestcodeTitle());

            }
            else {
                showSelectGluDlg();
            }
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
    AlertDialog gluSelectDialog;
    /**
     * 弹出对话框：选择血糖测量项目
     */
    private void showSelectGluDlg() {
        try {
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.glu_test_select_dialog_view, null);
            final ListView lstView = (ListView) view.findViewById(R.id.list_glu_select);
            final ArrayAdapter<GluTestCode> adapter=new ArrayAdapter<GluTestCode>(this, android.R.layout.simple_list_item_single_choice, glu_data.getUntested7PointTestCode());
            lstView.setAdapter(adapter);
            lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GluTestCode test_code =glu_data.getUntested7PointTestCode().get(position);
                    if (test_code != null) {
                        glu_data.setCur_test_code(test_code);
                    }
                }
            });
            gluSelectDialog = DialogUtil.myViewAlertDialogHasCancel(this, view, R.string.dialog_glu_select_testitem,
                    R.string.confirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(glu_data.getCur_test_code()!=null)
                                manualInputBtn.setText(glu_data.getTestcodeTitle());
                            else
                                manualInputBtn.setText("无当前检测项目");
                            //setDialogState(gluSelectDialog, false);

                        }
                    }, R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            glu_data.setCur_test_code(null);
                            manualInputBtn.setText("无当前检测项目");
                            //setDialogState(gluSelectDialog, false);
                        }
                    });
            gluSelectDialog.show();
        }
        catch (Exception e)
        {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private void initBlue() {
        //注册EventBus
        EventBus.getDefault().register(this);
        btNormalManager = new BtNormalManager(this, myLineView, nativeRecordId,mUniqueKey);
        findViewById(R.id.bt_detect_connect).setOnClickListener(this);
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
        manualInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTestsuitMenu();
            }
        });
        manualInputBtn.setVisibility(View.VISIBLE);
    }

    void showTestsuitMenu()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("请选择血糖检查项目");
        builder.setItems(this.glu_data.getUntested7PointTitles(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GluTestCode test_code=glu_data.getUntested7PointTestCode().get(which);
                glu_data.setCur_test_code(test_code);
                manualInputBtn.setText(test_code.getTitle());
                dialog.dismiss();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
        WindowManager.LayoutParams layoutParams =dialog.getWindow().getAttributes();
        layoutParams.width=this.manualInputBtn.getWidth();
        dialog.getWindow().setAttributes(layoutParams);
    }

    private void setLineViewData()
    {
        List<RecordItem>[] list = glu_data.getRecordList(mUniqueKey);
        List<RecordItem> item_list=list[0];
        Hashtable<String,List<RecordItem>> item_list_bag=new Hashtable<String,List<RecordItem>>();
        for(RecordItem itm:item_list)
        {
            List<RecordItem> tmp_item_list;
            if(!item_list_bag.containsKey(itm.getTestCode()))
            {
                tmp_item_list=new ArrayList<RecordItem>();
                item_list_bag.put(itm.getTestCode(),tmp_item_list);
            }
            else
                tmp_item_list=item_list_bag.get(itm.getTestCode());
            tmp_item_list.add(itm);
        }
        int cnt=item_list_bag.size();
        String [] ins_names=new String [cnt];
        List<RecordItem> [] curvelist=new ArrayList[cnt];
        Enumeration<String> keys=item_list_bag.keys();
        int i=0;
        while(keys.hasMoreElements())
        {
            String key=keys.nextElement();
            curvelist[i]=item_list_bag.get(key);
            ins_names[i]=GluTestCode.find(key).getTitle();
            i++;
        }
        myLineView.addData(curvelist, ins_names);

    }

    private void setData() {
        this.glu_data=(BloodGlucoseData)  btNormalManager.getData();
        List<RecordItem>[] list = glu_data.getRecordList(mUniqueKey);
        myLineView.addData(list, glu_data.getInsName());
        //setLineViewData();

        DetectGluListAdapter listAdapter = new DetectGluListAdapter(this,
                glu_data.getRecordAllList(mUniqueKey));
        listAdapter.addDeleteListener(new GluDeleteEventListener() {
            @Override
            public void OnCheckItemDelete(GluDeleteEvent evt) {
                glu_data.deleteTestValue(evt.getItem());
            }
        });
        listview.setAdapter(listAdapter);
    }


    private void refreshLineByData() {
        isDetected = true;
        setData();
        if(glu_data.getCur_test_code()==null)
            manualInputBtn.setText("无当前检测项目");
        myLineView.invalidate();
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

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}
