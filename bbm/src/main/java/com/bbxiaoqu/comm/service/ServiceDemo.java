package com.bbxiaoqu.comm.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.bean.BbMessage;
import com.bbxiaoqu.bean.InfoBase;
import com.bbxiaoqu.comm.service.db.MessageDB;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.UiTools;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * Android Service 示例
 * 
 * @author dev
 * 
 */
public class ServiceDemo extends Service {
	private static final String TAG = "ServiceDemo" ;
	public static final String ACTION = "com.bbxiaoqu.comm.service.ServiceDemo";	
	private DemoApplication myapplication;
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.v(TAG, "ServiceDemo onBind");
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.v(TAG, "ServiceDemo onCreate");
		super.onCreate();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.v(TAG, "ServiceDemo onStart");
		super.onStart(intent, startId);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "ServiceDemo onStartCommand");
		myapplication = (DemoApplication) getApplication();
		//new Thread(new Threadlocaltion()).start();  //开启线程
		//new Thread(new Threaddatasync()).start();  //开启线程
		
		while(true)
		{
			
			System.out.println("check");
			try {
				Thread.sleep(1000*30);
			} catch (InterruptedException e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	
    
    // 线程类  
    class Threaddatasync implements Runnable {  
        @Override  
        public void run() {  
            // TODO Auto-generated method stub  
        	while(true)
        	{
        	//获取最大的			
        		//myapplication.getInstance().startxmpp();
			try {
				Thread.sleep(1000*60*1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
        	}
        }

		/*private void runsync() {
			MessageDB db1 = new MessageDB(ServiceDemo.this);
			String last_sendtime=db1.getmaxtime();
			String target="";
			if(last_sendtime.length()>0)
			{
				try {
					target = myapplication.getlocalhost()+"/getlastinfo.php?last_sendtime="+URLEncoder.encode(last_sendtime,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}else
			{
				target = myapplication.getlocalhost()+"/getlastinfo.php";
			}
			Log.v(TAG, "ServiceDemo Threaddatasync:"+target);
			List<InfoBase> bfjllist = null;
			HttpGet httprequest = new HttpGet(target);
			HttpClient HttpClient1 = new DefaultHttpClient();
			// 请求超时
			HttpClient1.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
			// 读取超时
			HttpClient1.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
			HttpResponse httpResponse = null;
			try {
				httpResponse = HttpClient1.execute(httprequest);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (httpResponse!=null&&httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream jsonStream = null;
				try {
					jsonStream = httpResponse.getEntity().getContent();
					byte[] data = StreamTool.read(jsonStream);
					String json = new String(data);
					savetolocal(json);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}  
    }  

	private void savetolocal(String json) {
		if(json.length()>0)
		{
			JSONArray jsonarray = null;
			try {
				jsonarray = new JSONArray(json);
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject customJson = jsonarray.getJSONObject(i);
					BbMessage mess = new BbMessage();
					mess.setMessage(customJson.getString("content").toString());
					mess.setSenduserId(customJson.getString("senduser").toString());
					mess.setSendnickname(customJson.getString("username").toString());
					mess.setCommunity(customJson.getString("community").toString());
					mess.setAddress(customJson.getString("address").toString());
					mess.setLat(Double.parseDouble(customJson.getString("lat").toString()));
					mess.setLng(Double.parseDouble(customJson.getString("lng").toString()));
					mess.setGuid(customJson.getString("guid").toString());
					mess.setInfocatagroy(customJson.getString("infocatagroy").toString());
					mess.setIcon(customJson.getString("photo").toString());
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						mess.setDate(df.parse(customJson.getString("sendtime").toString()));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mess.setDateStr(customJson.getString("sendtime").toString());
					mess.setComing(true);
					mess.setReaded(false);
					MessageDB db = new MessageDB(this.getApplicationContext());
					db.SyncToLocal(mess);//本地数据库中不存在同步到本地
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}*/		
	}
    
}
