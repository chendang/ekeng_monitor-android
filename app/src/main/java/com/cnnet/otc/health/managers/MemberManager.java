package com.cnnet.otc.health.managers;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cnnet.otc.health.bean.Member;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;

import android.content.Context;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import de.greenrobot.event.EventBus;
import com.cnnet.otc.health.events.MembersObtainedEvent;

/**
 * Created by Administrator on 2017/4/10.
 */
public class MemberManager {
    static MemberManager manager=null;
    private final String TAG = "MemberManager";
    private Downloading_Member_Status status=Downloading_Member_Status.DOWNLOADING_INITIAL;
    private String err_msg="";
    public static MemberManager getInstance()
    {
        if(manager==null)
        {
            manager=new MemberManager();
        }
        return manager;
    }
    private MemberManager() { }


    List<Member> all_members=new ArrayList<Member>();
    private List<Member> filtered_member=new ArrayList<Member>();
   // private List<Member> filtered_members=new ArrayList<Member>();

    int page_per_download=20;
    String search_str="";
    int sn=0;
    long last_start_tm=-1;

    public List<Member> filter(String search_str,List<Member> more_members)
    {
        if(search_str.trim().equals("")||search_str.trim().equals("输入姓名、拼音首字符、或电话"))
        {
            return more_members;
        }
        List<Member> filtered_more_members=new ArrayList<Member>();
        for(Member member:more_members)
        {
            String pinyin = member.getmNamePinyin().toLowerCase();
            String search_string = search_str.toLowerCase();
            if (pinyin != null && pinyin.indexOf(search_string) >= 0)
            {
                filtered_more_members.add(member);
            }
            if(member.getName()!=null&&member.getName().indexOf(search_str)>=0)
            {
                filtered_more_members.add(member);
            }
            if(member.getmPhone()!=null&&member.getmPhone().indexOf(search_str)>=0)
            {
                filtered_more_members.add(member);
            }
            if(member.getmIdNumber()!=null&&member.getmIdNumber().indexOf(search_str)>0)
            {
                filtered_more_members.add(member);
            }
        }
        return filtered_more_members;
    }

    public List<Member> filter()
    {
        return this.filter(this.search_str);
    }

    public List<Member> filter(String search_str)
    {

        List<Member> filtered_members=new ArrayList<Member>();
        if(search_str.trim().equals("")||search_str.trim().equals("输入姓名、拼音首字符、或电话"))
        {
            return this.all_members;
        }
        for(Member member:this.all_members)
        {
            String pinyin = member.getmNamePinyin().toLowerCase();
            String search_string = search_str.toLowerCase();
            this.search_str=search_string;
            if (pinyin != null && pinyin.indexOf(search_string) >= 0)
            {
                filtered_members.add(member);
            }
            if(member.getName()!=null&&member.getName().indexOf(search_str)>=0)
            {
                filtered_members.add(member);
            }
            if(member.getmPhone()!=null&&member.getmPhone().indexOf(search_str)>=0)
            {
                filtered_members.add(member);
            }
            if(member.getmIdNumber()!=null&&member.getmIdNumber().indexOf(search_str)>0)
            {
                filtered_members.add(member);
            }
        }
        return filtered_members;
    }


