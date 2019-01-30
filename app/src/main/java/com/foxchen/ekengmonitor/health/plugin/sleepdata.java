package com.foxchen.ekengmonitor.health.plugin;

import android.content.Context;
import android.os.Bundle;

import com.cnnet.otc.health.comm.SysApp;
import com.foxchen.ekengmonitor.bean.PedometerSleepAnalyzeData;
import com.lifesense.lssleepanalyze_ndk.LSSleepAnalyze;
import com.lifesense.lssleepanalyze_ndk.LSSleepAnalyzeResult;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.StandardFeature;
import io.dcloud.common.util.JSUtil;


/**
 * 5+ SDK 扩展插件示例
 * 5+ 扩扎插件在使用时需要以下两个地方进行配置
 * 		1  WebApp的mainfest.json文件的permissions节点下添加JS标识
 * 		2  assets/data/properties.xml文件添加JS标识和原生类的对应关系
 * 本插件对t应的JS文件在 assets/apps/H5Plugin/js/est.js
 * 本插件对应的使用的HTML assest/apps/H5plugin/index.html
 * 
 * 更详细说明请参考文档http://ask.dcloud.net.cn/article/66
 * **/
public class sleepdata extends StandardFeature
{

    private IWebview pWebview;
    private String CallBackID;
    private static  String resReturn="";
    private JSONArray returnArray = new JSONArray();
    public void onStart(Context pContext, Bundle pSavedInstanceState, String[] pRuntimeArgs) {

        /**
         * 如果需要在应用启动时进行初始化，可以继承这个方法，并在properties.xml文件的service节点添加扩展插件的注册即可触发onStart方法
         * */
    }
    public void ReturnJs(){
        // 调用方法将原生代码的执行结果返回给js层并触发相应的JS层回调函数

    };
    public void analyse(IWebview pWebview, JSONArray array) throws InterruptedException {
        this.CallBackID = array.optString(0);
        this.pWebview=pWebview;
        SysApp.setmUniqueKey(array.optString(1));
        analyzeSleepData(array.optString(1),Long.parseLong(array.optString(2)));

        JSUtil.execCallback(pWebview, CallBackID, returnArray, JSUtil.OK, false);

    }

    public String convertTime(long timestamp)
    {
        String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp * 1000));
        return date;
    }
    public Object analyzeSleepData(String data,long time){
        LSSleepAnalyze sleepAnalyze = new LSSleepAnalyze();
//        long time = 1461674640;
//        long time = 1484785199;
//        String data = "1e1e1e1e1e1e1e1e0c181a131e121e1e1e1508080b0c0a040305040a0a071e07090e0c080101010101010101011e1e1a161e0600000000000200010600010503000202040001000000040201030101000502001201011e15071e10031e010218151e1418041401010003160000000000000407020608000009000100080000030a0b001a1e1e1e1e1e1e1e1e1e1e1e0f04020c0711111e1e16121e1e091e1e1e1e1e1e141019191e111306141e0c050e1e171e0d041e1e1e161e1e1e";
//        String data = "515e5f53000000000000000054525158534d55484a4c4b4d4848494b484847484a49464c4b484c4a4c484b5850535353510000000000000000000000000000000051530000000000513a3737380000000040000000400000";
//        ArrayList<LSSleepAnalyzeResult> results = sleepAnalyze.sleepAnalyze(data, 1459333499, 300, 1459351223, 1459385466);
        List resList = new ArrayList();
        ArrayList<LSSleepAnalyzeResult> results = sleepAnalyze.sleepAnalyze(data, time, 300, 8);
        System.err.println("results:" + results);
        if (results != null) {
            for (LSSleepAnalyzeResult result : results) {
                PedometerSleepAnalyzeData psa = new PedometerSleepAnalyzeData();
                String sleepTimeStr = convertTime(result.sleepTimeUTC );
                String getupTimeStr = convertTime(result.getupTimeUTC);
                returnArray.put(sleepTimeStr);
                returnArray.put(getupTimeStr);
                returnArray.put( result.awakeSleepTime);
                returnArray.put( result.lightSleepTime);
                returnArray.put(  result.deepSleepTime);
                System.out.println("入睡时间: " + sleepTimeStr + "\t, 起床时间: " + getupTimeStr + "\t,清醒: " + result.awakeSleepTime + "\t分,浅睡: " + result.lightSleepTime + "\t分,深睡: " + result.deepSleepTime + "\t分");
                psa.setAwakeCount(result.getupTimeUTC);
//                psa.setSleepTimeUTC();
                resList.add(psa);
            }
        }
        return resList;

    }
}