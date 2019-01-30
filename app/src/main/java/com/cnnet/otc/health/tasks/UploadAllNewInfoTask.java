package com.cnnet.otc.health.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cnnet.otc.health.bean.FormImage;
import com.cnnet.otc.health.bean.Member;
import com.cnnet.otc.health.bean.MemberRecord;
import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.comm.volleyRequest.PostUploadRequest;
import com.cnnet.otc.health.db.MyDBManager;
import com.cnnet.otc.health.interfaces.SubmitServerListener;
import com.cnnet.otc.health.managers.JsonManager;
import com.cnnet.otc.health.managers.RequestManager;
import com.cnnet.otc.health.util.AppCheckUtil;
import com.cnnet.otc.health.util.NetUtil;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by SZ512 on 2016/1/16.
 */
public class UploadAllNewInfoTask {

    private static final String TAG = "UploadAllNewInfoTask";

    private static int index = 0;

    public static boolean isSync = false;

    /**
     * 同步本地信息
     * @param ctx
     * @param uniqueKey
     * @param l
     */
    public static void SynchronizationInfo(final Context ctx, final String uniqueKey, final SubmitServerListener l) {
        isSync = false;
        if(NetUtil.checkNetState(ctx)) { //当网络正常时开始同步
            //第一步：先同步会员信息，同步一条后需要修改本地会员表中KEY，和会员检查记录中的KEY
            uploadMemberInfo(ctx, uniqueKey, new SubmitServerListener() {
                @Override
                public void onResult(int result) {
                    if (result == 0) {
                        uploadRecordInfo(ctx, uniqueKey, new SubmitServerListener() {
                            @Override
                            public void onResult(int result) {
                                l.onResult(result);
                            }
                        });
                    } else {
                        l.onResult(result);
                    }
                }
            });
        } else {
            l.onResult(CommConst.ERROR_CODE_NETWORK_ERROR);
        }
    }
    /**
     * 同步本地信息
     * @param ctx
     * @param l
     */
    public static void SynchronizationOtcInfo(final Context ctx, final SubmitServerListener l) {
        isSync = false;
        if(NetUtil.checkNetState(ctx)) { //当网络正常时开始同步
            //第一步：先同步会员信息，同步一条后需要修改本地会员表中KEY，和会员检查记录中的KEY
//            uploadMemberInfo(ctx, uniqueKey, new SubmitServerListener() {
//                @Override
//                public void onResult(int result) {
//                    if (result == 0) {
            uploadOtcRecordInfo(ctx, new SubmitServerListener() {
                            @Override
                            public void onResult(int result) {
                                l.onResult(result);
                            }
                        });
//                    } else {
//                        l.onResult(result);
//                    }
//                }
//            });
        } else {
            l.onResult(CommConst.ERROR_CODE_NETWORK_ERROR);
        }
    }
    /**
     * 获取需要上传会员的对象，开始逐一进行上传
     * @param ctx
     * @param uniqueKey
     * @param l
     */
    private static void uploadMemberInfo(Context ctx, String uniqueKey, SubmitServerListener l) {
        index = 0;
        List<Member> memberList = SysApp.getMyDBManager().getWaitUploadMemberList(uniqueKey);
        if(memberList != null && memberList.size() > 0) {
            uploadMemberHead(ctx, memberList, l);
        } else {
            l.onResult(0);
        }
    }

