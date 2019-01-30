package com.cnnet.otc.health.comm;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Environment;
import android.util.DisplayMetrics;

import com.cnnet.otc.health.bean.MyBlueToothDevice;
import com.cnnet.otc.health.db.MyDBManager;
import com.cnnet.otc.health.interfaces.IUser;
import com.cnnet.otc.health.managers.RequestManager;
import com.cnnet.otc.health.managers.SpManager;
import com.facebook.stetho.Stetho;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.dcloud.application.DCloudApplication;

public class SysApp extends DCloudApplication {

	public static int SCREEN_WIDTH;   //屏幕宽度
	public static int SCREEN_HEIGHT;  //屏幕高度
	public static float SCREEN_DENSITY;  

	public static final boolean isDebug = false;   //请在正式发布时，将该值改为false

	public static int LOGIN_STATE = CommConst.FLAG_USER_STATUS_OFF_LINE;  //离线中
	public static boolean NEED_REFRESH_MEMBER_LIST=false;

	public static CheckType check_type = CheckType.NONE;//设备类型
	public static BrandType brand_type = BrandType.NONE;//设备类型
	public static MyBlueToothDevice btDevice = null;  //当前连接蓝牙信息

	private static ExecutorService LIMITED_TASK_EXECUTOR;  //线程池数量和对象
	private static Context ctx;

	private static IUser accountBean;

	private static SpManager spManager = null;
	private static MyDBManager myDBManager = null;

	public static String LOCAL_ROOT_FLODER = "";  //根目录
	public static String LOCAL_HEAD_FLODER = "";  //头像目录
	private static String mUniqueKey; //用户会员号

	public static String getmUniqueKey() {
		return mUniqueKey;
	}

	public static void setmUniqueKey(String mUniqueKey) {
		SysApp.mUniqueKey = mUniqueKey;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		accountBean = null;
		ctx = this;
		appInit();
		RequestManager.getInstance(this);
		myDBManager = MyDBManager.getInstance(this);
		getSpManager();
		if(isDebug){
			Stetho.initialize(
					Stetho.newInitializerBuilder(ctx)
							.enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
							.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(ctx))
							.build());
		}
		initImageLoader(this);
	}

	public static MyDBManager getMyDBManager() {
		if(myDBManager == null) {
			myDBManager = MyDBManager.getInstance(ctx);
		}
		return myDBManager;
	}

	/**
	 * 获取SpManager对象
	 * @return
	 */
	public static SpManager getSpManager() {
		if(spManager == null) {
			spManager = SpManager.getInstance(ctx);
		}
		return spManager;
	}

	public static void setLoginUser(IUser user) {
		accountBean = user;
	}

	public static IUser getAccountBean() {
		return accountBean;
	}

	/**
	 * 初始化屏幕高宽
	 * @param mActivity
	 */
	public static void initScreenInfo(Activity mActivity) {
		DisplayMetrics dm = new DisplayMetrics();
		mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		SCREEN_WIDTH = dm.widthPixels;
		SCREEN_HEIGHT = dm.heightPixels;
		SCREEN_DENSITY = dm.density;
	}

	/**
	 * 退出APP调用方法
	 */
	public static void exitApp(){
		if(myDBManager != null) {
			myDBManager.destory();
			myDBManager = null;
		}
		accountBean = null;
		LOGIN_STATE = CommConst.FLAG_USER_STATUS_OFF_LINE;
		ImageLoader.getInstance().destroy();
		Intent intent = new Intent (CommConst.INTENT_ACTION_EXIT_APP);
		ctx.sendBroadcast(intent);
	}

	/**
	 * 获取并初始化线程池
	 * @return
	 */
	public static ExecutorService getExecutorNum() {
		if (LIMITED_TASK_EXECUTOR == null) {
			LIMITED_TASK_EXECUTOR = Executors.newFixedThreadPool(10);
		}
		return LIMITED_TASK_EXECUTOR;
	}

	public static void initImageLoader(Context context) {

		File cacheDir = StorageUtils.getOwnCacheDirectory(context, CommConst.APP_FOLDER_NAME);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration
				.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.threadPoolSize(3)
				.memoryCacheExtraOptions(480, 800)
				.denyCacheImageMultipleSizesInMemory()
				.tasksProcessingOrder(QueueProcessingType.FIFO)
						//.memoryCache(new LruMemoryCache(4 * 1024 * 1024))
				.memoryCache(new WeakMemoryCache())
						//.memoryCacheSize(4 * 1024 * 1024)
						//.writeDebugLogs() // Remove for release app
				.diskCache(new LimitedAgeDiscCache(cacheDir,60*60*24))//自定义缓存路径  ,24小时删除
						// .diskCacheFileCount(200)
						// .diskCacheSize(100 * 1024 * 1024)
				.build();
		ImageLoader.getInstance().init(config);
	}

	/**
	 * 初始化App
	 */
	public static void appInit(){
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			final String SD_PATH = Environment.getExternalStorageDirectory().getPath() + "/";
			SysApp.LOCAL_ROOT_FLODER =  SD_PATH + CommConst.APP_FOLDER_NAME;
			SysApp.LOCAL_HEAD_FLODER = SD_PATH + CommConst.APP_HEAND_NAME;
		} else {

			SysApp.LOCAL_ROOT_FLODER = Environment.getDataDirectory().getPath()
					+ "/" + CommConst.APP_FOLDER_NAME;
			SysApp.LOCAL_HEAD_FLODER = Environment.getDataDirectory().getPath()
					+ "/" + CommConst.APP_HEAND_NAME;
		}
	}

	/**
	 * 获取当前Resource
	 * @return
	 */
	public static Resources getResource() {
		return ctx.getResources();
	}

	public static Context getAppContext() {
		return ctx;
	}
}
