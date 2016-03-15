package com.bbxiaoqu.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BmUserAdapter extends BaseAdapter implements OnClickListener {
	private LayoutInflater mInflater;
	private List<Map<String, Object>> dataList;
	private Callback mCallback;
	private boolean isbm=false;
	public interface Callback {
		public void click(View v);
	}

	public BmUserAdapter(Context context, List<Map<String, Object>> dataList,
			Callback callback,boolean isbm) {
		this.mInflater = LayoutInflater.from(context);
		this.dataList = dataList;
		this.mCallback = callback;
		this.isbm=isbm;

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
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_item_user, null);
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.imageView);
			holder.username = (TextView) convertView
					.findViewById(R.id.username);
			holder.telphone = (TextView) convertView
					.findViewById(R.id.telphone);
			//holder.button = (Button) convertView.findViewById(R.id.button1);
			holder.button2 = (Button) convertView.findViewById(R.id.button2);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (dataList.get(position).get("headface").toString().length() > 0) {

			String photo = dataList.get(position).get("headface").toString();
			if (photo.indexOf(",") > 0) {
				String fileName = DemoApplication.getInstance().getlocalhost()
						+ "uploads/" + photo.split(",")[0];
				ImageLoader.getInstance().displayImage(fileName,
						holder.imageView, ImageOptions.getOptions());
			} else {
				String fileName = DemoApplication.getInstance().getlocalhost()
						+ "uploads/" + photo;
				ImageLoader.getInstance().displayImage(fileName,
						holder.imageView, ImageOptions.getOptions());
			}
		} else {

			holder.imageView.setImageResource(R.mipmap.empty);
		}

		holder.username.setText(dataList.get(position).get("username")
				.toString());
		holder.telphone.setText(dataList.get(position).get("telphone")
				.toString());

		/*holder.button.setOnClickListener(this);
		holder.button.setTag(position+"_tel");*/

		if(this.isbm)
		{//确定有人报名
			if(dataList.get(position).get("status").toString().equals("0"))
			{//未报名,其它人已经报名,报名不能用
				holder.button2.setVisibility(View.GONE);
				holder.button2.setEnabled(false);//禁用
			}
			else if(dataList.get(position).get("status").toString().equals("1"))
			{//已报名,可确认付款				
				holder.button2.setText("已解决");
				holder.button2.setOnClickListener(this);
				holder.button2.setTag(position+"_finsh");
			}else if(dataList.get(position).get("status").toString().equals("2"))
			{
				holder.button2.setText("活雷锋");
				holder.button2.setOnClickListener(this);
				holder.button2.setTag(position+"_end");
				holder.button2.setEnabled(false);//禁用
			}else
			{
				holder.button2.setVisibility(View.GONE);
				holder.button2.setEnabled(false);//禁用
			}
		}else
		{//没有任务人报名			
			holder.button2.setOnClickListener(this);
			//holder.button2.setTag(position+"_order");
			holder.button2.setTag(position+"_finsh");
		}
		return convertView;
	}

	public final class ViewHolder {

		ImageView imageView;
		TextView username;
		TextView telphone;
		//Button button;
		Button button2;
	}

	@Override
	public void onClick(View v) {
		mCallback.click(v);
	}

}