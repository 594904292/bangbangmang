/*
 * Copyright (C) 2010 mAPPn.Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bbxiaoqu.api;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.bbxiaoqu.R;
import com.bbxiaoqu.api.util.Utils;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;



/**
 * 机锋市场API请求任务
 * 
 * @author andrew.wang
 * @date 2010-10-27
 * @since Version 0.4.0
 */
public class ApiAsyncTask extends AsyncTask<Void, Void, Object> {

	// 超时（网络）异常
	public static final int TIMEOUT_ERROR = 600;
	// 业务异常
	public static final int BUSSINESS_ERROR = 610;

	private AndroidHttpClient mClient;
	private int mReuqestAction;
	private ApiRequestListener mHandler;
	private Object mParameter;
	//private Session mSession;
	private Context mContext;	

    ApiAsyncTask(Context context, int action, ApiRequestListener handler, Object param) {
        this.mContext = context;
        //this.mSession = Session.get(context);
        this.mReuqestAction = action;
        this.mHandler = handler;
        this.mParameter = param;        
        this.mClient = HttpClientFactory.get().getHttpClient();
    }
   
	@Override
	protected Object doInBackground(Void... params) {	    
        if (!Utils.isNetworkAvailable(mContext)) {
            return TIMEOUT_ERROR;
        }
        String requestUrl =  MarketAPI.API_URLS[mReuqestAction];  
        requestUrl = GETURL(requestUrl,mParameter);//生成GETURL
		System.out.println("requestUrl:"+requestUrl);
	    System.out.println("mParameter:"+mParameter.toString());
	    HttpEntity requestEntity = null;
	    try {
            requestEntity = ApiRequestFactory.getRequestEntity(mReuqestAction, mParameter);
        } catch (UnsupportedEncodingException e) {
            Utils.D("OPPS...This device not support UTF8 encoding.[should not happend]");
            return BUSSINESS_ERROR;
        }
        Object result = null;        
        HttpResponse response = null;
        HttpUriRequest request = null;
        try {
            request = ApiRequestFactory.getRequest(requestUrl, mReuqestAction, requestEntity);
            mClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,10000);//连接时间
            mClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);//连接时间            
            response = mClient.execute(request);            
            final int statusCode = response.getStatusLine().getStatusCode();
            Utils.D("requestUrl " + requestUrl + " statusCode: " + statusCode);
            if (HttpStatus.SC_OK != statusCode) {
                // 非正常返回
                return statusCode;
            }
            result = ApiResponseFactory.getResponse(mContext, mReuqestAction, response);
            // 处理API Response，如果解析出错，返回BUSSINESS_ERROR【610】
            return result == null ? BUSSINESS_ERROR : result;            
        } catch (IOException e) {
            Utils.D("Market API encounter the IO exception[mostly is timeout exception]", e);
            return TIMEOUT_ERROR;
        } finally {
            // release the connection
            if (request != null) {
                request.abort();
            }
            if (response != null) {
                try {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        entity.consumeContent();
                    }
                } catch (IOException e) {
                    Utils.D("release low-level resource error");
                }
            }
        }
	}

	private String GETURL(String requestUrl,Object Parameter) {
		if(mReuqestAction==MarketAPI.ACTION_GETDYNAMICS)
        {
        	 HashMap<String, Object> requestParams = null;
             if (Parameter instanceof HashMap) {
                 requestParams = (HashMap<String, Object>) Parameter;
             } 
        	requestUrl=requestUrl+"?userid="+requestParams.get("userid").toString()+"&rang=xiaoqu&start=0&limit=10";
        }else if(mReuqestAction==MarketAPI.ACTION_GETINFO)
        {
       	 	HashMap<String, Object> requestParams = null;
            if (Parameter instanceof HashMap) {
                requestParams = (HashMap<String, Object>) Parameter;
            } 
            requestUrl=requestUrl+"?guid="+requestParams.get("guid").toString();
       }else if(mReuqestAction==MarketAPI.ACTION_GETINFOS)
       {
      	 	HashMap<String, Object> requestParams = null;
           if (Parameter instanceof HashMap) {
               requestParams = (HashMap<String, Object>) Parameter;
           } 
           String userid=requestParams.get("_userid").toString();
           String rang=requestParams.get("_rang").toString();
           String status=requestParams.get("_status").toString();
           String start=requestParams.get("_start").toString();
           String limit=requestParams.get("_limit").toString();
           requestUrl=requestUrl+"?userid="+userid+"&rang="+rang+"&status="+status+"&start="+start+"&limit="+limit;
      }else if(mReuqestAction==MarketAPI.ACTION_GETFRIENDS)
      {
    	 	HashMap<String, Object> requestParams = null;
         if (Parameter instanceof HashMap) {
             requestParams = (HashMap<String, Object>) Parameter;
         } 
         String userid=requestParams.get("_userid").toString();        
         requestUrl=requestUrl+"?mid1="+userid;
        }else if(mReuqestAction==MarketAPI.ACTION_GETXIAOQUS)
        {
            HashMap<String, Object> requestParams = null;
            if (Parameter instanceof HashMap) {
                requestParams = (HashMap<String, Object>) Parameter;
            }
            String keyword=requestParams.get("keyword").toString();
            String latitude=requestParams.get("latitude").toString();
            String longitude=requestParams.get("longitude").toString();
            if(keyword!=null&&keyword.length()>0)
            {
                requestUrl = requestUrl + "?keyword=" + keyword;
            }else
            {
                requestUrl = requestUrl + "?latitude=" + latitude + "&longitude=" + longitude;
            }
        }
		return requestUrl;
	}

	@Override
	protected void onPostExecute(Object response){
        if (mHandler == null) {
            return;
        }
        if (mContext instanceof Activity && ((Activity) mContext).isFinishing()) {
            //页面已经被关闭，无须进行后续处理
            return;
        }
		if (response == null) {
			mHandler.onError(this.mReuqestAction, BUSSINESS_ERROR);
			return;
		} else if (response instanceof Integer) {		    
		    Integer statusCode = (Integer) response;
            if (!handleCommonError(statusCode)) 
            {//公用错误处理
                mHandler.onError(this.mReuqestAction, (Integer) response);
                return;
            }
		}
		mHandler.onSuccess(this.mReuqestAction, response);
	}
	
    /**
     * 处理公用Http Status Code
     * @param statusCode Http Status Code
     * @return 此Code是否被处理（True：已经被处理）
     */
	private boolean handleCommonError(int statusCode) {
	    
        if (statusCode == 200) {
            return true;
        }
	    if(statusCode >= TIMEOUT_ERROR) {
	        Utils.makeEventToast(mContext, mContext.getString(R.string.notification_server_error),false);
	    } else if(statusCode >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            Utils.makeEventToast(mContext, mContext.getString(R.string.notification_server_error),false);
	    } else if(statusCode >= HttpStatus.SC_BAD_REQUEST) {
	        Utils.makeEventToast(mContext, mContext.getString(R.string.notification_client_error),false);
	    }
	    return false;
	}

	/**
	 * 市场API请求监听器
	 * 
	 * @author andrew.wang
	 * @date 2010-10-28
	 * @since Version 0.4.0
	 */
	public interface ApiRequestListener {

		/**
		 * The CALLBACK for success aMarket API HTTP response
		 * 
		 * @param response
		 *            the HTTP response
		 */
		void onSuccess(int method, Object obj);

		/**
		 * The CALLBACK for failure aMarket API HTTP response
		 * 
		 * @param statusCode
		 *            the HTTP response status code
		 */
		void onError(int method, int statusCode);
	}

}