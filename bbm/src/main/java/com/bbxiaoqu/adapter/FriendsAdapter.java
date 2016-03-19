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
public class FriendsAdapter extends BaseAdapter {
	
	private ViewHolderimg holder_img;
	private List<Map<String, Object>> list;
	private Context context;	

	public FriendsAdapter(Context context, List<Map<String, Object>> list) {
		
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
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_friends, null);
			holder_img.usercatagory = (ImageView) convertView.findViewById(R.id.usercatagory);
			holder_img.username = (TextView) convertView.findViewById(R.id.username);
			holder_img.userhead = (ImageView) convertView.findViewById(R.id.userhead);
			convertView.setTag(holder_img);
		} else {
			holder_img = (ViewHolderimg) convertView.getTag();
		}
		
		holder_img.usercatagory.setImageResource(R.mipmap.dynamic_info_left);
		String fileName =  "http://api.bbxiaoqu.com/uploads/" + list.get(position).get("headface").toString();
		ImageLoader.getInstance().displayImage(fileName,holder_img.userhead,
				ImageOptions.getOptions());
		holder_img.username.setText(list.get(position).get("username").toString());
		return convertView;
	}
	


	private static class ViewHolderimg {
		ImageView usercatagory;
		TextView username;		
		ImageView userhead;
	
	}
	
	
	
	
	 
}
