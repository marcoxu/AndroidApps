/*
 * Copyright (c) 2012 Shanda Corporation. All rights reserved.
 *
 * Created on 2012-01-30.
 */
package com.snda.myPhone;
  
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.snda.myPhone.myPhoneActivity;

import android.app.Activity;  
import android.os.Bundle;  
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
  
//添加黑名单
public class blacklistManager extends Activity {
	private static final String TAG = "com.snda.myPhone.blacklist_manager";
	public List<HashMap<String, Object>> data_array_for_import = new ArrayList<HashMap<String,Object>>();
	private myAdapter listItemAdapter = null;
	private int importFrom = 0; // 黑名单导入类型  1：通话历史记录导入   2：联系人导入
	public static final int EVENT_CALLLOG_LOAD_DONE = 0;
	public static callLogLoadHandler logHandler = null;

    //消息通知Handler黑名单已更新
    public static void notifyBlacklistUpdate(){
    	if(myPhoneActivity.resultHandler == null){
            Log.i(TAG,"resultHandler is null");
        	return;
    	}
        Log.i(TAG,"send EVENT_BLACKLIST_UPDATE");
        Message msg = new Message();
        msg.what = myPhoneActivity.EVENT_BLACKLIST_UPDATE;
        myPhoneActivity.resultHandler.removeMessages(myPhoneActivity.EVENT_BLACKLIST_UPDATE);
        myPhoneActivity.resultHandler.sendMessage(msg);
    }

    //消息通知Handler加载通话记录
    public static void notifyLoadCalllog(){
    	if(myPhoneActivity.resultHandler == null){
            Log.i(TAG,"resultHandler is null");
        	return;
    	}
        Log.i(TAG,"send EVENT_LOAD_CALLLOG");
        Message msg = new Message();
        msg.what = myPhoneActivity.EVENT_LOAD_CALLLOG;
        myPhoneActivity.resultHandler.removeMessages(myPhoneActivity.EVENT_LOAD_CALLLOG);
        myPhoneActivity.resultHandler.sendMessage(msg);
    }

	//处理子线程消息在UI线程更新Listview
	class callLogLoadHandler extends Handler {
		public callLogLoadHandler() {
			super();
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {			
			case EVENT_CALLLOG_LOAD_DONE:
				//通话记录更新完毕
				Log.i(TAG, "EVENT_CALLLOG_LOAD_DONE is received");
				showImportList();
				break;
			}
		}
	}

    private class ButtonClickListener implements OnClickListener{
    	public synchronized void onClick(View v) {
    		switch(v.getId()){
    		case R.id.select_ok:
    			//执行批量添加黑名单
    			if(listItemAdapter == null){
    				break;
    			}
    			for (int i = 0; i< listItemAdapter.getList().size(); i++) {
    				myAdapter.ViewHolder holder = (myAdapter.ViewHolder) listItemAdapter.getList().get(i);
    				
    				TextView name = (TextView) holder.mName;
    				TextView number = (TextView) holder.mNumbr;
    				if(holder.isChecked){
    					Log.i(TAG, "Checkbox is checked. name = " + name.getText() + ", number = " + number.getText());
    					if(number != null && number.length() > 0){
    						myPhoneActivity.dbHandler.insertBlacklist(1, name.getText().toString(), number.getText().toString());
            				notifyBlacklistUpdate();
    					}
    				}else{
    					Log.i(TAG, "Checkbox is unchecked. name = " + name.getText() + ", number = " + number.getText());
    				}
    			}
    			blacklistManager.this.finish();
    		    break;
    		case R.id.select_cancel:
    			//取消添加操作
    			blacklistManager.this.finish();
                break;
    		}
    	}
    }

	//显示黑名单导入列表（从联系人导入或通话记录导入）
    public void showImportList(){
        ListView blacklist = (ListView) findViewById(R.id.blackListSelect);  
        if(blacklist == null){
        	return;
        }
        
        Log.i(TAG, "importFrom is " + importFrom);
        switch(importFrom){
        case 1:
        	// 从通话历史导入
        	if(callLogManager.calllog_array != null){
        		data_array_for_import = (ArrayList<HashMap<String, Object>>) callLogManager.calllog_array.clone();

        		for(int i=0;i<data_array_for_import.size();i++){
        			HashMap<String, Object> map = data_array_for_import.get(i);
        			map.put("isChecked", 0);
        		}
        		
        		//生成适配器的Item和动态数组对应的元素   
        	   listItemAdapter = new myAdapter(getApplicationContext(), data_array_for_import,
        				                       R.layout.blacklist_select_item,
                                               new String[] {"number", "name", "location"},    
        				                       new int[] {R.id.blacklist_number, R.id.blacklist_name, R.id.blacklist_location});
        		//添加并且显示   
        		if(blacklist.getAdapter() == null){
        			blacklist.addHeaderView(new LinearLayout(getApplicationContext()));
        			blacklist.addFooterView(new LinearLayout(getApplicationContext()));
        		}
        		blacklist.setAdapter(listItemAdapter);  
        	}
    		break;
        case 2:
        	// 从联系人导入
            if(contactManager.contact_array != null){
            	data_array_for_import = (ArrayList<HashMap<String, Object>>) contactManager.contact_array.clone();
        		        		
        		for(int i=0;i<data_array_for_import.size();i++){
        			HashMap<String, Object> map = data_array_for_import.get(i);
        			map.put("isChecked", 0);
        		}
                //生成适配器的Item和动态数组对应的元素   
        		listItemAdapter = new myAdapter(getApplicationContext(), data_array_for_import,
                                                R.layout.blacklist_select_item,
                                                new String[] {"number", "showname"},    
    				                            new int[] {R.id.blacklist_number, R.id.blacklist_name});
                //添加并且显示   
                if(blacklist.getAdapter() == null){
                	blacklist.addHeaderView(new LinearLayout(getApplicationContext()));
                	blacklist.addFooterView(new LinearLayout(getApplicationContext()));
                }
                blacklist.setAdapter(listItemAdapter); 
            }
        default:
        	Log.i(TAG, "Invalid import type" + importFrom);
            break;
        }    	
    }
    
	public class importScrollListener implements ListView.OnScrollListener{
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			view.getLastVisiblePosition();
			if(scrollState == SCROLL_STATE_IDLE){  
				//判断滚动到底部   
				if(view.getLastVisiblePosition()==(view.getCount()-1)){  
					//列表滚动到底部，继续加载通话历史记录
					Log.i(TAG, "onScrollStateChanged to list end " + view.getLastVisiblePosition());
					notifyLoadCalllog();
				}
			}		
		}

		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			
		}
	}

    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.blacklist_selection);

        // 启动Handler处理通话历史记录加载事件，完成导入列表更新
        logHandler = new callLogLoadHandler();
        HandlerThread callloghdlThread = new HandlerThread("myPhone.importCallLog");
        callloghdlThread.start();
        
        importFrom = getIntent().getIntExtra("ImportFrom", 0);
        ListView blacklist = (ListView) findViewById(R.id.blackListSelect);  
        Button btn_ok = (Button) findViewById(R.id.select_ok);  
        Button btn_cancel = (Button) findViewById(R.id.select_cancel);  
        if(blacklist == null || btn_ok == null || btn_cancel == null){
        	return;
        }

        btn_ok.setOnClickListener(new ButtonClickListener());
        btn_cancel.setOnClickListener(new ButtonClickListener());
        blacklist.setOnScrollListener(new importScrollListener());
        
        // 显示黑名单导入列表
        showImportList();
    }  
}