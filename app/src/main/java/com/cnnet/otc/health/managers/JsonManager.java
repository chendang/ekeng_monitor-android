package com.cnnet.otc.health.managers;


import android.util.Log;

import com.cnnet.otc.health.bean.Member;
import com.cnnet.otc.health.bean.MemberRecord;
import com.cnnet.otc.health.bean.OperatorType;
import com.cnnet.otc.health.bean.RecordItem;
import com.cnnet.otc.health.bean.RoleUser;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.interfaces.IUser;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by SZ512 on 2016/1/8.
 */
public class JsonManager {

    private static final String TAG = "JsonManager";

    public static int getCode(JSONObject json) {
        if(json != null) {
            try {
                return json.getInt("code");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public static long getVersion(JSONObject json) {
        if(json != null) {
            try {
                return json.getLong("version");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    /**
     * 获取登录信息
     * @param json
     * @return
     */
    public static IUser getLoginInfo(JSONObject json) {
        if(json != null) {
            int role = 0;
            try {
                role = json.getInt("role");
                if(role == CommConst.FLAG_USER_ROLE_MEMBER) {
                    //return getMemberByJson(json);
                } else {
                    return getRoleUserByLoginJson(json);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * 根据返回的json获取会员的信息
     * @param json
     * @return
     */
    private static Member getLoginMemberByJson(JSONObject json) {
        if(json != null) {
            try {
                Member member = new Member();
                member.setRole(json.getInt("role"));
                json = json.getJSONObject("members");
                member.setUniqueKey(json.getString("mUniqueKey"));
                member.setFaUniqueKey(json.getString("mUserUniqueKey"));
                member.setmHeadPath(json.getString("mHeadPath"));
                member.setName(json.getString("mName"));
                member.setSex(json.getString("mSex"));
                member.setmIdNumber(json.getString("mIdNumber"));
                member.setmTel(json.getString("mTel"));
                member.setmPhone(json.getString("mPhone"));
                member.setmAddress(json.getString("mAddress"));
                member.setmBrithday(json.getString("mBrithday"));
                member.setmSSN(json.getString("mSSN"));
                member.setmCreatePersion(json.getString("mCreateNurse"));
                member.setmAnamnesis(json.getString("mAnamnesis"));
                member.setmCreateTime(json.getLong("mCreateTime"));
                member.setmUpdateTime(json.getLong("mUpdateTime"));
                member.setRecordCount(json.getInt("recordCount"));
                member.setLastRecordTime(json.getString("lastRecordTime"));
                return member;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private static List<Member> getGrpMemberByJson(JSONObject json) {
        if(json != null) {
            try {
                JSONArray jsonArray=json.getJSONArray("group");
                List<Member> grp_members=new ArrayList<Member>();
                int l=jsonArray.length();
                for(int i=0;i<l;i++)
                {
                    Member member=new Member();
                    JSONObject jsonObj=(JSONObject) jsonArray.getJSONObject(i);
                    member.setUniqueKey(json.getString("mUniqueKey"));
                    member.setName(json.getString("mName"));
                    member.setmPhone(json.getString("mPhone"));
                    grp_members.add(member);
                }
                return grp_members;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 获取医生（护士）用户登录时，返回的信息
     * @param json
     * @return
     */
    private static RoleUser getRoleUserByLoginJson(JSONObject json) {
        if(json != null) {
            try {
                RoleUser user = new RoleUser();
                user.setRole(json.getInt("role"));
                if(user.getRole() == CommConst.FLAG_USER_ROLE_NURSE
                        || user.getRole() == CommConst.FLAG_USER_ROLE_DOCTOR) {
                    json = json.getJSONObject("users");
                    user.setUniqueKey(json.getString("userUniqueKey"));
                    user.setFaUniqueKey(json.getString("superUniqueKey"));
                    user.setName(json.getString("name"));
                    user.setSex(json.getString("sex"));
                    user.setJobTitle(json.getString("jobTitle"));
                    user.setJobPosition(json.getString("jobPosition"));
                    user.setJobDepartment(json.getString("jobDepartment"));
                    return user;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * 获取护士角色列表
     * @param result
     * @param getByUniqueKey  被哪个用户ID所获取
     * @return
     */
    public static int getAndSaveDoctorListsByJson(JSONObject result, String getByUniqueKey) {
        if(result != null) {
            try {
                List<RoleUser> list = new ArrayList<>();
                JSONArray array = result.getJSONArray("doctors");
                int length = array.length();
                for(int i = 0; i < length; i++) {
                    JSONObject obj = array.getJSONObject(i);
                    RoleUser user = new RoleUser();
                    user.setUniqueKey(obj.getString("doctorUniqueKey"));
                    user.setRole(obj.getInt("roleId"));
                    user.setFaUniqueKey(obj.getString("faUniqueKey"));
                    user.setName(obj.getString("doctorName"));
                    user.setSex(obj.getString("sex"));
                    user.setBrithday(obj.getString("brithday"));
                    user.setJobTitle(obj.getString("jobTitle"));
                    user.setJobPosition(obj.getString("jobPosition"));
                    user.setJobDepartment(obj.getString("jobDepartment"));
                    user.setAddByUniqueKey(getByUniqueKey);
                    SysApp.getMyDBManager().saveRoleUser(user);
                }
                return length;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return 0;
    }

    /**
     * 存储返回的会员信息
     * @param obj
     * @return  会员总够条数
     */
    public static List<Member> saveMemberInfo(JSONObject obj) {
        final List<Member> members=new ArrayList<Member>();
        if(obj != null) {
            try {
                JSONArray array = obj.getJSONArray("members");
                int length = array.length();
                int lastIndex = length - 1;
                for (int i = 0; i < length; i++) {
                    Member member = new Member();
                    JSONObject item = array.getJSONObject(i);
                    member.setUniqueKey(item.getString("mUniqueKey"));
                    member.setFaUniqueKey(item.getString("mUserUniqueKey"));
                    member.setmHeadPath(item.getString("mHeadPath"));
                    member.setName(item.getString("mName"));
                    if(item.has("pinyin"))
                    {
                        member.setmNamePinyin(item.getString("pinyin"));
                    }
                    member.setSex(item.getString("mSex"));
                    member.setmIdNumber(item.getString("mIdNumber"));
                    member.setmTel(item.getString("mTel"));
                    member.setmPhone(item.getString("mPhone"));
                    member.setmAddress(item.getString("mAddress"));
                    member.setmBrithday(item.getString("mBrithday"));
                    member.setmSSN(item.getString("mSSN"));
                    member.setmCreatePersion(item.getString("mCreateNurse"));
                    member.setmAnamnesis(item.getString("mAnamnesis"));
                    member.setmCreateTime(item.getLong("mCreateTime"));
                    member.setmUpdateTime(item.getLong("mUpdateTime"));
                    member.setRecordCount(item.getInt("recordCount"));
                    member.setLastRecordTime(item.getString("lastRecordTime"));
                    member.setVersion(item.getLong("version"));
                    members.add(member);
                }
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (Member member : members) {
                            SysApp.getMyDBManager().saveMemberInfo(member);
                        }
                        if(members.size()>0)
                        {
                            Member member=members.get(members.size()-1);
                            SysApp.getMyDBManager().saveDataVersion(member.getFaUniqueKey(), CommConst.FLAG_USER_ROLE_MEMBER, member.getVersion());
                        }
                    }
                });
                thread.start();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return members;
    }



    /**
     * 获取返回的会员信息
     * @param obj
     * @param isSuperClinic  数据是否从超级诊所而来
     * @return  会员列表
     */
    public static List<Member> getMemberListByJson(JSONObject obj, boolean isSuperClinic) {
        if(obj != null) {
            try {
                JSONArray array = obj.getJSONArray("members");
                int length = array.length();
                List<Member> lists = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    Member member = new Member();
                    JSONObject item = array.getJSONObject(i);
                    member.setUniqueKey(item.getString("mUniqueKey"));
                    member.setFaUniqueKey(item.getString("mUserUniqueKey"));
                    member.setmHeadPath(item.getString("mHeadPath"));
                    member.setName(item.getString("mName"));
                    member.setSex(item.getString("mSex"));
                    member.setmIdNumber(item.getString("mIdNumber"));
                    member.setmTel(item.getString("mTel"));
                    member.setmPhone(item.getString("mPhone"));
                    member.setmAddress(item.getString("mAddress"));
                    member.setmBrithday(item.getString("mBrithday"));
                    member.setmSSN(item.getString("mSSN"));
                    member.setmCreatePersion(item.getString("mCreateNurse"));
                    member.setmAnamnesis(item.getString("mAnamnesis"));
                    member.setmCreateTime(item.getLong("mCreateTime"));
                    member.setmUpdateTime(item.getLong("mUpdateTime"));
                    member.setRecordCount(item.getInt("recordCount"));
                    member.setLastRecordTime(item.getString("lastRecordTime"));
                    member.setVersion(item.getLong("version"));
                    member.setIsSuperClinic(isSuperClinic);
                    lists.add(member);
//                    SysApp.getMyDBManager().saveMemberInfo(member);
                }
                return lists;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return null;
    }


    /**
     * 获取添加会员成功后的信息
     * @param obj
     * @param member
     * @return
     */
    public static Member getAddMemberSuccessInfoFromJson(JSONObject obj, Member member) {
        if(member != null && obj != null) {
            try {
                member.setUniqueKey(obj.getString("mUniqueKey"));
                member.setVersion(obj.getLong("version"));
                if(obj.has("pinyin"))
                {
                    member.setmNamePinyin(obj.getString("pinyin"));
                }
                return member;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取上传成功后返回的路劲
     * @param obj
     * @return
     */
    public static String getUploadPath(JSONObject obj) {
        if(obj != null) {
            try {
                return obj.getString("path");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /********************************将信息转换为JSON，准备提交信息**********************************/

    /**
     * 根据maps对象，将其转换为JSON对象
     * @param maps
     * @return
     */
    public static JSONObject getJSONByMaps(Map<String, Object> maps) {
        if(maps != null) {
            try {
                JSONObject obj = new JSONObject();
                for (Map.Entry<String, Object> entry : maps.entrySet()) {
                    obj.put(entry.getKey(), entry.getValue());
                }
                return obj;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据List将其转换为护士JSON数组
     * @param lists
     * @return
     */
    public static JSONArray getNurseJSONArrByList(List<RoleUser> lists) {
        JSONArray jsonArray = new JSONArray();
        if(lists != null) {
            try {
                for(RoleUser user: lists) {
                    JSONObject obj = new JSONObject();
                    obj.put("nurseUniqueKey", user.getUniqueKey());
                    obj.put("name", user.getName());
                    jsonArray.put(obj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    /**
     * 将一条检查记录的信息转成JSON数组
     * @param lists
     * @return
     */
    public static JSONArray getRecordInfoJSONArrByList(List<RecordItem> lists) {
        JSONArray jsonArray = new JSONArray();
        if(lists != null) {
            try {
                for(RecordItem item: lists) {
                    JSONObject obj = new JSONObject();
                    obj.put("iType", item.getiType());
                    obj.put("value1", item.getValue1());
                    obj.put("iConclusion", item.getiConcluison());
                    obj.put("desc1", item.getDesc1());
                    long times = item.getCreateTime().getTime();
                    obj.put("recordTime",times);
                    obj.put("testCode",item.getTestCode());
                    jsonArray.put(obj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    /**
     * 将一条检查记录的信息转成JSON数组
     * @param recordItem
     * @return
     */
    public static JSONObject getOtcRecordInfoJSON( RecordItem recordItem) {
       JSONObject obj = new JSONObject();
        if(recordItem != null) {
            try {
                obj.put("iType", recordItem.getiType());
                obj.put("value1", recordItem.getValue1());
                obj.put("idesc", recordItem.getiConcluison());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }
    /**
     * 修改会员时，获取将要提交的会员信息
     * @param member
     * @return
     */
    public static JSONObject getUpdateMemberJson(Member member) {
        if(member != null) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("mUniqueKey", member.getUniqueKey());
                obj.put("mName", member.getName());
                obj.put("mIdNumber", member.getmIdNumber());
                obj.put("mSex", member.getSex());
                obj.put("mAddress", member.getmAddress());
                obj.put("mTel", member.getmTel());
                obj.put("mBrithday", member.getmBrithday());
                obj.put("mSSN", member.getmSSN());
                obj.put("mAnamnesis", member.getmAnamnesis());
                return obj;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 使用新增会员信息填充JSONObject
     * @param member
     * @param obj
     * @return
     */
    public static void fillJsonWithMember(Member member,JSONObject obj) throws org.json.JSONException
    {
        String headPath = member.getmHeadPath();
        if(StringUtil.isNotEmpty(headPath)) {
            headPath = headPath.substring(headPath.lastIndexOf("/") + 1, headPath.length());
        } else {
            headPath = "";
        }
        obj.put("orgRoot",CommConst.SUFIX);
        obj.put("mNickname","");
        obj.put("mHeadPath", headPath);
        obj.put("mName", member.getName());
        obj.put("mSex", member.getSex());
        obj.put("mIdNumber", member.getmIdNumber());
        obj.put("mTel", member.getmTel());
        obj.put("mPhone", member.getmPhone());
        obj.put("message", member.getMessage());
        obj.put("mAddress", member.getmAddress());
        obj.put("mBrithday", member.getmBrithday());
        obj.put("mSSN", member.getmSSN());
        obj.put("mAnamnesis", member.getmAnamnesis());
        obj.put("mCreateTime", member.getmCreateTime());
        obj.put("mCreateTime", member.getmUpdateTime());

    }

    /**
     * 当以护士角色登录APP时，新增会员将要提交的信息
     * @param member
     * @param nurseUniqueKey
     * @return
     */
    public static JSONObject getAddMemberByNurseJson(Member member, String nurseUniqueKey) {
        if(member != null && StringUtil.isNotEmpty(nurseUniqueKey) && StringUtil.isNotEmpty(member.getWithDoctor())) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("nurseUniqueKey", nurseUniqueKey);
                obj.put("operator_type",OperatorType.BY_NURSE.ordinal());
                fillJsonWithMember(member,obj);
                JSONArray array = new JSONArray();
                JSONObject doctors = new JSONObject();
                doctors.put("doctorUniqueKey", member.getWithDoctor());
                array.put(doctors);
                obj.put("doctor", array);
                return obj;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 当以医生角色登录APP时，新增会员将要提交的信息
     * @param member
     * @param doctorUniqueKey
     * @return
     */
    public static JSONObject getAddMemberByDoctorJson(Member member, String doctorUniqueKey) {
        if(member != null && StringUtil.isNotEmpty(doctorUniqueKey)) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("doctorUniqueKey", doctorUniqueKey);
                obj.put("operator_type",OperatorType.BY_DOCTOR.ordinal());
                fillJsonWithMember(member,obj);
                return obj;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 当患者使用APP自我注册新增会员时将要提交的信息
     * @param member
     * @return
     */
    public static JSONObject getAddMemberBySelf(Member member) {
        if(member != null) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("operator_type",OperatorType.BY_SELF.ordinal());
                fillJsonWithMember(member,obj);
                JSONArray array = new JSONArray();
                return obj;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 获取新增会员记录提交的信息
     * @param record
     * @param nurseUniqueKey
     * @return
     */
    public static JSONObject getAddMemberRecordJson(MemberRecord record, String nurseUniqueKey) {
        if(record != null && StringUtil.isNotEmpty(nurseUniqueKey)) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("orgRoot",CommConst.SUFIX);
                obj.put("nurseUniqueKey", nurseUniqueKey);
                obj.put("mUniqueKey", record.getmUniqueKey());
                record.setCreateTime(new Date(System.currentTimeMillis()));
                long times = record.getCreateTime().getTime();
                Log.d(TAG, "times = " + times);
                obj.put("createTime", times);
                return obj;
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取自我检测记录提交的信息
     * @param record
     * @return
     */
    public static JSONObject getAddMemberRecordJsonBySelf(MemberRecord record) {
        if(record != null ) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("operator_type",OperatorType.BY_SELF.ordinal());
                obj.put("mUniqueKey", record.getmUniqueKey());
                record.setCreateTime(new Date(System.currentTimeMillis()));
                long times = record.getCreateTime().getTime();
                Log.d(TAG, "times = " + times);
                obj.put("createTime", times);
                return obj;
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 修改会员时，获取将要提交的信息
     * @param user
     * @return
     */
    public static JSONObject getUpdateDoctorJson(RoleUser user) {
        if(user != null) {
            try{
                JSONObject obj = new JSONObject();
                obj.put("doctorUniqueKey", user.getUniqueKey());
                obj.put("userName", user.getName());
                obj.put("sex", user.getSex());
                obj.put("brithday", user.getBrithday());
                return obj;
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取搜索Json
     * @param faUniqueKey
     * @param searchName
     * @param searchMobile
     * @param searchIdNumber
     * @return
     */
    public static JSONObject getSearchMemberJson(String faUniqueKey, String searchName, String searchMobile,
                                                 String searchIdNumber) {
        if(StringUtil.isNotEmpty(faUniqueKey) && (StringUtil.isNotEmpty(searchName) || StringUtil.isNotEmpty(searchMobile)
            || StringUtil.isNotEmpty(searchIdNumber))) {
            try{
                JSONObject obj = new JSONObject();
                obj.put("faUniqueKey", faUniqueKey);
                obj.put("searchName", searchName);
                obj.put("searchMobile", searchMobile);
                obj.put("searchIdNumber", searchIdNumber);
                return obj;
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
