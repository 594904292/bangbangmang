package com.bbxiaoqu.ui.sub;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.adapter.DynamicListViewAdapter;
import com.bbxiaoqu.adapter.FriendsAdapter;
import com.bbxiaoqu.adapter.RecentAdapter;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.api.ApiAsyncTask.ApiRequestListener;
import com.bbxiaoqu.bean.BbMessage;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver;
import com.bbxiaoqu.client.baidu.Utils;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver.onMessageReadListener;
import com.bbxiaoqu.comm.service.db.DatabaseHelper;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.tool.L;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.ui.LoginActivity;
import com.bbxiaoqu.ui.SearchActivity;
import com.bbxiaoqu.ui.main.MainActivity;
import com.bbxiaoqu.ui.main.ViewActivity;
import com.bbxiaoqu.widget.AutoListView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FriendsActivity extends Activity implements ApiRequestListener {
	private DemoApplication myapplication;
	private FriendsAdapter adapter;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	ListView lstv;
	private TextView title;
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
		setContentView(R.layout.activity_friends);

		dbHelper = new DatabaseHelper(FriendsActivity.this);
		myapplication = (DemoApplication) this.getApplication();
		myapplication.getInstance().addActivity(this);
		initView();
		initData();
		LoadData();
	}

	private void initView() {
		title = (TextView) findViewById(R.id.title);
		right_text = (TextView) findViewById(R.id.right_text);
		right_text.setVisibility(View.VISIBLE);
		right_text.setClickable(true);
		top_more = (ImageView) findViewById(R.id.top_more);	
		top_more.setVisibility(View.VISIBLE);
		top_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(FriendsActivity.this,SearchActivity.class);									
				startActivity(intent);
				
				
			}
		});
		lstv = (ListView) findViewById(R.id.friendslv);
		lstv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int location, long arg3) {								
					
					//提取用户id
					/*Intent intent = new Intent(FriendsActivity.this,ChattingActivity.class);
					Bundle arguments = new Bundle();
					arguments.putString("to", dataList.get(location).get("userid").toString());
					arguments.putString("my",myapplication.getUserId());
					intent.putExtras(arguments);					
					startActivity(intent);*/

				Intent Intent1 = new Intent();
				Intent1.setClass(FriendsActivity.this, ViewUserInfoActivity.class);
				Bundle arguments = new Bundle();
				arguments.putString("userid",dataList.get(location).get("userid").toString());
				Intent1.putExtras(arguments);
				startActivity(Intent1);
					
			}
		}); 	
	}

	private void initData() {
		title.setText("好友列表");
		right_text.setText("");
	}

	private void LoadData() {
		if (!isFinishing()) {
	            showDialog(DIALOG_PROGRESS);
	    } else {
	            // 如果当前页面已经关闭，不进行登录操作
	       return;
	    }
		
		MarketAPI.getFriends(getApplicationContext(), this,myapplication.getUserId());
	}
	
	public void doBack(View view) {
		onBackPressed();
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {			
			case 1:
				adapter = new FriendsAdapter(FriendsActivity.this, dataList);
			    lstv.setAdapter(adapter);
			default:
				break;
			}			
		};
	};

	@Override
	public void onSuccess(int method, Object obj) {
		// TODO Auto-generated method stub
		  switch (method) {
	        case MarketAPI.ACTION_GETFRIENDS:
	        	 try{
	                 dismissDialog(DIALOG_PROGRESS);
	             }catch (IllegalArgumentException e) {
	             }
	            HashMap<String, String> result = (HashMap<String, String>) obj;
	            String JsonContext=result.get("friends");           
	            if(JsonContext.length()>0)
				{
					JSONArray jsonarray = null;
					try {
						dataList.clear();
						jsonarray = new JSONArray(JsonContext);
						for (int i = 0; i < jsonarray.length(); i++) {
							JSONObject customJson = jsonarray.getJSONObject(i);
							HashMap<String, Object> item = new HashMap<String, Object>();
							item.put("id", String.valueOf(customJson.getString("id").toString()));
							item.put("userid", String.valueOf(customJson.getString("userid").toString()));
							item.put("username", String.valueOf(customJson.getString("username").toString()));
							item.put("headface", String.valueOf(customJson.getString("headface").toString()));
							dataList.add(item);
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						Utils.makeEventToast(FriendsActivity.this, "friends xml解释错误",false);
						e1.printStackTrace();
					}
					Message msg = handler.obtainMessage();			
					msg.what = 1;			
					handler.sendMessage(msg);
				}
	            break; 	       
	        default:
	            break;
	        }
	}

	@Override
	public void onError(int method, int statusCode) {
		// TODO Auto-generated method stub
		switch (method) {
		case MarketAPI.ACTION_GETFRIENDS:
			// 隐藏登录框
			try {
				dismissDialog(DIALOG_PROGRESS);
			} catch (IllegalArgumentException e) {
			}			
			break;
		default:
			break;
		}
	}
}