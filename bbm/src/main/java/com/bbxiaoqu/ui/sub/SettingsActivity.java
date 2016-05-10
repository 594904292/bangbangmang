package com.bbxiaoqu.ui.sub;


import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.AboutActivity;
import com.bbxiaoqu.ui.DataCleanManager;
import com.bbxiaoqu.ui.SearchActivity;
import com.bbxiaoqu.ui.Setting;
import com.bbxiaoqu.ui.SuggestActivity;
import com.bbxiaoqu.update.UpdataInfo;
import com.bbxiaoqu.update.UpdateManager;
import com.bbxiaoqu.update.UpdateService;
import com.bbxiaoqu.view.BaseActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SettingsActivity extends BaseActivity implements OnClickListener{
	TextView title;
	TextView right_text;
	public ImageView top_more;
	LinearLayout clear;
	LinearLayout update;
	LinearLayout suggest;
	LinearLayout help;
	private UpdateManager mUpdateManager;
	private DemoApplication myapplication;

	private String OldVersionName="";
	private String NewVersionName="";
	public static final String appName = "updatebbm";
	public static final String downUrl = "bbm.apk";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		myapplication = (DemoApplication) this.getApplication();
		initView();
		initData();
		clear=(LinearLayout)findViewById(R.id.clear);
		update=(LinearLayout)findViewById(R.id.update);
		suggest=(LinearLayout)findViewById(R.id.suggest);
		help=(LinearLayout)findViewById(R.id.use_help);
		
		clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataCleanManager.cleanApplicationData(myapplication.getApplicationContext());
				
			}
		});
			
		update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread(updataRun).start();
				/*// TODO Auto-generated method stub
				// 这里来检测版本是否需要更新
				try {
					//mUpdateManager = new UpdateManager(SettingsActivity.this,myapplication.getlocalhost());
					//mUpdateManager.checkUpdateInfo();
				*//*	*//*
					if (!NetworkUtils.isNetConnected(myapplication)) {			
						T.showShort(myapplication, "当前无网络连接,请稍后再试！");
						return;
					}
					new AlertDialog.Builder(SettingsActivity.this).setTitle("确认升级吗？")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setPositiveButton("升级", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(SettingsActivity.this,UpdateService.class);
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

				} catch (Exception e) {
					e.printStackTrace();
				}*/
			}
		});
		suggest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent Intent1 = new Intent();
				Intent1.setClass(SettingsActivity.this, SuggestActivity.class);
				startActivity(Intent1);				
			}
		});
		help.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent Intent1 = new Intent();
				Intent1.setClass(SettingsActivity.this, AboutActivity.class);
				Bundle Bundle1 = new Bundle();
				Bundle1.putString("type", "about");
				Intent1.putExtras(Bundle1);
				startActivity(Intent1);				
			}
		});
	}
	
	
	private void initView() {
		title = (TextView)findViewById(R.id.title);
		right_text = (TextView)findViewById(R.id.right_text);
		right_text.setVisibility(View.VISIBLE);
		right_text.setClickable(true);
		right_text.setOnClickListener(this);
		top_more = (ImageView) findViewById(R.id.top_more);	
		top_more.setVisibility(View.VISIBLE);
		top_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(SettingsActivity.this,SearchActivity.class);									
				startActivity(intent);
				
				
			}
		});

	}

	private void initData() {
		title.setText("设置");
		right_text.setText("");
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
//		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.right_text:
			
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

	public static UpdataInfo getUpdataInfo(InputStream is) throws Exception{
		XmlPullParser parser = Xml.newPullParser();
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
	private String getVersionName(){
		//获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		//getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return packInfo.versionName;
	}

	Handler errorhandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String error = data.getString("error");
			Toast.makeText(SettingsActivity.this, error,Toast.LENGTH_SHORT).show();
		}
	};

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
					T.showShort(SettingsActivity.this, "已是最新版本:"+OldVersionName+"！");
				}else{
					if(Double.parseDouble(OldVersionName)<Double.parseDouble(info.getVersion()))
					{

						new AlertDialog.Builder(SettingsActivity.this).setTitle("确认升级吗？")
								.setIcon(android.R.drawable.ic_dialog_info)
								.setPositiveButton("升级", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent intent = new Intent(SettingsActivity.this,UpdateService.class);
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
}
