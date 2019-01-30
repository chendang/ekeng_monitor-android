package com.cnnet.otc.health.bean;

import java.util.Date;
import java.util.List;

/**
 * Created by SZ512 on 2016/1/11.
 */
public class MemberRecord {

    private long id;  //本地ID
    private long recordId;  //云盘ID
    private long memberNativeId; //会员本地ID
    private String mUniqueKey;  //会员唯一ID
    private String recordPersion;//给该会员做检查的医生(护士)
    private Date createTime;  //创建时间
    private Date updateTime; //修改时间
    private short recordType; //0:代表默认数据，PC端采集数据；1：代表上传图片数据
    private short state;  //当前记录是否提交：0待提交，1可以提交，2已经提交
    private String add_uniqueKey; //当前的记录是那个用户的：是医生或护士时则为其id(id>0)，是会员自己检测时为0

    private String nurseUniqueKey; //护士

    private List<RecordItem> checkedItems = null;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public String getmUniqueKey() {
        return mUniqueKey;
    }

    public void setmUniqueKey(String mUniqueKey) {
        this.mUniqueKey = mUniqueKey;
    }

    public String getRecordPersion() {
        return recordPersion;
    }

    public void setRecordPersion(String recordPersion) {
        this.recordPersion = recordPersion;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public short getRecordType() {
        return recordType;
    }

    public void setRecordType(short recordType) {
        this.recordType = recordType;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public void setAdd_uniqueKey(String add_uniqueKey) {
        this.add_uniqueKey = add_uniqueKey;
    }

    public String getAdd_uniqueKey() {
        return add_uniqueKey;
    }

    public List<RecordItem> getCheckedItems() {
        return checkedItems;
    }

    public void setCheckedItems(List<RecordItem> checkedItems) {
        this.checkedItems = checkedItems;
    }

    public String getNurseUniqueKey() {
        return nurseUniqueKey;
    }

    public void setNurseUniqueKey(String nurseUniqueKey) {
        this.nurseUniqueKey = nurseUniqueKey;
    }

    public long getMemberNativeId() {
        return memberNativeId;
    }

    public void setMemberNativeId(long memberNativeId) {
        this.memberNativeId = memberNativeId;
    }
}
