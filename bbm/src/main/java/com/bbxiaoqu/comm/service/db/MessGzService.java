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

public class MessGzService {
	public DatabaseHelper dbHelper;
	public MessGzService(Context context){
		dbHelper=new DatabaseHelper(context);
	}
	
	public ArrayList<String> getguids(String userid) {
		List<String> guidlist = new ArrayList<String>();
		SQLiteDatabase sdb = dbHelper.getReadableDatabase();
		String sql="";		
		sql = "select * from messagegz  where userid=?";		
		L.i("query",sql);
		String obj[]=new String[]{userid};
		Cursor c = sdb.rawQuery(sql, obj);
		while (c.moveToNext()) {
			guidlist.add(String.valueOf(c.getString(1).toString()));
		}
		c.close();
		sdb.close();
		return (ArrayList<String>) guidlist;
	}
	
	public boolean addgz(String infoid,String userid){
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="insert into messagegz (infoid,userid) values(?,?)";
		Object obj[]={infoid,userid};
		sdb.execSQL(sql, obj);
		sdb.close();
		return true;
	}
	
	
	public boolean removegz(String infoid,String userid){
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="delete from messagegz where infoid=? and userid=?";
		Object obj[]={infoid,userid};
		sdb.execSQL(sql, obj);
		sdb.close();
		return true;
	}
	
	
	
	public boolean isexit(String str,String userid) {
		// 读写数据库
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="select * from messagegz where infoid=? and userid=?";
		Cursor cursor=sdb.rawQuery(sql, new String[]{str,userid});		
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
