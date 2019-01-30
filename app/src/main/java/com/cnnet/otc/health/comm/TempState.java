package com.cnnet.otc.health.comm;

/**
 * 蓝牙测试温度返回状态
 * Created by SZ512 on 2016/1/7.
 */
public class TempState {
    /**
     * 正常
     */
    public static final short NORMAL = 0;
    /**
     * 测量温度过低
     */
    public static final short MEASURING_TEMP_LOW = 1;
    /**
     * 测量温度过高
     */
    public static final short MEASURING_TEMP_HIGTH = 2;
    /**
     * 环温Lo过高
     */
    public static final short ENVIZ_TEMP_HIGTH = 3;
    /**
     * 环温Lo过低
     */
    public static final short ENVIZ_TEMP_LOW = 4;
    /**
     * 出错
     */
    public static final short EEPROM = 5;
    /**
     * 传感器出错
     */
    public static final short SENSOR = 6;

}