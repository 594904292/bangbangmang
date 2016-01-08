package com.bbxiaoqu.ui.community;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.service.User;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.LoginActivity;
import com.bbxiaoqu.ui.fragment.HomeActivity;
import com.bbxiaoqu.ui.sub.MyOrientationListener;
import com.bbxiaoqu.ui.sub.MyOrientationListener.OnOrientationListener;
import com.bbxiaoqu.view.BaseActivity;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

public class AddCommunityActivity extends BaseActivity implements
OnGetGeoCoderResultListener {
	private DemoApplication myapplication;
	// 初始化全局 bitmap 信息，不用时及时 recycle
	private BitmapDescriptor mIconMaker;
	
	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	/**
	 * 定位的客户端
	 */
	private LocationClient mLocationClient;
	/**
	 * 定位的监听器
	 */
	public MyLocationListener mMyLocationListener;
	/**
	 * 当前定位的模式
	 */
	private LocationMode mCurrentMode = LocationMode.NORMAL;
	/***
	 * 是否是第一次定位
	 */
	private volatile boolean isFristLocation = true;

	/**
	 * 最新一次的经纬度
	 */
	private double mCurrentLantitude;
	private double mCurrentLongitude;
	/**
	 * 地图定位的模式
	 */
	private String[] mStyles = new String[] { "地图模式【正常】", "地图模式【跟随】",
			"地图模式【罗盘】" };
	private int mCurrentStyle = 0;


	TextView title;
	TextView right_text;
	TextView latlngtip;
	EditText name;
	EditText propertymanagement;
	EditText address;
	Button save;
	
	
	String nameStr="";
	String latStr="";
	String lngStr="";
	String addressStr="";
	String businessStr="";
	String countryStr="";
	String provinceStr="";
	String cityStr="";
	String districtStr="";
	String streetStr="";
	String streetNumberStr="";
	String propertymanagementStr="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		myapplication = (DemoApplication) this.getApplication();
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_add_community);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		initView();
		initData();
		// 第一次定位
		isFristLocation = true;
		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		latlngtip= (TextView) findViewById(R.id.latlngtip);
		name= (EditText) findViewById(R.id.name);
		propertymanagement= (EditText) findViewById(R.id.propertymanagement);
		address= (EditText) findViewById(R.id.address);
		save= (Button) findViewById(R.id.save);
		// 获得地图的实例
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setOnMapClickListener(onmaplistener);
		mIconMaker = BitmapDescriptorFactory.fromResource(R.mipmap.maker);
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
		mBaiduMap.setMapStatus(msu);
		// 初始化定位
		initMyLocation();
		// 初始化传感器
		//initOritationListener();
		
		
		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);
		
	}

	
	private void initView() {
		title = (TextView) findViewById(R.id.title);
		right_text = (TextView) findViewById(R.id.right_text);
		right_text.setVisibility(View.VISIBLE);

	}
	private void initData() {
		title.setText("添加小区 ");
		right_text.setText("保存");
		right_text.setOnClickListener(new OnClickListener() {
			
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(name.getText().toString().trim().length()==0)
				{
					T.showShort(myapplication, "小区名称不能为空！");
					return;
				}
				if(propertymanagement.getText().toString().trim().length()==0)
				{
					T.showShort(myapplication, "物业公司不能为空！");
					return;
				}
				
				if (!NetworkUtils.isNetConnected(myapplication)) {			
					T.showShort(myapplication, "当前无网络连接,请稍后再试！");
					return;
				}
				new Thread(saveRun).start();
			}
		});
	}

	
