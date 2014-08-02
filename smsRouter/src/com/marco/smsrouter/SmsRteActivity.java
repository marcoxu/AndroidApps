package com.marco.smsrouter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import com.marco.smsrouter.R;
import com.marco.smsrouter.dataaccessor.dataAccessor;
import com.marco.smsrouter.preference.DialogPreference;
import com.marco.smsrouter.preference.SmsServiceTogglePreference;
import com.marco.smsrouter.service.smsRteService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class SmsRteActivity extends PreferenceActivity implements OnTouchListener {
	private static final String TAG                  = "smsRouter.SmsRteActivity";
	public static final String dbPath                = "/mnt/sdcard/smsRte/databases/";
	public static final String dbName                = "smsrte.db";
	private SmsServiceTogglePreference mToggleServPreference   = null;
	private DialogPreference mDialogPreference       = null;
	private SmsRteActivity thisAct                   = null;
	public static int DIALOG_TOGGLE                  = 0;
	public static int DIALOG_ABOUT                   = 1;
	private String KEY_TOGGLE_SERVICE                = "sms_router_key";
	public final static int EVENT_SERVICE_START_DONE = 0;
	public final static int EVENT_LOAD_RTEHISTORY    = 1;
	public final static int EVENT_FLOW_CONTROL_CHG   = 2;
	public static serviceResultHandler resultHandler = null;
	public static ArrayList<HashMap<String, Object>> mRteHistory = new ArrayList<HashMap<String, Object>>();
	private static dataAccessor mAccessor            = null;
	private PreferenceScreen mFlowControl            = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
  	  //requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
	  //setContentView(R.layout.activity_sms_rte);
  	  addPreferencesFromResource(R.xml.smsrte_preference);    
  	  thisAct = this;
      resultHandler = new serviceResultHandler();
	  setmAccessor(smsCreater.createDataAccessor(smsCreater.SQLITE_DB_TYPE, getBaseContext()));
  	
  	  mToggleServPreference = 
              (SmsServiceTogglePreference) getPreferenceScreen().findPreference(KEY_TOGGLE_SERVICE); //取得我们自定义的preference
  	  mFlowControl = (PreferenceScreen) getPreferenceScreen().findPreference(getString(R.string.sms_rte_flow_ctrl));

  	  View.OnClickListener listener = new View.OnClickListener() {
          @Override
          public void onClick(View v) {
        	  Log.i(TAG, "onTouch start");
        	  dbAccessor dbQuery = new dbAccessor(thisAct, dbPath+dbName);
        	  ArrayList<HashMap<String, Object>> callNo = dbQuery.getForwardNo();
        	  int callNoSize = callNo.size();
        	  boolean isServiceRunning = isServiceStarted(thisAct);
        	  Log.i(TAG, "callNo is " + callNo);
        	  
        	  if(callNoSize > 0  && isServiceRunning){
  			    	//删除db文件
  		    		Log.i(TAG, "Stop service");
  		    		thisAct.getApplicationContext().stopService(new Intent(thisAct.getApplicationContext(), smsRteService.class));  
  		    		if(!isServiceStarted(thisAct)){
  		            	mToggleServPreference.setImageButtonOff();
  		            	mToggleServPreference.updateServiceDescWithCallNo(null);
  					  	delServConfigDB();  
  		    		}
  			  }else if(callNoSize <= 0  && !isServiceRunning){
  				  Log.i(TAG, "Start service");
          		  inputForwardCallNo();
  			  }
  	    	  Log.i(TAG, "onTouch done");
          }
      };
       
	  Log.i(TAG, "Start setmSwitchServListener");
	  if(mToggleServPreference != null){
		  mToggleServPreference.setToggleButtonId(R.id.switch_service);
		  mToggleServPreference.setToggleResourceLayout(R.layout.toggle_preference);
		  mToggleServPreference.setDialogDisabled();
		  mToggleServPreference.setToggleClickListener(listener);
	  }
	  else
	     return;

	  if(!checkServConfigDB()){
 	      //设置初始状态为true  
		  createServConfigDB();
 	  }

      dbAccessor dbQuery = new dbAccessor(this, dbPath+dbName);
      ArrayList<HashMap<String, Object>> callNo = dbQuery.getForwardNo();
      int callNoSize = callNo.size();

	  Log.i(TAG, "Start setImageButton state");
	  if(callNoSize > 0){
 	      //设置初始状态为true  
		  Log.i(TAG, "setImageButtonOn");
 	      mToggleServPreference.setImageButtonOn();
	      initFlowConfigPreference();
 	  }else{
 	      //设置初始状态为false  
		  Log.i(TAG, "setImageButtonOff");
 	      mToggleServPreference.setImageButtonOff();
 	  }

	    Log.i(TAG, "Try start service");
 	    // 如果service未启动，则启动它
 	    if(callNoSize > 0 && !isServiceStarted(this) ){
 	   		Log.i(TAG, "Start smsRteService");
 	       	Intent i = new Intent(Intent.ACTION_RUN);
 	        i.setClass(this.getApplicationContext(), smsRteService.class);
 	        this.getApplicationContext().startService(i); 
 	    }	
 	    
        //Try and find app version number
        String version;
        PackageManager pm = this.getPackageManager();
        try {
          //Get version number, not sure if there is a better way to do this
          version = " v" +
          pm.getPackageInfo(
        		  SmsRteActivity.class.getPackage().getName(), 0).versionName;
        } catch (NameNotFoundException e) {
          version = "";
        }

        Log.i(TAG, "set aboutPref");
        mDialogPreference =
      	      (DialogPreference) getPreferenceScreen().findPreference("smsForwarder");
 		
        if(mDialogPreference != null)
 			Log.i(TAG, "aboutPref ok");
 		else
 	        Log.i(TAG, "aboutPref is null");
        mDialogPreference.setDialogType(DIALOG_ABOUT);
        mDialogPreference.setDialogTitle(getString(R.string.app_name) +version);
        mDialogPreference.setDialogLayoutResource(R.layout.about);
	}

	private void initFlowConfigPreference() {
		ArrayList<HashMap<String, Object>> record = getmAccessor().getFlowCtlRecord();
		int flowCtlType = -1;
		int flowctlmax = -1;
		if(record != null && record.size() > 0) {
			flowCtlType = (Integer) record.get(0).get("flowctltype");
			flowctlmax = (Integer) record.get(0).get("flowctlmax");
			updateFlowControlSummary(flowCtlType, flowctlmax);
		} else {
			updateFlowControlSummary(-1, 0);
		}
	}
	
	private void updateFlowControlSummary(int type, int max) {
        if(mFlowControl != null) {
        	switch(type) {
        	case smsCreater.BLOCK_FLOW_CONTROL_BY_YEAR:
        		mFlowControl.setSummary("每年最多转发" + max + "条短信");
        		break;
        	case smsCreater.BLOCK_FLOW_CONTROL_BY_MONTH:
        		mFlowControl.setSummary("每月最多转发" + max + "条短信");
        		break;
        	case smsCreater.BLOCK_FLOW_CONTROL_BY_DAY:
        		mFlowControl.setSummary("每日最多转发" + max + "条短信");
        		break;
        	default:
        		mFlowControl.setSummary(getString(R.string.sms_rte_flow_ctrl_summary));
        		break;
        	}
        }
	}

	private void inputForwardCallNo() {
        LayoutInflater inflater_current = LayoutInflater.from(SmsRteActivity.this);   
		final View DialogView = inflater_current.inflate (R.layout.callno_add, null);       
		new AlertDialog.Builder(SmsRteActivity.this)   
		.setTitle("请输入转发目的号码")  
		.setIcon(android.R.drawable.ic_dialog_info)  
		.setView(DialogView)
		.setPositiveButton("确定", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton)
            {
	             EditText editText =(EditText) DialogView.findViewById(R.id.add_callno);
	             Log.i(TAG,"category editText is " + editText.getText());
	             
	             if(editText.getText().toString().trim().length() == 0)
	            	 return;
	             
	             //pwdConfigActivity.conf_dbHdlr.insertCategory(editText.getText().toString());
	             //updateCategoryList();
         		  Intent i = new Intent(Intent.ACTION_RUN);
         		  i.putExtra("CallNo", editText.getText().toString().trim());
         		  i.setClass(thisAct.getApplicationContext(), smsRteService.class);
         		  thisAct.getApplicationContext().startService(i);  
         		  
         		  if(isServiceStarted(thisAct)){
         			  serviceStartOk(editText.getText().toString().trim());
         		  }
            }
		}) 
		.setNegativeButton("取消", null)  
		.show();             	    

	}
	
	@Override    
	public boolean onTouch(View v, MotionEvent event) {     
		if(event.getAction() == MotionEvent.ACTION_UP){     
	    	Log.i(TAG, "onTouch start");
	        dbAccessor dbQuery = new dbAccessor(this, dbPath+dbName);
	        ArrayList<HashMap<String, Object>> callNo = dbQuery.getForwardNo();
	        int callNoSize = callNo == null?0:callNo.size();

	    	Log.i(TAG, "callNoSize is " + callNoSize);
	    	boolean isServiceRunning = isServiceStarted(thisAct);
	        if(callNoSize > 0  && isServiceRunning){
			    //删除db文件
		    	Log.i(TAG, "Stop service");
		    	thisAct.getApplicationContext().stopService(new Intent(thisAct.getApplicationContext(), smsRteService.class));  
		        if(!isServiceStarted(thisAct)){
	       	      	mToggleServPreference.setImageButtonOff();
					delServConfigDB();  
	            }
			} else if(callNoSize <= 0  && !isServiceRunning){
		    	Log.i(TAG, "Start service");
		    	
	        	Intent i = new Intent(Intent.ACTION_RUN);
	            i.setClass(thisAct.getApplicationContext(), smsRteService.class);
	            thisAct.getApplicationContext().startService(i);    
	            if(isServiceStarted(thisAct)){
	       	      	mToggleServPreference.setImageButtonOn();
	            	createServConfigDB();  
	            }
			}
	    	Log.i(TAG, "onTouch done");

	    }     
	   return false;     
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
			if(runningService.get(i).service.getClassName().toString().equals("com.marco.smsrouter.service.smsRteService"))
			{
		    	Log.i(TAG, "Service is running");
				return true;
			}
		}
    	Log.i(TAG, "Service is NOT running");
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_sms_rte, menu);
		return true;
	}

    // 根据文件是否存在判断是否默认启动service
	public static boolean checkServConfigDB(){
    	String filePath = dbPath + dbName;
		
		File dbdir = new File(dbPath); 
        if (!dbdir.exists()) { 
            return false;
        } 
		File pwdbfile = new File(filePath); 
        if (!pwdbfile.exists()) { 
        	return false;
        }
        
    	Log.i(TAG, "dbfile " + filePath + " does exist");
        return true;
    }
    
    // 创建db文件
    public static void createServConfigDB(){
    	String filePath = dbPath + dbName;
		
		File dbdir = new File(dbPath); 
        if (!dbdir.exists()) { 
            try { 
            	  Log.i(TAG, "create db dir " + dbPath);
                //在指定的文件夹中创建文件 
          	  boolean result = dbdir.mkdirs(); 
            	  Log.i(TAG, "create db dir returns " + result);
          } catch (Exception e) { 
  			e.printStackTrace();
          } 
        } 
		File pwdbfile = new File(filePath); 
        if (!pwdbfile.exists()) { 
      	    try { 
      		    //在指定的文件夹中创建文件 
      		    pwdbfile.createNewFile(); 
          	    Log.i(TAG, "Finish create DB " + filePath);
      	    } catch (Exception e) { 
      		    e.printStackTrace();
      	    } 
        }
    }
    
    // 删除db文件
    public void delServConfigDB(){
    	String filePath = dbPath + dbName;
		
		File dbdir = new File(dbPath); 
        if (!dbdir.exists()) { 
            return;
        } 
		File pwdbfile = new File(filePath); 
        if (!pwdbfile.exists()) { 
      	    return;
        }
        
        dbAccessor dbQuery = new dbAccessor(this, dbPath+dbName);
        dbQuery.delAllForwardNo();

  	    //Log.i(TAG, "delete pwdbfile" + filePath);
        //pwdbfile.delete();
    }

    //消息通知Handler联系人数组已更新
    static void serviceStartOk(String callNo){
    	if(SmsRteActivity.resultHandler == null){
            Log.i(TAG,"resultHandler is null");
        	return;
    	}
        Log.i(TAG,"send EVENT_SERVICE_START_DONE");
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("callno", callNo);
        msg.setData(bundle);
        msg.what = SmsRteActivity.EVENT_SERVICE_START_DONE;
        SmsRteActivity.resultHandler.removeMessages(SmsRteActivity.EVENT_SERVICE_START_DONE);
        SmsRteActivity.resultHandler.sendMessage(msg);
    }

	public static dataAccessor getmAccessor() {
		return mAccessor;
	}

	public static void setmAccessor(dataAccessor mAccessor) {
		SmsRteActivity.mAccessor = mAccessor;
	}

	//处理子线程消息在UI线程更新Listview
	class serviceResultHandler extends Handler {
		public serviceResultHandler() {
			super();
		}
		
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			switch (msg.what) {			
			case EVENT_SERVICE_START_DONE:
		        Log.i(TAG,"recv EVENT_SERVICE_START_DONE");
	            if(isServiceStarted(thisAct)){
	            	mToggleServPreference.setImageButtonOn();
	            	mToggleServPreference.updateServiceDescWithCallNo(msg.getData().getString("callno"));
     				createServConfigDB();  
	            }
                break;
			case EVENT_LOAD_RTEHISTORY:
		        Log.i(TAG,"recv EVENT_LOAD_RTEHISTORY");
				dbAccessor dbQuery = new dbAccessor(thisAct, dbPath+dbName);
				if(dbQuery != null){
					mRteHistory = dbQuery.getForwardHistory();
			        Message msg1 = new Message();
			        msg1.what = smsRteHistoryActivity.EVENT_LOAD_RTEHISTORY_DONE;
			        smsRteHistoryActivity.historyHandler.removeMessages(smsRteHistoryActivity.EVENT_LOAD_RTEHISTORY_DONE);
			        smsRteHistoryActivity.historyHandler.sendMessage(msg1);
				}
                break;
			case EVENT_FLOW_CONTROL_CHG:
		        Log.i(TAG,"recv EVENT_FLOW_CONTROL_CHG");
		        initFlowConfigPreference();
                break;
			}
	        Log.i(TAG,"handle message done");
		}
	}
}
