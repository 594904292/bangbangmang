package com.bbxiaoqu.adapter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.service.User;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.ui.LoginActivity;
import com.bbxiaoqu.ui.fragment.HomeActivity;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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

/**
 * @author SunnyCoffee
 * @date 2014-2-2
 * @version 1.0
 * @desc 适配器
 * 
 */
public class ListViewAdapter extends BaseAdapter {
	
	private ViewHolderimg holder_img;
	private List<Map<String, Object>> list;
	private Context context;

	public ListViewAdapter(Context context, List<Map<String, Object>> list) {
		
		this.list = list;
		this.context = context;		
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
	public View getView(int position, View convertView, ViewGroup parent) {
		return haveimg(position, convertView);
	}

	private View haveimg(int position, View convertView) {
		if (convertView == null) {
			holder_img = new ViewHolderimg();
			if(list.get(position).get("icon").toString().trim().length()>0)
			{
				convertView = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
				holder_img.imageView = (ImageView) convertView.findViewById(R.id.imageView);
				holder_img.infocatagroy = (TextView) convertView.findViewById(R.id.infocatagroy);
				holder_img.senduser = (TextView) convertView.findViewById(R.id.senduser);
				holder_img.sendtimer = (TextView) convertView.findViewById(R.id.sendtimer);
				holder_img.sendcontent = (TextView) convertView.findViewById(R.id.sendcontent);
				holder_img.sendaddress = (TextView) convertView.findViewById(R.id.sendaddress);
				holder_img.tag1 = (TextView) convertView.findViewById(R.id.tag1);
				holder_img.tag2 = (TextView) convertView.findViewById(R.id.tag2);
				//holder_img.tag3 = (TextView) convertView.findViewById(R.id.tag3);
				holder_img.status = (TextView) convertView.findViewById(R.id.status);
				
			}else
			{
				convertView = LayoutInflater.from(context).inflate(R.layout.listview_noimg_item, null);
				holder_img.imageView = (ImageView) convertView.findViewById(R.id.imageView);
				holder_img.infocatagroy = (TextView) convertView.findViewById(R.id.infocatagroy);
				holder_img.senduser = (TextView) convertView.findViewById(R.id.senduser);
				holder_img.sendtimer = (TextView) convertView.findViewById(R.id.sendtimer);
				holder_img.sendcontent = (TextView) convertView.findViewById(R.id.sendcontent);
				holder_img.sendaddress = (TextView) convertView.findViewById(R.id.sendaddress);
				holder_img.tag1 = (TextView) convertView.findViewById(R.id.tag1);
				holder_img.tag2 = (TextView) convertView.findViewById(R.id.tag2);
				//holder_img.tag3 = (TextView) convertView.findViewById(R.id.tag3);		
				holder_img.status = (TextView) convertView.findViewById(R.id.status);
			}
			//convertView.setTag(holder_img);
			convertView.setTag(list.get(position).get("guid").toString().trim());
		} else {
			holder_img = (ViewHolderimg) convertView.getTag();
		}
		
		holder_img.imageView.setTag(list.get(position).get("icon").toString());//设置标签
		if(list.get(position).get("icon").toString().trim().length()>0)
		{		
			String fileName="";
			if(list.get(position).get("icon").toString().trim().length()>4)
			{						
				String photo=list.get(position).get("icon").toString().trim();
				if(photo.indexOf(",")>0)
				{
					fileName = DemoApplication.getInstance().getlocalhost()+"uploads/"+photo.split(",")[0]; 
				}else
				{
					fileName = DemoApplication.getInstance().getlocalhost()+"uploads/"+photo; 
				}
			}		 
			ImageLoader.getInstance().displayImage(fileName, holder_img.imageView,  ImageOptions.getOptions(), new ImageLoadingListener()
			{
	
				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					// TODO Auto-generated method stub
					 //加载取消
				}
	
				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
					// TODO Auto-generated method stub
					//加载成功
					if(bitmap==null)
					{
						holder_img.imageView.setImageResource(R.mipmap.empty);
					}
				}
	
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason bitmap) {
					// TODO Auto-generated method stub
					//加载失败
					if(bitmap==null)
					{
						holder_img.imageView.setImageResource(R.mipmap.empty);
					}
				}
				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					// TODO Auto-generated method stub
					//开始加载
				}
			});
		}
		if(list.get(position).get("infocatagroy").toString().equals("0"))
		{//求帮助
			holder_img.infocatagroy.setText("求");
		}else if(list.get(position).get("infocatagroy").toString().equals("1"))
		{//寻宝贝
			holder_img.infocatagroy.setText("寻");
		}else if(list.get(position).get("infocatagroy").toString().equals("2"))
		{//大转让
			holder_img.infocatagroy.setText("转");
		}else if(list.get(position).get("infocatagroy").toString().equals("3"))
		{//我能帮
			holder_img.infocatagroy.setText("帮");
		}
		holder_img.senduser.setText(list.get(position).get("sendnickname").toString());
		holder_img.sendtimer.setText(list.get(position).get("date").toString());
		holder_img.sendcontent.setText(list.get(position).get("message").toString());
		holder_img.sendaddress.setText(list.get(position).get("address").toString());
		
		holder_img.tag1.setText(list.get(position).get("tag1").toString());
		holder_img.tag2.setText(list.get(position).get("tag2").toString());
		if(list.get(position).get("infocatagroy").toString().equals("3")) {
			holder_img.status.setText("");
		}else
		{
			if (list.get(position).get("status").toString().equals("0")) {
				holder_img.status.setTextColor(Color.RED);
				holder_img.status.setText("求助中");
			} else if (list.get(position).get("status").toString().equals("1")) {
				holder_img.status.setTextColor(Color.GREEN);
				holder_img.status.setText("解决中");

			} else if (list.get(position).get("status").toString().equals("2")) {
				holder_img.status.setTextColor(Color.GRAY);
				holder_img.status.setText("已解决");
			}
		}
		return convertView;
	}
	
	
	

	private static class ViewHolderimg {
		ImageView imageView;
		TextView infocatagroy;
		TextView senduser;
		TextView sendtimer;
		TextView sendcontent;
		TextView sendaddress;
		TextView tag1;
		TextView tag2;
		//TextView tag3;
		TextView status;
	}
	
	
	
	public class ProgressLoadtagTask extends AsyncTask<Integer, Integer, String> {  
		  
	    private ListViewAdapter adapter;  
	    private int pos;  
	      
	      
	    public ProgressLoadtagTask(ListViewAdapter textView,int pos) {  
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
	       
	    	/*String nums1 = getgz();
	    	String nums2 = getdicuzz();*/
	    	
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
			
				System.out.println("dicuzzpos:"+pos);
				System.out.println("dicuzzpos:"+adapter.list.get(pos).get("guid").toString());
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
