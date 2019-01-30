package com.cnnet.otc.health.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cnnet.otc.health.MainActivity;
import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.activities.AddMemberActivity;
import com.cnnet.otc.health.bean.Member;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.managers.Downloading_Member_Status;
import com.cnnet.otc.health.managers.JsonManager;
import com.cnnet.otc.health.managers.MemberManager;
import com.cnnet.otc.health.managers.RequestManager;
import com.cnnet.otc.health.util.DialogUtil;
import com.cnnet.otc.health.util.NetUtil;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.util.ToastUtil;
//import com.cnnet.otc.health.views.EmptyLayout;
import com.cnnet.otc.health.views.adapter.MemberListAdapter;
//import com.njw.xlistview.library.XListView;

import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by SZ512 on 2016/1/9.
 */
public class MemberFragment extends Fragment implements
        MemberListAdapter.OnClickCheckBoxListener,AdapterView.OnItemClickListener {

    private final String TAG = "MemberFragment";

    private boolean autoConnectServer = true;
    private View root;
    @Bind(R.id.fg_member_bar_top)
    LinearLayout memberTopBar;
    @Bind(R.id.fg_member_search_bar_top)
    LinearLayout memberSearchBar;
    @Bind(R.id.bt_search_member)
    Button btnSearchMember;
    @Bind(R.id.bt_quit_search_member)
    Button btnQuitSearch;
    @Bind(R.id.bt_add_member)
    Button btnAddMember;
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.et_search)
    EditText etSearch;
    @Bind(R.id.fg_downloading_bar_top)
    LinearLayout fg_downloading_bar_top;
    MemberListAdapter adapter;
    //private EmptyLayout emptyLayout;
    private int cnt_per_load=20;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "AAAAAAAAAA____onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "AAAAAAAAAA____onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "AAAAAAAAAA____onCreateView");
        return initView(inflater, container);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "AAAAAAAAAA____onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "AAAAAAAAAA____onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "AAAAAAAAAA____onResume");
        if (SysApp.getAccountBean() == null ||
                SysApp.getAccountBean().getRole() == CommConst.FLAG_USER_ROLE_MEMBER) {
            //emptyLayout.setErrorLayout(R.string.empty_message, null);
        } else {
            //DialogUtil.loadProgress(getActivity(), getString(R.string.loading));
            displayMemberInfo();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "AAAAAAAAAA____onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "AAAAAAAAAA____onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "AAAAAAAAAA____onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AAAAAAAAAA____onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "AAAAAAAAAA____onDetach");
        DialogUtil.cancelDialog();
        ButterKnife.unbind(this);
    }



    private View initView(LayoutInflater inflater, ViewGroup container) {
        if(root == null) {
            root = inflater.inflate(R.layout.fragment_member, container, false);
            ButterKnife.bind(this, root);
            btnAddMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddMemberActivity.class);
                    startActivityForResult(intent, CommConst.INTENT_REQUEST_ADD_MEMBER);
                }
            });
            adapter = new MemberListAdapter(getActivity(), null, this);
            //adapter.setOnClisItemCheckBoxListener(this);
            listview = (ListView) root.findViewById(R.id.listview);
            //listview.setLis(this);
            //listview.setAutoLoad(true);
            //listview.setPullRefreshEnable(true);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(this);
            //emptyLayout = new EmptyLayout(getActivity(), listview);
            //emptyLayout = new EmptyLayout(getActivity());


            btnSearchMember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    memberTopBar.setVisibility(View.GONE);
                    memberSearchBar.setVisibility(View.VISIBLE);
                    etSearch.requestFocus();
                    inVisiableInput(etSearch, true);
                }
            });

            btnQuitSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    memberTopBar.setVisibility(View.VISIBLE);
                    memberSearchBar.setVisibility(View.GONE);
                    inVisiableInput(etSearch, false);
                    onRefresh();
                }
            });

            /*etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        String search_str=etSearch.getText().toString().trim();
                        if(!search_str.equals(""))
                        {
                            MemberManager manager=MemberManager.getInstance();
                            memberOffset=0;
                            manager.filter(search_str);
                            displayMemberInfo();
                        }
                        return true;
                    }
                    return false;
                }
            });*/

            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String search_str=etSearch.getText().toString().trim();
                    MemberManager manager=MemberManager.getInstance();
                    manager.setFiltered_member(manager.filter(search_str));
                    displayMemberInfo();
                }
            });

            /*etSearchLike.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_ENTER) {
                        String searchLike = etSearchLike.getText().toString().trim();
                        if (StringUtil.isNotEmpty(searchLike)) {
                            inVisiableInput(etSearchLike, false);
                            adapter.refreshData(SysApp.getMyDBManager().searchClientMember(SysApp.getAccountBean().getFaUniqueKey(), searchLike));
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm.isActive()) {
                                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });*/
        }
        return root;
    }

    /**
     * 隐藏键盘
     * @param view
     * @param isVisiable
     */
    private void inVisiableInput(View view, boolean isVisiable) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(isVisiable) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);  //表示强制显示
        } else {
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0); //强制隐藏键盘
            }
        }
    }

    /**
     * 当点击键盘上的搜索按钮时，开启搜索功能
     */
    /*private void startSearchMember() {
        String searchName = etSearchNameLike.getText().toString().trim();
        String searchMobile = etSearchMobileLike.getText().toString().trim();
        String searchIdNumber = etSearchIdNumberLike.getText().toString().trim();
        if (StringUtil.isNotEmpty(searchName) || StringUtil.isNotEmpty(searchMobile) || StringUtil.isNotEmpty(searchIdNumber)) {
            DialogUtil.loadProgress(getActivity(), getString(R.string.loading));
            inVisiableInput(etSearchMobileLike, false);
            listview.setAutoLoad(false);
            adapter.refreshData(SysApp.getMyDBManager().searchClientMember(SysApp.getAccountBean().getFaUniqueKey(),
                    searchName, searchMobile, searchIdNumber));
            RequestManager.searchMemberBySuperClinic(getContext(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Log.d(TAG, "startSearchMember : " + jsonObject.toString());
                    if(JsonManager.getCode(jsonObject) == 0) {
                        List<Member> lists = JsonManager.getMemberListByJson(jsonObject, true);
                        if(lists != null && lists.size() > 0) {
                            adapter.setData(lists, 1);
                        }
                    }
                    DialogUtil.cancelDialog();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    DialogUtil.cancelDialog();
                }
            }, SysApp.getAccountBean().getFaUniqueKey(), searchName, searchMobile, searchIdNumber);
        }
    }*/


    public void setMoreData(List<Member> more_members)
    {
        if(adapter!=null&&more_members.size()>0) {
            adapter.setNewData(more_members);
            adapter.notifyDataSetChanged();
        }
    }

    public void change_downloading_status(boolean is_downloading)
    {
        fg_downloading_bar_top.setVisibility(is_downloading? View.VISIBLE:View.GONE);
    }

    /**
     * 显示本地会员信息列表
     */
    private void displayMemberInfo() {
        MemberManager manager=MemberManager.getInstance();
        if(manager.getStatus()==Downloading_Member_Status.DOWNLOADING_INITIAL||manager.getStatus()==Downloading_Member_Status.IN_DOWNLOADING)
        {
            change_downloading_status(true);
        }
        else
        {
            change_downloading_status(false);
        }
        if(manager.getFiltered_member().size()<=0)
        {
            //emptyLayout.setErrorLayout(R.string.empty_message, null);
            return;
        }
        List<Member> members=manager.getFiltered_member();
        int size=members.size();
        /*if((size<CommConst.DB_DATA_PAGE)&&manager.getStatus()== Downloading_Member_Status.DOWNLOADING_ALL_DATA_READY)
        {
            listview.setAutoLoad(false);
        }*/
        adapter.setData(members);
        adapter.notifyDataSetChanged();
        //listview.stopLoadMore();
        //listview.stopRefresh();
        DialogUtil.cancelDialog();
    }

    //@Override
    public void onRefresh() {
        //listview.setAutoLoad(true);
        displayMemberInfo();
    }

    /*@Override
    public void onLoadMore() {
            DialogUtil.loadProgress(getActivity(), getString(R.string.loading));
            displayMemberInfo();
            //listview.stopLoadMore();
        //listview.stopRefresh();
            DialogUtil.cancelDialog();

    }*/

    public void onClick(int resId, boolean hadSelectAll) {
        switch (resId) {
            case R.id.select_all:
                adapter.setSelectAll(!hadSelectAll);
                break;
            case R.id.cancel:
                adapter.setSelectAll(false);
                break;
            case R.id.fl_add_insp:
                List<Member> list = adapter.getSelectAllMembers();
                /*if(list != null && list.size() > 0) {
                    for(Member member:list) {
                        SysApp.getMyDBManager().addWaitForInspector(member.getUniqueKey(),
                                SysApp.getAccountBean().getName(), SysApp.getAccountBean().getUniqueKey());
                    }
                }*/
                Log.d(TAG, "uniqueKey = " + SysApp.getAccountBean().getUniqueKey());
                SysApp.getMyDBManager().addWaitFroInsList(list,SysApp.getAccountBean().getName(), SysApp.getAccountBean().getUniqueKey());
                ((MainActivity) getActivity()).changeFramgment(R.id.fl_home);
                adapter.setSelectAll(false);
                break;
            case  R.id.fl_delele_member:
                if(SysApp.LOGIN_STATE!=CommConst.FLAG_USER_STATE_OFFLINE_LOGIN) {
                    ToastUtil.TextToast(this.getContext(), R.string.TOAST_HOSET_CONNECT, 2000);
                    return;
                }
                DialogUtil.Confirm(this.getContext(), R.string.dialog_alert_title, R.string.dialog_delete_record_item,
                        R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                List<Member> list = adapter.getSelectAllMembers();
                                if(list != null && list.size() > 0) {
                                  for(Member member:list) {
                                        SysApp.getMyDBManager().deleteMember(member);
                                    }
                                    refreshList(SysApp.getMyDBManager().getClientMemberList(SysApp.getAccountBean().getFaUniqueKey(), 0));
                                }

                            }
                        }, R.string.cancel, null);
        }
    }

    void refreshList(List<Member> members)
    {
       /* int size = members.size();
        if(size < CommConst.DB_DATA_PAGE) {
            //over
            listview.setAutoLoad(false);
        }
        adapter.setData(members, memberOffset);
        memberOffset += size;*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == CommConst.INTENT_REQUEST_ADD_MEMBER) {
                //onRefresh();
                boolean isAddMemberOK=data.getBooleanExtra("isAddMemberOK",false);
                if(isAddMemberOK) {
                    ((MainActivity) getActivity()).changeFramgment(R.id.fl_home);
                    adapter.setSelectAll(false);
                }
                //displayMemberInfo(null);
            } else if(requestCode == CommConst.INTENT_REQUEST_SEARCH_MEMBER) {

            }
        }
    }

    @Override
    public void onClickItemCheckBox(int position, boolean isAll) {
        List<Member> list = adapter.getSelectAllMembers();
        if(list == null){
            ((MainActivity)getActivity()).onClickCheckBox(0, false);
        }else{
            int size = list.size();
            isAll = size == adapter.getCount();
            ((MainActivity)getActivity()).onClickCheckBox(size, isAll);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0 || position == adapter.getCount()+1){
            return ;
        }
        List<Member> memberList = adapter.getSelectAllMembers();
        if(memberList != null && memberList.size() >0){
            adapter.selectItem(position - 1);
        }
    }
}
