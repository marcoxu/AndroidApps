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
    private static int currentIndexLoaded = -1; //当前已载入的最后一条通话记录的index
    private static int callLogLoadItemPerTime = 20; // 一次载入的通话号码条数
	
	public callLogManager(){
	}

    //清空通话记录数组
    @SuppressWarnings("unused")
	private void clearCalllogArray(){
        if(calllog_array != null && !calllog_array.isEmpty()){
        	calllog_array.clear();
        }    	
    	currentIndexLoaded = -1;
    }

	//查询指定号码是否已包含在通话记录数组中
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

	
	//添加通话记录到通话记录数组中
	public static boolean insertCalllogArray(Cursor calllog){
    	if(!findContactInCalllogArray(calllog.getString(0))){
            SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = new Date(Long.parseLong(calllog.getString(3)));
            String time = sfd.format(date);
    		HashMap<String, Object> map = new HashMap<String, Object>();
        	String name = (calllog.getString(1)==null || calllog.getString(1).length() == 0)?"陌生号码":calllog.getString(1);
            map.put("number", calllog.getString(0));
            map.put("name", name);   
            map.put("location", "");
            map.put("type", "");
            map.put("time", time);   
            ArrayList<HashMap<String, Object>> history = myPhoneActivity.dbHandler.quryCallHistory(calllog.getString(0));
            if(history != null && history.size() != 0){
                map.put("ringtime", "响铃:" + history.get(history.size()-1).get("ringtime") + "秒");   
            }else{
                map.put("ringtime", "响铃: 0秒");
            }
            calllog_array.add(map);
        	currentIndexLoaded += 1;
        	return true;
    	}
    	currentIndexLoaded += 1;
    	return false;
    }

    //消息通知Handler通话记录数组已更新
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

    //获取通话记录并写入通话记录数组
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
                    //通知Handler更新Listview，实现动态加载数据效果
                	queryCalllogOk(0);
                }
                if(loadCount >= callLogLoadItemPerTime){
                    //用户拖动滚动条到底部，加大一次载入的条数以加快载入速度
                	callLogLoadItemPerTime += 20;
                	break;
                }
            }
            Log.i(TAG, "Current loaded index is " + currentIndexLoaded);
        	cursor.close();
        }
    }
}