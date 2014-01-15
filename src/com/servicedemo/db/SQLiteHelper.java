package com.servicedemo.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

	public SQLiteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, "Test", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		System.out.println("Creating Table");
		StringBuffer sb = new StringBuffer("CREATE TABLE IF NOT EXISTS USER ");
		sb.append(" (")
		.append("	ID				INTEGER PRIMARY KEY AUTOINCREMENT")
		.append("	,USER_ID		INTEGER")
		.append(" 	,USER_NAME	 		VARCHAR")
		.append(" )");
		db.execSQL(sb.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public void addData(SQLiteDatabase db){
		System.out.println("add");
		String sql = "INSERT INTO USER (USER_ID,USER_NAME) VALUES (1,'RIVER')";
		db.execSQL(sql);
	}
	
	public void selectData(SQLiteDatabase db){
		System.out.println("select");
		String sql = "SELECT * FROM USER";
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			String name = cursor.getString(2);
			System.out.println("Is the result right?："+name);
		}
		cursor.close();
	}
}
