package com.bbxiaoqu.comm.service.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class XiaoquService {
	public DatabaseHelper dbHelper;
	public XiaoquService(Context context){
		dbHelper=new DatabaseHelper(context);
	}
	
	
	
	public boolean addxiaoqu(String xiaoquid,String xiaoquname){
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="insert into xiaoqu (xiaoquid,xiaoquname) values(?,?)";
		Object obj[]={xiaoquid,xiaoquname};
		sdb.execSQL(sql, obj);
		sdb.close();
		return true;
	}
	
	
	public boolean removexiaoqu(String xiaoquid){
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="delete from xiaoqu where xiaoquid=?";
		Object obj[]={xiaoquid};
		sdb.execSQL(sql, obj);
		sdb.close();
		return true;
	}
	
	
	
	public boolean isexit(String xiaoquid) {
		// 读写数据库
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="select * from xiaoqu where xiaoquid=?";
		Cursor cursor=sdb.rawQuery(sql, new String[]{xiaoquid});
		if(cursor.moveToFirst()==true){
			cursor.close();
			sdb.close();
			return true;
		}	
		cursor.close();
		sdb.close();
		return false;
	}


	public String allxiaoqu() {
		// 读写数据库
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="select xiaoquname from xiaoqu";
		String name="";
		String obj[]=new String[]{};
		Cursor c = sdb.rawQuery(sql, obj);
		while (c.moveToNext()) {
			//guidlist.add(String.valueOf(c.getString(1).toString()));
			name=name+String.valueOf(c.getString(0).toString());
			if(!c.isLast())
			{
				name=name+",";
			}
		}
		c.close();
		sdb.close();
		return name;
	}

	public int allxiaoqunum() {
		// 读写数据库
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="select xiaoquname from xiaoqu";
		String name="";
		String obj[]=new String[]{};
		int num=0;
		Cursor c = sdb.rawQuery(sql, obj);

			num=c.getCount();

		c.close();
		sdb.close();
		return num;
	}


	public void close() {  
	     if (dbHelper != null) {  
	    	 dbHelper.close();  
	     }  
	 }  
}
