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
  
//��ʾָ����ϵ�˵���ϸͨ����¼
public class callLogs extends Activity {
	private static final String TAG = "com.snda.myPhone.call_logs";
    private static final ArrayList<HashMap<String, Object>> calllog_array = new ArrayList<HashMap<String, Object>>();   
    
    /** ��ȡ��ϵ����ϸ��֯��Ϣ */  
	private HashMap<String, String> getCompanyInfo(String name, String number){
		// �����ϵ�˵�ID��
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
                    		//����ƥ�䣬�ҵ���ϵ��ID
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
		//���organization
		Cursor orgCursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
		                                              new String[]{
				                                             ContactsContract.CommonDataKinds.Organization.COMPANY,
				                                             ContactsContract.CommonDataKinds.Organization.DEPARTMENT,
				                                             ContactsContract.CommonDataKinds.Organization.TITLE}, 
				                                      filter,
		                                              null, 
		                                              null);

        // ��ȡ��֯��Ϣ
		if(orgCursor != null && orgCursor.getCount() > 0){
			if(orgCursor.moveToFirst()){
				do{
					String company = orgCursor.getString(0);
					String position = orgCursor.getString(2);
					String department = orgCursor.getString(1);

					Log.i(TAG, "��˾: "+company+" ְλ: "+position + "����: " + department);
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
	
	//��ȡ��ϵ��ͨ����ʷ��¼����
	public synchronized void insertCalllogArray(Cursor contact, ArrayList<HashMap<String, Object>> history){
		if(contact != null && contact.getCount() > 0){
			for(int i=0;i<contact.getCount();i++){
				contact.moveToPosition(i);
	            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	            Date date = new Date(Long.parseLong(contact.getString(3)));
	            String time = sfd.format(date);

	    		HashMap<String, Object> map = new HashMap<String, Object>();
	            map.put("time", time);   
	            map.put("duration", "ͨ��:" + contact.getString(4) + "��");   
	            Log.i(TAG, "time " + time + ", duration " + "," + contact.getString(4));
	            if(history != null && history.size() > i){
		            Log.i(TAG, "ringtime " + history.get(history.size()-i-1).get("ringtime"));
	                map.put("ringtime", "����:" + history.get(history.size()-i-1).get("ringtime") + "��");   
	            }else{
	        		// ͨ��������ʱ��δ��¼����ΪĬ��ֵ0��
		            Log.i(TAG, "ringtime 0");
	                map.put("ringtime", "����:" + "0��");   
	            }
	            calllog_array.add(map);
			}
    	}
	}
	
	//������ϵ�˺����ȡͨ����¼
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
    		// ��ʾ���뼰��Ӧ��ϵ������
            final TextView display_caller = (TextView) findViewById(R.id.display_name);
            if(name != null && name.length() > 0){
                display_caller.setText(name + " " + number);
            }
            
    		// ��ʾ��������ؼ���������
            HashMap<String, Object> location = myPhoneActivity.dbHandler.queryPhoneLocation(number);
            if(location != null){
                final TextView display_location = (TextView) findViewById(R.id.location);
                final TextView display_type = (TextView) findViewById(R.id.type);
                display_location.setText("������: " + (String)location.get("location"));
                display_type.setText("��    ��: " + ((String)location.get("type")));
            }
           
    		// ��ʾ��ϵ�˵���֯��Ϣ
            HashMap<String, String> map = getCompanyInfo(name, number);
            if(map != null){
                final TextView display_company = (TextView) findViewById(R.id.company);
                final TextView display_department = (TextView) findViewById(R.id.department);
                final TextView display_position = (TextView) findViewById(R.id.position);
                final ImageView display_img = (ImageView) findViewById(R.id.contactorimg1);
                display_company.setText("��    ˾: " + map.get("company"));
                display_department.setText("��    ��: " + map.get("department"));
                display_position.setText("ְ    λ: " + map.get("position"));
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

                //������������Item�Ͷ�̬�����Ӧ��Ԫ��   
                SimpleAdapter listItemAdapter = new SimpleAdapter(this, calllog_array,
                                                   R.layout.call_history_item,
                                                   new String[] {"time", "ringtime", "duration"},    
                                                   new int[] {R.id.calltime,R.id.callringtime,R.id.callduration});

                
                //��Ӳ�����ʾ   
                if(contacts.getAdapter() == null){
                    contacts.addHeaderView(new LinearLayout(this));
                }
                contacts.setAdapter(listItemAdapter);
            }
            
        }
    }  
}