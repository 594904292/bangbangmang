package com.bbxiaoqu.ui.user;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.service.User;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.ui.LoginActivity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class RegisterActivity extends Activity {
	private DemoApplication myapplication;
	EditText password_edit;
	EditText telphone_edit;	
	EditText authoncode_edit;
	Button register_btn,btn_verf,login_btn;
	String pass;
	String tlephone;
	private TimeCount time;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		 myapplication = (DemoApplication) this.getApplication();
		loadui();
		time = new TimeCount(60000, 1000);//构造CountDownTimer对象
		//////////////////////
		//创建电话管理
		////////////////////////
		btn_verf.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tlephone = telphone_edit.getText().toString().trim();
				if(tlephone.length()<1)
				{
					Toast.makeText(RegisterActivity.this, "请填写电话号码", Toast.LENGTH_LONG);
					return;
				}
				new Thread(getauthcodeRun).start();
				time.start();//开始计时
			}
		});
		
		
		register_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			
				pass = password_edit.getText().toString().trim();
				tlephone = telphone_edit.getText().toString().trim();
				
				if(pass.equals(""))
				{
					Toast.makeText(RegisterActivity.this, "请填写用户密码", Toast.LENGTH_LONG);
					return;
				}
				if(tlephone.equals(""))
				{
					Toast.makeText(RegisterActivity.this, "请填写电话号码", Toast.LENGTH_LONG);
					return;
				}
				Log.i("TAG", pass + "_" + tlephone);				
				new Thread(saveRun).start();
			}
		});
		
		login_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
				startActivity(intent);
			}
		});
		
	}
	
	

	private LocationManager locationManager;  
	private String locationProvider;  
	private Location location;
	private void loadui() {
		//nicknameRegister_edit= (EditText) findViewById(R.id.nicknameRegister);
		password_edit = (EditText) findViewById(R.id.passwordRegister);
		telphone_edit = (EditText) findViewById(R.id.tlephoneRegister);	
		authoncode_edit= (EditText) findViewById(R.id.authoncode_edit);	
		
		register_btn = (Button) findViewById(R.id.registerok);
		btn_verf= (Button) findViewById(R.id.btn_verf);
		login_btn= (Button) findViewById(R.id.login);
	}
	
	Runnable getauthcodeRun = new Runnable() {
		public void run() {
			
			String target = myapplication.getlocalhost()+"getauthcode.php";
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
			authoncode_edit.setText(data.getString("authcode").trim());
		}
	};
	
	Runnable saveRun = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			String pass = password_edit.getText().toString().trim();
			String telnum = telphone_edit.getText().toString().trim();
			String authoncode = authoncode_edit.getText().toString().trim();
			//String nicknameRegister = nicknameRegister_edit.getText().toString().trim();
			int result;
			String target = myapplication.getlocalhost()+"save.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			//paramsList.add(new BasicNameValuePair("_username", nicknameRegister));// 公司代号
			paramsList.add(new BasicNameValuePair("_userid", telnum));// 公司代号
			paramsList.add(new BasicNameValuePair("_telphone", telnum));// 公司代号
			paramsList.add(new BasicNameValuePair("_password", pass));// 公司代号			
			paramsList.add(new BasicNameValuePair("_authoncode", authoncode));// 公司代号
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
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("result", result);
				msg.setData(data);
				savehandler.sendMessage(msg);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	
	
	Handler savehandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			int result = data.getInt("result");
			Log.i("mylog", "请求结果-->" + result);
			if (result == 1) {
				Toast.makeText(RegisterActivity.this, "保存成功",Toast.LENGTH_SHORT).show();
				
				UserService uService = new UserService(RegisterActivity.this);
				User user = new User();
				user.setUsername(tlephone);
				user.setPassword(pass);
				user.setTelphone(tlephone);	
				user.setHeadface("");
				uService.register(user);	
				new Thread(sysncxmpp).start();
				Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();	
				
				Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
				startActivity(intent);
			}else if (result == 2) {
				Toast.makeText(RegisterActivity.this, "用户ID已注册",Toast.LENGTH_SHORT).show();
			}else if (result == 3) {
				Toast.makeText(RegisterActivity.this, "手机号已注册",Toast.LENGTH_SHORT).show();
			} else if (result == 4) {
				Toast.makeText(RegisterActivity.this, "验证码不正确",Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RegisterActivity.this, "保存失败",Toast.LENGTH_SHORT).show();
			}
		}
	};

Runnable sysncxmpp = new Runnable(){  
		
		@Override  
		public void run() {  
			//XmppTool.getInstance().createAccount(tlephone, pass);//向xmpp服务器注册帐号
		}};
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
