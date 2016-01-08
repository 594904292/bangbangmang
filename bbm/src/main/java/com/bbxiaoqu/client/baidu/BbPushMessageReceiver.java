package com.bbxiaoqu.client.baidu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.android.pushservice.PushMessageReceiver;
import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.bean.BbMessage;
import com.bbxiaoqu.bean.ChatMessage;
import com.bbxiaoqu.comm.service.db.ChatDB;
import com.bbxiaoqu.comm.service.db.FriendDB;
import com.bbxiaoqu.comm.service.db.MessageDB;
import com.bbxiaoqu.comm.service.db.NoticeDB;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.ui.fragment.HomeActivity;

/*
 * Push消息处理receiver。请编写您需要的回调函数， 一般来说： onBind是必须的，用来处理startWork返回值；
 *onMessage用来接收透传消息； onSetTags、onDelTags、onListTags是tag相关操作的回调；
 *onNotificationClicked在通知被点击时回调； onUnbind是stopWork接口的返回值回调

 * 返回值中的errorCode，解释如下：
 *0 - Success
 *10001 - Network Problem
 *10101  Integrate Check Error
 *30600 - Internal Server Error
 *30601 - Method Not Allowed
 *30602 - Request Params Not Valid
 *30603 - Authentication Failed
 *30604 - Quota Use Up Payment Required
 *30605 -Data Required Not Found
 *30606 - Request Time Expires Timeout
 *30607 - Channel Token Timeout
 *30608 - Bind Relation Not Found
 *30609 - Bind Number Too Many

 * 当您遇到以上返回错误时，如果解释不了您的问题，请用同一请求的返回值requestId和errorCode联系我们追查问题。
 *
 */

public class BbPushMessageReceiver extends PushMessageReceiver {
    /** TAG to Log */
    public static final String TAG = BbPushMessageReceiver.class
            .getSimpleName();

    
    public static int mNewNum = 0;// 通知栏新消息条目，我只是用了一个全局变量，
	public static final int NOTIFY_ID = 0x000;
	
	public static ArrayList<onMessageReadListener> msgReadListeners = new ArrayList<onMessageReadListener>();
    public static ArrayList<onNewMessageListener> msgListeners = new ArrayList<onNewMessageListener>();//在使用界面上添加  	
  
  	
  	public static interface onNewMessageListener
  	{
  		public abstract void onNewMessage(BbMessage message);
  	}

	public static interface onMessageReadListener
  	{
  		public abstract void onReadMessage();
  	}

  	

