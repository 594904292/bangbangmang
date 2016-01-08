package com.bbxiaoqu.ui;


import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.bean.BbMessage;
import com.bbxiaoqu.bean.InfoBase;
import com.bbxiaoqu.comm.service.db.DatabaseHelper;
import com.bbxiaoqu.comm.service.db.MessGzService;
import com.bbxiaoqu.comm.service.db.NoticeDB;
import com.bbxiaoqu.comm.service.db.XiaoquService;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.main.ViewActivity;
import com.bbxiaoqu.ui.sub.ChattingActivity;
import com.bbxiaoqu.ui.sub.InfoGzActivity;
import com.bbxiaoqu.ui.sub.ViewUserInfoActivity;
import com.bbxiaoqu.ui.sub.InfoGzActivity.MyAdapter;
import com.bbxiaoqu.ui.sub.InfoGzActivity.ViewHolder;
import com.bbxiaoqu.view.BaseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class NoticeActivity extends BaseActivity {
	TextView back;
	TextView title;
	TextView right_text;
	private DemoApplication myapplication;
	NoticeDB db=new NoticeDB(this);
	ListView lstv;
	//private List<Map<String, String>> data;
	private List<Map<String, String>> dataList = new ArrayList<Map<String, String>>();
	//Context mContext;
	NoticeAdapter adapter;
	XiaoquService xiaoquService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notice);	
		initView();
		initData();
		myapplication = (DemoApplication) this.getApplication();
		xiaoquService = new XiaoquService(this);
		
		/*ProgressDataInitAsyncTask task=new ProgressDataInitAsyncTask();//初始化数据
		task.execute(1000);*/
		
		lstv = (ListView) findViewById(R.id.lvnotice);
		lstv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int location, long arg3) {		
				//T.showShort(NoticeActivity.this, dataList.get(location).get("relativeid").toString());
				if(dataList.get(location).get("catagory").toString().equals("评论"))
				{					
					Intent Intent1 = new Intent();
					Intent1.setClass(NoticeActivity.this, ViewActivity.class);
					Bundle arguments = new Bundle();
					arguments.putString("put", "false");
					arguments.putString("guid",dataList.get(location).get("relativeid").toString());
					Intent1.putExtras(arguments);
					startActivity(Intent1);
				}else if(dataList.get(location).get("catagory").toString().equals("消息"))
				{
					Intent Intent1 = new Intent();
					Intent1.setClass(NoticeActivity.this, ViewActivity.class);
					Bundle arguments = new Bundle();
					arguments.putString("put", "false");
					arguments.putString("guid",dataList.get(location).get("relativeid").toString());
					Intent1.putExtras(arguments);
					startActivity(Intent1);
				}else if(dataList.get(location).get("catagory").toString().equals("私信"))
				{
					
					Intent intent = new Intent(NoticeActivity.this,ChattingActivity.class);
					
					Bundle arguments = new Bundle();
					arguments.putString("to", dataList.get(location).get("relativeid").toString());
					arguments.putString("my",myapplication.getUserId());
					intent.putExtras(arguments);					
					startActivity(intent);	
					
				}
				else if(dataList.get(location).get("catagory").toString().equals("订单"))
				{
					Intent intent = new Intent(NoticeActivity.this,ChattingActivity.class);
					System.out.println("订单跳到对应的guidViewActivity");
					
					Intent Intent1 = new Intent();
					Intent1.setClass(NoticeActivity.this, ViewActivity.class);
					Bundle arguments = new Bundle();
					arguments.putString("put", "false");
					arguments.putString("guid",dataList.get(location).get("relativeid").toString());
					Intent1.putExtras(arguments);
					startActivity(Intent1);
					
					/*Bundle arguments = new Bundle();
					arguments.putString("to", dataList.get(location).get("relativeid").toString());
					arguments.putString("my",myapplication.getUserId());
					intent.putExtras(arguments);					
					startActivity(intent);	*/				
				}
			}
		}); 
		adapter = new NoticeAdapter(this.getApplicationContext());			
		loadData();	
	}

	private void initView() {
		
		back = (TextView)findViewById(R.id.back);
		title = (TextView)findViewById(R.id.title);
		right_text = (TextView)findViewById(R.id.right_text);
		right_text.setVisibility(View.VISIBLE);
		back.setVisibility(View.GONE);
		right_text.setClickable(true);		
	}

	private void initData() {
		title.setText("消息");
		right_text.setText("");
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();	
		finish();
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
		
		DatabaseHelper dbHelper=new DatabaseHelper(this);
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		String sql="";
		sql = "select * from  [notice] order by date  desc limit 0,10";
			Cursor c = sdb.rawQuery(sql, null);
			while (c.moveToNext()) {				
				//Message message = new Message(to,to,System.currentTimeMillis(), str);
				HashMap<String, String> item = new HashMap<String, String>();
				item.put("id", c.getString(0));
				item.put("date", c.getString(1));
				item.put("catagory", c.getString(2));
				
				item.put("relativeid", c.getString(3));
				item.put("content", c.getString(4));
				item.put("readed", c.getString(5));
				dataList.add(item);
			}
			c.close();
			sdb.close();
	}
	
	
	
	
	



	
	 public final class ViewHolder{
				    
			TextView date;
			TextView catagory;
			TextView content;
			
			//TextView relativeid;
			//TextView readed;
		}
		
	    public class NoticeAdapter extends BaseAdapter{
	    	 private Context ctx;  
		    	private LayoutInflater mInflater;
				
				
				public NoticeAdapter(Context context){
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
						convertView = mInflater.inflate(R.layout.list_item_notice, null);
						holder.date = (TextView) convertView.findViewById(R.id.notice_time);
						holder.catagory = (TextView) convertView.findViewById(R.id.notice_catagory);
						holder.content = (TextView) convertView.findViewById(R.id.notice_content);						
						convertView.setTag(holder);  
					}else {				
						holder = (ViewHolder)convertView.getTag();
					}					
					holder.date.setText(dataList.get(position).get("date").toString());
					holder.catagory.setText(dataList.get(position).get("catagory").toString());
					holder.content.setText(dataList.get(position).get("content").toString());					
					return convertView;
				}
	    }
	
	    public class ProgressDataInitAsyncTask extends AsyncTask<Integer, Integer, String> {  
	    	  
	       
	        public ProgressDataInitAsyncTask() {  
	            super();  
	          
	        }  
	      
	      
	        /**  
	         * 这里的Integer参数对应AsyncTask中的第一个参数   
	         * 这里的String返回值对应AsyncTask的第三个参数  
	         * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改  
	         * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作  
	         */  
	        @Override  
	        protected String doInBackground(Integer... params) {
	        	//向数据库中写入几条样例数据
	        	SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	    		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
/*	        	db.add(sDateFormat.format(curDate), "消息", "11111", "新消息", "0");
	        	db.add(sDateFormat.format(curDate), "评论", "22222", "新评论", "0");
	        	db.add(sDateFormat.format(curDate), "私信", "11111", "新对话", "0");*/
				return null;  	           
	        }  
	      
	      
	        /**  
	         * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）  
	         * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置  
	         */  
	        @Override  
	        protected void onPostExecute(String result) {  
	           
	        }  
	      
	      
	        //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置  
	        @Override  
	        protected void onPreExecute() {  
	             
	        }  
	      
	      
	        /**  
	         * 这里的Intege参数对应AsyncTask中的第二个参数  
	         * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行  
	         * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作  
	         */  
	        @Override  
	        protected void onProgressUpdate(Integer... values) {  
	           
	        }  
	      
	          
	          
	          
	      
	    }  
}
