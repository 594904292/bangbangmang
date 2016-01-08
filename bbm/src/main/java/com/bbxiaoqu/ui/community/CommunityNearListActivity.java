package com.bbxiaoqu.ui.community;

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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.bean.BbMessage;
import com.bbxiaoqu.bean.InfoBase;
import com.bbxiaoqu.ui.community.Community;
import com.bbxiaoqu.ui.fragment.HomeActivity;
import com.bbxiaoqu.comm.service.db.MessageDB;
import com.bbxiaoqu.comm.service.db.XiaoquService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.LoginActivity;
import com.bbxiaoqu.ui.PublishActivity;
import com.bbxiaoqu.ui.sub.InfoBean;
import com.bbxiaoqu.view.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author dzy
 * 小区列表订阅
 */
public class CommunityNearListActivity extends BaseActivity {
	private DemoApplication myapplication;
	SearchView sv = null;
	ListView lstv;
	List<Community> communitylist = new ArrayList<Community>();
	//Context mContext;
	MyListAdapter adapter;
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
		setContentView(R.layout.activity_near_community);
		myapplication = (DemoApplication) this.getApplication();
		xiaoquService = new XiaoquService(this);
		getData();	
		
		sv = (SearchView) findViewById(R.id.searchView1);
		sv.setIconifiedByDefault(false);		 
        sv.setSubmitButtonEnabled(true); 
        sv.setQueryHint("查询");
       
        
        
