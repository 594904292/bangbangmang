package com.bbxiaoqu.adapter;

import java.util.List;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.bean.ChatMessage;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ChatMessageAdapter extends BaseAdapter
{
	private LayoutInflater mInflater;
	private List<ChatMessage> mDatas;
	private DemoApplication myapplication;

	public ChatMessageAdapter(Context context, List<ChatMessage> datas)
	{
		mInflater = LayoutInflater.from(context);
		mDatas = datas;
		Activity activity = (Activity) context;
		 myapplication = (DemoApplication) activity.getApplication();

	}

	@Override
	public int getCount()
	{
		return mDatas.size();
	}

	@Override
	public Object getItem(int position)
	{
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public int getItemViewType(int position)
	{
		ChatMessage msg = mDatas.get(position);
		return msg.isComing() ? 1 : 0;
	}

	@Override
	public int getViewTypeCount()
	{
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ChatMessage chatMessage = mDatas.get(position);

		ViewHolder viewHolder = null;

		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			if (chatMessage.isComing())
			{
				convertView = mInflater.inflate(R.layout.main_chat_from_msg,
						parent, false);
				viewHolder.createDate = (TextView) convertView
						.findViewById(R.id.chat_from_createDate);
				viewHolder.content = (TextView) convertView
						.findViewById(R.id.chat_from_content);
				viewHolder.nickname = (TextView) convertView
						.findViewById(R.id.chat_from_name);
				viewHolder.headface = (ImageView) convertView
						.findViewById(R.id.chat_from_icon);
				convertView.setTag(viewHolder);
			} else
			{
				convertView = mInflater.inflate(R.layout.main_chat_send_msg,
						null);
				
				viewHolder.createDate = (TextView) convertView
						.findViewById(R.id.chat_send_createDate);
				viewHolder.content = (TextView) convertView
						.findViewById(R.id.chat_send_content);
				viewHolder.nickname = (TextView) convertView
						.findViewById(R.id.chat_send_name);
				viewHolder.headface = (ImageView) convertView
						.findViewById(R.id.chat_send_icon);
				convertView.setTag(viewHolder);
			}

		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
//		Date date = chatMessage.getDate();
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		viewHolder.content.setText(chatMessage.getMessage());
		viewHolder.createDate.setText(chatMessage.getDateStr());
		viewHolder.nickname.setText(chatMessage.getSendnickname());
		
    	ImageLoader.getInstance().displayImage(myapplication.getlocalhost()+"uploads/"+chatMessage.getSenduserIcon(), viewHolder.headface,ImageOptions.getOptions());
		return convertView;
	}

	private class ViewHolder
	{
		public TextView createDate;
		public TextView content;
		public TextView nickname;
		public ImageView headface;
	}

}