	/**
	 * Push消息处理receiver。请编写您需要的回调函数， 一般来说： onBind是必须的，用来处理startWork返回值；
	 * onMessage用来接收透传消息； onSetTags、onDelTags、onListTags是tag相关操作的回调；
	 * onNotificationClicked在通知被点击时回调； onUnbind是stopWork接口的返回值回调
	 * 
	 * 返回值中的errorCode，解释如下： 
	 *  0 - Success
	 *  10001 - Network Problem
	 *  30600 - Internal Server Error
	 *  30601 - Method Not Allowed 
	 *  30602 - Request Params Not Valid
	 *  30603 - Authentication Failed 
	 *  30604 - Quota Use Up Payment Required 
	 *  30605 - Data Required Not Found 
	 *  30606 - Request Time Expires Timeout 
	 *  30607 - Channel Token Timeout 
	 *  30608 - Bind Relation Not Found 
	 *  30609 - Bind Number Too Many
	 * 
	 * 当您遇到以上返回错误时，如果解释不了您的问题，请用同一请求的返回值requestId和errorCode联系我们追查问题。
	 * 
	 */
    /**
     * 调用PushManager.startWork后，sdk将对push
     * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
     * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     *
     * @param context
     *            BroadcastReceiver的执行Context
     * @param errorCode
     *            绑定接口返回值，0 - 成功
     * @param appid
     *            应用id。errorCode非0时为null
     * @param userId
     *            应用user id。errorCode非0时为null
     * @param channelId
     *            应用channel id。errorCode非0时为null
     * @param requestId
     *            向服务端发起的请求id。在追查问题时有用；
     * @return none
     */
    @Override
    public void onBind(final Context context, int errorCode, String appid,
            String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

        if (errorCode == 0) {
            // 绑定成功
        	Utils.setBind(context, true);
            DemoApplication myapplication=(DemoApplication) context.getApplicationContext();        	
          	myapplication.setAppid(appid);
          	//myapplication.setUserId(userId);
          	//myapplication.setPushuserId(userId);
          	myapplication.setChannelId(channelId);
          	myapplication.setRequestId(requestId);
          	myapplication.update();
        }else
        {
        	if (NetworkUtils.isNetConnected(context))
        	{
        		//T.showLong(context, "启动失败，正在重试...");
        		new Handler().postDelayed(new Runnable()
        		{
        						@Override
        						public void run()
        						{
        							PushManager.startWork(context,
        									PushConstants.LOGIN_TYPE_API_KEY,
        									DemoApplication.API_KEY);
        						}
        		}, 2000);// 两秒后重新开始验证
        	} else
        	{
        					//T.showLong(context, R.string.net_error_tip);
        	}
        }
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        //updateContent(context, responseString);
     	/*for (int i = 0; i < bindListeners.size(); i++)
     		bindListeners.get(i).onBind(userId, errorCode);*/
    }
    /**
     * 接收透传消息的函数。
     *
     * @param context
     *            上下文
     * @param message
     *            推送的消息
     * @param customContentString
     *            自定义内容,为空或者json字符串
     */
    @Override
    public void onMessage(Context context, String message,
            String customContentString) {
        String messageString = "透传消息 message=\"" + message + "\" customContentString=" + customContentString;
        Log.d(TAG, messageString);
        JSONObject customJson = null;   
        NoticeDB noticedb=new NoticeDB(context);	
        try {
			customJson = new JSONObject(message);				
			if(!customJson.isNull("catagory")&&customJson.getString("catagory").equals("order"))
			{//订单:知道具帮帮忙的信息号生成的订单											  
			    noticedb.add(customJson.getString("date").toString(), "订单", customJson.getString("guid").toString(), customJson.getString("guid").toString()+"接受"+customJson.getString("userid").toString()+"的帮助","0");
		        for (int i = 0; i < msgListeners.size(); i++)
		        	msgListeners.get(i).onNewMessage(new BbMessage());		
			}else  if(!customJson.isNull("catagory")&&customJson.getString("catagory").equals("chat"))
			{//聊天:知道具体人
				//chat(context, customJson, noticedb);走自己的xmpp,关闭		
			}else  if(!customJson.isNull("catagory")&&customJson.getString("catagory").equals("pl"))
			{//评论:知道具体哪条新闻,评论可以有多条				
			  	BbMessage mess=new BbMessage();			       
		        mess.setGuid(customJson.getString("guid").toString());		       
		        noticedb.add(customJson.getString("date").toString(), "评论", customJson.getString("guid").toString(), "一条新评论","0");
		        for (int i = 0; i < msgListeners.size(); i++)
		        	msgListeners.get(i).onNewMessage(mess);
			}else
			{//新闻:知道具体哪条新闻					
				  	BbMessage mess=new BbMessage();			       
			        mess.setGuid(customJson.getString("guid").toString());			        
			        if(!noticedb.isexit(mess.getGuid()))
			        {
			        	noticedb.add(customJson.getString("date").toString(), "消息", customJson.getString("guid").toString(), "一条新消息","0");
			        }			       
			        for (int i = 0; i < msgListeners.size(); i++)
			        	msgListeners.get(i).onNewMessage(new BbMessage());
			}
			       
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        /*updateContent(context, messageString);*/
    }
	private void chat(Context context, JSONObject customJson, NoticeDB noticedb)
			throws JSONException {
		ChatMessage mess=new ChatMessage();
		mess.setSenduserId(customJson.getString("senduserid").toString());
		mess.setSendnickname(customJson.getString("sendnickname").toString());
		mess.setSenduserIcon(customJson.getString("sendusericon").toString());					
		mess.setTouserId(customJson.getString("touserid").toString());
		mess.setTonickname(customJson.getString("tonickname").toString());
		mess.setTouserIcon(customJson.getString("tousericon").toString());
		mess.setGuid(customJson.getString("guid").toString());
		mess.setMessage(customJson.getString("message").toString());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		       
		try {
			mess.setDate(df.parse(customJson.getString("date").toString()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mess.setDateStr(customJson.getString("date").toString());			        						 
		if(!noticedb.isexit(mess.getGuid()))
		{
			noticedb.add(customJson.getString("date").toString(), "私信", customJson.getString("senduserid").toString(), customJson.getString("sendnickname").toString()+"发送了一条私信","0");
		}
		FriendDB fb=new FriendDB(context);
		//fb.removall();
		if(!fb.isexit(customJson.getString("senduserid")))
		{//添加朋友联系人
			fb.add(customJson.getString("senduserid").toString(), customJson.getString("sendnickname").toString(), customJson.getString("sendusericon").toString(),customJson.getString("senduserid").toString(), customJson.getString("sendnickname").toString(), customJson.getString("message").toString(), customJson.getString("date").toString(), 0);
		}
		
		fb.newmess(customJson.getString("senduserid").toString(),customJson.getString("sendusericon").toString(),customJson.getString("message").toString(), customJson.getString("date").toString());
		
		ChatDB db=new ChatDB(context);
		if(!db.isexit(mess.getGuid()))
		{
			db.add(mess);		        			        
		}
		for (int i = 0; i < msgListeners.size(); i++)
			msgListeners.get(i).onNewMessage(new BbMessage());
	}
    
    
/*    public void onMessage(Context context, String message,
            String customContentString) {
        String messageString = "透传消息 message=\"" + message + "\" customContentString=" + customContentString;
        Log.d(TAG, messageString);
        JSONObject customJson = null;        
        try {
			customJson = new JSONObject(message);				
			if(!customJson.isNull("catagory")&&customJson.getString("catagory").equals("order"))
			{//订单				
				NoticeDB noticedb=new NoticeDB(context);				  
			    noticedb.add(customJson.getString("date").toString(), "订单", customJson.getString("guid").toString(), customJson.getString("guid").toString()+"接受"+customJson.getString("userid").toString()+"的帮助","0");
		        for (int i = 0; i < msgListeners.size(); i++)
		        	msgListeners.get(i).onNewMessage(new BbMessage());
		
			}else  if(!customJson.isNull("catagory")&&customJson.getString("catagory").equals("chat"))
			{//聊天
				ChatMessage mess=new ChatMessage();
				mess.setSenduserId(customJson.getString("senduserid").toString());
				mess.setSendnickname(customJson.getString("sendnickname").toString());
				mess.setSenduserIcon(customJson.getString("sendusericon").toString());
					
				mess.setTouserId(customJson.getString("touserid").toString());
				mess.setTonickname(customJson.getString("tonickname").toString());
				mess.setTouserIcon(customJson.getString("tousericon").toString());

				mess.setGuid(customJson.getString("guid").toString());
				mess.setMessage(customJson.getString("message").toString());
		        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		       
				try {
					mess.setDate(df.parse(customJson.getString("date").toString()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mess.setDateStr(customJson.getString("date").toString());			        
				NoticeDB noticedb=new NoticeDB(context);				  
			    //if(!noticedb.isexit(mess.getGuid()))
			    //{
			    	noticedb.add(customJson.getString("date").toString(), "私信", customJson.getString("senduserid").toString(), customJson.getString("sendnickname").toString()+"发送了一条私信","0");
			    //}			        
		        ChatDB db=new ChatDB(context);
		        if(!db.isexit(mess.getGuid()))
		        {
		        	db.add(mess);
		        }
		        for (int i = 0; i < msgListeners.size(); i++)
		        	msgListeners.get(i).onNewMessage(new BbMessage());
		
			}else
			{//新闻
					
				  	BbMessage mess=new BbMessage();
			        mess.setMessage(customJson.getString("description").toString());
			        mess.setSenduserId(customJson.getString("senduserid").toString());
			        mess.setSendnickname(customJson.getString("sendnickname").toString());
			        mess.setCommunity(customJson.getString("community").toString());
			        mess.setAddress(customJson.getString("address").toString());
			        mess.setLat(Double.parseDouble(customJson.getString("lat").toString()));
			        mess.setLng(Double.parseDouble(customJson.getString("lng").toString()));
			        mess.setGuid(customJson.getString("guid").toString());
			        mess.setInfocatagroy(customJson.getString("infocatagroy").toString());
			        mess.setIcon(customJson.getString("icon").toString());			        
			        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						try {
							mess.setDate(df.parse(customJson.getString("date").toString()));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
			        mess.setDateStr(customJson.getString("date").toString());	
			        mess.setComing(true);
			        mess.setReaded(false);
			        NoticeDB noticedb=new NoticeDB(context);
			        if(!noticedb.isexit(mess.getGuid()))
			        {
			        	noticedb.add(customJson.getString("date").toString(), "消息", customJson.getString("guid").toString(), "一条新消息","0");
			        }
			        MessageDB db=new MessageDB(context);
			        if(!db.isexit(mess.getGuid()))
			        {
			        	db.add(mess);
			        }
			        for (int i = 0; i < msgListeners.size(); i++)
			        	msgListeners.get(i).onNewMessage(mess);
				}
			       
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
     
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        updateContent(context, messageString);
    }*/

    /**
     * 接收通知点击的函数。
     *
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationClicked(Context context, String title,
            String description, String customContentString) {
        String notifyString = "通知点击 title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        Log.d(TAG, notifyString);

        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Intent intent = new Intent();
        intent.setClass(context.getApplicationContext(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        //updateContent(context, notifyString);
    }
    
    
    

    /**
     * 接收通知到达的函数。
     *
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
     */

    @Override
    public void onNotificationArrived(Context context, String title,
            String description, String customContentString) {

        String notifyString = "onNotificationArrived  title=\"" + title
                + "\" description=\"" + description + "\" customContent="
                + customContentString;
        Log.d(TAG, notifyString);

        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        // 你可以參考 onNotificationClicked中的提示从自定义内容获取具体值
        updateContent(context, notifyString);
    }

    /**
     * setTags() 的回调函数。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
     * @param successTags
     *            设置成功的tag
     * @param failTags
     *            设置失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onSetTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        updateContent(context, responseString);
    }

    /**
     * delTags() 的回调函数。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
     * @param successTags
     *            成功删除的tag
     * @param failTags
     *            删除失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onDelTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        updateContent(context, responseString);
    }

    /**
     * listTags() 的回调函数。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示列举tag成功；非0表示失败。
     * @param tags
     *            当前应用设置的所有tag。
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
            String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags="
                + tags;
        Log.d(TAG, responseString);

        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        updateContent(context, responseString);
    }

    /**
     * PushManager.stopWork() 的回调函数。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示从云推送解绑定成功；非0表示失败。
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        Log.d(TAG, responseString);

        if (errorCode == 0) {
            // 解绑定成功
        }
        // Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
        updateContent(context, responseString);
    }

    private void updateContent(Context context, String content) {
        Log.d(TAG, "updateContent");
        String logText = "" + Utils.logStringCache;

        if (!logText.equals("")) {
            logText += "\n";
        }

        SimpleDateFormat sDateFormat = new SimpleDateFormat("HH-mm-ss");
        logText += sDateFormat.format(new Date()) + ": ";
        logText += content;

        Utils.logStringCache = logText;

     /*   Intent intent = new Intent();
        intent.setClass(context.getApplicationContext(), PushDemoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.getApplicationContext().startActivity(intent);*/
    }

}
