package com.cnnet.otc.health.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import com.foxchen.ekengmonitor.R;
import com.herily.dialog.HerilyAlertDialog;
import com.herily.dialog.HerilyProgressDialog;

/**
 * Created by cnnet on 2015/12/14.
 */
public class DialogUtil {

    private static HerilyAlertDialog loadingDialog;
    private static Context mContext;
    private static boolean enableShow = true;//是否允许显示dialog


    /**
     * 关闭对话框
     */
    public static void cancelDialog() {
        enableShow = false;
        if (loadingDialog != null) {
            loadingDialog.cancel();
            loadingDialog.dismiss();
        }
    }

    public static void dialogMsgWithTwoButton(Context context, String msg, DialogInterface.OnClickListener listener) {
        HerilyAlertDialog.Builder bd = new HerilyAlertDialog.Builder(context);
        bd.setMessage(msg);
        bd.setPositiveButton(context.getString(R.string.confirm), listener);
        bd.setNegativeButton(context.getString(R.string.cancel), listener);
        bd.show();
    }

    public static AlertDialog Confirm(Context ctx, CharSequence title,
                                           CharSequence message, CharSequence okText,
                                           DialogInterface.OnClickListener oklistener, CharSequence cancelText,
                                           DialogInterface.OnClickListener cancellistener, DialogInterface.OnKeyListener key) {
        HerilyAlertDialog.Builder builder = new HerilyAlertDialog.Builder(ctx);//createDialog(ctx, title, message);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setMessageGravity(Gravity.CENTER);
        builder.setPositiveButton(okText, oklistener);
        builder.setNegativeButton(cancelText, cancellistener);
        builder.setOnKeyListener(key).setCancelable(false);
        return builder.show();
    }

    public static AlertDialog ShowMessage(Context ctx, CharSequence title,
                                      CharSequence message, CharSequence okText)
    {
        HerilyAlertDialog.Builder builder = new HerilyAlertDialog.Builder(ctx);//createDialog(ctx, title, message);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setMessageGravity(Gravity.CENTER);
        builder.setPositiveButton(okText, null);
        return builder.show();
    }

    /**
     * 确认对话框
     *
     * @param ctx
     * @param titleId
     * @param messageId
     * @param okTextId
     */
    public static void ShowMessage(Context ctx, int titleId, int messageId,
                               int okTextId) {
        ShowMessage(ctx, ctx.getText(titleId), ctx.getText(messageId),
                ctx.getText(okTextId));
    }

    /**
     * 确认对话框
     *
     * @param ctx
     * @param titleId
     * @param messageId
     * @param okTextId
     * @param oklistener
     * @param cancelTextId
     * @param cancellistener
     */
    public static void Confirm(Context ctx, int titleId, int messageId,
                               int okTextId, DialogInterface.OnClickListener oklistener, int cancelTextId,
                               DialogInterface.OnClickListener cancellistener) {
        Confirm(ctx, ctx.getText(titleId), ctx.getText(messageId),
                ctx.getText(okTextId), oklistener, ctx.getText(cancelTextId),
                cancellistener, null);
    }

    public static AlertDialog loadProgress(Context context, String msg) {

        HerilyAlertDialog pd = new HerilyProgressDialog(context);
        if (TextUtils.isEmpty(msg)) {
            msg = context.getString(R.string.working);
        }
        pd.setMessage(msg);
        pd.setCanceledOnTouchOutside(false);
        enableShow = true;
        pd.show();
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        pd.setOnKeyListener(keylistener);
        loadingDialog = (HerilyAlertDialog) pd;
        return pd;
    }

    public static AlertDialog loadProgressUnClose(Context context, String msg) {

        HerilyAlertDialog pd = new HerilyProgressDialog(context);
        if (TextUtils.isEmpty(msg)) {
            msg = context.getString(R.string.working);
        }
        pd.setCancelable(false);
        pd.setMessage(msg);
        pd.setCanceledOnTouchOutside(false);
        enableShow = true;
        pd.show();
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        pd.setOnKeyListener(keylistener);
        loadingDialog = (HerilyAlertDialog) pd;
        return pd;
    }

    public static AlertDialog myViewAlertDialog(Context context, View view, int titleId,
                                                int okTextId, DialogInterface.OnClickListener oklistener) {
        HerilyAlertDialog.Builder builder = new HerilyAlertDialog.Builder(context);
        builder.setTitle(titleId);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton(okTextId, oklistener);
        return builder.create();
    }

    public static AlertDialog myViewAlertDialogHasCancel(Context context, View view, int titleId,
                                                int okTextId, DialogInterface.OnClickListener oklistener,
                                                int cancelId, DialogInterface.OnClickListener cancellistener) {
        HerilyAlertDialog.Builder builder = new HerilyAlertDialog.Builder(context);
        builder.setTitle(titleId);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton(okTextId, oklistener);
        builder.setNegativeButton(cancelId, cancellistener);
        return builder.create();
    }

    /**
     * 不带取消按键的Progress msg为空 显示默认的“正在处理”
     *
     * @param context
     * @param msg
     * @return
     */
    public static DialogInterface.OnKeyListener keylistener = new DialogInterface.OnKeyListener() {

        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                return false;
            } else {

                return false;
            }
        }
    };
}
