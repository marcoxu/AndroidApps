package com.marco.smsrouter.service;


import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

//�������ż���ȥ���¼�
public class smsListener{  
	private static final String TAG = "smsRouter.smsListener";
	private Context ctx;
	private boolean isregiset = false;  
	private static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	
	public smsListener(Context cont){
		ctx = cont;
	}
	
    // ע������¼�
	public void register(smsReceiver recevier) {  
		IntentFilter filter = new IntentFilter();  
		filter.addAction(SMS_ACTION);
		
		filter.setPriority(0x7fffffff);//�������ȼ����  
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