Runnable saveRun = new Runnable(){  
		
		@Override  
		public void run() {  
		    // TODO Auto-generated method stub  
			
			String target = myapplication.getlocalhost()+"savexiaoqu.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("name", name.getText().toString()));
			paramsList.add(new BasicNameValuePair("propertymanagement", propertymanagement.getText().toString()));
			paramsList.add(new BasicNameValuePair("lat", String.valueOf(mCurrentLantitude)));	
			paramsList.add(new BasicNameValuePair("lng", String.valueOf(mCurrentLongitude)));	
			
			paramsList.add(new BasicNameValuePair("address", addressStr));	
			paramsList.add(new BasicNameValuePair("business", businessStr));	
			paramsList.add(new BasicNameValuePair("country", countryStr));	
			paramsList.add(new BasicNameValuePair("province", provinceStr));	
			paramsList.add(new BasicNameValuePair("city", cityStr));	
			paramsList.add(new BasicNameValuePair("district", districtStr));	
			paramsList.add(new BasicNameValuePair("street", streetStr));	
			paramsList.add(new BasicNameValuePair("streetNumber", streetNumberStr));	
			
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList, "UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) 
				{
					InputStream  jsonStream = httpResponse.getEntity().getContent();
					byte[] data = null;
					try {
						data = StreamTool.read(jsonStream);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					Message msg = new Message();
					Bundle msgbundle = new Bundle();
					msgbundle.putString("reason", "保存成功");
					msg.setData(msgbundle);
					savehandler.sendMessage(msg);							 
							
				} else {		
					Message msg = new Message();
					Bundle msgbundle = new Bundle();
					msgbundle.putString("reason", "访问服务器异常,稍后再试...");
					msg.setData(msgbundle);
					savehandler.sendMessage(msg);					
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			

		}  
	 }; 
	 
	 
	 Handler savehandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle data = msg.getData();
				Toast.makeText(AddCommunityActivity.this, data.getString("reason"), Toast.LENGTH_LONG).show();
				finish();
			}
		};

	OnMapClickListener onmaplistener = new OnMapClickListener() {

		@Override
		public void onMapClick(LatLng ll) {
			// TODO Auto-generated method stub
			mBaiduMap.clear();
			overlayOptions = new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_blue)).zIndex(5);
			marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
			latlngtip.setText("经度:"+ll.latitude+",纬度:"+ll.longitude);
			
			mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
		}

		@Override
		public boolean onMapPoiClick(MapPoi arg0) {
			// TODO Auto-generated method stub
			return false;
		}
		
	};

	


	/**
	 * 初始化定位相关代码
	 */
	private void initMyLocation() {
		// 定位初始化
		mLocationClient = new LocationClient(this);
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		// 设置定位的相关配置
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
	}

	/**
	 * 实现实位回调监听
	 */
	OverlayOptions overlayOptions = null;
	Marker marker = null;
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			/*// 构造定位数据
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(mXDirection).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mCurrentAccracy = location.getRadius();*/
			// 设置定位数据
			//mBaiduMap.setMyLocationData(locData);
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();
			mLocationClient.stop();
			if (isFristLocation) { 
				isFristLocation = false; 
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				latlngtip.setText("经度:"+ll.latitude+",纬度:"+ll.longitude);
				mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
								
				overlayOptions = new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.fromResource(R.mipmap.pin_blue)).zIndex(5);
				marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u); 
			}
		}
	}
	
	/**
	 * 地图移动到我的位置,此处可以重新发定位请求，然后定位； 直接拿最近一次经纬度，如果长时间没有定位成功，可能会显示效果不好
	 */
	private void center2myLoc() {
		LatLng ll = new LatLng(mCurrentLantitude, mCurrentLongitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);
	}

	@Override
	protected void onStart() {
		// 开启图层定位
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		// 开启方向传感器
		//myOrientationListener.start();
		super.onStart();
	}

	@Override
	protected void onStop() {
		// 关闭图层定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();

		// 关闭方向传感器
		//myOrientationListener.stop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
		mIconMaker.recycle();
		mMapView = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}


	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(AddCommunityActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
	}


	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(AddCommunityActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
				
		addressStr=result.getAddress();
		businessStr=result.getBusinessCircle();
		countryStr="中国";
		provinceStr=result.getAddressDetail().province;
		cityStr=result.getAddressDetail().city;
		districtStr=result.getAddressDetail().district;
		streetStr=result.getAddressDetail().street;
		streetNumberStr=result.getAddressDetail().streetNumber;
		
		
		address.setText(result.getAddress());
	}

}
