package com.cnnet.otc.health.interfaces;

/**
 * Created by SZ512 on 2016/1/8.
 */
public class IUser {


    private long id;   //本地id

    private int role; //50:医生,    60: 护士   70:会员

    private String uniqueKey; //唯一键

    private String faUniqueKey;  ////四级管理员的 ID(诊所账号 ID)

    private String username;  //登录账号

    private String password;  //经过加密后的密码

    private String name; //用户姓名

    private String sex;   //性别：M男，F女

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getFaUniqueKey() {
        return faUniqueKey;
    }

    public void setFaUniqueKey(String faUniqueKey) {
        this.faUniqueKey = faUniqueKey;
    }
}
