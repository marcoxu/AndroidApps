/*
 * Copyright (c) 2012 Shanda Corporation. All rights reserved.
 *
 * Created on 2012-01-30.
 */
package com.snda.myPhone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.snda.woa.android.CallBack;
import com.snda.woa.android.OpenAPI;

public class mainLogin extends Activity implements CallBack {
    private static final String TAG = "com.snda.myPhone.mainLogin";
    private static final String AppId = "909";
    public static String SessionId = null;
    public static String UserId = null;
    public static String DeviceId = null;

    private void startSndaLogin(){
    	try {
    		// 登陆盛大通行证
    		OpenAPI.pwdLogin(mainLogin.this, AppId, mainLogin.this, null);
    	} catch (Exception e) {
    		Log.i(TAG, "盛大通行证登录错误： ", e);
    	}
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 启动myPhoneService
    	if(!myPhoneActivity.isServiceStarted(this)){
    		Log.i(TAG, "Start myPhoneService");
        	Intent i = new Intent(Intent.ACTION_RUN);
            i.setClass(this.getApplicationContext(), myPhoneService.class);
            this.getApplicationContext().startService(i);    	
    	}

    	if(SessionId != null && UserId != null){
            // 已登录，启动myPhoneActivity
            Intent startAct = new Intent(getApplicationContext(), myPhoneActivity.class);	
            startAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().getApplicationContext().startActivity(startAct);
            this.finish();
            return;
        }

        // 初始化无线OA
        OpenAPI.init(mainLogin.this, AppId, "channel_id", "product_id");
        Toast.makeText(mainLogin.this, "初始化无线OA", Toast.LENGTH_SHORT).show();

        try {
            // 检查可用网络
            ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if(activeNetInfo == null){
                Log.i(TAG, "无可用网络 ");
                Toast.makeText(mainLogin.this, "当前无可用网络", Toast.LENGTH_SHORT).show();
                this.finish();
                return;
            }else if(activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI){
                Log.i(TAG, "可用网络: WIFI");
            }else if(activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                int type = tm.getNetworkType();
                if (type == TelephonyManager.NETWORK_TYPE_UMTS) {
                    Log.i(TAG, "可用网络: UMTS");
                } else if (type == TelephonyManager.NETWORK_TYPE_GPRS) {
                    Log.i(TAG, "可用网络粶: GPRS");
                } else if (type == TelephonyManager.NETWORK_TYPE_EDGE) {
                    Log.i(TAG, "可用网络: EDGE");
                } else {
                    Log.i(TAG, "可用网络: UIM");
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "无可用网络 ", e);
            Toast.makeText(mainLogin.this, "当前无可用网络", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }

        // 登陆盛大通行证
        startSndaLogin();
    }

    public void doCallBack() {
        Log.i(TAG, "=======> OpenAPI.loginStat " + OpenAPI.getStatus());
        Log.i(TAG, "=======> OpenAPI.getMsg() " + OpenAPI.getStatusText());
        if (OpenAPI.getStatus() == 0) {
        	Log.i(TAG, "OpenAPI.getSessionId() " + OpenAPI.getSessionId() + ", deviceId " + OpenAPI.getUserId());
            
            // 登陆成功
            SessionId = OpenAPI.getSessionId();
            UserId = OpenAPI.getUserId();
            DeviceId = OpenAPI.getDeviceId();
            Intent startAct = new Intent(getApplicationContext(), myPhoneActivity.class);	
            startAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().getApplicationContext().startActivity(startAct);
            this.finish();
        }else{
        	if(OpenAPI.getStatus() == -10801017 || OpenAPI.getStatus() == -10801304){
        		// 取消登陆，退出
        		this.finish();
        	}else if(OpenAPI.getStatus() != -10801001){
                startSndaLogin();
        	}
        }
    }
}

