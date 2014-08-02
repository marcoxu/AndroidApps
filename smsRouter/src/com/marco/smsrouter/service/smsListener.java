package com.marco.smsrouter.service;


import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

//监听短信及来去电事件
public class smsListener{  
	private static final String TAG = "smsRouter.smsListener";
	private Context ctx;
	private boolean isregiset = false;  
	private static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	
	public smsListener(Context cont){
		ctx = cont;
	}
	
    // 注册短信事件
	public void register(smsReceiver recevier) {  
		IntentFilter filter = new IntentFilter();  
		filter.addAction(SMS_ACTION);
		
		filter.setPriority(0x7fffffff);//设置优先级最大  
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