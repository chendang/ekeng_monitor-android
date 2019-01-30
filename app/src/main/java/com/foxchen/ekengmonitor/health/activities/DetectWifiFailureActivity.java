package com.foxchen.ekengmonitor.health.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

import com.foxchen.ekengmonitor.R;

public class DetectWifiFailureActivity extends Activity {

    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_wifi_failure);
        ctx=this;
        //创建监听
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new  Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, DetectWifiGuideActivity.class);
                startActivity(intent);
            }

        });
        findViewById(R.id.btn_back).setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        } );
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }
}
