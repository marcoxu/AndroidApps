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

import android.app.Activity;  
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;  
import android.provider.ContactsContract;
import android.provider.ContactsContract.Data;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
  
//显示指定联系人的详细通话记录
public class callLogs extends Activity {
	private static final String TAG = "com.snda.myPhone.call_logs";
    private static final ArrayList<HashMap<String, Object>> calllog_array = new ArrayList<HashMap<String, Object>>();   
    
    /** 获取联系人详细组织信息 */  
	private HashMap<String, String> getCompanyInfo(String name, String number){
		// 获得联系人的ID号
		Cursor nameCursor = dataAccessor.queryContactByName(getApplicationContext(), name);

		HashMap<String, String> map = new HashMap<String, String>();
		String contactId = null;
        if(nameCursor != null && nameCursor.getCount() > 0){
        	for(int i=0;i<nameCursor.getCount();i++){
        		nameCursor.moveToPosition(i);
                String contact_id = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts._ID));
                String hasPhoneNumber = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                if( (Integer.parseInt(hasPhoneNumber) > 0) )
                {
                    Cursor phoneNumber = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    		                                        null,
                    		                                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contact_id,
                    		                                        null, 
                    		                                        null); 
                    if(phoneNumber != null){
                    	while (phoneNumber.moveToNext())
                    	{
                    		String strPhoneNumber = phoneNumber.getString(phoneNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    		//号码匹配，找到联系人ID
                    		if(number.equals(strPhoneNumber)){
                    			contactId = contact_id;
                    			break;
                    		}
                    	}
                    	phoneNumber.close();
                    }
                }            
        	}
        	
