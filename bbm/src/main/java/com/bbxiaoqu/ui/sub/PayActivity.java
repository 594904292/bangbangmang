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
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.bean.InfoBase;
import com.bbxiaoqu.bean.PayBase;
import com.bbxiaoqu.comm.service.db.MessBmService;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.community.SelCommunityActivity;
import com.bbxiaoqu.ui.main.ViewActivity;
import com.bbxiaoqu.ui.sub.InfoBmActivity.MyAdapter;
import com.bbxiaoqu.ui.sub.InfoBmActivity.ViewHolder;
import com.bbxiaoqu.view.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PayActivity extends BaseActivity {
	private DemoApplication myapplication;
	ListView lstv;
	private List<Map<String, Object>> data;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	MyAdapter adapter;
	TextView title;
	TextView righgtitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		.permitAll().build();
StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.pay);
		myapplication = (DemoApplication) this.getApplication();
		initView();
		initData();
		lstv = (ListView) findViewById(R.id.lvpay);
		adapter = new MyAdapter(this.getApplicationContext());
		loadData();

	}

	private void initView() {
		title = (TextView) findViewById(R.id.title);
		righgtitle = (TextView) findViewById(R.id.right_text);
		righgtitle.setVisibility(View.VISIBLE);
	}

	private void initData() {
		title.setText("支付");
		righgtitle.setText("充值");

		righgtitle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(PayActivity.this,
						PayAddActivity.class);
				startActivity(intent);
			}
		});
	}

	private void loadData() {
		// TODO Auto-generated method stub
		if (lstv == null) {
			return;
		}
		getData();
		lstv.setAdapter(adapter);
	}

	private void getData() {
		if (!NetworkUtils.isNetConnected(myapplication)) {
			T.showShort(myapplication, "当前无网络连接,请稍后再试！");
			NetworkUtils.showNoNetWorkDlg(PayActivity.this);
			return;
		}
		String target = myapplication.getlocalhost()
				+ "getmypayinfo.php?userid=" + myapplication.getUserId();
		dataList = new ArrayList<Map<String, Object>>();
		try {
			// /////////////////////////////
			List<PayBase> bfjllist = null;
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
					bfjllist = parsejson(json);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// /////////////////////////////

			for (PayBase PayBases : bfjllist) {
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("id", String.valueOf(PayBases.getId()));
				item.put("user", String.valueOf(PayBases.getUser()));
				item.put("score", String.valueOf(PayBases.getScore()));
				item.put("money", String.valueOf(PayBases.getMoney()));
				item.put("tool", String.valueOf(PayBases.getTool()));
				item.put("date", String.valueOf(PayBases.getDate()));
				item.put("status", String.valueOf(PayBases.getStatus()));
				dataList.add(item);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static List<PayBase> parsejson(InputStream jsonStream)
			throws Exception {
		List<PayBase> list = new ArrayList<PayBase>();
		byte[] data = StreamTool.read(jsonStream);
		String json = new String(data);
		JSONArray jsonarray = new JSONArray(json);
		for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject jsonobject = jsonarray.getJSONObject(i);
			int _id = jsonobject.getInt("id");
			String _user = jsonobject.getString("user");
			String _score = jsonobject.getString("score");
			String _money = jsonobject.getString("money");
			String _tool = jsonobject.getString("tool");
			String _date = jsonobject.getString("date");
			String _status = jsonobject.getString("status");
			list.add(new PayBase(_id, _user, _score, _money, _tool, _date,
					_status));
		}
		return list;
	}

	public final class ViewHolder {

		TextView txt_date;
		TextView txt_score;
		TextView txt_tool;
		TextView txt_status;
	}

	public class MyAdapter extends BaseAdapter {
		private Context ctx;
		private LayoutInflater mInflater;

		public MyAdapter(Context context) {
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
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.pay_item_info, null);
				holder.txt_date = (TextView) convertView
						.findViewById(R.id.txt_date);
				holder.txt_tool = (TextView) convertView
						.findViewById(R.id.txt_tool);
				holder.txt_score = (TextView) convertView
						.findViewById(R.id.txt_score);
				holder.txt_status = (TextView) convertView
						.findViewById(R.id.txt_status);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.txt_date.setText(dataList.get(position).get("date")
					.toString());
			if (dataList.get(position).get("tool").toString().equals("1")) {
				holder.txt_tool.setText("交易方式:支付宝");
			} else {
				holder.txt_tool.setText("交易方式:其它");
			}
			holder.txt_score.setText(dataList.get(position).get("score")
					.toString()+"积分");
			if (dataList.get(position).get("status").toString().equals("1")) {
				holder.txt_status.setText("状态:交易失败");
			} else {
				holder.txt_status.setText("状态:交易成功");
			}
			return convertView;
		}
	}

}
