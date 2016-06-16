package com.bbxiaoqu.ui.sub;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.adapter.ChatMessageAdapter;
import com.bbxiaoqu.bean.BbMessage;
import com.bbxiaoqu.bean.ChatMessage;
import com.bbxiaoqu.bean.User;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver;
import com.bbxiaoqu.client.baidu.BbPushMessageReceiver.onNewMessageListener;
import com.bbxiaoqu.comm.jsonservices.GetJson;
import com.bbxiaoqu.comm.service.db.ChatDB;
import com.bbxiaoqu.comm.service.db.DatabaseHelper;
import com.bbxiaoqu.comm.service.db.FriendDB;
import com.bbxiaoqu.comm.service.db.NoticeDB;
import com.bbxiaoqu.comm.tool.CustomerHttpClient;
import com.bbxiaoqu.comm.tool.L;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.comm.tool.SharePreferenceUtil;
import com.bbxiaoqu.comm.tool.StreamTool;
import com.bbxiaoqu.comm.tool.T;
import com.bbxiaoqu.ui.main.ViewActivity;
import com.bbxiaoqu.widget.AutoListView;
import com.bbxiaoqu.client.xmpp.ChatListener;
import com.bbxiaoqu.client.xmpp.ViConnectionListener;
import com.bbxiaoqu.client.xmpp.XmppTool;
import com.google.gson.Gson;


public class ChattingActivity extends Activity implements onNewMessageListener
{

	
	private String infoid = "0";
	private String  content= "";
	private String senduserid = "";
	private String sendusername = "";
	private boolean issolution = false;//是否解决
	
	private TextView mNickName;
	private EditText mMsgInput;
	private Button mMsgSend;
	
	private RelativeLayout ly_chat_top;
	private Button topbtn;
	private TextView event_name;
	private ListView mChatMessagesListView;
	private List<ChatMessage> mDatas = new ArrayList<ChatMessage>();
	private ChatMessageAdapter mAdapter;
	private DemoApplication myapplication;

	private User mFromUser;	
	private Gson mGson;
	private SharePreferenceUtil mSpUtil;
	private String from;//对方
	private String myself;//本人
	
	private String msg;
	
	private TextView title_tv;
	private TextView right_text_tv;

	
	String fromusername = "";
	String fromheadface = "";
	
