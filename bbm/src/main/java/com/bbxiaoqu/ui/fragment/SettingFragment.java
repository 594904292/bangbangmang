package com.bbxiaoqu.ui.fragment;



import com.bbxiaoqu.R;
import com.bbxiaoqu.ui.sub.UserInfoActivity;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class SettingFragment extends Fragment implements OnClickListener{
	private RelativeLayout userinfo_btn;
	private RelativeLayout message_btn;
	private RelativeLayout favorite_btn;
	private RelativeLayout  feedback_btn;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View settingLayout = inflater.inflate(R.layout.setting_layout,
				container, false);
		
		userinfo_btn =(RelativeLayout)settingLayout.findViewById(R.id.userinfo_btn);
		userinfo_btn.setOnClickListener(this);
		
		favorite_btn =(RelativeLayout)settingLayout.findViewById(R.id.favorite_btn);
		favorite_btn.setOnClickListener(this);
		
		
		feedback_btn =(RelativeLayout)settingLayout.findViewById(R.id.feedback_btn);
		feedback_btn.setOnClickListener(this);
		
		message_btn =(RelativeLayout)settingLayout.findViewById(R.id.message_btn);
		message_btn.setOnClickListener(this);
		
		return settingLayout;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		System.out.println(v.getId());
		switch (v.getId())
		{
			case R.id.userinfo_btn:
				this.getActivity().startActivity(new Intent(this.getActivity(),UserInfoActivity.class));
				this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);				
				break;
			case R.id.favorite_btn:
/*				this.getActivity().startActivity(new Intent(this.getActivity(),CommunityTab.class));				
				this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
*/				break;
			case R.id.feedback_btn:
/*				this.getActivity().startActivity(new Intent(this.getActivity(),AddInterestActivity.class));
				this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
*/				break;
			case R.id.message_btn:
				//this.getActivity().startActivity(new Intent(this.getActivity(),LeftMessageTab.class));
				//this.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				break;				
			default:
				break;
		}
	}

}
