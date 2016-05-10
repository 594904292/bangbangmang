package com.bbxiaoqu.ui.user;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
import com.bbxiaoqu.ui.sub.PayActivity;
import com.bbxiaoqu.ui.sub.RoundAngleImageView;
import com.bbxiaoqu.view.BaseActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class UserInfoViewActivity extends BaseActivity implements OnClickListener {
	private DemoApplication myapplication;
	TextView title;
	TextView txt_userid;
	TextView tv_score;
	private TextView username;
	private TextView age;
	//private TextView community;
	private TextView telphone;
	
	private String sex_str = "1";
	private TextView txt=null;
	private RadioGroup sex=null;
	private RadioButton male=null;
	private RadioButton female=null;
	
	Button save;
	Button score_btn;
	
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

	UserService uService = new UserService(UserInfoViewActivity.this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfoview);
		myapplication = (DemoApplication) this.getApplication();
		initView();
		initData();
	}
    private static final String[] sexs ={ " 男 " , "女" };
	private void initView() {
		title = (TextView) findViewById(R.id.title);
		username = (TextView) findViewById(R.id.username);
		age = (TextView) findViewById(R.id.age);
		//community = (TextView) findViewById(R.id.community);
		telphone = (TextView) findViewById(R.id.telphone);
		save = (Button) findViewById(R.id.save);
		score_btn  = (Button) findViewById(R.id.score_btn);
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
				Intent intent=new Intent(UserInfoViewActivity.this,SearchActivity.class);									
				startActivity(intent);
			}
		});
		score_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent(UserInfoViewActivity.this,PayActivity.class);
				startActivity(intent);
			}
		});
		save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!NetworkUtils.isNetConnected(myapplication)) {			
					T.showShort(myapplication, "当前无网络连接,请稍后再试！");
					return;
				}
				//更新本地库
				startActivity(new Intent(UserInfoViewActivity.this,UserInfoActivity.class));
			}
		});
		iv_photo = (RoundAngleImageView) findViewById(R.id.iv_photo);
		
	}

	private void initData() {
		title.setText("用户中心");
		if (!NetworkUtils.isNetConnected(myapplication)) {			
			T.showShort(myapplication, "当前无网络连接,请稍后再试！");
			return;
		}
		new Thread(loaduserinfo).start();		
	}

	Runnable loaduserinfo = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String username = "";
			String age = "";
			String sex = "";
			String telphone = "";
			String remote_headface = "";
			//String community="";
			String target = myapplication.getlocalhost()+"getuserinfo.php?userid="+ myapplication.getUserId();
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
						remote_headface = jsonobject.getString("headface");
						//community = jsonobject.getString("community");
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
				data.putString("sex", sex);
				data.putString("telphone", telphone);
				data.putString("headface", remote_headface);
				//data.putString("community", community);
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
			if(data.getString("sex").equals("1"))
			{
				male.setChecked(true);
			}else
			{
				female.setChecked(true);
			}
//			if(!data.getString("community").equals("null")&&data.getString("community").length()>0)
//			{
//				community.setText(data.getString("community"));
//			}else
//			{
//				community.setText("");
//			}
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
