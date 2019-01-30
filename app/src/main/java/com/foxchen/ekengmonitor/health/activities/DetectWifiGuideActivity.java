package com.foxchen.ekengmonitor.health.activities;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.comm.BrandType;
import com.cnnet.otc.health.comm.CheckType;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;

public class DetectWifiGuideActivity extends Activity {
    private WifiInfo wifiInfo =null;
    private static String[] devices = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_wifi_guide);
        ImageView imageView= (ImageView) findViewById(R.id.detectwifiintruimage);


        //设置标题和引导图片
        SysApp.brand_type= BrandType.LS;
        if (SysApp.getmUniqueKey()!=null)
            SysApp.setmUniqueKey(getIntent().getStringExtra(CommConst.INTENT_EXTRA_KEY_MEMBER_UNIQUEKEY));

        if (getIntent().getStringExtra(CommConst.INTENT_EXTRA_KEY_DEVICE_TYPE)!=null)
            initCheckType(Integer.parseInt(getIntent().getStringExtra(CommConst.INTENT_EXTRA_KEY_DEVICE_TYPE)));
        if(SysApp.check_type==CheckType.LS_WIFI_BLOOD_PRESSURE){
            imageView.setBackgroundResource(R.drawable.image_lswifibloodpresure);
        }else if(SysApp.check_type==CheckType.LS_WIFI_WEIGHT){
            imageView.setBackgroundResource(R.drawable.image_lswifiweight);
        }


        findViewById(R.id.btn_back).setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        } );

        //创建监听
        Button btn = (Button) findViewById(R.id.wifiNextBtn);
        btn.setOnClickListener(new  Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                 //下一步到设置页面
                WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
                wifiInfo = wifiManager.getConnectionInfo();
                if(wifiInfo.getSSID()==null){
                    //当前无网络情况 到引导设置网络
                    Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                    startActivity(wifiSettingsIntent);
                }else{
                    Intent intent= new Intent(DetectWifiGuideActivity.this,DetectWifiActivity.class);
                    startActivity(intent);
                }
            }

        });
    }
    protected void initCheckType(int deviceType){
        switch (deviceType) {
            case 0://血压
                SysApp.check_type = CheckType.LS_WIFI_BLOOD_PRESSURE;
                break;
            case 1://体重
                SysApp.check_type = CheckType.LS_WIFI_WEIGHT;
                break;


        }
    }

}
