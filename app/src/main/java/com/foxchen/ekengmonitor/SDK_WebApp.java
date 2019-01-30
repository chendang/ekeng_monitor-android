package com.foxchen.ekengmonitor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.interfaces.SubmitServerListener;
import com.cnnet.otc.health.tasks.UploadAllNewInfoTask;
import com.cnnet.otc.health.util.AppCheckUtil;
import com.cnnet.otc.health.util.NetUtil;
import com.cnnet.otc.health.util.ToastUtil;
import com.foxchen.ekengmonitor.R;
import com.idescout.sql.SqlScoutServer;

import de.greenrobot.event.EventBus;
import io.dcloud.EntryProxy;
import io.dcloud.RInformation;
import io.dcloud.common.DHInterface.IApp;
import io.dcloud.common.DHInterface.IApp.IAppStatusListener;
import io.dcloud.common.DHInterface.ICore;
import io.dcloud.common.DHInterface.ICore.ICoreStatusListener;
import io.dcloud.common.DHInterface.IOnCreateSplashView;
import io.dcloud.common.DHInterface.ISysEventListener.SysEventType;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.IWebviewStateListener;
import io.dcloud.feature.internal.sdk.SDK;
import static com.android.volley.VolleyLog.TAG;
/**
 * 本demo为以WebApp方式集成5+ sdk， 
 *
 */
public class SDK_WebApp extends Activity {

	boolean doHardAcc = true;
	EntryProxy mEntryProxy = null;
	private static Context ctx;
	private boolean stopThread=false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx=this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (mEntryProxy == null) {
			FrameLayout f = new FrameLayout(this);
			// 创建5+内核运行事件监听
			WebappModeListener wm = new WebappModeListener(this, f);
			// 初始化5+内核
			mEntryProxy = EntryProxy.init(this, wm);
			// 启动5+内核
			mEntryProxy.onCreate(this, savedInstanceState, SDK.IntegratedMode.WEBAPP, null);
			setContentView(f);
		}
		SqlScoutServer.create(this, getPackageName());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return mEntryProxy.onActivityExecute(this, SysEventType.onCreateOptionMenu, menu);
	}

	@Override
	public void onPause() {
		super.onPause();
		mEntryProxy.onPause(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		mEntryProxy.onResume(this);
		//启动同步数据程序
		startSyncInfo();
	}

	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getFlags() != 0x10600000) {// 非点击icon调用activity时才调用newintent事件
			mEntryProxy.onNewIntent(this, intent);
		}
	}
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		EventBus.getDefault().unregister(this);
		SysApp.exitApp();
		finish();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mEntryProxy.onStop(this);

		EventBus.getDefault().unregister(this);


	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyDown, new Object[] { keyCode, event });
		return _ret ? _ret : super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyUp, new Object[] { keyCode, event });
		return _ret ? _ret : super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyLongPress, new Object[] { keyCode, event });
		return _ret ? _ret : super.onKeyLongPress(keyCode, event);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		try {
			int temp = this.getResources().getConfiguration().orientation;
			if (mEntryProxy != null) {
				mEntryProxy.onConfigurationChanged(this, temp);
			}
			super.onConfigurationChanged(newConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mEntryProxy.onActivityExecute(this, SysEventType.onActivityResult, new Object[] { requestCode, resultCode, data });
	}


	/**
	 * 启动时，就开始同步数据中
	 */

	private Handler handler = new Handler(){

		public void handleMessage(Message msg)
		{
			// TODO Auto-generated method stub

			Log.e(TAG, Thread.currentThread().getName() + " " +msg.obj);
			setTitle("" +msg.obj);
			if (msg.what == 0) {
				ToastUtil.TextToast(getBaseContext(), R.string.sync_success, 2000);
			}
			if (msg.what == 1) {
				ToastUtil.TextToast(getBaseContext(), R.string.mobile_network_error, 2000);
			}

		}
	};
	private void startSyncInfo() {
//		if(SysApp.getAccountBean() != null && SysApp.getAccountBean().getRole() < CommConst.FLAG_USER_ROLE_MEMBER) {

		handler.post(new Runnable() {
			@Override
			public void run() {

				if(NetUtil.checkNetState(ctx) && !stopThread) {
//					DialogUtil.loadProgressUnClose(ctx, getString(R.string.syncing));
					UploadAllNewInfoTask.SynchronizationOtcInfo(ctx, new SubmitServerListener() {

						@Override
						public void onResult(int result) {
//							DialogUtil.cancelDialog();
							if (result == 0) {
								if(UploadAllNewInfoTask.isSync) {
//									mHandler.sendEmptyMessage(0);

									ToastUtil.TextToast(getBaseContext(), R.string.sync_success, 2000);
									stopThread=true;
								}

							} else {
//								mHandler.sendEmptyMessage(1);
								AppCheckUtil.toastErrMsgByConnectResultCode(getBaseContext(), result);
							}
//							Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//							startActivityForResult(enableIntent, 3);
						}
					});

				}
			}
		});


	}
//	}


}


