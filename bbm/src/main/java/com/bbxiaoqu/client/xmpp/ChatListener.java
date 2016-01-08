package com.bbxiaoqu.client.xmpp;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.bean.BbMessage;
import com.bbxiaoqu.bean.ChatMessage;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver;
import com.bbxiaoqu.comm.service.User;
import com.bbxiaoqu.comm.service.db.ChatDB;
import com.bbxiaoqu.comm.service.db.FriendDB;
import com.bbxiaoqu.comm.service.db.NoticeDB;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.ui.LoginActivity;
import com.bbxiaoqu.ui.main.MainActivity;

public class ChatListener implements ChatManagerListener{
	private Context mContext;
	public ChatListener(Context context)
	{
		this.mContext = context;
	}
	
	
	  private static ChatListener instance;  
	  private ChatListener (){}   
	  public static ChatListener getInstance(Context context) {  
	  if (instance == null) {  
	           instance = new ChatListener(context);  
	       }  
	       return instance;  
	       }  
	
	@Override
	public void chatCreated(Chat chat, boolean create) {
		chat.addMessageListener(new MessageListener() {
			@Override
			public void processMessage(Chat chat,
					org.jivesoftware.smack.packet.Message msg) {
				// TODO Auto-generated method stub
				System.out.println(chat.getParticipant() + ":" + msg.getBody());
				/*
				try {
					chat.sendMessage("你刚才说的是："+msg.getBody());		//发送消息
				} catch (XMPPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				String from=msg.getFrom().toString().split("@")[0];
				String to=msg.getTo().toString().split("@")[0];				
				if(from.equals(DemoApplication.getInstance().getUserId()))
				{//不处理自己的消息
					return;
				}else if(from.equals("admin"))
				{//广播消息
					
					
				}else {
					ChatMessage mess=new ChatMessage();
					mess.setSenduserId(from);
					mess.setTouserId(to);
					mess.setGuid(msg.getPacketID());
					mess.setMessage(msg.getBody());
			        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		       
					mess.setDate(new Date());
					
					mess.setDateStr(df.format(new Date()));			        						 
					NoticeDB noticedb=new NoticeDB(mContext);	
					ChatDB chatdb=new ChatDB(mContext);
					//if(!chatdb.isexit(mess.getGuid()))
				    //{
				        	chatdb.add(mess);		        			        
				    //}
					//if(!noticedb.isexit(mess.getGuid()))
				    //{
						//查询chat表,form用户未读数据
						if(noticedb.unreadnum(from,"私信")==0)
						{//不存在这人的私信
							noticedb.add(df.format(new Date()), "私信", from, from+"发送了一条私信","0");
						}else
						{//已经有一条通知了
							long num=chatdb.unreadnum(from,to);						
							noticedb.updateusercontent(from, from+"发送了"+num+"条私信");
						}
				    //}
				    FriendDB fb=new FriendDB(mContext);
				    //fb.removall();
				    if(!fb.isexit(from))
				    {//添加朋友联系人
				    	
				    	
				    	fb.add(from, "", "",from, "", msg.getBody(), df.format(new Date()), 0);
				    	GetUserThread thread = new GetUserThread(mContext,from);
				        thread.start();       
				    }else
				    {//更新最后联系人
				    	
				    }
				    //直接查数据库中信息
				    fb.newmess(from, from,msg.getBody(), df.format(new Date()));
			        for (int i = 0; i < BbPushMessageReceiver.msgListeners.size(); i++)
			        	BbPushMessageReceiver.msgListeners.get(i).onNewMessage(new BbMessage());
				}
			}

			
		});
		
	}
	
	
}
