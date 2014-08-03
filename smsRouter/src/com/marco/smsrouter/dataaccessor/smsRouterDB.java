package com.marco.smsrouter.dataaccessor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory; 
import android.util.Log;

//SQLite数据库访问类
public class smsRouterDB extends SQLiteOpenHelper{
	private static final String TAG = "smsRouter.smsRouterDB";
	public static final String SMS_FORWARD_HISTORY_FROM_NAME = "FromName";
	public static final String SMS_FORWARD_HISTORY_FROM_NO   = "FromNo";
	public static final String SMS_FORWARD_HISTORY_TO_NAME   = "ToName";
	public static final String SMS_FORWARD_HISTORY_TO_NO     = "ToNo";
	public static final String SMS_FORWARD_HISTORY_DATE      = "date";
	public static final String SMS_FORWARD_HISTORY_CONTENT   = "content";
	public static final String SMS_FORWARD_NUMBER_NO         = "number";
	public static final String SMS_FORWARD_NUMBER_TIME       = "time";
	public static final String SMS_FORWARD_NUMBER_TYPE       = "type";
	public static final String SMS_FLOWCTL_TYPE              = "flowctltype";
	public static final String SMS_FLOWCTL_THRESHOLD         = "flowctlmax";
	public static final String SMS_FLOWCTL_CURR_YEAR         = "year";
	public static final String SMS_FLOWCTL_CURR_MONTH        = "month";
	public static final String SMS_FLOWCTL_CURR_DAY          = "day";
	public static final String SMS_FLOWCTL_CURR_CNT          = "count";
	public static final int SMS_FLOWCTL_BY_YEAR              = 0;
	public static final int SMS_FLOWCTL_BY_MONTH             = 1;
	public static final int SMS_FLOWCTL_BY_DAY               = 2;
	
    //调用父类构造器   	
	public smsRouterDB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, 2);
		// TODO Auto-generated constructor stub
        Log.i(TAG,"smsRouterDB is created");
    }
	
	@Override  	
	public void onCreate(SQLiteDatabase db) {   		
        db.execSQL("CREATE TABLE if not exists " + "routerHistory" + " ( "
                + "FromName" + " TEXT NOT NULL, "
                + "FromNo" + " TEXT NOT NULL, "
                + "ToName" + " TEXT NOT NULL, "
                + "ToNo" + " TEXT NOT NULL, "
                + "date" + " TEXT NOT NULL, "
                + "content" + " TEXT NOT NULL)");
        db.execSQL("CREATE TABLE if not exists " + "forwardNo" + " ( "
                + "number TEXT NOT NULL, "
                + "time TEXT NOT NULL, "
                + "type INT NOT NULL)");
        db.execSQL("CREATE TABLE if not exists " + "flowctlRecord" + " ( "
                + SMS_FLOWCTL_TYPE + " INT NOT NULL, "
                + SMS_FLOWCTL_THRESHOLD + " INT NOT NULL)");
        db.execSQL("CREATE TABLE if not exists " + "flowctlCurrent" + " ( "
                + SMS_FLOWCTL_TYPE + " INT NOT NULL, "
                + SMS_FLOWCTL_CURR_YEAR + " TEXT NOT NULL, "
                + SMS_FLOWCTL_CURR_MONTH + " TEXT NOT NULL, "
                + SMS_FLOWCTL_CURR_DAY + " TEXT NOT NULL, "
                + SMS_FLOWCTL_CURR_CNT + " INT NOT NULL)");
        Log.i(TAG,"smsRouterDB is created");
	}   
	
	@Override  	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {   		
        Log.i(TAG,"smsRouterDB is upgraded");
	}    
  
}   