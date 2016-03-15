package com.bbxiaoqu.ui;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.service.User;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.ui.LoginActivity;
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

public class ResetPassActivity extends BaseActivity {
	private DemoApplication myapplication;
	EditText pass1;
	EditText pass2;
	Button reset_btn;
	String passstr;
	String pass1str;
	String tlephone;
	public ImageView top_more;
	private TextView title;
	private TextView right_text;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpass);
		myapplication = (DemoApplication) this.getApplication();
		Intent intent = getIntent(); //用于激活它的意图对象
		tlephone = intent.getStringExtra("tlephone");


		title = (TextView) findViewById(R.id.title);
		right_text = (TextView) findViewById(R.id.right_text);
		right_text.setVisibility(View.VISIBLE);
		right_text.setClickable(true);
		top_more = (ImageView) findViewById(R.id.top_more);
		top_more.setVisibility(View.GONE);
		title.setText("找回密码");
		right_text.setText("");

		pass1 = (EditText) findViewById(R.id.pass1);
		pass2= (EditText) findViewById(R.id.pass2);
		reset_btn = (Button) findViewById(R.id.reset_btn);

		reset_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				passstr=pass1.getText().toString();
				pass1str=pass2.getText().toString();

				Log.i("TAG", pass1str + "_" + tlephone);
				if(passstr.equals(""))
				{
					Toast.makeText(ResetPassActivity.this, "密码不能为空", Toast.LENGTH_LONG);
					return;
				}
				if(pass1str.equals(""))
				{
					Toast.makeText(ResetPassActivity.this, "确认密码不能为空", Toast.LENGTH_LONG);
					return;
				}
				if(!passstr.equals(pass1str))
				{
					Toast.makeText(ResetPassActivity.this, "密码不一致", Toast.LENGTH_LONG);
					return;
				}
				//做一次保存
				Log.i("TAG", pass1str + "_" + tlephone);
				new Thread(saveRun).start();
			}
		});
	}





	Runnable saveRun = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub


			int result;
			String target = myapplication.getlocalhost()+"resetpass.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("_telphone", tlephone));// 公司代号
			paramsList.add(new BasicNameValuePair("_password", pass1str));// 公司代号
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
				Toast.makeText(ResetPassActivity.this, "重设成功",Toast.LENGTH_SHORT).show();
			/*	UserService uService = new UserService(RegisterActivity.this);
				User user = new User();
				user.setUsername(tlephone);
				user.setPassword(pass);
				user.setTelphone(tlephone);
				user.setHeadface("");
				uService.register(user);
				new Thread(sysncxmpp).start();
				Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();
*/
				Intent intent = new Intent(ResetPassActivity.this,LoginActivity.class);
				startActivity(intent);
			}else {
				Toast.makeText(ResetPassActivity.this, "重设失败",Toast.LENGTH_SHORT).show();
			}
		}
	};




}