class WebappModeListener implements ICoreStatusListener, IOnCreateSplashView {
	Activity activity;
	View splashView = null;
	ViewGroup rootView;
	IApp app = null;
	ProgressDialog pd = null;

	public WebappModeListener(Activity activity, ViewGroup rootView) {
		this.activity = activity;
		this.rootView = rootView;
	}

	/**
	 * 5+内核初始化完成时触发
	 * */
	@Override
	public void onCoreInitEnd(ICore coreHandler) {

		// 表示Webapp的路径在 file:///android_asset/apps/HelloH5
//		String appBasePath = "/apps/qbsAPP";
		String appBasePath = "/apps/qbsAPP";

		// 设置启动参数,可在页面中通过plus.runtime.arguments;方法获取到传入的参数
		String args = "{url:'http://www.baidu.com'}";

		// 启动启动独立应用的5+ Webapp

		app = SDK.startWebApp(activity, appBasePath, args, new IWebviewStateListener() {
			// 设置Webview事件监听，可在监监听内获取WebIvew加载内容的进度
			@Override
			public Object onCallBack(int pType, Object pArgs) {
				switch (pType) {
				case IWebviewStateListener.ON_WEBVIEW_READY:
					// WebApp准备加载事件
					// 准备完毕之后添加webview到显示父View中，
					// 设置排版不显示状态，避免显示webview时html内容排版错乱问题
					View view = ((IWebview) pArgs).obtainApp().obtainWebAppRootView().obtainMainView();
					view.setVisibility(View.INVISIBLE);
					rootView.addView(view, 0);
					break;
					case IWebviewStateListener.ON_PAGE_STARTED:
						// 首页面开始加载事件
//					pd = ProgressDialog.show(activity, "加载中", "0/100");
						break;
					case IWebviewStateListener.ON_PROGRESS_CHANGED:
						// WebApp首页面加载进度变化事件
						if (pd != null) {
							pd.setMessage(pArgs + "/100");
						}
						break;
					case IWebviewStateListener.ON_PAGE_FINISHED:
						// WebApp首页面加载完成事件
					if (pd != null) {
						pd.dismiss();
						pd = null;
					}
					// 页面加载完毕，设置显示webview
					// 如果不需要显示spalsh页面将此行代码移动至onCloseSplash事件内
					app.obtainWebAppRootView().obtainMainView().setVisibility(View.VISIBLE);
					break;
				}
				return null;
			}
		}, this);

		app.setIAppStatusListener(new IAppStatusListener() {
			// 设置APP运行事件监听
			@Override
			public boolean onStop() {
				// 应用运行停止时调用
				rootView.removeView(app.obtainWebAppRootView().obtainMainView());
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public String onStoped(boolean b, String s) {
				return null;
			}

			@Override
			public void onStart() {
				// 独立应用启动时触发事件
			}

			@Override
			public void onPause(IApp arg0, IApp arg1) {
				// WebApp暂停运行时触发事件
			}
		});
	}

	@Override
	public void onCoreReady(ICore coreHandler) {
		// 初始化5+ SDK，
		// 5+SDK的其他接口需要在SDK初始化后才能調用
		SDK.initSDK(coreHandler);
		// 设置当前应用可使用的5+ API
		SDK.requestAllFeature();
	}

	@Override
	public boolean onCoreStop() {
		// 当返回false时候回关闭activity
		return false;
	}

	// 在Widget集成时如果不需要显示splash页面可按照如下步骤操作
	// 1 删除onCreateSplash方法内的代码
	// 2 将5+mainView添加到rootview时将页面设置为不可见
	// 3 在onCloseSplash方法中将5+mainView设置为可见
	// 4 修改androidmanifest.xml文件 将SDK_WebApp的主题设置为透明
	// 注意！
	// 如果不显示splash页面会造成用户点击后页面短时间内会没有变化，
	// 可能会给用户造成程序没响应的错觉，
	// 所以开发者需要对5+内核启动到5+应用页面显示之间的这段事件进行处理

	@Override
	public Object onCreateSplash(Context pContextWrapper) {
		splashView = new FrameLayout(activity);
		splashView.setBackgroundResource(RInformation.DRAWABLE_SPLASH);
		rootView.addView(splashView);
		return null;
	}

	@Override
	public void onCloseSplash() {
		rootView.removeView(splashView);
	}
}
