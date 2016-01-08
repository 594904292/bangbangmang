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



public class MessageDB
{
	public DatabaseHelper dbHelper;
	
	public MessageDB(Context context)
	{		
		dbHelper=new DatabaseHelper(context);
	}

	public String getmaxtime() {
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		String sql = "select max(date) from [message]";
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
	
	public void add(BbMessage chatMessage)
	{
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		int isComing = chatMessage.isComing() ? 1 : 0;
		int readed = chatMessage.isReaded() ? 1 : 0;
		//用户ID,图标,
		sdb.execSQL(
				"insert into [message] (senduserid,sendnickname,community,address,lng,lat,guid,infocatagroy,message,icon,date,is_coming,readed) values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
				new Object[] { 
						chatMessage.getSenduserId(),
						chatMessage.getSendnickname(),
						chatMessage.getCommunity(),
						chatMessage.getAddress(),
						chatMessage.getLng(),
						chatMessage.getLat(), 
						chatMessage.getGuid(),
						chatMessage.getInfocatagroy(),
						chatMessage.getMessage(),
						chatMessage.getIcon(),
						chatMessage.getDateStr(),
						isComing, 
						readed });
		sdb.close();		
	}
	
	
	public void SyncToLocal(BbMessage chatMessage)
	{		
		if(!isexit(chatMessage.getGuid()))
		{//存在
			int isComing = chatMessage.isComing() ? 1 : 0;
			int readed = chatMessage.isReaded() ? 1 : 0;
			SQLiteDatabase sdb=dbHelper.getReadableDatabase();
			sdb.execSQL(
					"insert into [message] (senduserid,sendnickname,community,address,lng,lat,guid,infocatagroy,message,icon,date,is_coming,readed) values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[] { 
							chatMessage.getSenduserId(),
							chatMessage.getSendnickname(),
							chatMessage.getCommunity(),
							chatMessage.getAddress(),
							chatMessage.getLng(),
							chatMessage.getLat(), 
							chatMessage.getGuid(),
							chatMessage.getInfocatagroy(),
							chatMessage.getMessage(),
							chatMessage.getIcon(),
							chatMessage.getDateStr(),
							isComing, 
							readed });
			sdb.close();	
		}
	}
	

	public boolean isexit(String guid){
		 boolean v=false;
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="select * from [message] where guid=?";
		Cursor cursor=sdb.rawQuery(sql, new String[]{guid});			
		if(cursor.moveToFirst()==true)
		{		
			L.i("MessageDB","guid hava:"+guid+","+cursor.getString(cursor.getColumnIndex("guid")));
			cursor.close();
			sdb.close();
			v= true;
		}else
		{
			L.i("MessageDB","guid not hava:"+guid);
			cursor.close();
			sdb.close();
			v= false;
		}
		return v;
	}

}
