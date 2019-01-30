package com.cnnet.otc.health.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.comm.BaseActivity;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.managers.JsonManager;
import com.cnnet.otc.health.util.DialogUtil;
import com.cnnet.otc.health.util.ImageUtils;
import com.cnnet.otc.health.util.NetUtil;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.util.ToastUtil;
import com.cnnet.otc.health.views.clip.ClipImageLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SZ512 on 2016/1/19.
 */
public class ClipActivity extends BaseActivity {
    private ClipImageLayout mClipImageLayout;
    private String path;
    private final String TAG = "ClipActivity";
    private String cloudHeadPath = null;
    private String saveHeadPath = null;
    private String fileImageName = null;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipimage);
        path=getIntent().getStringExtra("path");
        path = ImageUtils.getPath(getApplicationContext(), Uri.parse(path));
        fileImageName = System.currentTimeMillis() + ".png";
        if(TextUtils.isEmpty(path)||!(new File(path).exists())){
            ToastUtil.TextToast(this, getString(R.string.loadfail), Toast.LENGTH_SHORT);
            return;
        }

        Bitmap bitmap=ImageUtils.convertToBitmap(path, 600,600);
        if(bitmap==null){
            ToastUtil.TextToast(this, getString(R.string.loadfail), Toast.LENGTH_SHORT);
            return;
        }
        mClipImageLayout = (ClipImageLayout) findViewById(R.id.id_clipImageLayout);
        mClipImageLayout.setImgWH(100, 100);
        mClipImageLayout.setBitmap(bitmap);
        findViewById(R.id.btn_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.id_action_clip).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                DialogUtil.loadProgress(ClipActivity.this, "");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = mClipImageLayout.clip();
                        int result = uploadHeadBitmap(bitmap);
                        if (result != -1) {
                            String path = getNowHeadPath();
                            if (ImageUtils.savePhoto2SDCard(bitmap, path)) {
                                saveHeadPath = path;
                                if(result == 1) {
                                    mHandler.sendEmptyMessage(1);
                                } else if (result == 0) {
                                    mHandler.sendEmptyMessage(3);
                                }
                                clipSuccessBack(path);
                                return;
                            }
                        }
                        mHandler.sendEmptyMessage(2);
                    }
                }).start();
            }
        });
    }

    private void clipSuccessBack(String path) {
        Intent intent = new Intent();
        intent.putExtra(CommConst.INTENT_EXTRA_KEY_NATIVE_HEAD_PATH, path);
        if(StringUtil.isNotEmpty(cloudHeadPath)) {
            intent.putExtra(CommConst.INTENT_EXTRA_KEY_CLOUD_HEAD_PATH, cloudHeadPath);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    private String getNowHeadPath() {
        if(saveHeadPath == null) {
            //return Environment.getExternalStorageDirectory() + "/" + CommConst.APP_HEAND_NAME + fileImageName;
            return SysApp.LOCAL_HEAD_FLODER + fileImageName;
        }
        return saveHeadPath;
    }


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            DialogUtil.cancelDialog();
            switch(msg.what){
                case 1:
                    break;
                case 2:
                    ToastUtil.TextToast(ClipActivity.this, R.string.operFail, 1000);
                    break;
                case 3:
                    ToastUtil.TextToast(ClipActivity.this, R.string.upload_image_fail, 1000);
                    break;
            }
        }
    };

    /**
     * 上传图片
     * @param iconBitmap
     * @return
     * @throws NullPointerException
     */
    private int uploadHeadBitmap(Bitmap iconBitmap)
            throws NullPointerException {//
        ByteArrayOutputStream baos;
        baos = new ByteArrayOutputStream();
        iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        HttpURLConnection httpURLConnection = null;
        try {
            if(NetUtil.checkNetState(this)) {
                String end = "\r\n";
                String twoHyphens = "--";
                String boundary = "******";
                String httpParams = "/action/client/uploadFile?userUniqueKey=" + SysApp.getAccountBean().getUniqueKey();
                String request = SysApp.getSpManager().getServerUrl() +  httpParams;
                //	request = StringUtil.getEncodeStr(request);
                Log.d(TAG, "url : " + request);
                URL url = new URL(request);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(new Integer(CommConst.REQUEST_TIMEOUT_TIME));

                httpURLConnection.setReadTimeout(new Integer(CommConst.REQUEST_TIMEOUT_TIME));

                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setUseCaches(false);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Cookie", "userid=null");
                // httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("Charset", "UTF-8");
                httpURLConnection.setRequestProperty("Content-Type",
                        "multipart/form-data; boundary=" + boundary);
                //httpURLConnection.setRequestProperty("name","head");
                httpURLConnection.connect();
                DataOutputStream dos = new DataOutputStream(
                        httpURLConnection.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + end);
                dos.writeBytes("Content-Disposition: form-data; name=\"upload\"; filename=\"" + fileImageName + "\"" + end);

                dos.writeBytes(end);

                dos.write(data, 0, data.length);
                dos.writeBytes(end);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
                dos.flush();
                dos.close();

                InputStream is = httpURLConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String str = null;
                if ((str = br.readLine()) != null) {
                    Log.v(TAG, "str  === " + str);
                    JSONObject json = new JSONObject(str);

                    if (JsonManager.getCode(json) == 0) {
                        cloudHeadPath = JsonManager.getUploadPath(json);
                        return 1;
                    }
                }

                is.close();
                httpURLConnection.disconnect();
                return 1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            Log.e(TAG, "e: " + e);
            return -1;
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

}

