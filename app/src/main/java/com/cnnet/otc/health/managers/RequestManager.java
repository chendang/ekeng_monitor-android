package com.cnnet.otc.health.managers;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.Volley;
import com.cnnet.otc.health.bean.FormImage;
import com.cnnet.otc.health.bean.Member;
import com.cnnet.otc.health.bean.MemberRecord;
import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.bean.RoleUser;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.comm.volleyRequest.JsonStringRequest;
import com.cnnet.otc.health.comm.volleyRequest.PostUploadRequest;
import com.cnnet.otc.health.util.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by SZ512 on 2016/1/8.
 */
public class RequestManager {

    private static final String TAG = "RequestManager";

    private static Context mContext;
    private static final RequestManager instance = new RequestManager();
    private static RequestQueue requestQueue;//请求队列 在SysApp 中初始化，单例

    private RequestManager() {
    }

    /**
     * 初始化QequestManager对象
     * @param ctx
     * @return
     */
    public synchronized static RequestManager getInstance(Context ctx) {
        mContext = ctx;
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(mContext);
        }
        return instance;
    }

    /**
     * 销毁一个网络请求
     * @param tag
     */
    public static void cancelRequest(Object tag) {
        try {
            requestQueue.cancelAll(tag);
            Log.i(TAG, "cancelRequest :  " + tag);
        } catch (IllegalArgumentException e) {

        }
    }

    /**
     * 客户端登陆接口
     * @param ctx
     * @param listener
     * @param errorListener
     * @param paramsStr
     */
    public static void clientLogin(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                   String... paramsStr) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/clientLogin?");
        if (paramsStr.length == 2) {
            sb.append("userAccount=");
            sb.append(paramsStr[0]);
            sb.append("&password=");
            sb.append(paramsStr[1]);
        }
        String url = sb.toString();
        Log.d(TAG, "clientLogin: url(" + url + ")");
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, null, listener, errorListener);
        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }


    /**
     * 获取当前门店下的所有会员的最新版本
     * @param ctx
     * @param listener
     * @param errorListener
     * @param paramsStr
     */
    public static void getMemberVersion(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                        String... paramsStr) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/getMemberVersion?");
        if (paramsStr.length == 1) {
            sb.append("userUniqueKey=");
            sb.append(paramsStr[0]);
        }
        String url = sb.toString();
        Log.d(TAG, "getMemberVersion: url(" + url + ")");
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, null, listener, errorListener);
        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }

    /**
     * 当护士账号登录时：获取诊所下的所有会员列表
     * @param ctx
     * @param listener
     * @param errorListener
     * @param paramsStr
     */
    public static void getMemberGroupByLower(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                             String... paramsStr) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/getMemberGroupByLower?");
        if (paramsStr.length >= 1) {
            sb.append("nurseUniqueKey=");
            sb.append(paramsStr[0]);
            if(paramsStr.length >= 2) {
                sb.append("&version=");
                sb.append(paramsStr[1]);
                if (paramsStr.length == 3) {
                    sb.append("&offset=");
                    sb.append(paramsStr[2]);
                }
            }
        }
        String url = sb.toString();
        Log.d(TAG, "getMemberGroupByLower: url(" + url + ")");
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, null, listener, errorListener);
        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }

    /**
     * 医生账号登录时，获取其诊所的医生信息
     * @param ctx
     * @param listener
     * @param errorListener
     * @param paramsStr
     */
    public static void getMemberGroupByDoctor(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                              String... paramsStr) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/getMemberGroupByDoctor?");
        if (paramsStr.length >= 1) {
            sb.append("doctorUniqueKey=");
            sb.append(paramsStr[0]);
            if(paramsStr.length > 2) {
                sb.append("&version=");
                sb.append(paramsStr[1]);
                if (paramsStr.length == 3) {
                    sb.append("&offset=");
                    sb.append(paramsStr[2]);
                }
            }
        }
        String url = sb.toString();
        Log.d(TAG, "getMemberGroupByDoctor: url(" + url + ")");
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, null, listener, errorListener);
        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }

    /**
     * 根据条件搜索超级诊所里的信息
     * @param ctx
     * @param listener
     * @param errorListener
     * @param faUniqueKey
     * @param searchName
     * @param searchMobile
     * @param searchIdNumber
     */
    public static void searchMemberBySuperClinic(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                               String faUniqueKey, String searchName, String searchMobile,
                                               String searchIdNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/searchMemberBySuperClinic");
        JSONObject jsonByMaps = JsonManager.getSearchMemberJson(faUniqueKey, searchName, searchMobile, searchIdNumber);
        String url = sb.toString();
        Log.d(TAG, "searchMemberBySuperClinic: url(" + url + ")");
        Log.d(TAG, "searchMemberBySuperClinic: json(" + jsonByMaps.toString() + ")");
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, jsonByMaps.toString(), listener, errorListener);
        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }

    /**
     * 不是账号登录时，新增会员
     * @param ctx
     * @param listener    请求成功回调对象
     * @param errorListener  请求失败回调对象
     * @param member   //需要新增的会员信息列表
     * @param nurseUniqueKey  所属当前护士KEY列表
     */
    public static void addMemberByNurse(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                         Member member, String nurseUniqueKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/addMember");
        JSONObject jsonByMaps = JsonManager.getAddMemberByNurseJson(member, nurseUniqueKey);
        String url = sb.toString();
        Log.d(TAG, "addMemberByNurse: url(" + url + ")");
        String paramsStr = (jsonByMaps==null)?null:jsonByMaps.toString();
        Log.d(TAG, "params: " + paramsStr);
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, paramsStr, listener, errorListener);
        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }

    /**
     * 医生账号登录时，新增会员
     * @param ctx
     * @param listener    请求成功回调对象
     * @param errorListener  请求失败回调对象
     * @param member   //需要新增的会员信息列表
     * @param doctorUniqueKey   //传参医生的值
     */
    public static void addMemberByDoctor(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                         Member member, String doctorUniqueKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/addMemberByDoctor");
        JSONObject jsonByMaps = JsonManager.getAddMemberByDoctorJson(member, doctorUniqueKey);
        /*try {
            if(jsonByMaps != null) {
                jsonByMaps.put("nurse", JsonManager.getNurseJSONArrByList(nurseLists));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        String url = sb.toString();
        Log.d(TAG, "addMemberByDoctor: url(" + url + ")");
        String paramsStr = (jsonByMaps==null)?null:jsonByMaps.toString();
        Log.d(TAG, "params: " + paramsStr);
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, paramsStr, listener, errorListener);
        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }

    /**
     * 医生账号登录时，新增会员
     * @param ctx
     * @param listener    请求成功回调对象
     * @param errorListener  请求失败回调对象
     * @param member   //需要新增的会员信息列表
     * @param doctorUniqueKey   //传参医生的值
     */
    public static void addMember(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                         Member member, String doctorUniqueKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/addMemberBySelf");
        JSONObject jsonByMaps = JsonManager.getAddMemberBySelf(member);
        /*try {
            if(jsonByMaps != null) {
                jsonByMaps.put("nurse", JsonManager.getNurseJSONArrByList(nurseLists));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        String url = sb.toString();
        Log.d(TAG, "addMemberBySelf: url(" + url + ")");
        String paramsStr = (jsonByMaps==null)?null:jsonByMaps.toString();
        Log.d(TAG, "params: " + paramsStr);
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, paramsStr, listener, errorListener);
        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }

    /**
     * 添加健康检查记录
     * @param ctx
     * @param listener
     * @param errorListener
     * @param errorListener
     * @param record   检查记录对象
     * @param nurseUniqueKey   会员所属护士管辖
     */
   /* public static void addMemberRecord(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                       List<RecordItem> items, MemberRecord record, String nurseUniqueKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/addMemberRecord");
        JSONObject jsonByMaps = JsonManager.getAddMemberRecordJson(record, nurseUniqueKey);
        try {
            if(jsonByMaps != null) {
                jsonByMaps.put("items", JsonManager.getRecordInfoJSONArrByList(items));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = sb.toString();
        Log.d(TAG, "addMemberRecord: url(" + url + ")");
        String paramsStr = (jsonByMaps==null)?null:jsonByMaps.toString();
        Log.d(TAG, "params: " + paramsStr);
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, paramsStr, listener, errorListener);
        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }*/
    public static void addMemberRecord(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                       List<RecordItem> items, MemberRecord record, String nurseUniqueKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getOtcPushServerUrl());
        sb.append("/device/api/OtcPushData");
//        JSONObject jsonByMaps = JsonManager.getAddMemberRecordJson(record, nurseUniqueKey);
        JSONObject jsonByMaps = new JSONObject();

        try {
            jsonByMaps.put("UserName",record.getmUniqueKey());
            jsonByMaps.put("CreateTime", DateUtil.getDateStrByDate(record.getCreateTime()));
            for(RecordItem item: items) {
                jsonByMaps.put("data", JsonManager.getOtcRecordInfoJSON(item));
                String url = sb.toString();
                Log.d(TAG, "addMemberRecord: url(" + url + ")");
                String paramsStr = (jsonByMaps==null)?null:jsonByMaps.toString();
                Log.d(TAG, "params: " + paramsStr);
                JsonStringRequest jsonStringRequest = new JsonStringRequest(url, paramsStr, listener, errorListener);
                jsonStringRequest.setTag(ctx);
                requestQueue.add(jsonStringRequest);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    /**
     * 添加健康检查记录
     * @param ctx
     * @param listener
     * @param errorListener
     * @param errorListener
     * @param record   检查记录对象
     */
    public static void addMemberRecordBySelf(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                             List<RecordItem> items, MemberRecord record) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/addMemberRecord");
        JSONObject jsonByMaps = JsonManager.getAddMemberRecordJsonBySelf(record);
        try {
            if(jsonByMaps != null) {
                jsonByMaps.put("items", JsonManager.getRecordInfoJSONArrByList(items));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = sb.toString();
        Log.d(TAG, "addMemberRecord: url(" + url + ")");
        String paramsStr = (jsonByMaps==null)?null:jsonByMaps.toString();
        Log.d(TAG, "params: " + paramsStr);
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, paramsStr, listener, errorListener);
        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }

    /**
     * 根据检查类型获取检查结果列表
     * @param ctx
     * @param listener
     * @param errorListener
     * @param paramsStr
     */
    public static void getMemberRecordListByType(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                                 String... paramsStr) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/getMemberRecordListByType?");
        if (paramsStr.length == 2 || paramsStr.length == 3) {
            sb.append("mUniqueKey=");
            sb.append(paramsStr[0]);
            sb.append("&itemType=");
            sb.append(paramsStr[1]);
            if(paramsStr.length == 3) {
                sb.append("&offset=");
                sb.append(paramsStr[2]);
            }
        }
        String url = sb.toString();
        Log.d(TAG, "getMemberRecordListByType: url(" + url + ")");
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, null, listener, errorListener);
        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }

    /**
     * 修改会员信息
     * @param ctx
     * @param listener
     * @param errorListener
     * @param member   需要修改的会员信息对象
     */
    public static void updateMember(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                    Member member) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/updateMember");
        JSONObject params = JsonManager.getUpdateMemberJson(member);
        String url = sb.toString();
        Log.d(TAG, "updateMember: url(" + url + ")");
        String paramsStr = (params==null)?null:params.toString();
        Log.d(TAG, "params: " + paramsStr);
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, paramsStr, listener, errorListener);
        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }

    /**
     * 医生账号登录时，修改个人信息
     * @param ctx
     * @param listener
     * @param errorListener
     * @param doctor
     */
    public static void updateDoctor(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                    RoleUser doctor) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/updateDoctor");
        JSONObject params = JsonManager.getUpdateDoctorJson(doctor);
        String url = sb.toString();
        Log.d(TAG, "updateDoctor: url(" + url + ")");
        String paramsStr = (params==null)?null:params.toString();
        Log.d(TAG, "params: " + paramsStr);
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, paramsStr, listener, errorListener);
        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }

    /**
     * 根据护士KEY,获取当前护士所属医生列表
     * @param ctx
     * @param listener
     * @param errorListener
     * @param nurseUniqueKey
     */
    public static void getDoctorListByNurse(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                            String nurseUniqueKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/getDoctorListByNurse");
        sb.append("?nurseUniqueKey=");
        sb.append(nurseUniqueKey);
        String url = sb.toString();
        Log.d(TAG, "getDoctorListByNurse: url(" + url + ")");
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, null, listener, errorListener);
        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);

    }

    /**
     * 给手机发送验证码
     * @param ctx
     * @param listener
     * @param errorListener
     * @param userPhone  手机号
     */
    public static void sendMessage(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                   String userPhone) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/sendMessage?");
        sb.append("userAccount=");
        sb.append(userPhone);
        String url = sb.toString();
        Log.d(TAG, "sendMessage: url(" + url + ")");
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, null, listener, errorListener);
        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }

    /**
     * 判断数据库中是否存在该手机号
     * @param ctx
     * @param listener
     * @param errorListener
     * @param userPhone
     */
    public static void valid(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                             String userPhone) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/valid?");
        sb.append("userAccount=");
        sb.append(userPhone);
        String url = sb.toString();
        Log.d(TAG, "valid: url(" + url + ")");
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, null, listener, errorListener);
        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }

    /**
     * 上传头像或其他文件
     * @param ctx
     * @param listener
     * @param images  上传的图片对象
     * @param userUniqueKey 唯一ID
     */
    public static void uploadFile(Context ctx, PostUploadRequest.ResponseListener listener,
                                  FormImage images, String userUniqueKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/uploadFile?");
        sb.append("userUniqueKey=");
        sb.append(userUniqueKey);
        String url = sb.toString();
        Log.d(TAG, "uploadFile: url(" + url + ")");
        PostUploadRequest postUploadRequest = new PostUploadRequest(url, images, listener);
        postUploadRequest.setTag(ctx);
        requestQueue.add(postUploadRequest);
    }

    /**
     * 个人版会员注册
     * 预留接口
     */
    public static void addMemberBySelf(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                       Member member) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/action/client/addMemberBySelf");
        JSONObject jsonByMaps = JsonManager.getAddMemberBySelf(member);
        String url = sb.toString();
        Log.d(TAG, "addMemberBySelf: url(" + url + ")");
        String paramsStr = (jsonByMaps==null)?null:jsonByMaps.toString();
        Log.d(TAG, "params: " + paramsStr);
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, paramsStr, listener, errorListener);
        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }

    /**
     * 以表单形式上传病例图片或文件；（上传文件不能大于 30M）
     * 预留接口
     * @param ctx
     * @param listener
     * @param errorListener
     * @param paramsStr
     */
    public static void clientUploadCase(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,
                                        String... paramsStr) {

    }
    /**
     * 获取乐心设备信息
     * @param ctx
     * @param listener
     * @param errorListener
     * @param paramsStr
     */
    public static void getLSDeviceInfo(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,Map parammap,
                                        String... paramsStr) {
        StringBuilder sb = new StringBuilder();
        sb.append(CommConst.LS_GETDEIVCE_SERVER_URL);
//        sb.append("http://qa.sports.lifesense.com/operators_service/v1/api");
        sb.append("/getDeviceinfo?");
        if (paramsStr.length == 4) {
            sb.append("appid=");
            sb.append(paramsStr[0]);
            sb.append("&timestamp=");
            sb.append(paramsStr[1]);
            sb.append("&nonce=");
            sb.append(paramsStr[2]);
            sb.append("&checksum=");
            sb.append(paramsStr[3]);
        }
        String url = sb.toString();
        Log.d(TAG, "getLSDeviceInfo: url(" + url + ")");
        JSONObject params=new JSONObject(parammap);
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, params.toString(), listener, errorListener);

        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }
    /**
     * 提交乐心记录信息
     * @param ctx
     * @param listener
     * @param errorListener
     * @param paramsStr
     */
    public static void postLSRecordInfo(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,Map parammap,
                                       String... paramsStr) {
        StringBuilder sb = new StringBuilder();
        sb.append(CommConst.OTC_PUSH_SERVER_URL);
        sb.append("/device/api/Lx");
        sb.append(parammap.get("RecordType")+"Push");
        String url = sb.toString();
        Log.d(TAG, "postLSRecordInfo: url(" + url + ")");
        JSONObject params=new JSONObject(parammap);
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, params.toString(), listener, errorListener);

        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }
    /**
     * 提交记体脂称录信息
     * @param ctx
     * @param listener
     * @param errorListener
     * @param paramsStr
     */
    public static void postTZCRecordInfo(Context ctx, Listener<JSONObject> listener, ErrorListener errorListener,Map parammap,
                                         String... paramsStr) {
        StringBuilder sb = new StringBuilder();
        sb.append(SysApp.getSpManager().getServerUrl());
        sb.append("/api/HItemRecord/PostQNBodyInsRecord");
        String url = sb.toString();
        Log.d(TAG, "postTZCRecordInfo: url(" + url + ")");
        JSONObject params=new JSONObject(parammap);
        JsonStringRequest jsonStringRequest = new JsonStringRequest(url, params.toString(), listener, errorListener);

        jsonStringRequest.setTag(ctx);
        requestQueue.add(jsonStringRequest);
    }
}