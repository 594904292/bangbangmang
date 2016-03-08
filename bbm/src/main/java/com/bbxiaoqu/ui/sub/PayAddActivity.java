package com.bbxiaoqu.ui.sub;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
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

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View.OnClickListener;

public class PayAddActivity extends BaseActivity {
	private DemoApplication myapplication;
	TextView txt_score;
	TextView righgtitle;
	private List<String> list = new ArrayList<String>();	
	private Spinner mySpinner;
	private ArrayAdapter<String> adapter;
	private Button btn_buy;
	String tool="0";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payadd);
		myapplication = (DemoApplication) this.getApplication();
		initView();
		initData();
		list.add("支付宝");
		initui();
		btn_buy=(Button) findViewById(R.id.btn_buy);
		btn_buy.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(txt_score.equals(""))
				{
					Toast.makeText(PayAddActivity.this, "请填写购买的积分", Toast.LENGTH_LONG);
					return;
				}								
				new Thread(saveRun).start();
			}
		});
		
	}

	private void initui() {
		txt_score = (TextView) findViewById(R.id.txt_score);
		mySpinner = (Spinner) findViewById(R.id.Spinner_paytype);
		// 第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		// 第三步：为适配器设置下拉列表下拉时的菜单样式。
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 第四步：将适配器添加到下拉列表上
		mySpinner.setAdapter(adapter);
		// 第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
		mySpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						/* 将所选mySpinner 的值带入myTextView 中 */
						//myTextView.setText("您选择的是：" + adapter.getItem(arg2));
						/* 将mySpinner 显示 */
						if(arg2==0)
						{
							tool="1";
						}
						
						arg0.setVisibility(View.VISIBLE);
					}

					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
						//myTextView.setText("NONE");
						
						arg0.setVisibility(View.VISIBLE);
					}
				});
		/* 下拉菜单弹出的内容选项触屏事件处理 */
		mySpinner.setOnTouchListener(new Spinner.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				/** 
	                 *  
	                 */
				return false;
			}
		});
		/* 下拉菜单弹出的内容选项焦点改变事件处理 */
		mySpinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void initView() {
		txt_score = (TextView) findViewById(R.id.title);
		righgtitle = (TextView) findViewById(R.id.right_text);		
	}

	private void initData() {
		txt_score.setText("支付");
		
	}
	
	
	Runnable saveRun = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			String score = txt_score.getText().toString().trim();
			
			int result;
			String target = myapplication.getlocalhost()+"payadd.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sDateFormat.format(new java.util.Date());
			
			paramsList.add(new BasicNameValuePair("_user", myapplication.getUserId()));// 公司代号
			paramsList.add(new BasicNameValuePair("_score", score));// 公司代号
			paramsList.add(new BasicNameValuePair("_money", score));// 公司代号
			paramsList.add(new BasicNameValuePair("_tool", tool));// 公司代号
			paramsList.add(new BasicNameValuePair("_status", "0"));// 公司代号
			paramsList.add(new BasicNameValuePair("_date", date));// 公司代号
			
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
				Toast.makeText(PayAddActivity.this, "购买成功",Toast.LENGTH_SHORT).show();
				
							
				Intent intent = new Intent(PayAddActivity.this,PayActivity.class);
				startActivity(intent);			
			} else {
				Toast.makeText(PayAddActivity.this, "购买失败",Toast.LENGTH_SHORT).show();
			}
		}
	};


}
