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
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.UiTools;
import com.bbxiaoqu.ui.fragment.HomeActivity;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class ServiceSyncLocation extends Service{

	protected static final String TAG = "ServiceSyncLocation";
	public static final String ACTION = "com.bbxiaoqu.comm.service.ServiceSyncLocation";	
	private DemoApplication myapplication;
	LocationClient mLocationClient;
	
	
	
	
	@Override
	public void onCreate() {
		super.onCreate();				
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		UiTools.printLog(TAG, "ServiceSyncLocation onBind");
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		UiTools.printLog(TAG, "ServiceSyncLocation onStart");
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "ServiceDemo onStartCommand");
		myapplication = (DemoApplication) getApplication();		
		new Thread(new Threadlocaltion()).start();  //开启线程
		return super.onStartCommand(intent, flags, startId);
	}
		
	 // 线程类  
    class Threadlocaltion implements Runnable {  
        @Override  
        public void run() {  
            // TODO Auto-generated method stub  
            while (true) {  
            	while(true)
            	{
            	//获取最大的			
            		runmap();
    			try {
    				Thread.sleep(1000*60*60*1);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}  
            }  
        }  
    }  
    }

    
    private void runmap() {		

		// 注意：请在试用setContentView前初始化BMapManager对象，否则会报错,这句话太经典了		
    	mLocationClient = new LocationClient(this.myapplication);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000*30;

		option.setScanSpan(span);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);

		// 注册位置监听器
		mLocationClient.registerLocationListener(new BDLocationListener() {
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				if (location == null) {
					return;
				}				
				UiTools.printLog(TAG,location.getLatitude() + ","+ location.getLongitude());	
				myapplication.setLat(String.valueOf(location.getLatitude()));
				myapplication.setLng(String.valueOf(location.getLongitude()));
				mLocationClient.stop();
			}
		});
		mLocationClient.start();
		mLocationClient.requestLocation();
	}
    
   
    
   
    

}
