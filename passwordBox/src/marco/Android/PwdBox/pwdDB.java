package marco.Android.PwdBox;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory; 
import android.util.Log;

//SQLite���ݿ������
public class pwdDB extends SQLiteOpenHelper{
	private static final String TAG = "marco.Android.PwdBox.pwdDB";
	
    //���ø��๹����   	
	public pwdDB(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, 2);
		// TODO Auto-generated constructor stub
        Log.i(TAG,"pwdDB is created");
    }
	
	@Override  	
	public void onCreate(SQLiteDatabase db) {   		
        db.execSQL("CREATE TABLE if not exists " + "categorytb" + " ( "
                + "idx INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "category TEXT NOT NULL)");
        
        db.execSQL("CREATE TABLE if not exists " + "list" + " ( "
                + "Category" + " INTEGER, "
                + "desc" + " TEXT NOT NULL, "
                + "account" + " TEXT NOT NULL, "
                + "hint" + " TEXT , "
                + "dispmod" + " INTEGER, "
                + "encrymod" + " INTEGER, "
                + "pwd" + " TEXT NOT NULL, FOREIGN KEY(Category) REFERENCES categorytb(idx), PRIMARY KEY(Category, desc, account))");
        db.execSQL("CREATE TABLE if not exists " + "configure" + " ( "
                + "config TEXT NOT NULL, "
                + "value INTEGER)");
        Log.i(TAG,"pwdDB is onCreated");
	}   
	
    //�������ݿ�ʱ����İ汾���뵱ǰ�İ汾�Ų�ͬʱ����ø÷���   	
	@Override  	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {   		
        Log.i(TAG,"pwdDB is upgraded");
	}    
  
}   