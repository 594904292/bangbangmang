package com.bbxiaoqu.comm.service.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteInterestManager extends SQLiteOpenHelper{
	public SQLiteInterestManager(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table interest(_id integer primary key autoincrement, interestname varchar(20), "
				+ "imageurl varchar(20), weather varchar(20), temp varchar(20));";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table if exists interest;");
	}
}