    public void fetchMemberData(final Context ctx)
    {
        long tm=new Date().getTime();
        if(last_start_tm!=-1)
        {
            if(tm-last_start_tm<1000)
            {
                return;
            }
            else
            {
                last_start_tm=tm;
            }
        }
        this.setStatus(Downloading_Member_Status.IN_DOWNLOADING);
        this.setFiltered_member(new ArrayList<Member>());
        this.all_members=new ArrayList<Member>();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                sn++;
                startGetMemberData(ctx);
            }
        });
        thread.start();
    }

    /**
     * 获取会员数据
     */
    private void startGetMemberData(final Context ctx) {
        if(SysApp.LOGIN_STATE==CommConst.FLAG_USER_STATE_LOGIN) {
        RequestManager.getMemberVersion(ctx, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject jsonObject) {
                    Log.d(TAG, "result verison : " + jsonObject.toString());
                    if (jsonObject != null) {
                        requestServerGetMember(JsonManager.getVersion(jsonObject), ctx);
                        return;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    err_msg="获取会员版本失败";
                    EventBus.getDefault().post(MembersObtainedEvent.createDownloadingErrEvt(err_msg));
                    Log.d(TAG, "downloading error" );
                }
            }, SysApp.getAccountBean().getUniqueKey());
        }
        else
        {
            loadLocalMembers();
        }
    }
    /**
     * 请求服务器获取会员列表
     * @param cloud_version
     */
    private void requestServerGetMember(final long cloud_version,final Context ctx){
        long clientVersion = SysApp.getMyDBManager().getDataVersionByType(SysApp.getAccountBean().getFaUniqueKey(), CommConst.FLAG_USER_ROLE_MEMBER);
        Log.d(TAG, "clientVersion : " + clientVersion);
        if(cloud_version==-1||(clientVersion <= cloud_version)||SysApp.NEED_REFRESH_MEMBER_LIST) {

            if (SysApp.getAccountBean().getRole() == CommConst.FLAG_USER_ROLE_DOCTOR) {
                    requestServerGetMemberByDoctor(clientVersion, ctx,0,sn);
                } else {
                    requestServerGetMemberByNurse(clientVersion, ctx,0,sn);
                }

            SysApp.NEED_REFRESH_MEMBER_LIST=false;
        } else {
            loadLocalMembers();
        }
    }


    /**
     * 医生：请求服务器获取会员列表
     */
    private void requestServerGetMemberByDoctor(final long clientVersion,final Context ctx,final int offset ,final int cur_sn ) {
        RequestManager.getMemberGroupByDoctor(ctx, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.d(TAG, "result --- " + jsonObject.toString());
                if(cur_sn!=sn)
                {
                    return;
                }
                if (jsonObject != null && JsonManager.getCode(jsonObject) == 0) {
                    List<Member> members=JsonManager.saveMemberInfo(jsonObject);
                    boolean mayHasMore=(members.size()>=page_per_download);
                    all_members.addAll(members);
                    List<Member> the_filtered_members=filter(search_str,members);
                    if(the_filtered_members!=null&&the_filtered_members.size()>0) {
                        getFiltered_member().addAll(the_filtered_members);
                        EventBus.getDefault().post(MembersObtainedEvent.createDataObtainEvt(the_filtered_members ));
                    }
                    if(mayHasMore) {
                        int new_offset=offset+page_per_download;
                        requestServerGetMemberByDoctor(clientVersion,ctx,new_offset,cur_sn);
                    }
                    else
                    {
                        status=Downloading_Member_Status.DOWNLOADING_ALL_DATA_READY;
                        EventBus.getDefault().post(MembersObtainedEvent.createMembersReadyEvt());
                    }
            }
        }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(cur_sn!=sn)
                {
                    return;
                }
                err_msg=volleyError.toString();
                status=Downloading_Member_Status.DOWNLOADING_ERROR;
                EventBus.getDefault().post(MembersObtainedEvent.createDownloadingErrEvt(err_msg));
                Log.d(TAG, "GetMemberByDoctor Error : " + volleyError.toString());
            }
        }, SysApp.getAccountBean().getUniqueKey(), String.valueOf(clientVersion),String.valueOf(offset));
    }

    /**
     * 护士：请求服务器获取会员列表
     * @param clientVersion
     */
    private void requestServerGetMemberByNurse(final long clientVersion,final Context ctx,final int offset ,final int cur_sn) {
        final long start_tm=new Date().getTime();
        RequestManager.getMemberGroupByLower(ctx, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                if(cur_sn!=sn)
                {
                    return;
                }
                Log.d(TAG, "result : " + jsonObject.toString());
                if (jsonObject != null && JsonManager.getCode(jsonObject) == 0) {
                    long end_tm=new Date().getTime();
                    Log.d(TAG, "duration : " + (end_tm-start_tm)+"\n");
                    List<Member> members=JsonManager.saveMemberInfo(jsonObject);
                    boolean mayHasMore=(members.size()>=page_per_download);
                    all_members.addAll(members);
                    List<Member> the_filtered_members=filter(search_str,members);
                    if(the_filtered_members!=null&&the_filtered_members.size()>0) {
                        getFiltered_member().addAll(the_filtered_members);
                        EventBus.getDefault().post(MembersObtainedEvent.createDataObtainEvt(the_filtered_members));
                    }
                    //filter(search_str);
                    if(mayHasMore) {
                        int new_offset=offset+page_per_download;
                        requestServerGetMemberByNurse(clientVersion,ctx,new_offset,cur_sn);
                    }
                    else
                    {
                        status=Downloading_Member_Status.DOWNLOADING_ALL_DATA_READY;
                        EventBus.getDefault().post(MembersObtainedEvent.createMembersReadyEvt());
                    }
                }
                else
                {
                    err_msg="下载病人出现错误";
                    status=Downloading_Member_Status.DOWNLOADING_ERROR;
                    EventBus.getDefault().post(MembersObtainedEvent.createDownloadingErrEvt(err_msg));
                    Log.d(TAG, "download member error: " );
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(cur_sn!=sn)
                {
                    return;
                }
                err_msg=volleyError.toString();
                status=Downloading_Member_Status.DOWNLOADING_ERROR;
                EventBus.getDefault().post(MembersObtainedEvent.createDownloadingErrEvt(err_msg));
                Log.d(TAG, "GetMemberByNurse Error : " + volleyError.toString());
            }
        }, SysApp.getAccountBean().getUniqueKey(), String.valueOf(clientVersion),String.valueOf(offset));
    }

    /*bool isUpdateLocalStoped;
    void updateLocalDB(Object remote_member_obj)
    {
        List<Member> remote_members = (List<Member>)remote_member_obj;
        isUpdateLocalStoped=false;
        foreach(Member remote_member in remote_members)
        {
            if (isUpdateLocalStoped)
            {
                break;
            }
            remote_member.saveToLocal();
        }
    }*/

    /*public void syncMembersFromServer()
    {
        if (getStatus() == Downloading_Member_Status.IN_DOWNLOADING)
            return;
        setStatus(Downloading_Member_Status.IN_DOWNLOADING);
        isUpdateLocalStoped = false;
        Thread thread = new Thread(new ThreadStart(getusers_thread));
        thread.Start();
    }*/

    private void loadLocalMembers()
    {
        List<Member> members = SysApp.getMyDBManager().getClientMemberList(SysApp.getAccountBean().getFaUniqueKey(), 0);
        List<Member> the_filtered_members=filter(search_str,members);
        if(the_filtered_members!=null&&the_filtered_members.size()>0) {
            getFiltered_member().addAll(the_filtered_members);
            EventBus.getDefault().post(MembersObtainedEvent.createDataObtainEvt(the_filtered_members));
        }
        EventBus.getDefault().post(MembersObtainedEvent.createMembersReadyEvt());
        //this.all_members = filtered_members=members;
    }

    /*public void syncLocalMembers()
    {
        if (getStatus() == Downloading_Member_Status.IN_DOWNLOADING)
            return;
        setStatus(Downloading_Member_Status.IN_DOWNLOADING);
        Thread thread = new Thread(new ThreadStart(retrieveLocalMembers));
        thread.Start();
    }*/

    public void addNewMember(Member member)
    {
        this.all_members.add(0, member);
        List<Member> members=new ArrayList<Member>();
        members.add(member);
        List<Member> the_filtered_members=filter(search_str,members);
        if(the_filtered_members!=null&&the_filtered_members.size()>0) {
            getFiltered_member().addAll(the_filtered_members);
            EventBus.getDefault().post(MembersObtainedEvent.createDataObtainEvt(the_filtered_members ));
        }
    }

    public List<Member> getFiltered_member() {
        return filtered_member;
    }

    public void setFiltered_member(List<Member> filtered_member) {
        this.filtered_member = filtered_member;
    }

    public Downloading_Member_Status getStatus() {
        return status;
    }

    public void setStatus(Downloading_Member_Status status) {
        this.status = status;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    /*public void delMember(Member member,Context ctx)
    {
        if(SysApp.LOGIN_STATE==CommConst.FLAG_USER_STATE_LOGIN)
        {
            for (Member filtered_member : filtered_members)
            {
                if (filtered_member.getUniqueKey().equals(member.getUniqueKey()))
                {
                    filtered_members.remove(filtered_member);
                    break;
                }
            }
            for (Member the_member : all_members)
            {
                if (the_member.getUniqueKey().equals(member.getUniqueKey()))
                {
                    all_members.remove(the_member);
                    break;
                }
            }
        }
        else
        {
            foreach (Member filtered_member in filtered_members)
            {
                if (filtered_member.Id.Equals(member.Id))
                {
                    filtered_members.Remove(filtered_member);
                    break;
                }
            }
            foreach (Member the_member in all_members)
            {
                if (the_member.Id == member.Id)
                {
                    all_members.Remove(the_member);
                    break;
                }
            }
        }
    }*/




}
