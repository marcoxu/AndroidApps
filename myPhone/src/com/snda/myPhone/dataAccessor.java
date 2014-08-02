/*
 * Copyright (c) 2012 Shanda Corporation. All rights reserved.
 *
 * Created on 2012-01-30.
 */
package com.snda.myPhone;

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
import com.snda.myPhone.phoneLocationDB;

public class dataAccessor{  
    private static final String TAG = "myPhone.dataAccessor";
    private phoneLocationDB phoneLocationDBAccessor;
    
	public dataAccessor(Context cont, String dbname){
		phoneLocationDBAccessor = new phoneLocationDB(cont, dbname, null, 2);
	}

	//添加黑名单到数据库
	public int insertBlacklist(int blockType, String name, String blockContent) {
		SQLiteDatabase db = phoneLocationDBAccessor.getWritableDatabase();
		
        Log.i(TAG,"insertBlacklist: blockType is " + blockType + ", name is " + name + ", content is " + blockContent);
		String type = null;
		switch(blockType){
		case 0:
			type = "location";
	        db.execSQL("insert into blacklist values('" + type + "', '" + name + "', '" + blockContent + "')");
			break;
		case 1:
			type = "number";
	        db.execSQL("insert into blacklist values('" + type + "', '" + name + "', '" + blockContent + "')");
			break;
		default:
	        Log.i(TAG,"insertBlacklist: invalid block type " + blockType);
		}
        db.close();
       return 0;        
    }

	//删除黑名单
	public int removeBlacklist(int blockType, String blockContent) {
		SQLiteDatabase db = phoneLocationDBAccessor.getWritableDatabase();
        Log.i(TAG,"removeBlacklist: block type " + blockType + ", content " + blockContent);
        
		switch(blockType){
		case 0:
			db.execSQL("delete from blacklist where blockType = 'location' AND blockContent='" + blockContent + "'");
			break;
		case 1:
			db.execSQL("delete from blacklist where blockType = 'number' AND blockContent='" + blockContent + "'");
			break;
		default:
	        Log.i(TAG,"removeBlacklist: invalid block type " + blockType);
		}
		db.close();
		return 0;        
    }

	//查找给定归属地是否在黑名单中
	public boolean isLocationInBlacklist(String location) {
		SQLiteDatabase db = phoneLocationDBAccessor.getReadableDatabase();
		
		String selection = "blockType = 'location' AND blockContent LIKE '%" + location + "%'";

        Cursor cursor = null;;
        try{
        	//使用execSQL方法查询数据
        	cursor = db.query("blacklist", 
        			          new String[] {
        			               "blockType",
        			               "name",
        			               "blockContent"
        	                  }, 
        	                  selection, null, null, null, null);
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"quryCallHistory exception is caught");
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

	//查找给定号码是否在黑名单中
	public boolean isNumberInBlacklist(String number) {
		SQLiteDatabase db = phoneLocationDBAccessor.getReadableDatabase();
		
		String selection = "blockType = 'number' AND blockContent LIKE '%" + number + "%'";

        Cursor cursor = null;;
        try{
        	//使用execSQL方法查询数据
        	cursor = db.query("blacklist", 
        			          new String[] {
        			               "blockType",
        			               "name",
        			               "blockContent"
        	                  }, 
        	                  selection, null, null, null, null);
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"qury blacklist exception is caught");
        	db.close();
        	if(cursor != null){
        		cursor.close();
        	}
        	return false;
		}
        
        Log.i(TAG,"query blacklist (" + selection + ") returns " + (cursor==null?0:cursor.getCount()));
       
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

	//获取黑名单
	public ArrayList<HashMap<String, Object>> getBlacklist() {
		SQLiteDatabase db = phoneLocationDBAccessor.getReadableDatabase();
		ArrayList<HashMap<String, Object>> blacklist = new ArrayList<HashMap<String, Object>>();
		
        Cursor cursor = null;;
        try{
        	//使用execSQL方法查询数据
        	cursor = db.query("blacklist", 
        			          new String[] {
        			               "blockType",
        			               "name",
        			               "blockContent"
        	                  }, 
        	                  null, null, null, null, null);
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"quryCallHistory exception is caught");
        	db.close();
        	if(cursor != null){
        		cursor.close();
        	}
        	return null;
		}
        
        
        if(cursor != null && cursor.getCount() != 0){
        	for(int i=0;i < cursor.getCount(); i++){
        		HashMap<String, Object> map = new HashMap<String, Object>();
            	cursor.moveToPosition(i);
                map.put("blockType", cursor.getString(0).toString());   
                map.put("name", cursor.getString(1).toString()); 
                map.put("blockContent", cursor.getString(2).toString()); 
                Log.i(TAG,"getBlacklist: blockType is " + cursor.getString(0).toString() + ", content is " + cursor.getString(1).toString());
                blacklist.add(map);
        	}
            cursor.close();
        }else{
        	if(cursor != null){
        		cursor.close();
        	}
        }
        db.close();
    	return blacklist;
    }

