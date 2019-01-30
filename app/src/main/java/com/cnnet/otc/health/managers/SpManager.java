package com.cnnet.otc.health.managers;

import android.content.Context;

import com.cnnet.otc.health.comm.CheckType;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.interfaces.IUser;
import com.cnnet.otc.health.util.SpUtil;
import com.cnnet.otc.health.util.StringUtil;

/**
 * Created by SZ512 on 2016/1/7.
 */
public class SpManager {
    private final String TAG = "SpManager";

    private final String _SP_NAME = "userinfo";

    private final String SP_KEY_USERNAME = "username";
    private final String SP_KEY_PASSWORD = "password";
    private final String SP_KEY_ROLE = "login_role";
    private final String SP_KEY_ISFIRSTRUN = "isFristRun";
    private final String SP_KEY_SERVER_URL = "server_url";

    /******************蓝牙Key**********************/
    public static final String SP_KEY_BLOOD_GLUCOSE = "ble_blood_glucose";   //血糖仪
    public static final String SP_KEY_BLOOD_PRESSURE = "ble_blood_pressure";   //血压计
    public static final String SP_KEY_THERMOMETER = "ble_thermometer";       //体温计
    public static final String SP_KEY_OXIMETRY = "ble_oximetry";          //血氧饱和度
    public static final String SP_KEY_LIPID = "ble_lipid";            //血脂

    private SpUtil spUtil = null;


    public SpManager(Context ctx) {
        spUtil = SpUtil.getInstance(ctx, _SP_NAME);
    }

    /**
     * 获取SpManager的实例化对象
     * @param ctx
     * @return
     */
    public static SpManager getInstance(Context ctx) {
        return new SpManager(ctx);
    }

    /**
     * 保存当前登录用户的账号密码
     * @param user 当前登录登录对象
     */
    public void saveLoginInfo(IUser user) {
        spUtil.putKv(SP_KEY_USERNAME, user.getUsername());
        spUtil.putKv(SP_KEY_PASSWORD, user.getPassword());
        spUtil.putKv(SP_KEY_ROLE, user.getRole());
    }

    /**
     * 获取当前登录账号信息
     * @return
     */
    public IUser getLoginInfo() {
        IUser bean = new IUser();
        try {
            bean.setUsername((String) spUtil.get(SP_KEY_USERNAME));
            bean.setPassword((String) spUtil.get(SP_KEY_PASSWORD));
            if (StringUtil.isNotEmpty(bean.getUsername())) {
                int role = (int)spUtil.get(SP_KEY_ROLE);
                bean.setRole(role);
                return bean;
            }
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 清除当前登录账号
     */
    public void clearLoginInfo() {
        spUtil.remove(SP_KEY_USERNAME);
        spUtil.remove(SP_KEY_PASSWORD);
        spUtil.remove(SP_KEY_ROLE);
    }

    /**
     * 根据类型保存蓝牙
     * @param type
     */
    public void saveBlueToothAddress(CheckType type, String bleAddress) {
        switch (type) {
            case BLOOD_GLUCOSE:   //血糖仪
                spUtil.putKv(SP_KEY_BLOOD_GLUCOSE, bleAddress);
                break;
            case BLOOD_PRESSURE:   //血压计
                spUtil.putKv(SP_KEY_BLOOD_PRESSURE, bleAddress);
                break;
            case THERMOMETER:       //体温计
                spUtil.putKv(SP_KEY_THERMOMETER, bleAddress);
                break;
            case OXIMETRY:          //血氧饱和度
                spUtil.putKv(SP_KEY_OXIMETRY, bleAddress);
                break;
            case LIPID:            //血脂
                spUtil.putKv(SP_KEY_LIPID, bleAddress);
                break;
        }
    }

    /**
     * 获取当前连接的蓝牙
     * @param type
     * @return
     */
    public String getBlueToothAddress(CheckType type) {
        switch (type) {
            case BLOOD_GLUCOSE:   //血糖仪
                return (String)spUtil.get(SP_KEY_BLOOD_GLUCOSE);
            case BLOOD_PRESSURE:   //血压计
                return (String)spUtil.get(SP_KEY_BLOOD_PRESSURE);
            case THERMOMETER:       //体温计
                return (String)spUtil.get(SP_KEY_THERMOMETER);
            case OXIMETRY:          //血氧饱和度
                return (String)spUtil.get(SP_KEY_OXIMETRY);
            case LIPID:            //血脂
                return (String)spUtil.get(SP_KEY_LIPID);
        }
        return null;
    }

    /**
     * 获取本地存储Server Url
     * @return
     */
    public String getServerUrl() {
        String serverUrl = null;
        try {
            serverUrl = (String) spUtil.get(SP_KEY_SERVER_URL);
        }catch (Exception e) {
            e.printStackTrace();
        }
        if(StringUtil.isNotEmpty(serverUrl)) {
            return serverUrl;
        }
        return CommConst.SERVER_URL;
    }
    public String getOtcPushServerUrl() {

        return CommConst.OTC_PUSH_SERVER_URL;
    }
    /**
     * 修改设置服务器URL
     * @param url
     */
    public void setServerUrl(String url) {
        if(StringUtil.isNotEmpty(url)) {
            spUtil.putKv(SP_KEY_SERVER_URL, url);
        }
    }

    /**
     * 获取是否是第一次打开运行APP
     * @return
     */
    public boolean isFirstRun() {
        return (boolean)spUtil.get(SP_KEY_ISFIRSTRUN, true);
    }

    /**
     * 设置不是第一次打开运行APP
     */
    public void setIsNotFirstRun() {
        spUtil.putKv(SP_KEY_ISFIRSTRUN, false);
    }

}
