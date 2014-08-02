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
    		// ��½ʢ��ͨ��֤
    		OpenAPI.pwdLogin(mainLogin.this, AppId, mainLogin.this, null);
    	} catch (Exception e) {
    		Log.i(TAG, "ʢ��ͨ��֤��¼���� ", e);
    	}
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ����myPhoneService
    	if(!myPhoneActivity.isServiceStarted(this)){
    		Log.i(TAG, "Start myPhoneService");
        	Intent i = new Intent(Intent.ACTION_RUN);
            i.setClass(this.getApplicationContext(), myPhoneService.class);
            this.getApplicationContext().startService(i);    	
    	}

    	if(SessionId != null && UserId != null){
            // �ѵ�¼������myPhoneActivity
            Intent startAct = new Intent(getApplicationContext(), myPhoneActivity.class);	
            startAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().getApplicationContext().startActivity(startAct);
            this.finish();
            return;
        }

        // ��ʼ������OA
        OpenAPI.init(mainLogin.this, AppId, "channel_id", "product_id");
        Toast.makeText(mainLogin.this, "��ʼ������OA", Toast.LENGTH_SHORT).show();

        try {
            // ����������
            ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            if(activeNetInfo == null){
                Log.i(TAG, "�޿������� ");
                Toast.makeText(mainLogin.this, "��ǰ�޿�������", Toast.LENGTH_SHORT).show();
                this.finish();
                return;
            }else if(activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI){
                Log.i(TAG, "��������: WIFI");
            }else if(activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                int type = tm.getNetworkType();
                if (type == TelephonyManager.NETWORK_TYPE_UMTS) {
                    Log.i(TAG, "��������: UMTS");
                } else if (type == TelephonyManager.NETWORK_TYPE_GPRS) {
                    Log.i(TAG, "�������络: GPRS");
                } else if (type == TelephonyManager.NETWORK_TYPE_EDGE) {
                    Log.i(TAG, "��������: EDGE");
                } else {
                    Log.i(TAG, "��������: UIM");
                }
            }
        } catch (Exception e) {
            Log.i(TAG, "�޿������� ", e);
            Toast.makeText(mainLogin.this, "��ǰ�޿�������", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }

        // ��½ʢ��ͨ��֤
        startSndaLogin();
    }

    public void doCallBack() {
        Log.i(TAG, "=======> OpenAPI.loginStat " + OpenAPI.getStatus());
        Log.i(TAG, "=======> OpenAPI.getMsg() " + OpenAPI.getStatusText());
        if (OpenAPI.getStatus() == 0) {
        	Log.i(TAG, "OpenAPI.getSessionId() " + OpenAPI.getSessionId() + ", deviceId " + OpenAPI.getUserId());
            
            // ��½�ɹ�
            SessionId = OpenAPI.getSessionId();
            UserId = OpenAPI.getUserId();
            DeviceId = OpenAPI.getDeviceId();
            Intent startAct = new Intent(getApplicationContext(), myPhoneActivity.class);	
            startAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().getApplicationContext().startActivity(startAct);
            this.finish();
        }else{
        	if(OpenAPI.getStatus() == -10801017 || OpenAPI.getStatus() == -10801304){
        		// ȡ����½���˳�
        		this.finish();
        	}else if(OpenAPI.getStatus() != -10801001){
                startSndaLogin();
        	}
        }
    }
}

