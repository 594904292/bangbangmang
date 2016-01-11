package com.bbxiaoqu.ui.community;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.bean.InfoBase;
import com.bbxiaoqu.ui.fragment.HomeActivity;
import com.bbxiaoqu.comm.gallery.BigImgActivity;
import com.bbxiaoqu.comm.gallery.DetailGallery;
import com.bbxiaoqu.comm.gallery.TaoBaoImgShowActivity;
import com.bbxiaoqu.comm.gallery.Tool;
import com.bbxiaoqu.comm.gallery.ImgSwitchActivity.GalleryIndexAdapter;
import com.bbxiaoqu.ui.popup.ActionItem;
import com.bbxiaoqu.ui.popup.Constants.HINT;
import com.bbxiaoqu.ui.popup.DateUtils;
import com.bbxiaoqu.ui.popup.ListLazyAdapter;
import com.bbxiaoqu.ui.popup.TitlePopup;
import com.bbxiaoqu.ui.popup.TitlePopup.OnItemOnClickListener;
import com.bbxiaoqu.ui.popup.Utils;
import com.bbxiaoqu.comm.service.User;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.service.db.XiaoquService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.LoginActivity;
import com.bbxiaoqu.view.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
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
/**
 * @author dzy
 * 小区信息显示
 */
public class CommunityActivity extends BaseActivity {
	private DemoApplication myapplication;

	private static final String TAG = CommunityActivity.class.getSimpleName();
	public static final int chatflag = 1;

	private DetailGallery myGallery;
	private AssetManager assetManager;
	TextView title_tv;
	TextView right_text_tv;
	TextView group_discuss_tip;


