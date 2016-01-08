package com.bbxiaoqu.comm.service.db;

import com.bbxiaoqu.DemoApplication;
import com.bbxiaoqu.comm.service.User;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserService {
	public DatabaseHelper dbHelper;
	public UserService(Context context){
		dbHelper=new DatabaseHelper(context);
	}
	
	
	public boolean login(String userid,String password){
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="select * from [user] where userid=? and password=?";
		Cursor cursor=sdb.rawQuery(sql, new String[]{userid,password});			
		if(cursor.moveToFirst()==true){			
			cursor.close();
			sdb.close();			
			return true;
		}	
		cursor.close();
		sdb.close();
		return false;
	}
	
	
	
	public String getheadface(String userid){
		String headface="";		
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		Cursor cursor = sdb.rawQuery("select headface from [user] where userid=? ", new String[]{userid});
		if (cursor.moveToNext()) {
			headface = cursor.getString(cursor.getColumnIndex("headface"));			
		} else {
			headface="";
			cursor.close();
			sdb.close();
			return headface;
		}
		cursor.close();
		sdb.close();
		return headface;
	}
	
	public boolean register(User user){
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sql="insert into [user] (userid,nickname,password,telphone,headface,pass,online) values(?,?,?,?,?,?,?)";
		Object obj[]={user.getUsername(),user.getNickname(),user.getPassword(),user.getTelphone(),user.getHeadface(),1,0};
		sdb.execSQL(sql, obj);	
		sdb.close();
		return true;
	}
	
	
	/***
	 * 上线
	 */
	public void online(String userid) {
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sqlstr = "update [user] set online=1 and pass=1 where userid=?";
		Object obj[]={userid};
		sdb.execSQL(sqlstr,obj);
		sdb.close();
	}
	
	
	/***
	 * 下线
	 */
	public void offline(String userid) {
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sqlstr = "update [user] set online=0 and pass=0 where userid=?";
		Object obj[]={userid};
		sdb.execSQL(sqlstr,obj);
		sdb.close();
	}
	
	
	public void updatenickname(String nickname,String userid) {
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sqlstr = "update [user] set nickname=? where userid=?";
		Object obj[]={nickname,userid};
		sdb.execSQL(sqlstr,obj);
		sdb.close();
	}
	
	
	public void updateheadface(String headface,String userid) {
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sqlstr = "update [user] set headface=? where userid=?";
		Object obj[]={headface,userid};
		sdb.execSQL(sqlstr,obj);
		sdb.close();
	}
	
	
	/***
	 * 删除用户
	 *//*
	public void quit() {
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String sqlstr = "delete from [user]";
		sdb.execSQL(sqlstr);
		sdb.close();
	}
	*/
	
	/**
	 * 是否已经登录
	 * 
	 * @return
	 */
	public boolean ifpass(String userid) {
		Cursor cursor;
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		String obj[]={userid};
		cursor = sdb.rawQuery("select pass from [user] where userid=?", obj);
		if (cursor.moveToNext()) {
			if ("1".equals(cursor.getString(cursor.getColumnIndex("pass")))) {
				cursor.close();
				sdb.close();
				return true;
			}
		} else {
			cursor.close();
			sdb.close();
			return false;
		}
		cursor.close();
		sdb.close();
		return false;
	}
	
	
		
	
	public String getuserid() {
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		Cursor cursor;
		String userid = "";
		cursor = sdb.rawQuery("select * from [user] where online=1 and pass=1", null);
		if (cursor.moveToNext()) {
			userid = cursor.getString(cursor.getColumnIndex("userid"));
			cursor.close();
		}
		cursor.close();
		sdb.close();
		return userid.trim();
	}
	
	public String getuserpassword(String userid) {
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		Cursor cursor;		
		cursor = sdb.rawQuery("select * from [user] where userid='"+userid+"' ", null);
		if (cursor.moveToNext()) {
			userid = cursor.getString(cursor.getColumnIndex("password"));
			cursor.close();
		}
		cursor.close();
		sdb.close();
		return userid.trim();
	}
	
	public String getnickname(String userid) {
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		Cursor cursor;
		String username = "";
		cursor = sdb.rawQuery("select * from [user] where userid='"+userid+"' limit 0,1", null);
		if (cursor.moveToNext()) {
			username = cursor.getString(cursor.getColumnIndex("nickname"));
			if(username!=null)
			{
				username=username.trim();
			}else
			{
				username = "";
			}
			//cursor.close();
		}
		cursor.close();
		sdb.close();
		return username;
	}
	
	
	public String[] getonlineuserid() {
		String[] arr=null;
		SQLiteDatabase sdb=dbHelper.getReadableDatabase();
		Cursor cursor;
		String userid = "";
		String nickname = "";
		String telphone = "";
		String headface="";
		cursor = sdb.rawQuery("select * from [user] where online=1 and pass=1", null);
		if (cursor.moveToNext()) {
			arr=new String[4];
			userid = cursor.getString(cursor.getColumnIndex("userid"));
			nickname = cursor.getString(cursor.getColumnIndex("nickname"));
			telphone = cursor.getString(cursor.getColumnIndex("telphone"));
			headface = cursor.getString(cursor.getColumnIndex("headface"));
			arr[0]=userid;
			arr[1]=nickname;
			arr[2]=telphone;
			arr[3]=headface;			
			cursor.close();
			sdb.close();
			//return arr;
		}
		cursor.close();
		sdb.close();
		return arr;
	}
	
	
	public void close() {  
	     if (dbHelper != null) {  
	    	 dbHelper.close();  
	     }  
	 }  
}
