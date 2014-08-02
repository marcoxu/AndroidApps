package com.marco.smsrouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.marco.smsrouter.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class smsRteHistoryActivity extends Activity{
	private static final String TAG = "smsRouter.SmsRteActivity";
	private myAdapter listItemAdapter = null;
	private static Activity thisAct = null;
	public static final int EVENT_LOAD_RTEHISTORY_DONE = 0;
	public static final int EVENT_DELETE_RTEHISTORY = 1;
	public List<HashMap<String, Object>> data_array_for_import = new ArrayList<HashMap<String,Object>>();
	public static historyResultHandler historyHandler = null;
	
    //消息通知Handler加载通话记录
    public static void notifyLoadRteHistory(){
    	if(SmsRteActivity.resultHandler == null){
            Log.i(TAG,"resultHandler is null");
        	return;
    	}
        Log.i(TAG,"send EVENT_LOAD_RTEHISTORY");
        Message msg = new Message();
        msg.what = SmsRteActivity.EVENT_LOAD_RTEHISTORY;
        SmsRteActivity.resultHandler.removeMessages(SmsRteActivity.EVENT_LOAD_RTEHISTORY);
        SmsRteActivity.resultHandler.sendMessage(msg);
    }

	public class importScrollListener implements ListView.OnScrollListener{
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			view.getLastVisiblePosition();
			if(scrollState == SCROLL_STATE_IDLE){  
				//判断滚动到底部   
				if(view.getLastVisiblePosition()==(view.getCount()-1)){  
					//列表滚动到底部，继续加载通话历史记录
					Log.i(TAG, "onScrollStateChanged to list end " + view.getLastVisiblePosition());
					notifyLoadRteHistory();
				}
			}		
		}

		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
  	  requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
	  setContentView(R.layout.activity_sms_rte_history);
	  
	  thisAct = this;
      Log.i(TAG,"send smsRteHistoryActivity");
      historyHandler = new historyResultHandler();

      Button delBtn = (Button) findViewById(R.id.btndel);
      //按钮Click事件监听
      delBtn.setOnClickListener(new buttonClickListener());

      ListView rteHistoryList = (ListView) findViewById(R.id.rteHistoryList);  
      if(rteHistoryList == null){
      	return;
      }

      rteHistoryList.setOnScrollListener(new importScrollListener());
      
      notifyLoadRteHistory();
      // 显示黑名单导入列表
      //showImportList();
	}
	
	private void dialogForDelHistory() {
        LayoutInflater inflater_current = LayoutInflater.from(smsRteHistoryActivity.this);   
		final View DialogView = inflater_current.inflate (R.layout.confirm_dialog, null);       
		new AlertDialog.Builder(smsRteHistoryActivity.this)   
		.setTitle(" ")
		.setView(DialogView)
		.setPositiveButton("确定", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton)
            {
		        Message msg = new Message();
		        msg.what = smsRteHistoryActivity.EVENT_DELETE_RTEHISTORY;
		        smsRteHistoryActivity.historyHandler.removeMessages(smsRteHistoryActivity.EVENT_DELETE_RTEHISTORY);
		        smsRteHistoryActivity.historyHandler.sendMessage(msg);
            }
		}) 
		.setNegativeButton("取消", null)  
		.show();             	    

	}

	private void startDelHistory() {
		//执行批量添加黑名单
		if(listItemAdapter == null){
			return;
		}
		for (int i = 0; i< listItemAdapter.getList().size(); i++) {
			myAdapter.ViewHolder holder = (myAdapter.ViewHolder) listItemAdapter.getList().get(i);
			
			TextView fromNo = (TextView) holder.getmFromNo();
			TextView toNo = (TextView) holder.getmToNo();
			if(holder.isChecked()){
				String sFromName = SmsRteActivity.mRteHistory.get(i).get("FromName").toString();
				String sFromNo = SmsRteActivity.mRteHistory.get(i).get("FromNo").toString();
				String sToName = SmsRteActivity.mRteHistory.get(i).get("ToName").toString();
				String sToNo = SmsRteActivity.mRteHistory.get(i).get("ToNo").toString();
				String sdate = SmsRteActivity.mRteHistory.get(i).get("date").toString();
				String scontent = SmsRteActivity.mRteHistory.get(i).get("content").toString();
			    dbAccessor dbQuery = new dbAccessor(thisAct, SmsRteActivity.dbPath+SmsRteActivity.dbName);
			    dbQuery.delForwardHistory(sFromName, sFromNo, sToName, sToNo, sdate, scontent);
			    notifyLoadRteHistory();
			}else{
				Log.i(TAG, "Checkbox is unchecked. mFromNo = " + fromNo.getText() + ", toNo = " + toNo.getText());
			}
		}
	}

    @SuppressWarnings("unchecked")
	public void showImportList(){
        Log.i(TAG,"Start showImportList");
        ListView rteHistoryList = (ListView) findViewById(R.id.rteHistoryList);  
        if(rteHistoryList == null){
        	return;
        }
        
    	// 从通话历史导入
        data_array_for_import = (ArrayList<HashMap<String, Object>>) SmsRteActivity.mRteHistory.clone();

		for(int i=0;i<data_array_for_import.size();i++){
			HashMap<String, Object> map = data_array_for_import.get(i);
			map.put("isChecked", 0);
		}
    		
    		//生成适配器的Item和动态数组对应的元素   
    	   listItemAdapter = new myAdapter(getApplicationContext(), data_array_for_import,
    				                       R.layout.sms_rte_history_item,
                                           new String[] {"ToNo", "date", "content"},    
    				                       new int[] {R.id.to_number, R.id.sms_date, R.id.sms_content});
    		//添加并且显示   
    		if(rteHistoryList.getAdapter() == null){
    			rteHistoryList.addHeaderView(new LinearLayout(getApplicationContext()));
    			rteHistoryList.addFooterView(new LinearLayout(getApplicationContext()));
    		}
    		rteHistoryList.setAdapter(listItemAdapter);  
    	}
    
	class historyResultHandler extends Handler {
		public historyResultHandler() {
			super();
		}

		public void handleMessage(Message msg) {
			switch (msg.what) {			
			case EVENT_LOAD_RTEHISTORY_DONE:
		        Log.i(TAG,"recv EVENT_LOAD_RTEHISTORY_DONE");
		        showImportList();
                break;
			case EVENT_DELETE_RTEHISTORY:
		        Log.i(TAG,"recv EVENT_DELETE_RTEHISTORY");
		        startDelHistory();
                break;
			}
	        Log.i(TAG,"handle message done");
		}
	}
	
    private class buttonClickListener implements OnClickListener{
    	public synchronized void onClick(View v) {
    		switch(v.getId()){
    		case R.id.btndel:
    			dialogForDelHistory();
    		    break;
    		}
    	}
    }

}
