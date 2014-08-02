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

//监听短信及来去电事件
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
	
    // 注册来去电及短信事件
	public void register(smsReceiver recevier) {  
		IntentFilter filter = new IntentFilter();  
		filter.addAction(SMS_ACTION);
		filter.addAction(OUTCOMING_CALL_ACTION); // 监听去电
		filter.addAction(INCOMING_CALL_ACTION); // 监听来电
		
		filter.setPriority(0xffffffff);//设置优先级最大  
		ctx.registerReceiver(recevier, filter);  
		isregiset = true;  
		Log.i(TAG,"sms listener is registered");
	}  
	
    // 解注册
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