	Map<String, Map> UserMap = new HashMap<String, Map>();
	FriendDB db=new FriendDB(ChattingActivity.this);
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_chatting);
		BbPushMessageReceiver.msgListeners.add(this);//新消息
		myapplication = (DemoApplication) this.getApplication();
		Bundle Bundle1 = this.getIntent().getExtras();		
		from = Bundle1.getString("to");
		myself = Bundle1.getString("my");
		if(from==null||from.trim().length()<1)
		{
			T.showShort(myapplication, "获取不到对方参数！");
			return;
		}
		if(myself==null||myself.trim().length()<1)
		{
			T.showShort(myapplication, "获取不到自己的用户参数！");
			return;
		}
		if (!NetworkUtils.isNetConnected(myapplication)) {
			NetworkUtils.showNoNetWorkDlg(ChattingActivity.this);
			T.showShort(myapplication, "当前无网络连接,请稍后再试！");
			return;
		}
		db.updateuserheadface(myself, myapplication.getheadface());
		Map mymap=new HashMap();
		mymap.put("headface", myapplication.getheadface());
		mymap.put("username", myapplication.getNickname());
		UserMap.put(myself, mymap);
		if(Bundle1.getString("guid")!=null)
		{
			guid = Bundle1.getString("guid");				
		}else
		{
			
		}

		new Thread(loaduserinfo).start();
		new Thread(ajaxloadinfo).start();

		new Thread(xmpprunnable).start();
		//清掉通知里的消息
		ChatDB cdb=new ChatDB(ChattingActivity.this);
		cdb.readchat(from, myself);//标记已读
		
		NoticeDB ndb=new NoticeDB(ChattingActivity.this);
		ndb.delnotice(from, "私信");//从通知栏删除
	}

	Runnable xmpprunnable = new Runnable(){
		@Override
		public void run() {
			// TODO: http request.
			Message msg = new Message();
			msg.what=1;
			handler.sendMessage(msg);
		}
	};

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==1) {
				if (!NetworkUtils.isNetConnected(myapplication)) {
					T.showShort(myapplication, "当前无网络连接！");
					NetworkUtils.showNoNetWorkDlg(ChattingActivity.this);
					return;
				}
				myapplication.getInstance().startxmpp();
			}
		}
	};


	private JSONObject jsonobject;//通过GUID获取的消息
	Runnable ajaxloadinfo = new Runnable() {
		@Override
		public void run() {
			if (!NetworkUtils.isNetConnected(myapplication)) {
				T.showShort(myapplication, "当前无网络连接！");

				NetworkUtils.showNoNetWorkDlg(ChattingActivity.this);
				return;
			}
			String target = myapplication.getlocalhost()+"getinfo.php?guid=" + guid;
			String json = GetJson.GetJson(target);
			
				JSONArray jsonarray;
				
				try {
					jsonarray = new JSONArray(json);
					jsonobject = jsonarray.getJSONObject(0);
					infoid = jsonobject.getString("id");		
					content = jsonobject.getString("content");
					senduserid = jsonobject.getString("senduser");
					sendusername= jsonobject.getString("username");					
					if(jsonobject.getString("issolution").equals("1"))
					{
						issolution= true;
					}else
					{
						issolution= false;							
					}
					Message msg = basehandler.obtainMessage();								
					basehandler.sendMessage(msg);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
			}
		};

		private Handler basehandler = new Handler() {
			public void handleMessage(Message msg) {
				//判断是否显示
				if(senduserid.equals(myapplication.getUserId()))
				{//消息是自己发送的
					loadactionpanel();
					event_name.setText(content);
					
				}else
				{//别人发送的
					loadactionpanel();
					topbtn.setEnabled(false);
					event_name.setText(content);
				}
				if(issolution)
				{//已解决,按钮不能用了
					event_name.setText(event_name.getText()+",(已解决)");
					topbtn.setEnabled(false);
					topbtn.setText("已解决");
					topbtn.setVisibility(View.GONE);
				}
			};
		};

	private void loadactionpanel() {
		ly_chat_top=(RelativeLayout) findViewById(R.id.ly_chat_top);
		topbtn=(Button) findViewById(R.id.id_solutcion_btn);
		event_name=(TextView) findViewById(R.id.event_name);			
		ly_chat_top.setVisibility(View.VISIBLE);
		topbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//可以让事件接触
				new Thread(solutionThread).start();
			}
		});
	}

	
	Runnable solutionThread = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"solution.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();			
			
			paramsList.add(new BasicNameValuePair("_infoid", infoid));//信息id
			paramsList.add(new BasicNameValuePair("_guid", guid));//信息唯一标识
			paramsList.add(new BasicNameValuePair("_solutiontype", "2"));//对话私下解决
			paramsList.add(new BasicNameValuePair("_solutionpostion","0"));//留言项
			paramsList.add(new BasicNameValuePair("_solutionuserid", from));//留言人
			
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String solutiondate = sDateFormat.format(new java.util.Date());
			
			paramsList.add(new BasicNameValuePair("_solutiontime", solutiondate));// 本人
			
			
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String json = EntityUtils
							.toString(httpResponse.getEntity());
					result = Integer.parseInt(json);
				} else {
					result = 0;
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("result", result);
				msg.setData(data);
				freshhandler.sendMessage(msg);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		};
	
		Handler freshhandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle data = msg.getData();
				int result = data.getInt("result");
				Log.i("mylog", "请求结果-->" + result);
				if (result == 1) {
					topbtn.setEnabled(false);
					event_name.setText("已解决");
				}
			}
		};
	
	Runnable loaduserinfo = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int result;
			String target = myapplication.getlocalhost()+"getuserinfo.php?userid="+ from;
			HttpGet httprequest = new HttpGet(target);
			try {
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					InputStream jsonStream = null;
					jsonStream = httpResponse.getEntity().getContent();
					byte[] data = null;
					try {
						data = StreamTool.read(jsonStream);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String json = new String(data);
					JSONArray jsonarray;
					try {
						jsonarray = new JSONArray(json);
						JSONObject jsonobject = jsonarray.getJSONObject(0);
						fromusername = jsonobject.getString("username");
						fromheadface = jsonobject.getString("headface");
						
						
						 
						db.updatenickname(from, fromusername);
						db.updateuserheadface(from, fromheadface);
						
						Map tomap=new HashMap();
						tomap.put("headface", fromheadface);
						tomap.put("username", fromusername);
						UserMap.put(from, tomap);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(json);
					result = 1;
				} else {
					result = 0;
				}				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Message msg = new Message();
			
			changehandler.sendMessage(msg);

		}
	};
	
	Handler changehandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			initView();
			initEvent();
			title_tv.setText(fromusername);
			
		}
	};


	public void doBack(View view) {
		onBackPressed();
	}
	
	private void initView()
	{

		mChatMessagesListView = (ListView) findViewById(R.id.id_chat_listView);
		mMsgInput = (EditText) findViewById(R.id.id_chat_msg);
		mMsgSend = (Button) findViewById(R.id.id_chat_send);
		title_tv = (TextView) findViewById(R.id.title);
		right_text_tv = (TextView) findViewById(R.id.right_text);
		right_text_tv.setVisibility(View.GONE);
		title_tv.setText(from);
		right_text_tv.setText("");

	
		mDatas = getdata();
		mAdapter = new ChatMessageAdapter(this, mDatas);
		mChatMessagesListView.setAdapter(mAdapter);
		mChatMessagesListView.setSelection(mDatas.size() - 1);		
	}
	
	
	public List<ChatMessage>  getdata()
	{	
		List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
		DatabaseHelper dbHelper=new DatabaseHelper(this);
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		//senduserid,touserid,guid,message,date,readed
		String sql = "select * from  [chat] where (senduserid ='"+from+"' and touserid='"+myself+"') or (senduserid ='"+myself+"' and touserid='"+from+"') ";
			Cursor c = sdb.rawQuery(sql, null);
			while (c.moveToNext()) {				
				//Message message = new Message(to,to,System.currentTimeMillis(), str);
				ChatMessage chatMessage = new ChatMessage();
				
				if(myself.equals(c.getString(2)))
				{
					chatMessage.setComing(false);
				}else
				{
					chatMessage.setComing(true);
				}
				 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					chatMessage.setDate(df.parse(c.getString(5).toString()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				chatMessage.setSenduserId(c.getString(2));
				if(c.getString(2).equals(myself))
				{
					String myselfname="-";
					if(UserMap.get(myself).get("username")!=null)
					{
						myselfname=UserMap.get(myself).get("username").toString();
					}
					String myselfheadface="";
					if(UserMap.get(myself).get("headface")!=null)
					{
						myselfheadface=UserMap.get(myself).get("headface").toString();
					}
					chatMessage.setSendnickname(myselfname);
					chatMessage.setSenduserIcon(myselfheadface);
				}else
				{
					String fromname="-";
					if(UserMap.get(from).get("username")!=null)
					{
						fromname=UserMap.get(from).get("username").toString();
					}
					String fromheadface="";
					if(UserMap.get(myself).get("headface")!=null)
					{
						fromheadface=UserMap.get(from).get("headface").toString();
					}
					chatMessage.setSendnickname(fromname);
					chatMessage.setSenduserIcon(fromheadface);
				}			
				chatMessage.setMessage(c.getString(4));
				chatMessage.setReaded(true);
				chatMessages.add(chatMessage);								
			}
			c.close();
			sdb.close();
			return chatMessages;
	}

	private void initEvent()
	{
		mMsgSend.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				msg = mMsgInput.getText().toString();
				if (TextUtils.isEmpty(msg))
				{
					T.showShort(myapplication, "您还未填写消息呢!");

					return;
				}

				if (!NetworkUtils.isNetConnected(myapplication))
				{
					T.showShort(myapplication, "当前无网络连接！");
					NetworkUtils.showNoNetWorkDlg(ChattingActivity.this);
					return;
				}
				
				String guid=UUID.randomUUID().toString();
				ChatMessage chatMessage = new ChatMessage();
				chatMessage.setComing(false);
				chatMessage.setSenduserId(myself);
				chatMessage.setTouserId(from);
				
				
				chatMessage.setSendnickname(UserMap.get(myself).get("username").toString());
				chatMessage.setSenduserIcon(UserMap.get(myself).get("headface").toString());					

				Date d=new Date();
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				chatMessage.setGuid(guid);
				chatMessage.setMessage(msg);
				chatMessage.setDate(new Date());
				chatMessage.setDateStr(df.format(d));
				mDatas.add(chatMessage);
				 ChatDB db=new ChatDB(myapplication);
			     if(!db.isexit(chatMessage.getGuid()))
			     {
			      	db.add(chatMessage);
			     }
			     FriendDB fb=new FriendDB(myapplication);				    
				 if(!fb.isexit(from))
				 {//添加朋友联系人
				  	fb.add(from, fromusername, fromheadface,myself,myapplication.getNickname(), msg, df.format(d), 0);
				 }
				  fb.newmess(from, myself,msg, df.format(new Date()));
				  
				mAdapter.notifyDataSetChanged();
				mChatMessagesListView.setSelection(mDatas.size() - 1);
				mMsgInput.setText("");
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				//得到InputMethodManager的实例
				if (imm.isActive())
				{
					// 如果开启,关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
					imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,InputMethodManager.HIDE_NOT_ALWAYS);
				}
				new Thread(savechatThread).start();//先发送到服务器
			}
		});
	}
	
