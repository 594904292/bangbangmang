package com.bbxiaoqu.client.xmpp;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.bbxiaoqu.comm.service.db.FriendDB;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.StreamTool;

public class GetUserThread extends Thread{
	  private String userid;
	  private Context mContext;
		
	   public GetUserThread(Context context,String userid)
	    {
	    	this.mContext = context;
	        this.userid = userid;
	    }
	    public void run()
	    {
	    	String target =  "http://www.bbxiaoqu.com/getuserinfo.php?userid="+this.userid;
			HttpPost httprequest = new HttpPost(target);
			try {
				//httprequest.setEntity(new UrlEncodedFormEntity(null,"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					InputStream jsonStream = httpResponse.getEntity()
							.getContent();
					byte[] data = null;
					try {
						data = StreamTool.read(jsonStream);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					String JsonContext = new String(data);
					try {
						JSONArray jsonarray;
						jsonarray = new JSONArray(JsonContext);
						if(jsonarray.length()>0)
						{
							JSONObject jsonobj = jsonarray.getJSONObject(0);
							String userid = jsonobj.getString("userid");
							if (userid != "") {									
								String headface = jsonobj.getString("headface");
								String username = jsonobj.getString("username");		
								 FriendDB fb=new FriendDB(mContext);
								 fb.updatelastinfo(userid, username,headface,username);								
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
}
