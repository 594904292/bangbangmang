package com.bbxiaoqu.client.xmpp;
import java.io.IOException;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import android.content.Context;
import android.util.Log;
import com.bbxiaoqu.DemoApplication;
public class XmppTool {

	
	private static XmppTool instance = null;
	private static Context mContext;
    private static XMPPTCPConnection connection = null;
    private static ViConnectionListener connectionListener=null;
    private static ChatListener chatlistener = null;
	private static ChatManager cm=null;
	   protected XmppTool(Context context) {
		   mContext = context;// Exists only to defeat instantiation.      
	   }      
	   public static XmppTool getInstance(Context context) {      
	      if(instance == null) {      
	         instance = new XmppTool(context);      
	      }      
	      return instance;      
	   }
	

    public static XMPPTCPConnection getConnection() {

			if(connection!=null)
			{
				return connection;
			}
			XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
			builder.setServiceName("bbxiaoqu");
			builder.setHost(DemoApplication.getInstance().xmpphost);
			builder.setPort(DemoApplication.getInstance().xmppport);
			builder.setCompressionEnabled(false);
			builder.setDebuggerEnabled(false);
			builder.setSendPresence(true);
			builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);

			connection = new XMPPTCPConnection(builder.build());
			connectionListener = new ViConnectionListener(mContext);
			connection.addConnectionListener(connectionListener);

			cm = ChatManager.getInstanceFor(connection);

			chatlistener = ChatListener.getInstance(mContext);
			cm.addChatListener(chatlistener);

		return connection;
    }

	public static void closeConnection() {
		if(connection!=null&&connection.isConnected()){
			cm.removeChatListener(chatlistener);
			connection.removeConnectionListener(connectionListener);
			if(connection.isConnected())
				connection.disconnect();
			connection = null;
		}
		Log.i("XmppConnection", "關閉連接");
	}

	public static void login() {
				XMPPTCPConnection connection = XmppTool.getInstance(mContext).getConnection();
				try {
					connection.connect();
				} catch (SmackException e) {
					//e.printStackTrace();
				} catch (IOException e) {
					//e.printStackTrace();
				} catch (XMPPException e) {
					//e.printStackTrace();
				}
				try {
					connection.login(DemoApplication.getInstance().getUserId(), DemoApplication.getInstance().getPassword(), "XMPPTCPConnection");
				} catch (XMPPException e) {
					//e.printStackTrace();
				} catch (SmackException e) {
					//e.printStackTrace();
				} catch (IOException e) {
					//e.printStackTrace();
				}


		if (connection.isConnected() && connection.isAuthenticated()) {
				Presence presence = new Presence(Presence.Type.available);
				try {
					connection.sendStanza(presence);
				} catch (SmackException.NotConnectedException e) {
					e.printStackTrace();
				}
			}
	}

}