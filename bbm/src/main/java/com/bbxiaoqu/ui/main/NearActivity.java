package com.bbxiaoqu.ui.main;

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
import com.bbxiaoqu.adapter.FwListViewAdapter;
import com.bbxiaoqu.adapter.ListViewAdapter;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.api.ApiAsyncTask.ApiRequestListener;
import com.bbxiaoqu.bean.BbMessage;
import com.bbxiaoqu.bean.InfoBase;
import com.bbxiaoqu.client.baidu.Utils;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver.onMessageReadListener;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver.onNewMessageListener;
import com.bbxiaoqu.comm.service.db.DatabaseHelper;
import com.bbxiaoqu.comm.service.db.MessageDB;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.L;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.PublishActivity;
import com.bbxiaoqu.ui.PublishFwActivity;
import com.bbxiaoqu.ui.SearchActivity;
import com.bbxiaoqu.view.BaseActivity;
import com.bbxiaoqu.widget.AutoListView;
import com.bbxiaoqu.widget.AutoListView.OnLoadListener;
import com.bbxiaoqu.widget.AutoListView.OnRefreshListener;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class NearActivity extends BaseActivity  implements OnRefreshListener,OnLoadListener,OnClickListener,ApiRequestListener {
	TextView title;
	TextView right_text;
	public ImageView top_more;
	public ImageView top_add;
	private AutoListView lstv;
	private BaseAdapter adapter;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	private DatabaseHelper dbHelper;
	private View allLayout;
	private View mySosLayout;
	private ImageView allImage;
	private ImageView mySosImage;
	private TextView allText;
	private TextView mySosText;
	private View sosLayout;
	private ImageView sosImage;
	private TextView sosText;
	private View serviceLayout;
	private ImageView serviceImage;
	private TextView serviceText;
	private int current_sel=0;
	private DemoApplication myapplication;
	private static final int DIALOG_PROGRESS = 0;
	//用户不存在（用户名错误）
	private static final int ERROR_CODE_USERNAME_NOT_EXIST = 211;
	//用户密码错误
	private static final int ERROR_CODE_PASSWORD_INVALID = 212;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_near);	
		myapplication = (DemoApplication) this.getApplication();
		initView();
		initData();
	}

	int action=0;
	private void loadData(final int what) {
		if (!isFinishing()) {
            showDialog(DIALOG_PROGRESS);
	    } else {
	            // 如果当前页面已经关闭，不进行登录操作
	       return;
	    }
		if(what==AutoListView.REFRESH)
		{
			action=AutoListView.REFRESH;
			int start=0;
			int limit=10;
			if(current_sel==0)
			{
				MarketAPI.getINfos(getApplicationContext(), this, myapplication.getUserId(), "xiaoqu", "0", start, limit);
			}else if(current_sel==1)
			{
				MarketAPI.getINfos(getApplicationContext(), this, myapplication.getUserId(), "xiaoqu", "1", start, limit);				
			}else if(current_sel==2)
			{
				MarketAPI.getINfos(getApplicationContext(), this, myapplication.getUserId(), "self", "1", start, limit);
			}else if(current_sel==3)
			{
				MarketAPI.getFwINfos(getApplicationContext(), this, myapplication.getUserId(), "xiaoqufw", "1", start, limit);
			}
		}else
		{
			action=AutoListView.LOAD;
			int start=dataList.size() ;
			int limit=10;
			if(current_sel==0)
			{
				MarketAPI.getINfos(getApplicationContext(), this, myapplication.getUserId(), "xiaoqu", "0", start, limit);
			}else if(current_sel==1)
			{
				MarketAPI.getINfos(getApplicationContext(), this, myapplication.getUserId(), "xiaoqu", "1", start, limit);				
			}else if(current_sel==2)
			{
				MarketAPI.getINfos(getApplicationContext(), this, myapplication.getUserId(), "self", "1", start, limit);
			}else if(current_sel==3)
			{
				MarketAPI.getFwINfos(getApplicationContext(), this, myapplication.getUserId(), "xiaoqufw", "1", start, limit);
			}
		}
	}
	
	private void clearSelection() {
		allImage.setImageResource(R.mipmap.t12);
		allText.setTextColor(Color.GRAY);
		mySosImage.setImageResource(R.mipmap.t22);
		mySosText.setTextColor(Color.GRAY);
		sosImage.setImageResource(R.mipmap.t32);
		sosText.setTextColor(Color.GRAY);
		serviceImage.setImageResource(R.mipmap.t42);
		serviceText.setTextColor(Color.GRAY);
	}
	
	private void setTabSelection(int index) {
		clearSelection();
		dataList.clear();
		switch (index) {
		case 0:
			// 当点击了消息tab时，改变控件的图片和文字颜色
			adapter = new ListViewAdapter(NearActivity.this, dataList);
			lstv.setAdapter(adapter);
			lstv.setOnRefreshListener(this);
			lstv.setOnLoadListener(this);
			allImage.setImageResource(R.mipmap.t1);
			allText.setTextColor(Color.GRAY);
			loadData(AutoListView.REFRESH);
			break;
		case 1:
			// 当点击了联系人tab时，改变控件的图片和文字颜色
			adapter = new ListViewAdapter(NearActivity.this, dataList);
			lstv.setAdapter(adapter);
			lstv.setOnRefreshListener(this);
			lstv.setOnLoadListener(this);
			mySosImage.setImageResource(R.mipmap.t2);
			mySosText.setTextColor(Color.GRAY);
			loadData(AutoListView.REFRESH);
			break;
		case 2:
			// 当点击了动态tab时，改变控件的图片和文字颜色
			adapter = new ListViewAdapter(NearActivity.this, dataList);
			lstv.setAdapter(adapter);
			lstv.setOnRefreshListener(this);
			lstv.setOnLoadListener(this);
			sosImage.setImageResource(R.mipmap.t3);
			sosText.setTextColor(Color.GRAY);
			loadData(AutoListView.REFRESH);
			break;
		case 3:
			// 当点击了动态tab时，改变控件的图片和文字颜色
			adapter = new FwListViewAdapter(NearActivity.this, dataList);
			lstv.setAdapter(adapter);
			lstv.setOnRefreshListener(this);
			lstv.setOnLoadListener(this);
			serviceImage.setImageResource(R.mipmap.t4);
			serviceText.setTextColor(Color.GRAY);
			loadData(AutoListView.REFRESH);
			break;
		}
	}
	
	private void initView() {
		title = (TextView)findViewById(R.id.title);
		right_text = (TextView)findViewById(R.id.right_text);
		right_text.setVisibility(View.VISIBLE);
		right_text.setClickable(true);		
		top_more = (ImageView) findViewById(R.id.top_more);	
		top_more.setVisibility(View.VISIBLE);
		top_add = (ImageView) findViewById(R.id.top_add);
		top_add.setVisibility(View.VISIBLE);
		allLayout = findViewById(R.id.all_layout);
		mySosLayout = findViewById(R.id.mysos_layout);
		sosLayout = findViewById(R.id.sos_layout);
		serviceLayout = findViewById(R.id.service_layout);
		allImage = (ImageView) findViewById(R.id.all_image);
		mySosImage = (ImageView) findViewById(R.id.mysos_image);
		sosImage = (ImageView) findViewById(R.id.sos_image);
		serviceImage = (ImageView) findViewById(R.id.service_image);
		allText = (TextView) findViewById(R.id.all_text);
		mySosText = (TextView) findViewById(R.id.mysos_text);
		sosText = (TextView) findViewById(R.id.sos_text);
		serviceText = (TextView) findViewById(R.id.service_text);
		allLayout.setOnClickListener(this);
		mySosLayout.setOnClickListener(this);
		sosLayout.setOnClickListener(this);
		serviceLayout.setOnClickListener(this);
		lstv = (AutoListView) findViewById(R.id.lstv);
		lstv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int location, long arg3) 
			{
				if(dataList.get(location - 1).get("infocatagroy").toString().equals("3"))
				{
					Intent Intent1 = new Intent();
					Intent1.setClass(NearActivity.this, ViewFwActivity.class);
					Bundle arguments = new Bundle();
					arguments.putString("put", "false");
					arguments.putString("guid", dataList.get(location - 1).get("guid").toString());
					Intent1.putExtras(arguments);
					startActivity(Intent1);
				}else {
					Intent Intent1 = new Intent();
					Intent1.setClass(NearActivity.this, ViewActivity.class);
					Bundle arguments = new Bundle();
					arguments.putString("put", "false");
					arguments.putString("guid", dataList.get(location - 1).get("guid").toString());
					Intent1.putExtras(arguments);
					startActivity(Intent1);
				}
			}
		});
		top_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(NearActivity.this,SearchActivity.class);									
				startActivity(intent);
			}
		});
		top_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(NearActivity.this, PublishFwActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("infocatagroy", 3);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void initData() {
		title.setText("我能帮");
		right_text.setText("");
		current_sel=0;
		setTabSelection(current_sel);	
		dbHelper = new DatabaseHelper(NearActivity.this);
		myapplication = (DemoApplication) this.getApplication();
	}
	

	public void doBack(View view) {
		onBackPressed();
	}

	@Override
	public void onRefresh() {
		loadData(AutoListView.REFRESH);
	}

	@Override
	public void onLoad() {
		loadData(AutoListView.LOAD);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.all_layout:
			current_sel=0;
			setTabSelection(current_sel);			
			break;
		case R.id.mysos_layout:
			current_sel=1;
			setTabSelection(current_sel);			
			break;	
		case R.id.sos_layout:
			current_sel=2;
			setTabSelection(current_sel);			
			break;
		case R.id.service_layout:
			current_sel=3;
			setTabSelection(current_sel);			
			break;
		default:
			break;
		}
	}

	@Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

	@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_PROGRESS:
            ProgressDialog mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage(getString(R.string.init_data));
            return mProgressDialog;
        default:
            return super.onCreateDialog(id);
        }
    }

	@Override
	public void onSuccess(int method, Object obj) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> smalldataList = new ArrayList<Map<String, Object>>();
		HashMap<String, String> result = (HashMap<String, String>) obj;
		String json=result.get("infos");
		 switch (method) {
	        case MarketAPI.ACTION_GETINFOS:
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
							item.put("content", String.valueOf(customJson.getString("content").toString()));
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
				if(action==AutoListView.REFRESH)
				{
					lstv.onRefreshComplete();
					dataList.clear();
					dataList.addAll(smalldataList);		
				}else if(action==AutoListView.LOAD)
				{
					lstv.onLoadComplete();
					dataList.addAll(smalldataList);
				}
				lstv.setResultSize(dataList.size());
				adapter.notifyDataSetChanged();
	            break;
			 case MarketAPI.ACTION_GETFWINFOS:
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
							 item.put("title", String.valueOf(customJson.getString("title").toString()));
							 item.put("content", String.valueOf(customJson.getString("content").toString()));
							 item.put("icon", String.valueOf(customJson.getString("photo").toString()));
							 item.put("date", String.valueOf(customJson.getString("sendtime").toString()));
							 item.put("status", String.valueOf(customJson.getString("status").toString()));
							 item.put("visit", String.valueOf(customJson.getString("visit").toString()));
							 item.put("tag1", String.valueOf(customJson.getString("visit").toString()));
							 item.put("tag2", "评论数:"+String.valueOf(customJson.getString("plnum").toString()));
							 item.put("headface", String.valueOf(customJson.getString("headface").toString()));
							 item.put("telphone", String.valueOf(customJson.getString("telphone").toString()));
							 item.put("zannum", String.valueOf(customJson.getString("zannum").toString()));
							 item.put("tags", String.valueOf(customJson.getString("tags").toString()));
							 smalldataList.add(item);
						 }
					 } catch (JSONException e1) {
						 // TODO Auto-generated catch block
						 e1.printStackTrace();
					 }
				 }
				 if(action==AutoListView.REFRESH)
				 {
					 lstv.onRefreshComplete();
					 dataList.clear();
					 dataList.addAll(smalldataList);
				 }else if(action==AutoListView.LOAD)
				 {
					 lstv.onLoadComplete();
					 dataList.addAll(smalldataList);
				 }
				 lstv.setResultSize(dataList.size());
				 adapter.notifyDataSetChanged();
				 break;

			 default:
	            break;
	        }
		try{
			dismissDialog(DIALOG_PROGRESS);
		}catch (IllegalArgumentException e) {
		}
	}

	@Override
	public void onError(int method, int statusCode) {
		// TODO Auto-generated method stub
		switch (method) {
        case MarketAPI.ACTION_GETINFOS:            
            // 隐藏登录框
            try{
                dismissDialog(DIALOG_PROGRESS);
            }catch (IllegalArgumentException e) {
            }            		            
            break;
         default:
            break;
        }
	}
}
