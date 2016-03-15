package com.bbxiaoqu.ui.sub;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.adapter.BmUserAdapter;
import com.bbxiaoqu.adapter.BmUserAdapter.Callback;
import com.bbxiaoqu.bean.InfoBase;
import com.bbxiaoqu.ui.popup.ListLazyAdapter;
import com.bbxiaoqu.comm.service.db.MessBmService;
import com.bbxiaoqu.comm.service.db.MessGzService;
import com.bbxiaoqu.comm.service.db.XiaoquService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.view.BaseActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;








public class BmUserActivity extends Activity implements  Callback{
	private DemoApplication myapplication;
	ListView lstv;
	private List<Map<String, Object>> data;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	//Context mContext;
	BmUserAdapter adapter;	
	TextView title;
	TextView right_text;
	 private static final int MESSAGETYPE_01 = 0x0001;
	private ProgressDialog progressDialog = null;
	private String guid = "";
	private boolean isbm = false;

	private RatingBar ratingBar;
	private EditText content;
	private Button submit;
	private ImageButton closebtn;
	private RelativeLayout id_info;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_bmuser);
		Bundle Bundle1 = this.getIntent().getExtras();
		guid = Bundle1.getString("guid");
		initView();
		initData();
		myapplication = (DemoApplication) this.getApplication();			
		init();
	}

	private void init() {
		lstv = (ListView) findViewById(R.id.lvbmuser);
		getData() ;
		adapter= new BmUserAdapter(this, dataList, this,this.isbm);
		lstv.setAdapter(adapter);
		lstv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				// TODO Auto-generated method stub
				//Toast.makeText(BmUserActivity.this, "listview的item被点击了！，点击的位置是-->" + position, Toast.LENGTH_SHORT).show();
				Intent Intent1 = new Intent();
				Intent1.setClass(BmUserActivity.this, ViewUserInfoActivity.class);
				Bundle arguments = new Bundle();
				Map<String, Object> map=dataList.get(position);
				arguments.putString("userid", map.get("uerid").toString());
				Intent1.putExtras(arguments);
				startActivity(Intent1);
			}
		});
	}

	private void initView() {
		title = (TextView) findViewById(R.id.title);
		right_text = (TextView) findViewById(R.id.right_text);
		right_text.setVisibility(View.GONE);

		id_info=(RelativeLayout) findViewById(R.id.id_info);
		ratingBar=(RatingBar) findViewById(R.id.ratingBar);
		content=(EditText) findViewById(R.id.content);
		submit=(Button) findViewById(R.id.submit);
		closebtn=(ImageButton) findViewById(R.id.close_btn);
		closebtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				id_info.setVisibility(View.GONE);
			}
		});

		submit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				id_info.setVisibility(View.GONE);
				updateorder(selpos);//完毕
			}
		});

	}

	private void initData() {
		title.setText("人员列表");
		right_text.setText("");
	}
	
	
	private void getData() {
		if (!NetworkUtils.isNetConnected(myapplication)) {
			T.showShort(myapplication, "当前无网络连接！");
			return;
		}
		String target=myapplication.getlocalhost()+"getbmuserlist.php?guid="+guid;
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
			int _id = jsonobject.getInt("id");	
			String _uerid = jsonobject.getString("userid");
			String _username = jsonobject.getString("username");
			String _telphone = jsonobject.getString("telphone");
			String _headface = jsonobject.getString("headface");
			String _status = jsonobject.getString("status");
			_username=_username+"_"+_status;
			if(!_status.equals("0"))
			{//只要其中一个状态不为零,就说明不是报名,就是交易已经完成
				this.isbm = true;
			}
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("id",_id);
			item.put("uerid",_uerid);
			item.put("username",_username);
			item.put("telphone",_telphone);
			item.put("headface",_headface);
			item.put("status",_status);
			list.add(item);
		}
		return list;
	}


	/*@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		// TODO Auto-generated method stub
		 Toast.makeText(this, "listview的item被点击了！，点击的位置是-->" + position,Toast.LENGTH_SHORT).show();
	}
	*/
		int selpos=-1;
	     public void click(View v) {
	    	 String tag=v.getTag().toString();
	    	 String[] arr=tag.split("_");
	    	 selpos=Integer.parseInt(arr[0]);
	    	 String id=arr[1];
	    	/* if(id.equals("tel"))
	    	 {
		    	  Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+dataList.get(selpos).get("telphone")));
		    	  BmUserActivity.this.startActivity(intent);
	    	 }else  if(id.equals("order"))	    	 
	    	 {
	    		 //genorder(pos);//生成状态变1
				 updateorder(selpos);//完毕
	    	 }else  if(id.equals("finsh"))	    	 
	    	 {
	    		 updateorder(selpos);//完毕
	    	 }*/
			 //Toast.makeText(BmUserActivity.this, "listview的按钮被点击了！，点击的位置是-->" + selpos, Toast.LENGTH_SHORT).show();
			 id_info.setVisibility(View.VISIBLE);
	     }

/*
	private RatingBar ratingBar;
	private EditText content;
*/

		private void updateorder(int pos) {
			//SELECT * FROM `info_act` where action=2 
			 //生成订单
			 //订单号:guid
			 //用户id:dataList.get((Integer) v.getTag()).get("uerid")
			 String userid=dataList.get(pos).get("uerid").toString();
			 //更改状态 status=1
			String target = myapplication.getlocalhost()+"genfinshorder.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("_guid", guid));//产品id
			paramsList.add(new BasicNameValuePair("_fromuser", myapplication.getUserId()));//求助人确定订单是否完成
			paramsList.add(new BasicNameValuePair("_userid", userid));//用户id
			paramsList.add(new BasicNameValuePair("_status", "2"));//用户id


			paramsList.add(new BasicNameValuePair("_rating", String.valueOf(ratingBar.getRating())));//用户id
			paramsList.add(new BasicNameValuePair("_content", content.getText().toString()));//用户id
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				String code="";
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					code = EntityUtils.toString(httpResponse.getEntity());
					
				} else {
					code ="";
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("savecode", code);
				msg.setData(data);
				handler.sendMessage(msg);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		private void genorder(int pos) {
			//SELECT * FROM `info_act` where action=2 
			 //生成订单
			 //订单号:guid
			 //用户id:dataList.get((Integer) v.getTag()).get("uerid")
			 String userid=dataList.get(pos).get("uerid").toString();
			 //更改状态 status=1
			String target = myapplication.getlocalhost()+"genorder.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("_guid", guid));//产品id
			paramsList.add(new BasicNameValuePair("_fromuser", myapplication.getUserId()));//求助人才能发起订单
			paramsList.add(new BasicNameValuePair("_userid", userid));//用户id
			paramsList.add(new BasicNameValuePair("_status", "1"));//用户id			
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				String code="";
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					code = EntityUtils.toString(httpResponse.getEntity());
					
				} else {
					code ="";
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("savecode", code);
				//data.putBoolean("isbm", this.isbm);
				msg.setData(data);
				handler.sendMessage(msg);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		
		public void rsload()
		{
			getData() ;
		    adapter= new BmUserAdapter(this, dataList, this,true);
			lstv.setAdapter(adapter);
		}
	    	 
	    	 
	    Handler handler = new Handler() {
	    			@Override
	    			public void handleMessage(Message msg) {
	    				super.handleMessage(msg);
	    				rsload();
	    			
	    			}
	    		};

	     public void doBack(View view) {
	 		onBackPressed();
	 	}

}

