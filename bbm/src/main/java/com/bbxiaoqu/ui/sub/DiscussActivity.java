package com.bbxiaoqu.ui.sub;

import java.util.Map;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class DiscussActivity  extends Activity{
	private DemoApplication myapplication;
	private ListView listView;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		  getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	         
	        listView = (ListView)findViewById(R.id.group_discuss_list);
	       // this.discussOnItemClickListener();
		
		
		
	}
	
	
}
