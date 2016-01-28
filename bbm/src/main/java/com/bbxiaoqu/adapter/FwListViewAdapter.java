package com.bbxiaoqu.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.service.db.MessGzService;
import com.bbxiaoqu.comm.service.db.MessZanService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * @author SunnyCoffee
 * @date 2014-2-2
 * @version 1.0
 * @desc 适配器
 * 
 */
	public class FwListViewAdapter extends BaseAdapter {
	private static final String TAG = "FwListViewAdapter";
	private List<Map<String, Object>> list;
	final private Context context;
	private DemoApplication myapplication;
	private String infoid = "0";
	private String guid = "";
	private String gzaction = "";
	public FwListViewAdapter(Context context, List<Map<String, Object>> list) {
		this.list = list;
		this.context = context;
		myapplication = (DemoApplication) context.getApplicationContext();
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		return haveimg(position, convertView);
	}
	//ViewHolderimg holder_img = null;
	private View haveimg(int position, View convertView) {
		final ViewHolderimg holder_img;
		final DemoApplication myapplication = (DemoApplication) this.context.getApplicationContext();
		MessZanService messgzService = new MessZanService(this.context);
		if (convertView == null) {
			holder_img = new ViewHolderimg();
			//可以理解为从vlist获取view  之后把view返回给ListView
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_fw, null);
			holder_img.imageView = (ImageView) convertView.findViewById(R.id.imageView);
			holder_img.senduser = (TextView) convertView.findViewById(R.id.senduser);
			holder_img.sendcontent = (TextView) convertView.findViewById(R.id.sendcontent);
			holder_img.usertag = (TextView) convertView.findViewById(R.id.usertags);
			holder_img.zannum = (TextView) convertView.findViewById(R.id.zannum);
			holder_img.zan = (ImageView) convertView.findViewById(R.id.zan);
			holder_img.tel = (ImageView) convertView.findViewById(R.id.telphone);
			convertView.setTag(holder_img);
		}else
		{
			holder_img = (ViewHolderimg)convertView.getTag();
		}

		View.OnClickListener listener=new View.OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(v==holder_img.tel) {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_CALL);
					intent.setData(Uri.parse("tel:" + v.getTag().toString()));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					v.getContext().getApplicationContext().startActivity(intent);
				}
				if(v==holder_img.zan) {
					ImageView zanimg = (ImageView) v;
					String n_tag=v.getTag().toString();
					String[] arr=n_tag.split("_");
					int num=Integer.parseInt(arr[0]);
					int n1=0;
					int iszan=Integer.parseInt(arr[1]);
					String guidstr=arr[2];
					MessZanService messgzService = new MessZanService(v.getContext().getApplicationContext());

					boolean ishavezhan1 = messgzService.isexit(guidstr,myapplication.getUserId());
					System.out.println(guidstr+"____"+myapplication.getUserId()+"____"+ishavezhan1);
					if(ishavezhan1)
					{
						zanimg.setImageResource(R.mipmap.tab_sub_sos);
						n1=num-1;
						zanimg.setTag(n1+"_0_"+guidstr);
						messgzService.removezan(guidstr, myapplication.getUserId());
						T.showShort(myapplication, "取消赞！");
						gzaction = "remove";
						guid=guidstr;
						new Thread(savegzThread).start();
					}else
					{
						zanimg.setImageResource(R.mipmap.tab_sub_sos_sel);
						n1=num+1;
						zanimg.setTag(n1+"_1_"+guidstr);
						messgzService.addzan(guidstr, myapplication.getUserId());
						T.showShort(myapplication, "赞成功！");
						guid=guidstr;
						gzaction = "add";
						new Thread(savegzThread).start();
					}
					holder_img.zannum.setText(String.valueOf(n1));
				}
			}
		};

		holder_img.imageView.setTag(position);//设置标签
		if(list.get(position).get("headface")!=null&&list.get(position).get("headface").toString().trim().length()>0)
		{
			String fileName="";
			if(list.get(position).get("headface").toString().trim().length()>4)
			{
				String headface=list.get(position).get("headface").toString().trim();
				fileName = DemoApplication.getInstance().getlocalhost()+"uploads/"+headface;
			}
			ImageLoader.getInstance().displayImage(fileName, holder_img.imageView,  ImageOptions.getOptions(), new ImageLoadingListener()
			{
				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					// TODO Auto-generated method stub
				}
				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
					// TODO Auto-generated method stub
					if(bitmap==null)
					{
						holder_img.imageView.setImageResource(R.mipmap.t3);
					}
				}
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason bitmap) {
					// TODO Auto-generated method stub
					if(bitmap==null)
					{
						holder_img.imageView.setImageResource(R.mipmap.t3);
					}
				}
				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					// TODO Auto-generated method stub
				}
			});
		}
		//tab_sub_sos
		holder_img.senduser.setText(list.get(position).get("sendnickname").toString());
		//title
		if(list.get(position).get("title").toString().equals("帮帮忙"))
		{
			holder_img.sendcontent.setText(list.get(position).get("content").toString());
		}
		else
		{
			holder_img.sendcontent.setText(list.get(position).get("title").toString());
		}
		holder_img.zannum.setText(list.get(position).get("zannum").toString());
		holder_img.usertag.setText(list.get(position).get("tags").toString());
		holder_img.tel.setTag(list.get(position).get("telphone").toString());
		holder_img.tel.setOnClickListener(listener);

		boolean ishavezhan = messgzService.isexit(list.get(position).get("guid").toString(),myapplication.getUserId());
		Log.v(TAG,list.get(position).get("guid").toString()+"____"+myapplication.getUserId()+"____"+ishavezhan);
		if(ishavezhan)
		{
			holder_img.zan.setImageResource(R.mipmap.tab_sub_sos_sel);
			holder_img.zan.setTag(list.get(position).get("zannum").toString()+"_1_"+list.get(position).get("guid").toString());
			holder_img.zan.setOnClickListener(listener);

		}else {
			holder_img.zan.setImageResource(R.mipmap.tab_sub_sos);
			holder_img.zan.setTag(list.get(position).get("zannum").toString()+"_0_"+list.get(position).get("guid").toString());
			holder_img.zan.setOnClickListener(listener);
		}
		return convertView;
	}


	Runnable savegzThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost() + "adduserzaninfo.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("_userid", myapplication.getUserId()));// 本人
			paramsList.add(new BasicNameValuePair("_guid", guid));// 本人
			paramsList.add(new BasicNameValuePair("_action", gzaction));// 本人
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String json = EntityUtils
							.toString(httpResponse.getEntity());
					result = Integer.parseInt(json);
				} else {
					result = 0;
				}
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	};

	private static class ViewHolderimg {
		ImageView imageView;
		TextView senduser;
		TextView sendcontent;
		TextView usertag;
		TextView zannum;
		ImageView zan;
		ImageView tel;
	}
	
	
	
	public class ProgressLoadtagTask extends AsyncTask<Integer, Integer, String> {  
		  
	    private FwListViewAdapter adapter;
	    private int pos;  
	      
	      
	    public ProgressLoadtagTask(FwListViewAdapter textView, int pos) {
	        super();  
	        this.adapter = textView;  
	        this.pos = pos;  
	    }  
	  
	  
	    /**  
	     * 这里的Integer参数对应AsyncTask中的第一个参数   
	     * 这里的String返回值对应AsyncTask的第三个参数  
	     * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改  
	     * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作  
	     */  
	    @Override  
	    protected String doInBackground(Integer... params) {
	    	String nums1 = getgz();
	    	String nums2 = getdicuzz();;
	    	adapter.list.get(pos).put("tag1", "关注数:"+String.valueOf(nums1));
	    	adapter.list.get(pos).put("tag2", "评论数:"+String.valueOf(nums2));
	        return null;  
	    }


		private String getdicuzz() {
			String target = DemoApplication.getInstance().getlocalhost()+"getdiscussnum.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("_guid", adapter.list.get(pos).get("guid").toString()));
			String nums="0";
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
						JSONArray arr=new JSONArray(JsonContext);
						JSONObject jsonobj=arr.getJSONObject(0);
						nums=jsonobj.getString("nums");
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
		
		
		private String getgz() {
			String target = DemoApplication.getInstance().getlocalhost()+"getgznum.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			
				System.out.println("gzpos:"+pos);
				System.out.println("gzpos:"+adapter.list.get(pos).get("guid").toString());
			
			paramsList.add(new BasicNameValuePair("_guid", adapter.list.get(pos).get("guid").toString()));	
			String nums="0";
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
						JSONArray arr=new JSONArray(JsonContext);
						JSONObject jsonobj=arr.getJSONObject(0);
						nums=jsonobj.getString("nums");
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
	  
	  
	    /**  
	     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）  
	     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置  
	     */  
	    @Override  
	    protected void onPostExecute(String result) {  
	        //adapter.setText("异步操作执行结束" + result);  
	    	adapter.notifyDataSetChanged();
	    }  
	  
	  
	    //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置  
	    @Override  
	    protected void onPreExecute() {  
	        //adapter.setText("开始执行异步线程");  
	    }  
	  
	  
	    /**  
	     * 这里的Intege参数对应AsyncTask中的第二个参数  
	     * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行  
	     * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作  
	     */  
	    @Override  
	    protected void onProgressUpdate(Integer... values) {  
	        //int vlaue = values[0];  	          
	    }  
	  
	      
	      
	      
	  
	}  

	 
}
