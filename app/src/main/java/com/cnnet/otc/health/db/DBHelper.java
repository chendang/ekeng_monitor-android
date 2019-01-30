package com.cnnet.otc.health.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SZ512 on 2016/1/5.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DBHelper";
    private static DBHelper instance;

    /**
     * 数据的版本
     */
    protected static final String TB_LOGIN_INFO = "tb_login_info";
    protected static final String TB_DATA_VERSION = "tb_data_version";
    protected static final String TB_USERS = "tb_users";
    protected static final String TB_MEMBERS = "tb_members";
    protected static final String TB_MEMBER_RECORD = "tb_member_record";
    protected static final String TB_MEMBER_RECORD_ITEM = "tb_member_record_item";
    protected static final String TB_DEVICE_BLUETOOTHS = "tb_device_bluetooths";

    protected static final String ID = "id";

    protected static final String L_COL_UNIQUEKEY = "uniqueKey"; //主键
    protected static final String L_COL_SUPER_UNIQUEKEY = "superUniqueKey";  //门诊Key
    protected static final String L_COL_LOGIN_ROLE = "role";  //登录角色(50:医生；60: 护士；70: 会员)
    protected static final String L_COL_USERNAME = "username";
    /**
     * 经过DES加密后的密码
     */
    protected static final String L_COL_PASSWORD = "password";
    protected static final String L_COL_NAME = "name";
    protected static final String L_COL_SEX = "sex";//性别：M男，F女
    //protected static final String L_COL_LOGIN_TIME = "login_time";  //登录时间,long类型
    //protected static final String L_COL_LOGIN_STATE = "state";  //是否可以自动登录：1可以，0不可以

    protected static final String V_COL_UNIQUNKEY = "vUniqueKey";   //会员所属门诊的唯一键
    protected static final String V_COL_VERSION_TYPE = "versionType";  //类型：目前只有会员类型（70）
    protected static final String V_COL_VERSION = "version";  //当前会员的版本

    protected static final String U_COL_UNIQUEKEY = "userUniqueKey";   //唯一键，全数据库中表示唯一
    protected static final String U_COL_FA_UNIQUEKEY = "faUniqueKey"; //所属门诊Key
    protected static final String U_COL_USER_ROLE = "userRoleId";  //用户角色（外键，对应TB_ROLE）
    protected static final String U_COL_USERNAME = "userName";   //用户姓名
    protected static final String U_COL_SEX = "sex";  //用户性别：（M男，F女）
    protected static final String U_COL_BRITHDAY = "brithday";  //出生日期
    protected static final String U_COL_JOB_TITLE = "jobTitle";  //职称
    protected static final String U_COL_JOB_POSITION = "jobPosition";   //职位
    protected static final String U_COL_JOB_DEPARTMENT = "jobDepartment";  //科室
    protected static final String U_COL_ADD_BY_UNIQUEKEY = "addByUniqueKey";  //该信息被谁添加（只在本地查询有用）


    protected static final String M_COL_UNIQUEKEY = "mUniqueKey";                 //站唯一ID
    protected static final String M_COL_USER_UNIQUEKY = "mUserUniqueKey";         //所属站级管理员ID
    protected static final String M_COL_HEADPATH = "mHeadPath";         //头像路径
    protected static final String M_COL_NATIVE_HEADPATH = "mNativeHeadPath";         //本地头像路径：（未上传头像时使用）
    protected static final String M_COL_NAME = "mName";  //姓名
    protected static final String M_COL_SEX = "mSex";  //性别：M男，F女
    protected static final String M_COL_IDNUMBER = "mIdNumber";  //身份证号码
    protected static final String M_COL_TEL = "mTel";  //电话
    protected static final String M_COL_PHONE = "mPhone";  //手机号码
    protected static final String M_COL_ADDRESS = "mAddress";  //住址
    protected static final String M_COL_BRITHDAY = "mBrithday";  //出生日期
    protected static final String M_COL_ANAMNESIS = "mAnamnesis";  //既往病史
    protected static final String M_COL_SSN = "mSSN";  //社保卡
    protected static final String M_COL_CREATE_PERSION = "mCreatePersion";  //创建人员
    protected static final String M_COL_CREATE_TIME = "mCreateTime";
    protected static final String M_COL_UPDATE_TIME = "mUpdateTime";
    protected static final String M_COL_VERSION = "version";  //当前版本
    protected static final String M_COL_STATE = "state";  //当前会员状态：0为上传，1已经上传(当为1时，后面两个字段无用)
    protected static final String M_COL_ADD_UNIQUEKEY = "addUniqueKey";  //该会员被谁添加：（当护士或医生账号登陆时时有用）
    protected static final String M_COL_WITH_DOCTOR = "withDoctor";  //该会员所属医生；  //移动端只能属于一人
    protected static final String M_COL_PINYIN = "mPinyin";  //该会员所属医生；  //移动端只能属于一人

    protected static final String R_COL_RECORD_ID = "recordId";  //记录id
    protected static final String R_COL_NATIVE_MEMBER_ID = "memberNativeId";  //本地会员的唯一ID
    protected static final String R_COL_MEMBER_UNIQUEKEY = "mUniqueKey";  //会员的唯一ID
    protected static final String R_COL_RECORD_PERSION = "recordPersion";  //给该会员做检查的医生
    protected static final String R_COL_CREATE_TIME = "createTime";  //创建时间
    protected static final String R_COL_UPDATE_TIME = "updateTime";  //修改时间
    protected static final String R_COL_RECORD_TYPE = "recordType";//0:代表默认数据，PC端采集数据；1：代表上传图片数据
    protected static final String R_COL_STATE = "state";  //当前记录是否提交：0待提交，1确定后可以提交，2已经提交
    protected static final String R_COL_ADD_UNIQUEKEY = "add_uniqueKey";  //当前的记录是那个用户的：是医生或护士时则为其id(id>0)，是会员自己检测时为0

    protected static final String RI_COL_NATIVE_RECORD_ID = "iNativeId";  //在本地的ID
    protected static final String RI_COL_RECORD_ID = "iRecordId";  //
    protected static final String RI_COL_TYPE = "iType";  //检查类型
    protected static final String RI_COL_VALUE1 = "value1"; //检查结果值1
    protected static final String RI_COL_CONCLUSION = "iConclusion";  //说明
    protected static final String RI_COL_DESC1 = "desc1"; //描述1
    protected static final String RI_COL_SOURCE = "source";  //数据来源：1设备 ，2手动输入
    protected static final String RI_COL_DEVICE_TYPE = "deviceType"; //检查设备类型
    protected static final String RI_COL_RECORD_TIME = "recordTime"; //检测时间
    protected static final String RI_COL_TESTCODE="testCode";

    protected static final String B_COL_DEIVCE_NAME = "btName";  //蓝牙名称
    protected static final String B_COL_MAC = "mac";  //蓝牙地址
    protected static final String B_COL_DEVICE_TYPE = "device_type";  //当前设备是什么设备
    protected static final String B_COL_CONNECT_TIME = "connect_time";  //当前设备连接时间

    /*******************VALUE*******************/
    public static final short RI_SOURCE_DEVICE = 1;  //来源于设备
    public static final short RI_SOURCE_MANAUAL = 2;  //来源于手动输入

    public static DBHelper getInstance(Context ctx, String dbName, int dbVersion){
        if (instance == null) {
            instance = new DBHelper(ctx, dbName, dbVersion);
        }
        return instance;
    }

    private DBHelper (Context ctx, String dbName, int dbVersion) {
        this(ctx, dbName, null, dbVersion);
    }

    /**
     *
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    private DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,  int version) {
        super(context, name, factory, version);
    }
    /**
     25          * 这个方法
     26          * 1、在第一次打开数据库的时候才会走
     27          * 2、在清除数据之后再次运行-->打开数据库，这个方法会走
     28          * 3、没有清除数据，不会走这个方法
     29          * 4、数据库升级的时候这个方法不会走
     30          */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        clearDB(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }

    private void createTable(SQLiteDatabase mDB) {
        if(mDB != null) {
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("CREATE TABLE IF NOT EXISTS ").append(TB_DATA_VERSION)
                    .append(" (").append(ID).append("");

           /* mDB.execSQL("CREATE TABLE IF NOT EXISTS " + TB_LOGIN_INFO
                    + " (" + ID + " integer primary key autoincrement, " + L_COL_UNIQUEKEY + " text not null unique,"
                    + L_COL_SUPER_UNIQUEKEY + " text not null," + L_COL_LOGIN_ROLE + " integer not null," + L_COL_USERNAME + " text not null,"
                    + L_COL_PASSWORD + " text not null," + L_COL_NAME + " text," + L_COL_SEX + " text)");
            //+ L_COL_LOGIN_TIME + " integer not null," + L_COL_LOGIN_STATE + " integer not null

            mDB.execSQL("CREATE TABLE IF NOT EXISTS " + TB_DATA_VERSION
                    + " (" + ID + " integer primary key autoincrement, "
                    + V_COL_UNIQUNKEY + " text not null unique," + V_COL_VERSION_TYPE + " integer,"
                    + V_COL_VERSION + " integer)");

            mDB.execSQL("CREATE TABLE IF NOT EXISTS " + TB_USERS
                    + " (" + ID + " integer primary key autoincrement, "
                    + U_COL_UNIQUEKEY + " text not null," + U_COL_FA_UNIQUEKEY + " text not null," + U_COL_USER_ROLE
                    + " integer, " + U_COL_USERNAME + " text not null," + U_COL_SEX + " text not null,"
                    + U_COL_BRITHDAY + " text not null," + U_COL_JOB_TITLE + " text," + U_COL_JOB_POSITION + " text,"
                    + U_COL_JOB_DEPARTMENT + " text," + U_COL_ADD_BY_UNIQUEKEY + " text)");

            mDB.execSQL("CREATE TABLE IF NOT EXISTS " + TB_MEMBERS
                    + " (" + ID + " integer primary key autoincrement, "
                    + M_COL_UNIQUEKEY + " text unique," + M_COL_USER_UNIQUEKY + " text not null,"
                    + M_COL_HEADPATH + " text, " + M_COL_NATIVE_HEADPATH + " text, " + M_COL_NAME + " text not null," + M_COL_SEX + " text not null,"
                    + M_COL_IDNUMBER + " text," + M_COL_TEL + " text," + M_COL_PHONE + " text not null," + M_COL_ADDRESS + " text," + M_COL_BRITHDAY
                    + " text not null," + M_COL_ANAMNESIS + " text," + M_COL_SSN + " text," + M_COL_CREATE_PERSION + " text," + M_COL_CREATE_TIME
                    + " integer not null, " + M_COL_UPDATE_TIME + " integer not null," + M_COL_VERSION + " integer, " + M_COL_STATE + " integer, "
                    + M_COL_ADD_UNIQUEKEY + " text," + M_COL_WITH_DOCTOR + " text)");*/

            mDB.execSQL("CREATE TABLE IF NOT EXISTS " + TB_MEMBER_RECORD
                    + " (" + ID + " integer primary key autoincrement, "
                    + R_COL_RECORD_ID + " integer unique," + R_COL_NATIVE_MEMBER_ID + " integer not null," + R_COL_MEMBER_UNIQUEKEY + " text,"
                    + R_COL_RECORD_PERSION + " text, " + R_COL_CREATE_TIME + " integer DEFAULT (datetime(CURRENT_TIMESTAMP,'localtime')),"
                    + R_COL_UPDATE_TIME + " integer DEFAULT (datetime(CURRENT_TIMESTAMP,'localtime')),"
                    + R_COL_RECORD_TYPE + " integer," + R_COL_STATE + " integer," + R_COL_ADD_UNIQUEKEY + " integer)");

            mDB.execSQL("CREATE TABLE IF NOT EXISTS " + TB_MEMBER_RECORD_ITEM
                    + " (" + ID + " integer primary key autoincrement, " + RI_COL_NATIVE_RECORD_ID  + " integer not null,"
                    + RI_COL_RECORD_ID + " integer," + RI_COL_TYPE + " text not null,"
                    + RI_COL_VALUE1 + " float, " + RI_COL_SOURCE + " integer not null," + RI_COL_CONCLUSION + " text,"
                    + RI_COL_DESC1 + " text not null default ''," + RI_COL_DEVICE_TYPE + " integer not null," + RI_COL_RECORD_TIME + " integer DEFAULT (datetime(CURRENT_TIMESTAMP,'localtime')),"
                            + RI_COL_TESTCODE + " text not null default '')");

            mDB.execSQL("CREATE TABLE IF NOT EXISTS " + TB_DEVICE_BLUETOOTHS
                    + " (" + ID + " integer primary key autoincrement, " + B_COL_DEIVCE_NAME + " text not null,"
                    + B_COL_MAC  + " integer not null," + B_COL_DEVICE_TYPE + " integer not null," + B_COL_CONNECT_TIME + " integer not null)");

        }
    }

    private boolean clearDB(SQLiteDatabase mDB) {
        if(mDB != null) {
            mDB.execSQL("DROP TABLE IF EXISTS " + TB_LOGIN_INFO);
            mDB.execSQL("DROP TABLE IF EXISTS " + TB_DATA_VERSION);//
            mDB.execSQL("DROP TABLE IF EXISTS " + TB_USERS);
            mDB.execSQL("DROP TABLE IF EXISTS " + TB_MEMBERS);
            mDB.execSQL("DROP TABLE IF EXISTS " + TB_MEMBER_RECORD);//
            mDB.execSQL("DROP TABLE IF EXISTS " + TB_MEMBER_RECORD_ITEM);
            mDB.execSQL("DROP TABLE IF EXISTS " + TB_DEVICE_BLUETOOTHS);
            return true;
        }
        return false;
    }

    public void close() {
        if(instance != null) {
            instance = null;
        }
    }
}
