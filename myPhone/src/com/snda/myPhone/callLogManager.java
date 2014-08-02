/*
 * Copyright (c) 2012 Shanda Corporation. All rights reserved.
 *
 * Created on 2012-01-30.
 */
package com.snda.myPhone;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.os.Message;
import android.util.Log;

public class callLogManager{  
    private static final String TAG = "myPhone.callLogManager";
	public static final ArrayList<HashMap<String, Object>> calllog_array = new ArrayList<HashMap<String, Object>>();   
    private static int currentIndexLoaded = -1; //��ǰ����������һ��ͨ����¼��index
    private static int callLogLoadItemPerTime = 20; // һ�������ͨ����������
	
	public callLogManager(){
	}

    //���ͨ����¼����
    @SuppressWarnings("unused")
	private void clearCalllogArray(){
        if(calllog_array != null && !calllog_array.isEmpty()){
        	calllog_array.clear();
        }    	
    	currentIndexLoaded = -1;
    }

	//��ѯָ�������Ƿ��Ѱ�����ͨ����¼������
	public static boolean findContactInCalllogArray(String number){
		if(calllog_array == null || calllog_array.size() == 0){
			return false;
		}
		
        for(HashMap<String, Object> m: calllog_array){  
        	if(m.containsValue(number)){
        	    return true;
        	}
        } 
        return false;
    }

	
	//���ͨ����¼��ͨ����¼������
	public static boolean insertCalllogArray(Cursor calllog){
    	if(!findContactInCalllogArray(calllog.getString(0))){
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = new Date(Long.parseLong(calllog.getString(3)));
            String time = sfd.format(date);
    		HashMap<String, Object> map = new HashMap<String, Object>();
        	String name = (calllog.getString(1)==null || calllog.getString(1).length() == 0)?"İ������":calllog.getString(1);
            map.put("number", calllog.getString(0));
            map.put("name", name);   
            map.put("location", "");
            map.put("type", "");
            map.put("time", time);   
            ArrayList<HashMap<String, Object>> history = myPhoneActivity.dbHandler.quryCallHistory(calllog.getString(0));
            if(history != null && history.size() != 0){
                map.put("ringtime", "����:" + history.get(history.size()-1).get("ringtime") + "��");   
            }else{
                map.put("ringtime", "����: 0��");
            }
            calllog_array.add(map);
        	currentIndexLoaded += 1;
        	return true;
    	}
    	currentIndexLoaded += 1;
    	return false;
    }

    //��Ϣ֪ͨHandlerͨ����¼�����Ѹ���
    static void queryCalllogOk(int finish){
    	if(myPhoneActivity.resultHandler == null){
            Log.i(TAG,"resultHandler is null");
        	return;
    	}
        Message msg = new Message();
        msg.arg1 = finish;
        msg.what = myPhoneActivity.EVENT_CALLLOG_QUERY_DONE;
        myPhoneActivity.resultHandler.removeMessages(myPhoneActivity.EVENT_CALLLOG_QUERY_DONE);
        myPhoneActivity.resultHandler.sendMessage(msg);
    }

    //��ȡͨ����¼��д��ͨ����¼����
    static synchronized void getCallogData(Context cont){
        Cursor cursor = dataAccessor.getCallLogs(cont.getApplicationContext());
        if(cursor != null){
        	if(currentIndexLoaded >= cursor.getCount()){
        		// all call logs are loaded
        		cursor.close();
        		return;
        	}
        	
        	int loadCount = 0;
            Log.i(TAG, "Load calllog from " + (currentIndexLoaded + 1));
            for (int i = currentIndexLoaded+1; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                if(insertCalllogArray(cursor)){
                	loadCount ++;
                }
                if(loadCount%10 == 0 && loadCount/10 > 0){
                    //֪ͨHandler����Listview��ʵ�ֶ�̬��������Ч��
                	queryCalllogOk(0);
                }
                if(loadCount >= callLogLoadItemPerTime){
                    //�û��϶����������ײ����Ӵ�һ������������Լӿ������ٶ�
                	callLogLoadItemPerTime += 20;
                	break;
                }
            }
            Log.i(TAG, "Current loaded index is " + currentIndexLoaded);
        	cursor.close();
        }
    }
}