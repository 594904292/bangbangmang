package com.bbxiaoqu.client.xmpp;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.OfflineMessageManager;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.T;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

public class MessageService extends Service {

	private ChatManager cm;
	private OfflineMessageManager offlineManager;
	public static XMPPConnection con = null;
	static {
		try {
			Class.forName("org.jivesoftware.smack.ReconnectionManager");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	XMPPConnection connection=null;
	@Override
	public void onCreate() {
		// Log.d(TAG, "============> TService.onCreate");
		/*notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		showNotification();*/
		// connection = XmppTool.getInstance().getConnection();
		super.onCreate();
		
	}

	public void onStart(Intent intent, int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
					while(true)
					{
						if (NetworkUtils.isNetConnected(MessageService.this)) 
						{							
							System.out.println("-----------");								
							connection = XmppTool.getInstance(MessageService.this).getConnection();
							XmppTool.getInstance(MessageService.this).login();
							/*if(connection!=null)
							{
								if (connection.isConnected() && connection.isAuthenticated()) {	
									Presence presence = new Presence(Presence.Type.available);
									connection.sendPacket(presence);//在线
									
									ChatManager cm = connection.getChatManager(); // 取得聊天管理器
									cm.addChatListener(ChatListener.getInstance(MessageService.this));
								}
							}else
							{
								try {
									Thread.sleep(1000*5);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								continue;
							}*/
							
							try {
								Thread.sleep(1000*5);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						try {
							Thread.sleep(1000*60*5);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			}
		}).start();

		//objHandler.postDelayed(mTasks, 1000);
		super.onStart(intent, startId);

	}

	public class LocalBinder extends Binder {
		public MessageService getService() {
			return MessageService.this;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	public boolean onUnbind(Intent intent) {
		// Log.i(TAG, "============> TService.onUnbind");
		return false;
	}

	public void onRebind(Intent intent) {
		// Log.i(TAG, "============> TService.onRebind");
	}

	public void onDestroy() {
		//notificationManager.cancel(1000);
		//objHandler.removeCallbacks(mTasks);
		if(connection!=null)
		{
			XmppTool.getInstance(MessageService.this).closeConnection();
		}
		super.onDestroy();
	}
/*
	private void showNotification() {
		Notification notification = new Notification(R.drawable.icon,
				"SERVICE START", System.currentTimeMillis());

		Intent intent = new Intent(this, MessageService.class);
		intent.putExtra("FLG", 1);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, 0);

		notification.setLatestEventInfo(this, "SERVICE", "SERVICE START",
				contentIntent);
		notificationManager.notify(1000, notification);
	}*/

}
