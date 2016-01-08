package com.bbxiaoqu.comm.tool;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * 应用程序UI工具包：封装UI相关的常用的一些操作
 * @author zhows
 * 2015-3-28 下午6:36:28
 */
public class UiTools {
	
	private static Boolean debug = true;
	private static String debugTag = "com.bbxiaoqu";
	/**
     * 弹出Toast消息
     * @param msg
     */
	private static Toast toast =null;
    public static void ToastMsg(Context cont,String msg){
        if(null==toast){
        	toast=Toast.makeText(cont, msg, Toast.LENGTH_SHORT);
        }else{
        	toast.setText(msg);
        }
        toast.show();
    }
    /**
     * 定时关闭Toast
     * @param cont
     * @param msg
     * @param time
     */
    public static void ToastMsg(Context cont,String msg,int time){
        if(null==toast){
        	toast=Toast.makeText(cont, msg, time);
        }else{
        	toast.setText(msg);
        }
        toast.show();
    }
    
    /**
     * @param msg
     */
    public static void  printLog(String msg) {
    	if(debug){
    		Log.e(debugTag, msg);
    	}
	}
    /**
     * print activity tag msg
     * @param tag
     * @param msg
     */
    public static void  printLog(String tag,String msg) {
    	if(debug){
    		Log.e(tag, msg);
    	}
	}
    /**
     * @param tag
     * @param msg
     * @param e
     */
    public static void  printLog(String tag,String msg,Throwable e) {
    	if(debug){
    		Log.e(tag, msg,e);
    	}
	}
}
