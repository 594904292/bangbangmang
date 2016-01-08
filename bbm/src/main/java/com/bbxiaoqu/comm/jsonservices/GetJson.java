package com.bbxiaoqu.comm.jsonservices;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONException;

import android.os.Bundle;
import android.os.Message;

import com.bbxiaoqu.comm.tool.StreamTool;

public class GetJson {
	public static String GetJson(String url)
	 {
		String json="";
		HttpGet httprequest = new HttpGet(url);		
		HttpClient HttpClient1 = new DefaultHttpClient();
		HttpClient1.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
		// 读取超时
		HttpClient1.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
		HttpResponse httpResponse = null;
		try {
			httpResponse = HttpClient1.execute(httprequest);
		} catch (ClientProtocolException e1) {
			json="Error:ClientProtocolException";
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			json="Error:IOException";
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (httpResponse!=null&&httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			json=JsonToList(httpResponse);
		}else
		{
			json="Error:HTTPStatusCode_"+httpResponse.getStatusLine().getStatusCode();
		}
		return json;
	}

	

	private static String JsonToList(HttpResponse httpResponse)
	{
			String json="";
			InputStream jsonStream = null;
			try {
				jsonStream = httpResponse.getEntity().getContent();
				byte[] data = null;
				try {
					data = StreamTool.read(jsonStream);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					json="Error:jsonStream";
					e.printStackTrace();
				}
			
				if(data!=null)
				{
						json= new String(data);
				}
			} catch (IllegalStateException e) {
				json="Error:JsonToList";
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return json;

	}
	
}
