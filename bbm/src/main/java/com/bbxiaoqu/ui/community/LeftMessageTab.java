package com.bbxiaoqu.ui.community;

import com.bbxiaoqu.R;
import com.bbxiaoqu.ui.sub.InfoBmActivity;
import com.bbxiaoqu.ui.sub.InfoGzActivity;
import com.bbxiaoqu.ui.sub.InfoMyActivity;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class LeftMessageTab extends TabActivity {
    private TabHost m_tabHost;
    /** Called when the activity is first created. */
    TextView title ;
    TextView right_text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_tabs);
        initView();
        initData();
        //getTabHost返回的TabHost用于装载tabs
        m_tabHost = getTabHost();

        //add tabs,这里用于添加具体的Tabs,并用Tab触发相应的Activity
        addOneTab();
        addTwoTab();
        addThreeTab();
        //设置TabHost的背景颜色
        m_tabHost.setBackgroundColor(Color.argb(150, 22, 70, 150));
        //设置TabHost的背景图片资源
        m_tabHost.setBackgroundResource(R.mipmap.select_photo_up_bg);
    }

    public void addOneTab(){
        Intent intent = new Intent();
        intent.setClass(LeftMessageTab.this, InfoMyActivity.class);

       /* TabSpec spec = m_tabHost.newTabSpec("One");
        spec.setIndicator("已发送", getResources().getDrawable(R.drawable.tab_message));
        spec.setContent(intent);
        //spec.setBackgroundColor(Color.parseColor("#ECE2C2"));
*/

        View view1 = this.getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView image = (ImageView) view1.findViewById(R.id.icon);
        image.setImageResource(R.mipmap.tab_message);
		TextView tv1 = (TextView) view1.findViewById(R.id.tv);
		tv1.setText("已发送");
		view1.setBackgroundColor(Color.argb(150, 22, 70, 150));

        TabSpec spec = m_tabHost.newTabSpec("One");
        spec.setIndicator(view1);
        spec.setContent(intent);

        m_tabHost.addTab(spec);
    }

    public void addTwoTab(){
        Intent intent = new Intent();
        intent.setClass(LeftMessageTab.this, InfoGzActivity.class);

        View view1 = this.getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView image = (ImageView) view1.findViewById(R.id.icon);
        image.setImageResource(R.mipmap.tab_message);
		TextView tv1 = (TextView) view1.findViewById(R.id.tv);
		tv1.setText("关注");
		view1.setBackgroundColor(Color.argb(150, 22, 70, 150));


        TabSpec spec = m_tabHost.newTabSpec("Two");
        spec.setIndicator(view1);
        spec.setContent(intent);
        m_tabHost.addTab(spec);
    }

    public void addThreeTab(){
        Intent intent = new Intent();
        intent.setClass(LeftMessageTab.this, InfoBmActivity.class);

        View view1 = this.getLayoutInflater().inflate(R.layout.customtab, null);
        ImageView image = (ImageView) view1.findViewById(R.id.icon);
        image.setImageResource(R.mipmap.tab_message);
		TextView tv1 = (TextView) view1.findViewById(R.id.tv);
		tv1.setText("报名信息");
		view1.setBackgroundColor(Color.argb(150, 22, 70, 150));


        TabSpec spec = m_tabHost.newTabSpec("Three");
        //spec.setIndicator("报名信息", getResources().getDrawable(R.drawable.tab_message));
        spec.setIndicator(view1);
        spec.setContent(intent);
        m_tabHost.addTab(spec);
    }

	private void initView() {
		title = (TextView) findViewById(R.id.title);
		right_text = (TextView) findViewById(R.id.right_text);
		right_text.setVisibility(View.GONE);

	}

	private void initData() {
		title.setText("我的信息");
		right_text.setText("");
	}

    public void doBack(View view) {
		onBackPressed();
	}

}
