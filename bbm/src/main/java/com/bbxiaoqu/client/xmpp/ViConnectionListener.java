package com.bbxiaoqu.client.xmpp;

import java.util.Timer;
import java.util.TimerTask;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import android.content.Context;
import android.os.Build;
import android.util.Log;


public class ViConnectionListener implements ConnectionListener {
	private Timer tExit;
	private String username;
	private String password;
	private int logintime = 2000;
	/** Application Context */
    private Context mContext;
    
    
    ViConnectionListener(Context context) {
        mContext = context;
    }

	@Override
	public void connected(XMPPConnection connection) {
		Log.i("XmppConnection", "連接成功");
	}

	@Override
	public void authenticated(XMPPConnection connection, boolean resumed) {
		Log.i("XmppConnection", "验证成功");
	}

	@Override
	public void connectionClosed() {
		System.out.println("connectionClosed--->");
		Log.i("TaxiConnectionListener", "連接關閉");  
	    // 關閉連接  
		XmppTool.getInstance(mContext).closeConnection();
	    // 重连服务器  
	    tExit = new Timer();  
	    tExit.schedule(new timetask(), logintime);  
	}
	


	@Override
	public void connectionClosedOnError(Exception e) {
		System.out.println("connectionClosedOnError--->");
		// 这里就是网络不正常或者被挤掉断线
		if (e.getMessage().contains("conflict")) { // 被挤掉线
			Log("Connection conflict");
			// 关闭连接，由于是被人挤下线，可能是用户自己，关闭连接，让用户重新登录是一个比较好的
			// XmppTool.getInstance().closeConnection();
			XmppTool.getInstance(mContext).closeConnection();
			// 重连服务器
			tExit = new Timer();
			tExit.schedule(new timetask(), logintime);
			
		} else if (e.getMessage().contains("Connection timed out")) {// 连接超时
			// 不做任何操作，会实现自动重连
			Log("Connection timed out");
			XmppTool.getInstance(mContext).closeConnection();
			// 接下来你可以通过发�?�?��广播，提示用户被挤下线，重连很简单，就是重新登录
			// 重连服务器
			tExit = new Timer();
			tExit.schedule(new timetask(), logintime);
		}
	}

	@Override
	public void reconnectingIn(int arg0) {
		Log("reconnectingIn--->");
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
		Log("reconnectionFailed--->" + arg0.getMessage());
		arg0.printStackTrace();
	}

	@Override
	public void reconnectionSuccessful() {
		Log("reconnectionSuccessful--->");
	}

	private void Log(String msg) {
		System.out.println(msg);
	}
	
	class timetask extends TimerTask {
		@Override
		public void run() {			
			XmppTool.getInstance(mContext).getConnection();
			XmppTool.getInstance(mContext).login();
		}
	}
}