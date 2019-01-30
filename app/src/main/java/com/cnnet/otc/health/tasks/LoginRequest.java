package com.cnnet.otc.health.tasks;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.interfaces.IUser;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.events.LoginEvent;
import com.cnnet.otc.health.managers.JsonManager;
import com.cnnet.otc.health.managers.RequestManager;
import com.cnnet.otc.health.util.AppCheckUtil;
import com.cnnet.otc.health.util.DESUtil;
import com.cnnet.otc.health.util.NetUtil;
import com.cnnet.otc.health.util.StringUtil;

import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by SZ512 on 2016/1/8.
 */
public class LoginRequest {

    private static final String TAG = "LoginRequest";

    /**
     * 进行登录
     * @param ctx
     * @param username
     * @param password
     */
    public static void doLogin(final Context ctx, final String username, final String password) {
        SysApp.LOGIN_STATE = CommConst.FLAG_USER_STATUS_LOGIN_ING;
        if (NetUtil.checkNetState(ctx)) {
            RequestManager.clientLogin(ctx, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Log.d(TAG, "result : " + jsonObject.toString());
                    if (JsonManager.getCode(jsonObject) == 0) {
                            IUser userInfo = JsonManager.getLoginInfo(jsonObject);
                       /* IUser userInfo =new IUser();
                        userInfo.setId(00000);
                        userInfo.setName("晨晨");
                        userInfo.setRole(CommConst.FLAG_USER_ROLE_MEMBER);
                        userInfo.setUsername("晨晨");*/
                        if (userInfo != null) {
                                userInfo.setUsername(username);
                                userInfo.setPassword(DESUtil.encrypt(password));
                                if(userInfo.getRole() == CommConst.FLAG_USER_ROLE_NURSE) {
                                    getDoctorListByNurse(ctx, userInfo);  //当护士时，还需获取医生信息
                                } else {
                                    SysApp.getSpManager().saveLoginInfo(userInfo);
                                    SysApp.getMyDBManager().addLoginInfo(userInfo);
                                    SysApp.setLoginUser(userInfo);
                                    SysApp.LOGIN_STATE = CommConst.FLAG_USER_STATE_LOGIN;
                                    EventBus.getDefault().post(new LoginEvent(CommConst.FLAG_USER_STATE_LOGIN));
                                }
                                return;
                            }
                    }
                    SysApp.LOGIN_STATE = CommConst.FLAG_USER_STATUS_OFF_LINE;
                    AppCheckUtil.toastErrMsgByConnectResultCode(ctx, CommConst.ERROR_CODE_LOGIN_USER_PWD_ERR);
                    EventBus.getDefault().post(new LoginEvent(CommConst.FLAG_USER_STATUS_OFF_LINE));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    SysApp.LOGIN_STATE = CommConst.FLAG_USER_STATUS_OFF_LINE;
                    AppCheckUtil.toastErrMsgByConnectResultCode(ctx, CommConst.ERROR_CODE_SERVER_FAIL);
                    EventBus.getDefault().post(new LoginEvent(CommConst.FLAG_USER_STATUS_OFF_LINE));
                }
            }, username, password);
        } else {
            offlineLogin(ctx, username, password);
        }
    }

    /**
     * 护士角色通过服务器登录后，直接获取医生列表
     * @param ctx
     * @param userInfo
     */
    private static void getDoctorListByNurse(final Context ctx, final IUser userInfo) {
        if(userInfo != null && userInfo.getRole() == CommConst.FLAG_USER_ROLE_NURSE) {
            RequestManager.getDoctorListByNurse(ctx, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Log.d(TAG, "result : " + jsonObject.toString());
                    if (JsonManager.getCode(jsonObject) == 0) {
                        String addUniqueKey = userInfo.getUniqueKey();
                        SysApp.getMyDBManager().deleteRoleUserByAddUniqueKey(addUniqueKey, CommConst.FLAG_USER_ROLE_DOCTOR);
                        int count = JsonManager.getAndSaveDoctorListsByJson(jsonObject, addUniqueKey);
                        if(count > 0) {
                            SysApp.getSpManager().saveLoginInfo(userInfo);
                            SysApp.getMyDBManager().addLoginInfo(userInfo);
                            SysApp.setLoginUser(userInfo);
                            SysApp.LOGIN_STATE = CommConst.FLAG_USER_STATE_LOGIN;
                            EventBus.getDefault().post(new LoginEvent(CommConst.FLAG_USER_STATE_LOGIN));
                            return;
                        }
                    }
                    SysApp.LOGIN_STATE = CommConst.FLAG_USER_STATUS_OFF_LINE;
                    AppCheckUtil.toastErrMsgByConnectResultCode(ctx, CommConst.ERROR_CODE_LOGIN_USER_PWD_ERR);
                    EventBus.getDefault().post(new LoginEvent(CommConst.FLAG_USER_STATUS_OFF_LINE));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.d(TAG, "result : " + volleyError.toString());
                    SysApp.LOGIN_STATE = CommConst.FLAG_USER_STATUS_OFF_LINE;
                    AppCheckUtil.toastErrMsgByConnectResultCode(ctx, CommConst.ERROR_CODE_SERVER_FAIL);
                    EventBus.getDefault().post(new LoginEvent(CommConst.FLAG_USER_STATUS_OFF_LINE));
                }
            }, userInfo.getUniqueKey());
        }

    }

    /**
     * 在后台自动登录
     * @param ctx
     * @param user
     */
    public static void backgroundLogin(Context ctx, IUser user) {
        if (user != null && StringUtil.isNotEmpty(user.getUsername())
                && StringUtil.isNotEmpty(user.getPassword())) {
            String password = DESUtil.decrypt(user.getPassword());
            if(password != null) {
                doLogin(ctx, user.getUsername(), password);
                return;
            }
        }
        EventBus.getDefault().post(new LoginEvent(CommConst.FLAG_USER_STATUS_OFF_LINE));
    }

    /**
     * 离线登录
     * @param username
     * @param password
     */
    private static void offlineLogin(Context ctx, String username, String password) {
        password = DESUtil.encrypt(password);
        if(StringUtil.isNotEmpty(password)) {
            IUser userInfo = SysApp.getMyDBManager().checkOfflineLoginByPwd(username, password);
            if (userInfo != null) {
                SysApp.setLoginUser(userInfo);
                SysApp.LOGIN_STATE = CommConst.FLAG_USER_STATE_OFFLINE_LOGIN;
                EventBus.getDefault().post(new LoginEvent(CommConst.FLAG_USER_STATE_OFFLINE_LOGIN));
                return;
            }
        }
        SysApp.LOGIN_STATE = CommConst.FLAG_USER_STATUS_OFF_LINE;
        AppCheckUtil.toastErrMsgByConnectResultCode(ctx, CommConst.ERROR_CODE_SERVER_FAIL);
        EventBus.getDefault().post(new LoginEvent(CommConst.FLAG_USER_STATUS_OFF_LINE));
    }


}
