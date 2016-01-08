package com.bbxiaoqu.ui.fragment;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

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
import org.xmlpull.v1.XmlPullParser;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.bean.BbMessage;
import com.bbxiaoqu.bean.InfoBase;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver;
import com.bbxiaoqu.client.baidu.Utils;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver.onNewMessageListener;
import com.bbxiaoqu.comm.service.ServiceSyncLocation;
import com.bbxiaoqu.comm.service.ServiceDemo;
import com.bbxiaoqu.comm.service.db.MessageDB;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.LoginActivity;
import com.bbxiaoqu.ui.NoticeActivity;
import com.bbxiaoqu.ui.PublishActivity;
import com.bbxiaoqu.ui.RegisterActivity;
import com.bbxiaoqu.ui.sub.SettingsActivity;
import com.bbxiaoqu.ui.sub.UserInfoActivity;
import com.bbxiaoqu.update.UpdataInfo;
import com.bbxiaoqu.update.UpdateManager;
import com.bbxiaoqu.update.UpdateService;
import com.bbxiaoqu.view.DrawerView;
import com.bbxiaoqu.widget.AutoListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;

/**
 * 项目的主Activity，所有的Fragment都嵌入在这里。
 * 
 */
@SuppressLint("NewApi")
public class HomeActivity extends Activity implements OnClickListener,onNewMessageListener {
	
	public static final String appName = "updatebbm";	
	public static final String downUrl = "bbm.apk";
	private String OldVersionName="";

	private String NewVersionName="";
	private MessageFragment messageFragment;
	private PublishFragment publishFragment;
	private SubscribeFragment subscribeFragment;
	private SettingFragment settingFragment;

	private View messageLayout;
	private View contactsLayout;
	private View newsLayout;
	private View settingLayout;
	private ImageView messageImage;
	private ImageView publishImage;
	private ImageView newsImage;
	private ImageView settingImage;
	private TextView messageText;
	private TextView publishText;
	private TextView newsText;
	private TextView settingText;
	private FragmentManager fragmentManager;

	
	/** 头像*/
	public ImageView top_head;
	/** 更多 */
	public ImageView top_more;
	public TextView headtop_left_count;
	protected SlidingMenu side_drawer;
	
	
	private DemoApplication myapplication;
	
	private LocationClient mLocationClient;
	public Double nLatitude; 
	public Double nLontitude;


	private long touchTime = 0;

	private long waitTime = 2000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BbPushMessageReceiver.msgListeners.add(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_maintab);
		myapplication = (DemoApplication) this.getApplication();
		myapplication.getInstance().addActivity(this);
		
		UserService uService = new UserService(HomeActivity.this);
	
		if(myapplication.ifpass(myapplication.getUserId()))
		{//判断用户是否登录			
			initSlidingMenu();
			initViews();			
			setTabSelection(0);			

			new Thread(updataRun).start();						
		}else
		{
			Intent Intent1 = new Intent();
			Intent1.setClass(HomeActivity.this,LoginActivity.class);
			startActivity(Intent1);
		}
		if(myapplication.getNickname().equals(""))
		{//昵称为空,弹出用户基本信息修改
			Intent intent=new Intent(HomeActivity.this,UserInfoActivity.class);
			startActivity(intent);
		}
        PushManager.startWork(getApplicationContext(),

        PushConstants.LOGIN_TYPE_API_KEY,
        Utils.getMetaValue(HomeActivity.this, "api_key"));
		
