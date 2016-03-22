package com.bbxiaoqu.ui.user;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.comm.service.db.XiaoquService;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.SearchActivity;
import com.bbxiaoqu.ui.community.SelCommunityActivity;
import com.bbxiaoqu.ui.community.CommunityGzListActivity;
import com.bbxiaoqu.ui.sub.PayActivity;
import com.bbxiaoqu.ui.sub.RoundAngleImageView;
import com.bbxiaoqu.view.BaseActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class UserInfoActivity extends BaseActivity implements OnClickListener {
	private DemoApplication myapplication;
	TextView title;
	TextView txt_userid;
	TextView tv_score;
	private EditText username;
	private EditText age;
	private EditText brithday;
	private String sex_str = "1";
	private TextView txt=null;
	private RadioGroup sex=null;
	private RadioButton male=null;
	private RadioButton female=null;
	private EditText community_eidt;
	private EditText telphone;
	Button save;
	Button score_btn;
	Button SelCommunityBtn;
	private String headfacepath = "";
	private String headfacename = "";
	private String community_id="";
	private String community_lat="";
	private String community_lng="";
	private String score="";
	private String userid="";
	public ImageView top_more;
	/** ImageView对象 */
	private RoundAngleImageView iv_photo;
	private String[] items = new String[] { "选择本地图片", "拍照" };
	/** 头像名称 */
	private static final String IMAGE_FILE_NAME = "image.jpg";
	private static final int SelXq_REQUEST_CODE=100;
	private static final int GzXq_REQUEST_CODE=101;
	/** 请求码 */
	private static final int IMAGE_REQUEST_CODE = 0;
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int RESULT_REQUEST_CODE = 2;
	UserService uService = new UserService(UserInfoActivity.this);


	//获取日期格式器对象
	DateFormat fmtDateAndTime =  new SimpleDateFormat("yyyy-MM-dd");
	//定义一个TextView控件对象
	TextView dateAndTimeLabel = null;
	//获取一个日历对象
	Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);


	//当点击DatePickerDialog控件的设置按钮时，调用该方法
	DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener()
	{
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
							  int dayOfMonth) {
			//修改日历控件的年，月，日
			//这里的year,monthOfYear,dayOfMonth的值与DatePickerDialog控件设置的最新值一致
			dateAndTime.set(Calendar.YEAR, year);
			dateAndTime.set(Calendar.MONTH, monthOfYear);
			dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			//将页面TextView的显示更新为最新时间
			updateLabel();
		}
	};



	TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {

		//同DatePickerDialog控件
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
			dateAndTime.set(Calendar.MINUTE, minute);
			updateLabel();

		}
	};

	//更新页面TextView的方法
	private void updateLabel() {

		brithday.setText(fmtDateAndTime
				.format(dateAndTime.getTime()));
		DateFormat fmtDateAndTime1 =  new SimpleDateFormat("yyyy");
		String agea=fmtDateAndTime1.format(dateAndTime.getTime());
		String ageb=fmtDateAndTime1.format(new Date().getTime());

		int agecle=Integer.parseInt(ageb)-Integer.parseInt(agea);
		age.setText(String.valueOf(agecle));
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo);
		myapplication = (DemoApplication) this.getApplication();
		initView();
		initData();
	}
    private static final String[] sexs ={ " 男 " , "女" };
	private void initView() {
		title = (TextView) findViewById(R.id.title);
		username = (EditText) findViewById(R.id.username);
		brithday= (EditText) findViewById(R.id.brithday);
		age = (EditText) findViewById(R.id.age);
		community_eidt = (EditText) findViewById(R.id.community);
		telphone = (EditText) findViewById(R.id.telphone);
		save = (Button) findViewById(R.id.save);
		score_btn  = (Button) findViewById(R.id.score_btn);
		SelCommunityBtn = (Button) findViewById(R.id.SelCommunityBtn);
		txt_userid=(TextView) findViewById(R.id.txt_userid);
		tv_score=(TextView) findViewById(R.id.score_tv);
		this.txt=(TextView) super.findViewById(R.id.txt);
		this.sex=(RadioGroup) super.findViewById(R.id.sex);
		this.male=(RadioButton) super.findViewById(R.id.male);
		this.female=(RadioButton) super.findViewById(R.id.female);
		
		top_more = (ImageView) findViewById(R.id.top_more);	
		top_more.setVisibility(View.VISIBLE);
		top_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(UserInfoActivity.this,SearchActivity.class);									
				startActivity(intent);
			}
		});
		
		this.sex.setOnCheckedChangeListener(new OnCheckedChangeListenerImp());
		score_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent(UserInfoActivity.this,PayActivity.class);
				startActivity(intent);
			}
		});
		save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!NetworkUtils.isNetConnected(myapplication)) {			
					T.showShort(myapplication, "当前无网络连接,请稍后再试！");
					return;
				}
				new AlertDialog.Builder(UserInfoActivity.this).setTitle("确认修改吗？") 
	            .setIcon(android.R.drawable.ic_dialog_info) 
	            .setPositiveButton("确定", new DialogInterface.OnClickListener() { 
	                @Override 
	                public void onClick(DialogInterface dialog, int which) { 
	                // 点击“确认”后的操作 
	                	//更新本地库
	    				uService.updatenickname(username.getText().toString(), userid);
	    				if(headfacename!=null&&headfacename.length()>0)
	    				{//远程,头像不为空
	    					uService.updateheadface(headfacename, userid);
	    				}
	    				new Thread(saveuserinfo).start();
	                } 
	            }) 
	            .setNegativeButton("返回", new DialogInterface.OnClickListener() { 
	         
	                @Override 
	                public void onClick(DialogInterface dialog, int which) { 
	                // 点击“返回”后的操作,这里不设置没有任何操作 
	                } 
	            }).show(); 
			}
		});
		SelCommunityBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent(UserInfoActivity.this,SelCommunityActivity.class);
				startActivityForResult(intent, SelXq_REQUEST_CODE);    
			}
		});
		XiaoquService xiaoquService = new XiaoquService(this);
		String names=xiaoquService.allxiaoqu();


		//得到页面设定日期的按钮控件对象
		Button dateBtn = (Button)findViewById(R.id.setDate);
		//设置按钮的点击事件监听器
		dateBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//生成一个DatePickerDialog对象，并显示。显示的DatePickerDialog控件可以选择年月日，并设置
				new DatePickerDialog(UserInfoActivity.this,
						d,
						dateAndTime.get(Calendar.YEAR),
						dateAndTime.get(Calendar.MONTH),
						dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
			}
		});


		iv_photo = (RoundAngleImageView) findViewById(R.id.iv_photo);
		iv_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
	}

	
	private class OnCheckedChangeListenerImp implements OnCheckedChangeListener{

		public void onCheckedChanged(RadioGroup group, int checkedId) {
		String temp=null;
		if(UserInfoActivity.this.male.getId()==checkedId){
		temp="男";
		sex_str="1";
		}
		else if(UserInfoActivity.this.female.getId()==checkedId){
		temp="女";
		sex_str="0";
		}	
		UserInfoActivity.this.txt.setText("您的性别是"+temp);	
		}
		
		}
	/**
	 * 显示选择对话框
	 */
	private void showDialog() {
		new AlertDialog.Builder(this)
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							Intent intentFromGallery = new Intent();
							intentFromGallery.setType("image/*"); // 设置文件类型
							intentFromGallery
									.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentFromGallery,
									IMAGE_REQUEST_CODE);
							break;
						case 1:
							Intent intentFromCapture = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							// 判断存储卡是否可以用，可用进行存储
							String state = Environment
									.getExternalStorageState();
							if (state.equals(Environment.MEDIA_MOUNTED)) {
								File path = Environment
										.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
								File file = new File(path, IMAGE_FILE_NAME);
								intentFromCapture.putExtra(
										MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(file));
							}
							startActivityForResult(intentFromCapture,
									CAMERA_REQUEST_CODE);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 结果码不等于取消时候
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case SelXq_REQUEST_CODE:
				community_eidt.setText(data.getStringExtra("community"));
				community_id=data.getStringExtra("community_id");
				community_lat=data.getStringExtra("community_lat");
				community_lng=data.getStringExtra("community_lng");
				break;
			case IMAGE_REQUEST_CODE:
				startPhotoZoom(data.getData());
				break;
			case CAMERA_REQUEST_CODE:
				// 判断存储卡是否可以用，可用进行存储
				String state = Environment.getExternalStorageState();
				if (state.equals(Environment.MEDIA_MOUNTED)) {
					File path = Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
					File tempFile = new File(path, IMAGE_FILE_NAME);
					startPhotoZoom(Uri.fromFile(tempFile));
				} else {
					Toast.makeText(getApplicationContext(), "未找到存储卡，无法存储照片！",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case RESULT_REQUEST_CODE: // 图片缩放完成后
				if (data != null) {
					getImageToView(data);
				}
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 340);
		intent.putExtra("outputY", 340);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT_REQUEST_CODE);
	}

	/**
	 * 保存裁剪之后的图片数据
	 *
	 */
	private void getImageToView(Intent data) {
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");			
			photo=ThumbnailUtils.extractThumbnail(photo, 150, 150);			
			java.text.DateFormat format2 = new java.text.SimpleDateFormat(
					"yyyyMMddHHmmss");
			headfacename = myapplication.getUserId()+"_"+format2.format(new Date()) + ".jpg";
			headfacepath = saveBitmap(photo, headfacename);
			Drawable drawable = new BitmapDrawable(this.getResources(), photo);
			iv_photo.setImageDrawable(drawable);
		}
	}

	private String saveBitmap(Bitmap imgThumb, String fileName) {
		// TODO Auto-generated method stub
		FileOutputStream out = null;
		File yygypath = this.getFilesDir();// this.getCacheDir();
		String yygypathstr = yygypath.toString();
		try {
			out = new FileOutputStream(yygypathstr + "/" + fileName);
			imgThumb.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Throwable ignore) {
			}
		}
		return yygypathstr + "/" + fileName;
	}

	private void initData() {
		title.setText("用户中心");
		if (!NetworkUtils.isNetConnected(myapplication)) {			
			T.showShort(myapplication, "当前无网络连接,请稍后再试！");
			return;
		}
		new Thread(loaduserinfo).start();		
	}

	Runnable saveuserinfo = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"saveuserinfo.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			paramsList.add(new BasicNameValuePair("userid", myapplication.getUserId()));
			paramsList.add(new BasicNameValuePair("headface", headfacename));
			paramsList.add(new BasicNameValuePair("username", username.getText().toString()));
 			paramsList.add(new BasicNameValuePair("brithday", brithday.getText()	.toString()));
			paramsList.add(new BasicNameValuePair("age", age.getText()	.toString()));
			paramsList.add(new BasicNameValuePair("sex", sex_str));
			paramsList.add(new BasicNameValuePair("telphone", telphone.getText().toString()));
			paramsList.add(new BasicNameValuePair("community", community_eidt.getText().toString()));
			paramsList.add(new BasicNameValuePair("community_id", community_id));
			paramsList.add(new BasicNameValuePair("community_lat", community_lat));
			paramsList.add(new BasicNameValuePair("community_lng", community_lng));
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String json = EntityUtils.toString(httpResponse.getEntity());
					String target1 = myapplication.getlocalhost()+"upload.php";
					if(headfacepath!=null&&headfacepath.length()>0)
					{
						upLoadByAsyncHttpClient(target1);
					}
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

	Handler publishhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			int result = data.getInt("result");
			Log.i("mylog", "请求结果-->" + result);
			if (result == 1) {
				Toast.makeText(UserInfoActivity.this, "更新成功",
						Toast.LENGTH_SHORT).show();
			} else {
				// Toast.makeText(SendFragment.this,
				// "推送失败",Toast.LENGTH_SHORT).show();
			}
		}
	};
	//String remote_headface = "";
	Runnable loaduserinfo = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String username = "";
			String age = "";
			String brithday = "";
			String sex = "";
			String telphone = "";
			String remote_headface = "";
			String community="";
			String target = myapplication.getlocalhost()+"getuserinfo.php?userid="
					+ myapplication.getUserId();
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
						brithday= jsonobject.getString("brithday");
						sex = jsonobject.getString("sex");
						telphone = jsonobject.getString("telphone");
						remote_headface = jsonobject.getString("headface");
						community = jsonobject.getString("community");
						userid = jsonobject.getString("userid");
						score = jsonobject.getString("score");
						uService.updatenickname(username, userid);//更新用户昵称
						community_id = jsonobject.getString("community_id");
						community_lat = jsonobject.getString("community_lat");
						community_lng = jsonobject.getString("community_lng");
								
						uService.updatenickname(username, myapplication.getUserId());
						uService.updateheadface(remote_headface, myapplication.getUserId());
						
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
				data.putString("brithday", brithday);
				data.putString("sex", sex);
				data.putString("telphone", telphone);
				data.putString("headface", remote_headface);
				data.putString("community", community);
				data.putString("userid", userid);
				data.putString("score", score);
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
			username.setText(data.getString("username"));
			age.setText(data.getString("age"));
			brithday.setText(data.getString("brithday"));
			if(data.getString("sex").equals("1"))
			{
				male.setChecked(true);
			}else
			{
				female.setChecked(true);
			}
			if(!data.getString("community").equals("null")&&data.getString("community").length()>0)
			{
				community_eidt.setText(data.getString("community"));
			}else
			{
				community_eidt.setText("");
			}
			telphone.setText(data.getString("telphone"));
			
			
			txt_userid.setText("用户ID:"+data.getString("userid"));
			tv_score.setText("积分:"+data.getString("score").toString());
			
			if(data.getString("headface")!=null&&data.getString("headface").length()>0)
			{
				String fileName = myapplication.getlocalhost()+"uploads/"+ data.getString("headface");
				ImageLoader.getInstance().displayImage(fileName, iv_photo, ImageOptions.getOptions());
			}
		}
	};

	

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
