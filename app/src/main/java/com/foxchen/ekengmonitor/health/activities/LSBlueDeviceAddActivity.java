package com.foxchen.ekengmonitor.health.activities;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.foxchen.ekengmonitor.R;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cnnet.otc.health.managers.RequestManager;
import com.cnnet.otc.health.util.SHA1Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LSBlueDeviceAddActivity extends Activity {

    private String appid= "x8e0vrtofpopbnj23tsypep16wlpdn0a43qaqtgm";
    private String appsecrect= "csfxmp991g2zeklogosboeyg976proaavx10bxlq";
    private String timestamp ;
    private String nonce ="";
    private String checksum ;
    private EditText snEditText;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lsblue_device_add);

        mContext=this;
        timestamp = String.valueOf(new Date().getTime());

        Random random = new Random();

        for(int i=0;i<8;i++){
            nonce += String.valueOf(random.nextInt(10)) ;
        }

        Log.d("debug","Rand = "+nonce);
        snEditText= (EditText) findViewById(R.id.snInputEditText);
        checksum=appid+appsecrect+timestamp+nonce;

        Button btn = (Button) findViewById(R.id.snAddDeviceButton);
        btn.setOnClickListener(new  Button.OnClickListener(){//创建监听
            @Override
            public void onClick(View v) {


//                String sn = snEditText.getText().toString();
                String sn = "0301570101062540";
                if(sn.length() == 0){
                    Toast.makeText(mContext,"请输入序列号",Toast.LENGTH_SHORT).show();    //弹出一个自动消失的提示框
                    return;
                }
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
                                        //这里需要将dataJson数据计入数据库
                                        String mac = dataJson.getString("mac");
                                        Log.d("getmacinfo:", mac);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    Log.e("getLSDeviceInfo:", String.valueOf(volleyError));
                                }
                            },parmMap,appid,timestamp,nonce.toString(), SHA1Util.getSHA(checksum));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }


//                Intent intent= new Intent(LSBlueDeviceAddActivity.this,LSBlueDeviceAddActivity.class);
//                startActivity(intent);
            }

        });


    }

}
