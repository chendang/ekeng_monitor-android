package com.cnnet.otc.health.bean;

/**
 * Created by SZ512 on 2016/1/26.
 */
public class MyBlueToothDevice {
    private long id;  //本地ID
    private String name; //蓝牙名称
    private String device_name;
    private String address;  //蓝牙MAC地址
    private int deviceType;  //当前蓝牙设备类型
    private long connectTime; //连接时间

    public MyBlueToothDevice() {}

    public MyBlueToothDevice(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public long getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(long connectTime) {
        this.connectTime = connectTime;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }
}
