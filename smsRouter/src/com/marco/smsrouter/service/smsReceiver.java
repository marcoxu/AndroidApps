package com.marco.smsrouter.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import com.marco.smsrouter.R;
import com.marco.smsrouter.SmsRteActivity;
import com.marco.smsrouter.R.drawable;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class smsReceiver extends BroadcastReceiver {  
	private static final String TAG = "smsRouter.smsReceiver";
	private static NotificationManager m_NotificationManager = null;
	private Service mService = null;
	private smsForwardProcesser smsProc = null;
    public static final String dbPath = "/mnt/sdcard/smsrte/databases/";
    public static final String dbName = "smsrte.db";
	
	public void setMobileNo(String callno) {
		Log.i(TAG, "setMobileNo mobile_number is " + callno);
		smsProc.setMobileNo(callno);
	}

	public void setDefaultMobileNo() {
		smsProc.setDefaultMobileNo();
	}

	public smsReceiver(Service serv){
		mService = serv;
		smsProc = new smsForwardProcesser(mService);
		if(smsProc == null)
            Log.i(TAG,"get smsProc fail");
		else
            Log.i(TAG,"get smsProc ok");
		smsProc.setDefaultMobileNo();
	}

    // 设置短信通知Notification
	private void buildSmsNotification(Notification notify, Context cont, String content, String sender){
		// 设置通知在状态栏显示的图标                 
    	notify.icon = R.drawable.icon_message;                  
    	notify.tickerText = content;                  
    	notify.defaults = Notification.DEFAULT_ALL;   
    	notify.flags = Notification.FLAG_AUTO_CANCEL;
        int unreadSmsCount = 0;
        notify.number = unreadSmsCount;
        PendingIntent m_PendingIntent = PendingIntent.getActivity(cont, 0, new Intent(cont,SmsRteActivity.class), 0);
        notify.setLatestEventInfo(cont, "新信息",                          
        		                  unreadSmsCount + "条未读短信", m_PendingIntent);                  
    }
    
    @Override  
	public void onReceive(Context context, Intent intent) {  
		Log.i(TAG, "SmsRecevier onReceive");
		
	    // 检测到新短信
		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");  
			if (pdus != null && pdus.length > 0) {  
	            Log.i(TAG, "SMS received: " + pdus.length);           			
				SmsMessage[] messages = new SmsMessage[pdus.length];  
				for (int i = 0; i < pdus.length; i++) {  
					byte[] pdu = (byte[]) pdus[i];  
					messages[i] = SmsMessage.createFromPdu(pdu);  
				}  
				
				smsProc.handleSmsForward(messages, pdus.length);
			} 
		}
	}  

    // 发送通知
    private void notifyEvent(int id, Context cont, Notification notify){
    	m_NotificationManager = (NotificationManager) cont.getApplicationContext().getSystemService (Context.NOTIFICATION_SERVICE);  
		m_NotificationManager.notify(id, notify);  	
	}
	
    //消息通知Handler通话记录有更新
    @SuppressWarnings("unused")
	private void broadcastCalllogUpdate(String number){
        Log.i(TAG,"broadcase CALLLOG_UPDATE");
        Intent intent = new Intent();//创建Intent对象  
        intent.setAction("com.snda.myPhoneService.CALLLOG_UPDATE");  
        intent.putExtra("number", number);  
        mService.sendBroadcast(intent);//发送广播  
    }
    
    public void updatesmsForwardRules() {
    	smsProc.updateRulesControl();
    }
}  