	String getdatamethon = "";
	String id = "";
	String xqname = "";
	String address = "";
	String lat = "";
	String lng = "";
	String pic = "";
	String business = "";
	String develop = "";
	String propertymanagement = "";
	String propertytype = "";
	String homenumber = "";
	String buildyear = "";
	TextView title;
	TextView right_text;
	ImageView imgmore;
	Button btn1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_community_view);
		myapplication = (DemoApplication) this.getApplication();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		myGallery = (DetailGallery) findViewById(R.id.detail_shotcut_gallery);
		initView();
		initData();
		assetManager = this.getAssets();
		btn1=(Button)findViewById(R.id.button1);
		btn1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String btn_name=v.getTag().toString();
				XiaoquService xiaoquService = new XiaoquService(CommunityActivity.this);
				if(btn_name.equals("关注"))
				{
					if(xiaoquService.allxiaoqunum()>3) {
						T.showShort(myapplication, "最多只能关注三个小区");
						return;
					}else
					{
						xiaoquService.addxiaoqu(id, xqname);
						subscribe(id, "add");
						btn1.setText("取消关注");
					}
				}else if(btn_name.equals("取消关注"))
				{
					xiaoquService.removexiaoqu(id);
					subscribe(id, "remove");
					btn1.setText("关注");
				}

				Intent intent = new Intent();
				intent.putExtra("community", xqname);
				setResult(RESULT_OK, intent);
				finish();
				
			}
		});
		
		new Thread(loadUIThread).start();
		Bundle Bundle1 = this.getIntent().getExtras();		

		id = Bundle1.getString("id");
		xqname = Bundle1.getString("name");
		address = Bundle1.getString("address");
		lat = Bundle1.getString("lat");
		lng = Bundle1.getString("lng");
		pic = Bundle1.getString("pic");
		business = Bundle1.getString("business");
		develop = Bundle1.getString("develop");
		propertymanagement = Bundle1.getString("propertymanagement");
		propertytype = Bundle1.getString("propertytype");
		homenumber = Bundle1.getString("homenumber");
		buildyear = Bundle1.getString("buildyear");
		
		XiaoquService xiaoquService = new XiaoquService(
		CommunityActivity.this);
		boolean ishavezhan = xiaoquService.isexit(id);
		if(ishavezhan)
		{
			btn1.setText("取消关注");
			btn1.setTag("取消关注");
		}else
		{
			btn1.setText("关注");
			btn1.setTag("关注");
		}
		if(pic!=null&&!pic.equals("null")&&pic.length()>10)
		{
			init();
			addEvn();
		}
		if (!NetworkUtils.isNetConnected(myapplication)) {			
			T.showShort(myapplication, "当前无网络连接,请稍后再试！");
			return;
		}
		new Thread(ajaxloadinfo).start();

	}

	private void initView() {
		title = (TextView) findViewById(R.id.title);
		right_text = (TextView) findViewById(R.id.right_text);
		right_text.setVisibility(View.GONE);
		imgmore=(ImageView) findViewById(R.id.top_more);
		imgmore.setVisibility(View.GONE);


	}

	private void initData() {
		title.setText("附近小区 ");
		right_text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(CommunityActivity.this,AddCommunityActivity.class);
				startActivity(intent);
			}
		});
	}


	private void subscribe(String xiaoquid, String action) {
		String target = myapplication.getlocalhost()+"adduserxiaoqu.php";
		HttpPost httprequest = new HttpPost(target);
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();

		paramsList.add(new BasicNameValuePair("_userid", myapplication
				.getUserId()));
		paramsList.add(new BasicNameValuePair("_communityid", xiaoquid));
		paramsList.add(new BasicNameValuePair("_action", action));
		try {
			httprequest
					.setEntity(new UrlEncodedFormEntity(paramsList, "UTF-8"));
			HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
			HttpResponse httpResponse = HttpClient1.execute(httprequest);
			String authcode = "";
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				authcode = EntityUtils.toString(httpResponse.getEntity());

			} else {
				authcode = "";
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	Runnable ajaxloadinfo = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
				Message msg = new Message();
				Bundle arguments = new Bundle();
				arguments.putString("id",id);	
				arguments.putString("name",xqname);
				arguments.putString("address",address);								
				arguments.putString("pic",pic);					
				arguments.putString("business",business);	
				arguments.putString("develop",develop);	
				arguments.putString("propertymanagement",propertymanagement);	
				arguments.putString("propertytype",propertytype);	
				arguments.putString("homenumber",homenumber);	
				arguments.putString("buildyear",buildyear);	
				msg.setData(arguments);
				showhandler.sendMessage(msg);				
				// laodhandler.sendMessage(msg);
			

		}
	};
	
	
	
	Handler showhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			Bundle data = msg.getData();
			
			TextView name = (TextView) findViewById(R.id.c_name);
			TextView address = (TextView) findViewById(R.id.c_address);
			name.setText(data.getString("name"));
			address.setText( data.getString("address"));
		}
	};
	
	Runnable loadUIThread = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			initView();
			initData();
			
			
			

		}

	};



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	void init() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(1);
		
		GalleryAdapter adapter = new GalleryAdapter(list,
				getApplicationContext());
		myGallery.setAdapter(adapter);

	}

	void addEvn() {
		myGallery.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CommunityActivity.this,
						BigImgActivity.class);
				intent.putExtra("imageName", arg1.getTag().toString());
				startActivity(intent);
			}
		});
	}

	class GalleryAdapter extends BaseAdapter {
		List<Integer> imagList;
		Context context;

		public GalleryAdapter(List<Integer> list, Context cx) {
			imagList = list;
			context = cx;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imagList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageView imageView = (ImageView) LayoutInflater.from(context)
					.inflate(R.layout.img, null);
			Bitmap bitmap = null;
			if (position == 1) {
				bitmap = ImageLoader.getInstance().loadImageSync(pic);
				imageView.setTag(pic);
			} else {
				bitmap = ImageLoader.getInstance().loadImageSync(pic);
				imageView.setTag(pic);
			}
			// ����ͼƬ֮ǰ��������
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			float newHeight = 200;
			float newWidth = width * newHeight / height;
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			// ȡ����Ҫ���ŵ�matrix����
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			// �õ��µ�ͼƬ
			Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height,
					matrix, true);
			System.out.println(newbm.getHeight() + "-----------"
					+ newbm.getWidth());
			imageView.setImageBitmap(newbm);
			// }
			return imageView;

		}
	}
}
