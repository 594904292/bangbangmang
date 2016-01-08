package com.bbxiaoqu.ui.sub;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.jsonservices.GetJson;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.view.BaseActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ViewUserInfoActivity extends BaseActivity implements OnClickListener {
	private DemoApplication myapplication;
	TextView title;

	private TextView username_tv;
	private TextView score_tv;
	private TextView age_tv;
	private TextView sex_tv;
	private String sex_str = "1";
	private TextView community_tv;
	private TextView telphone_tv;
	Button save,chat;
	
	String username = "";
	String age = "";
	String sex = "";
	String telphone = "";
	String headface = "";
	String community="";
	String score="";
	
	
	private String headfacepath = "";
	private String headfacename = "";
	private String community_id="";
	private String community_lat="";
	private String community_lng="";
	/** ImageView对象 */
	private ImageView iv_photo;
	private String[] items = new String[] { "选择本地图片", "拍照" };
	/** 头像名称 */
	private static final String IMAGE_FILE_NAME = "image.jpg";

	private static final int SelXq_REQUEST_CODE=100;
	/** 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	private  String userid;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewuserinfo);
		myapplication = (DemoApplication) this.getApplication();
		Bundle Bundle1 = this.getIntent().getExtras();		
		userid = Bundle1.getString("userid");
		
		initView();
		initData();
	}
	
	
	
	private static final String[] sexs ={ " 男 " , "女" };
	private void initView() {
		title = (TextView) findViewById(R.id.title);
		username_tv = (TextView) findViewById(R.id.username);
		score_tv = (TextView) findViewById(R.id.score);
		age_tv = (TextView) findViewById(R.id.age);
		sex_tv = (TextView) findViewById(R.id.sex);
		community_tv = (TextView) findViewById(R.id.community);
		telphone_tv = (TextView) findViewById(R.id.telphone);
		save = (Button) findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!NetworkUtils.isNetConnected(myapplication)) {			
					T.showShort(myapplication, "当前无网络连接,请稍后再试！");
					return;
				}
				new Thread(addfriends).start();
			}
		});
		chat= (Button) findViewById(R.id.chat);
		chat.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ViewUserInfoActivity.this,ChattingActivity.class);
				
				Bundle arguments = new Bundle();
				arguments.putString("to", userid);
				arguments.putString("my",myapplication.getUserId());
				intent.putExtras(arguments);
				
				startActivity(intent);
			}
		});
		iv_photo = (ImageView) findViewById(R.id.iv_photo);
		
	}

	
	Runnable addfriends = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"addfriends.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("mid1", myapplication.getUserId()));
			paramsList.add(new BasicNameValuePair("mid2", userid));
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sDateFormat.format(new java.util.Date());
			paramsList.add(new BasicNameValuePair("addtime", date));
			if(save.getTag().equals("关注"))
			{
				paramsList.add(new BasicNameValuePair("action", "add"));
			}else
			{
				paramsList.add(new BasicNameValuePair("action", "del"));
			}			
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String json = EntityUtils.toString(httpResponse.getEntity());
					System.out.println(json);					
					result = 1;
				} else {
					result = 0;
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("result", result);
				msg.setData(data);
				publishhandler.sendMessage(msg);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	Handler publishhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			int result = data.getInt("result");
			Log.i("mylog", "请求结果-->" + result);			
			if (result == 1) {
				if(save.getTag().equals("关注"))
				{
					save.setText("取消关注");
					save.setTag("取消关注");
					Toast.makeText(ViewUserInfoActivity.this, "关注成功",Toast.LENGTH_SHORT).show();
				}else
				{
					save.setText("关注");
					save.setTag("关注");
					Toast.makeText(ViewUserInfoActivity.this, "取消关注成功",Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(ViewUserInfoActivity.this,"操作失败",Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	
	private void initData() {
		title.setText("用户中心");
		if (!NetworkUtils.isNetConnected(myapplication)) {			
			T.showShort(myapplication, "当前无网络连接,请稍后再试！");
			return;
		}
		new Thread(loaduserinfo).start();
	}

	
	private final String TAG = "UserInfoActivity";
	private AsyncHttpClient client;

	private void upLoadByAsyncHttpClient(String uploadUrl)
			throws FileNotFoundException {
		AsyncBody(uploadUrl, headfacepath);

	}

	private void AsyncBody(String uploadUrl, String localpath)
			throws FileNotFoundException {
		RequestParams params = new RequestParams();
		client = new AsyncHttpClient();
		params.put("uploadfile", new File(localpath));
		client.post(uploadUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, String arg1) {
				super.onSuccess(arg0, arg1);
				Log.i(TAG, arg1);
			}
		});
	}

	

	Runnable loaduserinfo = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"getuserinfo.php?userid="+ userid;
			HttpGet httprequest = new HttpGet(target);
			try {
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					InputStream jsonStream = null;
					jsonStream = httpResponse.getEntity().getContent();
					byte[] data = null;
					try {
						data = StreamTool.read(jsonStream);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String json = new String(data);
					JSONArray jsonarray;
					try {
						jsonarray = new JSONArray(json);
						JSONObject jsonobject = jsonarray.getJSONObject(0);
						username = jsonobject.getString("username");
						age = jsonobject.getString("age");
						sex = jsonobject.getString("sex");
						telphone = jsonobject.getString("telphone");
						headface = jsonobject.getString("headface");
						community = jsonobject.getString("community");
						score = jsonobject.getString("score");
						
						community_id = jsonobject.getString("community_id");
						community_lat = jsonobject.getString("community_lat");
						community_lng = jsonobject.getString("community_lng");
												
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(json);
					result = 1;
				} else {
					result = 0;
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("username", username);
				data.putString("age", age);
				data.putString("score", score);
				data.putString("sex", sex);
				data.putString("telphone", telphone);
				data.putString("headface", headface);
				data.putString("community", community);
				
				
				msg.setData(data);
				laodhandler.sendMessage(msg);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	};

	Handler laodhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			username_tv.setText("名称:"+data.getString("username"));
			score_tv.setText("积分:"+data.getString("score"));
			age_tv.setText("年龄："+data.getString("age"));
			if(data.getString("sex").equals("1"))
			{
				//male.setChecked(true);
				sex_tv.setText("性别:男");
			}else
			{
				sex_tv.setText("性别:女");
			}
			//sex.setText(data.getString("sex"));
			community_tv.setText("小区："+data.getString("community"));
			telphone_tv.setText("电话："+data.getString("telphone"));
			String fileName = myapplication.getlocalhost()+"uploads/"+ data.getString("headface");
			
			
			
			 ImageLoader.getInstance().displayImage(fileName, iv_photo, ImageOptions.getOptions());  
			 new Thread(ajaxloadfriend).start();//得到是否关注
		
		}
	};

	JSONObject jsonobject;//通过GUID获取的消息
	String isfriend="";
	Runnable ajaxloadfriend = new Runnable() {
		@Override
		public void run() {
			if (!NetworkUtils.isNetConnected(myapplication)) {
				T.showShort(myapplication, "当前无网络连接！");
				return;
			}
			String target = myapplication.getlocalhost()+"getisfriends.php?mid1="+myapplication.getUserId()+"&mid2="+userid;
			String json = GetJson.GetJson(target);
			if(json.startsWith("Error:"))
			{
				T.showShort(ViewUserInfoActivity.this, "网络错误:"+json);
				return;
			}
			JSONArray jsonarray;
			try {
				jsonobject = new JSONObject(json);
				//jsonobject = jsonarray.getJSONObject(0);
				isfriend = jsonobject.getString("isfriend");				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message msg = new Message();
			Bundle bundle = new Bundle();
			bundle.putString("isfriend", isfriend);
			msg.setData(bundle);
			friendhandler.sendMessage(msg);
		}};
	
		Handler friendhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				Bundle data = msg.getData();
				if(data.getString("isfriend").equals("yes"))
				{
					save.setText("取消关注");
					save.setTag("取消关注");
				}else
				{
					save.setText("关注");
					save.setTag("关注");
				}
				
			}};
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.right_text:

			break;
		default:
			break;
		}
	}
	
	
	
}
