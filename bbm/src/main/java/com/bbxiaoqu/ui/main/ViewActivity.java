package com.bbxiaoqu.ui.main;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.adapter.BmUserAdapter.Callback;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.api.ApiAsyncTask.ApiRequestListener;
import com.bbxiaoqu.comm.gallery.BigImgActivity;
import com.bbxiaoqu.view.HorizontalListView;
import com.bbxiaoqu.adapter.HorizontalListViewAdapter;
import com.bbxiaoqu.comm.jsonservices.GetJson;
import com.bbxiaoqu.ui.popup.Constants.HINT;
import com.bbxiaoqu.ui.popup.DateUtils;
import com.bbxiaoqu.ui.popup.ListLazyAdapter;
import com.bbxiaoqu.ui.popup.TitlePopup;
import com.bbxiaoqu.comm.service.db.MessGzService;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.SearchActivity;
import com.bbxiaoqu.ui.sub.BmUserActivity;
import com.bbxiaoqu.ui.sub.ChattingActivity;
import com.bbxiaoqu.ui.sub.ViewUserInfoActivity;
import com.bbxiaoqu.view.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ViewActivity extends BaseActivity implements OnItemClickListener,ApiRequestListener,
		Callback {

	private static final String TAG = ViewActivity.class.getSimpleName();
	private DemoApplication myapplication;
	private String infoid = "0";
	private String senduserid = "";
	private String sendusername = "";
	private String current_login_userid = "";
	private String current_login_usernickname = "";
	private String title = "";
	private String content = "";
	private String headface = "";
	private String sendtime = "";
	private String infocatagroy = "";
	private String address = "";

	private String photo = "";
	public static final int chatflag = 1;

	// 评论列表
	//private DetailGallery myGallery;
	private AssetManager assetManager;
	private ArrayList<String> potolist = new ArrayList<String>();
	private List<Map<String, String>> discussList = new ArrayList<Map<String, String>>();
	public ImageView top_more;
	private TextView title_tv;
	private TextView right_text_tv;
	private TextView group_discuss_tip;
	private ListView listView;
	private ImageView groupPopup;
	private TitlePopup titlePopup;
	private Button group_discuss_submit;
	private RelativeLayout rl_bottom;
	private RelativeLayout gallery;
	HorizontalListView hListView;
	//private Button btn_pl;
	private Button btn_gz;
	private String discuzz_content = "";
	private String getdatamethon = "";
	private String guid = "";
	private String gzaction = "";
	private String bmaction = "";
	private JSONObject jsonobject;// 通过GUID获取的消息
	private ImageView headface_img;
	private TextView sendusertv ;
	private TextView contenttv ;
	private TextView statustv ;
	private TextView sendtimetv ;
	private TextView sendaddresstv ;
	private Button chat_btn;
	private boolean issolution = false;// 是否解决
	private String solutionid = "0";// 解决ID
	private static final int DIALOG_PROGRESS = 0;
	// 用户不存在（用户名错误）
	private static final int ERROR_CODE_USERNAME_NOT_EXIST = 211;
	// 用户密码错误
	private static final int ERROR_CODE_PASSWORD_INVALID = 212;


	private LocationClient mLocationClient;
	public Double nLatitude;
	public Double nLontitude;


	HorizontalListViewAdapter hListViewAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		myapplication = (DemoApplication) this.getApplication();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		assetManager = this.getAssets();
		initView();
		initData();
		Bundle Bundle1 = this.getIntent().getExtras();
		getdatamethon = Bundle1.getString("put");
		guid = Bundle1.getString("guid");
		infocatagroy = Bundle1.getString("infocatagroy");
		if (!NetworkUtils.isNetConnected(myapplication)) {
			T.showShort(myapplication, "当前无网络连接！");
			NetworkUtils.showNoNetWorkDlg(ViewActivity.this);
			return;
		}
		LoadData();
		LoadLbsThread m = new LoadLbsThread();
		new Thread(m).start();
	}

	private void LoadData() {
		if (!isFinishing()) {
            showDialog(DIALOG_PROGRESS);
	    } else {
	       // 如果当前页面已经关闭，不进行登录操作
	       return;
	    }
		MarketAPI.getinfo(getApplicationContext(), this, guid);
		//MarketAPI.getItemNum(getApplicationContext(), this, guid);
	}

	
	private Handler basehandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				findViewById(R.id.info_sendname).setVisibility(View.GONE);
				findViewById(R.id.info_sendtime).setVisibility(View.GONE);
				findViewById(R.id.info_sendaddress).setVisibility(View.GONE);
				findViewById(R.id.info_gzbutton1).setVisibility(View.GONE);
				findViewById(R.id.headface).setVisibility(View.GONE);
				findViewById(R.id.view_group_discuss_tip).setVisibility(
						View.GONE);
				findViewById(R.id.view_rl_bottom).setVisibility(View.GONE);

				TextView content = (TextView) findViewById(R.id.info_content);
				content.setText("本条记录有不良、敏感信息,管理员已删除....");
				T.showShort(ViewActivity.this, "数据服务端已删除");
				break;
			case 2:
				Bundle data = msg.getData();
				String error = data.getString("error");
				T.showShort(ViewActivity.this, "网络错误:" + error);
				break;
			default:
				break;
			}
		};
	};



	/**/

	void init() {
		//GalleryAdapter adapter = new GalleryAdapter(potolist,getApplicationContext());
		//myGallery.setAdapter(adapter);


		hListViewAdapter = new HorizontalListViewAdapter(getApplicationContext(),potolist);
		hListView.setAdapter(hListViewAdapter);


	}

	void addEvn() {

		hListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ViewActivity.this,
						BigImgActivity.class);
				StringBuilder sb=new StringBuilder();
				for(int i=0;i<potolist.size();i++)
				{
					sb.append(potolist.get(i).toString());
					if(i<potolist.size()-1)
					{
						sb.append(",");
					}
				}
				intent.putExtra("imageName", position);
				intent.putExtra("imageNames", sb.toString());
				startActivity(intent);

			}
		});

	}

	Handler gzactionhandle = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String action = data.getString("action");
			if (action.equals("add")) {
				MessGzService messgzService = new MessGzService(
						ViewActivity.this);
				messgzService.addgz(guid, myapplication.getUserId());
				btn_gz.setText("取消收藏");
				btn_gz.setTag("取消收藏");
			} else if (action.equals("remove")) {
				MessGzService messgzService = new MessGzService(
						ViewActivity.this);
				messgzService.removegz(guid, myapplication.getUserId());
				btn_gz.setText("收藏");
				btn_gz.setTag("收藏");
			}
		}
	};

	private void initView() {
		title_tv = (TextView) findViewById(R.id.title);
		right_text_tv = (TextView) findViewById(R.id.right_text);
		right_text_tv.setVisibility(View.GONE);
		
		sendusertv = (TextView) findViewById(R.id.info_sendname);
		contenttv = (TextView) findViewById(R.id.info_content);
		statustv = (TextView) findViewById(R.id.info_status);
		sendtimetv = (TextView) findViewById(R.id.info_sendtime);
		sendaddresstv = (TextView) findViewById(R.id.info_sendaddress);
		
		listView = (ListView) findViewById(R.id.view_group_discuss_list);
		group_discuss_tip = (TextView) findViewById(R.id.view_group_discuss_tip);// 暂无评论
		rl_bottom = (RelativeLayout) findViewById(R.id.view_rl_bottom);// 评论布局
		//myGallery = (DetailGallery) findViewById(R.id.detail_shotcut_gallery);
		gallery = (RelativeLayout) findViewById(R.id.gallery);
		hListView = (HorizontalListView)findViewById(R.id.horizon_listview);
		//ext_mLayout = (LinearLayout) findViewById(R.id.layout_container_ext);
		headface_img = (ImageView) findViewById(R.id.headface);
		headface_img.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!NetworkUtils.isNetConnected(myapplication)) {
					//T.showShort(myapplication, "当前无网络连接！");
					NetworkUtils.showNoNetWorkDlg(ViewActivity.this);
					return;
				}
				Intent Intent1 = new Intent();
				Intent1.setClass(ViewActivity.this, ViewUserInfoActivity.class);
				Bundle arguments = new Bundle();
				arguments.putString("userid", senduserid);
				Intent1.putExtras(arguments);
				startActivity(Intent1);
			}
		});
		top_more = (ImageView) findViewById(R.id.top_more);	
		top_more.setVisibility(View.VISIBLE);
		top_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ViewActivity.this,SearchActivity.class);									
				startActivity(intent);
			}
		});

		chat_btn = (Button) findViewById(R.id.chat_btn);
		//chat.setVisibility(View.VISIBLE);//显示
		chat_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!NetworkUtils.isNetConnected(myapplication)) {
					T.showShort(myapplication, "当前无网络连接！");
					NetworkUtils.showNoNetWorkDlg(ViewActivity.this);
					return;
				}
				if (senduserid.equals(myapplication.getUserId())) {
					Intent intent = new Intent(ViewActivity.this,BmUserActivity.class);
					Bundle arguments = new Bundle();
					arguments.putString("guid", guid);
					intent.putExtras(arguments);
					startActivity(intent);
				} else {
					//开线程做会话存储,被认为是做了一次报名动作
					bmaction="add";
					new Thread(savebmThread).start();
					Intent intent = new Intent(ViewActivity.this,ChattingActivity.class);
					Bundle arguments = new Bundle();
					arguments.putString("to", senduserid);
					arguments.putString("my", myapplication.getUserId());
					intent.putExtras(arguments);
					startActivity(intent);
				}
			}
		});
		btn_gz = (Button) findViewById(R.id.info_gzbutton1);
		// btn_bm=(Button)findViewById(R.id.info__Bmbutton2);
		btn_gz.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!NetworkUtils.isNetConnected(myapplication)) {
					T.showShort(myapplication, "当前无网络连接！");
					NetworkUtils.showNoNetWorkDlg(ViewActivity.this);
					return;
				}
				String name = v.getTag().toString();
				if (name.equals("收藏")) {
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putString("action", "add");
					msg.setData(data);
					gzactionhandle.sendMessage(msg);
					gzaction = "add";
					new Thread(savegzThread).start();

				} else if (name.equals("取消收藏")) {
					Message msg = new Message();
					Bundle data = new Bundle();
					data.putString("action", "remove");
					msg.setData(data);
					gzactionhandle.sendMessage(msg);
					gzaction = "remove";
					new Thread(savegzThread).start();
				}
			}
		});

	}






	/*针对用户第一次会话做一次报名记录*/
	Runnable savebmThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (!NetworkUtils.isNetConnected(myapplication)) {
				T.showShort(myapplication, "当前无网络连接！");
				NetworkUtils.showNoNetWorkDlg(ViewActivity.this);
				return;
			}
			int result;
			String target = myapplication.getlocalhost()+"adduserbminfo.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("_userid", myapplication.getUserId()));// 本人
			paramsList.add(new BasicNameValuePair("_infoid", infoid));// 本人
			paramsList.add(new BasicNameValuePair("_guid", guid));// 本人
			paramsList.add(new BasicNameValuePair("_action", bmaction));// 本人

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
	private void initData() {
		title_tv.setText("查看");
		right_text_tv.setText("");
	}

	Runnable loaddiscuzzThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			loaddiscuzzBody();
		}

	};

	private void loaddiscuzzBody() {
		if (!NetworkUtils.isNetConnected(myapplication)) {
			T.showShort(myapplication, "当前无网络连接！");
			NetworkUtils.showNoNetWorkDlg(ViewActivity.this);
			return;
		}
		String target = myapplication.getlocalhost()+ "/getdiscuzz.php?infoid=" + this.infoid;
		try {
			String json = GetJson.GetJson(target);
			Message msg = new Message();
			Bundle bundledata = new Bundle();
			bundledata.putString("json", json);
			msg.setData(bundledata);
			discuzzhhandler.sendMessage(msg);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

	Handler discuzzhhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String json = data.getString("json");
			try {
				discuzzhandle(json);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	private void discuzzhandle(String json) throws JSONException {
		JSONArray jsonarray = new JSONArray(json);
		for (int i = 0; i < jsonarray.length(); i++) {
			JSONObject jsonobject = jsonarray.getJSONObject(i);
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", jsonobject.getString("id").toString());

			map.put("headface", jsonobject.getString("headface").toString());
			// 给map设置要显示的值
			map.put("distime", jsonobject.getString("sendtime").toString());
			map.put("content", jsonobject.getString("message").toString());

			// 设置父贴的发帖人信息
			map.put("puid", jsonobject.getString("touserid").toString());
			map.put("pname", jsonobject.getString("touser").toString());

			// 设置自己的信息
			map.put("uid", jsonobject.getString("senduserid").toString());
			map.put("username", jsonobject.getString("senduser").toString());
			discussList.add(map);
		}
		showhiddendiscuss();
	}

	private void showhiddendiscuss() {
		if (discussList.size() == 0) {
			group_discuss_tip.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
		} else {
			group_discuss_tip.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			ListLazyAdapter adapter = new ListLazyAdapter(this, discussList,
					this, senduserid, this.issolution, this.solutionid);
			listView.setAdapter(adapter);
			new Utility().setListViewHeightBasedOnChildren(listView);
		}
	}

	public void discussSubmit(View view) {

		EditText discussContent = (EditText) findViewById(R.id.view_group_discuss);
		discuzz_content = discussContent.getText().toString();
		discussContent.setText("");
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); // 隐软键盘
		// 判断content,不能为null或者特定字符
		if (discuzz_content == null || "".equals(discuzz_content)
				|| HINT.DISCUSSION.equals(discuzz_content)) {
			this.alertDialog(ViewActivity.this, "Error", "请输入回复内容! ");
			Log.e(TAG, "discuss content is null ! ");

			return;
		}
		if (!NetworkUtils.isNetConnected(ViewActivity.this)) {
			T.showShort(ViewActivity.this, "当前无网络连接！");
			NetworkUtils.showNoNetWorkDlg(ViewActivity.this);
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		// 给map设置要显示的值
		UserService uService = new UserService(ViewActivity.this);
		String headfaceurl = uService.getheadface(current_login_userid);

		map.put("headface", headfaceurl);
		map.put("distime", DateUtils.formaterDate2YMDHm(new Date(System
				.currentTimeMillis())));
		map.put("content", discuzz_content);
		// 设置父贴的发帖人信息
		map.put("puid", senduserid);
		map.put("pname", sendusername);

		// 设置自己的信息
		map.put("uid", current_login_userid);
		map.put("username", current_login_usernickname);
		discussList.add(map);

		showhiddendiscuss();
		new Thread(savediscussThread).start();
		//关联关系
		bmaction="add";
		new Thread(savebmThread).start();
	}

	private void alertDialog(Context context, String title, String message) {
		new AlertDialog.Builder(context)
				.setIcon(getResources().getDrawable(R.mipmap.no_image))
				.setTitle(title).setMessage(message).create().show();
	}

	Runnable savediscussThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 30000);
			HttpConnectionParams.setSoTimeout(httpParameters, 30000);

			int result;
			String target = myapplication.getlocalhost() + "discuzz.php";
			HttpPost httprequest = new HttpPost(target);
			httprequest.setParams(httpParameters);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());
			paramsList.add(new BasicNameValuePair("_infoid", infoid));// 本人
			paramsList.add(new BasicNameValuePair("_sendtime", date));// 本人
			paramsList.add(new BasicNameValuePair("_puserid", senduserid));// 本人
			paramsList.add(new BasicNameValuePair("_puser", sendusername));// 本人
			paramsList.add(new BasicNameValuePair("_touserid",current_login_userid));// 发帐人
			paramsList.add(new BasicNameValuePair("_touser",current_login_usernickname));// 发帐人
			paramsList.add(new BasicNameValuePair("_message", discuzz_content));// 公司代号
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String json = EntityUtils
							.toString(httpResponse.getEntity());
					System.out.println(json);
					result = 1;
				} else {
					result = 0;
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("result", result);
				msg.setData(data);
				savehandler.sendMessage(msg);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	Runnable savegzThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost() + "addusergzinfo.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("_userid", myapplication.getUserId()));// 本人
			paramsList.add(new BasicNameValuePair("_infoid", infoid));// 本人
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

	Handler savehandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			int result = data.getInt("result");
			Log.i("mylog", "请求结果-->" + result);
			if (result == 1) {
				Toast.makeText(ViewActivity.this, "评论发表成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(ViewActivity.this, "评论发表失败", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	String pos = "";

	@Override
	public void click(View v) {
		// TODO Auto-generated method stub
		// Toast.makeText(this,
		// v.getTag().toString(),Toast.LENGTH_SHORT).show();
		String tag = v.getTag().toString();
		String cat = tag.split("_")[0];
		pos = tag.split("_")[1];
		if (cat.startsWith("good")) {
			new Thread(solutionThread).start();
		} else if (cat.startsWith("head")) {// 聊天
			Map<String, String> map = discussList.get(Integer.parseInt(pos));
			String itemuid = map.get("uid").toString();
			if (itemuid.equals(myapplication.getUserId())) {
				Toast.makeText(ViewActivity.this, "不能与本人聊天", Toast.LENGTH_SHORT).show();
			} else {
				// 获取头像的id
				/*Intent intent = new Intent(ViewActivity.this,
						ChattingActivity.class);
				Bundle arguments = new Bundle();
				arguments.putString("guid", guid);
				arguments.putString("to", itemuid);
				arguments.putString("my", myapplication.getUserId());
				intent.putExtras(arguments);
				startActivity(intent);*/

				Intent Intent1 = new Intent();
				Intent1.setClass(ViewActivity.this, ViewUserInfoActivity.class);
				Bundle arguments = new Bundle();
				arguments.putString("userid",itemuid);
				Intent1.putExtras(arguments);
				startActivity(Intent1);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		// TODO Auto-generated method stub
		// Toast.makeText(this, "listview的item被点击了！，点击的位置是-->" +
		// position,Toast.LENGTH_SHORT).show();

	}

	Runnable solutionThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost() + "solution.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();

			paramsList.add(new BasicNameValuePair("_infoid", infoid));// 信息id
			paramsList.add(new BasicNameValuePair("_guid", guid));// 信息唯一标识
			Map<String, String> map = discussList.get(Integer.parseInt(pos));

			paramsList.add(new BasicNameValuePair("_solutiontype", "1"));// 留言
			paramsList.add(new BasicNameValuePair("_solutionpostion", map.get(
					"id").toString()));// 留言项
			paramsList.add(new BasicNameValuePair("_solutionuserid", map.get(
					"uid").toString()));// 留言人

			SimpleDateFormat sDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String solutiondate = sDateFormat.format(new java.util.Date());

			paramsList
					.add(new BasicNameValuePair("_solutiontime", solutiondate));// 本人

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
				freshhandler.sendMessage(msg);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	};

	Handler freshhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			int result = data.getInt("result");
			Log.i("mylog", "请求结果-->" + result);
			if (result == 1) {
				discussList.clear();
				MarketAPI.getinfo(getApplicationContext(), ViewActivity.this, guid);
			}
		}
	};

	@Override
	public void onSuccess(int method, Object obj) {
		// TODO Auto-generated method stub
		switch (method) {
        case MarketAPI.ACTION_GETINFO:
        	 try{
                 dismissDialog(DIALOG_PROGRESS);
             }catch (IllegalArgumentException e) {
             }
            HashMap<String, String> result = (HashMap<String, String>) obj;
            String JsonContext=result.get("guidinfo");           
            if (JsonContext == null || JsonContext.equals("")
					|| JsonContext.toString().length() == 0) {
				Message msg = basehandler.obtainMessage();
				msg.what = 1;
				basehandler.sendMessage(msg);
				return;
			} else {
				if (JsonContext.startsWith("Error:")) {
					Message msg = basehandler.obtainMessage();
					msg.what = 2;
					Bundle data = new Bundle();
					data.putString("error", "json");
					msg.setData(data);
					basehandler.sendMessage(msg);
					return;
				}
				JSONArray jsonarray;

				try {
					jsonarray = new JSONArray(JsonContext);
					jsonobject = jsonarray.getJSONObject(0);
					infoid = jsonobject.getString("id");
					title = jsonobject.getString("title");
					content = jsonobject.getString("content");
					headface = jsonobject.getString("headface");
					sendtime = jsonobject.getString("sendtime");
					infocatagroy = jsonobject.getString("infocatagroy");
					address = jsonobject.getString("address");
					photo = jsonobject.getString("photo");
					current_login_userid = myapplication.getUserId();
					current_login_usernickname = myapplication.getNickname();
					senduserid = jsonobject.getString("senduser");
					if (senduserid.equals(myapplication.getUserId())) {
						//chat.setVisibility(View.GONE);//隐藏
						//chat.setVisibility(View.VISIBLE);//显示
						chat_btn.setText("评价");
					}else
					{
						//chat_btn.setVisibility(View.VISIBLE);//显示
					}
					sendusername = jsonobject.getString("username");
					if (jsonobject.getString("issolution").equals("1")) {
						issolution = true;
					} else {
						issolution = false;
					}
					solutionid = jsonobject.getString("solutionid");
					if (sendusername.equals("")) {
						sendusername = "匿名";
					}
					sendusertv.setText(sendusername);
					contenttv.setText(content);
					sendtimetv.setText(sendtime);
					sendaddresstv.setText(address);
					if (issolution) {
						statustv.setText("已解决");
						statustv.setTextColor(Color.GREEN);
					} else {
						statustv.setText("未解决");
						statustv.setTextColor(Color.RED);
					}
					if (headface.length() > 0) {
						String headfaceurl = myapplication.getlocalhost() + "/uploads/"+headface;
						ImageLoader.getInstance().displayImage(headfaceurl,headface_img, ImageOptions.getOptions());
					}
					if (photo.indexOf(",") > 0) {
						for (int i = 0; i < photo.split(",").length; i++) {
							String fileName = myapplication.getlocalhost()+"uploads/"
									+ photo.split(",")[i];
							potolist.add(fileName);
						}
					} else {
						if (photo.length() > 0) {
							String fileName = myapplication.getlocalhost()+"uploads/"
									+ photo;
							potolist.add(fileName);
						}
					}
					if (potolist.size() > 0) {
						gallery.setVisibility(View.VISIBLE);
						init();
						addEvn();
					} else {
						gallery.setVisibility(View.GONE);
					}
					//init_extui();// 加载扩展信息
					if (senduserid.equals(myapplication.getUserId())) {
						btn_gz.setVisibility(View.GONE);
					} else {
						btn_gz.setVisibility(View.VISIBLE);
						MessGzService messgzService = new MessGzService(
								ViewActivity.this);
						boolean ishavezhan = messgzService.isexit(guid,
								myapplication.getUserId());
						if (ishavezhan) {
							btn_gz.setText("取消收藏");
							btn_gz.setTag("取消收藏");
						} else {
							btn_gz.setText("收藏");
							btn_gz.setTag("收藏");
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}								
				new Thread(loaddiscuzzThread).start();
			}
            break;
        default:
            break;
        }
	}

	@Override
	public void onError(int method, int statusCode) {
		// TODO Auto-generated method stub
		 switch (method) {
	        case MarketAPI.ACTION_GETINFO:            
	            // 隐藏登录框
	            try{
	                dismissDialog(DIALOG_PROGRESS);
	            }catch (IllegalArgumentException e) {
	            }            		            
	            break;
	        default:
	            break;
	        }
	}


	private void initlsb() {
		// TODO Auto-generated method stub
		mLocationClient = new LocationClient(this.myapplication);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000*20;

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
				Log.i("mylog", nLatitude+"-" + nLontitude);
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

	Handler handler = new Handler(){
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
