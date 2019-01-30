package com.cnnet.otc.health.activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.foxchen.ekengmonitor.R;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.cnnet.otc.health.bean.Member;
import com.cnnet.otc.health.bean.RoleUser;
import com.cnnet.otc.health.comm.BaseActivity;
import com.cnnet.otc.health.comm.CommConst;
import com.cnnet.otc.health.comm.SysApp;
import com.cnnet.otc.health.managers.JsonManager;
import com.cnnet.otc.health.managers.RequestManager;
import com.cnnet.otc.health.util.DialogUtil;
import com.cnnet.otc.health.util.ImageUtils;
import com.cnnet.otc.health.util.NetUtil;
import com.cnnet.otc.health.util.StringUtil;
import com.cnnet.otc.health.util.ToastUtil;
import com.cnnet.otc.health.util.ValidatorUtil;
import com.herily.dialog.HerilyAlertDialog;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * Created by SZ512 on 2016/1/19.
 */
public class AddMemberActivity extends BaseActivity implements View.OnClickListener {

    private final String TAG = "AddMemberActivity";

    @Bind(R.id.tr_with_doctor)
    protected TableRow trWithDoctor;
    @Bind(R.id.sp_add_member_withDoctor)
    protected Spinner spWithDoctor;
    @Bind(R.id.iv_add_member_head)
    protected ImageView ivMemberHead;
    @Bind(R.id.add_member_back_btn)
    protected ImageView ivBackBtn;
    @Bind(R.id.et_add_member_name)
    protected EditText mName;
    @Bind(R.id.sp_add_member_gender)
    protected Spinner mGender;
    @Bind(R.id.et_add_member_telephone)
    protected EditText mTelephone;
    @Bind(R.id.et_add_member_birthday)
    protected TextView mBirthday;
    @Bind(R.id.et_add_member_landline)
    protected EditText mLandline;
    @Bind(R.id.et_add_member_idnumber)
    protected EditText mIdnumber;
    @Bind(R.id.et_add_member_ssn)
    protected EditText mSSN;
    @Bind(R.id.et_add_member_address)
    protected EditText mAddress;
    @Bind(R.id.et_add_member_anamnesis)
    protected EditText mAnamnesis;

    private String[] doctorUniqueKeys = null;
    private List<String> doctorInfos = null;
    private String checkedDoctor = null;

    private String[] genderValues = null;
    private String genderValue = null;

    private DatePickerDialog dateDialog;
    private int year;
    private int month;
    private int dayOfMonth;

    private String cloudHeadPath = null;
    private String nativeHeadPath = null;