        	nameCursor.close();
        	if(contactId == null){
    			map.put("company", "");
    			map.put("position", "");
    			map.put("department", "");
    			map.put("id", "");
            	return map;
        	}
        }else{
        	if(nameCursor != null){
        		nameCursor.close();
        	}
			map.put("company", "");
			map.put("position", "");
			map.put("department", "");
			map.put("id", "");
        	return map;
        }

        String filter = ContactsContract.Data.CONTACT_ID+"='"+contactId + "' AND "  + Data.MIMETYPE +  "='" + ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE + "'";
		Log.i(TAG, "contact selection is " + filter);
		//获得organization
		Cursor orgCursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
		                                              new String[]{
				                                             ContactsContract.CommonDataKinds.Organization.COMPANY,
				                                             ContactsContract.CommonDataKinds.Organization.DEPARTMENT,
				                                             ContactsContract.CommonDataKinds.Organization.TITLE}, 
				                                      filter,
		                                              null, 
		                                              null);

        // 读取组织信息
		if(orgCursor != null && orgCursor.getCount() > 0){
			if(orgCursor.moveToFirst()){
				do{
					String company = orgCursor.getString(0);
					String position = orgCursor.getString(2);
					String department = orgCursor.getString(1);

					Log.i(TAG, "公司: "+company+" 职位: "+position + "部门: " + department);
					if(company != null && company.length() > 0){
						map.put("company", company);
					}else{
						map.put("company", "");
					}
					if(position != null && position.length() > 0){
					    map.put("position", position);
					}else{
						map.put("position", "");
					}
					if(department != null && department.length() > 0){
					    map.put("department", department);
					}else{
						map.put("department", "");
					}
					map.put("id", contactId);
				}while(orgCursor.moveToNext());
			}
			orgCursor.close();
		}else{
			map.put("company", "");
			map.put("position", "");
			map.put("department", "");
			map.put("id", contactId);
			if(orgCursor != null){
				orgCursor.close();
			}
		}
		return map;
	}
	
	//获取联系人通话历史记录数组
	public synchronized void insertCalllogArray(Cursor contact, ArrayList<HashMap<String, Object>> history){
		if(contact != null && contact.getCount() > 0){
			for(int i=0;i<contact.getCount();i++){
				contact.moveToPosition(i);
	            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	            Date date = new Date(Long.parseLong(contact.getString(3)));
	            String time = sfd.format(date);

	    		HashMap<String, Object> map = new HashMap<String, Object>();
	            map.put("time", time);   
	            map.put("duration", "通话:" + contact.getString(4) + "秒");   
	            Log.i(TAG, "time " + time + ", duration " + "," + contact.getString(4));
	            if(history != null && history.size() > i){
		            Log.i(TAG, "ringtime " + history.get(history.size()-i-1).get("ringtime"));
	                map.put("ringtime", "响铃:" + history.get(history.size()-i-1).get("ringtime") + "秒");   
	            }else{
	        		// 通话的响铃时间未记录，设为默认值0秒
		            Log.i(TAG, "ringtime 0");
	                map.put("ringtime", "响铃:" + "0秒");   
	            }
	            calllog_array.add(map);
			}
    	}
	}
	
	//根据联系人号码获取通话记录
    private void getCallogDataByNumber(String number){
        if(calllog_array != null && !calllog_array.isEmpty()){
        	calllog_array.clear();
        }    	
        
        Cursor cursor = dataAccessor.getCallLogsByNumber(getApplicationContext(), number);
        ArrayList<HashMap<String, Object>> history = myPhoneActivity.dbHandler.quryCallHistory(number);

        if(cursor != null){
            insertCalllogArray(cursor, history);
        	cursor.close();
        }
    }

    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.call_logs);
        
        String number = getIntent().getStringExtra("callnumber");
        String name = getIntent().getStringExtra("name");
        if(number != null){
        	Log.i(TAG, "call number:" + number);
    		// 显示号码及对应联系人姓名
            final TextView display_caller = (TextView) findViewById(R.id.display_name);
            if(name != null && name.length() > 0){
                display_caller.setText(name + " " + number);
            }
            
    		// 显示号码归属地及号码类型
            HashMap<String, Object> location = myPhoneActivity.dbHandler.queryPhoneLocation(number);
            if(location != null){
                final TextView display_location = (TextView) findViewById(R.id.location);
                final TextView display_type = (TextView) findViewById(R.id.type);
                display_location.setText("归属地: " + (String)location.get("location"));
                display_type.setText("类    型: " + ((String)location.get("type")));
            }
           
    		// 显示联系人的组织信息
            HashMap<String, String> map = getCompanyInfo(name, number);
            if(map != null){
                final TextView display_company = (TextView) findViewById(R.id.company);
                final TextView display_department = (TextView) findViewById(R.id.department);
                final TextView display_position = (TextView) findViewById(R.id.position);
                final ImageView display_img = (ImageView) findViewById(R.id.contactorimg1);
                display_company.setText("公    司: " + map.get("company"));
                display_department.setText("部    门: " + map.get("department"));
                display_position.setText("职    位: " + map.get("position"));
        		Cursor contactCursor = dataAccessor.queryContactById(getApplicationContext(), map.get("id"));
        		
        		if(contactCursor != null && contactCursor.getCount() > 0){
        			contactCursor.moveToFirst();
        			Bitmap bitmap = null;
        			Log.i(TAG, "Get header for " + contactCursor.getString(4));
        			bitmap = dataAccessor.getContactHead(getApplicationContext(), contactCursor.getString(4));
                	if(bitmap != null){
                		display_img.setImageBitmap(bitmap);
                	}
                	contactCursor.close();
        		}
        		
        		getCallogDataByNumber(number);
                ListView contacts = (ListView) findViewById(R.id.calllogList);  

                //生成适配器的Item和动态数组对应的元素   
                SimpleAdapter listItemAdapter = new SimpleAdapter(this, calllog_array,
                                                   R.layout.call_history_item,
                                                   new String[] {"time", "ringtime", "duration"},    
                                                   new int[] {R.id.calltime,R.id.callringtime,R.id.callduration});

                
                //添加并且显示   
                if(contacts.getAdapter() == null){
                    contacts.addHeaderView(new LinearLayout(this));
                }
                contacts.setAdapter(listItemAdapter);
            }
            
        }
    }  
}