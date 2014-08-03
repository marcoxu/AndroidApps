package com.marco.smsrouter.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class smsBootReceiver extends BroadcastReceiver {
	private static final String TAG = "smsRouter.smsBootReceiver";
	
	private class smsRteStartThread extends Thread {
		private Context mContext;
		
		public smsRteStartThread(Context context) {
			mContext = context;
		}
		
		public void run() {
			try{
				sleep(10000); // sleep for 10 seconds
	 	       	Intent i = new Intent(Intent.ACTION_RUN);
	 	        i.setClass(mContext, smsRteService.class);
		 	    mContext.startService(i); 				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

    @Override
    public void onReceive(Context context, Intent intent) {
  		Log.i(TAG, "smsBootReceiver onReceive");
  		if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
  	  		Log.i(TAG, "smsBootReceiver BOOT_COMPLETED");
  			new smsRteStartThread(context).start();
  		}
    }
}