    private boolean startAdd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        if(SysApp.getAccountBean().getRole() == CommConst.FLAG_USER_ROLE_NURSE) {
            trWithDoctor.setVisibility(View.VISIBLE);
            initWithDoctorSpinner();
        }
        genderValues = this.getResources().getStringArray(R.array.gender_value);
        genderValue = genderValues[0];
        ivMemberHead.setOnClickListener(this);
        findViewById(R.id.tv_add_member_head_click).setOnClickListener(this);
        ivBackBtn.setOnClickListener(this);
    }

    private void initWithDoctorSpinner() {

        List<RoleUser> doctors = SysApp.getMyDBManager().getRoleUserList(SysApp.getAccountBean().getUniqueKey(), CommConst.FLAG_USER_ROLE_DOCTOR);
        if(doctors != null) {
            int length = doctors.size();
            doctorUniqueKeys = new String[length];
            doctorInfos = new ArrayList<>();
            for(int i = 0; i < length; i++) {
                RoleUser d = doctors.get(i);
                if(i == 0) {
                    checkedDoctor = d.getUniqueKey();
                }
                doctorUniqueKeys[i] = d.getUniqueKey();
                doctorInfos.add(d.getName() + "  " + (d.getJobTitle()==null?"":d.getJobTitle()) + "  " + (d.getJobPosition()==null?"":d.getJobPosition()));
            }
            // 建立Adapter并且绑定数据源
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, doctorInfos);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //绑定 Adapter到控件
            spWithDoctor .setAdapter(adapter);
            spWithDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int pos, long id) {
                    checkedDoctor = doctorUniqueKeys[pos];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Another interface callback
                    checkedDoctor = null;
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_member_head:
            case R.id.tv_add_member_head_click:
                showImagePickDialog();
                break;
            case R.id.add_member_back_btn:
                DialogUtil.Confirm(this, R.string.dialog_alert_title, R.string.dialog_exit_add_member,
                        R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }, R.string.cancel, null);
                break;
            case R.id.iv_add_member_confirm:
                
                break;
        }
    }

    @OnItemSelected(R.id.sp_add_member_gender)
    public void genderSelected(int position) {
        genderValue = genderValues[position];
        Log.d(TAG, "genderValue --- " + genderValue);
    }

    @OnClick(R.id.et_add_member_birthday)
    public void setDate() {
        if(dateDialog == null) {
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    String date = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                    mBirthday.setText(date);
                }
            }, year, month, dayOfMonth);
        }
        dateDialog.show();
    }

    /**
     * 新增会员
     */
    @OnClick(R.id.iv_add_member_confirm)
    public void confirmAddMember() {
        if(startAdd) {
            return;
        }
        String name = mName.getText().toString();
//        if(StringUtil.isNotEmpty(nativeHeadPath)) {
            if (StringUtil.isNotEmpty(name)) {
                String telephone = mTelephone.getText().toString();
                if (StringUtil.isNotEmpty(telephone)) {
                    if (ValidatorUtil.isMobile(telephone)) {
                        String birthday = mBirthday.getText().toString();
                        if (StringUtil.isNotEmpty(birthday)) {
                            if (SysApp.getAccountBean().getRole() == CommConst.FLAG_USER_ROLE_NURSE
                                    && StringUtil.isEmpty(checkedDoctor)) {  //当角色为护士时，医生不能为空
                                ToastUtil.TextToast(this, R.string.doctor_is_not_null, 2000);
                                return;
                            }
                            Member member = new Member();
                            member.setName(name);
                            member.setmHeadPath(cloudHeadPath);
                            member.setmNativeHeadPath(nativeHeadPath);
                            member.setmPhone(telephone);
                            member.setmBrithday(birthday);
                            member.setSex(genderValue);
                            member.setmTel(mLandline.getText().toString());
                            member.setmAddress(mAddress.getText().toString());
                            member.setmAnamnesis(mAnamnesis.getText().toString());
                            member.setmIdNumber(mIdnumber.getText().toString());
                            member.setmSSN(mSSN.getText().toString());
                            member.setUsername(telephone);
                            member.setState(0);
                            member.setWithDoctor(checkedDoctor);
                            member.setFaUniqueKey(SysApp.getAccountBean().getFaUniqueKey());
                            member.setAddUniqueKey(SysApp.getAccountBean().getUniqueKey());
                            member.setmCreatePersion(SysApp.getAccountBean().getName());
                            addMemberByOther(member);
                        } else {
                            ToastUtil.TextToast(this, R.string.birthday_is_not_null, 2000);
                            mBirthday.requestFocus();
                        }
                    } else {
                        ToastUtil.TextToast(this, R.string.is_not_mobile, 2000);
                    }
                } else {
                    ToastUtil.TextToast(this, R.string.mobile_is_not_null, 2000);
                }
                mTelephone.requestFocus();
            } else {
                ToastUtil.TextToast(this, R.string.name_is_not_null, 2000);
                mName.requestFocus();
            }
//        } else {
//            ToastUtil.TextToast(this, R.string.head_image_is_not_null, 2000);
//        }
    }

    /**
     * 调用新增接口
     * @param member
     */
    private void addMemberByOther(final Member member) {
        startAdd = true;
        if(!SysApp.getMyDBManager().hasSamePhoneMember(member) && SysApp.getMyDBManager().addMember(member)) {
            if(NetUtil.checkNetState(this)) {
                if (SysApp.getAccountBean().getRole() == CommConst.FLAG_USER_ROLE_NURSE) {
                    RequestManager.addMemberByNurse(this, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            submitReponse(jsonObject, member);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            ToastUtil.TextToast(getContext(), R.string.network_error, 2000);
                            finishSendBack();
                        }
                    }, member, SysApp.getAccountBean().getUniqueKey());
                } else if (SysApp.getAccountBean().getRole() == CommConst.FLAG_USER_ROLE_DOCTOR) {
                    RequestManager.addMemberByDoctor(this, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            submitReponse(jsonObject, member);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            ToastUtil.TextToast(getContext(), R.string.network_error, 2000);
                            finishSendBack();
                        }
                    }, member, SysApp.getAccountBean().getUniqueKey());
                } else {
                    ToastUtil.TextToast(this, R.string.no_permission, 2000);
                    finishSendBack();
                }
            } else {
                ToastUtil.TextToast(this, R.string.native_save_member_success, 2000);
                finishSendBack();
            }
        } else {
            ToastUtil.TextToast(this, R.string.native_add_member_has_same, 2000);
            startAdd =false;
        }
        DialogUtil.cancelDialog();
    }

    /**
     * 提交返回成功
     * @param result
     * @param member
     */
    private void submitReponse(JSONObject result, Member member) {
        if (result != null && JsonManager.getCode(result) == 0) {
            Member mem = JsonManager.getAddMemberSuccessInfoFromJson(result, member);
            if(mem != null) {
                if(SysApp.getMyDBManager().updateMemberKeyCreatTime(mem)) {
                    finishSendBack();
                    return;
                }
            }
            SysApp.getMyDBManager().deleteMemberByCreateTime(member.getmCreateTime());
            ToastUtil.TextToast(getContext(), R.string.native_update_member_failed, 2000);
        } else {
            ToastUtil.TextToast(getContext(), R.string.cloud_add_member_failed, 2000);
        }
        startAdd = false;
    }

    private void finishSendBack() {
        startAdd = false;
        setResult(RESULT_OK, getIntent());
        finish();
    }

    /**
     * 显示头像选择方式对话框
     */
    private void showImagePickDialog() {
        String[] choices = new String[]{getString(R.string.camera_img), getString(R.string.local_img)};

        new HerilyAlertDialog.Builder(this)
                .setItems(choices, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                ImageUtils.openCameraImage(AddMemberActivity.this);
                                break;
                            case 1:
                                ImageUtils.openLocalImage(AddMemberActivity.this);
                                break;
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            // 拍照获取图片
            case ImageUtils.GET_IMAGE_BY_CAMERA:

                if(ImageUtils.imageUriFromCamera != null) {
                    //ImageUtils.cropImage(this, ImageUtils.imageUriFromCamera);

                    Intent intent3=new Intent(this, ClipActivity.class);
                    intent3.putExtra("path", ImageUtils.imageUriFromCamera.toString());
                    startActivityForResult(intent3, ImageUtils.CROP_IMAGE);
                }

                break;
            // 手机相册获取图片
            case ImageUtils.GET_IMAGE_FROM_PHONE:
                if(data != null && data.getData() != null) {
                    //ImageUtils.cropImage(this, data.getData());
                    Intent intent3=new Intent(this, ClipActivity.class);
                    intent3.putExtra("path", data.getData().toString());
                    startActivityForResult(intent3, ImageUtils.CROP_IMAGE);
                }
                break;
            // 裁剪图片后结果
            case ImageUtils.CROP_IMAGE:
                nativeHeadPath = data.getStringExtra(CommConst.INTENT_EXTRA_KEY_NATIVE_HEAD_PATH);
                cloudHeadPath = data.getStringExtra(CommConst.INTENT_EXTRA_KEY_CLOUD_HEAD_PATH);
                Log.d(TAG, "nativeHeadPath = " + nativeHeadPath + "\n cloudHeadPath = " + cloudHeadPath);
                ivMemberHead.setImageBitmap(ImageUtils.getBitmapFromUrl(nativeHeadPath));
                //ImageUtils.setHeadImg(SysApp.currentAccount.getFigure(), ivMemberHead);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