		lstv = (ListView) findViewById(R.id.lvnear);
		lstv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int location, long arg3) {
				System.out.println("触发了第" + location + "行");
				Community obj=communitylist.get(location);
				//T.show(getApplicationContext(), location, Toast.LENGTH_LONG);
				Intent intent=new Intent(CommunityNearListActivity.this,CommunityActivity.class);
				Bundle arguments = new Bundle();
				arguments.putString("id",obj.getId());	
				arguments.putString("name",obj.getName());	
				arguments.putString("address",obj.getAddress());				
				arguments.putString("lat",obj.getLat());	
				arguments.putString("lng",obj.getLng());	
				arguments.putString("pic",obj.getPic());					
				arguments.putString("business",obj.getBusiness());	
				arguments.putString("develop",obj.getDevelop());	
				arguments.putString("propertymanagement",obj.getPropertymanagement());	
				arguments.putString("propertytype",obj.getPropertytype());	
				arguments.putString("homenumber",obj.getHomenumber());	
				arguments.putString("buildyear",obj.getBuildyear());	
				intent.putExtras(arguments);
				startActivity(intent);
				
			
			}
		});
		adapter = new MyListAdapter(CommunityNearListActivity.this,communitylist);
		lstv.setAdapter(adapter);
		sv.setOnQueryTextListener(new OnQueryTextListener() {
        	 
	            @Override
	            public boolean onQueryTextChange(String str) {
	 
	                return false;
	            }
	 
	            @Override
	            public boolean onQueryTextSubmit(String str) {
	 
	                //Toast.makeText(CommunityListActivity.this, str, Toast.LENGTH_SHORT).show();
	            	keyword=str;
	            	getData();	
	            	
	            	
	                return false;
	            }
	        });
		if (!NetworkUtils.isNetConnected(myapplication)) {			
			T.showShort(myapplication, "当前无网络连接,请稍后再试！");
			return;
		}else
		{
			progressDialog = ProgressDialog.show(CommunityNearListActivity.this, "同步", "数据同步中,请稍候！");                               
		    new Thread(SyncSubscribe).start();//后台同步数据
		}
	}

	
	Runnable SyncSubscribe = new Runnable() {
		@Override
		public void run() {
			String target = myapplication.getlocalhost()+"getsubscribe.php?userid="+myapplication.getUserId();			
			List<InfoBase> bfjllist = null;
			HttpGet httprequest = new HttpGet(target);
			HttpClient HttpClient1 = new DefaultHttpClient();
			// 请求超时
			HttpClient1.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
			// 读取超时
			HttpClient1.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 20000);
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
				InputStream jsonStream = null;
				try {
					jsonStream = httpResponse.getEntity().getContent();
					byte[] data = StreamTool.read(jsonStream);
					String json = new String(data);
					Message msg = new Message();
					Bundle msgbundle = new Bundle();
					msgbundle.putString("json", json);
					msg.setData(msgbundle);
					msg.what = MESSAGETYPE_01;
					savejsonhandler.sendMessage(msg);
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
			
		}
	};

	
	Handler savejsonhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGETYPE_01:                      
					//super.handleMessage(msg);
					Bundle data = msg.getData();
					String json=data.getString("json");
					JSONArray jsonarray = null;
					try {
						jsonarray = new JSONArray(json);
						for (int i = 0; i < jsonarray.length(); i++) {
							JSONObject customJson = jsonarray.getJSONObject(i);
							String  community=customJson.getString("community").toString();
							String userid=myapplication.getUserId();
							XiaoquService xiaoquService = new XiaoquService(myapplication.getApplicationContext());
							if(!xiaoquService.isexit(community))
							{
								xiaoquService.addxiaoqu(community);
							}						
 						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					progressDialog.dismiss(); //关闭进度条
					break;
			}
			
		}

		
	};


	String keyword="";
	private void getData() {
		String target="";
		communitylist.clear();
		if(keyword.length()>0)
		{
		
		 target = myapplication.getlocalhost()+"getxiaoqu.php?keyword="+keyword;
		}else
		{
			
		 target = myapplication.getlocalhost()+"getxiaoqu.php?latitude="
						+ myapplication.getLat() + "&longitude="
						+ myapplication.getLng();
				
		}
		try {
			// /////////////////////////////
			List<InfoBase> bfjllist = null;
			HttpGet httprequest = new HttpGet(target);
			HttpClient HttpClient1 = new DefaultHttpClient();
			// 请求超时
			HttpClient1.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
			// 读取超时
			HttpClient1.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 20000);
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
				InputStream jsonStream = null;
				try {
					jsonStream = httpResponse.getEntity().getContent();
					byte[] data = StreamTool.read(jsonStream);
					String json = new String(data);
					Community mcommunity;
					JSONArray jsonarray = new JSONArray(json);
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject jsonobject = jsonarray.getJSONObject(i);
						double distance = InfoBean
								.getDistance(Double.parseDouble(myapplication
										.getLat()), Double
										.parseDouble(myapplication.getLng()),
										Double.parseDouble(jsonobject
												.getString("lat")), Double
												.parseDouble(jsonobject
														.getString("lng")));
						mcommunity = new Community();
						mcommunity.setId(jsonobject.getString("id"));
						mcommunity.setName(jsonobject.getString("name"));
						mcommunity.setAddress(jsonobject.getString("address"));
						mcommunity.setLat(jsonobject.getString("lat"));
						mcommunity.setLng(jsonobject.getString("lng"));
						mcommunity.setPic(jsonobject.getString("pic"));
						mcommunity
								.setBusiness(jsonobject.getString("business"));
						mcommunity.setDevelop(jsonobject.getString("develop"));
						mcommunity.setPropertymanagement(jsonobject
								.getString("propertymanagement"));
						mcommunity.setPropertytype(jsonobject
								.getString("propertytype"));
						mcommunity.setHomenumber(jsonobject
								.getString("homenumber"));
						mcommunity.setBuildyear(jsonobject
								.getString("buildyear"));
						XiaoquService xiaoquService = new XiaoquService(this);
						boolean ishavezhan = xiaoquService.isexit(mcommunity
								.getId());
						if (ishavezhan) {
							mcommunity.setIsgz(1);
						} else {
							mcommunity.setIsgz(0);
						}
						xiaoquService.close();
						mcommunity.setDistance(String.valueOf(distance));
						communitylist.add(mcommunity);
					}
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
		if(keyword.length()>0)
		{
			adapter.notifyDataSetChanged();
		}
	}

	// 自定义ListView适配器
	class MyListAdapter extends BaseAdapter {
		
		List<Community> listCommunity;
		HashMap<Integer, View> map = new HashMap<Integer, View>();
		private Context context;
		
		public MyListAdapter(Context context,List<Community> list) {
			this.context = context;
			listCommunity = new ArrayList<Community>();
			listCommunity = list;

		
		}

		@Override
		public int getCount() {
			return listCommunity.size();
		}

		@Override
		public Object getItem(int position) {
			return listCommunity.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder = null;

			if (map.get(position) == null) {

				view = LayoutInflater.from(context).inflate(R.layout.community_near_list_item, null);
				
				holder = new ViewHolder();
				holder.imageView = (ImageView) view
						.findViewById(R.id.xq_imageView);
				holder.distance = (TextView) view
						.findViewById(R.id.list_distance);
				holder.homenumber = (TextView) view
						.findViewById(R.id.list_homenumber);
				holder.isgz = (TextView) view
						.findViewById(R.id.list_isgz);
				
				holder.name = (TextView) view.findViewById(R.id.list_name);
				holder.address = (TextView) view
						.findViewById(R.id.list_address);
				final int p = position;
				map.put(position, view);
						
				view.setTag(holder);
			} else {
				Log.e("SubscribeCommunityActivity", "position2 = " + position);
				view = map.get(position);
				holder = (ViewHolder) view.getTag();
			}

			

			String fileName = listCommunity.get(position).getPic();
			ImageLoader.getInstance().displayImage(fileName, holder.imageView,ImageOptions.getOptions());
			holder.distance.setText(listCommunity.get(position).getDistance()+ "米");
			holder.homenumber.setText(listCommunity.get(position).getHomenumber());
			holder.name.setText(listCommunity.get(position).getName());
			holder.address.setText(listCommunity.get(position).getAddress());
			if (listCommunity.get(position).getIsgz() == 1) {
				
				holder.isgz.setText("已关注");
			} else {
				
				holder.isgz.setText("");
			}
			return view;
		}

	}

	static class ViewHolder {
		ImageView imageView;
		TextView distance;
		TextView homenumber;	
		TextView isgz;
		TextView name;
		TextView address;
	}
}
