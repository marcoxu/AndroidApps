package com.marco.smsrouter.service;

import java.io.FileDescriptor;

import com.marco.smsrouter.SmsRteActivity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

// 启动服务用于监听短信
public class smsRteService extends Service {
	private static final String TAG = "smsRouter.smsRteService";
	private smsListener listener = null;
	private smsReceiver recevier = null;
	private CommandReceiver cmdReceiver;
	public static final int CMD_UPDATE_FLOW_CONTROL = 0;
	public static final int CMD_UPDATE_RULE         = 1;
	private smsRteBinder mBinder = new smsRteBinder();
	
	public class smsRteBinder extends Binder {
		public smsRteService getService() {
			return smsRteService.this;
		}
	}
	
    //接收Activity传送过来的命令
    private class CommandReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            int cmd = intent.getIntExtra("cmd", -1);
            if(cmd == CMD_UPDATE_FLOW_CONTROL){
        		Log.i(TAG, "CommandReceiver CMD_UPDATE_FLOW_CONTROL");
        		recevier.updatesmsForwardRules();
            }      
            if(cmd == CMD_UPDATE_RULE){
        		Log.i(TAG, "CommandReceiver CMD_UPDATE_RULE");
            }
        }                      
    }

	public void configForwardCallno(String callno) {
		if(recevier != null) {
			recevier.setMobileNo(callno);
		}
	}

	public void configDefaultForwardCallno() {
		if(recevier != null) {
			recevier.setDefaultMobileNo();
		}
	}
	
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "smsRteService.onCreate");
		if(!SmsRteActivity.checkServConfigDB())
			SmsRteActivity.createServConfigDB();
		Log.i(TAG, "smsRteService create recevier");
		if(recevier == null)
    		recevier = new smsReceiver(this);
		Log.i(TAG, "smsRteService create listener");
		if(listener == null){
 	   		listener = new smsListener(this);
    		listener.register(recevier);
    	}
	}
	
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "smsRteService.onStart");
		super.onStart(intent, startId);
		String callNo = intent.getStringExtra("CallNo");
		if(callNo != null)
			configForwardCallno(callNo);
		else
			configDefaultForwardCallno();
		
		cmdReceiver = new CommandReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("weibo4andriod.focusme.weiboService");
		registerReceiver(cmdReceiver, filter);
	}
	
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "smsRteService.onBind");
		return mBinder;
	}
	
	public boolean onUnbind(Intent intent) {
		Log.i(TAG, "smsRteService.onUnbind");
		 return super.onUnbind(intent);
	}
	
	public void onRebind(Intent intent) {
		Log.i(TAG, "smsRteService.onRebind");
	}
	
	public void onDestroy() {
		Log.i(TAG, "smsRteService.onDestroy");
		if (listener != null) {  
			listener.unregister(recevier);  
		}
		if(cmdReceiver != null) {
			unregisterReceiver(cmdReceiver);
		}
	   	Intent i = new Intent(Intent.ACTION_RUN);
	    i.setClass(this.getApplicationContext(), smsRteService.class);
 	    this.getApplicationContext().startService(i);
		super.onDestroy();
	}
}