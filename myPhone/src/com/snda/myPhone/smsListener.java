/*
 * Copyright (c) 2012 Shanda Corporation. All rights reserved.
 *
 * Created on 2012-01-30.
 */
package com.snda.myPhone;

import com.snda.myPhone.smsReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

//�������ż���ȥ���¼�
public class smsListener{  
	private Context ctx;
	private boolean isregiset = false;  
	private static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	private static final String INCOMING_CALL_ACTION = "android.intent.action.PHONE_STATE";
	private static final String OUTCOMING_CALL_ACTION = "android.intent.action.NEW_OUTGOING_CALL";
	private static final String TAG = "com.snda.myPhone.smsListener";  
	
	public smsListener(Context cont){
		ctx = cont;
	}
	
    // ע����ȥ�缰�����¼�
	public void register(smsReceiver recevier) {  
		IntentFilter filter = new IntentFilter();  
		filter.addAction(SMS_ACTION);
		filter.addAction(OUTCOMING_CALL_ACTION); // ����ȥ��
		filter.addAction(INCOMING_CALL_ACTION); // ��������
		
		filter.setPriority(0xffffffff);//�������ȼ����  
		ctx.registerReceiver(recevier, filter);  
		isregiset = true;  
		Log.i(TAG,"sms listener is registered");
	}  
	
    // ��ע��
	public void unregister(smsReceiver recevier) {  
		if (recevier != null && isregiset) {  
			ctx.unregisterReceiver(recevier);  
			isregiset = false;  
			Log.i(TAG,"sms listener is unregistered");
		} else {
			Log.i(TAG,"sms listener is not registered");
		}
	}  
}  