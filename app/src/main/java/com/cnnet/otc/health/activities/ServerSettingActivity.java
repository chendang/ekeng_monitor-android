package com.cnnet.otc.health.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.comm.BaseActivity;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.util.DialogUtil;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.util.ToastUtil;
import com.cnnet.otc.health.util.ValidatorUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by SZ512 on 2016/2/24.
 */
public class ServerSettingActivity extends BaseActivity implements View.OnClickListener{

    @Bind(R.id.server_setting_back_btn)
    protected ImageView ivBackBtn;
    @Bind(R.id.et_server_url)
    protected EditText etServerUrl;
    @Bind(R.id.btn_server_setting)
    protected Button btnServerSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_setting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        ivBackBtn.setOnClickListener(this);
        btnServerSetting.setOnClickListener(this);
        etServerUrl.setText(SysApp.getSpManager().getServerUrl());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.server_setting_back_btn:
                finish();
                break;
            case R.id.btn_server_setting:
                final String serverUrl = etServerUrl.getText().toString();
                if(StringUtil.isNotEmpty(serverUrl)) {
                    if(!serverUrl.endsWith("/")) {
                        if (ValidatorUtil.isUrl(serverUrl)) {
                            DialogUtil.Confirm(this, R.string.dialog_alert_title, R.string.dialog_server_setting,
                                    R.string.confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SysApp.getSpManager().setServerUrl(serverUrl);
                                            finish();
                                        }
                                    }, R.string.cancel, null);
                        } else {
                            ToastUtil.TextToast(this, R.string.url_format_is_incorrect, 2000);
                            btnServerSetting.requestFocus();
                        }
                    } else {
                        ToastUtil.TextToast(this, R.string.url_is_not_end_with_slash, 2000);
                        btnServerSetting.requestFocus();
                    }
                } else {
                    ToastUtil.TextToast(this, R.string.url_is_not_null, 2000);
                    btnServerSetting.requestFocus();
                }

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
