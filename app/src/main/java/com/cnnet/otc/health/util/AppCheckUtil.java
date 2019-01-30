package com.cnnet.otc.health.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.foxchen.ekengmonitor.R;
import com.cnnet.otc.health.comm.CommConst;

import java.util.List;

public class AppCheckUtil {
    private static final String TAG = "AppCheckUtil";

    private static boolean isShowDialog = false;
    private static AlertDialog dialog  ;

    /***
     * 检查APP是否是在后台
     * @param context
     * @return
     */
    public static boolean isAppOnBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (null != tasks && !tasks.isEmpty()&&  tasks.size() > 0) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getApplicationContext().getPackageName())) {
                return true;
            }
        }
        return false;

    }

    /**
     *
     * @param ctx
     * @param resultCode
     */
    public static void toastErrMsgByConnectResultCode(final Context ctx, int resultCode){
        if(ctx == null){
            return ;
        }
        Log.v(TAG, "resultCode:" + resultCode);
        if(!NetUtil.checkNetState(ctx)){
            if(null == dialog || !dialog.isShowing()){
                dialog  = DialogUtil.Confirm(ctx, null, ctx.getString(R.string.isSetNet), ctx.getString(R.string.confirm),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(android.provider.Settings.ACTION_SETTINGS);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                ctx.startActivity(i);
                            }
                        }, ctx.getString(R.string.cancel), cancel, null);
                return ;
            }
        }
        switch(resultCode){
            case CommConst.ERROR_CODE_LOGIN_USER_PWD_ERR:
                ToastUtil.TextToast(ctx, ctx.getString(R.string.login_user_pwd_err), 2000);
                break;
            case CommConst.ERROR_CODE_SERVER_FAIL:
                ToastUtil.TextToast(ctx, ctx.getString(R.string.server_connect_fail), 2000);
                break;
            case CommConst.FLAG_USER_STATUS_OFF_LINE:
                ToastUtil.TextToast(ctx, ctx.getString(R.string.relogin), 2000);
                break;
            case CommConst.FLAG_USER_STATUS_LOGIN_ING:
                ToastUtil.TextToast(ctx, ctx.getString(R.string.logining), 2000);
                break;
            case CommConst.ERROR_CODE_SERVER_ADD_RECORD_ERROR:
                ToastUtil.TextToast(ctx, R.string.cloud_add_member_failed, 2000);
                break;
            case CommConst.ERROR_CODE_NETWORK_ERROR:
                ToastUtil.TextToast(ctx, R.string.mobile_network_error, 2000);
                break;
            case CommConst.ERROR_CODE_UPLOAD_FILE_ERROR:
                ToastUtil.TextToast(ctx, R.string.upload_file_error, 2000);
                break;

        }

    }

    static DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }

    };


}
