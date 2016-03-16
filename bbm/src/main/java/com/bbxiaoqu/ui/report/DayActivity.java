package com.bbxiaoqu.ui.report;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.adapter.EvaluateAdapter;
import com.bbxiaoqu.adapter.FriendsAdapter;
import com.bbxiaoqu.adapter.ReportsAdapter;
import com.bbxiaoqu.api.ApiAsyncTask.ApiRequestListener;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.client.baidu.Utils;
import com.bbxiaoqu.comm.service.db.DatabaseHelper;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.SearchActivity;
import com.bbxiaoqu.ui.sub.ChattingActivity;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DayActivity extends Activity {
	private DemoApplication myapplication;
	private ReportsAdapter adapter;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	ListView lstv;
	private TextView title;
	private TextView txt_order;
	private TextView txt_order_desc;
	private TextView right_text;
	public ImageView top_more;
	private DatabaseHelper dbHelper;
	
	
	private static final int DIALOG_PROGRESS = 0;
	// 用户不存在（用户名错误）
	private static final int ERROR_CODE_USERNAME_NOT_EXIST = 211;
	// 用户密码错误
	private static final int ERROR_CODE_PASSWORD_INVALID = 212;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_report_day);

		dbHelper = new DatabaseHelper(DayActivity.this);
		myapplication = (DemoApplication) this.getApplication();
		myapplication.getInstance().addActivity(this);
		initView();
		initData();
		LoadData();
		LoadRate();

		adapter= new ReportsAdapter(this, dataList);
		lstv.setAdapter(adapter);
	}

	private void initView() {
		title = (TextView) findViewById(R.id.title);
		txt_order = (TextView) findViewById(R.id.txt_order);
		txt_order_desc = (TextView) findViewById(R.id.txt_order_desc);
		right_text = (TextView) findViewById(R.id.right_text);
		right_text.setVisibility(View.VISIBLE);
		right_text.setClickable(true);
		top_more = (ImageView) findViewById(R.id.top_more);
		top_more.setVisibility(View.GONE);
		lstv = (ListView) findViewById(R.id.orderlv);
	}

	private void initData() {
		title.setText("日排名");
		right_text.setText("");
	}

	/*TextView order;
	TextView username;
	TextView nums;
	RatingBar ratingbar;*/


	private void LoadRate() {
		if (!NetworkUtils.isNetConnected(myapplication)) {
			T.showShort(myapplication, "当前无网络连接！");
			return;
		}
		String target=myapplication.getlocalhost()+"myrank.php?userid="+myapplication.getUserId();
		try {
			HttpGet httprequest = new HttpGet(target);
			HttpClient HttpClient1 = new DefaultHttpClient();
			HttpResponse httpResponse = null;
			try {
				httpResponse = HttpClient1.execute(httprequest);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				//InputStream json = null;
				try {

					//json = httpResponse.getEntity().getContent();

					InputStream instream = httpResponse.getEntity().getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(instream,"UTF-8"));
					String json=reader.readLine();

					Message message = new Message();
					message.what = 1;
					message.obj=json;
					handler.sendMessage(message);

				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					txt_order.setText(String.valueOf(msg.obj));
					txt_order_desc.setText("你排名第"+msg.obj+"位");
					break;
			}
			super.handleMessage(msg);
		}

	};


	private void LoadData() {
		if (!NetworkUtils.isNetConnected(myapplication)) {
			T.showShort(myapplication, "当前无网络连接！");
			return;
		}
		String target=myapplication.getlocalhost()+"rank.php";
		dataList = new ArrayList<Map<String, Object>>();
		try {
			List<Map<String, Object>> bfjllist=null;
			HttpGet httprequest = new HttpGet(target);
			HttpClient HttpClient1 = new DefaultHttpClient();
			HttpResponse httpResponse = null;
			try {
				httpResponse = HttpClient1.execute(httprequest);
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream json = null;
				try {
					json = httpResponse.getEntity().getContent();
					bfjllist= parsejson(json);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			for (Map map: bfjllist) {
				dataList.add(map);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private List<Map<String, Object>> parsejson(InputStream jsonStream)
			throws Exception {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		byte[] data = StreamTool.read(jsonStream);
		String json = new String(data);
		JSONArray jsonarray = new JSONArray(json);
		for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject jsonobject = jsonarray.getJSONObject(i);
			int order = jsonobject.getInt("order");
			String username = jsonobject.getString("username");
			String score = jsonobject.getString("score");
			String nums = jsonobject.getString("nums");
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("order", order);
			item.put("username",username);
			item.put("score", score);
			item.put("nums", nums);
			list.add(item);
		}
		return list;
	}



	
	public void doBack(View view) {
		onBackPressed();
	}
	

}