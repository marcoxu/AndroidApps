/*
 * Copyright (c) 2012 Shanda Corporation. All rights reserved.
 *
 * Created on 2012-01-30.
 */
package com.snda.myPhone;
  
import java.util.HashMap;

import android.app.Activity;  
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;  
import android.widget.ImageView;
import android.widget.TextView;


//������ʾActivity
public class onCallReceived extends Activity {
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.oncallreceived);
        
        String number = getIntent().getStringExtra("callnumber");
        if(number != null){
            final TextView display_caller = (TextView) findViewById(R.id.fromaddress);
            final TextView display_blacklist = (TextView) findViewById(R.id.blacklistInfo);
            final ImageView display_photo = (ImageView) findViewById(R.id.callerphoto);
            
            Cursor cursor = dataAccessor.queryContactByNumber(this.getApplicationContext(), number);
            if(cursor == null || cursor.getCount() == 0){
                // İ���������磬ֻ��ʾ����
                display_caller.setText(number);
                display_photo.setBackgroundResource(R.drawable.anonymous_caller);
                if(cursor != null){
                	cursor.close();
                }
            }else{
            	cursor.moveToFirst();
                display_caller.setText(number + "  " + cursor.getString(1));
                Cursor contactInfo = dataAccessor.queryContactByName(this.getApplicationContext(), cursor.getString(1));
                if(contactInfo == null || contactInfo.getCount() != 1){
                    // �Ҳ�����ϵ�ˣ���ΪĬ��ͷ��
                    display_photo.setBackgroundResource(R.drawable.anonymous_caller);
                }else{
                	contactInfo.moveToFirst();
                    String photo_id = contactInfo.getString(4);

                    if(photo_id != null && dataAccessor.isContactHeadExist(this.getApplicationContext(), photo_id)){
            			Bitmap bitmap = dataAccessor.getContactHead(this.getApplicationContext(), photo_id);
                    	if(bitmap != null){
                            // ������ϵ��ͷ��
                			display_photo.setImageBitmap(bitmap);
                    	}else{
                            // ��ȡͷ��ʧ�ܣ���ΪĬ��ͷ��
                            display_photo.setBackgroundResource(R.drawable.anonymous_caller);
                    	}
                	}else{
                        // �����Ӧ��ϵ����ͷ����ΪĬ��ͷ��
                        display_photo.setBackgroundResource(R.drawable.anonymous_caller);
                	}              	
                }  
                cursor.close();
                contactInfo.close();
            }
            
            // �����������Ƿ��ں�������
            dataAccessor dbHandler = new dataAccessor(getApplicationContext(), myPhoneActivity.dbName);
            if(dbHandler.isNumberInBlacklist(number)){
                display_blacklist.setText("�����������");
            }else{
                // �������������Ƿ��ں�������
            	HashMap<String, Object> map = dbHandler.queryPhoneLocation(number);
            	if(map != null){
    	            if(dbHandler != null){
                    	if(dbHandler.isLocationInBlacklist(map.get("location").toString())){
                            display_blacklist.setText("�����������");
                        }
    	            }
            	}
            	
            }
        }
    }  
}