package com.foxchen.ekengmonitor.health.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.foxchen.ekengmonitor.R;
import com.foxchen.ekengmonitor.SDK_WebApp;


public class DetectWifiSuccActivity extends Activity {
    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_wifi_succ);
        ctx=this;
        //创建监听
        Button btn = (Button) findViewById(R.id.button1);
        btn.setOnClickListener(new  Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, DetectWifiGuideActivity.class);
                startActivity(intent);

            }

        });
        Button btn2 = (Button) findViewById(R.id.button2);
        btn.setOnClickListener(new  Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ctx,SDK_WebApp.class));

            }

        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }
}
