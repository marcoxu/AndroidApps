/*
 * Copyright (c) 2012 Shanda Corporation. All rights reserved.
 *
 * Created on 2012-01-30.
 */
package com.snda.myPhone;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// 系统启动时自动启动服务
public class bootUpReceiver extends BroadcastReceiver{    
	public void onReceive(Context context, Intent intent) {
    	Intent serviceStartIntent = new Intent(Intent.ACTION_RUN);
    	serviceStartIntent.setClass(context, myPhoneService.class);
        context.startService(serviceStartIntent);    	
	}    
}
	
