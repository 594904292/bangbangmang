package com.bbxiaoqu.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.ScreenUtils;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.fragment.publish.BitmapUtils;
import com.bbxiaoqu.ui.fragment.publish.StringUtils;
import com.bbxiaoqu.ui.sub.SelectPhotoActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PublishFwActivity extends Activity implements OnClickListener {
    private static final String TAG = "demoActivity";

	/** 头像 */
	public ImageView top_head;
	/** 更多 */
	public ImageView top_more;

	private DemoApplication myapplication;
	EditText servname_edit;
	EditText content_edit;
	//EditText fee_edit;
	//RadioGroup radioGroup;
	Button addimg;
	Button send;
	private Button btn;
	public static final int TO_SELECT_PHOTO1 = 2;
	private String picPath = null;
	private LocationClient mLocationClient;
	public Double nLatitude; // 经度 给gps定位用
	public Double nLontitude; // 纬度 给gps定位用
	public String address; // 纬度 给gps定位用
	public String Country = "";
	public String Province = "";
	public String City = "";
	public String CityCode = "";
	public String District = "";
	public String Street = "";
	public String StreetNumber = "";
	public String Floor = "";
	public String addr = "";
	public String networklocationtype = "";
	public String operators = "";
	public String direction = "";
	public String radius = "";
	public String speed = "";

	public static final int STATUS_None = 0;
	public static final int STATUS_WaitingReady = 2;
	public static final int STATUS_Ready = 3;
	public static final int STATUS_Speaking = 4;
	public static final int STATUS_Recognition = 5;
	private SpeechRecognizer speechRecognizer;
	private int status = STATUS_None;
	private long speechEndTime = -1;
	private static final int EVENT_ERROR = 11;
	private LayoutInflater mInflater;


	private LinearLayout mLayout;
	private LinearLayout ext_mLayout;
	private List<ImageButton> mImageButtonList;
	private List<String> mPicturePathList;
	private List<String> compmPicturePathList;
	private int mCurrent;
	//private LinearLayout mpirceLayout;
	//private ToggleButton togglebutton;
	/** 屏幕宽度 */
	private int mScreenWidth = 0;
	/** Item宽度 */
	private int mItemWidth = 0;
	private int infocatagroy = 0;
	boolean isaddpic = false;
	private ProgressBar pb;
	private TextView pbtip;
	private TextView pbtip1;
	//private BaiduASRDigitalDialog mDialog = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish_fw);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		Resources resource = this.getResources();
		String pkgName = this.getPackageName();
		PushManager.startWork(getApplicationContext(),PushConstants.LOGIN_TYPE_API_KEY, Constants.API_KEY);
		TextView title = (TextView) findViewById(R.id.title);
		title.setText("发送消息");
		Bundle b = this.getIntent().getExtras();
		infocatagroy = b.getInt("infocatagroy");
		myapplication = (DemoApplication) this.getApplication();
		mScreenWidth = ScreenUtils.getWindowsWidth(this);
		mItemWidth = mScreenWidth / 3;// 一个Item宽度为屏幕的1/7
		//initbaidu(resource, pkgName);
		initlsb();

		mImageButtonList = new ArrayList<ImageButton>();
		mPicturePathList = new ArrayList<String>();
		compmPicturePathList = new ArrayList<String>();
		servname_edit= (EditText) findViewById(R.id.servicename);
		content_edit = (EditText) findViewById(R.id.content);
		send = (Button) findViewById(R.id.sendmessage);
		if (infocatagroy == 0) {
			content_edit.setHint("请输入您的求助信息");
			Resources resources = PublishFwActivity.this.getResources();
			Drawable btnDrawable = resources.getDrawable(R.drawable.button_help);
			send.setBackgroundDrawable(btnDrawable);
		} else if (infocatagroy == 1) {
			content_edit.setHint("请输入您的需求信息");
		} else if (infocatagroy == 2) {
			content_edit.setHint("请输入您的转让信息");
		} else if (infocatagroy == 3) {
			content_edit.setHint("请输入您的能帮助信息");
			Resources resources = PublishFwActivity.this.getResources();
			Drawable btnDrawable = resources.getDrawable(R.drawable.button_help3);
			send.setBackgroundDrawable(btnDrawable);

		}

		mLayout = (LinearLayout) findViewById(R.id.layout_container);
		ext_mLayout = (LinearLayout) findViewById(R.id.layout_container_ext);
		//mpirceLayout= (LinearLayout) findViewById(R.id.layout_container_price);
		mLayout.setVisibility(View.GONE);

		addimg = (Button) findViewById(R.id.addimg);
		btn = (Button) findViewById(R.id.yybtn);
		//togglebutton = (ToggleButton) findViewById(R.id.repayable_switch);//是否有偿

		pb = (ProgressBar) findViewById(R.id.myProgressBar1);
		pbtip=(TextView) findViewById(R.id.myProgressBar1Tip);
		pbtip1=(TextView) findViewById(R.id.myProgressBar1Tip1);
		pb.setVisibility(View.GONE);
		pbtip.setVisibility(View.GONE);
		pbtip1.setVisibility(View.GONE);


		addimg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 通过资源文件来获得指定一个Drawable对象
				if (isaddpic) {
					isaddpic = false;
					mLayout.setVisibility(View.GONE);
				} else {
					isaddpic = true;
					mLayout.setVisibility(View.VISIBLE);
				}
			}
		});

		send.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!NetworkUtils.isNetConnected(myapplication)) {
					T.showShort(myapplication, "当前无网络连接！");
					return;
				}
				String name=servname_edit.getText().toString();
				if(name.length()<5)
				{
					T.showShort(myapplication, "标题不能少于五个字符！");
					return;
				}
				String content = content_edit.getText().toString();
				if(content.length()<5)
				{
					T.showShort(myapplication, "内容不能少于五个字符！");
					return;
				}
				if (content.length() > 0) {
					pb.setVisibility(View.VISIBLE);
					pbtip.setVisibility(View.VISIBLE);
					pbtip1.setVisibility(View.VISIBLE);
					pbtip.setText("保存中");
					new Thread(sendRun).start();
				} else {
					Toast.makeText(v.getContext(), "信息为空,发布不成功", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		mCurrent = 0;
		init_imgui();

	}

	String[] tag;
	String[] name;
	@SuppressLint("ResourceAsColor")
	public void init_extui()
	{
		if(infocatagroy==1)
		{
			tag=new String[]{"low_price","hight_price"};
			name=new String[]{"最低价","最高价"};
			for(int i=0;i<name.length;i++)
			{
				LinearLayout row=new LinearLayout(this);
				row.setOrientation(LinearLayout.HORIZONTAL);
				row.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1.0f));

				TextView tv=new TextView(this);
				tv.setText(name[i]);
				tv.setLayoutParams(new LinearLayout.LayoutParams(mItemWidth, LayoutParams.WRAP_CONTENT, 1.0f));

				//android:layout_weight
				EditText edit =new EditText(this);
				edit.setTag(tag[i]);
				edit.setBackgroundResource(R.drawable.bg_edittext);
				edit.setTextColor(R.color.black);
				edit.setText("");
				edit.setLayoutParams(new LinearLayout.LayoutParams(mItemWidth*2, LayoutParams.WRAP_CONTENT, 1.0f));
				row.addView(tv);
				row.addView(edit);

				this.ext_mLayout.addView(row);

			}
		}else if(infocatagroy==2)
		{
			tag=new String[]{"name","price"};
			name=new String[]{"商品名称","价格"};
			for(int i=0;i<name.length;i++)
			{
				LinearLayout row=new LinearLayout(this);
				row.setOrientation(LinearLayout.HORIZONTAL);
				row.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, 1.0f));

				TextView tv=new TextView(this);
				tv.setText(name[i]);
				tv.setLayoutParams(new LinearLayout.LayoutParams(mItemWidth, LayoutParams.WRAP_CONTENT, 1.0f));

				//android:layout_weight
				EditText edit =new EditText(this);
				edit.setTag(tag[i]);
				edit.setBackgroundResource(R.drawable.bg_edittext);
				edit.setTextColor(R.color.black);
				edit.setText("");
				edit.setLayoutParams(new LinearLayout.LayoutParams(mItemWidth*2, LayoutParams.WRAP_CONTENT, 1.0f));
				row.addView(tv);
				row.addView(edit);

				this.ext_mLayout.addView(row);
			}
		}

	}

	private void init_imgui() {
		final int count = 6; // 9格
		final int rowCount = (count + 2) / 3;
		for (int i = 0; i < rowCount; i++) {
			if (i != 0) {
				// 加载横向布局线条
				View.inflate(this, R.layout.layout_line_horizonal, mLayout);
			}
			// 创建布局对象，设置按下颜色
			final LinearLayout linearLayout = new LinearLayout(this);
			linearLayout.setBackgroundResource(R.drawable.row_selector);
			for (int j = 0; j < 3; j++) {
				if (j != 0) {
					// 加载内层纵向布局线条
					View.inflate(this, R.layout.layout_line_vertical,
							linearLayout);
				}

				ImageButton imageButton = new ImageButton(this);
				imageButton.setBackgroundResource(R.drawable.row_selector);

				int indextag = i * 3 + j;
				imageButton.setTag(indextag);

				imageButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if (v.getTag() != null) {
							Intent intent;
							intent = new Intent(v.getContext(),
									SelectPhotoActivity.class);
							Bundle arguments = new Bundle();
							arguments.putInt("pos",
									Integer.parseInt(v.getTag().toString()));
							intent.putExtras(arguments);
							startActivityForResult(intent, TO_SELECT_PHOTO1);
							intent = null;
						} else {
							// super.onClick(v);
						}
					}
				});
				imageButton.setEnabled(false);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
						1.0f);
				// 添加到linearLayout布局中
				linearLayout.addView(imageButton, layoutParams);
				// 将imageButton对象添加到列表
				mImageButtonList.add(imageButton);
			}

			LayoutParams layoutParams = new LayoutParams(
					LayoutParams.MATCH_PARENT, mItemWidth);
			mLayout.addView(linearLayout, layoutParams);
		}

		final ImageButton currentImageButton = mImageButtonList.get(mCurrent);
		currentImageButton.setImageResource(R.mipmap.ic_add_picture);
		currentImageButton.setScaleType(ScaleType.CENTER);
		currentImageButton.setEnabled(true);
	}

	private void initbaidu(Resources resource, String pkgName) {
		// Push: 以apikey的方式登录，一般放在主Activity的onCreate中。
		// 这里把apikey存放于manifest文件中，只是一种存放方式，
		// 您可以用自定义常量等其它方式实现，来替换参数中的Utils.getMetaValue(PushDemoActivity.this,
		// "api_key")
		PushManager.startWork(this.myapplication,
				PushConstants.LOGIN_TYPE_API_KEY, DemoApplication.API_KEY);
		// Push: 如果想基于地理位置推送，可以打开支持地理位置的推送的开关
		// PushManager.enableLbs(getApplicationContext());

		// Push: 设置自定义的通知样式，具体API介绍见用户手册，如果想使用系统默认的可以不加这段代码
		// 请在通知推送界面中，高级设置->通知栏样式->自定义样式，选中并且填写值：1，
		// 与下方代码中 PushManager.setNotificationBuilder(this, 1, cBuilder)中的第二个参数对应
		/*
		 * CustomPushNotificationBuilder cBuilder = new
		 * CustomPushNotificationBuilder( this.getApplicationContext(),
		 * resource.getIdentifier( "notification_custom_builder", "layout",
		 * pkgName), resource.getIdentifier("notification_icon", "id", pkgName),
		 * resource.getIdentifier("notification_title", "id", pkgName),
		 * resource.getIdentifier("notification_text", "id", pkgName));
		 * cBuilder.setNotificationFlags(Notification.FLAG_AUTO_CANCEL);
		 * cBuilder.setNotificationDefaults(Notification.DEFAULT_SOUND |
		 * Notification.DEFAULT_VIBRATE);
		 * cBuilder.setStatusbarIcon(this.getApplicationInfo().icon);
		 * cBuilder.setLayoutDrawable(resource.getIdentifier(
		 * "simple_notification_icon", "drawable", pkgName));
		 * PushManager.setNotificationBuilder(this, 1, cBuilder);
		 */
	}

	private void initlsb() {
		// TODO Auto-generated method stub
		mLocationClient = new LocationClient(this.myapplication);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000;

		option.setScanSpan(span);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);

		mLocationClient.registerLocationListener(new BDLocationListener() {

			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				if (location == null) {
					return;
				}
				nLatitude = location.getLatitude();
				nLontitude = location.getLongitude();
				Country = location.getCountry();
				Province = location.getProvince();
				City = location.getCity();
				CityCode = location.getCityCode();
				District = location.getDistrict();
				Street = location.getStreet();
				StreetNumber = location.getStreetNumber();
				Floor = location.getFloor();
				addr = location.getAddrStr();

				networklocationtype = location.getNetworkLocationType();
				operators = String.valueOf(location.getOperators());
				direction = String.valueOf(location.getDirection());
				radius = String.valueOf(location.getRadius());
				speed = String.valueOf(location.getSpeed());

				mLocationClient.stop();

				myapplication.setLat(String.valueOf(nLatitude));
				myapplication.setLng(String.valueOf(nLontitude));
				myapplication.updatelocation();

				Message msg = new Message();
				Bundle data = new Bundle();
				data.putDouble("nLatitude", nLatitude);
				data.putDouble("nLontitude", nLontitude);
				msg.setData(data);
				lbsfinshhandler.sendMessage(msg);
			}

		});
		mLocationClient.start();

		mLocationClient.requestLocation();

	}

	Handler lbsfinshhandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};

	Runnable sendRun = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String servicename=servname_edit.getText().toString();
			String content = content_edit.getText().toString();

			//RadioButton radioButton = (RadioButton)findViewById(radioGroup.getCheckedRadioButtonId());
			//String fee = radioButton.getText().toString();
			//fee=fee.replaceAll("元", "");
			String fee="0";
			String target = myapplication.getlocalhost()+"send.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = sDateFormat.format(new Date());
			paramsList.add(new BasicNameValuePair("title", servicename));// 标题为空
			paramsList.add(new BasicNameValuePair("content", content));// 正文
			paramsList.add(new BasicNameValuePair("senduser", myapplication.getUserId()));
			paramsList.add(new BasicNameValuePair("lat", String.valueOf(nLatitude)));
			paramsList.add(new BasicNameValuePair("lng", String.valueOf(nLontitude)));
			paramsList.add(new BasicNameValuePair("country", Country));
			paramsList.add(new BasicNameValuePair("province", Province));
			paramsList.add(new BasicNameValuePair("city", City));
			paramsList.add(new BasicNameValuePair("citycode", CityCode));
			paramsList.add(new BasicNameValuePair("district", District));
			paramsList.add(new BasicNameValuePair("street", Street));
			paramsList.add(new BasicNameValuePair("guid", UUID.randomUUID().toString()));
			paramsList.add(new BasicNameValuePair("infocatagroy", String.valueOf(infocatagroy)));			
			paramsList.add(new BasicNameValuePair("fee", String.valueOf(fee)));			
			/*统一压缩*/
			for (int i = 0; i < mPicturePathList.size(); i++) {
				if (mPicturePathList.get(i).length() > 0) {
					String localpicpath = mPicturePathList.get(i);
					String compresslocalpicpath=compressBmpToFile(localpicpath,i);					
					compmPicturePathList.add(compresslocalpicpath);
				}
			}
			
			/*用户目录+压缩后的文件名*/
			picPath = "";
			for (int i = 0; i < compmPicturePathList.size(); i++) {
				if (compmPicturePathList.get(i).length() > 0) {
					String path = compmPicturePathList.get(i);					
					String picname ="/"+myapplication.getUserId()+"/"+path.substring(path.lastIndexOf("/") + 1);
					picPath = picPath + picname;
					if (i < compmPicturePathList.size() - 1) {
						picPath = picPath + ",";
					}
				}
			}
			paramsList.add(new BasicNameValuePair("photo", picPath));
			paramsList.add(new BasicNameValuePair("streetnumber", StreetNumber));
			paramsList.add(new BasicNameValuePair("village", ""));
			paramsList.add(new BasicNameValuePair("floor", Floor));
			paramsList.add(new BasicNameValuePair("address", addr));
			paramsList.add(new BasicNameValuePair("catagory", "1"));
			paramsList.add(new BasicNameValuePair("sendtime", date));
			paramsList.add(new BasicNameValuePair("networklocationtype",networklocationtype));
			paramsList.add(new BasicNameValuePair("operators", operators));
			paramsList.add(new BasicNameValuePair("direction", direction));
			paramsList.add(new BasicNameValuePair("radius", radius));
			paramsList.add(new BasicNameValuePair("speed", speed));
			try {
				UploadPic();
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,"UTF-8"));
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
		
		boolean isFolderExists(String strFolder) {
	        File file = new File(strFolder);        
	        if (!file.exists()) {
	            if (file.mkdirs()) {                
	                return true;
	            } else {
	                return false;

	            }
	        }
	        return true;

	    }
		
		public String compressBmpToFile(String filePath,int pos){
			
			SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyyMMddHHmmss"); 
			Date date = new Date(); 
			
			File afile =new File(filePath);
			String fileName=afile.getName();  
			String[] token = fileName.split("\\.");  
			String ext = token[1];  
			
			String tofilePath="";
			if(!isFolderExists(getApplicationContext().getFilesDir().getAbsolutePath()+"/temp/"))
			{
				//判断的时候已经创建
			}
			tofilePath =getApplicationContext().getFilesDir().getAbsolutePath()+"/temp/"+ bartDateFormat.format(date)+String.valueOf(pos)+"."+ext;
			Bitmap bmp=BitmapFactory.decodeFile(filePath);			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int options = 80;//个人喜欢从80开始,
			bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
			while (baos.toByteArray().length / 1024 > 100) { 
				baos.reset();
				options -= 10;
				bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
			}
			try {
				FileOutputStream fos = new FileOutputStream(tofilePath);
				fos.write(baos.toByteArray());
				fos.flush();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return tofilePath;
		}
		private void UploadPic() throws FileNotFoundException {
			for (int i = 0; i < compmPicturePathList.size(); i++) {
				if (compmPicturePathList.get(i).length() > 0) {
					String localpicpath = compmPicturePathList.get(i).toString();
					String actionUrl = myapplication.getlocalhost()+"upload.php?user="+myapplication.getUserId();//存到指定文件夹
					//压缩文件
					upLoadByAsyncHttpClient(actionUrl, localpicpath);
					Message msg = new Message();
					msg.what=1;
					Bundle data = new Bundle();
					data.putString("tip", "上传"+new File(localpicpath).getName()+",（"+(i+1) + " / " + compmPicturePathList.size()+")");
					msg.setData(data);
					showtiphandler.sendMessage(msg);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
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
				content_edit.setText("");
				pbtip.setVisibility(View.GONE);
				pbtip1.setVisibility(View.GONE);
				pb.setVisibility(View.GONE);
				finish();
			} else {
				pbtip.setText("发送失败,重新发送");
				pbtip1.setText("");
				pb.setVisibility(View.GONE);
			}
			
		}
	};
	
	
	Handler showtiphandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==1)
			{
				Bundle data = msg.getData();
				String tip = data.getString("tip");
				pbtip.setText(tip);
			}else if(msg.what==2)
			{
				Bundle data = msg.getData();
				String tip = data.getString("tip");
				pbtip1.setText(tip);

			}
		}
	};

	//private AsyncHttpClient client;
	private SyncHttpClient client;
	private void upLoadByAsyncHttpClient(String uploadUrl, String localpath)
			throws FileNotFoundException {
		AsyncBody(uploadUrl, localpath);
	}

	private void AsyncBody(String uploadUrl, String localpath)
			throws FileNotFoundException {
		RequestParams params = new RequestParams();
		client = new SyncHttpClient();
		params.put("uploadfile", new File(localpath));
		client.post(uploadUrl, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, String arg1) {
				super.onSuccess(arg0, arg1);
				Log.v("upload", arg1);
				 //progress.setProgress(0);
				/*progressDialog.setProgress(0);
				progressDialog.dismiss();*/
				Message msg = new Message();
		        msg.what=2;
				Bundle data = new Bundle();
				data.putString("tip", "已完成");
				msg.setData(data);
				showtiphandler.sendMessage(msg);
			}
			
			@Override  
	        public void onProgress(int bytesWritten, int totalSize) {  
	            // TODO Auto-generated method stub  
	            super.onProgress(bytesWritten, totalSize);  
	            int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);  
	            // 上传进度显示  	           
	            //progressDialog.setProgress(count);
	            Log.e("上传 Progress>>>>>", bytesWritten + " / " + totalSize);  
	            
	            Message msg = new Message();
	            msg.what=2;
				Bundle data = new Bundle();
				data.putString("tip", "已上传:"+bytesWritten + " / " + totalSize);
				msg.setData(data);
				showtiphandler.sendMessage(msg);
	        }  
		});
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v("onActivityResult:", requestCode + "," + resultCode);
		if (resultCode == Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO1) {
			String selpicPath = data
					.getStringExtra(SelectPhotoActivity.KEY_PHOTO_PATH);

			
			 String PATH_HOME = Environment.getExternalStorageDirectory().getPath()+"/temp/"; 
			 File dir = new File(PATH_HOME);
				if (!dir.exists()) {
					dir.mkdirs();
				}
			 String targetPath = PATH_HOME + StringUtils.toRegularHashCode(selpicPath) + ".jpg";
			 BitmapUtils.compressBitmap(selpicPath, targetPath, 640);
			
			mPicturePathList.add(targetPath);

			Bitmap bitmap = BitmapUtils.decodeBitmap(targetPath, 150);// 压缩大小
			Bundle Bundle1 = data.getExtras();
			int pos = Bundle1.getInt("pos");
			final ImageButton imageButton = mImageButtonList.get(pos);
			imageButton.setImageBitmap(bitmap);
			imageButton.setScaleType(ScaleType.FIT_XY);
			imageButton.setEnabled(false);
			if (pos < mImageButtonList.size() - 1) {
				mCurrent = pos + 1;
				final ImageButton nextImageButton = mImageButtonList
						.get(mCurrent);
				nextImageButton.setImageResource(R.mipmap.ic_add_picture);
				
				nextImageButton.setScaleType(ScaleType.CENTER);
				nextImageButton.setEnabled(true);
			}

		} 
	}

	

	
	


	



	private void print(String msg) {
		// txtLog.append(msg + "\n");
		// ScrollView sv = (ScrollView) txtLog.getParent();
		// sv.smoothScrollTo(0, 1000000);
		Log.d(TAG, "----" + msg);
	}

	

	public void doBack(View view) {
		onBackPressed();
	}

	
	 @Override
	    public void onClick(View v) {
	        switch (v.getId()) {
	            case R.id.yybtn:
	                break;	         
	            default:
	                break;
	        }

	    }
}