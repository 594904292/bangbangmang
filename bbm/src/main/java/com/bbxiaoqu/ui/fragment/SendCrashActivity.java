package com.bbxiaoqu.ui.fragment;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
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
import com.bbxiaoqu.comm.crash.UploadUtil;
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
import com.bbxiaoqu.ui.main.MainActivity;
import com.bbxiaoqu.ui.sub.SettingsActivity;
import com.bbxiaoqu.ui.sub.UserInfoActivity;
import com.bbxiaoqu.update.UpdataInfo;
import com.bbxiaoqu.update.UpdateManager;
import com.bbxiaoqu.update.UpdateService;
import com.bbxiaoqu.view.BaseActivity;
import com.bbxiaoqu.view.DrawerView;
import com.bbxiaoqu.widget.AutoListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Button;
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
public class SendCrashActivity extends BaseActivity {
	private TextView textView;
	TextView title;
	TextView back;
	TextView right_text;
	Button upload_btn;
	Button return_btn;
	private static String localFileUrl = "";
	private static final String uploadUrl = "http://www.bbxiaoqu.com/api/ReceiveCrash.php";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sendcrash);	
		initView();
		initData();
		upload_btn= (Button)findViewById(R.id.upload_btn);
		upload_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new SendCrashLog().execute("");
			}
		});
		
		return_btn= (Button)findViewById(R.id.return_btn);
		return_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				finish();
			}
		});
		
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) 
		{
			String sdcardPath = Environment.getExternalStorageDirectory().getPath();
			//localFileUrl = sdcardPath + "/bbm/crash/crash.log";
			//Log.d("TAG", "文     件："+localFileUrl);			
			File file=new File(sdcardPath + "/bbm/crash/");
			File[] tempList = file.listFiles();
			Arrays.sort(tempList, new Comparator<File>() {
				public int compare(File f1, File f2) {
					long diff = f1.lastModified() - f2.lastModified();
					if (diff > 0)
						return 1;
					else if (diff == 0)
						return 0;
					else
						return -1;
				}

				public boolean equals(Object obj) {
					return true;
				}

			});
			localFileUrl=tempList[tempList.length-1].toString();
			Log.d("TAG", "文     件："+tempList[tempList.length-1]);
		}
		
		
	}

	private void initView() {
		title = (TextView)findViewById(R.id.title);
		right_text = (TextView)findViewById(R.id.right_text);		
		right_text.setVisibility(View.GONE);
		//right_text.setClickable(true);	
		
		back = (TextView)findViewById(R.id.back);
		back.setVisibility(View.GONE);
	}

	private void initData() {
		title.setText("上传异常");
		right_text.setText("");
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		finish();
	}
	
	
	
	public class SendCrashLog extends AsyncTask<String, String, Boolean> 
	{
		public SendCrashLog() {  }

		@Override
		protected Boolean doInBackground(String... params) 
		{
			Log.d("TAG", "向服务器发送崩溃信息");
			UploadUtil.uploadFile(new File(localFileUrl), uploadUrl);
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			Toast.makeText(getApplicationContext(), "成功将崩溃信息发送到服务器，感谢您的反馈", 1000).show();
			Log.d("TAG", "发送完成");
			
		}
	}
	
}