//	 public void sendxmpp(String userid,String msg) {
//			XMPPConnection connection=XmppTool.getInstance(this).getConnection();
//			try {
//				if(connection.isConnected()&&connection.isAuthenticated())
//				{
//					ChatManager cm = connection.getChatManager(); 	//取得聊天管理器
//					cm.addChatListener(new ChatListener(ChattingActivity.this));//侦听				
//					String uuu=userid+"@"+DemoApplication.getInstance().xmppdomain;
//					Chat chat = cm.createChat(uuu, null);	//得到与另一个帐号的连接，这里是一对一,@后面是你安装openfire时注册的域
//					chat.sendMessage(msg);//发送消息
//				}
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//			//connection.disconnect();
//			
//		}
	 
	String guid="";
	Runnable savechatThread = new Runnable() {
		@Override
		public void run() {
//			if(myself.equals(DemoApplication.getInstance().getUserId()))
//			{//本人	
//				sendxmpp(from, msg);
//			}else
//			{
//				sendxmpp(myself, msg);
//			}
			int result;
			String target = myapplication.getlocalhost()+"chat.php";
			HttpPost httprequest = new HttpPost(target);
			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String date = sDateFormat.format(new java.util.Date());

			paramsList.add(new BasicNameValuePair("_catatory", "chat"));// 本人
			paramsList.add(new BasicNameValuePair("_senduserid", myself));// 本人
			paramsList.add(new BasicNameValuePair("_sendnickname", myself));// 本人
			paramsList.add(new BasicNameValuePair("_sendusericon", myself));// 本人
			paramsList.add(new BasicNameValuePair("_touserid", from));// 对方
			paramsList.add(new BasicNameValuePair("_tonickname", from));//对方
			paramsList.add(new BasicNameValuePair("_tousericon", from));//对方
			paramsList.add(new BasicNameValuePair("_guid", guid));//消息
			paramsList.add(new BasicNameValuePair("_message", msg));//消息
			paramsList.add(new BasicNameValuePair("_channelid", ""));// 通道
			try {
				httprequest.setEntity(new UrlEncodedFormEntity(paramsList,
						"UTF-8"));
				HttpClient HttpClient1 = CustomerHttpClient.getHttpClient();
				HttpResponse httpResponse = HttpClient1.execute(httprequest);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String json = EntityUtils
							.toString(httpResponse.getEntity());
					result = 1;
					//
				} else {
					result = 0;
				}
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putInt("result", result);
				msg.setData(data);
				savehandler.sendMessage(msg);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 发送到服务器

		}

		

	};
	
	
	Handler savehandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			int result = data.getInt("result");
			Log.i("mylog", "请求结果-->" + result);
			if (result == 1) {
				Toast.makeText(ChattingActivity.this, "已发送", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ChattingActivity.this, "发送失败", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};
	
	@Override
	protected void onDestroy()
	{
		BbPushMessageReceiver.msgListeners.remove(this);		
		super.onDestroy();

	}

	Handler newmesshandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mDatas = getdata();
			mAdapter = new ChatMessageAdapter(ChattingActivity.this, mDatas);
			mChatMessagesListView.setAdapter(mAdapter);
			if(mDatas!=null&&mDatas.size()>0)
			{
				mChatMessagesListView.setSelection(mDatas.size() - 1);
			}
		}
	};

	public void onNewMessage(BbMessage message)
	{//强制刷新就OK
		
		Message msg = new Message();
		newmesshandler.sendMessage(msg);
		
		
	}

}
