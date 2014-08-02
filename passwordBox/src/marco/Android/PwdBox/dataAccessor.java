package marco.Android.PwdBox;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

public class dataAccessor{  
	private static final String TAG = "marco.Android.PwdBox.dataAccessor";
    private pwdDB pwdDBAccessor;
    
	public dataAccessor(Context cont, String dbname){
		pwdDBAccessor = new pwdDB(cont, dbname, null, 2);
	}

	//添加黑名单到数据库
	public int insertPwdList(int category, String desc, String account, String hint, int dispmod, int encrymod, String pwd) {
		SQLiteDatabase db = pwdDBAccessor.getWritableDatabase();
		String encry_pwd = pwd;
		
		if(encrymod == 1){
			try {
				if(tabActivity.user_key == null){
					return 2;
				}
				String unencry_key = tabActivity.crypto.decrypt(tabActivity.global_key, tabActivity.user_key);
				encry_pwd = tabActivity.crypto.encrypt(unencry_key, pwd);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
	        	db.close();
	        	return 1;
			}
		}
		
        try{
        	
        	//使用execSQL方法查询数据
            //Log.i(TAG,"exe SQL: " + "insert into list values(" + category + ", '" + desc + "', '" + account +  "', '" + hint +  "', '" + dispmod +  "', '" + encrymod +  "', '" + encry_pwd + "')");
            db.execSQL("insert into list values(" + category + ", '" + desc + "', '" + account +  "', '" + hint +  "', '" + dispmod +  "', '" + encrymod +  "', '" + encry_pwd + "')");
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"insertPwdList exception is caught" + e.getMessage());
        	db.close();
        	return 3;
		}
        db.close();
        return 0;        
    }

	//删除黑名单
	public int removePwdList(int category, String desc, String account) {
		SQLiteDatabase db = pwdDBAccessor.getWritableDatabase();
        try{
        	
        	//使用execSQL方法查询数据
            //Log.i(TAG,"exe SQL: " + "delete from list where category = " + category + " AND desc='" + desc + "' AND account='" + account + "'");
    		db.execSQL("delete from list where category = " + category + " AND desc='" + desc + "' AND account='" + account + "'");
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"removePwdList exception is caught" + e.getMessage());
        	db.close();
        	return 1;
		}
		db.close();
		return 0;        
    }

	//获取黑名单
	public ArrayList<HashMap<String, Object>> getPwdList() {
		SQLiteDatabase db = pwdDBAccessor.getReadableDatabase();
		ArrayList<HashMap<String, Object>> pwdList = new ArrayList<HashMap<String, Object>>();
		
        Cursor cursor = null;;
        try{
        	//使用execSQL方法查询数据
        	cursor = db.query("list", 
        			          new String[] {
        			               "category",
        			               "desc",
        			               "account",
        			               "hint",
        			               "dispmod",
        			               "encrymod",
        			               "pwd"
        	                  }, 
        	                  null, null, null, null, null);
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"qury list exception is caught");
        	db.close();
        	if(cursor != null){
        		cursor.close();
        	}
        	return null;
		}
        
        
        //Log.i(TAG,"getPwdList: count is " + cursor.getCount());
        if(cursor != null && cursor.getCount() != 0){
        	for(int i=0;i < cursor.getCount(); i++){
        		HashMap<String, Object> map = new HashMap<String, Object>();
            	cursor.moveToPosition(i);
                map.put("category", cursor.getInt(0));   
                //Log.i(TAG,"getPwdList: category is " + cursor.getInt(0));
                map.put("desc", cursor.getString(1).toString()); 
                //Log.i(TAG,"getPwdList: desc is " + cursor.getString(1).toString());
                map.put("account", cursor.getString(2).toString()); 
                //Log.i(TAG,"getPwdList: account is " + cursor.getString(2).toString());
                map.put("hint", cursor.getString(3).toString());   
                //Log.i(TAG,"getPwdList: hint is " + cursor.getString(3).toString());
                map.put("dispmod", cursor.getInt(4)); 
                //Log.i(TAG,"getPwdList: dispmod is " + cursor.getInt(4));
                map.put("encrymod", cursor.getInt(5)); 
                //Log.i(TAG,"getPwdList: encrymod is " + cursor.getInt(5));
 
                if(cursor.getInt(5) == 1){
            		String encry_pwd = cursor.getString(6).toString();
            		
            		try {
        				if(tabActivity.user_key != null){
            				String unencry_key = tabActivity.crypto.decrypt(tabActivity.global_key, tabActivity.user_key);
                			encry_pwd = tabActivity.crypto.decrypt(unencry_key, cursor.getString(6).toString());
        				}
            		} catch (Exception e) {
            			// TODO Auto-generated catch block
            			e.printStackTrace();
            		}

                    map.put("pwd", encry_pwd); 
                    //Log.i(TAG,"getPwdList: pwd is " + encry_pwd);
                } else {
                    map.put("pwd", cursor.getString(6).toString()); 
                }
                //Log.i(TAG,"getPwdList: category is " + cursor.getString(0).toString() + ", desc is " + cursor.getString(1).toString());
                pwdList.add(map);
        	}
            cursor.close();
        }else{
        	if(cursor != null){
        		cursor.close();
        	}
        }
        db.close();
    	return pwdList;
    }
	
	//获取黑名单
	public ArrayList<HashMap<String, Object>> queryPwdList(String express) {
		SQLiteDatabase db = pwdDBAccessor.getReadableDatabase();
		ArrayList<HashMap<String, Object>> pwdList = new ArrayList<HashMap<String, Object>>();
		
        //Log.i(TAG,"exe SQL: " + "query pwd list where " + express);
        Cursor cursor = null;;
        try{
        	//使用execSQL方法查询数据
        	cursor = db.query("list", 
        			          new String[] {
        			               "category",
        			               "desc",
        			               "account",
        			               "hint",
        			               "dispmod",
        			               "encrymod",
        			               "pwd"
        	                  }, 
        	                  express, null, null, null, null);
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"qury list exception is caught");
        	db.close();
        	if(cursor != null){
        		cursor.close();
        	}
        	return pwdList;
		}
        
        
        //Log.i(TAG,"queryPwdList: count is " + cursor.getCount());
        if(cursor != null && cursor.getCount() != 0){
        	for(int i=0;i < cursor.getCount(); i++){
        		HashMap<String, Object> map = new HashMap<String, Object>();
            	cursor.moveToPosition(i);
                map.put("category", cursor.getInt(0));   
                //Log.i(TAG,"queryPwdList: category is " + cursor.getInt(0));
                map.put("desc", cursor.getString(1).toString()); 
                //Log.i(TAG,"queryPwdList: desc is " + cursor.getString(1).toString());
                map.put("account", cursor.getString(2).toString()); 
                //Log.i(TAG,"queryPwdList: account is " + cursor.getString(2).toString());
                map.put("hint", cursor.getString(3).toString());   
                //Log.i(TAG,"queryPwdList: hint is " + cursor.getString(3).toString());
                map.put("dispmod", cursor.getInt(4)); 
                //Log.i(TAG,"queryPwdList: dispmod is " + cursor.getInt(4));
                map.put("encrymod", cursor.getInt(5)); 
                //Log.i(TAG,"queryPwdList: encrymod is " + cursor.getInt(5));

                if(cursor.getInt(5) == 1){
            		String encry_pwd = cursor.getString(6).toString();
            		
            		try {
        				if(tabActivity.user_key != null){
            				String unencry_key = tabActivity.crypto.decrypt(tabActivity.global_key, tabActivity.user_key);
                			encry_pwd = tabActivity.crypto.decrypt(unencry_key, cursor.getString(6).toString());
        				}
            		} catch (Exception e) {
            			// TODO Auto-generated catch block
            			e.printStackTrace();
            		}

                    map.put("pwd", encry_pwd); 
                    //Log.i(TAG,"getPwdList: pwd is " + encry_pwd);
                } else {
                    map.put("pwd", cursor.getString(6).toString()); 
                }
                //Log.i(TAG,"getPwdList: category is " + cursor.getString(0).toString() + ", desc is " + cursor.getString(1).toString());
                pwdList.add(map);
        	}
            cursor.close();
        }else{
        	if(cursor != null){
        		cursor.close();
        	}
        }
        db.close();
    	return pwdList;
    }
	
	//添加黑名单到数据库
	public int insertCategory(String name) {
		SQLiteDatabase db = null;
		
        try{
        	if(!isCategoryInlist(name)){
        		db = pwdDBAccessor.getWritableDatabase();
            	//使用execSQL方法查询数据
                //Log.i(TAG,"exe SQL: " + "insert into categorytb values(NULL, '" + name + "')");
                db.execSQL("insert into categorytb values(NULL, '" + name + "')");
        	}
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"insertPwdList exception is caught:" + e.getMessage());
            if(db != null){
        	    db.close();
            }
        	return 1;
		}
        if(db != null){
    	    db.close();
        }
        return 0;        
    }

	//删除黑名单
	public int removeCategory(int category) {
		SQLiteDatabase db = pwdDBAccessor.getWritableDatabase();
        try{
        	
        	//使用execSQL方法查询数据
            //Log.i(TAG,"exe SQL: " + "delete from categorytb where idx = " + category);
    		db.execSQL("delete from categorytb where idx = " + category);
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"removePwdList exception is caught" + e.getMessage());
        	db.close();
        	return 1;
		}
		db.close();
		return 0;        
    }
	
	//获取黑名单
	public ArrayList<String> getCategoryList() {
		SQLiteDatabase db = pwdDBAccessor.getReadableDatabase();
		ArrayList<String> categoryList = new ArrayList<String>();
		
        Cursor cursor = null;;
        try{
        	//使用execSQL方法查询数据
        	cursor = db.query("categorytb", 
        			          new String[] {
        			               "idx",
        			               "category"
        	                  }, 
        	                  null, null, null, null, null);
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"qury list exception is caught");
        	db.close();
        	if(cursor != null){
        		cursor.close();
        	}
        	return null;
		}
        
        
        //Log.i(TAG,"getCategoryList: count is " + cursor.getCount());
        if(cursor != null && cursor.getCount() != 0){
        	for(int i=0;i < cursor.getCount(); i++){
            	cursor.moveToPosition(i);
                //Log.i(TAG,"getCategoryList: category is " + cursor.getInt(0) + " " + cursor.getString(1).toString());
        		categoryList.add(cursor.getString(1).toString());
        	}
            cursor.close();
        }else{
        	if(cursor != null){
        		cursor.close();
        	}
        }
        db.close();
    	return categoryList;
    }
	

	//查找给定归属地是否在黑名单中
	public boolean isCategoryInlist(String category) {
		SQLiteDatabase db = pwdDBAccessor.getReadableDatabase();
		
		String selection = "category LIKE '" + category + "'";

        Cursor cursor = null;;
        try{
        	//使用execSQL方法查询数据
        	cursor = db.query("categorytb", 
        			          new String[] {
        			               "idx",
        			               "category"
        	                  }, 
        	                  selection, null, null, null, null);
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"isCategoryInlist exception is caught");
        	db.close();
        	if(cursor != null){
        		cursor.close();
        	}
        	return false;
		}
        
        
        if(cursor != null && cursor.getCount() != 0){
            cursor.close();
        	db.close();
            return true;
        }else{
        	if(cursor != null){
        		cursor.close();
        	}
        }
        db.close();
    	return false;
    }

	//查找给定归属地是否在黑名单中
	public int getCategoryIndex(String category) {
		SQLiteDatabase db = pwdDBAccessor.getReadableDatabase();
		
		String selection = "category LIKE '" + category + "'";

        Cursor cursor = null;;
        try{
        	//使用execSQL方法查询数据
        	cursor = db.query("categorytb", 
        			          new String[] {
        			               "idx",
        			               "category"
        	                  }, 
        	                  selection, null, null, null, null);
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"quryCallHistory exception is caught");
        	db.close();
        	if(cursor != null){
        		cursor.close();
        	}
        	return -1;
		}
        
        
        if(cursor != null && cursor.getCount() != 0){
        	int index = -1;
        	for(int i=0;i < cursor.getCount(); i++){
            	cursor.moveToPosition(0);
                //Log.i(TAG,"getCategoryIndex: index is " + cursor.getInt(0) + " " + cursor.getString(1).toString());
                index = cursor.getInt(0);
        	}

            cursor.close();
        	db.close();
            return index;
        }else{
        	if(cursor != null){
        		cursor.close();
        	}
        }
        db.close();
    	return -1;
    }
	
}  