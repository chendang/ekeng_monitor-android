package com.cnnet.otc.health.comm.volleyRequest;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.cnnet.otc.health.bean.FormImage;
import com.cnnet.otc.health.util.StringUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by SZ512 on 2016/1/11.
 */
public class  PostUploadRequest extends Request<String> {

    private static final String TAG = "PostUploadRequest";

    /**
     * 正确数据的时候回掉用
     */
    private ResponseListener mListener ;
    /*请求 数据通过参数的形式传入*/
    private FormImage item ;

    private final String TWO_HYPHENS = "--";
    private final String MULTIPART_FORM_DATA = "multipart/form-data";  //数据类型
    private final String BOUNDARY = "--------------15-01-27"; //数据分隔线
    private final String END = "\r\n"; //终止符

    public PostUploadRequest(String url, FormImage item, ResponseListener listener) {
        super(Method.POST, url, listener);
        this.mListener = listener ;
        setShouldCache(false);
        this.item = item ;
        //设置请求的响应事件，因为文件上传需要较长的时间，所以在这里加大了，设为5秒
        setRetryPolicy(new DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * 这里开始解析数据
     * @param response Response from the network
     * @return
     */
    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String mString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.v(TAG, "====mString===" + mString);

            return Response.success(mString,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * 回调正确的数据
     * @param response The parsed response returned by
     */
    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (item == null || StringUtil.isEmpty(item.getFilePath())){
            return super.getBody() ;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
        StringBuffer sb= new StringBuffer() ;
            /*第一行*/
        //`"--" + BOUNDARY + "\r\n"`
        sb.append(TWO_HYPHENS + BOUNDARY).append(END) ;
            /*第二行*/
        //Content-Disposition: form-data; name="参数的名称"; filename="上传的文件名" + "\r\n"
        sb.append("Content-Disposition: form-data;");
        sb.append(" name=\"").append(item.getName()).append("\"") ;
        sb.append("; filename=\"").append(item.getFileName()).append("\"");
        sb.append(END) ;
            /*第三行*/
        //Content-Type: 文件的 mime 类型 + "\r\n"
        sb.append("Content-Type: ").append(item.getMime()).append(END) ;
            /*第四行*/
        //"\r\n"
        sb.append(END) ;
        try {
            bos.write(sb.toString().getBytes("utf-8"));
                /*第五行*/
            //文件的二进制数据 + "\r\n"
            bos.write(item.getBytes());
            bos.write(END.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*结尾行*/
        try {
            //`"--" + BOUNDARY + "--" + "\r\n"`
            String endLine = TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + "\r\n" ;
            bos.write(endLine.toString().getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v(TAG,"=====formImage====\n"+bos.toString()) ;
        return bos.toByteArray();
    }
    //Content-Type: multipart/form-data; boundary=----------8888888888888
    @Override
    public String getBodyContentType() {
        return MULTIPART_FORM_DATA+"; boundary="+BOUNDARY;
    }

    public interface ResponseListener extends Response.ErrorListener {
        /**
         * 上传相应接口
         * @param jsonResponse  //返回的Json字符串
         */
        void onResponse(String jsonResponse);
    }
}