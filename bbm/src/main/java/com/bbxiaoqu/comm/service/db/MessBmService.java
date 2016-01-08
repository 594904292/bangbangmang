package com.bbxiaoqu.comm.service.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbxiaoqu.comm.tool.L;
import com.bbxiaoqu.widget.AutoListView;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MessBmService {
	public DatabaseHelper dbHelper;
	public MessBmService(Context context){
		dbHelper=new DatabaseHelper(context);
	}
	
	public ArrayList<String> getguids(String userid) {
		List<String> guidlist = new ArrayList<String>();
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		String sql="";		
		sql = "select * from messagebm where userid=?";
		String obj[]={userid};
		L.i("query",sql);
		Cursor c = sdb.rawQuery(sql, obj);
		while (c.moveToNext()) {
			guidlist.add(String.valueOf(c.getString(1).toString()));
		}
		c.close();
		sdb.close();
		return (ArrayList<String>) guidlist;
	}
	
	public boolean addbm(String infoid,String userid){
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="insert into messagebm (infoid,userid) values(?,?)";
		Object obj[]={infoid,userid};
		sdb.execSQL(sql, obj);
		sdb.close();
		return true;
	}
	
	
	public boolean removebm(String infoid,String userid){
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="delete from messagebm where infoid=? and userid=?";
		Object obj[]={infoid,userid};
		sdb.execSQL(sql, obj);
		sdb.close();
		return true;
	}
	
	
	
	public boolean isexit(String infoid,String userid) {
		// 读写数据库
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="select * from messagebm where infoid=? and userid=?";
		Cursor cursor=sdb.rawQuery(sql, new String[]{infoid,userid});		
		if(cursor.moveToFirst()==true){
			cursor.close();
			sdb.close();
			return true;
		}	
		cursor.close();
		sdb.close();
		return false;
	}
	
	public void close() {  
	     if (dbHelper != null) {  
	    	 dbHelper.close();  
	     }  
	 }  
}
