package com.kingnewblesdk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cnnet.otc.health.managers.RequestManager;
import com.foxchen.ekengmonitor.R;
import com.kingnewblesdk.views.CircleBar;
import com.kitnew.ble.QNApiManager;
import com.kitnew.ble.QNBleApi;
import com.kitnew.ble.QNBleCallback;
import com.kitnew.ble.QNBleDevice;
import com.kitnew.ble.QNData;
import com.kitnew.ble.QNItemData;
import com.kitnew.ble.QNUser;
import com.kitnew.ble.utils.QNLog;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hdr on 15/12/11.
 */
public class ConnectActivity extends AppCompatActivity implements QNBleCallback {

    TextView nameTv;
    TextView macTv;
    Button connectBtn;

    RadioGroup sexRg;
    EditText idEt;
    EditText heightEt;
    EditText birthdayEt;

    TextView weightTv;
    TextView modelTv;
    TextView statusTv;
    RecyclerView reportRv;

    QNBleApi bleApi;
    private Context context;
    QNUser user;
    QNBleDevice device;
    private CircleBar progress;
    private TextView weightT;
    private TextView statusT;

    IndicatorAdapter indicatorAdapter;


    public static Intent getCallIntent(Context context, QNUser user, QNBleDevice device) {
        return new Intent(context, ConnectActivity.class).putExtra("user", user).putExtra("device", device);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        context=this;
        initViews();

        initData();
        initProgress(10);
//        resetUserView();
    }

    void initViews() {
        nameTv = (TextView) findViewById(R.id.nameTv);
        macTv = (TextView) findViewById(R.id.macTv);
        connectBtn = (Button) findViewById(R.id.connectBtn);

        weightTv = (TextView) findViewById(R.id.weightTv);
        modelTv = (TextView) findViewById(R.id.modelTv);
        statusTv = (TextView) findViewById(R.id.statusTv);
        statusT = (TextView) findViewById(R.id.status);
       /* idEt = (EditText) findViewById(R.id.idEt);
        heightEt = (EditText) findViewById(R.id.heightEt);
        birthdayEt = (EditText) findViewById(R.id.birthdayEt);

        sexRg = (RadioGroup) findViewById(R.id.sexRG);*/

        reportRv = (RecyclerView) findViewById(R.id.reportRv);
        reportRv.setLayoutManager(new LinearLayoutManager(this));

        indicatorAdapter = new IndicatorAdapter(this);
        reportRv.setAdapter(indicatorAdapter);
        reportRv.addItemDecoration(new RecyclerItemDecoration());

        /*findViewById(R.id.modifyBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyUserInfo();
            }
        });*/

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectBtn.getText().equals("连接")) {
                    doConnect();
                } else {
                    doDisconnect();
                }
            }
        });
    }

    void initData() {
        bleApi = QNApiManager.getApi(this);

        user = getIntent().getParcelableExtra("user");
        device = getIntent().getParcelableExtra("device");

        nameTv.setText(device.getDeviceName());
        macTv.setText(device.getMac());

        connectBtn.setText("连接");

        doConnect();
    }
    private void initProgress(int val) {

        progress = (CircleBar) findViewById(R.id.progress);
        weightT = (TextView) findViewById(R.id.weight);
//        lastTag = (TextView) findViewById(R.id.tag);

//        int hour = min / 60;

        //初始化显示时间
//        if (hour < 24) {
//            weight.setText(hour + "");
////            lastTag.setText("小时");
//        } else {
//            int day = hour / 24;
//            weight.setText(day + "");
////            lastTag.setText("天");
//        }

        //初始化进度条
        progress.setMaxstepnumber(110);
        progress.update(val, 3000);
    }
