package com.bbxiaoqu.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.bbxiaoqu.comm.tool.CustomerHttpClient;

import android.content.pm.PackageInfo;
import android.text.TextUtils;



/**
 * 这个类是获取API请求内容的工厂方法
 * 
 * @author andrew
 * @date    2011-4-21
 *
 */
public class ApiRequestFactory {

   
	public static HttpUriRequest getRequest(String url, int action, HttpEntity entity) throws IOException {
		HttpPost request = new HttpPost(url);			
		request.setEntity(entity);
		return request;
		//HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
		
    	 //HttpGet request = new HttpGet(url + session.getUid());
         //return request;
        /*if (MarketAPI.ACTION_UNBIND == action) {
            HttpGet request = new HttpGet(url + session.getUid());
            return request;
        } else if (UCENTER_API.contains(action)) {
            HttpPost request = new HttpPost(url);
            // update the User-Agent
            request.setHeader("User-Agent", session.getUCenterApiUserAgent());
            request.setEntity(entity);
            return request;
        } else if (S_XML_REQUESTS.contains(action)) {
            HttpPost request = new HttpPost(url);
            // update the g-header
            request.setHeader("G-Header", session.getJavaApiUserAgent());
            request.addHeader("Accept-Encoding", "gzip");
            request.setEntity(AndroidHttpClient.getCompressedEntity(entity.getContent()));
            return request;
        } else {
            // for BBS search API
            HttpPost request = new HttpPost(url);
            request.setEntity(entity);
            return request;
        }*/
    }
    
    /**
     * 获取Market API HTTP 请求内容
     * 
     * @param action 请求的API Code
     * @param params 请求参数
     * @return 处理完成的请求内容
     * @throws UnsupportedEncodingException 假如不支持UTF8编码方式会抛出此异常
     */
    public static HttpEntity getRequestEntity(int action, Object params)
            throws UnsupportedEncodingException {
    	/*	
    	if(action==MarketAPI.ACTION_LOGIN)
    	{
    		HashMap<String, Object> p =(HashMap<String, Object>) params;
    		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();    		
			paramsList.add(new BasicNameValuePair("_userid", p.get("_userid").toString()));
			HttpEntity entity=new UrlEncodedFormEntity(paramsList,"UTF-8");
			return entity;
    	}
 		return null;
    	if (params == null) {
            return "";
        }

        HashMap<String, Object> requestParams;
        if (params instanceof HashMap) {
            requestParams = (HashMap<String, Object>) params;
        } else {
            return "";
        }*/
    	/*List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("_userid", "369"));
		return new UrlEncodedFormEntity(paramsList, HTTP.UTF_8);*/
		//try {
		//	httprequest.setEntity(new UrlEncodedFormEntity(paramsList,"UTF-8"));
			
    	// String body = generateJsonRequestBody(params);
    	 //return new StringEntity(body, HTTP.UTF_8);
    	 //return new UrlEncodedFormEntity((ArrayList<NameValuePair>) params, HTTP.UTF_8);
    	 
    	List<NameValuePair> paramsList = generateFormRequestBody(params);
    	return  new UrlEncodedFormEntity(paramsList, HTTP.UTF_8);
    }
    
    private static List<NameValuePair> generateFormRequestBody(Object params) {
    	List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
    	if (params == null) {
            return paramsList;
        }

        HashMap<String, Object> requestParams;
        if (params instanceof HashMap) {
            requestParams = (HashMap<String, Object>) params;
        } else {
            return paramsList;
        }

        // add parameter node
        final Iterator<String> keySet = requestParams.keySet().iterator();
        while (keySet.hasNext()) {
		    final String key = keySet.next();
		    //jsonObject.put(key, requestParams.get(key));
		    paramsList.add(new BasicNameValuePair(key, (String) requestParams.get(key)));
		}
        return paramsList;
    	
    }
    private static String generateJsonRequestBody(Object params) {

        if (params == null) {
            return "";
        }

        HashMap<String, Object> requestParams;
        if (params instanceof HashMap) {
            requestParams = (HashMap<String, Object>) params;
        } else {
            return "";
        }

        // add parameter node
        final Iterator<String> keySet = requestParams.keySet().iterator();
        JSONObject jsonObject = new JSONObject();
        try {
            while (keySet.hasNext()) {
                final String key = keySet.next();
                jsonObject.put(key, requestParams.get(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return jsonObject.toString();
    }
   
    
    private static final String[] REPLACE = { "&", "&amp;", "\"", "&quot;", "'", "&apos;", "<",
            "&lt;", ">", "&gt;" };
    
    private static String wrapText(String input) {

        if (!TextUtils.isEmpty(input)) {
            for (int i = 0, length = REPLACE.length; i < length; i += 2) {
                input = input.replace(REPLACE[i], REPLACE[i + 1]);
            }
            return input;
        } else {
            return "";
        }
    }
    
}
