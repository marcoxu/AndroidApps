/*
 * Copyright (c) 2012 Shanda Corporation. All rights reserved.
 *
 * Created on 2012-01-30.
 */
package com.snda.myPhone;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.snda.utils.*;
import com.snda.woa.android.OpenAPI;
import com.snda.myPhone.dataAccessor;
import com.snda.myPhone.blacklistManager;
import com.snda.recommend.Const;
import com.snda.recommend.api.RecommendAPI;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class myPhoneActivity extends Activity {
	private static final String TAG = "myPhone.PhoneManagerActivity";
	private static int callLogListCurrentPosition = 0;
	private String currentContactFilter = null;
    public static final String dbPath = "/data/data/com.snda.myPhone/databases/";
    public static final String dbName = "phoneLocation.db";
	static boolean contactQueryInprogress;
	static boolean calllogQueryInprogress;
	private myPhoneActivity phoneActivity;
	private SystemInfo mSysInfo = null;
	private CallLogUpdateReceiver calllogReceiver = null;

	//生成动态数组，加入数据   
	public static dataAccessor dbHandler = null;
	public static queryResultHandler resultHandler = null;
	public static final int EVENT_CONTACTS_QUERY_DONE   = 0;
	public static final int EVENT_CONTACTS_QUERY_INPROG = 1;
	public static final int EVENT_CALL_RECEIVED         = 2;
	public static final int EVENT_CALLLOG_QUERY_DONE    = 3;
	public static final int EVENT_CALLLOG_QUERY_INPRO   = 4;
	public static final int EVENT_BLACKLIST_UPDATE      = 5;
	public static final int EVENT_CALLLOG_UPDATE        = 6;
	public static final int EVENT_LOAD_CALLLOG          = 7;
	
	//派生SimpleAdapter类用于设置Listview中的ImageView
	private class SimpleAdapertStub extends SimpleAdapter {
		public SimpleAdapertStub(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
			// TODO Auto-generated constructor stub
		}

		public void setViewImage(ImageView v, String value) {
			if(v == null || value == null || value.length() == 0){
				return;
			}
			Bitmap bitmap = null;
			bitmap = dataAccessor.getContactHead(phoneActivity.getApplicationContext(), value);
        	if(bitmap != null){
    			v.setImageBitmap(bitmap);
        	}
		} 	
	}

	//联系人记录获取子线程
	private class contactQueryThread extends Thread {
		private boolean inpgoress;
        public contactQueryThread() {
            super("contactQueryThread");
            inpgoress = false;
        }
        
        public void run() {
        	if(inpgoress == true){
        		return;
        	}
        	inpgoress = true;
        	
        	contactManager.clearContactArray();
        	// query by name
        	Cursor contacts = dataAccessor.getContacts(getApplicationContext(), buildQueryFilter());
            if(contacts != null){
            	contactManager.buildContactArray(phoneActivity, contacts);
            	contacts.close();
            }

            // query by number
            final EditText searchTxt = (EditText) findViewById(R.id.search_text);
            if(searchTxt != null && searchTxt.getText().length() != 0){
                contacts = dataAccessor.queryContactByNumber(getApplicationContext(), searchTxt.getText().toString());
                if(contacts != null){
                	contactManager.buildContactArray(phoneActivity, contacts);
                    contacts.close();
                }
            }

        	inpgoress = false;
        	contactManager.queryContactOk(1);
        }
    }

	//通话历史记录获取子线程
	private class calllogQueryThread extends Thread {
		private boolean inpgoress;
        public calllogQueryThread() {
            super("calllogQueryThread");
            inpgoress = false;
        }
        
        public void run() {
        	if(inpgoress == true){
        		return;
        	}
        	inpgoress = true;
        	callLogManager.getCallogData(getApplicationContext());
        	inpgoress = false;
        	callLogManager.queryCalllogOk(1);
        }
    }
	
	public class CallLogUpdateReceiver extends BroadcastReceiver {
		//自定义一个广播接收器
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Bundle bundle = intent.getExtras();
			String number = bundle.getString("number");
			notifyCalllogUpdate(number);
		}
		
		public CallLogUpdateReceiver(){
			//构造函数，做一些初始化工作，本例中无任何作用
		}
	}

    //消息通知Handler通话记录有更新
    private void notifyCalllogUpdate(String number){
    	if(myPhoneActivity.resultHandler == null){
            Log.i(TAG,"resultHandler is null");
        	return;
    	}
        Log.i(TAG,"send EVENT_CALLLOG_UPDATE");
        Message msg = new Message();
        Bundle data = new Bundle(); 
        data.putString("number", number);         
        msg.setData(data);
        msg.what = myPhoneActivity.EVENT_CALLLOG_UPDATE;
        myPhoneActivity.resultHandler.removeMessages(myPhoneActivity.EVENT_CALLLOG_UPDATE);
        myPhoneActivity.resultHandler.sendMessage(msg);
    }
	
	//通知黑名单导入列表更新Listview
	private void notifyBlacklistImportList(){
        if(blacklistManager.logHandler != null){
        	Message message = new Message();
        	message.what = blacklistManager.EVENT_CALLLOG_LOAD_DONE;
        	blacklistManager.logHandler.removeMessages(blacklistManager.EVENT_CALLLOG_LOAD_DONE);
        	blacklistManager.logHandler.sendMessage(message);
        }
	}
	
	//处理子线程消息在UI线程更新Listview
	class queryResultHandler extends Handler {
		public queryResultHandler() {
			super();
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {			
			case EVENT_CONTACTS_QUERY_DONE:
				//联系人记录更新完毕
                //更新Listview显示最新联系人记录
                showContactList();
                if(msg.arg1 == 1){
                    contactQueryInprogress = false;
                }
                break;
			case EVENT_CALLLOG_QUERY_DONE:
				//通话记录更新完毕
                final ListView calllogListV = (ListView) findViewById(R.id.ListView01);
    	        notifyBlacklistImportList();
                if(calllogListV == null){
        	        Log.i(TAG,"Do not show calllog ");
                	calllogQueryInprogress = false;
        	        break;
                }
                //更新Listview显示最新通话记录
                showCalllogList();
                if(msg.arg1 == 1){
            	    calllogQueryInprogress = false;
                }
                break;
			case EVENT_BLACKLIST_UPDATE:
		        Log.i(TAG,"send EVENT_BLACKLIST_UPDATE");
				showBlackList();
				break;
			case EVENT_LOAD_CALLLOG:
				Log.i(TAG, "EVENT_CALLLOG_LOAD_DONE is received");
				doQueryCalllogs();
				break;
			case EVENT_CALLLOG_UPDATE:
		          Bundle data = msg.getData(); 
		          String number = data.getString("number");
		          if(number != null){
		        	  Cursor cursor = dataAccessor.getCallLogsByNumber(getApplication(), number);
		        	  if(cursor == null || cursor.getCount() == 0){
		        		  if(cursor != null){
		        			  cursor.close();
		        		  }
		        		  break;  
		        	  }
		        	  
		        	  cursor.moveToFirst();
		        	  HashMap<String, Object> map = null;
		        	  boolean logExists = false;
		        	  if(callLogManager.findContactInCalllogArray(number)){	        		  
		        		  for(HashMap<String, Object> m: callLogManager.calllog_array){  
		        			  if(m.containsValue(number)){
		        				  map = m;
		        			  }
		        		  } 
		        		  
		        		  if(map == null){
		        			  cursor.close();
		        			  break;
		        		  }
			        	  callLogManager.calllog_array.remove(map);
			        	  map.remove("time");
			        	  map.remove("ringtime");
			        	  Log.i(TAG, "Call log update for existing number " + number);
			        	  logExists = true;
		        	  }else{
			        	  map = new HashMap<String, Object>();
			        	  Log.i(TAG, "Call log update for non-existing number " + number);
		        	  }

		        	  SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		        	  Date date = new Date(Long.parseLong(cursor.getString(3)));
		        	  String time = sfd.format(date);
		        	  map.put("time", time);   

		        	  if(!logExists){
			        	  HashMap<String, Object> location = dbHandler.queryPhoneLocation(cursor.getString(0));
			        	  String name = (cursor.getString(1)==null || cursor.getString(1).length() == 0)?"陌生号码":cursor.getString(1);
			        	  map.put("number", cursor.getString(0));
			        	  map.put("name", name);   
			        	  map.put("location", "");
			        	  map.put("type", "");
			        	  if(location != null){
			        		  map.put("location", "   ("+ location.get("location") + ")");
			        		  map.put("type", "   ("+ location.get("type") + ")");
			        		  Log.i(TAG, "Phone location is found:" + location.get("location") + "," + location.get("type"));
			        	  }
		        	  }

		              ArrayList<HashMap<String, Object>> history = dbHandler.quryCallHistory(number);
		              if(history != null && history.size() != 0){
		                  map.put("ringtime", "响铃:" + history.get(history.size()-1).get("ringtime") + "秒");   
		              }else{
		                  map.put("ringtime", "响铃: 0秒");   
		              }
        			  cursor.close();
        			  callLogManager.calllog_array.add(0, map);
		        	  showCalllogList();
		          }
		          break;
			}
	        Log.i(TAG,"handle message done");
		}
	}
    
	public class scrollListener implements ListView.OnScrollListener{
		public void onScroll(AbsListView arg0,
				int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			if (totalItemCount <= 0){
				return;
			}
			
			if (firstVisibleItem + visibleItemCount >= totalItemCount){
				//当前可视第1条index + 当前可视界面存在？条  >= 总数Index 时，说明在底端
				//doQueryCalllogs();
				Log.i(TAG, "onScroll to list end");
			}
		}
		
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			//当不滚动时  
			callLogListCurrentPosition = view.getLastVisiblePosition();
			if(scrollState == SCROLL_STATE_IDLE){  
				//判断滚动到底部   
				if(view.getLastVisiblePosition()==(view.getCount()-1)){  
					//your code
					Log.i(TAG, "onScrollStateChanged to list end " + view.getLastVisiblePosition());
					doQueryCalllogs();
				}
			}		
		}
	}

	//显示通话记录Listview
	private void showCalllogList(){
        ListView contacts = (ListView) findViewById(R.id.ListView01);  
        if(contacts != null){
        	contacts.setOnScrollListener(new scrollListener());
            if(contacts.getOnItemClickListener() == null){
            	contacts.setOnItemClickListener(new OnItemClickListener() {
        			public synchronized void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
        					long arg3) {
        				// TODO Auto-generated method stub
    	  		        Intent CallLogAct = new Intent(getApplicationContext(), callLogs.class);
        		        CallLogAct.putExtra("callnumber", (String)callLogManager.calllog_array.get(arg2-1).get("number"));
        		        CallLogAct.putExtra("name", (String)callLogManager.calllog_array.get(arg2-1).get("name"));
        		        CallLogAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		        getApplicationContext().startActivity(CallLogAct);
        			}
        		});
            }
        }else{
        	return;
        }

        //生成适配器的Item和动态数组对应的元素   
		ArrayList<HashMap<String, Object>> calllog_array_clone = 
				  (ArrayList<HashMap<String, Object>>) callLogManager.calllog_array.clone();
        SimpleAdapter listItemAdapter = new SimpleAdapter(this, calllog_array_clone,
                                           R.layout.list_items,
                                           new String[] {"number", "name", "time", "location", "ringtime"},    
                                           new int[] {R.id.number,R.id.name,R.id.time, R.id.location, R.id.ringtime});

        
        //添加并且显示   
        if(contacts.getAdapter() == null){
            contacts.addHeaderView(new LinearLayout(this));
        }
        contacts.setAdapter(listItemAdapter);
        contacts.setSelection(callLogListCurrentPosition);        
        Log.i(TAG,"ListView adapter is set");   	
    }

	//显示联系人Listview
    private void showContactList(){
        ListView contactlist = (ListView) findViewById(R.id.ContactsListView);
        if(contactlist == null){
        	return;
        }
                
		ArrayList<HashMap<String, Object>> contact_array_clone = (ArrayList<HashMap<String, Object>>) contactManager.contact_array.clone();
        
        //生成适配器的Item和动态数组对应的元素   
        SimpleAdapertStub listItemAdapter = new SimpleAdapertStub(this, contact_array_clone,
                                                                  R.layout.contacts_list,
                                                                  new String[] {"contactimg", "showname", "number"},    
                                                                  new int[] {R.id.contactorimg,R.id.contactorinfo, R.id.contactornumber});
        
        //添加并且显示   
        contactlist.setAdapter(listItemAdapter);
    }
    
    
    //启动子线程更新联系人数组
    private boolean doQueryContacts(){
        if(!contactQueryInprogress){
        	contactQueryInprogress = true;
            final EditText searchTxt = (EditText) findViewById(R.id.search_text);
            if(searchTxt != null){
            	Log.i(TAG, "search filter changed to " + searchTxt.getText().toString());
                currentContactFilter = searchTxt.getText().toString();
            }else{
            	Log.i(TAG, "search filter changed to null");
            	currentContactFilter = null;
            }

        	new contactQueryThread().start();
            Log.i(TAG,"contactQueryThread is started");
            return true;
        }
        return false;
    }
    
    //启动子线程更新通话记录数组
    private void doQueryCalllogs(){
        if(!calllogQueryInprogress){
        	calllogQueryInprogress = true;
        	new calllogQueryThread().start();
            Log.i(TAG,"calllogQueryThread is started");
        }
    }

    //根据过滤条件是否改变，判断联系人数组是否需要更新
    private boolean isContactArrayNeedUpdate(){
        final EditText searchTxt = (EditText) findViewById(R.id.search_text);

        Log.i(TAG, "currentContactFilter is " + currentContactFilter + ", search txt is " + searchTxt.getText().toString());
        if((!contactManager.contact_array.isEmpty()) &&
           ((currentContactFilter == null || currentContactFilter.length() == 0) && 
            (searchTxt == null || searchTxt.getText().length() == 0)) ||
            (searchTxt.getText().toString().equals(currentContactFilter))){
            Log.i(TAG, "Do not need to update contacts");
        	// contacts is not changed
        	return false;
        }
        Log.i(TAG, "Do need to update contacts");
        return true;
    }
    
    //根据搜索框输入生成数据库查询条件
    private String buildQueryFilter(){
        if(currentContactFilter != null && currentContactFilter.length() > 0){
        	return PhoneLookup.DISPLAY_NAME + " LIKE '%" + currentContactFilter + "%'";
        }
        return null;
    }
    
    //拷贝Assets下的号码归属地数据库文件
    private void loadPhoneLocationDB(){
        // 拷贝号码归属地数据库到/data/com.snda.myPhone/databases
    	String filePath = dbPath + dbName;
        InputStream assetsDB = null;
		try {
			assetsDB = this.getAssets().open("phoneLocation.jpg");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		File dbdir = new File(dbPath); 
        if (!dbdir.exists()) { 
              try { 
                  //在指定的文件夹中创建文件 
            	  dbdir.mkdirs(); 
            } catch (Exception e) { 
    			e.printStackTrace();
            } 
        } 
		File phonelocationdbfile = new File(filePath); 
        if (!phonelocationdbfile.exists()) { 
        	try { 
        		//在指定的文件夹中创建文件 
        		phonelocationdbfile.createNewFile(); 
        	} catch (Exception e) { 
        		e.printStackTrace();
        	} 

        	OutputStream dbOut = null;
        	try {
        		dbOut = new FileOutputStream(filePath, true);
        	} catch (FileNotFoundException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
        	
        	byte[] buffer = new byte[1024];
        	int length = 0;
        	try {
        		while ((length = assetsDB.read(buffer)) > 0) {
        			dbOut.write(buffer, 0, length);
        		}
        	} catch (IOException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
	        try {
				dbOut.flush();
				dbOut.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    	
        	Log.i(TAG, "Finish copying DB");
        }
        
        
        try {
			assetsDB.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
    
    //创建长按弹出式菜单,这是一个回调函数
    @Override                  
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        
        if (v instanceof Button) {
        	menu.setHeaderTitle("添加黑名单");
            menu.add(0, 0, 0, "从通话记录导入");  //添加菜单项
            menu.add(0, 1, 0, "从联系人导入");  
            menu.add(0, 2, 0, "添加归属地");  
            menu.add(0, 3, 0, "输入号码");  
        }else if(v instanceof ListView){
        	menu.setHeaderTitle("黑名单");
            menu.add(0, 4, 0, "删除");  //添加菜单项
        }
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
		Intent blacklistAct = null;
        switch(item.getItemId()){
        case 0:
        	Log.i(TAG, "从通话记录导入");
			blacklistAct = new Intent(phoneActivity.getApplicationContext(), blacklistManager.class);
			blacklistAct.putExtra("ImportFrom", 1);
			blacklistAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplicationContext().startActivity(blacklistAct);
            break;
        case 1:
        	Log.i(TAG, "从联系人导入");
			blacklistAct = new Intent(phoneActivity.getApplicationContext(), blacklistManager.class);
			blacklistAct.putExtra("ImportFrom", 2);
			blacklistAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplicationContext().startActivity(blacklistAct);
            break;
        case 2:
        	Log.i(TAG, "添加归属地");
			blacklistAct = new Intent(phoneActivity.getApplicationContext(), blacklistEditor.class);
			blacklistAct.putExtra("ImportFrom", 3);
			blacklistAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplicationContext().startActivity(blacklistAct);
        case 3:
        	Log.i(TAG, "输入号码");
			blacklistAct = new Intent(phoneActivity.getApplicationContext(), blacklistEditor.class);
			blacklistAct.putExtra("ImportFrom", 4);
			blacklistAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplicationContext().startActivity(blacklistAct);
            break;
        case 4:
        	Log.i(TAG, "删除黑名单");
        	AdapterView.AdapterContextMenuInfo menuInfo;
        	menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        	int index = menuInfo.position - 1;
	        ArrayList<HashMap<String, Object>> blacklist_show = dbHandler.getBlacklist();
        	Log.i(TAG, "blacklist select " + index + ":" + blacklist_show.size());
	        if(index < blacklist_show.size()){
	        	if(blacklist_show.get(index).get("blockType").toString().equals("location")){
		        	dbHandler.removeBlacklist(0, blacklist_show.get(index).get("blockContent").toString());
	        	}else if(blacklist_show.get(index).get("blockType").toString().equals("number")){
		        	dbHandler.removeBlacklist(1, blacklist_show.get(index).get("blockContent").toString());
	        	}else{
	            	Log.i(TAG, "Invalid block type " + blacklist_show.get(index).get("blockType").toString());
	        	}
	        }
	        showBlackList();
        	break;
        }
        return super.onContextItemSelected(item);

    }
    
    //设置按钮样式
    private void setButtonDisplay(int btnId){
		Button history_btn = (Button)findViewById(R.id.historyList);
		Button contact_btn = (Button)findViewById(R.id.contactors);
		Button blacklist_btn = (Button)findViewById(R.id.blacklist);
		history_btn.setTextColor(Color.BLACK);
		contact_btn.setTextColor(Color.BLACK);
		blacklist_btn.setTextColor(Color.BLACK);
		history_btn.setTextSize(15);
		contact_btn.setTextSize(15);
		blacklist_btn.setTextSize(15);
		
		switch(btnId){
		case R.id.historyList:
			history_btn.setTextColor(Color.RED);
			break;
		case R.id.contactors:
			contact_btn.setTextColor(Color.RED);
			break;
		case R.id.blacklist:
			blacklist_btn.setTextColor(Color.RED);
			break;
		}

    }
    //监听处理按钮点击事件
    private class ButtonClickListener implements OnClickListener{
    	public synchronized void onClick(View v) {
            final LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.tabLayout);
    		switch(v.getId()){
    		case R.id.historyList:
        		LinearLayout layout = (LinearLayout) inflater.inflate(
        				R.layout.page2, null).findViewById(R.id.widget30);
        		mainLayout.removeAllViews();
        		mainLayout.addView(layout);
        		setButtonDisplay(R.id.historyList);
        		callLogManager.queryCalllogOk(1);
                break;
    		case R.id.contactors:
    			RelativeLayout Rlayout = (RelativeLayout) inflater.inflate(
    					R.layout.page3, null).findViewById(R.id.cotactslayout);
    			mainLayout.removeAllViews();
    			mainLayout.addView(Rlayout);
        		setButtonDisplay(R.id.contactors);

        		Button searchBtn = (Button) findViewById(R.id.bt_search);
    			searchBtn.setOnClickListener(new OnClickListener() {
    				public synchronized void onClick(View v) {
    					// TODO Auto-generated method stub
    					if(!isContactArrayNeedUpdate()){
    						// contacts is not changed
    						return;
    					}     
    					currentContactFilter = null;
    					doQueryContacts();
    				}
    			});
    			
    			if(!isContactArrayNeedUpdate()){
    				// contacts is not changed
    				contactManager.queryContactOk(1);
    				return;
    			}            	
    			Log.i(TAG,"Click contacts to query");
    			doQueryContacts();
                break;
    		case R.id.blacklist:
    			RelativeLayout blacklistLayout = (RelativeLayout) inflater.inflate(
    					R.layout.blacklist, null).findViewById(R.id.blacklistlayout);
    			mainLayout.removeAllViews();
    			mainLayout.addView(blacklistLayout);
        		setButtonDisplay(R.id.blacklist);
    			
    			Button addBlackBtn = (Button) findViewById(R.id.blackListAdd);  
    	        ListView showBlackList = (ListView) findViewById(R.id.blackListShow);  
    	        if(showBlackList != null){
        	        registerForContextMenu(showBlackList);
    	        }else{
    	        	break;
    	        }

    	        if(addBlackBtn != null){
    	        	registerForContextMenu(addBlackBtn);
    	        	addBlackBtn.setOnClickListener(new OnClickListener() {
						
						public void onClick(View v) {
							// TODO Auto-generated method stub							
		    	        	openContextMenu(v);
						}
					});
    	        }else{
    	        	break;
    	        }

    	        showBlackList();
    	        break;
    		case R.id.snda_recommend:
    	    	TelephonyManager phoneMgr=(TelephonyManager)phoneActivity.getSystemService(Context.TELEPHONY_SERVICE);
    	    	Log.i(TAG, "本机号码：" + phoneMgr.getLine1Number());
    	    	
				boolean bRet = RecommendAPI.init(myPhoneActivity.this,
						"800001980", "9999");
				RecommendAPI.setSdid("1000000");
				RecommendAPI.setPhoneNum(phoneMgr.getLine1Number());
				RecommendAPI.setFromPos(myPhoneActivity.this,
						Const.Pos.MAIN_TOP);
				if (bRet == true) {
					RecommendAPI.openRecommendActivity(myPhoneActivity.this);
				}
    			break;
    		}
    	}
    }
    
    private void showBlackList(){
        ArrayList<HashMap<String, Object>> blacklist_show = null;
        blacklist_show = dbHandler.getBlacklist();
        ListView showBlackList = (ListView) findViewById(R.id.blackListShow);  
        if(showBlackList == null){
            Log.i(TAG,"Black list view is not found");
        	return;
        }
        if(blacklist_show == null){
        	blacklist_show = new ArrayList<HashMap<String, Object>>();
        }

    	//生成适配器的Item和动态数组对应的元素   
    	SimpleAdapter showlistItemAdapter = new SimpleAdapter(getApplicationContext(), blacklist_show,
    			R.layout.blacklist_item,
    			new String[] {"name", "blockContent"},    
    			new int[] {R.id.blacklist_name, R.id.blacklist_number});
    	
    	
    	//添加并且显示   
    	if(showBlackList.getAdapter() == null){
    		showBlackList.addHeaderView(new LinearLayout(getApplicationContext()));
    		showBlackList.addFooterView(new LinearLayout(getApplicationContext()));
    	}
    	showBlackList.setAdapter(showlistItemAdapter);
    }
    
    // 判断service是否启动
	public static boolean isServiceStarted(Activity act)
	{
		ActivityManager myManager = 
				(ActivityManager)act.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = 
				(ArrayList<RunningServiceInfo>) myManager.getRunningServices(30);
		for(int i = 0 ; i<runningService.size();i++)
		{
			if(runningService.get(i).service.getClassName().toString().equals("com.snda.myPhone.myPhoneService"))
			{
				return true;
			}
		}
		return false;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 判断是否已登录
        if(mainLogin.SessionId == null || mainLogin.UserId == null){
            // 未登录，进入登录界面
            Log.i(TAG, "=======> OpenAPI.loginStat " + OpenAPI.getStatus());
            Intent startLogin = new Intent(getApplicationContext(), mainLogin.class);	
            startLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().getApplicationContext().startActivity(startLogin);
            this.finish();
            return;
        }
        
		setContentView(R.layout.main);
        
        //注册接收器
        calllogReceiver  = new CallLogUpdateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.snda.myPhoneService.CALLLOG_UPDATE");
        myPhoneActivity.this.registerReceiver(calllogReceiver,filter);

    	// 创建归属地/响铃时间/黑名单数据库
//    	loadPhoneLocationDB();
    	// 数据统一上报
    	mSysInfo = new SystemInfo(this);
    	mSysInfo.OnStart();
    	mSysInfo.SetSendErrorReport(true);
    	mSysInfo.SendCustomEvent(this, "onCreate", "myPhoneActivity");
    	
    	// 初始化
        resultHandler = new queryResultHandler();
        phoneActivity = this;
        contactQueryInprogress = false;
        calllogQueryInprogress = false;
    	currentContactFilter = null;
    	
    	// 如果service未启动，则启动它
    	if(!isServiceStarted(this)){
    		Log.i(TAG, "Start myPhoneService");
        	Intent i = new Intent(Intent.ACTION_RUN);
            i.setClass(this.getApplicationContext(), myPhoneService.class);
            this.getApplicationContext().startService(i);    	
    	}
    	
    	dbHandler = new dataAccessor(getApplicationContext(), dbName);
    	//启动Handler
        HandlerThread queryhdlThread = new HandlerThread("myPhone.query");
        queryhdlThread.start();
        
        final LayoutInflater inflater = LayoutInflater.from(this);
        final LinearLayout mainLayout = (LinearLayout) findViewById(R.id.tabLayout);
        Button callLogBtn = (Button) findViewById(R.id.historyList);
        Button recommendBtn = (Button) findViewById(R.id.snda_recommend);
        Button contactBtn = (Button) findViewById(R.id.contactors);
        Button blacklistBtn = (Button) findViewById(R.id.blacklist);
    
        //按钮Click事件监听
        callLogBtn.setOnClickListener(new ButtonClickListener());
        contactBtn.setOnClickListener(new ButtonClickListener());
        blacklistBtn.setOnClickListener(new ButtonClickListener());
        recommendBtn.setOnClickListener(new ButtonClickListener());

        //启动时默认显示联系人页面
        RelativeLayout cotactslayout = (RelativeLayout) inflater.inflate(
        		R.layout.page3, null).findViewById(R.id.cotactslayout);
        mainLayout.removeAllViews();
        mainLayout.addView(cotactslayout);
		setButtonDisplay(R.id.contactors);

        ListView contactlist = (ListView) findViewById(R.id.ContactsListView);
        if(contactlist.getAdapter() == null){
            contactlist.addHeaderView(new LinearLayout(this.getApplicationContext()));
        }

        Button searchBtn = (Button) findViewById(R.id.bt_search);
        searchBtn.setOnClickListener(new OnClickListener() {
            public synchronized void onClick(View v) {
                // TODO Auto-generated method stub
                if(!isContactArrayNeedUpdate()){
                	// 联系人列表无更改，无需重新载入
                	return;
                }
            	// 载入联系人列表
                if(!doQueryContacts()){
                    Toast.makeText(myPhoneActivity.this, "联系人载入中，请稍后重试", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //启动子线程更新联系人及通话记录数组
        doQueryContacts();
        doQueryCalllogs();
    }

	protected void onDestroy() {  
		super.onDestroy();  
		if(calllogReceiver != null){
            myPhoneActivity.this.unregisterReceiver(calllogReceiver);
		}
	}
}