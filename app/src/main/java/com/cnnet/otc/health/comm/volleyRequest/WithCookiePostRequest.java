package com.cnnet.otc.health.comm.volleyRequest;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;

public class WithCookiePostRequest extends Request<JSONObject>
{
	private Map<String,String> mMap;
	private final Listener<JSONObject> listener;

	private static Map<String, String> mHeader = new HashMap<String, String>();
	static
	{
	
		
	}

	public WithCookiePostRequest(String url, Map<String,String> mMap, Listener<JSONObject> listener, ErrorListener errorListener)
	{
		super(Method.POST, url,errorListener);
		this.listener = listener;
		this.mMap = mMap;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError
	{
		//String cookie = SysApp.getUserCookie();
	/*	 try {
			 cookie = URLEncoder.encode(cookie,"UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}*/
			//mHeader.put("Cookie", "userid="+cookie);
		return mHeader;
	}
	
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mMap;
	}

	@Override
	protected void deliverResponse(JSONObject response)
	{
		listener.onResponse(response);
	}

	
	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response)
	{
		//DialogUtil.cancelDialog();
		try
		{
			String jsonStr = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			
			try {
				return Response.success(new JSONObject(jsonStr), HttpHeaderParser.parseCacheHeaders(response));
			} catch (JSONException e) {
				 return Response.error(new ParseError(e));
			}
		} catch (UnsupportedEncodingException e)
		{
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e)
		{
			return Response.error(new ParseError(e));
		}
	}
}