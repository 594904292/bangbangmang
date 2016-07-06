package com.bbxiaoqu.ui.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.adapter.FwListViewAdapter;
import com.bbxiaoqu.adapter.ListViewAdapter;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.api.ApiAsyncTask.ApiRequestListener;
import com.bbxiaoqu.bean.BbMessage;
import com.bbxiaoqu.bean.InfoBase;
import com.bbxiaoqu.client.baidu.Utils;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver.onMessageReadListener;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver.onNewMessageListener;
import com.bbxiaoqu.comm.service.db.DatabaseHelper;
import com.bbxiaoqu.comm.service.db.MessageDB;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.L;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.PublishActivity;
import com.bbxiaoqu.ui.SearchActivity;
import com.bbxiaoqu.view.BaseActivity;
import com.bbxiaoqu.widget.AutoListView;
import com.bbxiaoqu.widget.AutoListView.OnLoadListener;
import com.bbxiaoqu.widget.AutoListView.OnRefreshListener;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class NearActivity extends BaseActivity implements OnRefreshListener, OnLoadListener, OnClickListener, ApiRequestListener {
	TextView title;
	TextView right_text;
	public ImageView top_more;
	public ImageView top_add;
	private AutoListView lstv;
	private BaseAdapter adapter;
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	private DatabaseHelper dbHelper;
	private View allLayout;
	private View mySosLayout;
	private ImageView allImage;
	private ImageView mySosImage;
	private TextView allText;
	private TextView mySosText;
	private View sosLayout;
	private ImageView sosImage;
	private TextView sosText;
	/*private View serviceLayout;
	private ImageView serviceImage;
	private TextView serviceText;*/
	private int current_sel = 0;
	private DemoApplication myapplication;
	private static final int DIALOG_PROGRESS = 0;
	//用户不存在（用户名错误）
	private static final int ERROR_CODE_USERNAME_NOT_EXIST = 211;
	//用户密码错误
	private static final int ERROR_CODE_PASSWORD_INVALID = 212;


	private LocationClient mLocationClient;
	public Double nLatitude;
	public Double nLontitude;
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_near);
		myapplication = (DemoApplication) this.getApplication();
		initView();
		initData();
		LoadLbsThread m = new LoadLbsThread();
		new Thread(m).start();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.

	}

	int action = 0;

	private void loadData(final int what) {
		if (!isFinishing()) {
			showDialog(DIALOG_PROGRESS);
		} else {// 如果当前页面已经关闭，不进行登录操作
			return;
		}
		if (what == AutoListView.REFRESH) {
			action = AutoListView.REFRESH;
			int start = 0;
			int limit = 10;
			if (current_sel == 0) {
				if (myapplication.getLat() != null && myapplication.getLng() != null)
					MarketAPI.getINfos(getApplicationContext(), this, myapplication.getUserId(), myapplication.getLat(), myapplication.getLng(), "xiaoqu", "0", start, limit);
			} else if (current_sel == 1) {
				if (myapplication.getLat() != null && myapplication.getLng() != null)
					MarketAPI.getINfos(getApplicationContext(), this, myapplication.getUserId(), myapplication.getLat(), myapplication.getLng(), "xiaoqu", "1", start, limit);
			} else if (current_sel == 2) {
				if (myapplication.getLat() != null && myapplication.getLng() != null)
					MarketAPI.getINfos(getApplicationContext(), this, myapplication.getUserId(), myapplication.getLat(), myapplication.getLng(), "self", "1", start, limit);
			}/*else if(current_sel==3)
			{
				MarketAPI.getFwINfos(getApplicationContext(), this, myapplication.getUserId(), myapplication.getLat(),myapplication.getLng(),"xiaoqufw", "1", start, limit);
			}*/
		} else {
			action = AutoListView.LOAD;
			int start = dataList.size();
			int limit = 10;
			if (current_sel == 0) {
				if (myapplication.getLat() != null && myapplication.getLng() != null)
					MarketAPI.getINfos(getApplicationContext(), this, myapplication.getUserId(), myapplication.getLat(), myapplication.getLng(), "xiaoqu", "0", start, limit);
			} else if (current_sel == 1) {
				if (myapplication.getLat() != null && myapplication.getLng() != null)
					MarketAPI.getINfos(getApplicationContext(), this, myapplication.getUserId(), myapplication.getLat(), myapplication.getLng(), "xiaoqu", "1", start, limit);
			} else if (current_sel == 2) {
				if (myapplication.getLat() != null && myapplication.getLng() != null)
					MarketAPI.getINfos(getApplicationContext(), this, myapplication.getUserId(), myapplication.getLat(), myapplication.getLng(), "self", "1", start, limit);
			}/*else if(current_sel==3)
			{
				MarketAPI.getFwINfos(getApplicationContext(), this, myapplication.getUserId(), myapplication.getLat(), myapplication.getLng(), "xiaoqufw", "1", start, limit);
			}*/
		}
	}

	private void clearSelection() {
		allImage.setImageResource(R.mipmap.t12);
		allText.setTextColor(Color.GRAY);
		allText.setTextSize(14);
		/*TextPaint alltp = allText.getPaint();
		alltp.setFakeBoldText(false);*/

		mySosImage.setImageResource(R.mipmap.t22);
		mySosText.setTextColor(Color.GRAY);
		mySosText.setTextSize(14);
		/*TextPaint mySostp = mySosText.getPaint();
		mySostp.setFakeBoldText(false);*/

		sosImage.setImageResource(R.mipmap.t32);
		sosText.setTextColor(Color.GRAY);
		sosText.setTextSize(14);
		/*TextPaint sostp = sosText.getPaint();
		sostp.setFakeBoldText(false);*/

	}

	private void setTabSelection(int index) {
		clearSelection();
		dataList.clear();
		switch (index) {
			case 0:
				// 当点击了消息tab时，改变控件的图片和文字颜色
				adapter = new ListViewAdapter(NearActivity.this, dataList);
				lstv.setAdapter(adapter);
				lstv.setOnRefreshListener(this);
				lstv.setOnLoadListener(this);
				allImage.setImageResource(R.mipmap.t1);
				allText.setTextColor(Color.GRAY);

				loadData(AutoListView.REFRESH);

				break;
			case 1:
				// 当点击了联系人tab时，改变控件的图片和文字颜色
				adapter = new ListViewAdapter(NearActivity.this, dataList);
				lstv.setAdapter(adapter);
				lstv.setOnRefreshListener(this);
				lstv.setOnLoadListener(this);
				mySosImage.setImageResource(R.mipmap.t2);
				mySosText.setTextColor(Color.GRAY);

				loadData(AutoListView.REFRESH);

				break;
			case 2:
				// 当点击了动态tab时，改变控件的图片和文字颜色
				adapter = new ListViewAdapter(NearActivity.this, dataList);
				lstv.setAdapter(adapter);
				lstv.setOnRefreshListener(this);
				lstv.setOnLoadListener(this);
				sosImage.setImageResource(R.mipmap.t3);
				sosText.setTextColor(Color.GRAY);
 				loadData(AutoListView.REFRESH);

				break;

		}
	}
	private String[] PK = new String[] { "删除" };
	String sel_guid="";
	private void initView() {
		title = (TextView) findViewById(R.id.title);
		right_text = (TextView) findViewById(R.id.right_text);
		right_text.setVisibility(View.VISIBLE);
		right_text.setClickable(true);
		top_more = (ImageView) findViewById(R.id.top_more);
		top_more.setVisibility(View.VISIBLE);
		top_add = (ImageView) findViewById(R.id.top_add);
		top_add.setVisibility(View.VISIBLE);
		allLayout = findViewById(R.id.all_layout);
		mySosLayout = findViewById(R.id.mysos_layout);
		sosLayout = findViewById(R.id.sos_layout);
		/*serviceLayout = findViewById(R.id.service_layout);*/
		allImage = (ImageView) findViewById(R.id.all_image);
		mySosImage = (ImageView) findViewById(R.id.mysos_image);
		sosImage = (ImageView) findViewById(R.id.sos_image);
		/*serviceImage = (ImageView) findViewById(R.id.service_image);*/
		allText = (TextView) findViewById(R.id.all_text);
		allText.setTextSize(14);
		mySosText = (TextView) findViewById(R.id.mysos_text);
		mySosText.setTextSize(14);
		sosText = (TextView) findViewById(R.id.sos_text);
		sosText.setTextSize(14);
		/*serviceText = (TextView) findViewById(R.id.service_text);*/
		allLayout.setOnClickListener(this);
		mySosLayout.setOnClickListener(this);
		sosLayout.setOnClickListener(this);
		//serviceLayout.setOnClickListener(this);
		lstv = (AutoListView) findViewById(R.id.lstv);

		lstv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			 @Override
			 public boolean onItemLongClick(AdapterView<?> arg0, View arg1,final int location, long arg3)
			 {
				if(dataList.get(location-1).get("senduserid")!=null&&dataList.get(location-1).get("senduserid").toString().equals(myapplication.getUserId())) {
					new AlertDialog.Builder(NearActivity.this)
							.setTitle("操作")
							.setIcon(android.R.drawable.ic_dialog_info)
							//.setSingleChoiceItems(new String[] {"choice 1","choice 2","choice 3","choice 4"}, 0, new DialogInterface.OnClickListener() {
							.setItems(PK, new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									if (PK[which].equals("删除")) {
										sel_guid = dataList.get(location - 1).get("guid").toString();
										System.out.println(sel_guid);
										new Thread(DelItem).start();

									}
								}
							})
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
								}
							}).show();

				}
				 return true;
			 }
		});

		lstv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int location, long arg3) {
				if (!NetworkUtils.isNetConnected(myapplication)) {
					T.showShort(myapplication, "当前无网络连接！");
					NetworkUtils.showNoNetWorkDlg(NearActivity.this);
					return;
				}

				Intent Intent1 = new Intent();
				Intent1.setClass(NearActivity.this, ViewActivity.class);
				Bundle arguments = new Bundle();
				arguments.putString("put", "false");
				arguments.putString("guid", dataList.get(location - 1).get("guid").toString());
				Intent1.putExtras(arguments);
				startActivity(Intent1);
			}
		});
		top_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(NearActivity.this, SearchActivity.class);
				startActivity(intent);
			}
		});
		top_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub


				Intent intent = new Intent(NearActivity.this, PublishActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("infocatagroy", 0);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});


	}
	Runnable DelItem = new Runnable() {
		@Override
		public void run() {
			if (!NetworkUtils.isNetConnected(myapplication)) {
				T.showShort(myapplication, "当前无网络连接！");
				NetworkUtils.showNoNetWorkDlg(NearActivity.this);
				return;
			}
			int result;
			String target = myapplication.getlocalhost()+"delinfo.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();

			paramsList.add(new BasicNameValuePair("_guid", sel_guid));// 本人
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
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("result", result);
				msg.setData(data);
				msg.what=1;
				Noticehandler.sendMessage(msg);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	Handler Noticehandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==1) {
				Bundle data = msg.getData();
				int result = data.getInt("result");
				Log.i("mylog", "请求结果-->" + result);
				if (result == 1) {
					T.showShort(myapplication, "删除成功！");
					setTabSelection(current_sel);
				}else
				{
					T.showShort(myapplication, "删除失败！");
				}
			}
		}
	};
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		//info.id得到listview中选择的条目绑定的id
		String id = String.valueOf(info.id);
		switch (item.getItemId()) {
			case 0:
				//updateDialog(id);  //更新事件的方法
				return true;
			case 1:
				//System.out.println("删除"+info.id);
				//deleteData(db,id);  //删除事件的方法
				//showlist();
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	private void initData() {
		title.setText("我能帮");
		right_text.setText("");
		current_sel = 0;
		setTabSelection(current_sel);
		dbHelper = new DatabaseHelper(NearActivity.this);
		myapplication = (DemoApplication) this.getApplication();
	}


	public void doBack(View view) {
		onBackPressed();
	}

	@Override
	public void onRefresh() {
		loadData(AutoListView.REFRESH);
	}

	@Override
	public void onLoad() {
		loadData(AutoListView.LOAD);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.all_layout:
				current_sel = 0;
				setTabSelection(current_sel);
				break;
			case R.id.mysos_layout:
				current_sel = 1;
				setTabSelection(current_sel);
				break;
			case R.id.sos_layout:
				current_sel = 2;
				setTabSelection(current_sel);
				break;
			default:
				break;
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_PROGRESS:
				ProgressDialog mProgressDialog = new ProgressDialog(this);
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				mProgressDialog.setMessage(getString(R.string.init_data));
				return mProgressDialog;
			default:
				return super.onCreateDialog(id);
		}
	}

	@Override
	public void onSuccess(int method, Object obj) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> smalldataList = new ArrayList<Map<String, Object>>();
		HashMap<String, String> result = (HashMap<String, String>) obj;
		String json = result.get("infos");
		switch (method) {
			case MarketAPI.ACTION_GETINFOS:
				if (json.length() > 0) {
					JSONArray jsonarray = null;
					try {
						jsonarray = new JSONArray(json);
						for (int i = 0; i < jsonarray.length(); i++) {
							JSONObject customJson = jsonarray.getJSONObject(i);
							HashMap<String, Object> item = new HashMap<String, Object>();
							double len = getDistance(
									Double.parseDouble(myapplication.getLat()),
									Double.parseDouble(myapplication.getLng()),
									Double.parseDouble(customJson.getString("lat").toString()),
									Double.parseDouble(customJson.getString("lng").toString())
							);
							String len1 = "";
							if (len > 1000) {
								len1 = String.valueOf(Math.round(len / 100d) / 10d) + "千米";
							} else {
								len1 = String.valueOf(len) + "米";
							}
							item.put("senduserid", String.valueOf(customJson.getString("senduser").toString()));
							item.put("sendnickname", String.valueOf(customJson.getString("username").toString()));
							item.put("community", String.valueOf(customJson.getString("community").toString()));
							//item.put("address", String.valueOf(customJson.getString("address").toString()));
							item.put("address", String.valueOf(len1));
							item.put("lng", String.valueOf(customJson.getString("lng").toString()));
							item.put("lat", String.valueOf(customJson.getString("lat").toString()));
							item.put("guid", String.valueOf(customJson.getString("guid").toString()));
							item.put("infocatagroy", String.valueOf(customJson.getString("infocatagroy").toString()));
							String content = customJson.getString("content").toString();
							if (content.length() > 80) {
								content = content.substring(0, 80) + "...";
							}
							item.put("content", content);
							item.put("icon", String.valueOf(customJson.getString("photo").toString()));
							item.put("date", String.valueOf(customJson.getString("sendtime").toString()));
							item.put("status", String.valueOf(customJson.getString("status").toString()));
							item.put("visit", String.valueOf(customJson.getString("visit").toString()));
							item.put("tag1", "访客数:" + String.valueOf(customJson.getString("visit").toString()));
							item.put("tag2", "评论数:" + String.valueOf(customJson.getString("plnum").toString()));
							smalldataList.add(item);
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if (action == AutoListView.REFRESH) {
					lstv.onRefreshComplete();
					dataList.clear();
					dataList.addAll(smalldataList);
				} else if (action == AutoListView.LOAD) {
					lstv.onLoadComplete();
					dataList.addAll(smalldataList);
				}
				lstv.setResultSize(dataList.size());
				adapter.notifyDataSetChanged();
				break;
			case MarketAPI.ACTION_GETFWINFOS:
				if (json.length() > 0) {
					JSONArray jsonarray = null;
					try {
						jsonarray = new JSONArray(json);
						for (int i = 0; i < jsonarray.length(); i++) {
							JSONObject customJson = jsonarray.getJSONObject(i);
							HashMap<String, Object> item = new HashMap<String, Object>();
							double len = getDistance(
									Double.parseDouble(myapplication.getLat()),
									Double.parseDouble(myapplication.getLng()),
									Double.parseDouble(customJson.getString("lat").toString()),
									Double.parseDouble(customJson.getString("lng").toString())
							);
							String len1 = "";
							if (len > 1000) {
								len1 = String.valueOf(Math.round(len / 100d) / 10d) + "千米";
							} else {
								len1 = String.valueOf(len) + "米";
							}
							item.put("senduserid", String.valueOf(customJson.getString("senduser").toString()));
							item.put("sendnickname", String.valueOf(customJson.getString("username").toString()));
							item.put("community", String.valueOf(customJson.getString("community").toString()));
							//item.put("address", String.valueOf(customJson.getString("address").toString()));
							item.put("address", String.valueOf(len1));
							item.put("lng", String.valueOf(customJson.getString("lng").toString()));
							item.put("lat", String.valueOf(customJson.getString("lat").toString()));
							item.put("guid", String.valueOf(customJson.getString("guid").toString()));
							item.put("infocatagroy", String.valueOf(customJson.getString("infocatagroy").toString()));
							item.put("title", String.valueOf(customJson.getString("title").toString()));
							String content = customJson.getString("content").toString();
							if (content.length() > 80) {
								content = content.substring(0, 80) + "...";
							}
							item.put("content", content);
							item.put("icon", String.valueOf(customJson.getString("photo").toString()));
							item.put("date", String.valueOf(customJson.getString("sendtime").toString()));
							item.put("status", String.valueOf(customJson.getString("status").toString()));
							item.put("visit", String.valueOf(customJson.getString("visit").toString()));
							item.put("tag1", String.valueOf(customJson.getString("visit").toString()));
							item.put("tag2", "评论数:" + String.valueOf(customJson.getString("plnum").toString()));
							item.put("headface", String.valueOf(customJson.getString("headface").toString()));
							item.put("telphone", String.valueOf(customJson.getString("telphone").toString()));
							item.put("zannum", String.valueOf(customJson.getString("zannum").toString()));
							item.put("tags", String.valueOf(customJson.getString("tags").toString()));
							smalldataList.add(item);
						}
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if (action == AutoListView.REFRESH) {
					lstv.onRefreshComplete();
					dataList.clear();
					dataList.addAll(smalldataList);
				} else if (action == AutoListView.LOAD) {
					lstv.onLoadComplete();
					dataList.addAll(smalldataList);
				}
				lstv.setResultSize(dataList.size());
				adapter.notifyDataSetChanged();
				break;

			default:
				break;
		}
		try {
			dismissDialog(DIALOG_PROGRESS);
		} catch (IllegalArgumentException e) {
		}
	}

	@Override
	public void onError(int method, int statusCode) {
		// TODO Auto-generated method stub
		switch (method) {
			case MarketAPI.ACTION_GETINFOS:
				// 隐藏登录框
				try {
					dismissDialog(DIALOG_PROGRESS);
				} catch (IllegalArgumentException e) {
				}
				break;
			default:
				break;
		}
	}


	public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 6367000; //approximate radius of earth in meters
		/*
		Convert these degrees to radians
		to work with the formula
		*/

		lat1 = (lat1 * Math.PI) / 180;
		lng1 = (lng1 * Math.PI) / 180;

		lat2 = (lat2 * Math.PI) / 180;
		lng2 = (lng2 * Math.PI) / 180;

		/*
		Using the
		Haversine formula
		http://en.wikipedia.org/wiki/Haversine_formula
		calculate the distance
		*/
		double calcLongitude = lng2 - lng1;
		double calcLatitude = lat2 - lat1;
		double stepOne = Math.pow(Math.sin(calcLatitude / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(calcLongitude / 2), 2);

		double stepTwo = 2 * Math.asin(Math.min(1, Math.sqrt(stepOne)));
		double calculatedDistance = earthRadius * stepTwo;

		return Math.round(calculatedDistance); //四舍五入
	}


	private void initlsb() {
		// TODO Auto-generated method stub
		mLocationClient = new LocationClient(this.myapplication);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000 * 20;

		option.setScanSpan(span);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);

		mLocationClient.registerLocationListener(new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				if (location == null) {
					//Log.v(TAG, "HomeActivity location empty");
					return;
				}
				nLatitude = location.getLatitude();
				nLontitude = location.getLongitude();
				Log.i("mylog", nLatitude + "-" + nLontitude);
				myapplication.setLat(String.valueOf(nLatitude));
				myapplication.setLng(String.valueOf(nLontitude));
				myapplication.updatelocation();
				mLocationClient.stop();
			}
		});
		mLocationClient.start();
		mLocationClient.requestLocation();
	}



	class LoadLbsThread implements Runnable {
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					initlsb();
					break;
			}
			super.handleMessage(msg);
		}

	};


}
