package com.bbxiaoqu.adapter;

import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.service.db.DatabaseHelper;
import com.bbxiaoqu.comm.service.db.SQLiteInterestManager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



public class GridAddInterestAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private static final String[] interestnamearr = 
			{ "体育", "足球", "运动", "家教", "房产", "兼职","促销","培训"};
	SparseBooleanArray sba = new SparseBooleanArray();
	private String nowcityname;

	public GridAddInterestAdapter(Context context) {
		this.mInflater = LayoutInflater.from(context);

		SQLiteInterestManager sqlite = new SQLiteInterestManager(context, "bbdb", null, 1);
		SQLiteDatabase db = sqlite.getReadableDatabase();
		Cursor cursor = db.query("interest", null, null, null, null, null, null);
		while(cursor.moveToNext()){
			nowcityname = cursor.getString(cursor.getColumnIndex("interestname"));
			Log.i("TAG", nowcityname+"-->nowcityname");
			for(int i=0;i<interestnamearr.length;i++){
				if(nowcityname.equals(interestnamearr[i])){
					sba.put(i, true);
				}
			}
		}
		sqlite.close();
	}

	@Override
	public int getCount() {
		return interestnamearr == null ? 0 : interestnamearr.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_gridview_addcity,
					parent, false);// 此处需要加上第二个参数parent，否则item中的设置无效。如item高度设置。
		}
		TextView citytext = (TextView) convertView.findViewById(R.id.citytext);
		citytext.setText(interestnamearr[position]);

		// 查询数据库，数据库中有该城市则设置勾选
		if (sba.get(position)) {
			citytext.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					R.mipmap.city_checkbox_selected, 0);
		}
		return convertView;
	}
}
