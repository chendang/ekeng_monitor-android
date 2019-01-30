package com.cnnet.otc.health.activities;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnnet.otc.health.MainActivity;
import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.comm.BaseActivity;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.events.LoginEvent;
import com.cnnet.otc.health.tasks.LoginRequest;
import com.cnnet.otc.health.util.DialogUtil;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.util.ToastUtil;

import java.io.File;
import java.io.InputStream;

import de.greenrobot.event.EventBus;

/**
 * Created by SZ512 on 2016/1/7.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ImageView btnBack;
    private Button btnForgetPwd;
    private Button btnLogin;
    private EditText etPassWord;
    private EditText etUserName;

    private LinearLayout userLayout;
    private static boolean isChangeAccount = false;

    private Context mContext;

   void printGID()
   {
    try {
            java.lang.Process process=Runtime.getRuntime().exec("id");
            InputStream input=process.getInputStream();
            byte[] bytes =new byte[1204];
            int len;
            while ((len=(input.read(bytes)))>0)
            {
                System.out.print(new String(bytes,0,len));
            }
            input.close();
    }
    catch(Exception e)
    {
        System.out.println(e.getLocalizedMessage());
    }
   }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        userLayout = (LinearLayout) findViewById(R.id.linearLayout1);
        TextView v=(TextView) findViewById(R.id.login_title);
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder normalDialog =
                        new AlertDialog.Builder(LoginActivity.this);
                normalDialog.setTitle("重建数据库");
                normalDialog.setMessage("点击【重建数据库】按钮将重建数据库，本地保存的数据将丢失,程序将关闭。");
                normalDialog.setPositiveButton("重建数据库",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which)
                    {
                        File oldfile = new File("/data/data/com.cnnet.otc.health/databases/otc.db");
                        if(oldfile.exists())
                        {
                            oldfile.delete();
                            System.exit(0);
                        }
                    }
                });
                normalDialog.setNegativeButton("取消",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which)
                    {
                        dialog.cancel();
                    }
                });
                normalDialog.show();
                return true;
            }
        });
        printGID();
        init();
    }

    private void init() {

        etUserName = (EditText) findViewById(R.id.login_user);
        etPassWord = (EditText) findViewById(R.id.login_password);

        Bundle bun = getIntent().getExtras();
        if (bun != null) {
            if (bun.getBoolean("clearPass")) {
                etPassWord.setText("");
            }
        }

        findViewById(R.id.login_login_btn).setOnClickListener(this);

        findViewById(R.id.forget_passwd).setOnClickListener(this);

        findViewById(R.id.login_reback_btn).setOnClickListener(this);

        findViewById(R.id.login_setting).setOnClickListener(this);
    }


    @SuppressLint("ShowToast")
    private boolean isNameOrPsw(String user, String psw) {

        if (null == user || user.equals("")) {
            ToastUtil.TextToast(this, getString(R.string.usernameNull), 2000);
            //	etUserName.setError(getString(R.string.usernameNull));
            etUserName.requestFocus();
            etUserName.setCursorVisible(true);
            return false;
        } else if (!StringUtil.isTelephone(user)) {
            ToastUtil.TextToast(this, getString(R.string.usernameErr), 2000);
            //etUserName.setError(getString(R.string.usernameErr));
            return false;
        } else if (null == psw || psw.equals("")) {
            ToastUtil.TextToast(this, getString(R.string.pwdNull), 2000);
            etPassWord.requestFocus();
            etPassWord.setCursorVisible(true);
            //etPassWord.setError(getString(R.string.pwdNull));
            return false;
        } else if (psw.length() > 20 || psw.length() <= 6) {
            ToastUtil.TextToast(this, getString(R.string.pwdErr), 2000);
            etPassWord.setText("");
            etPassWord.requestFocus();
            etPassWord.setCursorVisible(true);
            //	etPassWord.setError(getString(R.string.pwdErr));
            return false;
        }

        return true;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(mContext);
        super.onDestroy();

    }

    /**
     * 登录回调消息
     * @param loginEvent
     */
    public void onEventMainThread(LoginEvent loginEvent) {
        DialogUtil.cancelDialog();
        int loginResult = loginEvent.getLoginEvent();
        EventBus.getDefault().unregister(mContext);
        if (loginResult == CommConst.FLAG_USER_STATE_LOGIN
                || loginResult == CommConst.FLAG_USER_STATE_OFFLINE_LOGIN) {
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            this.finish();
            if (isChangeAccount) {
                this.finish();
            } else {
                //SysApp.exitApp(true);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_login_btn:
                String userName = etUserName.getText().toString();
                String passWord = etPassWord.getText().toString();
                if (isNameOrPsw(userName, passWord)) {
                    DialogUtil.loadProgress(mContext, mContext.getString(R.string.logining));
                    EventBus.getDefault().register(mContext);
                    LoginRequest.doLogin(mContext, userName, passWord);
                }
                break;
            case R.id.forget_passwd:
                Intent intent = new Intent(LoginActivity.this, LoginForgetPassActivity.class);
                intent.putExtra("userName", etUserName.getText().toString());
                startActivity(intent);
                break;
            case R.id.login_reback_btn:
                DialogUtil.Confirm(this, R.string.dialog_alert_title, R.string.dialog_exit_login,
                        R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SysApp.exitApp();
                                finish();
                            }
                        }, R.string.cancel, null);

                break;
            case R.id.login_setting:
                Intent serverIntent = new Intent(this, ServerSettingActivity.class);
                startActivity(serverIntent);
                break;
        }
    }
}