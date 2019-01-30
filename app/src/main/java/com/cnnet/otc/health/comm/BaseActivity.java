package com.cnnet.otc.health.comm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by SZ512 on 2016/1/8.
 */
public class BaseActivity extends Activity {
    private ExitAppReceiver	exitReceiver;
    private static Context ctx;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receiverInit();
        ctx  = this;
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(exitReceiver);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {

            } else {
                throw e;
            }
        }
        super.onDestroy();
        //System.gc();
    }

    public class ExitAppReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (CommConst.INTENT_ACTION_EXIT_APP.equals(intent.getAction())) {
                //	exitFloatView();
                finish();
            }
        }

    }

    private void receiverInit(){
        exitReceiver = new ExitAppReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CommConst.INTENT_ACTION_EXIT_APP);
        registerReceiver(exitReceiver, intentFilter);
    }

    public static Context getContext(){
        return ctx;
    }

    protected void initCheckType(int deviceType){
        switch (deviceType) {
            case 0://血糖
                SysApp.check_type = CheckType.BLOOD_GLUCOSE;
                break;
            case 1://血压
                SysApp.check_type = CheckType.BLOOD_PRESSURE;
                break;
            case 2: //体温
                SysApp.check_type = CheckType.THERMOMETER;
                break;
            case 3: //血氧
                SysApp.check_type = CheckType.OXIMETRY;
                //intent = new Intent(ctx, DetectBle4Activity.class);
                //intent.putExtra(CommConst.INTENT_EXTRA_KEY_HAS_REAL, true);

                break;
            case 4://血脂
                SysApp.check_type = CheckType.LIPID;
                break;
            case 5://体重
                SysApp.check_type = CheckType.WEIGHT;
                break;
            case 6: //尿酸
                SysApp.check_type = CheckType.URIC_ACID;
                break;
        }
    }
}
