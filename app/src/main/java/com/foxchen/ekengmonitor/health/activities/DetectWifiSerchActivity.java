package com.foxchen.ekengmonitor.health.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.foxchen.ekengmonitor.R;
import com.lifesense.wificonfig.LSWifiConfig;
import com.lifesense.wificonfig.LSWifiConfigCode;
import com.lifesense.wificonfig.LSWifiConfigDelegate;

public class DetectWifiSerchActivity extends Activity {

    private Context mContext ;
    TextView textView;
    LSWifiConfig lsWifiConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_wifi_serch);
        mContext=this;

        findViewById(R.id.btn_back).setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
                if(lsWifiConfig!=null)
                    lsWifiConfig.stopConfig();
            }
        } );

        Intent intent1 = getIntent();
        System.loadLibrary("LSWifiConfig");
        lsWifiConfig= LSWifiConfig.shared();
        lsWifiConfig.setTimeout(60);
        lsWifiConfig.startConfig(intent1.getStringExtra("SSID"),intent1.getStringExtra("WIFIPassword"));

        lsWifiConfig.setDelegate(new LSWifiConfigDelegate(){
            @Override
            public void wifiConfigCallBack(LSWifiConfig lsWifiConfig, LSWifiConfigCode lsWifiConfigCode) {
                Log.d("搜索结果", String.valueOf(lsWifiConfigCode.ordinal()));;
                if(lsWifiConfigCode.name().equalsIgnoreCase("Success")){
                    //成功
//                    textView.setText("成功！");
                    Intent intent = new Intent(mContext, DetectWifiSuccActivity.class);
                    startActivity(intent);
                }else if(lsWifiConfigCode.name().equalsIgnoreCase("TimeOut")){
                    //超时
//                    textView.setText("搜索超时，请检查设备再试！");
                    Intent intent = new Intent(mContext, DetectWifiFailureActivity.class);
                    startActivity(intent);
            }else{
//                    if(lsWifiConfigCode.name().equalsIgnoreCase("Error")){
                //失败
                //textView.setText("搜索未超时，但未匹配成功");
                Intent intent = new Intent(mContext, DetectWifiFailureActivity.class);
                startActivity(intent);

//                    }
            }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(lsWifiConfig!=null)
            lsWifiConfig.stopConfig();
        this.finish();
    }
}
