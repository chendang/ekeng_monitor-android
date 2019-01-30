package com.cnnet.otc.health.comm.volleyRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class WithCookieGetRequest extends JsonRequest<JSONObject>
{
	private final Gson gson = new Gson();
	private Map<String,String> mMap;
	private final Listener<JSONObject> listener;

	private static Map<String, String> mHeader = new HashMap<String, String>();
	static
	{
		  // mHeader.put("APP-Secret", "ad12msa234das232in"); 
	
		
	}

	public WithCookieGetRequest(String url,Listener<JSONObject> listener, ErrorListener errorListener)
	{
		super(Method.GET, url, url, listener, errorListener);
		this.listener = listener;
		
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError
	{
	
		//String cookie = SysApp.getUserCookie();
		//mHeader.put("Cookie", "userid=" + cookie);
		return mHeader;
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