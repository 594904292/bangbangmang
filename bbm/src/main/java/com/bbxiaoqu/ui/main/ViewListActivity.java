package com.bbxiaoqu.ui.main;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.adapter.BmUserAdapter.Callback;
import com.bbxiaoqu.bean.InfoBase;
import com.bbxiaoqu.comm.gallery.BigImgActivity;
import com.bbxiaoqu.comm.gallery.DetailGallery;
import com.bbxiaoqu.comm.gallery.TaoBaoImgShowActivity;
import com.bbxiaoqu.comm.gallery.Tool;
import com.bbxiaoqu.comm.gallery.ImgSwitchActivity.GalleryIndexAdapter;
import com.bbxiaoqu.comm.jsonservices.GetJson;
import com.bbxiaoqu.ui.popup.ActionItem;
import com.bbxiaoqu.ui.popup.Constants.HINT;
import com.bbxiaoqu.ui.popup.DateUtils;
import com.bbxiaoqu.ui.popup.ListLazyAdapter;
import com.bbxiaoqu.ui.popup.TitlePopup;
import com.bbxiaoqu.ui.popup.TitlePopup.OnItemOnClickListener;
import com.bbxiaoqu.ui.popup.Utils;
import com.bbxiaoqu.comm.service.User;
import com.bbxiaoqu.comm.service.db.MessBmService;
import com.bbxiaoqu.comm.service.db.MessGzService;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.ScreenUtils;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.Constants;
import com.bbxiaoqu.ui.sub.BmUserActivity;
import com.bbxiaoqu.ui.sub.ChattingActivity;
import com.bbxiaoqu.ui.sub.GalleryAdapter;
import com.bbxiaoqu.ui.sub.ViewUserInfoActivity;
import com.bbxiaoqu.view.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ViewListActivity extends BaseActivity implements OnItemClickListener, Callback{

	private static final String TAG = ViewListActivity.class.getSimpleName();
	private DemoApplication myapplication;
	private String infoid = "0";
	private String senduserid = "";
	private String sendusername = "";
	private String current_login_userid = "";
	private String current_login_usernickname="";
	private String title = "";
	private String content = "";
	private String headface = "";
	private String sendtime = "";
	private String infocatagroy = "";
	private String address = "";

	private String photo = "";
	public static final int chatflag = 1;
	// 评论列表
	private AssetManager assetManager;
	private List<Map<String, String>> discussList = new ArrayList<Map<String, String>>();
	private TextView title_tv;
	private TextView right_text_tv;
	
	private ListView listView;
	private ImageView groupPopup;
	private TitlePopup titlePopup;
	private Button group_discuss_submit;
	private RelativeLayout rl_bottom;
	private RelativeLayout gallery;
	private String discuzz_content = "";
	private String getdatamethon = "";	
	private String guid = "";
	private String gzaction="";
	private String bmaction="";
	
	private JSONObject jsonobject;//通过GUID获取的消息
	private boolean issolution = false;//是否解决
	private String solutionid = "0";//解决ID

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_viewlist);
		
		myapplication = (DemoApplication) this.getApplication();
		Bundle Bundle1 = this.getIntent().getExtras();
		getdatamethon = Bundle1.getString("put");	
		guid = Bundle1.getString("guid");
		infocatagroy = Bundle1.getString("infocatagroy");
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
	        
	
		
		
		assetManager = this.getAssets();
		listView = (ListView) findViewById(R.id.view_group_discuss_list);
		rl_bottom = (RelativeLayout) findViewById(R.id.view_rl_bottom);//评论布局		
		
		new Thread(loadUIThread).start();
			
		new Thread(ajaxloadinfo).start();
		
	}
	
	
	public static Bitmap getImage(String urlpath) {  
        URL url;
        Bitmap bitmap = null;  
		try {
			url = new URL(urlpath);
			  HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
		        conn.setRequestMethod("GET");  
		        conn.setConnectTimeout(20 * 1000);  
		        conn.setReadTimeout(20 * 1000);  
		       
		        if (conn.getResponseCode() == 200) {  
		            InputStream inputStream = conn.getInputStream();  
		            bitmap = BitmapFactory.decodeStream(inputStream);  
		        }  
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
      
        return bitmap;  
    }  
	
	
	 public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
	        int i;
	        int j;
	        if (bmp.getHeight() > bmp.getWidth()) {
	            i = bmp.getWidth();
	            j = bmp.getWidth();
	        } else {
	            i = bmp.getHeight();
	            j = bmp.getHeight();
	        }
	        
	        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
	        Canvas localCanvas = new Canvas(localBitmap);
	        
	        while (true) {
	            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0,i, j), null);
	            if (needRecycle)
	                bmp.recycle();
	            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
	            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
	                    localByteArrayOutputStream);
	            localBitmap.recycle();
	            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
	            try {
	                localByteArrayOutputStream.close();
	                return arrayOfByte;
	            } catch (Exception e) {
	                //F.out(e);
	            }
	            i = bmp.getHeight();
	            j = bmp.getHeight();
	        }
	    }
	  
	    
	
	

	Runnable ajaxloadinfo = new Runnable() {
		@Override
		public void run() {
			if (!NetworkUtils.isNetConnected(myapplication)) {
				T.showShort(myapplication, "当前无网络连接！");
				return;
			}
			String target = myapplication.getlocalhost()+"getinfo.php?guid=" + guid;
			String json = GetJson.GetJson(target);
			if(json==null||json.equals("")||json.toString().length()==0)
			{
				
				Message msg = basehandler.obtainMessage();			
				msg.what = 1;				
				basehandler.sendMessage(msg);
				return;
			}else
			{
				if(json.startsWith("Error:"))
				{
					//T.showShort(ViewActivity.this, "网络错误:"+json);
					Message msg = basehandler.obtainMessage();			
					msg.what = 2;				
					
					
			        Bundle data = new Bundle();
			        data.putString("error","json");	       
			        msg.setData(data);
			        
					basehandler.sendMessage(msg);
					return;
				}
				JSONArray jsonarray;
				
				try {
					jsonarray = new JSONArray(json);
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
					sendusername= jsonobject.getString("username");
					if(jsonobject.getString("issolution").equals("1"))
					{
						issolution= true;
					}else
					{
						issolution= false;							
					}
					
			
					solutionid= jsonobject.getString("solutionid");
					
					if(sendusername.equals(""))
					{
						sendusername="匿名";
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}							
				new Thread(loaddiscuzzThread).start();
			}
		}
	};
	
	
	private Handler basehandler = new Handler() {
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case 1:
				T.showShort(ViewListActivity.this, "数据服务端已删除");
				break;
			case 2:
				Bundle data = msg.getData();	
				 String error = data.getString("error");
				T.showShort(ViewListActivity.this, "网络错误:"+error);
				break;
			default:
				break;
			}			
		};
	};
	
	

	

	
	
	
	Runnable loadUIThread = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			initView();
			initData();

		}

	};

	private void initView() {
		title_tv = (TextView) findViewById(R.id.title);
		right_text_tv = (TextView) findViewById(R.id.right_text);
		right_text_tv.setVisibility(View.GONE);

	}

	private void initData() {
		title_tv.setText("评论");
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
		String target = myapplication.getlocalhost()+"/getdiscuzz.php?infoid="+ this.infoid;		
		try {
			String json=GetJson.GetJson(target);
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
			
			listView.setVisibility(View.GONE);
		} else {			
			listView.setVisibility(View.VISIBLE);
			ListLazyAdapter adapter = new ListLazyAdapter(this, discussList,this,this.senduserid,this.issolution,this.solutionid);
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
			this.alertDialog(ViewListActivity.this, "Error", "请输入回复内容! ");
			Log.e(TAG, "discuss content is null ! ");

			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		// 给map设置要显示的值
		UserService uService = new UserService(ViewListActivity.this);
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
			//
			int result;
			String target = myapplication.getlocalhost()+"discuzz.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			paramsList.add(new BasicNameValuePair("_infoid", infoid));// 本人
			paramsList.add(new BasicNameValuePair("_sendtime", date));// 本人
			paramsList.add(new BasicNameValuePair("_puserid", senduserid));// 本人
			paramsList.add(new BasicNameValuePair("_puser", sendusername));// 本人
			paramsList.add(new BasicNameValuePair("_touserid", current_login_userid));// 发帐人
			paramsList.add(new BasicNameValuePair("_touser", current_login_usernickname));// 发帐人
			paramsList.add(new BasicNameValuePair("_message", discuzz_content));// 公司代号
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
				savehandler.sendMessage(msg);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 发送到服务器

		}

	};
	
	
	Runnable savegzThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"addusergzinfo.php";
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
		
		
		Runnable savebmThread = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (!NetworkUtils.isNetConnected(myapplication)) {
					T.showShort(myapplication, "当前无网络连接！");
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
	Handler savehandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			int result = data.getInt("result");
			Log.i("mylog", "请求结果-->" + result);
			if (result == 1) {
				Toast.makeText(ViewListActivity.this, "保存成功", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(ViewListActivity.this, "保存失败", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	@Override
	public void click(View v) {
		// TODO Auto-generated method stub
		Toast.makeText(this, v.getTag().toString(),Toast.LENGTH_SHORT).show();
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "listview的item被点击了！，点击的位置是-->" + position,Toast.LENGTH_SHORT).show();
	}


	
}
