package com.cnnet.otc.health.bean.data;

/**
 * Created by ljs-jj on 2017/5/20.
 */
public interface MyData {
    void todo(byte[] var1);

    void todoConnected();

    void todoDisconnected();

    void todoConnecting();

    void todoDisconnected_failed();
}
