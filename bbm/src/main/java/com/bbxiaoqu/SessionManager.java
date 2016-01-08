package com.bbxiaoqu;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Pair;


//一个Observer对象监视着一个Observable对象的变化，当Observable对象发生变化时，Observer得到通知，就可以进行相应的工作。
//观察者类 
public class SessionManager implements Observer {
	public static final String P_MARKET_ISLOGIN = "pref.islogin";
	public static final String P_MARKET_USERNAME = "pref.username";
	public static final String P_MARKET_PASSWORD = "pref.password";
	public static final String P_MARKET_HEADFACE = "pref.headface";
	public static final String P_MARKET_USERID = "pref.userid";
	public static final String P_MARKET_XIAOQUID = "pref.xiaoquid";
	public static final String P_MARKET_XIAOQUNAME = "pref.xiaoquname";
	public static final String P_MARKET_LAT = "pref.lat";
	public static final String P_MARKET_LNG = "pref.lng";
    private static SessionManager mInstance;
    
    //SharedPreferences是Android中最容易理解的数据存储技术，实际上SharedPreferences处理的就是一个key-value（键值对）SharedPreferences常用来存储一些轻量级的数据。
    private SharedPreferences mPreference;
    private Context mContext;
	private LinkedList<Pair<String, Object>> mUpdateQueue = new LinkedList<Pair<String, Object>>();//队列
	private Thread mCurrentUpdateThread;
    
    private SessionManager(Context context) {
        synchronized (this) {
            mContext = context;
            if (mPreference == null) {
            	////实例化SharedPreferences对象（第一步） 
                mPreference = PreferenceManager.getDefaultSharedPreferences(mContext);
            }
        }
    }
    
    public static SessionManager get(Context context) {
        if (mInstance == null) {
            mInstance = new SessionManager(context);
        }
        return mInstance;
    }
    
    private static final Method sApplyMethod = findApplyMethod();

    private static Method findApplyMethod() {
        try {
			Class<Editor> cls = SharedPreferences.Editor.class;
            return cls.getMethod("apply");
        } catch (NoSuchMethodException unused) {
            // fall through
        }
        return null;
    }

    /** Use this method to modify preference */
    public static void apply(SharedPreferences.Editor editor) {
        if (sApplyMethod != null) {
            try {
                sApplyMethod.invoke(editor);
                return;
            } catch (InvocationTargetException unused) {
                // fall through
            } catch (IllegalAccessException unused) {
                // fall through
            }
        }
        //提交当前数据 
        editor.commit();
    }
    
	
    
    /**
     * Release all resources
     */
    public void close() {
        mPreference = null;
        mInstance = null;
    }
    
    private boolean isPreferenceNull() {
        if(mPreference == null) 
            return true;
        return false;
    }

	@SuppressWarnings("unchecked")
    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof Pair) {
            synchronized (mUpdateQueue) {
                if (data != null) {
                    mUpdateQueue.add((Pair<String, Object>) data);
                }
            }
            writePreferenceSlowly();
        }
    }
	
//    private void logTaskQueue() {
//        if (mUpdateQueue != null) {
//            Log.d("hibenate", " task queue is ");
//            int length = mUpdateQueue.size();
//            for (int i = 0; i < length; i++) {
//                Pair p = mUpdateQueue.get(i);
//                Log.d("hibenate", "key " + p.first + " value " + p.second);
//            }
//        }
//    }
	
	/*
	 * Do Hibernation slowly
	 */
	private void writePreferenceSlowly() {
		if (mCurrentUpdateThread != null) {
			if (mCurrentUpdateThread.isAlive()) {
				// the update thread is still running, 
				// so no need to start a new one
				return;
			}
		}
		
		// update the seesion value back to preference
		// ATTENTION: some more value will be add to the queue while current task is running
		mCurrentUpdateThread = new Thread() {
			
			@Override
			public void run() {
				
				try {
					// sleep 10secs to wait some concurrent task be 
					// inserted into the task queue
					sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				writePreference();
			}
			
		};
		mCurrentUpdateThread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		mCurrentUpdateThread.start();
	}
	
	/*
	 * Do Hibernation immediately
	 */
	public void writePreferenceQuickly() {
		
		// update the seesion value back to preference
		// ATTENTION: some more value will be add to the queue while current task is running
		mCurrentUpdateThread = new Thread() {
			
			@Override
			public void run() {
				writePreference();
			}
			
		};
		mCurrentUpdateThread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		mCurrentUpdateThread.start();
	}
	
	/**
	 * Write session value back to preference
	 */
	private void writePreference() {
		//实例化SharedPreferences.Editor对象（第二步）
		Editor editor = mPreference.edit();

		synchronized (mUpdateQueue) {
			while (!mUpdateQueue.isEmpty()) {

				// remove already unused reference from the task queue
				Pair<String, Object> updateItem = mUpdateQueue.remove();

				// the preference key
				final String key = (String) updateItem.first;
				editor.putString(key, (String) updateItem.second);				
			}
		}
		// update the preference
		apply(editor);
	}

	//使用getString方法获得value，注意第2个参数是value的默认值 
	public HashMap<String, Object> readPreference() {
		
		if (isPreferenceNull()) {
            return null;
        }

		HashMap<String, Object> data = new HashMap<String, Object>();
		String islogin=mPreference.getString(P_MARKET_ISLOGIN, null);
		data.put(P_MARKET_ISLOGIN, islogin);
		
		String userid = mPreference.getString(P_MARKET_USERID, null);		
		data.put(P_MARKET_USERID, userid);
		String username = mPreference.getString(P_MARKET_USERNAME, null);		
		data.put(P_MARKET_USERNAME, username);
		String password = mPreference.getString(P_MARKET_PASSWORD, null);		
		data.put(P_MARKET_PASSWORD, password);		
		String headface = mPreference.getString(P_MARKET_HEADFACE, null);		
		data.put(P_MARKET_HEADFACE, headface);		
		String xiaoquid = mPreference.getString(P_MARKET_XIAOQUID, null);		
		data.put(P_MARKET_XIAOQUID, xiaoquid);
		String xiaoquname = mPreference.getString(P_MARKET_XIAOQUNAME, null);		
		data.put(P_MARKET_XIAOQUNAME, xiaoquname);
		String lat = mPreference.getString(P_MARKET_LAT, null);		
		data.put(P_MARKET_LAT, lat);
		String lng = mPreference.getString(P_MARKET_LNG, null);		
		data.put(P_MARKET_LNG, lng);

		return data;
	}
	
}

