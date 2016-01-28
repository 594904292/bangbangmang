package com.bbxiaoqu.ui.main;

import android.app.AlertDialog;
import android.content.Context;
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

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.adapter.BmUserAdapter.Callback;
import com.bbxiaoqu.api.ApiAsyncTask.ApiRequestListener;
import com.bbxiaoqu.api.MarketAPI;
import com.bbxiaoqu.comm.gallery.BigImgActivity;
import com.bbxiaoqu.comm.gallery.DetailGallery;
import com.bbxiaoqu.comm.jsonservices.GetJson;
import com.bbxiaoqu.comm.service.db.MessGzService;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.SearchActivity;
import com.bbxiaoqu.ui.popup.Constants.HINT;
import com.bbxiaoqu.ui.popup.DateUtils;
import com.bbxiaoqu.ui.popup.ListLazyAdapter;
import com.bbxiaoqu.ui.popup.TitlePopup;
import com.bbxiaoqu.ui.sub.ChattingActivity;
import com.bbxiaoqu.ui.sub.GalleryAdapter;
import com.bbxiaoqu.ui.sub.ViewUserInfoActivity;
import com.bbxiaoqu.view.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewFwActivity extends BaseActivity implements OnItemClickListener,ApiRequestListener,
		Callback {

	private static final String TAG = ViewFwActivity.class.getSimpleName();
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
	private DetailGallery myGallery;
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

	private TextView sendtimetv ;
	private TextView sendaddresstv ;
	private Button chat;
	private boolean issolution = false;// 是否解决
	private String solutionid = "0";// 解决ID
	private static final int DIALOG_PROGRESS = 0;
	// 用户不存在（用户名错误）
	private static final int ERROR_CODE_USERNAME_NOT_EXIST = 211;
	// 用户密码错误
	private static final int ERROR_CODE_PASSWORD_INVALID = 212;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_fw);
		myapplication = (DemoApplication) this.getApplication();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		assetManager = this.getAssets();
		initView();
		initData();
		Bundle Bundle1 = this.getIntent().getExtras();
		getdatamethon = Bundle1.getString("put");
		guid = Bundle1.getString("guid");
		infocatagroy = Bundle1.getString("infocatagroy");
		LoadData();
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
				T.showShort(ViewFwActivity.this, "数据服务端已删除");
				break;
			case 2:
				Bundle data = msg.getData();
				String error = data.getString("error");
				T.showShort(ViewFwActivity.this, "网络错误:" + error);
				break;
			default:
				break;
			}
		};
	};



	/**/

	void init() {
		GalleryAdapter adapter = new GalleryAdapter(potolist,getApplicationContext());
		myGallery.setAdapter(adapter);
	}

	void addEvn() {
		myGallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(ViewFwActivity.this,
						BigImgActivity.class);
				intent.putExtra("imageName", arg1.getTag().toString());
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
						ViewFwActivity.this);
				messgzService.addgz(guid, myapplication.getUserId());
				btn_gz.setText("取消收藏");
				btn_gz.setTag("取消收藏");
			} else if (action.equals("remove")) {
				MessGzService messgzService = new MessGzService(
						ViewFwActivity.this);
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
		sendtimetv = (TextView) findViewById(R.id.info_sendtime);
		sendaddresstv = (TextView) findViewById(R.id.info_sendaddress);

		listView = (ListView) findViewById(R.id.view_group_discuss_list);
		group_discuss_tip = (TextView) findViewById(R.id.view_group_discuss_tip);// 暂无评论
		rl_bottom = (RelativeLayout) findViewById(R.id.view_rl_bottom);// 评论布局
		myGallery = (DetailGallery) findViewById(R.id.detail_shotcut_gallery);
		gallery = (RelativeLayout) findViewById(R.id.gallery);
		//ext_mLayout = (LinearLayout) findViewById(R.id.layout_container_ext);
		headface_img = (ImageView) findViewById(R.id.headface);
		headface_img.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent Intent1 = new Intent();
				Intent1.setClass(ViewFwActivity.this, ViewUserInfoActivity.class);
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
				Intent intent=new Intent(ViewFwActivity.this,SearchActivity.class);
				startActivity(intent);


			}
		});

		chat = (Button) findViewById(R.id.chat);
		chat.setVisibility(View.GONE);
		chat.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (senduserid.equals(myapplication.getUserId())) {
					Toast.makeText(ViewFwActivity.this, "不能与本人聊天",
							Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(ViewFwActivity.this,
							ChattingActivity.class);
					Bundle arguments = new Bundle();
					arguments.putString("to", senduserid);
					arguments.putString("my", myapplication.getUserId());
					intent.putExtras(arguments);
					startActivity(intent);
				}
			}
		});

		//btn_pl = (Button) findViewById(R.id.info_plbutton);
		btn_gz = (Button) findViewById(R.id.info_gzbutton1);
		// btn_bm=(Button)findViewById(R.id.info__Bmbutton2);
		/*btn_pl.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String name = v.getTag().toString();
				if (name.equals("暂无评论")) {
					Intent Intent1 = new Intent();
					Intent1.setClass(ViewActivity.this, ViewListActivity.class);
					Bundle arguments = new Bundle();
					arguments.putString("put", "false");
					arguments.putString("guid", guid);
					Intent1.putExtras(arguments);
					startActivity(Intent1);

				} else {
					Intent Intent1 = new Intent();
					Intent1.setClass(ViewActivity.this, ViewListActivity.class);
					Bundle arguments = new Bundle();
					arguments.putString("put", "false");
					arguments.putString("guid", guid);
					Intent1.putExtras(arguments);
					startActivity(Intent1);
				}
			}
		});*/
		btn_gz.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
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
		}
	}

	public void discussSubmit(View view) {

		EditText discussContent = (EditText) findViewById(R.id.view_group_discuss);
		discuzz_content = discussContent.getText().toString();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS); // 隐软键盘
		// 判断content,不能为null或者特定字符
		if (discuzz_content == null || "".equals(discuzz_content)
				|| HINT.DISCUSSION.equals(discuzz_content)) {
			this.alertDialog(ViewFwActivity.this, "Error", "请输入回复内容! ");
			Log.e(TAG, "discuss content is null ! ");

			return;
		}
		if (!NetworkUtils.isNetConnected(ViewFwActivity.this)) {
			T.showShort(ViewFwActivity.this, "当前无网络连接！");
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		// 给map设置要显示的值
		UserService uService = new UserService(ViewFwActivity.this);
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
			String date = sDateFormat.format(new Date());
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
			paramsList.add(new BasicNameValuePair("_userid", myapplication
					.getUserId()));// 本人
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
				/*
				 * Message msg = new Message(); Bundle data = new Bundle();
				 * data.putInt("result", result); msg.setData(data);
				 * savehandler.sendMessage(msg);
				 */
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
				Toast.makeText(ViewFwActivity.this, "评论发表成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(ViewFwActivity.this, "评论发表失败", Toast.LENGTH_SHORT)
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
				Toast.makeText(ViewFwActivity.this, "不能与本人聊天", Toast.LENGTH_SHORT)
						.show();
			} else {
				// 获取头像的id
				Intent intent = new Intent(ViewFwActivity.this,
						ChattingActivity.class);
				Bundle arguments = new Bundle();
				arguments.putString("guid", guid);
				arguments.putString("to", itemuid);
				arguments.putString("my", myapplication.getUserId());
				intent.putExtras(arguments);
				startActivity(intent);
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
			String solutiondate = sDateFormat.format(new Date());

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
				MarketAPI.getinfo(getApplicationContext(), ViewFwActivity.this, guid);
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
						chat.setVisibility(View.GONE);//隐藏
					}else
					{
						chat.setVisibility(View.VISIBLE);//显示
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
								ViewFwActivity.this);
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
        /*case MarketAPI.ACTION_GETITEMNUM:
        	 HashMap<String, String> result1 = (HashMap<String, String>) obj;
	         String JsonContext1=result1.get("guidnums");   
	         if(JsonContext1!=null&&JsonContext1.length()>0)
	         {
	        	 JSONObject jsonobj;
	            	try {
	            		jsonobj = new JSONObject(JsonContext1);
						String[] nums = new String[3];
						nums[0] = jsonobj.getString("bmnum");
						nums[1] = jsonobj.getString("dicussnum");
						nums[2] = jsonobj.getString("gznum");
						String num = nums[1];
	            		if(num=="0")
	            		{
		    				btn_pl.setText("暂无评论");
		    				btn_pl.setTag("暂无评论");
	            		}else
	            		{
		    				btn_pl.setTag("有评论");
		    				btn_pl.setText("评论:" + num);
	            		}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();						
					}					
				}
        	break;*/
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
	            
	     /*   case MarketAPI.ACTION_GETITEMNUM: 
	            // 隐藏登录框
	            try{
	                dismissDialog(DIALOG_PROGRESS);
	            }catch (IllegalArgumentException e) {
	            }            		            
	            break;*/
	        default:
	            break;
	        }
	}

}
