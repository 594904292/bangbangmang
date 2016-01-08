package com.bbxiaoqu.comm.tool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class NetworkUtils {

    public static final int NETWORN_NONE = 0;
    public static final int NETWORN_WIFI = 1;
    public static final int NETWORN_MOBILE = 2;

    public static int getNetworkState(Context context){
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //Wifi
        State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        if(state == State.CONNECTED||state == State.CONNECTING){
            return NETWORN_WIFI;
        }

        //3G
        state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if(state == State.CONNECTED||state == State.CONNECTING){
            return NETWORN_MOBILE;
        }
        return NETWORN_NONE;
    }
    
    public static boolean isNetConnected(Context context) {
		boolean isNetConnected;
		// 获得网络连接服务
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
//			String name = info.getTypeName();
//			L.i("当前网络名称：" + name);
			isNetConnected = true;
		} else {
			L.i("没有可用网络");
			isNetConnected = false;
		}
		return isNetConnected;
	}
}
