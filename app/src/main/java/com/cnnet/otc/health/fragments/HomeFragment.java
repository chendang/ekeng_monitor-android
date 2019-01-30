package com.cnnet.otc.health.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.activities.DetectBle2Activity;
import com.cnnet.otc.health.activities.DetectBle3Activity;
import com.cnnet.otc.health.activities.DetectBle4Activity;
import com.cnnet.otc.health.activities.DetectBle5Activity;
import com.cnnet.otc.health.bean.Member;
import com.cnnet.otc.health.comm.CheckType;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.db.MyDBManager;
import com.cnnet.otc.health.interfaces.SubmitServerListener;
import com.cnnet.otc.health.services.BluetoothService;
import com.cnnet.otc.health.tasks.UploadAllNewInfoTask;
import com.cnnet.otc.health.util.DensityUtil;
import com.cnnet.otc.health.util.DialogUtil;
import com.cnnet.otc.health.util.ToastUtil;
import com.cnnet.otc.health.views.MyGridView;
import com.cnnet.otc.health.views.adapter.MyDeviceGridAdapter;
import com.cnnet.otc.health.views.adapter.WaitInsMemberListAdapter;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by SZ512 on 2016/1/9.
 */
public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener{

    private final String TAG = "HomeFragment";
    private Activity mActivity;
    private Context ctx;
    private View root;
    private MyGridView gvDevice = null;
    private MyDeviceGridAdapter deviceAdapter;


    @Bind(R.id.listview)
    SwipeMenuListView listView;

    private WaitInsMemberListAdapter adapter;
    private Member nowWaitMember = null;
    private boolean isLoading = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.ctx = activity;
        this.mActivity = activity;
        Log.d(TAG, "HomeFragment____onAttach");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "HomeFragment____onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "HomeFragment____onCreateView");
        if(root == null) {
            root = inflater.inflate(R.layout.fragment_home, container, false);
            ButterKnife.bind(this, root);
            initView();
        }
        return root;
    }

    private void SwipeMenuList() {
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(DensityUtil.dp2px(getActivity(), 90));
                // set a icon
                deleteItem.setIcon(R.mipmap.ic_del);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        listView.setMenuCreator(creator);
        // LEFT 左滑菜单
        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        // step 2. 绑定左滑和删除事件
        listView.setOnMenuItemClickListener(
                new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                        //ApplicationInfo item = mAppList.get(position);
                        switch (index) {
                            case 0:
                                // delete
                                //delete(item);
                                DialogUtil.Confirm(getActivity(), R.string.dialog_alert_title, R.string.dialog_delete_wait_ins_record,
                                        R.string.confirm, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Member member = (Member) adapter.getItem(position);
                                                boolean isDel = SysApp.getMyDBManager().deleteWaitInspectorRecord(SysApp.getAccountBean().getUniqueKey(), member.getNative_record_id());
                                                if (isDel) {
                                                    adapter.remove(position);
                                                    nowWaitMember = null;
                                                    deviceAdapter.setNowRecordedDevice(0);
                                                }
                                            }
                                        }, R.string.cancel, null);
                                break;
                        }
                        // false : close the menu; true : not close the menu
                        return false;
                    }
                });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        SwipeMenuList();
        root.findViewById(R.id.submit_record_info).setOnClickListener(this);
        gvDevice = (MyGridView) root.findViewById(R.id.fg_home_gv_device);
        deviceAdapter = new MyDeviceGridAdapter(ctx);
        //配置适配器
        gvDevice.setAdapter(deviceAdapter);
        gvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, Object> items = (Map<String, Object>) parent.getItemAtPosition(position);
                if (items != null) {
                    int type = (int) (items.get("image"));
                    if(type != 0) {
                        if(nowWaitMember != null ) {
                            Intent intent = new Intent(ctx, DetectBle3Activity.class);
                            switch (type) {
                                case R.drawable.btn_xt_selector://血糖
                                    SysApp.check_type = CheckType.BLOOD_GLUCOSE;
                                    intent = new Intent(ctx, DetectBle2Activity.class);
                                    break;
                                case R.drawable.btn_bp_selector://血压
                                    SysApp.check_type = CheckType.BLOOD_PRESSURE;
                                    break;
                                case R.drawable.btn_tp_selector: //体温
                                    SysApp.check_type = CheckType.THERMOMETER;
                                    break;
                                case R.drawable.btn_xy_selector: //血氧
                                    SysApp.check_type = CheckType.OXIMETRY;
                                    intent = new Intent(ctx, DetectBle4Activity.class);
                                    intent.putExtra(CommConst.INTENT_EXTRA_KEY_HAS_REAL, true);
                                    break;
                                case R.drawable.btn_xz_selector://血脂
                                    SysApp.check_type = CheckType.LIPID;
                                    intent = new Intent(ctx, DetectBle5Activity.class);
                                    break;
                                case R.drawable.btn_tz_selector://体重
                                    SysApp.check_type = CheckType.WEIGHT;
                                    break;
                                case R.drawable.btn_ns_selector: //尿酸
                                    SysApp.check_type = CheckType.URIC_ACID;
                                    break;
                            }
                            intent.putExtra(CommConst.INTENT_EXTRA_KEY_MEMBER_UNIQUEKEY, nowWaitMember.getUniqueKey());
                            intent.putExtra(CommConst.INTENT_EXTRA_KEY_NATIVE_RECORD_ID, nowWaitMember.getNative_record_id());
                            startActivityForResult(intent, CommConst.INTENT_REQUEST_DETECT);
                        } else {
                            try {
                                ToastUtil.TextToast(getActivity().getApplicationContext(), "请先选择待检会员", 2000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        initList();
    }
    /*********待检人员*********/
    private void initList() {
        adapter = new WaitInsMemberListAdapter(mActivity);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("选择操作");
                menu.add("从待检列表移除");
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem itm)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) itm.getMenuInfo();
        if(itm.getItemId()==0)
        {
            Member member=(Member)adapter.getItem(info.position);
            SysApp.getMyDBManager().removeFromWaitInspectorMemberList(member);
            adapter.setCheckPosition(-1);
            refreshUI();
        }
        return true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "HomeFragment____onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "HomeFragment____onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshUI();
        Log.d(TAG, "HomeFragment____onResume");
    }

    private void refreshUI() {
        try {
            adapter.setData(SysApp.getMyDBManager().getWaitInspectorMemberList(SysApp.getAccountBean().getUniqueKey()));
            deviceAdapter.setNowRecordedDevice(nowWaitMember == null ? 0 : nowWaitMember.getNative_record_id());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "HomeFragment____onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "HomeFragment____onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "HomeFragment____onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "HomeFragment____onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ButterKnife.unbind(this);
        Log.d(TAG, "HomeFragment____onDetach");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CommConst.INTENT_REQUEST_DETECT) {
            boolean isDetected = data.getBooleanExtra(CommConst.INTENT_EXTRA_KEY_IS_DETECTED, false);
            if(isDetected) { //检查记录改变时，刷新检查项
                deviceAdapter.setNowRecordedDevice(nowWaitMember.getNative_record_id());
            }
            SysApp.btDevice = null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(!isLoading) {
            Member item = (Member) adapter.getItem(position);
            //当选中的待检人员为空，或者和当前检查人员不一样时，不做操作
            if(nowWaitMember == null || nowWaitMember.getNative_record_id() != item.getNative_record_id()) {
                nowWaitMember = item;
                adapter.setCheckPosition(position);
                deviceAdapter.setNowRecordedDevice(nowWaitMember.getNative_record_id());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_record_info:
                if(!isLoading) {
                    if(nowWaitMember != null) {
                        isLoading = true;
                        DialogUtil.loadProgress(getActivity(), null);

                    /*if (SysApp.getMyDBManager().submitRecordInfo(SysApp.getAccountBean().getUniqueKey(), nowWaitMember.getId())) {
                        adapter.setData(SysApp.getMyDBManager().getWaitInspectorMemberList(SysApp.getAccountBean().getUniqueKey()));
                    }*/
                        UploadAllNewInfoTask.submitOneRecordInfo(getActivity(), SysApp.getAccountBean().getUniqueKey(), nowWaitMember.getNative_record_id(),
                                new SubmitServerListener() {
                                    @Override
                                    public void onResult(int result) {
                                        isLoading = false;
                                        DialogUtil.cancelDialog();
                                        if (result == 0) { //success
                                            adapter.setCheckPosition(-1);
                                            nowWaitMember = null;
                                        } else if(result == -2){
                                            ToastUtil.TextToast(getActivity().getApplicationContext(), "请先至少检查一项...", 2000);
                                        } else {
                                            adapter.setCheckPosition(-1);
                                            nowWaitMember = null;
                                            ToastUtil.TextToast(getActivity().getApplicationContext(), "提交失败，请检查网络是否正常...", 2000);
                                        }
                                        refreshUI();
                                    }
                                });
                    } else {
                        ToastUtil.TextToast(getActivity().getApplicationContext(), "请先选中一个会员...", 2000);
                    }
                }
                break;
        }
    }

    @OnClick(R.id.unregister)
    public void unRegister() {
        DialogUtil.Confirm(getActivity(), R.string.dialog_alert_title, R.string.device_un_register_info,
                R.string.confirm, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SysApp.getSpManager().clearLoginInfo();
                        try {
                            BluetoothService.disConnect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        BluetoothAdapter mBtAdapter = BluetoothAdapter
                                .getDefaultAdapter();
                        mBtAdapter.disable();
                        SysApp.exitApp();
                        getActivity().finish();
                        System.exit(0);
                    }
                }, R.string.cancel, null);

    }
}
