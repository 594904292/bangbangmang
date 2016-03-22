package com.bbxiaoqu;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import static com.bbxiaoqu.SessionManager.P_MARKET_ISLOGIN;
import static com.bbxiaoqu.SessionManager.P_MARKET_ISNOTIC;
import static com.bbxiaoqu.SessionManager.P_MARKET_USERID;
import static com.bbxiaoqu.SessionManager.P_MARKET_USERNAME;
import static com.bbxiaoqu.SessionManager.P_MARKET_PASSWORD;
import static com.bbxiaoqu.SessionManager.P_MARKET_HEADFACE;
import static com.bbxiaoqu.SessionManager.P_MARKET_XIAOQUID;
import static com.bbxiaoqu.SessionManager.P_MARKET_XIAOQUNAME;
import static com.bbxiaoqu.SessionManager.P_MARKET_LAT;
import static com.bbxiaoqu.SessionManager.P_MARKET_LNG;
/**
 * 
 * The Client Seesion Object for GfanMobile, contains some necessary
 * information.
 * 
 * @author andrew
 * @date 2010-12-22
 * @since Version 0.5.1
 * 
 */

//观察者设计模式（ Observable类Observer接口）
//一个Observer对象监视着一个Observable对象的变化，当Observable对象发生变化时，Observer得到通知，就可以进行相应的工作。
// 被观察者类 
public class Session extends Observable {

    /** Log tag */
    private final static String TAG = "Session";
    
    /** Application Context */
    private Context mContext;
    
    /** The version of OS */
    private int osVersion;

    /** The user-visible version string. E.g., "1.0" */
    private String buildVersion;


    /** The application uid */
    private Boolean islogin=false;
    private Boolean isnotice=false;


    private String userid;
    private String username;
    private String password;
   	private String headface;
    private String xiaoquid;
    private String xiaoquname;
    private String lat;
    private String lng;


    /** Session Manager */
    private SessionManager mSessionManager;

    /** The singleton instance */
    private static Session mInstance;
    

    
    /**
     * default constructor
     * @param context
     */
    private Session(Context context) {
        
        synchronized (this) {
            mContext = context;
            osVersion = Build.VERSION.SDK_INT;
            buildVersion = Build.VERSION.RELEASE;
            
            readSettings();
        }
    }
    
    /*
     * 读取用户所有的设置
     */
    private void readSettings() {
        new Thread() {
            public void run() {
                mSessionManager = SessionManager.get(mContext);
                addObserver(mSessionManager);//加入观察者
                HashMap<String, Object> preference = mSessionManager.readPreference();
                //islogin = (String) preference.get(P_MARKET_ISLOGIN);
                if(preference.get(P_MARKET_ISLOGIN)!=null&&preference.get(P_MARKET_ISLOGIN).equals("1"))
                {
                	islogin=true;
                }else
                {
                	islogin=false;
                }
                if(preference.get(P_MARKET_ISNOTIC)!=null&&preference.get(P_MARKET_ISNOTIC).equals("1"))
                {
                    isnotice=true;
                }else
                {
                    isnotice=false;
                }
                userid = (String) preference.get(P_MARKET_USERID);
                username = (String) preference.get(P_MARKET_USERNAME);
                password = (String) preference.get(P_MARKET_PASSWORD);
                
                headface = (String) preference.get(P_MARKET_HEADFACE);
                xiaoquid = (String) preference.get(P_MARKET_XIAOQUID);
                xiaoquname = (String) preference.get(P_MARKET_XIAOQUNAME);
                
                lat = (String) preference.get(P_MARKET_LAT);
                lng = (String) preference.get(P_MARKET_LNG);


            };
        }.start();
    }  
    public static Session get(Context context) {
        if (mInstance == null) {
            mInstance = new Session(context);
        }
        return mInstance;
    }

    public Boolean getIslogin() {
  		return islogin;
  	}

  	public void setIslogin(Boolean islogin) {
  		this.islogin = islogin;
  		 super.setChanged();
  		 if(islogin)
  		 {
  			 super.notifyObservers(new Pair<String, Object>(P_MARKET_ISLOGIN, "1"));//方法通知其所有observers
  		 }else
  		 {
  			super.notifyObservers(new Pair<String, Object>(P_MARKET_ISLOGIN, "0"));//方法通知其所有observers
  		 }
     
  	}

    public Boolean getIsNotic() {
        return isnotice;
    }

    public void setIsNotic(Boolean isnotice) {
        this.isnotice = isnotice;
        super.setChanged();
        if(isnotice)
        {
            super.notifyObservers(new Pair<String, Object>(P_MARKET_ISNOTIC, "1"));//方法通知其所有observers
        }else
        {
            super.notifyObservers(new Pair<String, Object>(P_MARKET_ISNOTIC, "0"));//方法通知其所有observers
        }

    }
  	
    public String getUid() {
        return userid;
    }

    public void setUid(String uid) {

        this.userid = uid;
        super.setChanged();
        super.notifyObservers(new Pair<String, Object>(P_MARKET_USERID, uid));//方法通知其所有observers
    }
    
    public String getUserName() {
        return username;
    }

    public void setUserName(String userName) {

        this.username = userName;
        super.setChanged();
        super.notifyObservers(new Pair<String, Object>(P_MARKET_USERNAME, userName));
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {

        this.password = password;
        super.setChanged();
        super.notifyObservers(new Pair<String, Object>(P_MARKET_PASSWORD, password));
    }
    
    
    public String getHeadface() {
		return headface;
	}

	public void setHeadface(String headface) {
		this.headface = headface;
		super.setChanged();
	    super.notifyObservers(new Pair<String, Object>(P_MARKET_HEADFACE, headface));
	}

	public String getXiaoquid() {
		return xiaoquid;
	}

	public void setXiaoquid(String xiaoquid) {
		this.xiaoquid = xiaoquid;
		super.setChanged();
	    super.notifyObservers(new Pair<String, Object>(P_MARKET_XIAOQUID, xiaoquid));

	}

	public String getXiaoquname() {
		return xiaoquname;
	}

	public void setXiaoquname(String xiaoquname) {
		this.xiaoquname = xiaoquname;
		super.setChanged();
	    super.notifyObservers(new Pair<String, Object>(P_MARKET_XIAOQUNAME, xiaoquname));

	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
		super.setChanged();
	    super.notifyObservers(new Pair<String, Object>(P_MARKET_LAT, lat));

	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
		super.setChanged();
	    super.notifyObservers(new Pair<String, Object>(P_MARKET_LNG, lng));

	}

    public void close() {
        mSessionManager.writePreferenceQuickly();             
        mInstance = null;
    }

   
      
}