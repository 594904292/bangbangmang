package com.bbxiaoqu.ui;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.R.layout;
import com.bbxiaoqu.comm.service.db.UserService;
import com.bbxiaoqu.ui.fragment.HomeActivity;
import com.bbxiaoqu.ui.main.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class Welcome extends Activity {
	private DemoApplication myapplication;
	private AlphaAnimation start_anima;
	View view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		view = View.inflate(this, R.layout.welcome, null);
		setContentView(view);
		 myapplication = (DemoApplication) this.getApplication();
		initView();
		initData();
	}
	private void initData() {
		start_anima = new AlphaAnimation(0.3f, 1.0f);
		start_anima.setDuration(2000);
		view.startAnimation(start_anima);
		start_anima.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				redirectTo();
			}
		});
	}
	
	private void initView() {
		
	}

	private void redirectTo() {		
		UserService uService = new UserService(Welcome.this);
		String[] arr=uService.getonlineuserid();
		if(arr==null)
		{
			startActivity(new Intent(getApplicationContext(), LoginActivity.class));
			finish();
		}else
		{
			//PPConnection.DEBUG_ENABLED = true;
			//conServer();
			
			String userid=arr[0];
			myapplication.setUserId(userid);
			uService.online(userid);//更改状态
			Intent intent=new Intent(Welcome.this,MainActivity.class);
			startActivity(intent);
		}
		
	}
	
	/**
	 * 连接服务器
	 * 
	 * @return
	 */
	public boolean conServer() {
		XMPPConnection connection;
		ConnectionConfiguration config = new ConnectionConfiguration("101.200.194.1", 5222);	//新建连接配置对象，设置服务器IP和监听端口
		/** 是否启用安全验证 */
		config.setSASLAuthenticationEnabled(false);
		/** 是否启用调试 */
		// config.setDebuggerEnabled(true);
		/** 创建connection链接 */
		try {
			connection = new XMPPConnection(config);
			/** 建立连接 */
			connection.connect();
			
			connection.login("admin", "admin");
			ChatManager cm = connection.getChatManager(); 	//取得聊天管理器
			Chat chat = cm.createChat("369@iz25u7vq72az", null);	//得到与另一个帐号的连接，这里是一对一,@后面是你安装openfire时注册的域
			
			/*
			 * 添加监听器
			 */
			cm.addChatListener(new ChatManagerListener() {				
				@Override
				public void chatCreated(Chat chat, boolean create) {
					chat.addMessageListener(new MessageListener() {
						
						@Override
						public void processMessage(Chat chat, Message msg) {
							System.out.println(chat.getParticipant() + ":" + msg.getBody());
							try {
								chat.sendMessage("你刚才说的是："+msg.getBody());		//发送消息
							} catch (XMPPException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}
			});
			
			chat.sendMessage("hello");		//发送消息			
			 connection.getAccountManager().deleteAccount();    
			return true;
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		return false;
	}
	/*private void test() {
		AccountManager accountManager;
		final ConnectionConfiguration connectionConfig =  new ConnectionConfiguration("101.200.194.1", 9090);
		// 允许自动连接
		connectionConfig.setReconnectionAllowed(true);
		//connectionConfig.setSendPresence(true);

		XMPPConnection connection = new XMPPConnection(connectionConfig);
		try {
			connection.connect();// 开启连接
			accountManager = connection.getAccountManager();// 获取账户管理类
		} catch (XMPPException e) {
			throw new IllegalStateException(e);
		}

		// 登录
		try {
			connection.login("admin", "admin");
			System.out.println(connection.getUser()); 
			connection.getChatManager().createChat("369@bbxiaoqu.com",null).sendMessage("Hello word!");
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
