/*
 * Copyright (c) 2012 Shanda Corporation. All rights reserved.
 *
 * Created on 2012-01-30.
 */
package com.snda.myPhone;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory; 
import android.util.Log;

//SQLite���ݿ������
public class phoneLocationDB extends SQLiteOpenHelper{
	private static final String TAG = "myPhone.phoneLocationDB";
	
    //���ø��๹����   	
	public phoneLocationDB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, 2);
		// TODO Auto-generated constructor stub
        Log.i(TAG,"phoneLocationDB is created");
    }
	
	@Override  	
	public void onCreate(SQLiteDatabase db) {   		
        db.execSQL("CREATE TABLE if not exists " + "list" + " ( "
                + "RecNo" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ID" + " TEXT NOT NULL, "
                + "num" + " TEXT NOT NULL, "
                + "area" + " TEXT NOT NULL, "
                + "t" + " TEXT NOT NULL)");
        db.execSQL("CREATE TABLE if not exists " + "callhistory" + " ( "
                + "num TEXT NOT NULL, "
                + "type TEXT NOT NULL, "
                + "ringtime INT NOT NULL)");
        db.execSQL("CREATE TABLE if not exists " + "blacklist" + " ( "
                + "blockType INT NOT NULL, "
                + "name TEXT NOT NULL, "
                + "blockContent TEXT NOT NULL)");
        Log.i(TAG,"phoneLocationDB is created");
	}   
	
    //�������ݿ�ʱ����İ汾���뵱ǰ�İ汾�Ų�ͬʱ����ø÷���   	
	@Override  	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {   		
        Log.i(TAG,"phoneLocationDB is upgraded");
	}    
  
}   