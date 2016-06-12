package com.bbxiaoqu;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.widget.RemoteViews;

import com.baidu.mapapi.SDKInitializer;
import com.bbxiaoqu.comm.crash.CrashHandler;
import com.bbxiaoqu.comm.service.db.MessageDB;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.SharePreferenceUtil;
import com.bbxiaoqu.ui.main.MainActivity;
import com.bbxiaoqu.client.xmpp.ChatListener;
import com.bbxiaoqu.client.xmpp.XmppTool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.bugly.crashreport.CrashReport;

public class DemoApplication extends Application {

	static DemoApplication mApplication;
	public static final String TAG = "DemoApplication";
	//6111438
	public final static String API_KEY = "zGbL7fLxRVOu5ccPb3G57DdM";
	public final static String SECRIT_KEY = "kpjjzLA89QWY5cKGDHsoeUajsR2CCqQ1";
	public static final String SP_FILE_NAME = "push_msg_sp";
	public static int mNetWorkState = NetworkUtils.NETWORN_NONE;
	private List<Activity> activityList = new LinkedList<Activity>();
	public String appid;
	public String userId;
	public String nickname;
	public String Password;
	public String headface;	
	public String channelId;
	public String requestId;
	private String localhost;
	public String lat;
	public String lng;
	public static String xmpphost="101.200.194.1";
	public static int xmppport=5222;
	public static String xmppdomain="bbxiaoqu";
	protected Session mSession;
	public static String getXmpphost() {
		return xmpphost;
	}

	public static void setXmpphost(String xmpphost) {
		DemoApplication.xmpphost = xmpphost;
	}

	public static int getXmppport() {
		return xmppport;
	}

	public static void setXmppport(int xmppport) {
		DemoApplication.xmppport = xmppport;
	}

	public static String getXmppdomain() {
		return xmppdomain;
	}

	public static void setXmppdomain(String xmppdomain) {
		DemoApplication.xmppdomain = xmppdomain;
	}

	
	
	public synchronized static DemoApplication getInstance()
	{
		 
		return mApplication;
	}
	
	public int userChoice=0;
	

	public int getUserChoice() {
		return userChoice;
	}

	public void setUserChoice(int userChoice) {
		this.userChoice = userChoice;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
		mSession = Session.get(this);	
		CrashHandler.getInstance().init(this);
	    Log.v(TAG, "application created");
		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext

		SDKInitializer.initialize(this);

		File cacheDir = StorageUtils.getCacheDirectory(this); // 缓存文件夹路径
		ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this)
				.threadPoolSize(10) // default 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2) // default 设置当前线程的优先级
				.denyCacheImageMultipleSizesInMemory()
				.memoryCacheSize(100 * 1024 * 1024) // 100M内存缓存的最大值
				.memoryCacheSizePercentage(13) // default
				.diskCacheSize(200 * 1024 * 1024) // 500 Mb sd卡(本地)缓存的最大值
				.diskCacheFileCount(1000) // 可以缓存的文件数量
				.imageDownloader(new BaseImageDownloader(this,5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间   
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
				.writeDebugLogs() // 打印debug log
				.build(); // 开始构建
		ImageLoader.getInstance().init(configuration);
		CrashReport.initCrashReport(getApplicationContext(), "900024545", false);
	}

	
	@Override
	public void onTerminate() {
		super.onTerminate();		
	}
	
	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getUserId() {
		if(userId==null)
		{
			UserService u=new UserService(this);
			userId=u.getuserid();
		}
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	
	public String getPassword() {
		if(Password==null)
		{
			UserService u=new UserService(this);
			Password=u.getuserpassword(getUserId());
		}
		return Password;
	}

	public void setPassword(String Password) {
		this.Password = Password;
	}
	
	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public int errorCode;

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getNickname() {
		if(nickname==null)
		{
			UserService u=new UserService(this);
			nickname=u.getnickname(getUserId());
		}
		return nickname;
	}


	public void setNickname(String nickname) {
		this.nickname = nickname;
	}


	public String getHeadface() {
		return headface;
	}

	public void setHeadface(String headface) {
		this.headface = headface;
	}


	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}
	

	public String getlocalhost() {
		return "http://api.bbxiaoqu.com/";
	}
	

	public boolean ifpass(String userid) {
		boolean ispass=false;
		UserService u=new UserService(this);
		if (u.ifpass(userid)) {			
			ispass=true;
		}else
		{
			ispass=false;
		}
		return ispass;
	}
	
	public String getheadface() {
		boolean ispass=false;
		UserService u=new UserService(this);
		String headface=u.getheadface(this.getUserId());
		return headface;
	}
	
	
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		XmppTool.getInstance(this).closeConnection();//xmpp下线
		UserService u=new UserService(this);
		u.offline(getUserId());//下线
		mSession.setIslogin(false);

	}

