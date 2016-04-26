package com.bbxiaoqu.ui.main;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.bbxiaoqu.adapter.DynamicListViewAdapter;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.api.ApiAsyncTask.ApiRequestListener;
import com.bbxiaoqu.bean.BbMessage;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver;
import com.bbxiaoqu.client.baidu.Utils;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver.onMessageReadListener;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver.onNewMessageListener;
import com.bbxiaoqu.comm.service.db.NoticeDB;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.LoginActivity;
import com.bbxiaoqu.ui.NoticeActivity;
import com.bbxiaoqu.ui.PublishActivity;
import com.bbxiaoqu.ui.SearchActivity;
import com.bbxiaoqu.ui.report.DayActivity;
import com.bbxiaoqu.ui.report.MonthActivity;
import com.bbxiaoqu.ui.report.WeekActivity;
import com.bbxiaoqu.ui.sub.RecentActivity;
import com.bbxiaoqu.ui.user.UserInfoActivity;
import com.bbxiaoqu.ui.sub.ViewUserInfoActivity;
import com.bbxiaoqu.update.UpdataInfo;
import com.bbxiaoqu.update.UpdateService;
import com.bbxiaoqu.view.BaseActivity;
import com.bbxiaoqu.view.DrawerView;
import com.bbxiaoqu.client.xmpp.XmppTool;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ViewSwitcher;

public class MainActivity extends BaseActivity  implements ViewSwitcher.ViewFactory, View.OnTouchListener,OnClickListener ,onNewMessageListener,onMessageReadListener,ApiRequestListener{
	
	private DemoApplication myapplication;
	public ImageView top_head;
	public ImageView top_more;
	public BadgeView headtop_left_count;
	protected SlidingMenu side_drawer;
	private LocationClient mLocationClient;
	public Double nLatitude; 
	public Double nLontitude;
	public Button sos_btn;
	public Button can_sos_btnmap;	
	public static final String appName = "updatebbm";	
	public static final String downUrl = "bbm.apk";
	private String OldVersionName="";
	private String NewVersionName="";
	private LinearLayout linearlayout_body1;
	private LinearLayout linearlayout_body2;
	//private LinearLayout linearlayout_body3;
	private TextView gonggao;
	private DynamicListViewAdapter adapter;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	//ListView lstv;
	private static final int DIALOG_PROGRESS = 0;
	// 用户不存在（用户名错误）
	private static final int ERROR_CODE_USERNAME_NOT_EXIST = 211;
	// 用户密码错误
	private static final int ERROR_CODE_PASSWORD_INVALID = 212;

	NoticeDB db=new NoticeDB(this);




	/**
	 * ImagaSwitcher 的引用
	 */
	private ImageSwitcher mImageSwitcher;
	/**
	 * 图片id数组
	 */
	private int[] imgIds;
	/**
	 * 当前选中的图片id序号
	 */
	private int currentPosition;
	/**
	 * 按下点的X坐标
	 */
	private float downX;
	/**
	 * 装载点点的容器
	 */
	private LinearLayout linearLayout;
	/**
	 * 点点数组
	 */
	private ImageView[] tips;

	public Button reportday_btn;
	/*public Button reportweek_btn;
	public Button reportmonth_btn;*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		BbPushMessageReceiver.msgListeners.add(this);//新消息
		BbPushMessageReceiver.msgReadListeners.add(this);//点了阅读
		setContentView(R.layout.activity_main_main);		
		myapplication = (DemoApplication) this.getApplication();
		myapplication.getInstance().addActivity(this);
		Resources resource = this.getResources();
		String pkgName = this.getPackageName();
		/*位置:服务中无法初始位置，先在界面中实现定位*/
		initbaidu(resource, pkgName);
		initlsb();
	    PushManager.startWork(getApplicationContext(),
	    PushConstants.LOGIN_TYPE_API_KEY,
	    Utils.getMetaValue(MainActivity.this, "api_key"));
		UserService uService = new UserService(MainActivity.this);	
		if(myapplication.getNickname().equals(""))
		{//昵称为空,弹出用户基本信息修改
			Intent intent=new Intent(MainActivity.this,UserInfoActivity.class);
			startActivity(intent);
		}
		if(myapplication.ifpass(myapplication.getUserId()))
		{//判断用户是否登录			
			initSlidingMenu();
			initViews();	
			new Thread(updataRun).start();					
		}else
		{
			Intent Intent1 = new Intent();
			Intent1.setClass(MainActivity.this,LoginActivity.class);
			startActivity(Intent1);
			return;
		}		
 		LoadData();

