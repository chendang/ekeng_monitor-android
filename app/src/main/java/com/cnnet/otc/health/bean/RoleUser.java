package com.cnnet.otc.health.bean;

import com.cnnet.otc.health.interfaces.IUser;

/**
 * Created by SZ512 on 2016/1/8.
 */
public class RoleUser extends IUser {

    private String brithday;  //出生日期
    private String jobTitle; //职称
    private String jobPosition; //职位
    private String jobDepartment; //部门
    private String addByUniqueKey; //被谁添加：该值只在本地有用

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getJobPosition() {
        return jobPosition;
    }

    public void setJobPosition(String jobPosition) {
        this.jobPosition = jobPosition;
    }

    public String getJobDepartment() {
        return jobDepartment;
    }

    public void setJobDepartment(String jobDepartment) {
        this.jobDepartment = jobDepartment;
    }

    public String getBrithday() {
        return brithday;
    }

    public void setBrithday(String brithday) {
        this.brithday = brithday;
    }

    public String getAddByUniqueKey() {
        return addByUniqueKey;
    }

    public void setAddByUniqueKey(String addByUniqueKey) {
        this.addByUniqueKey = addByUniqueKey;
    }
}
