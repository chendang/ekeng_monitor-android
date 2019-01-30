package com.cnnet.otc.health;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnnet.otc.health.bean.Member;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.events.MembersObtainedEvent;
import com.cnnet.otc.health.fragments.DeviceFragment;
import com.cnnet.otc.health.fragments.HomeFragment;
import com.cnnet.otc.health.fragments.MemberFragment;
import com.cnnet.otc.health.interfaces.SubmitServerListener;
import com.cnnet.otc.health.managers.MemberManager;
import com.cnnet.otc.health.managers.UpdateManager;
import com.cnnet.otc.health.services.BluetoothService;
import com.cnnet.otc.health.tasks.UploadAllNewInfoTask;
import com.cnnet.otc.health.util.AppCheckUtil;
import com.cnnet.otc.health.util.DialogUtil;
import com.cnnet.otc.health.util.NetUtil;
import com.cnnet.otc.health.util.ToastUtil;
import com.foxchen.ekengmonitor.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";

    private List<Fragment> fragments = new ArrayList<Fragment>();
    private int currentTab = 0;
    private int currentFrag = 0;
    @Bind(R.id.home)
    ImageView home;
    @Bind(R.id.member)
    ImageView member;
    @Bind(R.id.my_device)
    ImageView myDevice;

    private boolean isAsynced = false;  //第一次进入时开始同步

    private TextView textSelectNum;
    private TextView btnSelectAll;
    private boolean hadSelectAll = false;
    private LinearLayout topBar;
    private LinearLayout bottomBar;
    private Animation topAnimIn, topAnimOut, bottomAnimIn, bottomAnimOut;
    /**
     * 软件更新对象
     */
    private UpdateManager updateManager;

    /**
     * 用来显示下载的进度条组件
     */

    private ProgressDialog updateProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MemberManager.getInstance().fetchMemberData(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        updateManager = new UpdateManager(this, appUpdateCb);
        updateManager.checkUpdate();
    }

    private void init() {
        EventBus.getDefault().register(this);
        findViewById(R.id.fl_home).setOnClickListener(this);
        findViewById(R.id.fl_member).setOnClickListener(this);
        findViewById(R.id.fl_my_device).setOnClickListener(this);
        textSelectNum = (TextView) findViewById(R.id.select_num);
        topBar = (LinearLayout) findViewById(R.id.ll_opeate_bar_top);
        bottomBar = (LinearLayout) findViewById(R.id.ll_opeate_bar_bottom);
        findViewById(R.id.fl_add_insp).setOnClickListener(this);

        if(SysApp.LOGIN_STATE!=CommConst.FLAG_USER_STATE_OFFLINE_LOGIN)
        {
            findViewById(R.id.fl_delele_member).setVisibility(View.GONE);
        }
        else {
            findViewById(R.id.fl_delele_member).setOnClickListener(this);
        }
        btnSelectAll = (TextView)findViewById(R.id.select_all);
        btnSelectAll.setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        initAnimation();
        initFragment();
    }

    private void initAnimation() {
        topAnimIn = AnimationUtils.loadAnimation(this, R.anim.bar_top_in);
        topAnimOut = AnimationUtils.loadAnimation(this,
                R.anim.bar_top_out);
        bottomAnimIn = AnimationUtils.loadAnimation(this,
                R.anim.bar_bottom_in);
        bottomAnimOut = AnimationUtils.loadAnimation(this,
                R.anim.bar_bottom_out);
    }

    private void initFragment() {
        fragments.add(new HomeFragment());
        fragments.add(new MemberFragment());
        fragments.add(new DeviceFragment());
        // 默认显示第一页
        changeFramgment(R.id.fl_home);
    }

    /**
     * 修改Framgment
     */
    public void changeFramgment(int id) {
        if(id != currentTab) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment current = fragments.get(currentFrag);
            if(current.isAdded()) {
                Log.d(TAG, "hide ------- ");
                current.onPause(); // 暂停当前tab
                ft.hide(current);
            }
            currentTab = id;
            switch (id) {
            case R.id.fl_home:
                home.setSelected(true);
                myDevice.setSelected(false);
                member.setSelected(false);
                currentFrag = 0;
                break;
            case R.id.fl_member:
                home.setSelected(false);
                myDevice.setSelected(false);
                member.setSelected(true);
                currentFrag = 1;
                break;
            case R.id.fl_my_device:
                home.setSelected(false);
                myDevice.setSelected(true);
                member.setSelected(false);
                currentFrag = 2;
                break;
            }

            Fragment fragment = fragments.get(currentFrag);
            if(!fragment.isAdded()) {
                ft.add(R.id.main_fragment_content, fragments.get(currentFrag));
            } else {
                Log.d(TAG, "show ------- ");
                fragment.onResume(); // 启动目标tab的onResume()
            }
            ft.show(fragment);
            ft.commit();
        }
    }

    public void onEventMainThread(MembersObtainedEvent event)
    {
        if(currentFrag==1) {
            MemberFragment fragment = (MemberFragment) fragments.get(currentFrag);
            if(event.getResultCode()==CommConst.FLAG_MEMBERS_OBTAINED) {

                fragment.setMoreData(event.getMembers());
            }
            else
            {
                fragment.change_downloading_status(false);
            }
        }

    }

    public void showHome() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = fragments.get(0);
        if(!fragment.isAdded()) {
            ft.add(R.id.main_fragment_content, fragments.get(currentFrag));
        } else {
            Log.d(TAG, "show ------- ");
            fragment.onResume(); // 启动目标tab的onResume()
        }
        ft.show(fragment);
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        //Intent intent = new Intent(this, DetectBle3Activity.class);
        switch (v.getId()) {
           case R.id.fl_home:
            case R.id.fl_member:
            case R.id.fl_my_device:
                changeFramgment(v.getId());
                break;
            case R.id.fl_add_insp:
            case R.id.fl_delele_member:
                //hide();
                //break;
            case R.id.select_all:
            case R.id.cancel:
                ((MemberFragment)fragments.get(1)).onClick(v.getId(), hadSelectAll);
                break;
            default:
                break;
        }
        //startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.TIPS));
            builder.setPositiveButton(getString(R.string.CONFIRM),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                BluetoothService.disConnect();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            BluetoothAdapter mBtAdapter = BluetoothAdapter
                                    .getDefaultAdapter();
                            mBtAdapter.disable();
                            SysApp.exitApp();
                            System.exit(0);
                        }
                    });
            builder.setNegativeButton(getString(R.string.CANCEL), null);
            builder.setMessage(getString(R.string.IS_EXIT));
            builder.show();
        }

        return super.onKeyDown(keyCode, event);

    }

    /**
     * 当点击多选框后出发方法
     * @param num
     * @param isAll
     */
    public void onClickCheckBox(int num, boolean isAll) {
        if (num > 0 && bottomBar.getVisibility() != View.VISIBLE) {
            bottomBar.setVisibility(View.VISIBLE);
            bottomBar.startAnimation(bottomAnimIn);
            topBar.setVisibility(View.VISIBLE);
            topBar.startAnimation(topAnimIn);
        } else if (num == 0 && bottomBar.getVisibility() == View.VISIBLE) {
            hide();
        }
        hadSelectAll = isAll;
        String text = String.format(getString(R.string.chooseNum), num);
        textSelectNum.setText(text);
        btnSelectAll.setText(isAll ? R.string.cancelchooseAll : R.string.chooseAll);
    }

    /**
     * 隐藏工具栏
     */
    private void hide() {
        bottomBar.setVisibility(View.INVISIBLE);
        topBar.setVisibility(View.INVISIBLE);
        topBar.startAnimation(topAnimOut);
        bottomBar.startAnimation(bottomAnimOut);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isAsynced) {
            isAsynced = true;
            startSyncInfo();
        }
    }

    /**
     * 启动时，就开始同步数据中
     */
    private void startSyncInfo() {
        if(SysApp.getAccountBean() != null && SysApp.getAccountBean().getRole() < CommConst.FLAG_USER_ROLE_MEMBER) {
            if(NetUtil.checkNetState(this)) {
                DialogUtil.loadProgressUnClose(this, getString(R.string.syncing));
                UploadAllNewInfoTask.SynchronizationInfo(this, SysApp.getAccountBean().getUniqueKey(), new SubmitServerListener() {
                    @Override
                    public void onResult(int result) {
                        DialogUtil.cancelDialog();
                        if (result == 0) {
                            if(UploadAllNewInfoTask.isSync) {
                                ToastUtil.TextToast(getBaseContext(), R.string.sync_success, 2000);
                            }
                        } else {
                            AppCheckUtil.toastErrMsgByConnectResultCode(getBaseContext(), result);
                        }
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, 3);
                    }
                });
            }
        }
    }

    /**
     * 下载进度对话框的取消事件监听器
     */
    private DialogInterface.OnCancelListener dialogCancelListener = new DialogInterface.OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
            updateManager.cancelDownload();
        }
    };
    /**
     * 下载更新APK回调接口
     */
    UpdateManager.UpdateCallback appUpdateCb = new UpdateManager.UpdateCallback() {

        @Override
        public void checkUpdateCompleted(Boolean hasUpdate,
                                         CharSequence updateInfo, String changelog) {
            if (hasUpdate) {
                DialogUtil.Confirm(
                        MainActivity.this,
                        getText(R.string.dialog_update_title),
                        getText(R.string.dialog_update_msg).toString()
                                + updateInfo
                                + getText(R.string.dialog_update_msg2)
                                .toString(),
                        getText(R.string.dialog_update_btnupdate),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                updateProgressDialog = new ProgressDialog(
                                        MainActivity.this);
                                // 设置对话框不可以通过点击其他区域而退出
                                updateProgressDialog.setCancelable(false);
                                updateProgressDialog
                                        .setMessage(getText(R.string.dialog_downloading_msg));
                                updateProgressDialog.setIndeterminate(false);
                                updateProgressDialog
                                        .setOnCancelListener(dialogCancelListener);
                                updateProgressDialog
                                        .setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                updateProgressDialog.setMax(100);
                                updateProgressDialog.setProgress(0);
                                updateProgressDialog.show();

                                updateManager.downloadPackage();
                            }
                        }, getText(R.string.dialog_update_btnnext), null,null);
            }

        }

        @Override
        public void downloadCanceled() {
            // 取消下载的回调函数实现，这里是空的
        }

        @Override
        public void downloadCompleted(Boolean sucess, CharSequence errorMsg) {
            if (updateProgressDialog != null
                    && updateProgressDialog.isShowing()) {
                updateProgressDialog.dismiss();
            }
            if (sucess) {
                // 下载成功更新
                updateManager.update();
            } else {
                // 下载失败，弹出确认对话框
                DialogUtil.Confirm(MainActivity.this,
                        R.string.dialog_error_title,
                        R.string.dialog_downfailed_msg,
                        R.string.dialog_downfailed_btndown,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                updateProgressDialog.setProgress(0);
                                updateProgressDialog.show();
                                updateManager.downloadPackage();
                            }
                        }, R.string.dialog_downfailed_btnnext, null);
            }
        }

        @Override
        public void downloadProgressChanged(int progress) {
            if (updateProgressDialog != null
                    && updateProgressDialog.isShowing()) {
                updateProgressDialog.setProgress(progress);
            }
        }

        @Override
        public void netError() {
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if(SysApp.getMyDBManager() != null) {
            SysApp.getMyDBManager().destory();
        }
    }
}
