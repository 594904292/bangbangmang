package com.bbxiaoqu.ui.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.adapter.ListViewAdapter;
import com.bbxiaoqu.comm.service.db.DatabaseHelper;
import com.bbxiaoqu.comm.service.db.XiaoquService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.L;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.community.AddCommunityActivity;
import com.bbxiaoqu.ui.community.CommunityActivity;
import com.bbxiaoqu.ui.community.SubscribeCommunityMapActivity;
import com.bbxiaoqu.ui.community.SubscribeCommunityMapActivity.MyLocationListener;
import com.bbxiaoqu.ui.sub.MessageInfoBean;
import com.bbxiaoqu.widget.AutoListView;
import com.bbxiaoqu.widget.AutoListView.OnLoadListener;
import com.bbxiaoqu.widget.AutoListView.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MapActivity extends Activity {
	private DemoApplication myapplication;
	private DatabaseHelper dbHelper;
	public volatile List<MessageInfoBean> infos = new ArrayList<MessageInfoBean>();
	// 初始化全局 bitmap 信息，不用时及时 recycle
	private BitmapDescriptor mIconMaker;
	private RelativeLayout mMarkerInfoLy;
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;
	private LocationMode mCurrentMode = LocationMode.NORMAL;
	private volatile boolean isFristLocation = true;

	private double mCurrentLantitude;
	private double mCurrentLongitude;
	private boolean ishas;
	TextView title;
	TextView right_text;
	Button btn_listshow;
	LatLng startLng;
	LatLng finishLng ;
	private ViewHolder viewHolder;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		myapplication = (DemoApplication) this.getApplication();
		dbHelper = new DatabaseHelper(MapActivity.this);
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main_map);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		initView();
		initData();
		btn_listshow=(Button) findViewById(R.id.btn_listshow);
		btn_listshow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MapActivity.this,NearActivity.class);				
				startActivity(intent);
			}
		});
		
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
		right_text.setVisibility(View.GONE);

	}
	private void initData() {
		title.setText("我能帮 ");
		//right_text.setText("");
		
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

	public static volatile MessageInfoBean currentInfo;
	public static volatile Marker currentMarker;
	private void initMarkerClickEvent() {
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(Marker marker) {

				MessageInfoBean info = (MessageInfoBean) marker.getExtraInfo().get("info");
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
		List<Map<String, Object>> smalldataList = new ArrayList<Map<String, Object>>();
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		String sql="";
						
		sql = "select * from  [message] order by date  desc ";
			Cursor c = sdb.rawQuery(sql, null);
			while (c.moveToNext()) {
//				HashMap<String, Object> item = new HashMap<String, Object>();
//				item.put("senduserid", String.valueOf(c.getString(1)));
//				item.put("sendnickname", String.valueOf(c.getString(2)));
//				item.put("community", String.valueOf(c.getString(3)));
//				item.put("address", String.valueOf(c.getString(4)));
//				item.put("lng", String.valueOf(c.getString(5)));
//				item.put("lat", String.valueOf(c.getString(6)));
//				item.put("guid", String.valueOf(c.getString(7)));
//				item.put("infocatagroy", String.valueOf(c.getString(8)));
//				item.put("message", String.valueOf(c.getString(9)));
//				item.put("icon", String.valueOf(c.getString(10)));
//				item.put("date", String.valueOf(c.getString(11)));
//				item.put("is_coming", String.valueOf(c.getString(12)));
//				item.put("readed", String.valueOf(c.getString(13)));
				double r = MessageInfoBean
						.getDistance(latitude, longitude, 
								Double.parseDouble(String.valueOf(c.getString(6))), Double
								.parseDouble(String.valueOf(c.getString(5))));	
			infos.add(new MessageInfoBean(String.valueOf(c.getString(7)),Double.parseDouble(String.valueOf(c.getString(6))), Double
					.parseDouble(String.valueOf(c.getString(5))),
					String.valueOf(c.getString(10)), String.valueOf(c.getString(2)+":"+c.getString(9)), "距离" + r + "米",
					100, 0));
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
	
	protected void popupInfo(RelativeLayout mMarkerLy, MessageInfoBean info) {
		//ViewHolder viewHolder = null;
		if (mMarkerLy.getTag() == null) {
			viewHolder = new ViewHolder();
			viewHolder.infoImg = (ImageView) mMarkerLy
					.findViewById(R.id.info_icon);
			viewHolder.infoName = (TextView) mMarkerLy
					.findViewById(R.id.info_name);
			viewHolder.infoDistance = (TextView) mMarkerLy
					.findViewById(R.id.info_distance);
					
			mMarkerLy.setTag(viewHolder);			
		} else {
			viewHolder = (ViewHolder) mMarkerLy.getTag();			
		}
		putmark(info);
	}
	private void putmark(MessageInfoBean info) {
		viewHolder.infoImg.setTag(info.getImgId());//设置标签
		String fileName="";
		if(info.getImgId().toString().trim().length()>4)
		{						
			String photo=info.getImgId().toString().trim();
			if(photo.indexOf(",")>0)
			{
				fileName = DemoApplication.getInstance().getlocalhost()+"uploads/"+photo.split(",")[0]; 
			}else
			{
				fileName = DemoApplication.getInstance().getlocalhost()+"uploads/"+photo; 
			}
		}
	
		 
			ImageLoader.getInstance().displayImage(fileName, viewHolder.infoImg,  ImageOptions.getOptions(), new ImageLoadingListener()
			{
	
				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					// TODO Auto-generated method stub
					 //加载取消
				}
	
				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
					// TODO Auto-generated method stub
					//加载成功
					if(bitmap==null)
					{
						viewHolder.infoImg.setImageResource(R.mipmap.empty);
					}
				}
	
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason bitmap) {
					// TODO Auto-generated method stub
					//加载失败
					if(bitmap==null)
					{
						viewHolder.infoImg.setImageResource(R.mipmap.empty);
					}
				}
	
				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					// TODO Auto-generated method stub
					//开始加载
				}
				
				
			});
		viewHolder.infoDistance.setText(info.getDistance());
		viewHolder.infoName.setText(info.getName());
		Button zan = (Button) findViewById(R.id.info_zan_action);	
		zan.setText("我去帮");

	}
	//点标题
	public void show(View view) {
		if (currentInfo != null) {
			Intent Intent1 = new Intent();
			Intent1.setClass(MapActivity.this, ViewActivity.class);
			Bundle arguments = new Bundle();
			arguments.putString("put", "false");
			arguments.putString("guid",currentInfo.getId());
			Intent1.putExtras(arguments);
			startActivity(Intent1);
		}
	}
	//点按钮
	public void zan(View view) {
		if (currentInfo != null) {
			OverlayOptions overlayOptions = null;
			
			Intent Intent1 = new Intent();
			Intent1.setClass(MapActivity.this, ViewActivity.class);
			Bundle arguments = new Bundle();
			arguments.putString("put", "false");
			arguments.putString("guid",currentInfo.getId());
			Intent1.putExtras(arguments);
			startActivity(Intent1);
			
			
//Button zan = (Button) findViewById(R.id.info_zan_action);			
//			if(view==zan)
//			{
//				XiaoquService xiaoquService = new XiaoquService(
//						MapActivity.this);
//				boolean ishavezhan = xiaoquService.isexit(currentInfo.getId().trim());
//	
//				if (ishavezhan) {
//	
//					xiaoquService.removexiaoqu(currentInfo.getId());
//					subscribe(currentInfo.getId(), "remove");
//					currentMarker.setIcon(BitmapDescriptorFactory
//							.fromResource(R.drawable.icon_gcoding));
//					//zan.setImageResource(R.drawable.map_zan);
//					zan.setText("关注");
//					currentInfo.setIszan(0);
//					
//				} else {
//					xiaoquService.addxiaoqu(currentInfo.getId());
//					subscribe(currentInfo.getId(), "add");
//					currentMarker.setIcon(BitmapDescriptorFactory
//							.fromResource(R.drawable.pin_blue));
//					currentInfo.setIszan(1);
//					//zan.setImageResource(R.drawable.cancel_zan);
//					zan.setText("取消关注");
//					
//					
//				}
//				xiaoquService.close();
//			}
		}
	}


	/**
	 * 复用弹出面板mMarkerLy的控件
	 * 
	 * @author zhy
	 * 
	 */
	private class ViewHolder {
		ImageView infoImg;
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
			Iterator<MessageInfoBean> iterator = infos.iterator();
			while (iterator.hasNext()) {
				MessageInfoBean info = iterator.next();
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
