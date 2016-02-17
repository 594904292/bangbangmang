package com.bbxiaoqu.client.xmpp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.content.Context;
import android.util.Log;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.R;
import com.bbxiaoqu.api.util.Utils;
import com.bbxiaoqu.comm.tool.NetworkUtils;
import com.bbxiaoqu.view.BaseActivity;

/**
 * @author Sam.Io
 * @time 2011/11/18
 * @project AdXmpp
 */
public class XmppTool {

	
	private static XmppTool instance = null;      
	private static Context mContext;
    private static XMPPConnection con = null;
    private static ViConnectionListener connectionListener=null;
    private static ChatListener chatlistener = null;
    
	   protected XmppTool(Context context) {      
		   mContext = context;// Exists only to defeat instantiation.      
	   }      
	   public static XmppTool getInstance(Context context) {      
	      if(instance == null) {      
	         instance = new XmppTool(context);      
	      }      
	      return instance;      
	   }      
	
    private static void openConnection() {
        
    	if (NetworkUtils.isNetConnected(DemoApplication.getInstance())) 
    	{
            ConnectionConfiguration connConfig = new ConnectionConfiguration(DemoApplication.getInstance().xmpphost, DemoApplication.getInstance().xmppport);
            connConfig.setReconnectionAllowed(false);//要么就不用自己实现重连操作。
            con = new XMPPConnection(connConfig);                       
            connectionListener = new ViConnectionListener(mContext);
            chatlistener=ChatListener.getInstance(mContext);
            try {
				if(con!=null)
				{
	            	if(!con.isConnected())
	            	{//未连接
	            		con.connect();
						if(con!=null)
						{
							con.addConnectionListener(connectionListener);//放在连接后,要不出错
						}

	            	}
				}
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				con=null;
				e.printStackTrace();
			} catch (XMPPException e) {
				con=null;
				// TODO Auto-generated catch block
				e.printStackTrace();
			}				
    	}
           
    }

    public static XMPPConnection getConnection() {
        
    	if (con == null) {
        	openConnection();
        }
    	if(con!=null&&!con.isConnected())
    	{
    		openConnection();
    	}
        return con;
    }
    
    public static void login() {
		if(con!=null&&con.isConnected())
        {
	        if(con!=null&&!con.isAuthenticated())
	        {
	        	if (con.isConnected() && !con.isAuthenticated()) 
				{
					try {
						con.login(DemoApplication.getInstance().getUserId(), DemoApplication.getInstance().getPassword());
					
						if (con.isConnected() && con.isAuthenticated()) {	
							Presence presence = new Presence(Presence.Type.available);
							con.sendPacket(presence);//在线
							
							ChatManager cm = con.getChatManager(); // 取得聊天管理器
							cm.addChatListener(chatlistener);
						}
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						con=null;						
					} catch (XMPPException e) {
						e.printStackTrace();
						con=null;						
					}		
				}
	        }
        }
	}

    public static void closeConnection() {
    	if(con!=null){
			//移除連接監聽
    		con.getChatManager().removeChatListener(chatlistener);//移消息监听
    		con.removeConnectionListener(connectionListener);//移连接监听
			if(con.isConnected())
				con.disconnect();
			con = null;
		}
		Log.i("XmppConnection", "關閉連接");      
    }
    
    
   
    
    /** 
     * 获取好友列表 
     *  
     * @param username 
     * @param pass 
     * @return 
     * @throws XMPPException 
     */  
    public List<RosterEntry> getRosterList(String username, String pass)  
            throws XMPPException {  
        con = XmppTool.getInstance(mContext).getConnection();  
        con.connect();  
        con.login(username, pass);  
        Collection<RosterEntry> rosters = con.getRoster().getEntries();  
        for (RosterEntry rosterEntry : rosters) {  
            System.out.print("name: " + rosterEntry.getName() + ",jid: "  
                    + rosterEntry.getUser()); // 此处可获取用户JID  
            System.out.println("");  
        }  
        return null;  
    }  
  
    /** 
     * 获取用户列表（含组信息） 
     *  
     * @param username 
     * @param pass 
     * @return 
     * @throws XMPPException 
     */  
    public List<RosterEntry> getRoster(String username, String pass)  
            throws XMPPException {  
        con = XmppTool.getInstance(mContext).getConnection();  
        con.connect();  
        con.login(username, pass);  
        Roster roster = con.getRoster();  
        List<RosterEntry> EntriesList = new ArrayList<RosterEntry>();  
        Collection<RosterEntry> rosterEntry = roster.getEntries();  
        Iterator<RosterEntry> i = rosterEntry.iterator();  
        while (i.hasNext()) {  
            EntriesList.add(i.next());  
        }  
        return EntriesList;  
    }  
    
 
}