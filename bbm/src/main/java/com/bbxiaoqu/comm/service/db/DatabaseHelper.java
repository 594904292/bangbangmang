package com.bbxiaoqu.comm.service.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	static String name="bangbang.db";
	static int dbVersion=39;
	public DatabaseHelper(Context context) {
		super(context, name, null, dbVersion);
	}

	//只在创建的时候用一次
	public void onCreate(SQLiteDatabase db) {
		String sql="create table  IF NOT EXISTS [user] (id integer primary key autoincrement,userid varchar(20),nickname varchar(20),password varchar(20),telphone varchar(2),headface varchar(2),pass BOOLEAN  NULL,online BOOLEAN  NULL)";
		db.execSQL(sql);
		
		String sql1 = "create table  IF NOT EXISTS  [xiaoqu] (_id integer primary key autoincrement, xiaoquid varchar(20), xiaoquname varchar(50))";
		db.execSQL(sql1);
		
		
		String gzsql = "create table  IF NOT EXISTS  [messagegz] (_id integer primary key autoincrement, infoid varchar(50), userid varchar(50))";
		db.execSQL(gzsql);

		String zansql = "create table  IF NOT EXISTS  [messagezan] (_id integer primary key autoincrement, infoid varchar(50), userid varchar(50))";
		db.execSQL(zansql);

		String sql3 = "create table  IF NOT EXISTS [messagebm] (_id integer primary key autoincrement, infoid varchar(50), userid varchar(50))";
		db.execSQL(sql3);
		
		
		
		String sql4 ="CREATE table IF NOT EXISTS [message] (_id INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ "senduserid varchar(20), " //发送人id
				+ "sendnickname varchar(50), " //发送人
				+ "community varchar(200), " //小区
				+ "address varchar(200), " //详细地址
				+ "lng varchar(20), " //经度
				+ "lat varchar(20), " //纬度
				+ "guid varchar(100), " //GUID服务器上唯一标识
				+ "infocatagroy varchar(20), " //信息类别
				+ "message varchar(200), " //正文
				+ "icon   varchar(200), " //图片
				+ "date varchar(20) , "//日期
				+ "is_coming integer ,"//判断是否自己
				+ "readed integer)";				
		db.execSQL(sql4);//
		
		
		String sql5 ="CREATE table IF NOT EXISTS [chat] (_id INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ "guid varchar(100), " //GUID服务器上唯一标识
				+ "senduserid varchar(20), " //发送人id
			
				+ "touserid varchar(20), " //发送人id
				+ "message varchar(200), " //正文			
				+ "date varchar(20) , "//日期
				+ "readed integer)";				
		db.execSQL(sql5);//
		
		
		String sql6 ="CREATE table IF NOT EXISTS [notice] (_id INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ "date varchar(100), " //提醒时间
				+ "catagory varchar(20), " //提醒类别				
				+ "relativeid varchar(50), " //关联id	
				+ "content varchar(20), " //内容
				+ "readed integer)";				
		db.execSQL(sql6);//
	
		
		

		String sql7 ="CREATE table IF NOT EXISTS [friend] (_id INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ "userid varchar(50), "    //朋友id
				+ "nickname varchar(50), "  //昵称
				+ "usericon varchar(200), " //头像
				+ "lastuserid varchar(50), "    //朋友id
				+ "lastnickname varchar(50), "  //昵称
				+ "lastusericon varchar(200), " //头像
				+ "lastinfo varchar(200), " //最后信息
				+ "lasttime varchar(20), "  //最后时间
				+ "messnum integer)";		//消息数		
		db.execSQL(sql7);//
		
		
	}
	
	
	
	
	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists [user]");
		db.execSQL("drop table if exists [xiaoqu]");
		db.execSQL("drop table if exists [message]");
		db.execSQL("drop table if exists [messagegz]");
		db.execSQL("drop table if exists [messagebm]");
		db.execSQL("drop table if exists [chat]");
		db.execSQL("drop table if exists [notice]");
		db.execSQL("drop table if exists [friend]");
		onCreate(db);
	}

}
