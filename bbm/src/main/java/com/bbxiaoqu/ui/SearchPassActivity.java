package com.bbxiaoqu.ui;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.service.User;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.ui.LoginActivity;
import com.bbxiaoqu.ui.main.MainActivity;
import com.bbxiaoqu.view.BaseActivity;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class SearchPassActivity extends BaseActivity {
	private String TAG="SearchPassActivity";
	private DemoApplication myapplication;
	EditText telphone_edit;
	EditText authoncode_edit;
	Button nextbtn,btn_verf;
	String authcode="";
	String tlephone;
	private TimeCount time;
	String lastauthcode="9811";


	public ImageView top_more;
	private TextView title;
	private TextView right_text;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchpass);
		 myapplication = (DemoApplication) this.getApplication();
		loadui();
		time = new TimeCount(60000, 1000);//构造CountDownTimer对象
		btn_verf.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tlephone = telphone_edit.getText().toString().trim();
				if(tlephone.length()<1)
				{
					Toast.makeText(SearchPassActivity.this, "请填写电话号码", Toast.LENGTH_LONG);
					return;
				}
				new Thread(getauthcodeRun).start();
				time.start();//开始计时
			}
		});


		nextbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			

				tlephone = telphone_edit.getText().toString().trim();
				authcode=authoncode_edit.getText().toString().trim();

				if(tlephone.equals(""))
				{
					Toast.makeText(SearchPassActivity.this, "请填写电话号码", Toast.LENGTH_LONG);
					return;
				}
				if(!authcode.equals(lastauthcode))
				{
					Toast.makeText(SearchPassActivity.this, "验证码不正确",Toast.LENGTH_SHORT).show();
					return;
				}

				//Log.i("TAG", pass + "_" + tlephone);
				//new Thread(saveRun).start();
				/*Intent intent = new Intent(SearchPassActivity.this,	ResetPassActivity.class);
				startActivity(intent);*/

				Intent intent = new Intent(getApplicationContext(), ResetPassActivity.class);
				//以下二个为OtherActivity传参数
				intent.putExtra("tlephone", tlephone);

				/*//也可以使用Bundle来传参数
				Bundle bundle = new Bundle();
				bundle.putString("Name1", "eboy1");
				bundle.putInt("Age1", 23);
				intent.putExtras(bundle);*/
				startActivity(intent);
			}
		});

	/*	authoncode_edit.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				Log.d(TAG, "onTextChanged 被执行---->s=" + s + "----start="+ start
						+ "----before="+before + "----count" +count); temp = s;
			}

			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				Log.d(TAG, "beforeTextChanged 被执行----> s=" + s+"----start="+ start
						+ "----after="+after + "----count" +count);
			}

			public void afterTextChanged(Editable s) {
				Log.d(TAG, "afterTextChanged 被执行---->" + s);
				selectionStart = authoncode_edit.getSelectionStart();
				selectionEnd = authoncode_edit.getSelectionEnd();
				if(s.equals(lastauthcode))
				{
					nextbtn.setEnabled(true);
				}
			}
		});*/

		
	}
	
	

	private LocationManager locationManager;
	private String locationProvider;
	private Location location;
	private void loadui() {
		title = (TextView) findViewById(R.id.title);
		right_text = (TextView) findViewById(R.id.right_text);
		right_text.setVisibility(View.VISIBLE);
		right_text.setClickable(true);
		top_more = (ImageView) findViewById(R.id.top_more);
		top_more.setVisibility(View.GONE);
		title.setText("重设密码");
		right_text.setText("");
		top_more = (ImageView) findViewById(R.id.top_more);
		top_more.setVisibility(View.GONE);
		//nicknameRegister_edit= (EditText) findViewById(R.id.nicknameRegister);
		telphone_edit = (EditText) findViewById(R.id.tlephoneRegister);
		authoncode_edit= (EditText) findViewById(R.id.authoncode_edit);
		btn_verf= (Button) findViewById(R.id.btn_verf);
		nextbtn= (Button) findViewById(R.id.nextbtn);




	}
	
	Runnable getauthcodeRun = new Runnable() {
		public void run() {
			
			String target = myapplication.getlocalhost()+"getauthcode.php?show=true";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("_telphone", telphone_edit.getText().toString()));// 电话号
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				String authcode="";
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					authcode = EntityUtils.toString(httpResponse.getEntity());
					
				} else {
					authcode ="";
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("authcode", authcode);
				msg.setData(data);
				authonhandler.sendMessage(msg);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	Handler authonhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			lastauthcode=data.getString("authcode").trim();//赋值
			authoncode_edit.setText(data.getString("authcode").trim());//赋值
		}
	};
	



	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
		}
		@Override
		public void onFinish() {//计时完毕时触发
			btn_verf.setText("重新验证");
			btn_verf.setClickable(true);
			time.cancel();
		}
		@Override
		public void onTick(long millisUntilFinished){//计时过程显示
			btn_verf.setClickable(false);
			btn_verf.setText(millisUntilFinished /1000+"秒");
		}
	}
}