		/*位置:服务中无法初始位置，先在界面中实现定位*/
		initlsb();
		/*后台进行数据同步*/	 
		//bindService(new Intent(ServiceDemo.ACTION), conn, BIND_AUTO_CREATE);  
		//startService(new Intent(ServiceDemo.ACTION));  
		
		
		Intent mIntent = new Intent();
		mIntent.setAction("com.bbxiaoqu.comm.service.ServiceDemo");//你定义的service的action
		mIntent.setPackage(getPackageName());//这里你需要设置你应用的包名
		myapplication.startService(mIntent);
	}
	
	
	private void initbaidu(Resources resource, String pkgName) {
		// Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
		// 这里把apikey存放于manifest文件中，只是一种存放方式，
		// 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(PushDemoActivity.this,
		// "api_key")
		PushManager.startWork(this.myapplication,
				PushConstants.LOGIN_TYPE_API_KEY, DemoApplication.API_KEY);
		// Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
		// PushManager.enableLbs(getApplicationContext());

		// Push: 设置自定义的通知样式，具体API介绍见用户手册，如果想使用系统默认的可以不加这段代码
		// 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1，
		// 与下方代码中 PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
		/*
		 * CustomPushNotificationBuilder cBuilder = new
		 * CustomPushNotificationBuilder( this.getApplicationContext(),
		 * resource.getIdentifier( "notification_custom_builder", "layout",
		 * pkgName), resource.getIdentifier("notification_icon", "id", pkgName),
		 * resource.getIdentifier("notification_title", "id", pkgName),
		 * resource.getIdentifier("notification_text", "id", pkgName));
		 * cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
		 * cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND |
		 * Notification.DEFAULT_VIBRATE);
		 * cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
		 * cBuilder.setLayoutDrawable(resource.getIdentifier(
		 * "simple_notification_icon", "drawable", pkgName));
		 * PushManager.setNotificationBuilder(this, 1, cBuilder);
		 */
	}
	
	
	 private static final String TAG = "ServiceDemoActivity";  
	 ServiceConnection conn = new ServiceConnection() {  
	        public void onServiceConnected(ComponentName name, IBinder service) {  
	            Log.v(TAG, "onServiceConnected");  
	        }  
	        public void onServiceDisconnected(ComponentName name) {  
	            Log.v(TAG, "onServiceDisconnected");  
	        }  
	    };  
	
	
	/**
	 * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
	 */
	private void initViews() {
		messageLayout = findViewById(R.id.message_layout);
		contactsLayout = findViewById(R.id.contacts_layout);
		newsLayout = findViewById(R.id.news_layout);
		settingLayout = findViewById(R.id.setting_layout);
		messageImage = (ImageView) findViewById(R.id.message_image);
		publishImage = (ImageView) findViewById(R.id.contacts_image);
		newsImage = (ImageView) findViewById(R.id.news_image);
		settingImage = (ImageView) findViewById(R.id.setting_image);
		messageText = (TextView) findViewById(R.id.message_text);
		publishText = (TextView) findViewById(R.id.contacts_text);
		newsText = (TextView) findViewById(R.id.news_text);
		settingText = (TextView) findViewById(R.id.setting_text);
		messageLayout.setOnClickListener(this);
		contactsLayout.setOnClickListener(this);
		newsLayout.setOnClickListener(this);
		settingLayout.setOnClickListener(this);		
		headtop_left_count = (TextView) findViewById(R.id.headtop_left_count);
		headtop_left_count.setOnClickListener(this);		
		headtop_left_count.setVisibility(View.GONE);
		top_head = (ImageView) findViewById(R.id.top_head);
		top_head.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(side_drawer.isMenuShowing()){
					side_drawer.showContent();//显示中间
				}else{
					side_drawer.showMenu();//显示左侧
				}
			}
		});
		top_more = (ImageView) findViewById(R.id.top_more);
		top_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(HomeActivity.this,PublishActivity.class);				
				Bundle bundle = new Bundle();
				bundle.putInt("infocatagroy", 0);
				intent.putExtras(bundle);
				startActivity(intent);
				
				
			}
		});
	}



	private void initlsb() {
		// TODO Auto-generated method stub
		mLocationClient = new LocationClient(this.myapplication);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000*5;
	
		option.setScanSpan(span);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	
		mLocationClient.registerLocationListener(new BDLocationListener() {
	
			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				if (location == null) {
					Log.v(TAG, "HomeActivity location empty");
					return;
				}
				nLatitude = location.getLatitude();
				nLontitude = location.getLongitude();
				
				Log.v(TAG, "HomeActivity :"+nLatitude+"--"+nLontitude);
				//mLocationClient.stop();
	
				myapplication.setLat(String.valueOf(nLatitude));
				myapplication.setLng(String.valueOf(nLontitude));
				myapplication.updatelocation();
	
				
			}
	
		});
		mLocationClient.start();		
		mLocationClient.requestLocation();
		
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.message_layout:
			setTabSelection(0);
			break;
		case R.id.contacts_layout:
			setTabSelection(1);
			break;
		case R.id.news_layout:
			setTabSelection(2);
			break;
		case R.id.setting_layout:
			// 当点击了设置tab时，选中第4个tab
			setTabSelection(3);
			break;
		case R.id.headtop_left_count:
			/*setTabSelection(5);
			
			 headtop_left_count.setVisibility(View.GONE);	
			 headtop_left_count.setText("0");
			*/ 
			 
			Intent intent=new Intent(HomeActivity.this,NoticeActivity.class);
			startActivity(intent);
			    
			break;
		default:
			break;
		}
	}

	/**
	 * 根据传入的index参数来设置选中的tab页。
	 * 
	 * @param index
	 *            每个tab页对应的下标。0表示消息，1表示联系人，2表示动态，3表示设置。
	 */
	private void setTabSelection(int index) {
		clearSelection();
		fragmentManager = getFragmentManager();		
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		switch (index) {
		case 5:
			// 当点击了消息tab时，改变控件的图片和文字颜色
			messageImage.setImageResource(R.mipmap.tab_message_sel);
			messageText.setTextColor(Color.WHITE);
			if (messageFragment == null) {
				// 如果MessageFragment为空，则创建一个并添加到界面上
				messageFragment = new MessageFragment();
				transaction.add(R.id.content, messageFragment);
			} else {
				messageFragment = new MessageFragment();
				transaction.replace(R.id.content, messageFragment);
			}
			break;
		case 0:
			// 当点击了消息tab时，改变控件的图片和文字颜色
			messageImage.setImageResource(R.mipmap.tab_message_sel);
			messageText.setTextColor(Color.WHITE);
			if (messageFragment == null) {
				// 如果MessageFragment为空，则创建一个并添加到界面上
				messageFragment = new MessageFragment();
				transaction.add(R.id.content, messageFragment);
			} else {
				// 如果MessageFragment不为空，则直接将它显示出来
				transaction.show(messageFragment);
			}
			break;
		case 1:
			// 当点击了联系人tab时，改变控件的图片和文字颜色
			publishImage.setImageResource(R.mipmap.tab_publish_sel);
			publishText.setTextColor(Color.WHITE);
			if (publishFragment == null) {
				// 如果ContactsFragment为空，则创建一个并添加到界面上
				publishFragment = new PublishFragment();
				transaction.add(R.id.content, publishFragment);
			} else {
				// 如果ContactsFragment不为空，则直接将它显示出来
				transaction.show( publishFragment);
			}
			break;
		case 2:
			// 当点击了动态tab时，改变控件的图片和文字颜色
			newsImage.setImageResource(R.mipmap.tab_subscribe_sel);
			newsText.setTextColor(Color.WHITE);
			if (subscribeFragment == null) {
				// 如果NewsFragment为空，则创建一个并添加到界面上
				subscribeFragment = new SubscribeFragment();
				transaction.add(R.id.content, subscribeFragment);
			} else {
				// 如果NewsFragment不为空，则直接将它显示出来
				transaction.show( subscribeFragment);
			}
			break;
		case 3:
		default:
			// 当点击了设置tab时，改变控件的图片和文字颜色
			settingImage.setImageResource(R.mipmap.tab_settings_sel);
			settingText.setTextColor(Color.WHITE);
			if (settingFragment == null) {
				// 如果SettingFragment为空，则创建一个并添加到界面上
				settingFragment = new SettingFragment();
				transaction.add(R.id.content, settingFragment);
			} else {
				// 如果SettingFragment不为空，则直接将它显示出来
				transaction.show(settingFragment);
			}
			break;
		}
		transaction.commit();
	}

	/**
	 * 清除掉所有的选中状态。
	 */
	private void clearSelection() {
		messageImage.setImageResource(R.mipmap.tab_message);
		messageText.setTextColor(Color.parseColor("#ffffff"));
		publishImage.setImageResource(R.mipmap.tab_publish);
		publishText.setTextColor(Color.parseColor("#ffffff"));
		newsImage.setImageResource(R.mipmap.tab_subscribe);
		newsText.setTextColor(Color.parseColor("#ffffff"));
		settingImage.setImageResource(R.mipmap.tab_settings);
		settingText.setTextColor(Color.parseColor("#ffffff"));
	}


	private void hideFragments(FragmentTransaction transaction) {
		if (messageFragment != null) {			
			transaction.hide(messageFragment);
		}
		if (publishFragment != null) {
			transaction.hide(publishFragment);
		}
		if (subscribeFragment != null) {
			transaction.hide(subscribeFragment);
		}
		if (settingFragment != null) {
			transaction.hide(settingFragment);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& KeyEvent.KEYCODE_BACK == keyCode) {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - touchTime) >= waitTime) {
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				touchTime = currentTime;
			} else {
				myapplication.getInstance().exit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	
	
	
	

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case 1:
				//progressDialog.dismiss();
				new Thread(updataRun).start();			
				break;
			case 2:

				Bundle data = msg.getData();		    
			    int num = data.getInt("num");
			    
			    String v=headtop_left_count.getText().toString();
			    if(v.length()>0)
			    {
			    	int basenum=Integer.parseInt(headtop_left_count.getText().toString());
			    	num=basenum+num;			    	
			    }
			    headtop_left_count.setVisibility(View.VISIBLE);	
			    headtop_left_count.setText(String.valueOf(num));
			   
				
				break;
			default:
				break;
			}			
		};
	};

	Runnable updataRun = new Runnable(){
	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			CheckVersionTask();
			Looper.loop();
		}  
		
	};

	protected void initSlidingMenu() {
		side_drawer = new DrawerView(this).initSlidingMenu();
	}

	/*
	 * 获取当前程序的版本号 
	 */
	private String getVersionName(){
		//获取packagemanager的实例 
		PackageManager packageManager = getPackageManager();
		//getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return packInfo.versionName; 
	}
	
	
	public static UpdataInfo getUpdataInfo(InputStream is) throws Exception{
		XmlPullParser  parser = Xml.newPullParser();  
		parser.setInput(is, "utf-8");//设置解析的数据源 
		int type = parser.getEventType();
		UpdataInfo info = new UpdataInfo();//实体
		while(type != XmlPullParser.END_DOCUMENT ){
			switch (type) {
			case XmlPullParser.START_TAG:
				if("version".equals(parser.getName())){
					info.setVersion(parser.nextText());	//获取版本号
				}else if ("url".equals(parser.getName())){
					info.setUrl(parser.nextText());	//获取要升级的APK文件
				}else if ("description".equals(parser.getName())){
					info.setDescription(parser.nextText());	//获取该文件的信息
				}
				break;
			}
			type = parser.next();
		}
		return info;
	}
	
	private void CheckVersionTask() {
		OldVersionName=getVersionName();
		UpdataInfo info=null;
		String target = myapplication.getlocalhost()+ "/update.xml?t="+System.currentTimeMillis();
		 URL url = null;  
		 try {  
			 url = new URL(target);//构造一个url对象  
		  } catch (MalformedURLException e) {  		            
		     e.printStackTrace();  
		  }  
		 if(url!=null){  
				 HttpURLConnection urlConnection ;
				try {
					urlConnection  = (HttpURLConnection)  url.openConnection();
					urlConnection.setConnectTimeout(50000);
					urlConnection.setRequestMethod("GET");
						if (urlConnection .getResponseCode() == 200) {
							InputStream json = urlConnection.getInputStream();
							info=getUpdataInfo(json);		
							NewVersionName=info.getVersion();
						}
				} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
				} catch (IOException e) {
						// TODO Auto-generated catch block
						Message msg = new Message();
				        Bundle data = new Bundle();
				        data.putString("error","请检查设备网络连接");	       
				        msg.setData(data);
				        errorhandler.sendMessage(msg);				        
						//e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(info!=null)
					{
						if(info.getVersion().equals(OldVersionName)){  
							    //Log.i(TAG,"版本号相同无需升级");  
						}else{  
							if(Double.parseDouble(OldVersionName)<Double.parseDouble(info.getVersion()))
							{
							
								/*Intent mIntent = new Intent();
								mIntent.setAction("com.bbxiaoqu.comm.service.UpdateService");//你定义的service的action
								mIntent.putExtra("Key_App_Name",appName);
								mIntent.putExtra("Key_Down_Url",myapplication.getlocalhost()+downUrl);
								mIntent.setPackage(getPackageName());//这里你需要设置你应用的包名
								myapplication.startService(mIntent);
								*/
								
								Intent intent = new Intent(HomeActivity.this,UpdateService.class);
								intent.putExtra("Key_App_Name",appName);
								intent.putExtra("Key_Down_Url",myapplication.getlocalhost()+downUrl);						
								startService(intent);
								
							}  
						}
					}
		 }	
	}

	Handler errorhandler = new Handler(){
	    @Override
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        Bundle data = msg.getData();		    
	        String error = data.getString("error");
	        Toast.makeText(HomeActivity.this, error,Toast.LENGTH_SHORT).show();				
	    }
	};
	
	
	private void loadData(final int what) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				int num=1;		
				Message msg = handler.obtainMessage();			
				msg.what = 2;	
				Bundle data = new Bundle();
			    data.putInt("num",num);	
				msg.setData(data);
				handler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	public void onNewMessage(BbMessage message) {
		// TODO Auto-generated method stub
		//System.out.println("有新消息");
		loadData(1);
	}
	
	
	
	
	@Override
	public void onDestroy() {
		BbPushMessageReceiver.msgListeners.remove(this);
		PushManager.stopWork(getApplicationContext());
		super.onDestroy();
	}
	

}
