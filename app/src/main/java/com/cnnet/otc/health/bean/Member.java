package com.cnnet.otc.health.bean;

import com.cnnet.otc.health.interfaces.IUser;

/**
 * Created by SZ512 on 2016/1/8.
 */
public class Member extends IUser {

    private String mHeadPath; //头像路径
    private String mNativeHeadPath; //本地头像路径：目前上传前使用
    private String mIdNumber; //身份证号
    private String mTel; //座机电话号码
    private String mPhone;  //手机号码
    private String mAddress;    //地址
    private String mBrithday;  //出生日期
    private String mSSN; //社保卡号
    private String mCreatePersion;    //添加会员的人员名称
    private String mAnamnesis; //既往病史
    private String mNickname; //用户昵称或登录帐户名
    private String mNamePinyin=""; //用户姓名拼音
    private long mCreateTime; //创建时间 (long 长整形时间戳)
    private long mUpdateTime; // 更新时间 (lon 长整形时间戳)
    private int recordCount; //检查次数
    private String lastRecordTime;    //最后一次检查时间, long 长整形
    private long version; //最新版本
    private int state; //当前会员状态：0为上传，1已经上传(当为1时，后面两个字段无用)
    private String addUniqueKey; //添加会员的账号Key（目前仅在新增会员时，使用）
    private String withDoctor;  //护士登录新增会员时，保存（目前仅在新增会员时，使用）
    private String token;       //注册用户时，可用于传递身份验证相关信息

    private String message;  //验证手机号时，发送的验证码（目前仅在新增会员时，使用）
    private long native_record_id;  //本地中待检记录ID

    private boolean isSelect;  //是否已经被选中
    private boolean isSuperClinic; //判断是否是超级诊所的会员

    public String getmIdNumber() {
        return mIdNumber;
    }

    public void setmIdNumber(String mIdNumber) {
        this.mIdNumber = mIdNumber;
    }

    public String getmTel() {
        return mTel;
    }

    public void setmTel(String mTel) {
        this.mTel = mTel;
    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getmBrithday() {
        return mBrithday;
    }

    public void setmBrithday(String mBrithday) {
        this.mBrithday = mBrithday;
    }

    public String getmSSN() {
        return mSSN;
    }

    public void setmSSN(String mSSN) {
        this.mSSN = mSSN;
    }

    public String getmCreatePersion() {
        return mCreatePersion;
    }

    public void setmCreatePersion(String mCreatePersion) {
        this.mCreatePersion = mCreatePersion;
    }

    public String getmAnamnesis() {
        return mAnamnesis;
    }

    public void setmAnamnesis(String mAnamnesis) {
        this.mAnamnesis = mAnamnesis;
    }

    public long getmCreateTime() {
        return mCreateTime;
    }

    public void setmCreateTime(long mCreateTime) {
        this.mCreateTime = mCreateTime;
    }

    public long getmUpdateTime() {
        return mUpdateTime;
    }

    public void setmUpdateTime(long mUpdateTime) {
        this.mUpdateTime = mUpdateTime;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public String getLastRecordTime() {
        return lastRecordTime;
    }

    public void setLastRecordTime(String lastRecordTime) {
        this.lastRecordTime = lastRecordTime;
    }

    public String getmHeadPath() {
        return mHeadPath;
    }

    public void setmHeadPath(String mHeadPath) {
        this.mHeadPath = mHeadPath;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getNative_record_id() {
        return native_record_id;
    }

    public void setNative_record_id(long native_record_id) {
        this.native_record_id = native_record_id;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public String getmNativeHeadPath() {
        return mNativeHeadPath;
    }

    public void setmNativeHeadPath(String mNativeHeadPath) {
        this.mNativeHeadPath = mNativeHeadPath;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getAddUniqueKey() {
        return addUniqueKey;
    }

    public void setAddUniqueKey(String addUniqueKey) {
        this.addUniqueKey = addUniqueKey;
    }

    public String getWithDoctor() {
        return withDoctor;
    }

    public void setWithDoctor(String withDoctor) {
        this.withDoctor = withDoctor;
    }

    public boolean isSuperClinic() {
        return isSuperClinic;
    }

    public void setIsSuperClinic(boolean isSuperClinic) {
        this.isSuperClinic = isSuperClinic;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getmNickname() {
        return mNickname;
    }

    public void setmNickname(String mNickname) {
        this.mNickname = mNickname;
    }

    public String getmNamePinyin() {
        return mNamePinyin;
    }

    public void setmNamePinyin(String mNamePinyin) {
        this.mNamePinyin = mNamePinyin;
    }
}
