package com.bbxiaoqu.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.ImageOptions;
import com.bbxiaoqu.R;
import com.bbxiaoqu.ui.sub.FriendsActivity;
import com.bbxiaoqu.ui.sub.InfoGzActivity;
import com.bbxiaoqu.ui.sub.MyinfosActivity;
import com.bbxiaoqu.ui.sub.RecentActivity;
import com.bbxiaoqu.ui.sub.SettingsActivity;
import com.bbxiaoqu.ui.sub.UserInfoViewActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.nostra13.universalimageloader.core.ImageLoader;


public class DrawerView implements OnClickListener{
	private final Activity activity;
	private SlidingMenu localSlidingMenu;	
	private ImageView avatar;
	private TextView username;
	private RelativeLayout userinfo_btn;
	private RelativeLayout message_btn;
	private RelativeLayout  friends_btn;
	private RelativeLayout message_btn_my;
	private RelativeLayout message_btn_bm;
	private RelativeLayout setting_btn;
	private RelativeLayout favorite_btn;
	private RelativeLayout  feedback_btn;
	private RelativeLayout  offline_btn;
	private List<Map<String, Object>> data;
	private ListView lv;	
	private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> morelist=null;
	private Button bt;
	private ProgressBar pg;
	private Handler handler;
	private DemoApplication myapplication;
	public DrawerView(Activity activity) {
		this.activity = activity;
		myapplication = (DemoApplication) this.activity.getApplication();
	}

	public SlidingMenu initSlidingMenu() {
		localSlidingMenu = new SlidingMenu(activity);
		//localSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
		localSlidingMenu.setMode(SlidingMenu.LEFT);
		localSlidingMenu.setTouchModeAbove(SlidingMenu.SLIDING_WINDOW);
		//localSlidingMenu.setTouchModeBehind(SlidingMenu.SLIDING_CONTENT);
		localSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		localSlidingMenu.setShadowDrawable(R.drawable.shadow);
		localSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		localSlidingMenu.setFadeDegree(0.35F);
		localSlidingMenu.attachToActivity(activity, SlidingMenu.RIGHT);
		
		//localSlidingMenu.setBehindWidthRes(R.dimen.left_drawer_avatar_size);
		localSlidingMenu.setMenu(R.layout.left_drawer_fragment);
		//localSlidingMenu.toggle();
		localSlidingMenu.setSecondaryMenu(R.layout.profile_drawer_right);
		
		localSlidingMenu.setSecondaryShadowDrawable(R.drawable.shadowright);
		localSlidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
					public void onOpened() {						
					}
				});
		localSlidingMenu.setOnClosedListener(new OnClosedListener() {			
			@Override
			public void onClosed() {
				// TODO Auto-generated method stub
				
			}
		});
		initView();
		return localSlidingMenu;
	}

	private void initView() {
		
		userinfo_btn =(RelativeLayout)localSlidingMenu.findViewById(R.id.userinfo_btn);
		userinfo_btn.setOnClickListener(this);
		
		favorite_btn =(RelativeLayout)localSlidingMenu.findViewById(R.id.favorite_btn);
		favorite_btn.setOnClickListener(this);
		
		
		feedback_btn =(RelativeLayout)localSlidingMenu.findViewById(R.id.feedback_btn);
		feedback_btn.setOnClickListener(this);
		
		message_btn =(RelativeLayout)localSlidingMenu.findViewById(R.id.message_btn);
		message_btn.setOnClickListener(this);
		
		friends_btn =(RelativeLayout)localSlidingMenu.findViewById(R.id.friends_btn);
		friends_btn.setOnClickListener(this);
		
		
		
		/*message_btn_my =(RelativeLayout)localSlidingMenu.findViewById(R.id.message_btn_my);
		message_btn_my.setOnClickListener(this);

		
		message_btn_bm =(RelativeLayout)localSlidingMenu.findViewById(R.id.message_btn_bm);
		message_btn_bm.setOnClickListener(this);
*/
		
		setting_btn =(RelativeLayout)localSlidingMenu.findViewById(R.id.setting_btn);
		setting_btn.setOnClickListener(this);
		
		
		offline_btn=(RelativeLayout)localSlidingMenu.findViewById(R.id.offline_btn);
		offline_btn.setOnClickListener(this);
		
		avatar=(ImageView)localSlidingMenu.findViewById(R.id.avatar);
		username=(TextView)localSlidingMenu.findViewById(R.id.user_name);
		username.setText(myapplication.getUserId());
		if(myapplication.getheadface()!=null&&myapplication.getheadface().length()>0)
		{
			String fileName = myapplication.getlocalhost()+"uploads/" +myapplication.getheadface();
			 ImageLoader.getInstance().displayImage(fileName, avatar, ImageOptions.getOptions());  
		}
	}


	@Override
	public void onClick(View v) {	
		System.out.println(v.getId());
		switch (v.getId())
		{
			case R.id.userinfo_btn:
				activity.startActivity(new Intent(activity,UserInfoViewActivity.class));
				activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);				
				break;
			case R.id.favorite_btn:
				activity.startActivity(new Intent(activity,RecentActivity.class));				
				activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				break;
			case R.id.friends_btn:
				activity.startActivity(new Intent(activity,FriendsActivity.class));				
				activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				break;
			case R.id.feedback_btn:
				activity.startActivity(new Intent(activity,MyinfosActivity.class));
				activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				break;
			case R.id.message_btn:
				activity.startActivity(new Intent(activity,InfoGzActivity.class));
				activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);			
				break;	
			case R.id.setting_btn:
				activity.startActivity(new Intent(activity,SettingsActivity.class));
				activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				break;
			case R.id.offline_btn:
				myapplication.exit();
				break;
			default:
				break;
		}
	}

}
