package com.bbxiaoqu.ui.community;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.bean.InfoBase;
import com.bbxiaoqu.comm.service.db.DatabaseHelper;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.service.db.XiaoquService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.comm.tool.LocationUtil.MyLocationListener;
import com.bbxiaoqu.ui.LoginActivity;
import com.bbxiaoqu.ui.sub.InfoBean;
import com.bbxiaoqu.ui.sub.MyOrientationListener;
import com.bbxiaoqu.ui.sub.MyOrientationListener.OnOrientationListener;
import com.bbxiaoqu.view.BaseActivity;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
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
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * @author dzy
 * 小区地图订阅
 */
public class SubscribeCommunityMapActivity extends Activity {
	private DemoApplication myapplication;
	public volatile List<InfoBean> infos = new ArrayList<InfoBean>();
	// 初始化全局 bitmap 信息，不用时及时 recycle
	private BitmapDescriptor mIconMaker;
	/**
	 * 详细信息的 布局
	 */
	private RelativeLayout mMarkerInfoLy;
	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView;
	private BaiduMap mBaiduMap;
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
	


	

	private boolean ishas;

	TextView title;
	TextView right_text;	
	LatLng startLng;
	LatLng finishLng ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		myapplication = (DemoApplication) this.getApplication();
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_overlay);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		initView();
		initData();
		
		
		// 第一次定位
		isFristLocation = true;
		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMarkerInfoLy = (RelativeLayout) findViewById(R.id.id_marker_info);
		// 获得地图的实例
		mBaiduMap = mMapView.getMap();
		mIconMaker = BitmapDescriptorFactory.fromResource(R.mipmap.maker);
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(16.0f);
		mBaiduMap.setMapStatus(msu);
		// 初始化定位
		initMyLocation();
	    new Thread(loadEventThread).start();	   
	    
	    
	    mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
            
            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {
                    // TODO Auto-generated method stub
                    Log.i("", "==-->滚动状态开始");
                    startLng = arg0.target;
            }
            
            public void onMapStatusChangeFinish(MapStatus arg0) {
                // TODO Auto-generated method stub
                Log.i("", "==-->滚动状态停止");
                finishLng = arg0.target;
                if (startLng.latitude != finishLng.latitude
                        || startLng.longitude != finishLng.longitude) {
	                Projection ject = mBaiduMap.getProjection();
	                Point startPoint = ject.toScreenLocation(startLng);
	                Point finishPoint = ject.toScreenLocation(finishLng);
	                double x = Math.abs(finishPoint.x - startPoint.x);
	                double y = Math.abs(finishPoint.y - startPoint.y);
	                if (x > 1 || y > 1) {
	//在这处理滑动        
	                        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory
	                                        .newMapStatus(new MapStatus.Builder().target(finishLng)
	                                        .overlook(-15).rotate(180).build());
	                        mBaiduMap.setMapStatus(mapStatusUpdate);
	                }
                }
            }

			@Override
			public void onMapStatusChange(MapStatus arg0) {
				// TODO Auto-generated method stub
				
			}
            
	    });
	   
	}

	Runnable loadEventThread = new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			initMarkerClickEvent();
			initMapClickEvent();
		}  
		
	};
	
	private void initView() {
		title = (TextView) findViewById(R.id.title);
		right_text = (TextView) findViewById(R.id.right_text);
		right_text.setVisibility(View.VISIBLE);

	}
	private void initData() {
		title.setText("关注小区 ");
		right_text.setText("添加小区");
		right_text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(SubscribeCommunityMapActivity.this,AddCommunityActivity.class);				
				startActivity(intent);
			}
		});
	}

	private void initMapClickEvent() {
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			public void onMapClick(LatLng arg0) {
				if (mMarkerInfoLy != null) {
					mMarkerInfoLy.setVisibility(View.GONE);
				}
				if (mBaiduMap != null) {
					mBaiduMap.hideInfoWindow();
				}

			}
		});
	}

	public static volatile InfoBean currentInfo;
	public static volatile Marker currentMarker;
	private void initMarkerClickEvent() {
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(Marker marker) {

				InfoBean info = (InfoBean) marker.getExtraInfo().get("info");
				currentInfo = info;
				currentMarker = marker;
				InfoWindow mInfoWindow;
				OnInfoWindowClickListener listener = null;
				// 生成一个TextView用户在地图中显示InfoWindow
				TextView location = new TextView(getApplicationContext());
				location.setBackgroundResource(R.drawable.location_tips);
				location.setPadding(30, 20, 30, 50);
				location.setText(info.getName());

			

				final LatLng ll = marker.getPosition();
				Point p = mBaiduMap.getProjection().toScreenLocation(ll);
				p.y -= 47;
				LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
				// 为弹出的InfoWindow添加点击事件
				listener = new OnInfoWindowClickListener() {
					public void onInfoWindowClick() {
						/*LatLng ll = marker.getPosition();
						LatLng llNew = new LatLng(ll.latitude + 0.005,
								ll.longitude + 0.005);
						marker.setPosition(llNew);*/
						mBaiduMap.hideInfoWindow();
					}
				};

				mInfoWindow = new InfoWindow(BitmapDescriptorFactory
						.fromView(location), ll, -47, listener);
				mBaiduMap.showInfoWindow(mInfoWindow);
				// 设置详细信息布局为可见
				mMarkerInfoLy.setVisibility(View.VISIBLE);
				// 根据商家信息为详细信息布局设置信息
				popupInfo(mMarkerInfoLy, info);
				return true;
			}
		});
	}

	
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
	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {

			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
		
			mCurrentLantitude = location.getLatitude();
			mCurrentLongitude = location.getLongitude();
			mLocationClient.stop();
			mLocationClient.stop();
			if (isFristLocation) { 
				isFristLocation = false;
				new Thread(loadxiaoqu).start();
			}
					
		}

	}

	Runnable loadxiaoqu = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getnear(mCurrentLantitude, mCurrentLongitude);
		}

	};

	/* 经纬度 */
	public void getnear(double latitude, double longitude) {
		if (!NetworkUtils.isNetConnected(myapplication)) {			
			T.showShort(myapplication, "当前无网络连接,请稍后再试！");
			return;
		}
		String target = myapplication.getlocalhost()+"getxiaoqu.php?latitude="
				+ latitude + "&longitude=" + longitude;
		try {
			// /////////////////////////////
			List<InfoBase> bfjllist = null;
			HttpGet httprequest = new HttpGet(target);
			HttpClient HttpClient1 = new DefaultHttpClient();
			// 请求超时
			HttpClient1.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
			// 读取超时
			HttpClient1.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 20000);

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
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream jsonStream = null;
				try {
					jsonStream = httpResponse.getEntity().getContent();
					byte[] data = StreamTool.read(jsonStream);
					JSONObject jsonobject;
					String json = new String(data);
					JSONArray jsonarray = new JSONArray(json);
					for (int i = 0; i < jsonarray.length(); i++) {
						jsonobject = jsonarray.getJSONObject(i);

						double r = InfoBean
								.getDistance(latitude, longitude, Double
										.parseDouble(jsonobject
												.getString("lat")), Double
										.parseDouble(jsonobject
												.getString("lng")));
						XiaoquService xiaoquService = new XiaoquService(this);
						boolean ishavezhan = xiaoquService.isexit(jsonobject.getString("id"));
						if (ishavezhan) {
							infos.add(new InfoBean(jsonobject.getString("id"),Double.parseDouble(jsonobject
									.getString("lat")), Double
									.parseDouble(jsonobject.getString("lng")),
									jsonobject.getString("pic"), jsonobject
											.getString("name"), "距离" + r + "米",
									100, 1));
						} else {
							infos.add(new InfoBean(jsonobject.getString("id"),Double.parseDouble(jsonobject
									.getString("lat")), Double
									.parseDouble(jsonobject.getString("lng")),
									jsonobject.getString("pic"), jsonobject
											.getString("name"), "距离" + r + "米",
									100, 0));
						}
						xiaoquService.close();
						
						
					}
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Message msg = new Message();
		Bundle data = new Bundle();

		data.putString("status", "finsh");
		msg.setData(data);
		lbsfinshhandler.sendMessage(msg);
	}

	Handler lbsfinshhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.getData().getString("status").equals("finsh")) {
				addInfosOverlay();
			}
		}
	};

	/**
	 * 根据info为布局上的控件设置信息
	 * 
	 * @param mMarkerInfo2
	 * @param info
	 */
	protected void popupInfo(RelativeLayout mMarkerLy, InfoBean info) {
		ViewHolder viewHolder = null;
		if (mMarkerLy.getTag() == null) {
			viewHolder = new ViewHolder();
			/*viewHolder.infoImg = (ImageView) mMarkerLy
					.findViewById(R.id.info_img);*/
			viewHolder.infoName = (TextView) mMarkerLy
					.findViewById(R.id.info_name);
			viewHolder.infoDistance = (TextView) mMarkerLy
					.findViewById(R.id.info_distance);
					
			mMarkerLy.setTag(viewHolder);			
		} else {
			viewHolder = (ViewHolder) mMarkerLy.getTag();			
		}
		putmark(info, viewHolder);
	}
	private void putmark(InfoBean info, ViewHolder viewHolder) {
		viewHolder.infoDistance.setText(info.getDistance());
		viewHolder.infoName.setText(info.getName());
		Button zan = (Button) findViewById(R.id.info_zan_action);		
		XiaoquService xiaoquService = new XiaoquService(
				SubscribeCommunityMapActivity.this);
		boolean ishavezhan = xiaoquService.isexit(info.getId().trim());
		if (ishavezhan) {
			zan.setText("取消关注");
		} else {
			zan.setText("关注");
		}
		xiaoquService.close();

	}

	public void show(View view) {
		if (currentInfo != null) {
			Intent intent=new Intent(SubscribeCommunityMapActivity.this,CommunityActivity.class);
			Bundle arguments = new Bundle();
			arguments.putString("id",currentInfo.getId());	
			arguments.putString("name",currentInfo.getName());	
			arguments.putString("address",currentInfo.getDistance());				
			arguments.putString("lat",String.valueOf(currentInfo.getLatitude()));	
			arguments.putString("lng",String.valueOf(currentInfo.getLongitude()));	
			/*arguments.putString("pic",obj.getPic());					
			arguments.putString("business",obj.getPic());	
			arguments.putString("develop",obj.getPic());	
			arguments.putString("propertymanagement",obj.getPic());	
			arguments.putString("propertytype",obj.getPic());	
			arguments.putString("homenumber",obj.getPic());	
			arguments.putString("buildyear",obj.getPic());	*/
			intent.putExtras(arguments);
			startActivity(intent);
		}
	}
	public void zan(View view) {
		if (currentInfo != null) {
			OverlayOptions overlayOptions = null;
			Button zan = (Button) findViewById(R.id.info_zan_action);
			
			if(view==zan)
			{
				XiaoquService xiaoquService = new XiaoquService(
						SubscribeCommunityMapActivity.this);
				boolean ishavezhan = xiaoquService.isexit(currentInfo.getId().trim());
	
				if (ishavezhan) {
	
					xiaoquService.removexiaoqu(currentInfo.getId());
					subscribe(currentInfo.getId(), "remove");
					currentMarker.setIcon(BitmapDescriptorFactory
							.fromResource(R.mipmap.icon_gcoding));
					//zan.setImageResource(R.drawable.map_zan);
					zan.setText("关注");
					currentInfo.setIszan(0);
					
				} else {
					xiaoquService.addxiaoqu(currentInfo.getId(),currentInfo.getName());
					subscribe(currentInfo.getId(), "add");
					currentMarker.setIcon(BitmapDescriptorFactory
							.fromResource(R.mipmap.pin_blue));
					currentInfo.setIszan(1);
					//zan.setImageResource(R.drawable.cancel_zan);
					zan.setText("取消关注");
					
					
				}
				xiaoquService.close();
			}
		}
	}

	private void subscribe(String xiaoquid, String action) {
		if (!NetworkUtils.isNetConnected(myapplication)) {			
			T.showShort(myapplication, "当前无网络连接,请稍后再试！");
			return;
		}
		String target = myapplication.getlocalhost()+"adduserxiaoqu.php";
		HttpPost httprequest = new HttpPost(target);
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();

		paramsList.add(new BasicNameValuePair("_userid", myapplication
				.getUserId()));
		paramsList.add(new BasicNameValuePair("_communityid", xiaoquid));
		paramsList.add(new BasicNameValuePair("_action", action));
		try {
			httprequest
					.setEntity(new UrlEncodedFormEntity(paramsList, "UTF-8"));
			HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
			HttpResponse httpResponse = HttpClient1.execute(httprequest);
			String authcode = "";
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				authcode = EntityUtils.toString(httpResponse.getEntity());

			} else {
				authcode = "";
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 复用弹出面板mMarkerLy的控件
	 * 
	 * @author zhy
	 * 
	 */
	private class ViewHolder {
		//ImageView infoImg;
		TextView infoName;
		TextView infoDistance;
		//TextView infoZan;// 关注
	}

	/**
	 * 初始化图层
	 */
	public synchronized void addInfosOverlay() {
		LatLng latLng = null;
		synchronized (infos) {

			OverlayOptions overlayOptions = null;
			Marker marker = null;
			Iterator<InfoBean> iterator = infos.iterator();
			while (iterator.hasNext()) {
				InfoBean info = iterator.next();
				// 位置
				 latLng = new LatLng(info.getLatitude(), info.getLongitude());
				
				 
				if (info.getIszan()==1) {
					overlayOptions = new MarkerOptions()
							.position(latLng)
							.icon(BitmapDescriptorFactory
									.fromResource(R.mipmap.pin_blue))
							.zIndex(5);
					info.setIszan(1);
				} else {
					overlayOptions = new MarkerOptions()
							.position(latLng)
							.icon(BitmapDescriptorFactory
									.fromResource(R.mipmap.icon_gcoding))
							.zIndex(5);
					info.setIszan(0);
				}				
				marker = (Marker) (mBaiduMap.addOverlay(overlayOptions));
				Bundle bundle = new Bundle();
				bundle.putSerializable("info", info);
				marker.setExtraInfo(bundle);
			}
		}
		// 将地图移到到最后一个经纬度位置
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.setMapStatus(u);
	}



	@Override
	protected void onStart() {
		// 开启图层定位
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		super.onStart();
	}

	@Override
	protected void onStop() {
		// 关闭图层定位
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();

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
	
	
	public void doBack(View view) {
		onBackPressed();
	}

}
