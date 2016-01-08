package com.bbxiaoqu.ui.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver;
import com.bbxiaoqu.comm.service.db.DatabaseHelper;
import com.bbxiaoqu.comm.service.db.MessageDB;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.L;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.LoginActivity;
import com.bbxiaoqu.ui.main.ViewActivity;
import com.bbxiaoqu.widget.AutoListView;
import com.bbxiaoqu.widget.AutoListView.OnLoadListener;
import com.bbxiaoqu.widget.AutoListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("NewApi")
public class MessageFragment extends Fragment implements OnRefreshListener,OnLoadListener,OnClickListener {

	private AutoListView lstv;
	private ListViewAdapter adapter;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	private DatabaseHelper dbHelper;

	private View allLayout;
	private View mySosLayout;
	private View sosLayout;
	private View marketLayout;
	private ImageView allImage;
	private ImageView mySosImage;
	private ImageView sosImage;
	private ImageView marketImage;
	private TextView allText;
	private TextView mySosText;
	private TextView sosText;
	private TextView marketText;
	private int current_sel=0;
	private DemoApplication myapplication;
	
	
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);  
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		System.out.println("ExampleFragment--onCreateView");
		//BbPushMessageReceiver.msgListeners.add(this);
		View view = inflater.inflate(R.layout.message_layout, container, false);
		dbHelper = new DatabaseHelper(this.getActivity());
		myapplication = (DemoApplication) this.getActivity().getApplication();
		
		allLayout = view.findViewById(R.id.all_layout);
		mySosLayout = view.findViewById(R.id.mysos_layout);
		sosLayout = view.findViewById(R.id.sos_layout);
		marketLayout = view.findViewById(R.id.market_layout);
		allImage = (ImageView) view.findViewById(R.id.all_image);
		mySosImage = (ImageView) view.findViewById(R.id.mysos_image);
		sosImage = (ImageView) view.findViewById(R.id.sos_image);
		marketImage = (ImageView) view.findViewById(R.id.market_image);
		allText = (TextView) view.findViewById(R.id.all_text);
		mySosText = (TextView) view.findViewById(R.id.mysos_text);
		sosText = (TextView) view.findViewById(R.id.sos_text);
		marketText = (TextView) view.findViewById(R.id.market_text);
		allLayout.setOnClickListener(this);
		mySosLayout.setOnClickListener(this);
		sosLayout.setOnClickListener(this);
		marketLayout.setOnClickListener(this);
		
		lstv = (AutoListView) view.findViewById(R.id.lstv);
		lstv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int location, long arg3) {
				System.out.println("触发了第" + location + "行");

				Intent Intent1 = new Intent();
				Intent1.setClass(getActivity(), ViewActivity.class);
				Bundle arguments = new Bundle();
				arguments.putString("put", "false");
				arguments.putString("guid",
						dataList.get(location - 1).get("guid").toString());
				Intent1.putExtras(arguments);
				startActivity(Intent1);
			}

		});
		adapter = new ListViewAdapter(this.getActivity(), dataList);
		lstv.setAdapter(adapter);
		lstv.setOnRefreshListener(this);
		lstv.setOnLoadListener(this);
		current_sel=0;
		setTabSelection(current_sel);			
		return view;
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

	@Override
	public void onDestroy() {
		//BbPushMessageReceiver.msgListeners.remove(this);
		super.onDestroy();
	}

	

	private void loadData(final int what) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = handler.obtainMessage();			
				msg.what = what;
				msg.obj = getData(what);
				handler.sendMessage(msg);
			}
		}).start();
	}

	public ArrayList<Map<String, Object>> getData(int what) {
		List<Map<String, Object>> smalldataList = new ArrayList<Map<String, Object>>();
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		String sql="";
		if(what==AutoListView.REFRESH)
		{
			if(current_sel==0)
			{//全部				
				sql = "select * from  [message] order by date  desc limit 0,10";
			}else if(current_sel==1)
			{//求帮				
				sql = "select * from  [message] where infocatagroy=0 and senduserid='"+myapplication.getUserId()+"'  order by date  desc limit 0,10";
			}else if(current_sel==2)
			{//我能				
				sql = "select * from  [message] where infocatagroy=0 and senduserid!='"+myapplication.getUserId()+"' order by date  desc limit 0,10";
			}else if(current_sel==3)
			{//跳蚤				
				sql = "select * from  [message] where infocatagroy in(1,2) order by date  desc limit 0,10";
			}
			L.i("query",sql);
			Cursor c = sdb.rawQuery(sql, null);
			while (c.moveToNext()) {
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("senduserid", String.valueOf(c.getString(1)));
				item.put("sendnickname", String.valueOf(c.getString(2)));
				item.put("community", String.valueOf(c.getString(3)));
				item.put("address", String.valueOf(c.getString(4)));
				item.put("lng", String.valueOf(c.getString(5)));
				item.put("lat", String.valueOf(c.getString(6)));
				item.put("guid", String.valueOf(c.getString(7)));
				item.put("infocatagroy", String.valueOf(c.getString(8)));
				item.put("message", String.valueOf(c.getString(9)));
				item.put("icon", String.valueOf(c.getString(10)));
				item.put("date", String.valueOf(c.getString(11)));
				item.put("is_coming", String.valueOf(c.getString(12)));
				item.put("readed", String.valueOf(c.getString(13)));
				/*item.put("tag1", "关注数:"+getgz(String.valueOf(c.getString(7))));
				item.put("tag2", "评论数:"+getdicuzz(String.valueOf(c.getString(7))));
				item.put("tag3", "报名数:"+getbm(String.valueOf(c.getString(7))));*/
				item.put("tag1", "关注数:"+getitemnum(String.valueOf(c.getString(7)))[2]);
				item.put("tag2", "评论数:"+getitemnum(String.valueOf(c.getString(7)))[1]);
				//item.put("tag3", "报名数:"+getitemnum(String.valueOf(c.getString(7)))[0]);
				item.put("status", "状态:未结束");
				smalldataList.add(item);
			}
			c.close();
			sdb.close();
		}else
		{
			if(current_sel==0)
			{
				sql = "select * from  [message] order by date  desc limit "+ dataList.size() + ",10";
			}else if(current_sel==1)
			{
				sql = "select * from  [message]  where infocatagroy=0  and senduserid='"+myapplication.getUserId()+"' order by date  desc limit "+ dataList.size() + ",10";
			}else if(current_sel==2)
			{
				sql = "select * from  [message]  where infocatagroy=0 and senduserid!='"+myapplication.getUserId()+"' order by date  desc limit "+ dataList.size() + ",10";
			}else if(current_sel==3)
			{
				sql = "select * from  [message]  where infocatagroy in(1,2) order by date  desc limit "+ dataList.size() + ",10";
			}
			L.i("query",sql);
			Cursor c = sdb.rawQuery(sql, null);
			while (c.moveToNext()) {
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("senduserid", String.valueOf(c.getString(1)));
				item.put("sendnickname", String.valueOf(c.getString(2)));
				item.put("community", String.valueOf(c.getString(3)));
				item.put("address", String.valueOf(c.getString(4)));
				item.put("lng", String.valueOf(c.getString(5)));
				item.put("lat", String.valueOf(c.getString(6)));
				item.put("guid", String.valueOf(c.getString(7)));
				item.put("infocatagroy", String.valueOf(c.getString(8)));
				item.put("message", String.valueOf(c.getString(9)));
				item.put("icon", String.valueOf(c.getString(10)));
				item.put("date", String.valueOf(c.getString(11)));
				item.put("is_coming", String.valueOf(c.getString(12)));
				item.put("readed", String.valueOf(c.getString(13)));
/*				item.put("tag1", "关注数:"+getgz(String.valueOf(c.getString(7))));
				item.put("tag2", "评论数:"+getdicuzz(String.valueOf(c.getString(7))));
				item.put("tag3", "报名数:"+getbm(String.valueOf(c.getString(7))));*/
				item.put("tag1", "浏览数:"+getitemnum(String.valueOf(c.getString(7)))[2]);
				item.put("tag2", "评论数:"+getitemnum(String.valueOf(c.getString(7)))[1]);
				//item.put("tag3", "报名数:"+getitemnum(String.valueOf(c.getString(7)))[0]);
				item.put("status", "状态:未结束");
				smalldataList.add(item);
			}
			c.close();
			sdb.close();
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
					//JSONArray arr=new JSONArray(JsonContext);
					//JSONObject jsonobj=arr.getJSONObject(0);
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
		case R.id.market_layout:
			current_sel=3;
			setTabSelection(current_sel);			
			break;
		default:
			break;
		}
	}

	private void clearSelection() {
		allImage.setImageResource(R.mipmap.tab_sub_all);
		allText.setTextColor(Color.parseColor("#ffffff"));
		mySosImage.setImageResource(R.mipmap.tab_sub_sos_my);
		mySosText.setTextColor(Color.parseColor("#ffffff"));
		sosImage.setImageResource(R.mipmap.tab_sub_sos);
		sosText.setTextColor(Color.parseColor("#ffffff"));
		marketImage.setImageResource(R.mipmap.tab_sub_market);
		marketText.setTextColor(Color.parseColor("#ffffff"));
	}

	
	private void setTabSelection(int index) {
		clearSelection();		
		switch (index) {
		case 0:
			// 当点击了消息tab时，改变控件的图片和文字颜色
			allImage.setImageResource(R.mipmap.tab_sub_all_sel);
			allText.setTextColor(Color.WHITE);
			loadData(AutoListView.REFRESH);
			break;
		case 1:
			// 当点击了联系人tab时，改变控件的图片和文字颜色
			mySosImage.setImageResource(R.mipmap.tab_sub_sos_my_sel);
			mySosText.setTextColor(Color.WHITE);
			loadData(AutoListView.REFRESH);
			break;
		case 2:
			// 当点击了动态tab时，改变控件的图片和文字颜色
			sosImage.setImageResource(R.mipmap.tab_sub_sos_sel);
			sosText.setTextColor(Color.WHITE);
			loadData(AutoListView.REFRESH);
			break;
		case 3:
		default:
			// 当点击了设置tab时，改变控件的图片和文字颜色
			marketImage.setImageResource(R.mipmap.tab_sub_market_sel);
			marketText.setTextColor(Color.WHITE);
			loadData(AutoListView.REFRESH);
			break;
		}		
	}
}
