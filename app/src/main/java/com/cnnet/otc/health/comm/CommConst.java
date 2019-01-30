package com.cnnet.otc.health.comm;

public class CommConst {
	public final static String APP_FOLDER_NAME = "OTC";
	public final static String APP_HEAND_NAME = APP_FOLDER_NAME + "/head/";

	public static final String SERVER_URL = "http://healthapi.ekeng365.com";//"http://192.168.1.112:8081";
	public static final String OTC_PUSH_SERVER_URL = "http://healthapi.ekeng365.com";//"http://192.168.1.112:8081";
	public static final String SUFIX = "HAVA";
	public static final String UPDATE_CHECK_URL = SERVER_URL + "/action/client/checkSoftwareVersion?softname=Android&product=OTC_COMM"; //更新APP路径
//	public static final String UPDATE_CHECK_URL = SERVER_URL + "/action/client/checkSoftwareVersion?softname=Android&product=OTC_WENHE"; //更新APP路径
	public final static String DOWN_APK_FILENAME = "update.apk";

	public static final int DB_DATA_PAGE = 20;  //数据库中分析数据
	public static final int HEAD_IMG_W = 500;
	public static final int REQUEST_TIMEOUT_TIME = 30000;
   //获取乐心设备信息地址
	public static final String LS_GETDEIVCE_SERVER_URL="http://open.lifesense.com/deviceopenapi_service/device/api";
	/***************************************************************/

	/**
	 * 安全密钥: 加密解密KEY
	 */
	public static final String KEY_DATA = "[%{'M`b!7eo.shF[B8`sQ?u8_";

	/*********************************登录角色******************************/
	public static final short FLAG_USER_ROLE_DOCTOR = 50;  //医生
	public static final short FLAG_USER_ROLE_NURSE = 60; //护士
	public static final short FLAG_USER_ROLE_MEMBER = 70; //会员

	/******************************************登录状态***********************************************************/
	public static final int FLAG_USER_STATE_LOGIN = -1;  //在线登录成功
	public static final int FLAG_USER_STATE_OFFLINE_LOGIN = -2;  //离线登录成功
	public static final int FLAG_USER_STATUS_LOGIN_ING = -3;  //登录中
	public static final int FLAG_USER_STATUS_OFF_LINE = -10;//离线

	/******************************************检查记录状态***********************************************************/
	public static final int STATE_RECORD_WAIT = 0;  //等待检查
	public static final int STATE_RECORD_NEED_UPLOAD = 1;  //需要上传记录
	public static final int STATE_RECORD_SUCCESS = 2;  //提交成功后已经拿到服务器上的recordid

	/**************************************错误异常*****************************************************/
	public static final int ERROR_CODE_LOGIN_USER_PWD_ERR = 1000;  //登录失败，账号密码错误
	public static final int ERROR_CODE_SERVER_FAIL = 1001;  //连接服务器异常

	public static final int ERROR_CODE_SERVER_ADD_RECORD_ERROR = 1002; //提交检查记录失败
	public static final int ERROR_CODE_SERVER_ADD_MEMBER_ERROR = 1003; //提交会员信息失败
	public static final int ERROR_CODE_NETWORK_ERROR = 1004;  //网络异常
	public static final int ERROR_CODE_UPLOAD_FILE_ERROR = 1005; //上传文件失败

	/**************************************检测结果状态*********************************************************/
	public static final String VALUE_GREATER="1";
	public static final String VALUE_SMALLER="2";
	public static final String VALUE_STRANGE="3";

	/******************************************蓝牙现在所处连接状态*********************************************/
	/**
	 * 未连接
	 */
	public static final int FLAG_BT_DISCONNECT = -1;  //未连接，断开
	/**
	 * 正在连接中
	 */
	public static final int FLAG_BT_CONNECTING = 0;
	/**
	 * 连接成功
	 */
	public static final int FLAG_BT_CONNECT_SUCCESS = 1;
	/**
	 * 连接失败
	 */
	public static final int FLAG_BT_CONNECT_FAILED = -2;
	
	/******************************************蓝牙是否连接状态*********************************************/
	/**
	 * 已连接
	 */
	public static final int BT_STATE_CONNECTED = 1;
	/**
	 * 已断开
	 */
	public static final int BT_STATE_DISCONNECTED = 0;

	public static final int FLAG_MEMBERS_OBTAINED = 0;
	public static final int FLAG_MEMBERS_ERR = 1;
	public static final int FLAG_MEMBERS_READY = 2;
	
