package com.bbxiaoqu.comm.service.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbxiaoqu.bean.BbMessage;
import com.bbxiaoqu.bean.ChatMessage;
import com.bbxiaoqu.comm.tool.L;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;



public class FriendDB
{
	public DatabaseHelper dbHelper;
	public FriendDB(Context context)
	{		
		dbHelper=new DatabaseHelper(context);
	}

	public void add(String userid,String nickname,String usericon,String lastuserid,String lastnickname,String lastinfo,String lasttime,int  messnum)
	{
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		sdb.execSQL(
				"insert into [friend] (userid,nickname,usericon,lastuserid,lastnickname,lastinfo,lasttime,messnum) values(?,?,?,?,?,?,?,?)",
				new Object[] { 
						userid,
						nickname,
						usericon,					
						lastuserid,
						lastnickname,										
						lastinfo,
						lasttime,
						messnum
						});
		sdb.close();		
	}
	
	
	public void newmess(String userid,String lastuserid,String lastinfo,String lasttime)
	{
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();		
		sdb.execSQL(
				"update [friend] set lastuserid='"+lastuserid+"',lastinfo='"+lastinfo+"',lasttime='"+lasttime+"',messnum=messnum+1 where userid='"+userid+"'");
		sdb.close();		
	}
	
	public void updateuserheadface(String userid,String usericon)
	{
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();		
		sdb.execSQL(
				"update [friend] set  usericon='"+usericon+"' where userid='"+userid+"'");
		sdb.close();		
	}
	
	public void updatelastinfo(String userid,String nickname,String usericon,String lastnickname)
	{
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();		
		sdb.execSQL(
				"update [friend] set  nickname='"+nickname+"',usericon='"+usericon+"',lastnickname='"+lastnickname+"'   where userid='"+userid+"'");
		sdb.close();		
	}
	
	public void updatenickname(String userid,String nickname)
	{
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();		
		sdb.execSQL(
				"update [friend] set  nickname='"+nickname+"' where userid='"+userid+"'");
		sdb.close();		
	}
	
	
	public boolean removeuser(String userid){
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="delete from friend where userid=?";
		Object obj[]={userid};
		sdb.execSQL(sql, obj);
		sdb.close();
		return true;
	}
	
	
	public boolean removall(){
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="delete from friend";		
		sdb.execSQL(sql);
		sdb.close();
		return true;
	}

	public boolean isexit(String userid){
		 boolean v=false;
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="select * from [friend] where userid=?";
		Cursor cursor=sdb.rawQuery(sql, new String[]{userid});			
		if(cursor.moveToFirst()==true)
		{		
			L.i("FriendDB","guid hava:"+userid+","+cursor.getString(cursor.getColumnIndex("userid")));
			cursor.close();
			sdb.close();
			v= true;
		}else
		{
			L.i("FriendDB","userid not hava:"+userid);
			cursor.close();
			sdb.close();
			v= false;
		}
		return v;
	}

}
