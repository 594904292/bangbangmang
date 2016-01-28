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

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.bean.InfoBase;
import com.bbxiaoqu.comm.service.db.MessGzService;
import com.bbxiaoqu.comm.service.db.XiaoquService;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.ui.main.ViewActivity;
import com.bbxiaoqu.ui.main.ViewFwActivity;
import com.bbxiaoqu.view.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class InfoMyActivity extends Activity {
	private DemoApplication myapplication;
	
	ListView lstv;
	private List<Map<String, Object>> data;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	//Context mContext;
	MyAdapter adapter;
	XiaoquService xiaoquService;

	 private static final int MESSAGETYPE_01 = 0x0001;
	private ProgressDialog progressDialog = null;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_gzinfo);
		
		myapplication = (DemoApplication) this.getApplication();
		xiaoquService = new XiaoquService(this);
		getData();	
		lstv = (ListView) findViewById(R.id.lvgz);
		lstv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int location, long arg3) {
				Intent Intent1=null;
				if(dataList.get(location).get("infocatagroy").toString().equals("3")) {
					Intent1 = new Intent(InfoMyActivity.this, ViewFwActivity.class);
				}else{
					Intent1 = new Intent(InfoMyActivity.this, ViewActivity.class);
				}
				//Intent Intent1=new Intent(InfoMyActivity.this,ViewActivity.class);
				Bundle arguments = new Bundle();
				arguments.putString("put", "false");
				arguments.putString("guid",dataList.get(location).get("guid").toString());
				Intent1.putExtras(arguments);
				startActivity(Intent1);
			}
		}); 
       
		adapter = new MyAdapter(this.getApplicationContext());			
		
		loadData();
	
	}

	
	private void loadData() {
		// TODO Auto-generated method stub
	    if (lstv == null)
        {
          return;
        }
	    getData() ;      
	    lstv.setAdapter(adapter);
	}
	
	private void getData() {
		
		String target=myapplication.getlocalhost()+"getmyinfo.php?userid="+myapplication.getUserId();
		 dataList = new ArrayList<Map<String, Object>>();
	    	try {
	    		///////////////////////////////
	    		List<InfoBase> bfjllist=null;
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
	    		
	    		///////////////////////////////

				for (InfoBase InfoBases: bfjllist) {
					HashMap<String, Object> item = new HashMap<String, Object>();
					item.put("id",String.valueOf(InfoBases.getId()));
					item.put("guid",String.valueOf(InfoBases.getGuid()));
					item.put("senduser",String.valueOf(InfoBases.getSenduser()));
					item.put("lat",String.valueOf(InfoBases.getLat()));
					item.put("lng",String.valueOf(InfoBases.getLng()));
					item.put("title",String.valueOf(InfoBases.getTitle()));
					item.put("content",String.valueOf(InfoBases.getContent()));
					item.put("photo",String.valueOf(InfoBases.getPhoto()));
					item.put("voice",String.valueOf(InfoBases.getVoice()));
					item.put("sendtime",InfoBases.getSendtime());
					item.put("address",InfoBases.getAddress());
					item.put("headface",InfoBases.getHeadface());
					item.put("guid",InfoBases.getGuid());
					item.put("infocatagroy",InfoBases.getInfocatagroy());
					dataList.add(item);
				}					
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  		
		}

	
	private static List<InfoBase> parsejson(InputStream jsonStream)
			throws Exception {
		List<InfoBase> list = new ArrayList<InfoBase>();
		byte[] data = StreamTool.read(jsonStream);
		String json = new String(data);
		JSONArray jsonarray = new JSONArray(json);
		for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject jsonobject = jsonarray.getJSONObject(i);
			int _id = jsonobject.getInt("id");	
			String _senduser = jsonobject.getString("senduser");
			String _lat = jsonobject.getString("lat");
			String _lng = jsonobject.getString("lng");
			String _title = jsonobject.getString("title");
			String _content = jsonobject.getString("content");
			String _photo = jsonobject.getString("photo");
			String _voice = jsonobject.getString("voice");
			String _sendtime = jsonobject.getString("sendtime");
			String _address= jsonobject.getString("address");
			String _headface= jsonobject.getString("headface");
			String _guid= jsonobject.getString("guid");
			String _infocatagroy= jsonobject.getString("infocatagroy");
		
			
			
			
			list.add(new InfoBase(_id,_senduser,_lat,_lng, _title,_content,_photo,_voice,
					_sendtime, _address,_headface,_guid,_infocatagroy));
		}
		return list;
	}
	
	
	



	
	 public final class ViewHolder{
			
	    	ImageView imageView;
			TextView infocatagroy;
			TextView senduser;
			TextView sendtimer;
			TextView sendcontent;
			TextView sendaddress;
		}
		
	    public class MyAdapter extends BaseAdapter{
	    	 private Context ctx;  
		    	private LayoutInflater mInflater;
				
				
				public MyAdapter(Context context){
					 this.ctx = context;  
					this.mInflater = LayoutInflater.from(context);
				}
				
				@Override
				public int getCount() {
					// TODO Auto-generated method stub
					return dataList == null ? 0 : dataList.size();  
				}

				@Override
				public Object getItem(int position) {
					// TODO Auto-generated method stub
					return dataList.get(position); 
				}

				@Override
				public long getItemId(int position) {
					// TODO Auto-generated method stub
					return position;
				}


				
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					// TODO Auto-generated method stub
					ViewHolder holder = null;
					if (convertView == null) {				
						holder=new ViewHolder();  
						convertView = mInflater.inflate(R.layout.list_item_info, null);
						holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
						holder.infocatagroy = (TextView) convertView.findViewById(R.id.infocatagroy);
						holder.senduser = (TextView) convertView.findViewById(R.id.senduser);
						holder.sendtimer = (TextView) convertView.findViewById(R.id.sendtimer);
						holder.sendcontent = (TextView) convertView.findViewById(R.id.sendcontent);
						holder.sendaddress = (TextView) convertView.findViewById(R.id.sendaddress);
						 convertView.setTag(holder);  
					}else {				
						holder = (ViewHolder)convertView.getTag();
					}
					if(dataList.get(position).get("photo").toString().length()>0)
					{
						
						
						String photo=dataList.get(position).get("photo").toString();
						if(photo.indexOf(",")>0)
						{
							String fileName = myapplication.getlocalhost()+"uploads/"+photo.split(",")[0]; 
							ImageLoader.getInstance().displayImage(fileName, holder.imageView, ImageOptions.getOptions());  
						}else
						{
							String fileName = myapplication.getlocalhost()+"uploads/"+photo; 
							ImageLoader.getInstance().displayImage(fileName, holder.imageView, ImageOptions.getOptions());  
						}
					}else
					{
						
						holder.imageView.setImageResource(R.mipmap.empty);
					}
					
					if(dataList.get(position).get("infocatagroy").toString().equals("0"))
					{
						holder.infocatagroy.setText("求");
					}else if(dataList.get(position).get("infocatagroy").toString().equals("1"))
					{
						holder.infocatagroy.setText("购");
					}else if(dataList.get(position).get("infocatagroy").toString().equals("2"))
					{
						holder.infocatagroy.setText("售");
					}else if(dataList.get(position).get("infocatagroy").toString().equals("3"))
					{
						holder.infocatagroy.setText("帮");
					}
					
					holder.senduser.setText(dataList.get(position).get("senduser").toString());
					holder.sendtimer.setText(dataList.get(position).get("sendtime").toString());
					holder.sendcontent.setText(dataList.get(position).get("content").toString());
					holder.sendaddress.setText(dataList.get(position).get("address").toString());
					return convertView;
				}
	    }
}

