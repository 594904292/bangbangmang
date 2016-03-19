package com.bbxiaoqu.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.adapter.ListViewAdapter;
import com.bbxiaoqu.api.util.Utils;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.ScreenUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.fragment.publish.BitmapUtils;
import com.bbxiaoqu.ui.fragment.publish.StringUtils;
import com.bbxiaoqu.ui.main.NearActivity;
import com.bbxiaoqu.ui.main.ViewActivity;
import com.bbxiaoqu.ui.main.ViewFwActivity;
import com.bbxiaoqu.ui.sub.SelectPhotoActivity;
import com.bbxiaoqu.view.DrawerView;
import com.bbxiaoqu.widget.AutoListView;
import com.bbxiaoqu.widget.AutoListView.OnLoadListener;
import com.bbxiaoqu.widget.AutoListView.OnRefreshListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.ToggleButton;
import android.view.ViewGroup.MarginLayoutParams;  

public class SearchActivity extends Activity{
	//TextView title;
	/*TextView right_text;*/
	private Drawable mIconSearchDefault; // 搜索文本框默认图标
    private Drawable mIconSearchClear; // 搜索文本框清除文本内容图标
    private EditText etSearch ;
    ImageView  ivDeleteText;
    Button btnSearch;
    private String mNames[] = {  
            "美食","天气","风景",  
            "浪漫","风光","租房","培训"  
    };     
    private XCFlowLayout mFlowLayout;  
    String keyword="";
	private ListView lstv;
	private ListViewAdapter adapter;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	private DemoApplication myapplication;
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		Resources resource = this.getResources();
		String pkgName = this.getPackageName();
		myapplication = (DemoApplication) this.getApplication();
		 initChildViews();
		initView();
		initData();
		
		lstv = (ListView) findViewById(R.id.search_lstv);
		lstv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int location, long arg3) 
			{
				Intent Intent1 = new Intent();
				if(dataList.get(location).get("infocatagroy").toString().equals("3")) {
					Intent1.setClass(SearchActivity.this, ViewFwActivity.class);
				}else
				{
					Intent1.setClass(SearchActivity.this, ViewActivity.class);
				}
				Bundle arguments = new Bundle();
				arguments.putString("put", "false");
				arguments.putString("guid",dataList.get(location).get("guid").toString());
				Intent1.putExtras(arguments);
				startActivity(Intent1);
			}
		});
		adapter = new ListViewAdapter(SearchActivity.this, dataList);
		lstv.setAdapter(adapter);	
	}
    
    private void initChildViews() {
        // TODO Auto-generated method stub
        mFlowLayout = (XCFlowLayout) findViewById(R.id.flowlayout);
        MarginLayoutParams lp = new MarginLayoutParams(
                LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 5;
        lp.rightMargin = 5;
        lp.topMargin = 5;
        lp.bottomMargin = 5;
        for(int i = 0; i < mNames.length; i ++){
            TextView view = new TextView(this);
            view.setText(mNames[i]);
            view.setTag(mNames[i]);
            view.setTextColor(Color.WHITE);
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.textview_bg));
            view.setOnClickListener(new OnClickListener() {
            	public void onClick(View v){
            		keyword=v.getTag().toString();
    				new Thread(new Runnable() {
    					@Override
    					public void run(){
    						Message msg = handler.obtainMessage();								
    						msg.obj = getData();
    						handler.sendMessage(msg);
    					}
    				}).start();
            	}
            });		                    
            mFlowLayout.addView(view,lp);
        }
    }
	
	private void initView() {
		final Resources res = getResources();
        mIconSearchDefault = res.getDrawable(R.mipmap.txt_search_default);
        mIconSearchClear = res.getDrawable(R.mipmap.txt_search_clear);
        ivDeleteText = (ImageView) findViewById(R.id.ivDeleteText);  
        etSearch  = (EditText) findViewById(R.id.etSearch);
        ivDeleteText.setOnClickListener(new OnClickListener() {  
            
            public void onClick(View v) {  
                etSearch.setText("");  
            }  
        });  
          
        etSearch.addTextChangedListener(new TextWatcher() {  
              
            public void onTextChanged(CharSequence s, int start, int before, int count) {  
                // TODO Auto-generated method stub  
                  System.out.println(s);
            }  
              
            public void beforeTextChanged(CharSequence s, int start, int count,  
                    int after) {  
                // TODO Auto-generated method stub  
            	 System.out.println(s);
            }  
              
            public void afterTextChanged(Editable s) {  
                if (s.length() == 0) {  
                    ivDeleteText.setVisibility(View.GONE);  
                } else {  
                    ivDeleteText.setVisibility(View.VISIBLE);  
                }  
            }  
        });  
        
        btnSearch  = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				keyword=etSearch.getText().toString();
				if(keyword.length()==0)
				{
					 Utils.makeEventToast(SearchActivity.this, "请输入关键词",false);
					 return;
				}
					new Thread(new Runnable() {
					@Override
					public void run(){
						Message msg = handler.obtainMessage();								
						msg.obj = getData();
						handler.sendMessage(msg);
					}
				}).start();
			}
		});
	}

	private void initData() {
		//title.setText("我能帮");
		//right_text.setText("");
	}
	
	
	
	public void doBack(View view) {
		onBackPressed();
	}


	
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			List<Map<String, Object>> partresult = (List<Map<String, Object>>) msg.obj;//不放在这里获取是防界面僵					
			if(partresult!=null&&partresult.size()>0)
			{
				mFlowLayout.setVisibility(View.GONE);
			}else
			{
				mFlowLayout.setVisibility(View.VISIBLE);
			}
			dataList.clear();
			dataList.addAll(partresult);				
			adapter.notifyDataSetChanged();
		};
	};
	
	

	
	
	public ArrayList<Map<String, Object>> getData() {
		List<Map<String, Object>> smalldataList = new ArrayList<Map<String, Object>>();
		String url="";
		
		dataList.clear();
		int start=0;
		int limit=10;
		//String keyword=etSearch.getText().toString();
		//myapplication.getLat(),myapplication.getLng()
				//+"&latitude="+latitude+"&longitude="+longitude+"
		url="http://api.bbxiaoqu.com/getinfos.php?userid="+myapplication.getUserId()+"&latitude="+myapplication.getLat()+"&longitude="+myapplication.getLng()+"&rang=xiaoqu&keyword="+keyword+"&start="+start+"&limit="+limit;
					
		HttpGet httprequest = new HttpGet(url);
		HttpClient HttpClient1 = new DefaultHttpClient();
		// 请求超时
		HttpClient1.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
		// 读取超时
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
	
	
}