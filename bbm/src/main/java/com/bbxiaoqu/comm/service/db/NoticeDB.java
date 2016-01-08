package com.bbxiaoqu.comm.service.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbxiaoqu.bean.BbMessage;
import com.bbxiaoqu.comm.tool.L;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;



public class NoticeDB
{

	
	public DatabaseHelper dbHelper;

	
	public NoticeDB(Context context)
	{		
		dbHelper=new DatabaseHelper(context);
	}

	public String getmaxtime() {
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		String sql = "select max(date) from [notice]";
		Cursor cursor = sdb.rawQuery(sql, null);
		String time = "";
		if (cursor.moveToFirst() == true) {
			if (cursor.getString(0) != null) {
				time = cursor.getString(0).toString();
			}
		}
		cursor.close();
		sdb.close();
		return time;
	}
	
	public boolean isexit(String relativeid){
		 boolean v=false;
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="select * from [notice] where relativeid=?";
		Cursor cursor=sdb.rawQuery(sql, new String[]{relativeid});			
		if(cursor.moveToFirst()==true)
		{		
			L.i("NoticeDB","guid hava:"+relativeid+","+cursor.getString(cursor.getColumnIndex("relativeid")));
			cursor.close();
			sdb.close();
			v= true;
		}else
		{
			L.i("NoticeDB","guid not hava:"+relativeid);
			cursor.close();
			sdb.close();
			v= false;
		}
		return v;
	}
	
	public void add(String date,String catagory,String relativeid,String content,String readed)
	{
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
	
		//时间，类型 ,关联(聊天：发送人,订单：发起人),内容，是否已读
		sdb.execSQL(
				"insert into [notice] (date,catagory,relativeid,content,readed) values(?,?,?,?,?)",
				new Object[] { 
						date, 
						catagory, 
						relativeid, 
						content, 
						readed });
		sdb.close();
		
	}
	
	
	public void updateusercontent(String userid,String content)
	{
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();		
		sdb.execSQL(
				"update [notice] set  content='"+content+"' where relativeid='"+userid+"'");
		sdb.close();		
	}
	
	
	
	//读了这人的消息
	public void delnotice(String userid,String catatgory)
	{
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();		
		sdb.execSQL("delete from  [notice] where relativeid='"+userid+"' and catagory='"+catatgory+"'");
		sdb.close();		
	}
	
	
	public long unreadnum(){
		 boolean v=false;
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="select count(*) from [notice] where readed=0";
		Cursor cursor=sdb.rawQuery(sql, null);
		cursor.moveToFirst();  
		long num= cursor.getLong(0);  	
	    cursor.close();
		sdb.close();
		return num;
	}

	public long unreadnum(String userid,String catagory){
		 boolean v=false;
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="select count(*) from [notice] where relativeid='"+userid+"' and readed=0 and catagory='"+catagory+"'";
		Cursor cursor=sdb.rawQuery(sql, null);
		cursor.moveToFirst();  
		long num= cursor.getLong(0);  	
	    cursor.close();
		sdb.close();
		return num;
	}
}
