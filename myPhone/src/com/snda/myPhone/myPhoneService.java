package com.snda.myPhone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

// 启动服务用于监听来电及短信
public class myPhoneService extends Service {
	private static final String TAG = "com.snda.myPhone.myPhoneService";
	private smsListener listener;
	private smsReceiver recevier;  
	private phoneStateListener phoneCallListener;
	
	public void onCreate() {
		Log.i(TAG, "myPhoneService.onCreate");
    	recevier = new smsReceiver(this);
    	listener = new smsListener(this);
    	phoneCallListener = new phoneStateListener(getApplicationContext());
    	loadPhoneLocationDB();
    	listener.register(recevier);
		super.onCreate();
	}
	
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "myPhoneService.onStart");
		super.onStart(intent, startId);
	}
	
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "myPhoneService.onBind");
		return null;
	}
	
	public class LocalBinder extends Binder {
		public myPhoneService getService() {
			return myPhoneService.this;
		}
	}
	
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "myPhoneService.onUnbind");
		return false;
	}
	
	public void onRebind(Intent intent) {
		Log.i(TAG, "myPhoneService.onRebind");
	}
	
	public void onDestroy() {
		Log.i(TAG, "myPhoneService.onDestroy");
		if (listener != null) {  
			listener.unregister(recevier);  
		}
		super.onDestroy();
	}

    //拷贝Assets下的号码归属地数据库文件
    private void loadPhoneLocationDB(){
        // 拷贝号码归属地数据库到/data/com.snda.myPhone/databases
    	String filePath = myPhoneActivity.dbPath + myPhoneActivity.dbName;
        InputStream assetsDB = null;
		try {
			assetsDB = this.getAssets().open("phoneLocation.jpg");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		File dbdir = new File(myPhoneActivity.dbPath); 
        if (!dbdir.exists()) { 
              try { 
                  //在指定的文件夹中创建文件 
            	  dbdir.mkdirs(); 
            } catch (Exception e) { 
    			e.printStackTrace();
            } 
        } 
		File phonelocationdbfile = new File(filePath); 
        if (!phonelocationdbfile.exists()) { 
        	try { 
        		//在指定的文件夹中创建文件 
        		phonelocationdbfile.createNewFile(); 
        	} catch (Exception e) { 
        		e.printStackTrace();
        	} 

        	OutputStream dbOut = null;
        	try {
        		dbOut = new FileOutputStream(filePath, true);
        	} catch (FileNotFoundException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
        	
        	byte[] buffer = new byte[1024];
        	int length = 0;
        	try {
        		while ((length = assetsDB.read(buffer)) > 0) {
        			dbOut.write(buffer, 0, length);
        		}
        	} catch (IOException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
	        try {
				dbOut.flush();
				dbOut.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    	
        	Log.i(TAG, "Finish copying DB");
        }
        
        
        try {
			assetsDB.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
}