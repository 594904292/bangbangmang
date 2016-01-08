package com.bbxiaoqu.comm.service;



import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.comm.tool.NetworkUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

public class AppBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
        	DemoApplication.mNetWorkState = NetworkUtils.getNetworkState(context);
        }

    }

}
