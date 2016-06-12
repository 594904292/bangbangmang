package com.bbxiaoqu.view;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.Session;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.ui.sub.FriendsActivity;
import com.bbxiaoqu.ui.sub.InfoGzActivity;
import com.bbxiaoqu.ui.sub.MyinfosActivity;
import com.bbxiaoqu.ui.sub.RecentActivity;
import com.bbxiaoqu.ui.sub.SettingsActivity;
import com.bbxiaoqu.ui.user.UserInfoViewActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


public class DrawerView implements OnClickListener{
	private final Activity activity;
	private SlidingMenu localSlidingMenu;	
	private ImageView avatar;
	private TextView username;
	private RelativeLayout userinfo_btn;
	private RelativeLayout message_btn;
	private RelativeLayout  friends_btn;
	private RelativeLayout message_btn_my;
	private RelativeLayout message_btn_bm;
	private RelativeLayout setting_btn;
	private RelativeLayout favorite_btn;
	//private RelativeLayout  feedback_btn;
	private RelativeLayout  offline_btn;
	private List<Map<String, Object>> data;
	private ListView lv;	
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> morelist=null;
	private Button bt;
	private ProgressBar pg;
	private Handler handler;

	private SwitchButton night_mode_btn;
	private TextView night_mode_text;
	protected Session mSession;
	private DemoApplication myapplication;
	public DrawerView(Activity activity) {
		this.activity = activity;
		myapplication = (DemoApplication) this.activity.getApplication();
		mSession = Session.get(myapplication);
	}