		imgIds = new int[]{R.mipmap.banner1,R.mipmap.banner2,R.mipmap.banner3,R.mipmap.banner4};
		//实例化ImageSwitcher
		mImageSwitcher  = (ImageSwitcher) findViewById(R.id.imageSwitcher1);
		//设置Factory
		mImageSwitcher.setFactory(this);
		//设置OnTouchListener，我们通过Touch事件来切换图片
		mImageSwitcher.setOnTouchListener(this);

		linearLayout = (LinearLayout) findViewById(R.id.viewGroup);

		tips = new ImageView[imgIds.length];
		for(int i=0; i<imgIds.length; i++){
			ImageView mImageView = new ImageView(this);
			tips[i] = mImageView;
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT));
			layoutParams.rightMargin = 3;
			layoutParams.leftMargin = 3;

			mImageView.setBackgroundResource(R.mipmap.page_indicator_unfocused);
			linearLayout.addView(mImageView, layoutParams);
		}

		//这个我是从上一个界面传过来的，上一个界面是一个GridView
		currentPosition = getIntent().getIntExtra("position", 0);
		mImageSwitcher.setImageResource(imgIds[currentPosition]);
		//Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imgIds[currentPosition]);
		//mImageSwitcher.setImageDrawable(new BitmapDrawable(bitmap));

		setImageBackground(currentPosition);

		new Thread(xmpprunnable).start();
	}


	Runnable xmpprunnable = new Runnable(){
		@Override
		public void run() {
			// TODO: http request.
			Message msg = new Message();
			msg.what=4;
			handler.sendMessage(msg);
		}
	};



	/**
	 * 设置选中的tip的背景
	 * @param selectItems
	 */
	private void setImageBackground(int selectItems){
		for(int i=0; i<tips.length; i++){
			if(i == selectItems){
				tips[i].setBackgroundResource(R.mipmap.page_indicator_focused);
			}else{
				tips[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
			}
		}
	}


	@Override
	public View makeView() {
		final ImageView i = new ImageView(this);
		i.setBackgroundColor(0xff000000);
		i.setScaleType(ImageView.ScaleType.CENTER_CROP);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT));
		return i ;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:{
				//手指按下的X坐标
				downX = event.getX();
				break;
			}
			case MotionEvent.ACTION_UP:{
				float lastX = event.getX();
				//抬起的时候的X坐标大于按下的时候就显示上一张图片
				if(lastX > downX){
					if(currentPosition > 0){
						//设置动画，这里的动画比较简单，不明白的去网上看看相关内容
						mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.left_in));
						mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_out));
						currentPosition --;
						//Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imgIds[currentPosition % imgIds.length]);
						//mImageSwitcher.setImageDrawable(new BitmapDrawable(bitmap));

						mImageSwitcher.setImageResource(imgIds[currentPosition % imgIds.length]);
						setImageBackground(currentPosition);
					}else{
						Toast.makeText(getApplication(), "已经是第一张", Toast.LENGTH_SHORT).show();
					}
				}

				if(lastX < downX){
					if(currentPosition < imgIds.length - 1){
						mImageSwitcher.setInAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.right_in));
						mImageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getApplication(), R.anim.lift_out));
						currentPosition ++ ;
						//Bitmap bitmap = ImageLoader.getInstance().loadImageSync(imgIds[currentPosition]);
						//mImageSwitcher.setImageDrawable(new BitmapDrawable(bitmap));

						mImageSwitcher.setImageResource(imgIds[currentPosition]);
						setImageBackground(currentPosition);
					}else{
						Toast.makeText(getApplication(), "到了最后一张", Toast.LENGTH_SHORT).show();
					}
				}
			}

			break;
		}

		return true;
	}

	private void LoadData() {
		if (!isFinishing()) {
	            showDialog(DIALOG_PROGRESS);
	    } else {
	            // 如果当前页面已经关闭，不进行登录操作
	       return;
	    }
		//MarketAPI.dynamics(getApplicationContext(), this, myapplication.getUserId(), "xiaoqu","0","10");//
		MarketAPI.gonggao(getApplicationContext(), this);
	}



	protected void initSlidingMenu() {
		side_drawer = new DrawerView(this).initSlidingMenu();
	}
	
	private void initViews() {
		linearlayout_body1= (LinearLayout) this.findViewById(R.id.body1);
		linearlayout_body2= (LinearLayout) this.findViewById(R.id.body2);
		//linearlayout_body3= (LinearLayout) this.findViewById(R.id.body3);
		top_head = (ImageView) findViewById(R.id.top_head);
		top_more = (ImageView) findViewById(R.id.top_more);	
		top_more.setVisibility(View.VISIBLE);
		sos_btn=(Button) findViewById(R.id.sos_btn);
		can_sos_btnmap=(Button) findViewById(R.id.can_sos_btnmap);
		headtop_left_count = (BadgeView) findViewById(R.id.headtop_left_count);
		gonggao=(TextView) findViewById(R.id.gonggao);
		//lstv = (ListView) findViewById(R.id.nearnewlv);
		headtop_left_count.setOnClickListener(this);
		headtop_left_count.setVisibility(View.GONE);		
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
		top_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,SearchActivity.class);									
				startActivity(intent);
				
				
			}
		});
		//求帮助
		sos_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,PublishActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("infocatagroy", 0);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		//我能帮
		can_sos_btnmap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MainActivity.this,NearActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("infocatagroy", 3);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		
		gonggao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String id=v.getTag().toString();
				Uri uri = Uri.parse(myapplication.getlocalhost()+"wap/gonggao.php?id="+id);  
				Intent it = new Intent(Intent.ACTION_VIEW, uri);  
				startActivity(it);
			}
		});

		reportday_btn= (Button) this.findViewById(R.id.day_button);
		reportday_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(MainActivity.this,DayActivity.class);
				startActivity(intent);

			}
		});

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

	private void initlsb() {
		// TODO Auto-generated method stub
		mLocationClient = new LocationClient(this.myapplication);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000*20;
	
		option.setScanSpan(span);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	
		mLocationClient.registerLocationListener(new BDLocationListener() {
	
			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				if (location == null) {
					//Log.v(TAG, "HomeActivity location empty");
					return;
				}
				nLatitude = location.getLatitude();
				nLontitude = location.getLongitude();
				Log.i("mylog", nLatitude+"-" + nLontitude);
				myapplication.setLat(String.valueOf(nLatitude));
				myapplication.setLng(String.valueOf(nLontitude));
				myapplication.updatelocation();
			}	
		});
		mLocationClient.start();		
		mLocationClient.requestLocation();		
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		finish();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {		
		case R.id.headtop_left_count:
			 headtop_left_count.setVisibility(View.GONE);	
			 headtop_left_count.setText("0");
			//Intent intent=new Intent(MainActivity.this,NoticeActivity.class);

			Intent intent=new Intent(MainActivity.this,RecentActivity.class);
			startActivity(intent);
			    
			break;
		default:
			break;
		}
	}



	Runnable updataRun = new Runnable(){	
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Looper.prepare();
			CheckVersionTask();
			Looper.loop();
		}  
		
	};
	Handler errorhandler = new Handler(){
	    @Override
	    public void handleMessage(Message msg) {
	        super.handleMessage(msg);
	        Bundle data = msg.getData();		    
	        String error = data.getString("error");
	        Toast.makeText(MainActivity.this, error,Toast.LENGTH_SHORT).show();				
	    }
	};

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

								new AlertDialog.Builder(MainActivity.this).setTitle("确认升级吗？")
										.setIcon(android.R.drawable.ic_dialog_info)
										.setPositiveButton("升级", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												Intent intent = new Intent(MainActivity.this,UpdateService.class);
												intent.putExtra("Key_App_Name",appName);
												intent.putExtra("Key_Down_Url",myapplication.getlocalhost()+downUrl);
												startService(intent);
											}
										})
										.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												// 点击“返回”后的操作,这里不设置没有任何操作
											}
										}).show();

								
							}  
						}
					}
		 }	
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				new Thread(updataRun).start();			
				break;
			case 2:
				Bundle data = msg.getData();		    
			    int num = data.getInt("num");
			    String v=headtop_left_count.getText().toString();			    
			    headtop_left_count.setVisibility(View.VISIBLE);	
			    headtop_left_count.setText(String.valueOf(num));
			   //下面的也要更新吗
				
				break;
			case 4:
					if (!NetworkUtils.isNetConnected(myapplication)) {
						T.showShort(myapplication, "当前无网络连接！");
						return;
					}
					myapplication.getInstance().startxmpp();
				break;
	/*		case 3:
				adapter = new DynamicListViewAdapter(MainActivity.this, dataList);
			    lstv.setAdapter(adapter);*/
			default:
				break;
			}			
		};
	};
	
	@Override
	public void onNewMessage(BbMessage message) {
		// TODO Auto-generated method stub		
		Message msg = handler.obtainMessage();			
		msg.what = 2;	
		Bundle data = new Bundle();
		NoticeDB db=new NoticeDB(MainActivity.this);
		data.putInt("num",Integer.parseInt(String.valueOf(db.unreadnum())));	
		msg.setData(data);
		handler.sendMessage(msg);
	}
	@Override
	public void onDestroy() {
		BbPushMessageReceiver.msgListeners.remove(this);
		BbPushMessageReceiver.msgReadListeners.remove(this);
		PushManager.stopWork(getApplicationContext());
		XmppTool.getInstance(this).closeConnection();//关闭xmpp链接
		super.onDestroy();
	}
	
	

	    public static int getScreenWidth(Context context) { 
	        WindowManager manager = (WindowManager) context 
	                .getSystemService(Context.WINDOW_SERVICE); 
	        Display display = manager.getDefaultDisplay(); 
	        return display.getWidth(); 
	    } 
	    //获取屏幕的高度 
	    public static int getScreenHeight(Context context) { 
	        WindowManager manager = (WindowManager) context 
	                .getSystemService(Context.WINDOW_SERVICE); 
	        Display display = manager.getDefaultDisplay(); 
	        return display.getHeight(); 
	    }


		@Override
		public void onReadMessage() {
			// TODO Auto-generated method stub
			//更新操作
			 headtop_left_count.setVisibility(View.GONE);	
			 headtop_left_count.setText("0");
		}
	
		private long touchTime = 0;
		private long waitTime = 2000;		
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
		
		
		@Override
	    protected void onPrepareDialog(int id, Dialog dialog) {
	        super.onPrepareDialog(id, dialog);

	        if (dialog.isShowing()) {
	            dialog.dismiss();
	        }
	    }	
	 @Override
	    protected Dialog onCreateDialog(int id) {

	        switch (id) {
	        case DIALOG_PROGRESS:
	            ProgressDialog mProgressDialog = new ProgressDialog(this);
	            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	            mProgressDialog.setMessage(getString(R.string.init_data));
	            return mProgressDialog;

	        default:
	            return super.onCreateDialog(id);
	        }
	    }

		//MarketAPI.getHomeRecommend(getApplicationContext(), this, 0, 50);//
		@Override
		public void onSuccess(int method, Object obj) {
			// TODO Auto-generated method stub
			  switch (method) {
		        case MarketAPI.ACTION_GONGGAO:
		        	 try{
		                 dismissDialog(DIALOG_PROGRESS);
		             }catch (IllegalArgumentException e) {
		             }
		            HashMap<String, String> result1 = (HashMap<String, String>) obj;
		            String JsonContext1=result1.get("gonggao");   
		            if(JsonContext1.length()>0)
    				{
		            	JSONArray jsonarray;
		            	try {
							jsonarray = new JSONArray(JsonContext1);
							JSONObject jsonobject = jsonarray.getJSONObject(0);						
							String id = jsonobject.getString("id");
							String title = jsonobject.getString("title");
							gonggao.setText(title);
							gonggao.setTag(id);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							 Utils.makeEventToast(MainActivity.this, "gonggao xml解释错误",false);
						}
						
    				}
		        	break;
		        default:
		            break;
		        }
		}


		@Override
		public void onError(int method, int statusCode) {
			// TODO Auto-generated method stub
			 switch (method) {
		        case MarketAPI.ACTION_LOGIN:            
		            // 隐藏登录框
		            try{
		                dismissDialog(DIALOG_PROGRESS);
		            }catch (IllegalArgumentException e) {
		            }            		            
		            break;
		            
		        case MarketAPI.ACTION_GONGGAO:     
		            // 隐藏登录框		                    		            
		            break;
		        default:
		            break;
		        }
		}
}
