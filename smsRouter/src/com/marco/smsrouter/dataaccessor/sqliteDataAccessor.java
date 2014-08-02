package com.marco.smsrouter.dataaccessor;

import java.util.ArrayList;
import java.util.HashMap;

import com.marco.smsrouter.service.smsForwardProcesser;
import com.marco.smsrouter.service.smsForwardProcesser.smsFormat;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

public class sqliteDataAccessor extends dataAccessor {
	private smsRouterDB mSmsSqliteDb = null;
	public static final String dbPath = "/mnt/sdcard/smsRte/databases/";
	public static final String dbName = "smsrte.db";

	public sqliteDataAccessor(Context cont){
		mContext = cont;
		mfilePath = dbPath + dbName;
		mSmsSqliteDb = new smsRouterDB(cont, mfilePath, null, 2);
	}

	@Override
	public ArrayList<HashMap<String, Object>> getForwardHistory() {
		SQLiteDatabase db = mSmsSqliteDb.getReadableDatabase();
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

	@Override
	public ArrayList<HashMap<String, Object>> getForwardNo() {
		SQLiteDatabase db = mSmsSqliteDb.getReadableDatabase();
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

	@Override
	public int delAllForwardNo() {
		SQLiteDatabase db = mSmsSqliteDb.getWritableDatabase();
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

	@Override
	public int insertForwardNo(String number, String time, int type) {
		SQLiteDatabase db = null;

        try{
        	delAllForwardNo();
        	db = mSmsSqliteDb.getWritableDatabase();
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

	@Override
	public int insertForwardHistory(smsForwardProcesser.smsFormat forwardhistory) {
		SQLiteDatabase db = null;
		String FromName = forwardhistory.getSrcContactName();
		String FromNo   = forwardhistory.getSrcContactNo();
		String ToName   = forwardhistory.getDestName();
		String ToNo     = forwardhistory.getDestNo();
		String date     = forwardhistory.getDate();
		String content  = forwardhistory.getSmsMultiParts().toString();
		
        try{
        	db = mSmsSqliteDb.getWritableDatabase();
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

	@Override
	public int delForwardHistory(smsForwardProcesser.smsFormat forwardhistory) {
		SQLiteDatabase db = null;
		String FromName = forwardhistory.getSrcContactName();
		String FromNo   = forwardhistory.getSrcContactNo();
		String ToName   = forwardhistory.getDestName();
		String ToNo     = forwardhistory.getDestNo();
		String date     = forwardhistory.getDate();
		String content  = forwardhistory.getSmsMultiParts().toString();
		
        try{
        	db = mSmsSqliteDb.getWritableDatabase();
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

	@Override
	public String getContactName(String number) {
    	Log.i(TAG, "getContactName: " + number);
    	int idx = -1;
    	if((idx = number.indexOf("+86")) != -1){
    		number = number.substring(idx+3, number.length());
    	}
    	Log.i(TAG, "query number: " + number);
        Cursor cursor = queryContactByNumber(mContext, number);
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

	@Override
	public Cursor queryContactByNumber(Context cont, String number) {
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
	
	public int insertFlowCtlRecord(int type, int max){
		SQLiteDatabase db = null;

        try{
        	delAllFlowCtlRecord();
        	db = mSmsSqliteDb.getWritableDatabase();
            Log.i(TAG,"exe SQL: " + "insert into flowctlRecord values(" + type + ", " + max + ")");
            db.execSQL("insert into flowctlRecord values(" + type + ", " + max + ")");
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"insertFlowCtlRecord exception is caught:" + e.getMessage());
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
	
	public ArrayList<HashMap<String, Object>> getFlowCtlRecord() {
		SQLiteDatabase db = mSmsSqliteDb.getReadableDatabase();
		ArrayList<HashMap<String, Object>> flowCtlRecordList = new ArrayList<HashMap<String, Object>>();
		
        Cursor cursor = null;;
        try{
        	//使用execSQL方法查询数据
        	cursor = db.query("flowctlRecord", 
        			          new String[] {
        			               "flowctltype",
        			               "flowctlmax"
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
        
        
        //Log.i(TAG,"getFlowCtlRecord: count is " + cursor.getCount());
        if(cursor != null && cursor.getCount() != 0){
        	for(int i=0;i < cursor.getCount(); i++){
        		HashMap<String, Object> map = new HashMap<String, Object>();
            	cursor.moveToPosition(i);
                map.put("flowctltype", cursor.getInt(0));   
                //Log.i(TAG,"getFlowCtlRecord: flowctltype is " + cursor.getInt(0));
                map.put("flowctlmax", cursor.getInt(1)); 
                //Log.i(TAG,"getFlowCtlRecord: flowctlmax is " + cursor.getInt(1));

                flowCtlRecordList.add(map);
        	}
            cursor.close();
        }else{
        	if(cursor != null){
        		cursor.close();
        	}
        }
        db.close();
    	return flowCtlRecordList;
	}
	
	public int delAllFlowCtlRecord(){
		SQLiteDatabase db = mSmsSqliteDb.getWritableDatabase();
        try{
    		db.execSQL("delete from flowctlRecord");
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"delAllFlowCtlRecord exception is caught" + e.getMessage());
        	db.close();
        	return 1;
		}
		db.close();
		return 0;        
	}
	
	private int insertFlowCtlCurrent(int type, String year, String month, String day, int count) {
		SQLiteDatabase db = mSmsSqliteDb.getReadableDatabase();
		int retCode = 0;
		
        try{
        	//使用execSQL方法查询数据
        	db = mSmsSqliteDb.getWritableDatabase();
        	switch(type) {
        	case smsRouterDB.SMS_FLOWCTL_BY_YEAR:
                //Log.i(TAG,"exe SQL: " + "select flowctlCurrent values(NULL, '" + name + "')");
                db.execSQL("insert into flowctlCurrent values( " + type + " , '" + year + "', '0', '0', " + count + ")");
                break;
        	case smsRouterDB.SMS_FLOWCTL_BY_MONTH:
                Log.i(TAG,"exe SQL: " + "insert into flowctlCurrent values( " + type + " , '0', '" + month + "', '0', " + count + ")");
                db.execSQL("insert into flowctlCurrent values( " + type + " , '0', '" + month + "', '0', " + count + ")");
                break;
        	case smsRouterDB.SMS_FLOWCTL_BY_DAY:
                //Log.i(TAG,"exe SQL: " + "select flowctlCurrent values(NULL, '" + name + "')");
                db.execSQL("insert into flowctlCurrent values( " + type + " , '0', '0', '" + day + "', " + count + ")");
                break;
        	}
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"qury list exception is caught");
        	db.close();
        	retCode = -1;
		}
        
        db.close();

    	return retCode;
	}

	public int increaseFlowCtlCurrent(int type, String year, String month, String day){
		SQLiteDatabase db = mSmsSqliteDb.getReadableDatabase();
		int totalcount = 0;
		int retCode = 0;
		
        Cursor cursor = null;
        try{
        	//使用execSQL方法查询数据
        	db = mSmsSqliteDb.getWritableDatabase();
        	switch(type) {
        	case smsRouterDB.SMS_FLOWCTL_BY_YEAR:
                //Log.i(TAG,"exe SQL: " + "select flowctlCurrent values(NULL, '" + name + "')");
                //db.execSQL("insert flowctlCurrent where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=" + type + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_YEAR + "=" + year + ")");
                cursor = db.rawQuery("select * from flowctlCurrent where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=?" + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_YEAR + "=?",new String[]{""+type, year});
                break;
        	case smsRouterDB.SMS_FLOWCTL_BY_MONTH:
                Log.i(TAG,"exe SQL: " + "select * from flowctlCurrent where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=?" + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_MONTH + "=?");
                //db.execSQL("insert flowctlCurrent where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=" + type + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_MONTH + "=" + month + ")");
                cursor = db.rawQuery("select * from flowctlCurrent where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=?" + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_MONTH + "=?",new String[]{""+type, month});
                break;
        	case smsRouterDB.SMS_FLOWCTL_BY_DAY:
                Log.i(TAG,"exe SQL: " + "select * from flowctlCurrent where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=?" + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_DAY + "=?");
                //db.execSQL("insert flowctlCurrent where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=" + type + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_DAY + "=" + day + ")");
                cursor = db.rawQuery("select * from flowctlCurrent where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=?" + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_DAY + "=?",new String[]{""+type, day});
                break;
        	}
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"qury list exception is caught");
        	db.close();
        	if(cursor != null){
        		cursor.close();
        	}
        	retCode = -1;
		}
        
        //Log.i(TAG,"getFlowCtlCurrent: count is " + cursor.getCount());
        if(cursor != null && cursor.getCount() != 0){
            try{
        	    cursor.moveToPosition(0);
        	    totalcount = cursor.getInt(4);
        	    totalcount += 1;
        	    
            	switch(type) {
            	case smsRouterDB.SMS_FLOWCTL_BY_YEAR:
                    //Log.i(TAG,"exe SQL: " + "select flowctlCurrent values(NULL, '" + name + "')");
                    //db.execSQL("insert flowctlCurrent where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=" + type + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_YEAR + "=" + year + ")");
                    db.execSQL("update flowctlCurrent set " + smsRouterDB.SMS_FLOWCTL_CURR_CNT + "=" + totalcount + " where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=" + type + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_YEAR + "='" + year + "'");
                    break;
            	case smsRouterDB.SMS_FLOWCTL_BY_MONTH:
                    Log.i(TAG,"exe SQL: " + "update flowctlCurrent set " + smsRouterDB.SMS_FLOWCTL_CURR_CNT + "=" + totalcount + " where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=" + type + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_MONTH + "='" + month + "'");
                    //db.execSQL("insert flowctlCurrent where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=" + type + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_MONTH + "=" + month + ")");
                    db.execSQL("update flowctlCurrent set " + smsRouterDB.SMS_FLOWCTL_CURR_CNT + "=" + totalcount + " where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=" + type + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_MONTH + "='" + month + "'");
                    break;
            	case smsRouterDB.SMS_FLOWCTL_BY_DAY:
                    //Log.i(TAG,"exe SQL: " + "select flowctlCurrent values(NULL, '" + name + "')");
                    //db.execSQL("insert flowctlCurrent where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=" + type + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_DAY + "=" + day + ")");
                    db.execSQL("update flowctlCurrent set " + smsRouterDB.SMS_FLOWCTL_CURR_CNT + "=" + totalcount + " where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=" + type + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_DAY + "='" + day + "'");
                    break;
            	}
            	if(cursor != null){
            		cursor.close();
            	}
                db.close();
            } catch (Exception e) {
    			// TODO: handle exception
                Log.i(TAG,"qury list exception is caught");
            	db.close();
            	if(cursor != null){
            		cursor.close();
            	}
            	retCode = -1;
    		}
        } else {
        	if(cursor != null){
        		cursor.close();
        	}
            db.close();
        	delAllFlowCtlCurrent();
        	insertFlowCtlCurrent(type, year, month, day, 1);
        }

    	return retCode;
	}
	
	public int delAllFlowCtlCurrent(){
		SQLiteDatabase db = mSmsSqliteDb.getWritableDatabase();
        try{
    		db.execSQL("delete from flowctlCurrent");
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"delAllFlowCtlCurrent exception is caught" + e.getMessage());
        	db.close();
        	return 1;
		}
		db.close();
		return 0;        
	}
	
	public int getFlowCtlCurrent(int type, String year, String month, String day){
		SQLiteDatabase db = mSmsSqliteDb.getReadableDatabase();
		int totalcount = 0;
        Cursor cursor = null;
        try{
        	//使用execSQL方法查询数据
        	db = mSmsSqliteDb.getWritableDatabase();
        	switch(type) {
        	case smsRouterDB.SMS_FLOWCTL_BY_YEAR:
                Log.i(TAG,"exe SQL: " + "select * from flowctlCurrent where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=?" + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_YEAR + "=?");
                cursor = db.rawQuery("select * from flowctlCurrent where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=?" + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_YEAR + "=?",new String[]{""+type, year});
                break;
        	case smsRouterDB.SMS_FLOWCTL_BY_MONTH:
                Log.i(TAG,"exe SQL: " + "select * from flowctlCurrent where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=?" + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_MONTH + "=?");
                cursor = db.rawQuery("select * from flowctlCurrent where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=?" + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_MONTH + "=?",new String[]{""+type, month});
                break;
        	case smsRouterDB.SMS_FLOWCTL_BY_DAY:
                Log.i(TAG,"exe SQL: " + "select * from flowctlCurrent where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=?" + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_DAY + "=?");
                cursor = db.rawQuery("select * from flowctlCurrent where " + smsRouterDB.SMS_FLOWCTL_TYPE + "=?" + " AND " + smsRouterDB.SMS_FLOWCTL_CURR_DAY + "=?",new String[]{""+type, day});
                break;
        	}
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"qury list exception is caught");
        	db.close();
        	if(cursor != null){
        		cursor.close();
        	}
        	return 0;
		}
        
        
        //Log.i(TAG,"getFlowCtlCurrent: count is " + cursor.getCount());
        if(cursor != null && cursor.getCount() != 0){
        	for(int i=0;i < cursor.getCount(); i++){
            	cursor.moveToPosition(i);
            	totalcount = cursor.getInt(4);
        	}
            cursor.close();
        }else{
        	if(cursor != null){
        		cursor.close();
        	}
        }
        db.close();
    	return totalcount;
	}


}
