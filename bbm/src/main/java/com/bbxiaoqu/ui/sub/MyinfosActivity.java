package com.bbxiaoqu.ui.sub;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.adapter.ListViewAdapter;
import com.bbxiaoqu.bean.BbMessage;
import com.bbxiaoqu.bean.InfoBase;
import com.bbxiaoqu.comm.service.db.DatabaseHelper;
import com.bbxiaoqu.comm.service.db.MessageDB;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.L;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.SearchActivity;
import com.bbxiaoqu.ui.main.ViewActivity;
import com.bbxiaoqu.ui.main.ViewFwActivity;
import com.bbxiaoqu.widget.AutoListView;
import com.bbxiaoqu.widget.AutoListView.OnLoadListener;
import com.bbxiaoqu.widget.AutoListView.OnRefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MyinfosActivity extends Activity  implements OnRefreshListener,OnLoadListener {
	
	private TextView title;
	private TextView right_text;	
	public ImageView top_more;
	private AutoListView lstv;
	private ListViewAdapter adapter;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	private DatabaseHelper dbHelper;
	private DemoApplication myapplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myinfos);	
		initView();
		initData();
		
		dbHelper = new DatabaseHelper(MyinfosActivity.this);
		myapplication = (DemoApplication) this.getApplication();
		lstv = (AutoListView) findViewById(R.id.lstv);
		lstv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int location, long arg3) 
			{
				Intent Intent1 = new Intent();
				if(dataList.get(location - 1).get("infocatagroy").toString().equals("3")) {
					Intent1.setClass(MyinfosActivity.this, ViewFwActivity.class);
				}else
				{
					Intent1.setClass(MyinfosActivity.this, ViewActivity.class);
				}
				Bundle arguments = new Bundle();
				arguments.putString("put", "false");
				arguments.putString("guid",dataList.get(location - 1).get("guid").toString());
				Intent1.putExtras(arguments);
				startActivity(Intent1);
			}
		});
		adapter = new ListViewAdapter(MyinfosActivity.this, dataList);
		lstv.setAdapter(adapter);
		lstv.setOnRefreshListener(this);
		lstv.setOnLoadListener(this);	
		loadData(AutoListView.REFRESH);
	}
	
	
	private void initView() {
		title = (TextView)findViewById(R.id.title);
		right_text = (TextView)findViewById(R.id.right_text);
		right_text.setVisibility(View.VISIBLE);
		right_text.setClickable(true);		
		top_more = (ImageView) findViewById(R.id.top_more);	
		top_more.setVisibility(View.VISIBLE);
		top_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MyinfosActivity.this,SearchActivity.class);									
				startActivity(intent);
				
				
			}
		});
	}

	private void initData() {
		title.setText("我的求助");
		right_text.setText("");
	}	

	public void doBack(View view) {
		onBackPressed();
	}

	private String getVersionName(){
		PackageManager packageManager = getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return packInfo.versionName; 
	}

	@Override
	public void onRefresh() {
		loadData(AutoListView.REFRESH);
	}

	@Override
	public void onLoad() {
		loadData(AutoListView.LOAD);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			List<Map<String, Object>> partresult = (List<Map<String, Object>>) msg.obj;//不放在这里获取是防界面僵
			switch (msg.what) {
			case AutoListView.REFRESH:
				lstv.onRefreshComplete();
				dataList.clear();
				dataList.addAll(partresult);				
				break;
			case AutoListView.LOAD:
				lstv.onLoadComplete();
				dataList.addAll(partresult);
				break;
			}
			lstv.setResultSize(partresult.size());
			adapter.notifyDataSetChanged();
		};
	};
	
	private void loadData(final int what) {
		if (!NetworkUtils.isNetConnected(MyinfosActivity.this))
		{
			T.showShort(MyinfosActivity.this, "当前无网络连接！");
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run(){
				Message msg = handler.obtainMessage();			
				msg.what = what;
				msg.obj = getData(what);
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	public ArrayList<Map<String, Object>> getData(int what) {
		List<Map<String, Object>> smalldataList = new ArrayList<Map<String, Object>>();
		String url="";
		if(what==AutoListView.REFRESH)
		{
			dataList.clear();
			int start=0;
			int limit=10;						
			url="http://www.bbxiaoqu.com/getinfos.php?userid="+myapplication.getUserId()+"&rang=self&start="+start+"&limit="+limit;						
		}else
		{
			int start=dataList.size() ;
			int limit=10;						
			url="http://www.bbxiaoqu.com/getinfos.php?userid="+myapplication.getUserId()+"&rang=self&start="+start+"&limit="+limit;						
		}
		HttpGet httprequest = new HttpGet(url);
		HttpClient HttpClient1 = new DefaultHttpClient();
		//请求超时
		HttpClient1.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
		//读取超时
		HttpClient1.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
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
		if (httpResponse!=null&&httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			InputStream jsonStream = null;
			try {
				jsonStream = httpResponse.getEntity().getContent();
				byte[] data = StreamTool.read(jsonStream);
				String json = new String(data);
				if(json.length()>0)
				{
					JSONArray jsonarray = null;
					try {
						jsonarray = new JSONArray(json);
						for (int i = 0; i < jsonarray.length(); i++) {
							JSONObject customJson = jsonarray.getJSONObject(i);
							HashMap<String, Object> item = new HashMap<String, Object>();
							item.put("senduserid", String.valueOf(customJson.getString("senduser").toString()));
							item.put("sendnickname", String.valueOf(customJson.getString("username").toString()));
							item.put("community", String.valueOf(customJson.getString("community").toString()));
							item.put("address", String.valueOf(customJson.getString("address").toString()));
							item.put("lng", String.valueOf(customJson.getString("lng").toString()));
							item.put("lat", String.valueOf(customJson.getString("lat").toString()));
							item.put("guid", String.valueOf(customJson.getString("guid").toString()));
							item.put("infocatagroy", String.valueOf(customJson.getString("infocatagroy").toString()));
							item.put("message", String.valueOf(customJson.getString("content").toString()));
							item.put("icon", String.valueOf(customJson.getString("photo").toString()));
							item.put("date", String.valueOf(customJson.getString("sendtime").toString()));
							item.put("status", String.valueOf(customJson.getString("status").toString()));
							item.put("visit", String.valueOf(customJson.getString("visit").toString()));
							item.put("tag1", "访客数:"+String.valueOf(customJson.getString("visit").toString()));
							item.put("tag2", "评论数:"+String.valueOf(customJson.getString("plnum").toString()));
							smalldataList.add(item);
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}		
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return (ArrayList<Map<String, Object>>) smalldataList;
	}
	
	
	private String[] getitemnum(String guid) {
		String target = DemoApplication.getInstance().getlocalhost()+"getitemnum.php";
		HttpPost httprequest = new HttpPost(target);
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();		
		paramsList.add(new BasicNameValuePair("_guid", guid));	
		String[] nums=new String[3];
		try {
			httprequest.setEntity(new UrlEncodedFormEntity(paramsList, "UTF-8"));
			HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
			HttpClient1.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,3000);
			HttpClient1.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);
			HttpResponse httpResponse = HttpClient1.execute(httprequest);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) 
			{
				InputStream  jsonStream = httpResponse.getEntity().getContent();
				byte[] data = null;
				try {
					data = StreamTool.read(jsonStream);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String JsonContext = new String(data);
				if(JsonContext.length()>0)
				{
					JSONObject jsonobj = new JSONObject(JsonContext);//转换为JSONObject  
					nums[0]=jsonobj.getString("bmnum");
					nums[1]=jsonobj.getString("dicussnum");
					nums[2]=jsonobj.getString("gznum");
				}
			}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return nums;
	}  
 	
}
