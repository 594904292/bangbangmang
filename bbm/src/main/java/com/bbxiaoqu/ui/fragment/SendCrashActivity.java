package com.bbxiaoqu.ui.fragment;


import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.crash.UploadUtil;
import com.bbxiaoqu.ui.main.MainActivity;
import com.bbxiaoqu.view.BaseActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.content.Intent;

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