    /**
     * 上传头像：
     *      当本地头像上传未上传时，执行上传操作；
     *      当头像上传成功时取消上传头像，直接提交会员信息
     * @param ctx
     * @param memberList
     * @param l
     */
    private static void uploadMemberHead(final Context ctx, final List<Member> memberList, final SubmitServerListener l) {
        if(index < memberList.size()) {
            isSync = true;
            final Member member = memberList.get(index);
            if (StringUtil.isEmpty(member.getmHeadPath()) && StringUtil.isNotEmpty(member.getmNativeHeadPath())) {
                FormImage image = new FormImage(member.getmNativeHeadPath());
                RequestManager.uploadFile(ctx, new PostUploadRequest.ResponseListener() {
                    @Override
                    public void onResponse(String jsonResponse) {
                        try {
                            JSONObject result = new JSONObject(jsonResponse);
                            if (JsonManager.getCode(result) == 0) {
                                String path = JsonManager.getUploadPath(result);
                                if (StringUtil.isNotEmpty(path)) {
                                    member.setmHeadPath(path);
                                    SysApp.getMyDBManager().updateMemberHeadById(member);
                                    if (SysApp.getAccountBean().getRole() == CommConst.FLAG_USER_ROLE_DOCTOR) {
                                        submitOneMemberByDoctor(ctx, memberList, l);
                                    } else /*if(SysApp.getAccountBean().getRole() == CommConst.FLAG_USER_ROLE_NURSE)*/ {
                                        submitOneMemberByNurse(ctx, memberList, l);
                                    }
                                    return;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d(TAG, "error = " + volleyError.toString());
                        l.onResult(CommConst.ERROR_CODE_UPLOAD_FILE_ERROR);
                    }
                }, image, SysApp.getAccountBean().getUniqueKey());
            } else {
                if (SysApp.getAccountBean().getRole() == CommConst.FLAG_USER_ROLE_DOCTOR) {
                    submitOneMemberByDoctor(ctx, memberList, l);
                } else /*if(SysApp.getAccountBean().getRole() == CommConst.FLAG_USER_ROLE_NURSE)*/ {
                    submitOneMemberByNurse(ctx, memberList, l);
                }
            }
        } else {
            l.onResult(0);
            index = 0;
        }
    }

    /**
     * 护士：账号上传一个会员信息
     * @param ctx
     * @param memberList
     * @param l
     */
    private static void submitOneMemberByNurse(final Context ctx, final List<Member> memberList, final SubmitServerListener l) {
        final Member member = memberList.get(index);
        RequestManager.addMemberByNurse(ctx, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                if (result != null && JsonManager.getCode(result) == 0) {
                    Member mem = JsonManager.getAddMemberSuccessInfoFromJson(result, member);
                    if(mem != null) {
                        if(!SysApp.getMyDBManager().updateMemberKeyById(mem)) {
                            SysApp.getMyDBManager().deleteMemberByCreateTime(member.getmCreateTime());
                        }
                        index++;
                        uploadMemberHead(ctx, memberList, l);
                        return;
                    }
                }
                l.onResult(CommConst.ERROR_CODE_SERVER_ADD_MEMBER_ERROR);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                l.onResult(CommConst.ERROR_CODE_SERVER_FAIL);
            }
        }, member, member.getAddUniqueKey());
    }

    /**
     * 医生：账号上传一个会员信息
     * @param ctx
     * @param memberList
     * @param l
     */
    private static void submitOneMemberByDoctor(final Context ctx, final List<Member> memberList, final SubmitServerListener l) {
        final Member member = memberList.get(index);
        RequestManager.addMemberByDoctor(ctx, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject result) {
                if (result != null && JsonManager.getCode(result) == 0) {
                    Member mem = JsonManager.getAddMemberSuccessInfoFromJson(result, member);
                    if (mem != null) {
                        if (!SysApp.getMyDBManager().updateMemberKeyById(mem)) {
                            SysApp.getMyDBManager().deleteMemberByCreateTime(member.getmCreateTime());
                        }
                        index++;
                        uploadMemberHead(ctx, memberList, l);
                        return;
                    }
                }
                l.onResult(CommConst.ERROR_CODE_SERVER_ADD_MEMBER_ERROR);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                l.onResult(CommConst.ERROR_CODE_SERVER_FAIL);
            }
        }, member, member.getAddUniqueKey());
    }

    /**
     * 获取需要同步的会员检查记录，逐一开始同步
     * @param ctx
     * @param uniqueKey
     * @param l
     */
    private static void uploadRecordInfo(Context ctx, String uniqueKey, SubmitServerListener l) {
        index = 0;
        List<MemberRecord> recordsLists = SysApp.getMyDBManager().getWaitUploadRecordList(uniqueKey);
        if(recordsLists != null && recordsLists.size() > 0) {
            isSync = true;
            submitOneRecord(ctx, recordsLists, index, uniqueKey, l);
        } else {
            l.onResult(0);
        }
    }

    /**
     * 依次提交检查记录
     * @param ctx
     * @param recordsLists
     * @param position
     * @param addUniquKey
     * @param l
     */
    private static void submitOneRecord(final Context ctx, final List<MemberRecord> recordsLists, int position, final String addUniquKey, final SubmitServerListener l) {
        final MemberRecord record = recordsLists.get(position);
        List<RecordItem> lists = SysApp.getMyDBManager().getSubmitedListByRecordId(record.getId());
        RequestManager.addMemberRecord(ctx, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if(JsonManager.getCode(jsonObject) == 0) {
                            try {
                                Log.d(TAG, "submitOneRecord : " + jsonObject.toString());
                                long recordId = jsonObject.getLong("recordId");
                                SysApp.getMyDBManager().submitRecordSuccess(addUniquKey, record.getId(), recordId, record.getCreateTime());
                                index++;
                                if(index >= recordsLists.size()) {
                                    l.onResult(0);
                                    index = 0;
                                } else {
                                    submitOneRecord(ctx, recordsLists, index, addUniquKey, l);
                                }
                                return;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        l.onResult(CommConst.ERROR_CODE_SERVER_ADD_RECORD_ERROR);
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        l.onResult(CommConst.ERROR_CODE_SERVER_FAIL);
                    }
                }, lists, record, addUniquKey);
    }

    /**
     * OTC
     * 获取需要同步的会员检查记录，逐一开始同步
     * @param ctx
     * @param l
     */
    private static void uploadOtcRecordInfo(Context ctx, SubmitServerListener l) {
        index = 0;
        List<MemberRecord> recordsLists = SysApp.getMyDBManager().getWaitUploadRecordList("");
        if(recordsLists != null && recordsLists.size() > 0) {
            isSync = true;
            submitOtcOneRecord(ctx, recordsLists, index, l);
        } else {
            l.onResult(0);
        }
    }
    /**
     * OTC
     * 依次提交检查记录
     * @param ctx
     * @param recordsLists
     * @param position
     * @param l
     */
    private static void submitOtcOneRecord(final Context ctx, final List<MemberRecord> recordsLists, int position,final SubmitServerListener l) {
        final MemberRecord record = recordsLists.get(position);
        final String addUniquKey=  record.getAdd_uniqueKey();
        List<RecordItem> lists = SysApp.getMyDBManager().getSubmitedListByRecordId( record.getRecordId());
        RequestManager.addMemberRecord(ctx, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if(JsonManager.getCode(jsonObject) == 200) {
                            SysApp.getMyDBManager().submitRecordSuccess(addUniquKey, record.getRecordId(), record.getRecordId(), record.getCreateTime());
                            index++;
                            if(index >= recordsLists.size()) {
                                l.onResult(0);
                                index = 0;
                            } else {
                                submitOtcOneRecord(ctx, recordsLists, index, l);
                            }
                            return;
                        }
                        l.onResult(CommConst.ERROR_CODE_SERVER_ADD_RECORD_ERROR);
                    }
                },
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        l.onResult(CommConst.ERROR_CODE_SERVER_FAIL);
                    }
                }, lists, record, addUniquKey);
    }
    public static void submitOneRecordInfo(Context ctx, final String addUniquKey, final long nativeRecordId, final SubmitServerListener l) {
        if(StringUtil.isNotEmpty(addUniquKey) && nativeRecordId > 0) {
            final MemberRecord record = SysApp.getMyDBManager().getWaitInspectorRecord(addUniquKey, nativeRecordId);
            if(record != null) {
                List<RecordItem> lists = SysApp.getMyDBManager().getSubmitedListByRecordId(record.getRecordId());
                if(lists != null && lists.size() > 0) {
                    SysApp.getMyDBManager().submitRecordInfo(addUniquKey, record.getRecordId());
                    RequestManager.addMemberRecord(ctx, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    if (JsonManager.getCode(jsonObject) == 200) {
//                                        try {
//                                            long recordId = jsonObject.getLong("recordId");
                                            SysApp.getMyDBManager().submitRecordSuccess(addUniquKey, nativeRecordId, nativeRecordId, record.getCreateTime());
                                            l.onResult(0);
                                            return;
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
                                    }
                                    l.onResult(CommConst.ERROR_CODE_SERVER_ADD_RECORD_ERROR);
                                }
                            },
                            new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    l.onResult(CommConst.ERROR_CODE_SERVER_FAIL);
                                }
                            }, lists, record, addUniquKey);
                    return;
                } else {
                    l.onResult(-2);
                    return;
                }
            }
        }
        l.onResult(-1);
    }
}