	//添加通话历史记录数据项到数据库
	public int insertCallHistory(String num, String type, int time) {
		SQLiteDatabase db = phoneLocationDBAccessor.getWritableDatabase();
		
        db.execSQL("insert into callhistory values('" + num + "', '" + type + "', " + time + ")");
        db.close();
       return 0;        
    }

	//查询指定号码的通话历史记录
	public ArrayList<HashMap<String, Object>> quryCallHistory(String number) {
		SQLiteDatabase db = phoneLocationDBAccessor.getReadableDatabase();
		
		String selection = "num LIKE '%" + number + "%'";
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

        Cursor cursor = null;;
        try{
        	
        	//使用execSQL方法查询数据
        	cursor = db.query("callhistory", 
        			          new String[] {
        			               "num",
        			               "type",
        			               "ringtime"
        	                  }, 
        	                  selection, null, null, null, "num asc");
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"quryCallHistory exception is caught");
        	db.close();
        	return null;
		}
        
        
        if(cursor != null && cursor.getCount() != 0){
        	for(int i=0;i < cursor.getCount(); i++){
        		HashMap<String, Object> map = new HashMap<String, Object>();
            	cursor.moveToPosition(i);
                map.put("num", cursor.getString(0));   
                map.put("type", cursor.getString(1));   
                map.put("ringtime", cursor.getInt(2));   
                list.add(map);
        	}
            cursor.close();
        }else{
        	if(cursor != null){
        		cursor.close();
        	}
        }
        db.close();
    	return list;
    }

	//从号码中获取归属地数据库查询关键字
	private String getQueryPhoneNumber(String sender){
        String queryPhoneNum = "";
        
        if(sender.startsWith("+86")){
        	//号码带国际区号
        	sender = sender.substring(3);
        }
        
        if(sender.startsWith("0")){
            //固话号码带区号
        	if(sender.charAt(1) == '2' ||
        	   sender.charAt(1) == '1'){
                //3位区号
        		queryPhoneNum = sender.substring(0, 3);
        	}else{
        		queryPhoneNum = sender.substring(0, 4);
        	}
        }else{
        	if(sender.length() >= 7){
    		    queryPhoneNum = sender.substring(0, 7);
        	}else{
    		    queryPhoneNum = sender.substring(0, sender.length());
        	}
        }
        return queryPhoneNum;
    }

	//查询指定号码的归属地
	public HashMap<String, Object> queryPhoneLocation(String number) {
		SQLiteDatabase db = phoneLocationDBAccessor.getReadableDatabase();
		
		String queryNum = getQueryPhoneNumber(number);
		String selection = "num LIKE '" + queryNum + "%'";
        Log.i(TAG,"queryPhoneLocation selection " + selection);
		HashMap<String, Object> map = null;

        Cursor cursor = null;;
        try{
        	
        	//使用execSQL方法查询数据
        	cursor = db.query("list", 
        			          new String[] {
        			               "num",
        			               "area",
        			               "t"
        	                  }, 
        	                  selection, null, null, null, "num asc");
        }catch (Exception e) {
			// TODO: handle exception
            Log.i(TAG,"queryPhoneLocation exception is caught");
        	db.close();
        	return map;
		}
        
		if(cursor == null){
	    	db.close();
        	return map;
		}
        if(cursor.getCount() != 1){
        	//号码没有或有多于一个的归属地
            Log.i(TAG,"number " + number + "'s location is unknown " + cursor.getCount());
        }else{
        	cursor.moveToFirst();
        	map = new HashMap<String, Object>();
        	Log.i(TAG,"number " + number + " is from " + cursor.getString(1)); 
        	map.put("location", cursor.getString(1));
        	map.put("type", cursor.getString(2));
        }
        
        cursor.close();
    	db.close();
    	return map;
    }

	//获取未读短信的数目
	public static int getUnreadSms(Context cont) {
        int unreadSmsCount = 0;
        Cursor cursor = cont.getContentResolver().query(Uri.parse("content://sms/inbox"),
                                                           new String[] {
        		                                                 "date",
        		                                                 "address",
        		                                                 "person",
        		                                                 "body"},
                                                                 "read = 0",
                                                            null,
                                                            null);

        unreadSmsCount = cursor.getCount();
        return unreadSmsCount;        
    }

	//获取未接来电的数目
	public static int getMissedCalls(Context cont) {
        int missedCallCount = 0;
        Cursor callCursor = cont.getContentResolver().query(Calls.CONTENT_URI, 
        		                                            new String[] { 
        		                                                       Calls.NUMBER, 
        		                                                       Calls.TYPE, 
        		                                                       Calls.NEW }, 
        		                                            null, 
        		                                            null, 
        		                                            Calls.DEFAULT_SORT_ORDER);
        if (callCursor != null) {
            while (callCursor.moveToNext()) {
                int type = callCursor.getInt(callCursor.getColumnIndex(Calls.TYPE));
                switch (type) {
                case Calls.MISSED_TYPE:
                    if (callCursor.getInt(callCursor.getColumnIndex(Calls.NEW)) == 1) {
                        missedCallCount++;
                    }
                    break;
                case Calls.INCOMING_TYPE:
                case Calls.OUTGOING_TYPE:
                    break;
                }
            }
        }
        callCursor.close();
        return missedCallCount;
    }  

	//获取联系人头像图片
	public static Bitmap getContactHead(Context cont, String photo_id){
    	Bitmap bitmap = null;
		String photo_selection = null;
		if(photo_id != null && photo_id.length() > 0){
			photo_selection = "ContactsContract.Data._ID = " + photo_id;    
		}else{
			return bitmap;
		}

		Cursor cur = cont.getContentResolver().query(ContactsContract.Data.CONTENT_URI, 
				                                new String[] {ContactsContract.Data.DATA15}, 
				                                photo_selection, null, null); 

		if(cur != null && cur.getCount() > 0){
        	cur.moveToFirst();        	    
		    byte[] contactIcon = null;
		    contactIcon = cur.getBlob(0);        		    
		    if(contactIcon != null && contactIcon.length > 0){        		    
                bitmap = BitmapFactory.decodeByteArray(contactIcon, 0, contactIcon.length); 
		    }
		    cur.close();
		}else if (cur != null){
			cur.close();
		}
        return bitmap;
    }

	//获取所有的通话历史记录
	public static Cursor getCallLogs(Context cont){
        ContentResolver cr = cont.getContentResolver();
        final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, 
        		                       new String[]{
        		                                      CallLog.Calls.NUMBER,
        		                                      CallLog.Calls.CACHED_NAME,
        		                                      CallLog.Calls.TYPE, 
        		                                      CallLog.Calls.DATE
        		                                    }, 
        		                       null, 
        		                       null,
        		                       CallLog.Calls.DEFAULT_SORT_ORDER);
        return cursor;
    }
    
	//获取指定号码的通话历史记录
	public static Cursor getCallLogsByNumber(Context cont, String number){
		ContentResolver cr = cont.getContentResolver();
		String filter = CallLog.Calls.NUMBER + " = '" + number + "'";
		final Cursor cursor = cr.query(CallLog.Calls.CONTENT_URI, 
				new String[]{
				CallLog.Calls.NUMBER,
				CallLog.Calls.CACHED_NAME,
				CallLog.Calls.TYPE, 
				CallLog.Calls.DATE,
				CallLog.Calls.DURATION
		}, 
		filter, 
		null,
		CallLog.Calls.DEFAULT_SORT_ORDER);
		return cursor;
	}

	//获取所有联系人记录
	public static Cursor getContacts(Context cont, String filter){
		Log.i(TAG, "Query contacts with filter:" + filter);
    	final Cursor contactList = cont.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, 
                                                              new String[]{
    			                                                   PhoneLookup._ID,
    			                                                   PhoneLookup.DISPLAY_NAME,
    			                                                   PhoneLookup.LAST_TIME_CONTACTED, 
    			                                                   PhoneLookup.HAS_PHONE_NUMBER,
    			                                                   Contacts.PHOTO_ID
                                                              }, 
                                                              filter,
    			                                              null, 
    			                                              null);

    	return contactList;
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
        			                                                   PhoneLookup.HAS_PHONE_NUMBER,
        			                                                   Contacts.PHOTO_ID
                                                                  }, 
                                                                  nameFilter,
        			                                              null, 
        			                                              null);

    	}
    	return contact;
    }

	//获取与指定号码类似的号码记录
	public static Cursor queryContactDataByNumber(Context cont, String number){
		String filter = ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE '%" + number + "%'";
    	Log.i(TAG, "queryContactByNumber:" + filter);
    	final Cursor numberResult = cont.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
                                                              new String[]{
    			                                                   ContactsContract.PhoneLookup.DISPLAY_NAME,
    			                                                   ContactsContract.CommonDataKinds.Phone.NUMBER,
    			                                                   ContactsContract.Contacts._ID
                                                              }, 
                                                              filter,
    			                                              null, 
    			                                              null);

    	return numberResult;
    }

	//获取与指定联系人姓名类似的联系人记录
	public static Cursor queryContactByName(Context cont, String name){
		String filter = PhoneLookup.DISPLAY_NAME + " LIKE '%" + name + "%'";
    	final Cursor contact = cont.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, 
                                                              new String[]{
    			                                                   PhoneLookup._ID,
    			                                                   PhoneLookup.DISPLAY_NAME,
    			                                                   PhoneLookup.LAST_TIME_CONTACTED, 
    			                                                   PhoneLookup.HAS_PHONE_NUMBER,
    			                                                   Contacts.PHOTO_ID
                                                              }, 
                                                              filter,
    			                                              null, 
    			                                              null);

    	return contact;
    }

	//根据联系人ID获取联系人记录
	public static Cursor queryContactById(Context cont, String id){
		String filter = PhoneLookup._ID + " = '" + id + "'";
    	final Cursor contact = cont.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, 
                                                              new String[]{
    			                                                   PhoneLookup._ID,
    			                                                   PhoneLookup.DISPLAY_NAME,
    			                                                   PhoneLookup.LAST_TIME_CONTACTED, 
    			                                                   PhoneLookup.HAS_PHONE_NUMBER,
    			                                                   Contacts.PHOTO_ID
                                                              }, 
                                                              filter,
    			                                              null, 
    			                                              null);

    	return contact;
    }

	//查询指定联系人头像图片id是否存在
	public static boolean isContactHeadExist(Context cont, String photo_id){
		String photo_selection = "ContactsContract.Data._ID = "+photo_id;    
		Cursor cur = cont.getContentResolver().query(ContactsContract.Data.CONTENT_URI, 
				                                new String[] {ContactsContract.Data.DATA15}, 
				                                photo_selection, null, null); 
		if(cur != null && cur.getCount() > 0){
			cur.close();
		    return true;
		}
		if(cur != null){
			cur.close();
		}
        return false;
    }
}  