/*
    void resetUserView() {
        this.idEt.setText(this.user.getId());
        this.sexRg.check(this.user.getGender() == 0 ? R.id.sexWoman : R.id.sexMan);
        this.birthdayEt.setText(new SimpleDateFormat("yyyy-MM-dd").format(this.user.getBirthday()));
        this.heightEt.setText(String.valueOf(this.user.getHeight()));
    }

    void modifyUserInfo() {
        QNUser newUser = buildUser();
        this.user = newUser;
        bleApi.setUser(device.getMac(), newUser.getId(), newUser.getHeight(), newUser.getGender(), newUser.getBirthday());
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

    }*/

    void doConnect() {
        bleApi.connectDevice(device, user.getId(), user.getHeight(), user.getGender(), user.getBirthday(), this);
    }

    void doDisconnect() {
        bleApi.disconnectDevice(device.getMac());
    }

    @Override
    public void onConnectStart(QNBleDevice bleDevice) {
        statusTv.setText("正在连接");
        statusT.setText("正在连接");
        connectBtn.setText("断开");
    }

    @Override
    public void onConnected(QNBleDevice bleDevice) {
        statusTv.setText("连接成功");
        statusT.setText("连接成功");
        modelTv.setText(bleDevice.getModel());
    }

    @Override
    public void onDisconnected(QNBleDevice bleDevice, int status) {
        statusTv.setText("连接已断开");
        statusT.setText("连接已断开");
        connectBtn.setText("连接");
    }

    @Override
    public void onUnsteadyWeight(QNBleDevice bleDevice, float weight) {
        int unit = bleApi.getWeightUnit();
        String unitString = " kg";
        if (unit == QNBleApi.WEIGHT_UNIT_LB) {
            unitString = " lb";
        } else if (unit == QNBleApi.WEIGHT_UNIT_JIN) {
            unitString = " 斤";
        }
        weightTv.setText(weight + unitString);
        weightT.setText(weight+"");
        progress.update((int)weight, 3000);
    }

    @Override
    public void onReceivedData(QNBleDevice bleDevice, QNData data) {
        statusTv.setText("测量完成");
        statusT.setText("测量完成");
        weightT.setText(data.getWeight()+"");
        progress.update((int)data.getWeight(), 3000);
        indicatorAdapter.setSex(String.valueOf(user.getGender()));
        indicatorAdapter.setHeight(String.valueOf(user.getHeight()));
        indicatorAdapter.setQnData(data);

        Map dataMap =  new HashMap();


        dataMap.put("id",user.getId());
        dataMap.put("height",user.getHeight());
        dataMap.put("birthday", user.getBirthday());
        dataMap.put("gender", user.getGender());
        dataMap.put("mac", device.getMac());
        dataMap.put("scale_name", device.getDeviceName());
        dataMap.put("createtime",data.getCreateTime());
        List list =  data.getAll();
        for (int i= 0;i<list.size();i++){
            QNItemData itemData =  (QNItemData)list.get(i);
            switch (itemData.type) {
                case 2:
                    dataMap.put("weight",itemData.value);
                    break;
                case 3:
                    dataMap.put("bmi",itemData.value);
                    break;
                case 6:
                    dataMap.put("visfat",itemData.value);
                    break;
                case 9:
                    dataMap.put("muscle",itemData.value);
                    break;

                case 10:
                    dataMap.put("bone",itemData.value);
                    break;
                case 4:
                    if("1".equals(user.getGender()))
                        itemData.value = itemData.value+11;
//                        dataMap.put("bodyfat",itemData.value+11);

                    dataMap.put("bodyfat",itemData.value);
                    break;
                case 7:
                    dataMap.put("water",itemData.value);
                    break;
                case 5:
                    dataMap.put("subfat",itemData.value);
                    break;
                case 11:
                    dataMap.put("protein",itemData.value);
                    break;
                case 12:
                    dataMap.put("bmr",itemData.value);
                    break;
                default:
                    dataMap.put(itemData.name,itemData.value);
                    break;
            }
        }

        RequestManager.postTZCRecordInfo(context,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("体脂称提交数据返回",jsonObject.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("体脂称提交数据返回",volleyError.getMessage());
                    }
                }, dataMap);

        QNLog.log("he", data);
    }

    @Override
    public void onReceivedStoreData(QNBleDevice bleDevice, List<QNData> datas) {
    }

    @Override
    public void onDeviceModelUpdate(QNBleDevice bleDevice) {
        Log.d("hdr", "读取到了新的型号：" + bleDevice.getModel());
    }

    @Override
    public void onLowPower() {
        Log.d("hdr", "称端电量过低");
    }


    @Override
    public void onCompete(int errorCode) {

    }


/**
 * 添加分割线
 */
private class RecyclerItemDecoration extends RecyclerView.ItemDecoration{

    private Paint mPaint ;
    /**
     * 所有初始化可以都放在构造方法中，来初始化一些基本参数
     */
    public RecyclerItemDecoration(){
        mPaint = new Paint() ;
        mPaint.setAntiAlias(true);  //设置抗锯齿
        mPaint.setColor(Color.GRAY); //设置画笔颜色
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // 代表在每个item底部的位置留出10px 来绘制分割线 , 效果可以实现，问题是最后一个item还有分割线

        int position = parent.getChildAdapterPosition(view);

        // position : 每一个item的角标 从0 - 25
        // getChildCount() : 表示获取当前角标对应的item个数
        // 比如当前角标position=0 -> 则getChildCount() =1
        // 当前角标position=1 -> 则getChildCount() =2
        // 当前角标position=2 -> 则getChildCount() =3

        //由于getChildCount() 是不断变化的，所以不能保证是最后一条

//            Log.e("TAG" , "position -> " + position + ", " + "parent -> " + parent.getChildCount()) ;
//            if (position != parent.getChildCount() -1){
//                outRect.bottom = 10 ;
//            }


        // 保证第一条：可以保证给每个item的头部添加分割线，但是不给第一个添加就可以  , 这个方式是可以的
//            if (position != 0){
//                outRect.top = 10 ;
//            }


        // 留出头部位置 ，即就是第一个item上边的位置
        if (position != 0){
            outRect.top = 5 ;
        }
    }



    // 绘制分割线
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        // 利用canvas想绘制什么就绘制什么
        // 在每一个item的头部来绘制
        int childCount = parent.getChildCount() ;


        // 指定绘制的区域
        Rect rect = new Rect() ;
        rect.left = parent.getPaddingLeft() ;
        rect.right = parent.getWidth() - parent.getPaddingRight() ;
        // 头部第一个不需要绘制分割线，所以直接从第二个开始
        for (int i = 1; i < childCount; i++) {
            // 分割线的底部就是 item的头部
            rect.bottom = parent.getChildAt(i).getTop() ;
            rect.top = rect.bottom - 10 ;
            c.drawRect(rect , mPaint);
        }
    }
}

}