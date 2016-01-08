package com.bbxiaoqu.ui.main;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
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
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.adapter.ChatMessageAdapter;
import com.bbxiaoqu.adapter.DynamicListViewAdapter;
import com.bbxiaoqu.adapter.ListViewAdapter;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.api.ApiAsyncTask.ApiRequestListener;
import com.bbxiaoqu.bean.BbMessage;
import com.bbxiaoqu.bean.InfoBase;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver;
import com.bbxiaoqu.client.baidu.Utils;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver.onMessageReadListener;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver.onNewMessageListener;
import com.bbxiaoqu.comm.service.User;
import com.bbxiaoqu.comm.service.db.DatabaseHelper;
import com.bbxiaoqu.comm.service.db.MessGzService;
import com.bbxiaoqu.comm.service.db.NoticeDB;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.tool.BuileGestureExt;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.LoginActivity;
import com.bbxiaoqu.ui.NoticeActivity;
import com.bbxiaoqu.ui.PublishActivity;
import com.bbxiaoqu.ui.NoticeActivity.NoticeAdapter;
import com.bbxiaoqu.ui.NoticeActivity.ViewHolder;
import com.bbxiaoqu.ui.SearchActivity;
import com.bbxiaoqu.ui.fragment.HomeActivity;
import com.bbxiaoqu.ui.sub.ChattingActivity;
import com.bbxiaoqu.ui.sub.InfoGzActivity;
import com.bbxiaoqu.ui.sub.UserInfoActivity;
import com.bbxiaoqu.ui.sub.ViewUserInfoActivity;
import com.bbxiaoqu.update.UpdataInfo;
import com.bbxiaoqu.update.UpdateService;
import com.bbxiaoqu.view.BaseActivity;
import com.bbxiaoqu.view.DrawerView;
import com.bbxiaoqu.client.xmpp.ChatListener;
import com.bbxiaoqu.client.xmpp.MessageService;
import com.bbxiaoqu.client.xmpp.ViConnectionListener;
import com.bbxiaoqu.client.xmpp.XmppTool;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends BaseActivity implements OnClickListener ,onNewMessageListener,onMessageReadListener,ApiRequestListener{
	
	private DemoApplication myapplication;
	public ImageView top_head;
	public ImageView top_more;
	public TextView headtop_left_count;
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
	private LinearLayout linearlayout_body3;
	private TextView gonggao;
	
	private DynamicListViewAdapter adapter;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	ListView lstv;
	private static final int DIALOG_PROGRESS = 0;
	// 用户不存在（用户名错误）
	private static final int ERROR_CODE_USERNAME_NOT_EXIST = 211;
	// 用户密码错误
	private static final int ERROR_CODE_PASSWORD_INVALID = 212;

	
	NoticeDB db=new NoticeDB(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		BbPushMessageReceiver.msgListeners.add(this);//新消息
		BbPushMessageReceiver.msgReadListeners.add(this);//点了阅读
		setContentView(R.layout.activity_main_main);		
		myapplication = (DemoApplication) this.getApplication();
		myapplication.getInstance().addActivity(this);		
		/*位置:服务中无法初始位置，先在界面中实现定位*/
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
	}

	private void LoadData() {
		if (!isFinishing()) {
	            showDialog(DIALOG_PROGRESS);
	    } else {
	            // 如果当前页面已经关闭，不进行登录操作
	       return;
	    }
		MarketAPI.dynamics(getApplicationContext(), this, myapplication.getUserId(), "xiaoqu","0","10");//		
		MarketAPI.gonggao(getApplicationContext(), this);
		//new Thread(xmppstartRun).start();
	}



	protected void initSlidingMenu() {
		side_drawer = new DrawerView(this).initSlidingMenu();
	}
	
	private void initViews() {
	
		linearlayout_body1= (LinearLayout) this.findViewById(R.id.body1);
		linearlayout_body2= (LinearLayout) this.findViewById(R.id.body2);
		linearlayout_body3= (LinearLayout) this.findViewById(R.id.body3);
		
		top_head = (ImageView) findViewById(R.id.top_head);
		top_more = (ImageView) findViewById(R.id.top_more);	
		top_more.setVisibility(View.VISIBLE);
		
		sos_btn=(Button) findViewById(R.id.sos_btn);
		can_sos_btnmap=(Button) findViewById(R.id.can_sos_btnmap);
		
		headtop_left_count = (TextView) findViewById(R.id.headtop_left_count);
		gonggao=(TextView) findViewById(R.id.gonggao);
		lstv = (ListView) findViewById(R.id.nearnewlv);

		
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
		lstv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int location, long arg3) {								
				
				if(dataList.get(location).get("actionname").toString().equals("publish"))
				{//查看信息
					Intent Intent1=new Intent(MainActivity.this,ViewActivity.class);	
					Bundle arguments = new Bundle();
					arguments.putString("put", "false");
					arguments.putString("guid",dataList.get(location).get("guid").toString());
					Intent1.putExtras(arguments);
					startActivity(Intent1);
				}else
				{//看人
					Intent Intent1 = new Intent();
					Intent1.setClass(MainActivity.this,ViewUserInfoActivity.class);
					Bundle arguments = new Bundle();
					arguments.putString("userid", dataList.get(location).get("userid").toString());
					Intent1.putExtras(arguments);
					startActivity(Intent1);
				}
			}
		});
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
			Intent intent=new Intent(MainActivity.this,NoticeActivity.class);
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
								Intent intent = new Intent(MainActivity.this,UpdateService.class);
								intent.putExtra("Key_App_Name",appName);
								intent.putExtra("Key_Down_Url",myapplication.getlocalhost()+downUrl);						
								startService(intent);
								
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
			case 3:
				adapter = new DynamicListViewAdapter(MainActivity.this, dataList);
			    lstv.setAdapter(adapter);
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
		        case MarketAPI.ACTION_GETDYNAMICS:
		        	 try{
		                 dismissDialog(DIALOG_PROGRESS);
		             }catch (IllegalArgumentException e) {
		             }
		            HashMap<String, String> result = (HashMap<String, String>) obj;
		            String JsonContext=result.get("daymic");           
		            if(JsonContext.length()>0)
    				{
    					JSONArray jsonarray = null;
    					try {
    						dataList.clear();
    						jsonarray = new JSONArray(JsonContext);
    						for (int i = 0; i < jsonarray.length(); i++) {
    							JSONObject customJson = jsonarray.getJSONObject(i);
    							HashMap<String, Object> item = new HashMap<String, Object>();
    							item.put("id", String.valueOf(customJson.getString("id").toString()));
    							item.put("userid", String.valueOf(customJson.getString("userid").toString()));
    							item.put("username", String.valueOf(customJson.getString("username").toString()));
    							item.put("actionname", String.valueOf(customJson.getString("actionname").toString()));
    							item.put("actiontime", String.valueOf(customJson.getString("actiontime").toString()));
    							item.put("guid", String.valueOf(customJson.getString("guid").toString()));
    							item.put("messdesc", String.valueOf(customJson.getString("messdesc").toString()));    							
    							dataList.add(item);
    						}
    					} catch (JSONException e1) {
    						// TODO Auto-generated catch block
    						Utils.makeEventToast(MainActivity.this, "daymic xml解释错误",false);
    						e1.printStackTrace();
    					}
    					Message msg = handler.obtainMessage();			
    					msg.what = 3;			
    					handler.sendMessage(msg);
    				}
		            break; 
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