	/******************************************蓝牙是否连接状态*********************************************/
	public static final int FLAG_CONNECT_EVENT_RESET = 0;  //重置
	
	public static final int FLAG_CONNECT_EVENT_DISPLAY_DATA = 1;  //显示数据
	
	public static final int FLAG_CONNECT_EVENT_SAVE_RECORD_ITEM=2;  //保存检查项

	/*public static final int FLAG_CONNECT_EVENT_EFEFE=3;*/

	public static final int FLAG_CONNECT_EVENT_DISCONNECT_BT = 4;  //关闭蓝牙连接
	
	public static final int FLAG_CONNECT_EVENT_UPDATE = 5;  //修改提示信息
	
	public static final int FLAG_CONNECT_EVENT_UPDATE_STATE = 6;  //修改显示的状态
	
	public static final int FLAG_CONNECT_EVENT_UPDATE_SCAN = 7;  //设置闪烁
	
	public static final int FLAG_CONNECT_EVENT_UPDATE_VALUE = 8;  //修改显示的值

	public static final int FLAG_SAVE_BLUETOOTH_ADDRESS = 9;  //将蓝牙地址写入数据库

	public static final int FLAG_CONNECTED_STOP_TIMER = 10;  //连接成功后，关闭定时器

	public static final int FLAG_DISCONNECT_START_TIMER = 11;  //断开连接后,开启定时器

	public static final int FLAG_CLOSE_BT_DEVICE = 12;  //关闭已经连接的蓝牙设备
	
	/************************************************蓝牙4.0数据*********************************************************/
	public static final int FLAG_BLE_CONNECT_UPDATE_SCAN = 0;  //修改显示闪烁
	
	public static final int FLAG_BLE_CONNECT_UPDATE_STATE = 1;  //修改显示状态
	
	public static final int FLAG_BLE_CONNECT_SCUESS = 2;    //蓝牙连接成功

	public static final int FLAG_REFRESH_REAL_TIME_DATA = 3;    //刷新实时数据

	public static final int FLAG_BLE_DISCONNECTED= 4;    //刷新实时数据

	public static final int FLAG_BLE_EVENT_RESET=5;

	/************************************************蓝牙类型*********************************************************/


	/*************************************自定义广播**********************************/
	public final static String INTENT_ACTION_EXIT_APP = "Intent.action.exit.app"; //exit app Action

	/***************************************intent的Request*************************************/
	public final static int INTENT_REQUEST_DETECT = 1000;  //打开检测activity
	public final static int INTENT_REQUEST_ADD_MEMBER = 1001; //新增会员
	public final static int INTENT_REQUEST_DEVICE_INFO = 1002;  //打开设备详情
	public final static int INTENT_REQUEST_SEARCH_MEMBER = 1003;  //搜索会员


	/***************************************intent传值的KEY*************************************/

	public final static String INTENT_EXTRA_KEY_HAS_REAL = "EXTRA_HAS_REAL_DATA";  //存在实时数据需要显示

	public final static String INTENT_EXTRA_KEY_MEMBER_UNIQUEKEY = "EXTRA_MEMBER_UNIQUEKEY";

	public final static String INTENT_EXTRA_KEY_NATIVE_RECORD_ID = "EXTRA_RECORD_ID";

	public final static String INTENT_EXTRA_KEY_IS_DETECTED = "isDetected";//采集成功

	public final static String INTENT_EXTRA_KEY_NATIVE_HEAD_PATH = "nativeHeadPath";  //本地头像

	public final static String INTENT_EXTRA_KEY_CLOUD_HEAD_PATH = "cloudHeadPath";  //上传返回路劲

	public final static String INTENT_EXTRA_KEY_DEVICE_TYPE_NAME = "DEIVCE_TYPE_NAME";   //设备类型名称

	public final static String INTENT_EXTRA_KEY_DEVICE_TYPE = "DEIVCE_TYPE";   //设备类型

	public final static String INTENT_EXTRA_KEY_DEVICE_IS_DELETED = "isDeviceDeleted";//设备删除成功

	/***************************************服务器错误码*************************************/

	public static int SUCCESS=0;
	public static int ERR_ADDMEMBER_DUPLICATE_NICKNAME=-3;
	public static int ERR_ADDMEMBER_ILLIGAL_CONTENT=-4;
	public static int ERR_ADDMEMBER_DUPLICATE_NAME=-2;
	public static int ERR_ADDMEMBER_CANNOT_VERIFY=-6;
	public static int ERR_ADDMEMBER_OTHER=-1;

	public static int ERR_LOGIN_MULTIPLE_USERS_WITH_SAME_PHONE=-2;

}
