package com.bbxiaoqu.comm.service.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class XiaoquService {
	public DatabaseHelper dbHelper;
	public XiaoquService(Context context){
		dbHelper=new DatabaseHelper(context);
	}
	
	
	
	public boolean addxiaoqu(String xiaoquname){
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="insert into xiaoqu (xiaoquname) values(?)";
		Object obj[]={xiaoquname};
		sdb.execSQL(sql, obj);
		sdb.close();
		return true;
	}
	
	
	public boolean removexiaoqu(String xiaoquname){
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="delete from xiaoqu where xiaoquname=?";
		Object obj[]={xiaoquname};
		sdb.execSQL(sql, obj);
		sdb.close();
		return true;
	}
	
	
	
	public boolean isexit(String str) {
		// 读写数据库
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="select * from xiaoqu where xiaoquname=?";
		Cursor cursor=sdb.rawQuery(sql, new String[]{str});		
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
