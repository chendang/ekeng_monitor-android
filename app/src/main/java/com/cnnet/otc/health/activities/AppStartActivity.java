package com.cnnet.otc.health.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.MainActivity;

import com.cnnet.otc.health.comm.BaseActivity;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.events.LoginEvent;
import com.cnnet.otc.health.interfaces.IUser;
import com.cnnet.otc.health.managers.SpManager;
import com.cnnet.otc.health.tasks.LoginRequest;

import de.greenrobot.event.EventBus;

/**
 * 开场版权声明，之后判断是否是第一次运行。 如果是第一次，则就开始进行 功能演示； 如果不是则直接进入登陆界面
 * 
 * @author Administrator
 */
public class AppStartActivity extends BaseActivity {
	private final String T = "AppStartActivity";
	
	/*
	 * 第一次安装，创建程序目录要求在程序目录的根目录下要有各个应用的目录比如相册目录，通讯录目录
	 */
	// 初始化界面的延迟时间
	private int intDeplayTimeNum = 2000;
	private TextView mTv_load;
	private Context mContext;

	private SpManager spManager = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_start_activity);
		mContext = this;
		EventBus.getDefault().register(this);
		SysApp.initScreenInfo(this);
		spManager = SysApp.getSpManager();
		mTv_load = (TextView)findViewById(R.id.loading);
		/*
		 * 判断是否第一次安装 如果是第一次安装 界面上应该有产品 介绍 通过偏好设置来实现
		 */
		if(spManager.isFirstRun()){
			spManager.setIsNotFirstRun();
			// 是第一次安装，跳转至产品介绍页面
			delayJumpToLogin();
		} else {
			// 不是第一次安装，持久化登录
			IUser user = SysApp.getSpManager().getLoginInfo();
			if(user != null){
				mTv_load.setVisibility(View.VISIBLE);
				LoginRequest.backgroundLogin(mContext, user);
				return ;
			}
			intDeplayTimeNum = 1000;
			// 不是第一次安装，没有登录 直接进入登陆界面
			delayJumpToLogin();
		}

	}

	/**
	 * 延迟跳转到登录界面
	 */
	private void delayJumpToLogin() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(AppStartActivity.this,
						LoginActivity.class);
				startActivity(intent);
				AppStartActivity.this.finish();

			}
		}, intDeplayTimeNum);
	}

	@Override
	
	public void onDestroy(){
		//LoginCallback.unregisterLoginListener(mContext);
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		EventBus.getDefault().unregister(this);
		SysApp.exitApp();
		finish();
	}

	// 判断是是否插入SD卡
	public static boolean hasSDcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 登录回调消息
	 * @param loginEvent
	 */
	public void onEventMainThread(LoginEvent loginEvent) {
		int loginResult = loginEvent.getLoginEvent();
		EventBus.getDefault().unregister(mContext);
		if (loginResult == CommConst.FLAG_USER_STATE_LOGIN || loginResult == CommConst.FLAG_USER_STATE_OFFLINE_LOGIN) {
			/*Intent mainIntent = new Intent(AppStartActivity.this,MainActivity.class);
			startActivity(mainIntent);
			finish();
		} else if(loginResult == CommConst.FLAG_USER_STATE_OFFLINE_LOGIN) {*/
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(AppStartActivity.this,
							MainActivity.class);
					startActivity(intent);
					AppStartActivity.this.finish();

				}
			}, intDeplayTimeNum);
		} else {
			delayJumpToLogin();
		}
	}
	

}