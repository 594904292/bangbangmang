package com.bbxiaoqu.ui.sub;


import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.AboutActivity;
import com.bbxiaoqu.ui.DataCleanManager;
import com.bbxiaoqu.ui.SearchActivity;
import com.bbxiaoqu.ui.SuggestActivity;
import com.bbxiaoqu.update.UpdateManager;
import com.bbxiaoqu.update.UpdateService;
import com.bbxiaoqu.view.BaseActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


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
	public static final String appName = "updatebbm";
	/*******down APP address*******/
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
				// TODO Auto-generated method stub
				// 这里来检测版本是否需要更新
				try {
					//mUpdateManager = new UpdateManager(SettingsActivity.this,myapplication.getlocalhost());
					//mUpdateManager.checkUpdateInfo();
				/*	*/
					if (!NetworkUtils.isNetConnected(myapplication)) {			
						T.showShort(myapplication, "当前无网络连接,请稍后再试！");
						return;
					}
					/*Intent mIntent = new Intent();
					mIntent.setAction("com.bbxiaoqu.comm.service.UpdateService");//你定义的service的action
					mIntent.putExtra("Key_App_Name",appName);
					mIntent.putExtra("Key_Down_Url",myapplication.getlocalhost()+downUrl);
					mIntent.setPackage(getPackageName());//这里你需要设置你应用的包名
					myapplication.startService(mIntent);*/
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
				}
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
}
