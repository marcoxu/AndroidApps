package com.marco.smsrouter;

import java.util.ArrayList;
import java.util.HashMap;

import com.marco.smsrouter.dataaccessor.smsRouterDB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

public class dbAccessor {
    private static final String TAG = "smsRouter.dbAccessor";
    private Context cntx = null;
    private smsRouterDB smsRouterDBAccessor = null;
    
	public dbAccessor(Context cont, String dbname){
		cntx = cont;
		smsRouterDBAccessor = new smsRouterDB(cont, dbname, null, 2);
	}

	public ArrayList<HashMap<String, Object>> getForwardHistory() {
		SQLiteDatabase db = smsRouterDBAccessor.getReadableDatabase();
		ArrayList<HashMap<String, Object>> historyList = new ArrayList<HashMap<String, Object>>();
		
        Cursor cursor = null;;
        try{
        	//使用execSQL方法查询数据
        	cursor = db.query("routerHistory", 
        			          new String[] {
        			               "FromName",
        			               "FromNo",
        			               "ToName",
        			               "ToNo",
        			               "date",
        			               "content"
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
        
        
        //Log.i(TAG,"getForwardHistory: count is " + cursor.getCount());
        if(cursor != null && cursor.getCount() != 0){
        	for(int i=cursor.getCount()-1;i >= 0; i--){
        		HashMap<String, Object> map = new HashMap<String, Object>();
            	cursor.moveToPosition(i);
                map.put("FromName", cursor.getString(0).toString());   
                //Log.i(TAG,"getForwardHistory: FromName is " + cursor.getString(0).toString());
                map.put("FromNo", cursor.getString(1).toString()); 
                //Log.i(TAG,"getForwardHistory: FromNo is " + cursor.getString(1).toString());
                map.put("ToName", cursor.getString(2).toString()); 
                //Log.i(TAG,"getForwardHistory: ToName is " + cursor.getString(2).toString());
                map.put("ToNo", cursor.getString(3).toString());   
                //Log.i(TAG,"getForwardHistory: ToNo is " + cursor.getString(3).toString());
                map.put("date", cursor.getString(4).toString()); 
                //Log.i(TAG,"getForwardHistory: date is " + cursor.getString(4).toString());
                map.put("content", cursor.getString(5).toString()); 
                //Log.i(TAG,"getForwardHistory: content is " + cursor.getString(5).toString());

                historyList.add(map);
        	}
            cursor.close();
        }else{
        	if(cursor != null){
        		cursor.close();
        	}
        }
        db.close();
    	return historyList;
    }

	public ArrayList<HashMap<String, Object>> getForwardNo() {
		SQLiteDatabase db = smsRouterDBAccessor.getReadableDatabase();
		ArrayList<HashMap<String, Object>> forwardList = new ArrayList<HashMap<String, Object>>();
		
        Cursor cursor = null;;
        try{
        	//使用execSQL方法查询数据
        	cursor = db.query("forwardNo", 
        			          new String[] {
        			               "number",
        			               "time",
        			               "type"
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
        
        
        //Log.i(TAG,"getForwardNo: count is " + cursor.getCount());
        if(cursor != null && cursor.getCount() != 0){
        	for(int i=0;i < cursor.getCount(); i++){
        		HashMap<String, Object> map = new HashMap<String, Object>();
            	cursor.moveToPosition(i);
                map.put("number", cursor.getString(0).toString());   
                //Log.i(TAG,"getForwardNo: number is " + cursor.getString(0).toString());
                map.put("time", cursor.getString(1).toString()); 
                //Log.i(TAG,"getForwardNo: time is " + cursor.getString(1).toString());
                map.put("type", cursor.getInt(2)); 
                //Log.i(TAG,"getForwardNo: type is " + cursor.getInt(2));

                forwardList.add(map);
        	}
            cursor.close();
        }else{
        	if(cursor != null){
        		cursor.close();
        	}
        }
        db.close();
    	return forwardList;
    }

	public int delAllForwardNo() {
		SQLiteDatabase db = smsRouterDBAccessor.getWritableDatabase();
        try{
    		db.execSQL("delete from forwardNo");
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"delAllForwardNo exception is caught" + e.getMessage());
        	db.close();
        	return 1;
		}
		db.close();
		return 0;        
    }

	public int insertForwardNo(String number, String time, int type) {
		SQLiteDatabase db = null;
		
        try{
        	delAllForwardNo();
        	db = smsRouterDBAccessor.getWritableDatabase();
            //Log.i(TAG,"exe SQL: " + "insert into forwardNo values(NULL, '" + name + "')");
            db.execSQL("insert into forwardNo values('" + number + "', '" + time + "', " + type + ")");
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"insertForwardNo exception is caught:" + e.getMessage());
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

	public int insertForwardHistory(String FromName, String FromNo, String ToName, String ToNo, String date, String content) {
		SQLiteDatabase db = null;
		
        try{
        	db = smsRouterDBAccessor.getWritableDatabase();
            Log.i(TAG,"exe SQL: " + "insert into routerHistory values('" + FromName + "', '" + FromNo + "', '" + ToName +  "', '" + ToNo +  "', '" + date +  "', '" + content + "')");
            db.execSQL("insert into routerHistory values('" + FromName + "', '" + FromNo + "', '" + ToName +  "', '" + ToNo +  "', '" + date +  "', '" + content + "')");
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"insertForwardHistory exception is caught:" + e.getMessage());
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

	public int delForwardHistory(String FromName, String FromNo, String ToName, String ToNo, String date, String content) {
		SQLiteDatabase db = null;
		
        try{
        	db = smsRouterDBAccessor.getWritableDatabase();
            Log.i(TAG,"exe SQL: " + "delete from routerHistory where FromName = '" + FromName + "' and FromNo  = '" + FromNo + "' and ToName = '" + ToName +  "' and ToNo = '" + ToNo +  "' and date = '" + date +  "' and content = '" + content + "'");
            db.execSQL("delete from routerHistory where FromName = '" + FromName + "' and FromNo  = '" + FromNo + "' and ToName = '" + ToName +  "' and ToNo = '" + ToNo +  "' and date = '" + date +  "' and content = '" + content + "'");
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"insertForwardHistory exception is caught:" + e.getMessage());
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

	public String getContactName(String number){
    	Log.i(TAG, "getContactName: " + number);
    	int idx = -1;
    	if((idx = number.indexOf("+86")) != -1){
    		number = number.substring(idx+3, number.length());
    	}
    	Log.i(TAG, "query number: " + number);
        Cursor cursor = queryContactByNumber(cntx, number);
        if(cursor == null || cursor.getCount() == 0){
            if(cursor != null){
            	cursor.close();
            }
            return number;
        }else{
        	cursor.moveToFirst();
            String contactName = cursor.getString(1);
            cursor.close();
            return contactName;
        }
	}

	//获取指定号码对应的联系人记录
	public static Cursor queryContactByNumber(Context cont, String number){
		String filter = ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE '%" + number + "%'";
    	final Cursor numberResult = cont.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
                                                              new String[]{
    			                                                   ContactsContract.PhoneLookup.DISPLAY_NAME,
    			                                                   ContactsContract.CommonDataKinds.Phone.NUMBER,
    			                                                   ContactsContract.Contacts._ID
                                                              }, 
                                                              filter,
    			                                              null, 
    			                                              null);

    	Cursor contact = null;
    	if(numberResult != null && numberResult.getCount() > 0){
    		String nameFilter = null;
        	for(int i = 0; i < numberResult.getCount(); i ++){
        		numberResult.moveToPosition(i);
        		if(nameFilter == null || nameFilter.length() == 0){
            	    nameFilter = PhoneLookup.DISPLAY_NAME + " = '" + numberResult.getString(0) + "' ";
        		}else{
            	    nameFilter = nameFilter + PhoneLookup.DISPLAY_NAME + " = '" + numberResult.getString(0) + "' ";
        		}
        	    if((i+1) < numberResult.getCount()){
            	    nameFilter = nameFilter + " OR ";
        	    }
        	}

            contact = cont.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, 
                                                                  new String[]{
        			                                                   PhoneLookup._ID,
        			                                                   PhoneLookup.DISPLAY_NAME,
        			                                                   PhoneLookup.LAST_TIME_CONTACTED, 
        			                                                   PhoneLookup.HAS_PHONE_NUMBER
                                                                  }, 
                                                                  nameFilter,
        			                                              null, 
        			                                              null);

    	}
    	return contact;
    }
}
