package com.bbxiaoqu.client.xmpp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartXmppReceiver extends BroadcastReceiver{
	private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

	

	@Override
	public void onReceive(Context context, Intent intent) {
		 if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){  
	            System.out.println("手机开机了...bootComplete!");  
	        }else  
	        if(Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())){  
	            System.out.println("新安装了应用程序....pakageAdded!");  
	        }else  
	        if(Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())){  
	            System.out.println("应用程序被卸载了....pakageRemoved!");  
	        }else  
	        if(Intent.ACTION_USER_PRESENT.equals(intent.getAction())){  
	            System.out.println("手机被唤醒了.....userPresent");  
	            Intent service = new Intent();  
	            service.setAction("android.intent.action.BOOT_COMPLETED");  
	            service.setClass(context, MessageService.class);  
	            context.startService(service);  
	        }  	
	}     
}