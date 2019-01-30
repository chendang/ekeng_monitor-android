package com.cnnet.otc.health.bean;

import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.ParamType;
import com.cnnet.otc.health.util.StringUtil;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by SZ512 on 2016/1/4.
 */
public class RecordItem extends RealmObject {

    private long iId;  //id
    private long iNativeRecordId; //本地检查记录ID
    private long iRecordId;  //检查记录Id
    private int deviceType;  //设备检查类型
    private String iType;  //检查项
    private float value1;  //值1
    private short source;  //数据来源：1设备 ，2手动输入
    private String iConcluison; //描述
    private String desc1; //
    private String desc2; //
    private String desc3; //
    private String desc4; //
    private String desc5; //

    private float value2;
    private float value3;
    private float value4;
    private float value5;

    private String value1Txt="";
    private String value2Txt="";
    private String value3Txt="";
    private String value4Txt="";
    private String value5Txt="";

    private Date createTime;//检查时间
    private String insName;  //检查项名称
    private String insUnit;  //值得单位
    private int insValueRange;  //所得值得类型1,整形，2浮点型
    private int state;  //检查记录状态
    private String testCode;

    public static final int INS_VALUE_NUMBER = 1;  //值的类型为整形
    public static final int INS_VALUE_FLOAT = 2;  //值的类型为浮点型
    public static final int INS_VALUE_STRING = 3;  //值的类型为字符型

    public static final int CHECK_TYPE_WEEK = 1; //最近一个星期
    public static final int CHECK_TYPE_MONTH = 2; //最近一个月
    public static final int CHECK_TYPE_THREE_MONTHS = 3;  //最近三个月
    public static final int CHECK_TYPE_HALF_YEAR = 4; //最近半年




    public long getiId() {
        return iId;
    }

    public void setiId(long iId) {
        this.iId = iId;
    }

    public long getiRecordId() {
        return iRecordId;
    }

    public void setiRecordId(long iRecordId) {
        this.iRecordId = iRecordId;
    }

    public String getiType() {
        return iType;
    }

    public void setiType(String iType) {
        this.iType = iType;
    }

    public float getValue1() {
        return value1;
    }

    public void setValue1(float value1) {
        this.value1 = value1;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getInsName() {
        return insName;
    }

    public void setInsName(String insName) {
        this.insName = insName;
    }

    public String getInsUnit() {
        return insUnit;
    }

    public void setInsUnit(String insUnit) {
        this.insUnit = insUnit;
    }

    public int getInsValueRange() {
        return insValueRange;
    }

    public void setInsValueRange(int insValueRange) {
        this.insValueRange = insValueRange;
    }

    public long getiNativeRecordId() {
        return iNativeRecordId;
    }

    public void setiNativeRecordId(long iNativeRecordId) {
        this.iNativeRecordId = iNativeRecordId;
    }

    public short getSource() {
        return source;
    }

    public void setSource(short source) {
        this.source = source;
    }

    public String getDesc1() {
        if(desc1==null)
            return "";
        return desc1;
    }

    public void setDesc1(String desc1) {
        this.desc1 = desc1;
    }

    public String getiConcluison() {
        return iConcluison;
    }

    public void setiConcluison(String iConcluison) {
        this.iConcluison = iConcluison;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public float getValue2() {
        return value2;
    }

    public void setValue2(float value2) {
        this.value2 = value2;
    }

    public float getValue3() {
        return value3;
    }

    public void setValue3(float value3) {
        this.value3 = value3;
    }

    public float getValue4() {
        return value4;
    }

    public void setValue4(float value4) {
        this.value4 = value4;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public float getValue5() {
        return value5;
    }

    public void setValue5(float value5) {
        this.value5 = value5;
    }

    public String getDesc2() {
        return desc2;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    public String getDesc3() {
        return desc3;
    }

    public void setDesc3(String desc3) {
        this.desc3 = desc3;
    }

    public String getDesc4() {
        return desc4;
    }

    public void setDesc4(String desc4) {
        this.desc4 = desc4;
    }

    public String getDesc5() {
        return desc5;
    }

    public void setDesc5(String desc5) {
        this.desc5 = desc5;
    }

    public String getTestCode() {
        return testCode;
    }

    public void setTestCode(String testCode) {
        this.testCode = testCode;
    }

    public static String format(float val,String iType,String descr)
    {
        ParamType paramType=ParamType.find(iType);
        if(paramType==null) return String.valueOf(val);
        int  range=paramType.getRange();
        if(descr == null)
        {
            return "N/A";
        }

        if(descr.equals(CommConst.VALUE_GREATER))
        {
            return ">"+StringUtil.getInsValueStr(range,val,"");
        }
        else if(descr.equals(CommConst.VALUE_SMALLER))
        {
            return "<"+StringUtil.getInsValueStr(range,val,"");
        }
        else if(descr.equals(CommConst.VALUE_STRANGE))
            return "N/A";
        return StringUtil.getInsValueStr(range,val,"");
    }


    public String getValue2Txt() {
        return value2Txt;
    }

    public void setValue2Txt(String value2Txt) {
        this.value2Txt = value2Txt;
    }

    public String getValue1Txt() {
        return value1Txt;
    }

    public void setValue1Txt(String value1Txt) {
        this.value1Txt = value1Txt;
    }

    public String getValue3Txt() {
        return value3Txt;
    }

    public void setValue3Txt(String value3Txt) {
        this.value3Txt = value3Txt;
    }

    public String getValue4Txt() {
        return value4Txt;
    }

    public void setValue4Txt(String value4Txt) {
        this.value4Txt = value4Txt;
    }

    public String getValue5Txt() {
        return value5Txt;
    }

    public void setValue5Txt(String value5Txt) {
        this.value5Txt = value5Txt;
    }
}
