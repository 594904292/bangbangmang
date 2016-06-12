package com.bbxiaoqu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Map;

/**
 * @author SunnyCoffee
 * @date 2014-2-2
 * @version 1.0
 * @desc 适配器
 * 
 */
public class EvaluateAdapter extends BaseAdapter {

	private ViewHolderimg holder_img;
	private List<Map<String, Object>> list;
	private Context context;

	public EvaluateAdapter(Context context, List<Map<String, Object>> list) {
		
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
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_evaluate, null);
			holder_img.usercatagory = (ImageView) convertView.findViewById(R.id.usercatagory);
			holder_img.infouser = (TextView) convertView.findViewById(R.id.infouser);
			holder_img.addtime = (TextView) convertView.findViewById(R.id.addtime);
			holder_img.userhead = (ImageView) convertView.findViewById(R.id.userhead);//
			holder_img.evaluate = (TextView) convertView.findViewById(R.id.evaluate);
			holder_img.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
			convertView.setTag(holder_img);
		} else {
			holder_img = (ViewHolderimg) convertView.getTag();
		}
		holder_img.usercatagory.setImageResource(R.mipmap.dynamic_info_left);
		holder_img.evaluate.setText(list.get(position).get("evaluate").toString());
		holder_img.infouser.setText(list.get(position).get("username").toString());
		holder_img.addtime.setText(list.get(position).get("addtime").toString());
		holder_img.ratingBar.setRating(Float.parseFloat(list.get(position).get("score").toString()));
		return convertView;
	}
	


	private static class ViewHolderimg {
		ImageView usercatagory;
		TextView infouser;
		TextView addtime;
		ImageView userhead;
		TextView evaluate;
		RatingBar ratingBar;
	}
	
	
	
	
	 
}
