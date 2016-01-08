package com.bbxiaoqu.view;


import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.Session;
import com.bbxiaoqu.client.xmpp.MessageService;
import com.bbxiaoqu.comm.tool.NetworkUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class BaseActivity extends Activity {

	private DemoApplication myapplication;
	GestureDetector mGestureDetector;
	protected Session mSession;
	private boolean mNeedBackGesture = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initGestureDetector();
		final Context context = getApplicationContext();
	    mSession = Session.get(context);
	    myapplication = (DemoApplication) this.getApplication();
	    if(myapplication.ifpass(myapplication.getUserId()))
	    {
	    	new Thread(xmppstartRun).start();
	    }
	}

	
	Runnable xmppstartRun = new Runnable() {
		@Override
		public void run() {
			if (NetworkUtils.isNetConnected(BaseActivity.this)) 
			{
				Intent intent = new Intent(BaseActivity.this,MessageService.class);								
				startService(intent);
			}
		}
	};
	
	private void initGestureDetector() {
		if (mGestureDetector == null) {
			mGestureDetector = new GestureDetector(getApplicationContext(),
					new BackGestureListener(this));
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if(mNeedBackGesture){
			return mGestureDetector.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}

	public void setNeedBackGesture(boolean mNeedBackGesture){
		this.mNeedBackGesture = mNeedBackGesture;
	}
	  /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        final Context context = getApplicationContext();
        mSession = Session.get(context);
        //Collector.onResume(context);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        //Collector.onPause(getApplicationContext());
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onLowMemory()
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
    
	public void doBack(View view) {
		onBackPressed();
	}

}