	public void update() {
		new Thread(savechannelIdRun).start();
	}

	Runnable savechannelIdRun = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = getlocalhost()+"updatechannelid.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("_userId", getUserId()));// 公司代号
			paramsList.add(new BasicNameValuePair("_channelId", getChannelId()));// 公司代号
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String json = EntityUtils
							.toString(httpResponse.getEntity());
					result = 1;
				} else {
					result = 0;
				}
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};
	XMPPConnection connection=null;
	ChatManager cm=null;
	public void startxmpp() {
		new Thread(xmppstartRun).start();
		/*try {
			connection=XmppTool.getConnection();
			if(connection!=null) {
				if (connection.isConnected() && !connection.isAuthenticated()) {
					connection.login(getUserId(), getPassword());
				}
				cm = connection.getChatManager();    //取得聊天管理器
				if (cm.getChatListeners().size() == 0) {
					cm.addChatListener(new ChatListener(mApplication));
				}
			}
		} catch (XMPPException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/

		/*new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					XMPPConnection connection=XmppTool.getInstance(mApplication).getConnection();
					if (connection.isConnected() && !connection.isAuthenticated())
					{
						connection.login(getUserId(), getPassword());
					}
					ChatManager cm = connection.getChatManager();    //取得聊天管理器

					cm.addChatListener(new ChatListener(mApplication));

				} catch (XMPPException e) {
					XmppTool.getInstance(mApplication).closeConnection();
				}
			}
		}).start();*/
	}

	Runnable xmppstartRun = new Runnable() {
		@Override
		public void run() {
			connection=XmppTool.getInstance(mApplication).getConnection();
			XmppTool.getInstance(mApplication).login();

		}
			/*try {
				connection=XmppTool.getInstance(mApplication).getConnection();
				if(connection!=null) {
					if (connection.isConnected() && !connection.isAuthenticated()) {
						String a = getUserId();
						String b = getPassword();
						XmppTool.getInstance(mApplication).getConnection().login(a, b);
					}
					cm = connection.getChatManager();    //取得聊天管理器
					if (cm.getChatListeners().size() == 0) {
						cm.addChatListener(new ChatListener(mApplication));
					}
				}
			} catch (XMPPException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}		
		}*/
	};

	public void updatelocation() {
		new Thread(savelocationIdRun).start();
	}

	Runnable savelocationIdRun = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (getUserId() != null && getUserId().length() > 0) {
				int result;
				String target = getlocalhost() + "updatelocation.php";
				HttpPost httprequest = new HttpPost(target);
				List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
				paramsList.add(new BasicNameValuePair("_userId", getUserId()));// 公司代号
				paramsList.add(new BasicNameValuePair("_lat", getLat()));// 公司代号
				paramsList.add(new BasicNameValuePair("_lng", getLng()));// 公司代号
				paramsList.add(new BasicNameValuePair("_os", "android"));// 公司代号
				try {
					httprequest.setEntity(new UrlEncodedFormEntity(paramsList,
							"UTF-8"));
					HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
					HttpResponse httpResponse = HttpClient1.execute(httprequest);
					if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						String json = EntityUtils
								.toString(httpResponse.getEntity());
						result = Integer.parseInt(json);
					} else {
						result = 0;
					}
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	};
}
