package com.cnnet.otc.health.comm;

/**
 * Created by SZ512 on 2016/1/6.
 */
public enum LoginRole {
    ROLE_DOCTOR(50),  //医生
    ROLE_NURSE(60),  //护士
    ROLE_MEMBER(70);  //会员(医生权限)

    private int _value;
    private LoginRole(int role) {
        _value = role;
    }
    public int value() {
        return _value;
    }
}
