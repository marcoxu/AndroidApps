/*
 * Copyright (c) 2012 Shanda Corporation. All rights reserved.
 *
 * Created on 2012-01-30.
 */
package com.snda.myPhone;

import android.content.Context;
import android.media.AudioManager;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.os.IBinder;
import android.os.ServiceManager;

import java.lang.reflect.Method; 
import java.util.HashMap;

import com.android.internal.telephony.ITelephony;

class phoneStateListener extends PhoneStateListener
{
	private static final String TAG = "com.snda.myPhone.phoneStateListener";
	private TelephonyManager m_telephonyManager;
	private ITelephony m_telephonyService;
	private AudioManager m_audioManager; 
	private Context context;
	
	public phoneStateListener(Context cont) {
		context = cont;
		Log.i(TAG, "phoneStateListener.onCreate");
		m_telephonyManager = (TelephonyManager) cont.getSystemService(Context.TELEPHONY_SERVICE);
		m_telephonyService = ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
		m_audioManager = (AudioManager) cont.getSystemService(Context.AUDIO_SERVICE);
		m_telephonyManager.listen(this, PhoneStateListener.LISTEN_CALL_STATE); 	
	}

	public void onCallStateChanged(int state, String incomingNumber)
    {
		m_audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		
		Log.i(TAG, "Call state change: state:" + state + ", number:" + incomingNumber);
		switch (state)
		{
		case TelephonyManager.CALL_STATE_IDLE:
			Log.i(TAG, "Call IDLE " + incomingNumber);
			m_audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:
			//CALL_STATE_OFFHOOK;
			Log.i(TAG, "Call offhook " + incomingNumber);
			break;
		case TelephonyManager.CALL_STATE_RINGING:
			Log.i(TAG, "Ringing Call " + incomingNumber);
            // 检查来电号码是否在黑名单中
            dataAccessor dbHandler = new dataAccessor(context, myPhoneActivity.dbName);
            boolean blockCall = false;
            if(dbHandler.isNumberInBlacklist(incomingNumber)){
                // block call
    			Log.i(TAG, "Block Call number " + incomingNumber);
    			try {
    				m_telephonyService.endCall();
    				blockCall = true;
    			} catch (RemoteException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            }else{
                // 检查来电归属地是否在黑名单中
            	HashMap<String, Object> map = dbHandler.queryPhoneLocation(incomingNumber);
            	if(map != null){
    	            if(dbHandler != null){
            			Log.i(TAG, "Incoming Call location " + map.get("location").toString());
                    	if(dbHandler.isLocationInBlacklist(map.get("location").toString())){
                            // block call
                			Log.i(TAG, "Block Call location " + map.get("location").toString());
                			try {
                				m_telephonyService.endCall();
                				blockCall = true;
                			} catch (RemoteException e) {
                				// TODO Auto-generated catch block
                				e.printStackTrace();
                			}
                       }
    	            }
            	}
            	
            }

            if(!blockCall){
				m_audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
			break;
		default:
			break;
		}
    }
}