	public SlidingMenu initSlidingMenu() {
		localSlidingMenu = new SlidingMenu(activity);
		//localSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
		localSlidingMenu.setMode(SlidingMenu.LEFT);
		localSlidingMenu.setTouchModeAbove(SlidingMenu.SLIDING_WINDOW);
		//localSlidingMenu.setTouchModeBehind(SlidingMenu.SLIDING_CONTENT);
		localSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		localSlidingMenu.setShadowDrawable(R.drawable.shadow);
		localSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		localSlidingMenu.setFadeDegree(0.35F);
		localSlidingMenu.attachToActivity(activity, SlidingMenu.RIGHT);
		
		//localSlidingMenu.setBehindWidthRes(R.dimen.left_drawer_avatar_size);
		localSlidingMenu.setMenu(R.layout.left_drawer_fragment);
		//localSlidingMenu.toggle();
		localSlidingMenu.setSecondaryMenu(R.layout.profile_drawer_right);
		
		localSlidingMenu.setSecondaryShadowDrawable(R.drawable.shadowright);
		localSlidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
					public void onOpened() {						
					}
				});
		localSlidingMenu.setOnClosedListener(new OnClosedListener() {			
			@Override
			public void onClosed() {
				// TODO Auto-generated method stub
				
			}
		});
		initView();

		new Thread(loaduser).start();
		return localSlidingMenu;
	}


	boolean isfirst=true;
	private void initView() {

		night_mode_btn = (SwitchButton)localSlidingMenu.findViewById(R.id.night_mode_btn);
		night_mode_text = (TextView)localSlidingMenu.findViewById(R.id.night_mode_text);
		night_mode_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					Message msg = basehandler.obtainMessage();
					msg.what = 1;
					basehandler.sendMessage(msg);
				}else{
					Message msg = basehandler.obtainMessage();
					msg.what = 2;
					basehandler.sendMessage(msg);
				}
				if(!isfirst) {
					new Thread(saverecvmessRun).start();
					new Thread(saveopenvoiceRun).start();
				}
				isfirst=false;
			}
		});


		userinfo_btn =(RelativeLayout)localSlidingMenu.findViewById(R.id.userinfo_btn);
		userinfo_btn.setOnClickListener(this);
		
		favorite_btn =(RelativeLayout)localSlidingMenu.findViewById(R.id.favorite_btn);
		favorite_btn.setOnClickListener(this);

		message_btn =(RelativeLayout)localSlidingMenu.findViewById(R.id.message_btn);
		message_btn.setOnClickListener(this);
		
		friends_btn =(RelativeLayout)localSlidingMenu.findViewById(R.id.friends_btn);
		friends_btn.setOnClickListener(this);
		
		

		setting_btn =(RelativeLayout)localSlidingMenu.findViewById(R.id.setting_btn);
		setting_btn.setOnClickListener(this);
		
		
		offline_btn=(RelativeLayout)localSlidingMenu.findViewById(R.id.offline_btn);
		offline_btn.setOnClickListener(this);
		
		avatar=(ImageView)localSlidingMenu.findViewById(R.id.avatar);
		username=(TextView)localSlidingMenu.findViewById(R.id.user_name);
		username.setText(myapplication.getUserId());
		if(myapplication.getheadface()!=null&&myapplication.getheadface().length()>0)
		{
			String fileName = myapplication.getlocalhost()+"uploads/" +myapplication.getheadface();
			 ImageLoader.getInstance().displayImage(fileName, avatar, ImageOptions.getOptions());  
		}
	}

	Runnable loaduser = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"login.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sDateFormat.format(new java.util.Date());
			paramsList.add(new BasicNameValuePair("_userid", myapplication.getUserId()));
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String json = EntityUtils
							.toString(httpResponse.getEntity());
					try {
						JSONObject jsonobj = new JSONObject(json);
						String isrecvmess = jsonobj.getString("isrecvmess");
						String isopenvoice = jsonobj.getString("isopenvoice");
						if(isrecvmess.equals("1"))
						{
							//mSession.setIsNotic(true);
							Message msg = basehandler.obtainMessage();
							msg.what = 1;
							basehandler.sendMessage(msg);
						}else
						{
							//mSession.setIsNotic(false);
							Message msg = basehandler.obtainMessage();
							msg.what = 1;
							basehandler.sendMessage(msg);
						}
						/*if(mSession.getIsNotic())
						{
							night_mode_btn.setChecked(true);
						}else
						{
							night_mode_btn.setChecked(false);
						}

						if(night_mode_btn.isChecked()){
							night_mode_text.setText(activity.getResources().getString(R.string.action_open_mode));
						}else{
							night_mode_text.setText(activity.getResources().getString(R.string.action_close_mode));
						}*/
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


					result = 1;
				} else {
					result = 0;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};


	Runnable saverecvmessRun = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"resetuserfield.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sDateFormat.format(new java.util.Date());
			paramsList.add(new BasicNameValuePair("userid", myapplication.getUserId()));
			paramsList.add(new BasicNameValuePair("field", "isrecvmess"));
			if(mSession.getIsNotic()) {
				paramsList.add(new BasicNameValuePair("fieldvalue", "1"));// 正文
			}else {
				paramsList.add(new BasicNameValuePair("fieldvalue", "0"));// 正文
			}
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String json = EntityUtils
							.toString(httpResponse.getEntity());
					System.out.println(json);
					result = 1;
				} else {
					result = 0;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	Runnable saveopenvoiceRun = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"resetuserfield.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sDateFormat.format(new java.util.Date());
			paramsList.add(new BasicNameValuePair("userid", myapplication.getUserId()));
			paramsList.add(new BasicNameValuePair("field", "isopenvoice"));
			if(mSession.getIsNotic()) {
				paramsList.add(new BasicNameValuePair("fieldvalue", "1"));// 正文
			}else {
				paramsList.add(new BasicNameValuePair("fieldvalue", "0"));// 正文
			}
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String json = EntityUtils
							.toString(httpResponse.getEntity());
					System.out.println(json);
					result = 1;
				} else {
					result = 0;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};


	@Override
	public void onClick(View v) {	
		System.out.println(v.getId());
		switch (v.getId())
		{
			case R.id.userinfo_btn:
				activity.startActivity(new Intent(activity,UserInfoViewActivity.class));
				activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);				
				break;
			case R.id.favorite_btn:
				activity.startActivity(new Intent(activity,RecentActivity.class));				
				activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				break;
			case R.id.friends_btn:
				activity.startActivity(new Intent(activity,FriendsActivity.class));				
				activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				break;
			case R.id.message_btn:
				activity.startActivity(new Intent(activity,InfoGzActivity.class));
				activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);			
				break;	
			case R.id.setting_btn:
				activity.startActivity(new Intent(activity,SettingsActivity.class));
				activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				break;
			case R.id.offline_btn:
				myapplication.exit();
				//System.exit(0);
				android.os.Process.killProcess(android.os.Process.myPid());
				break;
			default:
				break;
		}
	}



	private Handler basehandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					night_mode_btn.setChecked(true);
					night_mode_text.setText(activity.getResources().getString(R.string.action_open_mode));
					mSession.setIsNotic(true);
					break;
				case 2:
					night_mode_btn.setChecked(false);
					night_mode_text.setText(activity.getResources().getString(R.string.action_close_mode));
					mSession.setIsNotic(false);
					break;
				default:
					break;
			}
		};
	};

}
