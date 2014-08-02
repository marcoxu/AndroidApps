/*
 * Copyright (c) 2012 Shanda Corporation. All rights reserved.
 *
 * Created on 2012-01-30.
 */
package com.snda.myPhone;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

public class contactManager{  
    private static final String TAG = "myPhone.contactManager";
    public static final ArrayList<HashMap<String, Object>> contact_array = new ArrayList<HashMap<String, Object>>();   

	public contactManager(){
	}

	//查询指定联系人是否已包含在联系人数组中
	public static boolean findContactInContactsArray(String name){
		if(contact_array == null || contact_array.size() == 0){
			return false;
		}
		
        for(HashMap<String, Object> m: contact_array){  
        	if(m.containsValue(name)){
        	    return true;
        	}
        } 
        return false;
    }

	
	//添加联系人到联系人数组中
	public static void insertContactsArray(Context cont, Cursor contact){
    	String contactName = contact.getString(contact.getColumnIndex(PhoneLookup.DISPLAY_NAME));

    	if(contactName == null || contactName.length() == 0){
    		contactName = contact.getString(0);
    	}
    	
    	if(!findContactInContactsArray(contactName)){
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = new Date(Long.parseLong(contact.getString(contact.getColumnIndex(PhoneLookup.LAST_TIME_CONTACTED))));
            String time = sfd.format(date);
    		HashMap<String, Object> map = new HashMap<String, Object>();   
    		String buildString = contactName;
            // get contact name
            map.put("showname", contactName);
            
            // get phone number
            String contactId = contact.getString(contact.getColumnIndex(ContactsContract.Contacts._ID));
            String hasPhoneNumber = contact.getString(contact.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            if( (Integer.parseInt(hasPhoneNumber) > 0) )
            {
                Cursor phoneNumber = cont.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                		                                        null,
                		                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId,
                		                                        null, 
                		                                        null); 
                if(phoneNumber != null){
                    while (phoneNumber.moveToNext())
                    {
                        String strPhoneNumber = phoneNumber.getString(phoneNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        map.put("number", strPhoneNumber);
                	    buildString = buildString + " " + strPhoneNumber;
                        break;
                    }
                    phoneNumber.close();
                }
            }            

            // get contact image
            map.put("contactimg", R.drawable.ic_launcher);  
            String photo_id = contact.getString(contact.getColumnIndex(Contacts.PHOTO_ID));
        	if(photo_id != null && dataAccessor.isContactHeadExist(cont.getApplicationContext(), photo_id)){
                Log.i(TAG,"Set contact: " + contactName + "'s header photo!");
                map.put("contactimg", photo_id);  
        	}
            
            map.put("time", time);
            contact_array.add(map);
    	}
    }

    //获取联系人记录并写入联系人数组
	static void buildContactArray(Context cont, Cursor cursor){
    	if(cursor == null || cursor.getCount() == 0){
    		return;
    	}
    	
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToPosition(i);
            insertContactsArray(cont, cursor);
            if(i%20 == 0 && i/20 > 0){
            	//通知Handler更新Listview，实现动态加载数据效果 	
            	queryContactOk(0);
            }
        }    	
    }
    
    //清空联系人数组
    static void clearContactArray(){
        if(contact_array != null && !contact_array.isEmpty()){
            contact_array.clear();
        }
    }

    //消息通知Handler联系人数组已更新
    static void queryContactOk(int finish){
    	if(myPhoneActivity.resultHandler == null){
            Log.i(TAG,"resultHandler is null");
        	return;
    	}
        Message msg = new Message();
        msg.arg1 = finish;
        msg.what = myPhoneActivity.EVENT_CONTACTS_QUERY_DONE;
        myPhoneActivity.resultHandler.removeMessages(myPhoneActivity.EVENT_CONTACTS_QUERY_DONE);
        myPhoneActivity.resultHandler.sendMessage(msg);
    }
}