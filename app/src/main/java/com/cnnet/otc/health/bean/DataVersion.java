package com.cnnet.otc.health.bean;

/**
 * Created by SZ512 on 2016/1/12.
 */
public class DataVersion {

    private int id; //本地id
    private String vUniqueKey; //门诊唯一Key
    private short versionType;  //版本类型：会员70，护士60，医生50
    private long version;  //版本号

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getvUniqueKey() {
        return vUniqueKey;
    }

    public void setvUniqueKey(String vUniqueKey) {
        this.vUniqueKey = vUniqueKey;
    }

    public short getVersionType() {
        return versionType;
    }

    public void setVersionType(short versionType) {
        this.versionType = versionType;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
