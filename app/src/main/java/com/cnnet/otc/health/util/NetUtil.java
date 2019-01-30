package com.cnnet.otc.health.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

/**
 * Created by SZ512 on 2016/1/7.
 */
public class NetUtil {

    private static final int CMNET = 3;

    private static final int CMWAP = 2;

    public static final int WIFI = 1;

    /**
     * 判断目前是否处于联网状态
     *
     * @param context
     * @return
     */
    public static boolean checkNetState(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return false;
        }

        return true;
    }

    /**
     * -1：不通  1：wifi 2 :wap 3: net
     * @param context
     * @return
     */
    public static int getAPNType(Context context) {
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        //	System.out.println("networkInfo.getExtraInfo() is "+ networkInfo.getExtraInfo());

        if (nType == ConnectivityManager.TYPE_MOBILE) {
            if(!TextUtils.isEmpty(networkInfo.getExtraInfo())){
                if ( networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
                    netType = CMNET;
                } else {
                    netType = CMWAP;
                }
            }else{
                netType = CMNET;
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = WIFI;
        }
        return netType;
    }

    /**
     * 使用HttpClient进行GET请求
     * @param url
     * @return
     * @throws Exception
     */
    public static String httpStringGet(String url) throws Exception {
        // This method for HttpConnection
        String page = "";
        BufferedReader bufferedReader = null;
        try {
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,"android");

            HttpParams httpParams = client.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
            HttpConnectionParams.setSoTimeout(httpParams, 5000);

            HttpGet request = new HttpGet();
            request.setHeader("Content-Type", "text/plain; charset=utf-8");
            request.setURI(new URI(url));
            HttpResponse response = client.execute(request);
            bufferedReader = new BufferedReader(new InputStreamReader(response
                    .getEntity().getContent(), "UTF-8"));

            StringBuffer stringBuffer = new StringBuffer("");
            String line = "";

            String NL = System.getProperty("line.separator");
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + NL);
            }
            bufferedReader.close();
            page = stringBuffer.toString();
            Log.i("page", "page:" + page);
            System.out.println(page + "page");
            return page;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.d("BBB", e.toString());
                }
            }
        }
    }
}
