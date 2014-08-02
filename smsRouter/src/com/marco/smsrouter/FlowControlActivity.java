package com.marco.smsrouter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.marco.smsrouter.dataaccessor.dataAccessor;
import com.marco.smsrouter.flowctlhandler.smsFlowCtlBlockByDay;
import com.marco.smsrouter.flowctlhandler.smsFlowCtlBlockByMonth;
import com.marco.smsrouter.flowctlhandler.smsFlowCtlBlockByYear;
import com.marco.smsrouter.preference.BaseTextDisplayPreference;
import com.marco.smsrouter.preference.SmsFlowControlTogglePreference;
import com.marco.smsrouter.preference.TextDisplayPreference;
import com.marco.smsrouter.service.smsRteService;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class FlowControlActivity extends PreferenceActivity implements OnPreferenceClickListener, OnPreferenceChangeListener {
	private static final String TAG                                = "smsRouter.SmsRteActivity";
	private SmsFlowControlTogglePreference mToggleServPreference   = null;
	private BaseTextDisplayPreference mCurrentCountPreference      = null;
	private BaseTextDisplayPreference mConfFlowCtlPreference       = null;
	private PreferenceGroup mBase                                  = null;
	private FlowControlActivity thisAct                            = null;
	private dataAccessor mAccessor                                 = null;
	private flowControlHandler mHandler                            = null;
	private static final int EVENT_FLOW_CONTROL_UPDATE             = 0;
	private static final int EVENT_FLOW_CONTROL_DISPLAY            = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
  	  addPreferencesFromResource(R.xml.flow_control_config_preference);    
  	
  	  thisAct = this;
  	  mHandler = new flowControlHandler();
	  //mAccessor = smsCreater.createDataAccessor(smsCreater.SQLITE_DB_TYPE, getBaseContext());
  	  mAccessor = SmsRteActivity.getmAccessor();
  	  mBase = ((PreferenceGroup) findPreference(getString(R.string.config_title))); 
  	  mToggleServPreference = 
              (SmsFlowControlTogglePreference) getPreferenceScreen().findPreference(getString(R.string.config_flow_control_key)); //取得我们自定义的preference
  	  mCurrentCountPreference = 
              (BaseTextDisplayPreference) getPreferenceScreen().findPreference(getString(R.string.flow_control_disp_count_key));
  	  mConfFlowCtlPreference =
            (BaseTextDisplayPreference) getPreferenceScreen().findPreference(getString(R.string.flow_control_config_key));

  	  setDefaultPreferenceConfig();

  	  View.OnClickListener listener = new View.OnClickListener() {
          @Override
          public void onClick(View v) {
        	Log.i(TAG, "configFlowCtlListener onClick");
	    	if(mToggleServPreference.isToggleOn()){
	        	Log.i(TAG, "configFlowCtlListener off");
	        	toggleOffFlowControl();
	    	} else {
	        	Log.i(TAG, "configFlowCtlListener on");
	        	toggleOnFlowControl();
	    	}
          }
      };
       
	  Log.i(TAG, "Start setmSwitchServListener");      
      if(mToggleServPreference != null) {
		  mToggleServPreference.setToggleButtonId(R.id.switch_service);
		  mToggleServPreference.setToggleResourceLayout(R.layout.toggle_preference);
		  mToggleServPreference.setDialogDisabled();
		  mToggleServPreference.setToggleClickListener(listener);
      }
	  else
	     return;


      ArrayList<HashMap<String, Object>> record = mAccessor.getFlowCtlRecord();
      int flowCtlType = -1;
      int flowctlmax = -1;
      if(record != null && record.size() > 0) {
    	  flowCtlType = (Integer) record.get(0).get("flowctltype");
    	  flowctlmax = (Integer) record.get(0).get("flowctlmax");
    	  Log.i(TAG, "getFlowCtlRecord " + flowCtlType + "," + flowctlmax);

    	  // send message to update the displayed flow control information
		  mToggleServPreference.setSummaryText(getString(R.string.flow_control_enabled));
    	  Message msg = new Message();
    	  Bundle data = new Bundle(); 
    	  data.putInt("flowCtlType", flowCtlType);         
    	  data.putInt("maxCount", flowctlmax);         
    	  msg.setData(data);
    	  msg.what = EVENT_FLOW_CONTROL_DISPLAY;
    	  mHandler.removeMessages(EVENT_FLOW_CONTROL_DISPLAY);
    	  mHandler.sendMessage(msg);
      } else {
    	  Log.i(TAG, "getFlowCtlRecord returns null or 0");
 	      mToggleServPreference.setImageButtonOff();
		  mToggleServPreference.setSummaryText(getString(R.string.flow_control_disabled));
 	      mBase.removePreference(mCurrentCountPreference);
 	      mBase.removePreference(mConfFlowCtlPreference);
      }
	}

	private void setDefaultPreferenceConfig() {
	      if(mConfFlowCtlPreference != null) {
	    	  mConfFlowCtlPreference.setTextLayoutResource(R.layout.text_preference);
	    	  mConfFlowCtlPreference.setTextDescId(R.id.current_count_desc);
	    	  mConfFlowCtlPreference.setTextNameId(R.id.current_count_name);
	    	  mConfFlowCtlPreference.setTextNameStringId(R.string.flow_control_config_name);
	    	  mConfFlowCtlPreference.setTextDescStringId(R.string.flow_control_config_desc);
	    	  mConfFlowCtlPreference.setOnPreferenceClickListener(thisAct); 
	    	  mConfFlowCtlPreference.setOnPreferenceChangeListener(thisAct);
	      }

	      if(mCurrentCountPreference != null) {
	    	  mCurrentCountPreference.setTextLayoutResource(R.layout.text_preference);
	    	  mCurrentCountPreference.setTextDescId(R.id.current_count_desc);
	    	  mCurrentCountPreference.setTextNameId(R.id.current_count_name);
	    	  mCurrentCountPreference.setTextNameStringId(R.string.flow_control_disp_count_key);
	    	  mCurrentCountPreference.setTextDescStringId(R.string.flow_control_disp_count_info);
	      }
	}

	private void toggleOffFlowControl() {
		setDefaultPreferenceConfig();
		notifyFlowControlUpdate();
		mAccessor.delAllFlowCtlCurrent();
		mAccessor.delAllFlowCtlRecord();
		mToggleServPreference.setSummaryText(getString(R.string.flow_control_disabled));
		mToggleServPreference.setImageButtonOff();
		mBase.removePreference(mCurrentCountPreference);
		mBase.removePreference(mConfFlowCtlPreference);
	}
	
	private void toggleOnFlowControl() {
		mToggleServPreference.setSummaryText(getString(R.string.flow_control_enabled));
		mToggleServPreference.setImageButtonOn();
    	Log.i(TAG, "configFlowCtlListener on");
		mBase.addPreference(mCurrentCountPreference);
		mBase.addPreference(mConfFlowCtlPreference);
		  if(mConfFlowCtlPreference != null) {
			  //mConfFlowCtlPreference.setmSwitchServListener(configFlowCtlListener);
			  mConfFlowCtlPreference.setOnPreferenceClickListener(thisAct);  
		  } else {
			  return;
		  }
	}

	public boolean onTouch(View v, MotionEvent event) {     
		if(event.getAction() == MotionEvent.ACTION_UP){
	    	Log.i(TAG, "onTouch start");
	    	if(mToggleServPreference.isToggleOn()){
	    		mToggleServPreference.setImageButtonOff();
	    	} else {
	    		mToggleServPreference.setImageButtonOn();
	    	}
	    	Log.i(TAG, "onTouch done");
       }     
	   return false;     
	  }     
	
	private void initFlowConfigLayout(View DialogView) {
		ArrayList<HashMap<String, Object>> record = mAccessor.getFlowCtlRecord();
		int flowCtlType = -1;
		int flowctlmax = -1;
		if(record != null && record.size() > 0) {
			flowCtlType = (Integer) record.get(0).get("flowctltype");
			flowctlmax = (Integer) record.get(0).get("flowctlmax");
		}
		
		Spinner intervalSpinner = (Spinner) DialogView.findViewById(R.id.spinner_category);
		EditText maxCount = (EditText) DialogView.findViewById(R.id.flow_control_threshod_config);
        ArrayList<String> category_list = new ArrayList<String>();
        
        category_list.add("年");
        category_list.add("月");
        category_list.add("日");
        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。     
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, category_list);     
        //第三步：为适配器设置下拉列表下拉时的菜单样式。     
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     
        //第四步：将适配器添加到下拉列表上 
        intervalSpinner.setAdapter(adapter);
        if(flowCtlType >= 0) {
        	intervalSpinner.setSelection(flowCtlType);
        }
        if(flowctlmax > 0) {
        	maxCount.setText(Integer.toString(flowctlmax));
        }
	}
	
    protected void showDialog() {
        LayoutInflater inflater_current = LayoutInflater.from(FlowControlActivity.this);   
		final View DialogView = inflater_current.inflate (R.layout.flow_control_rule_config, null);       
		
		initFlowConfigLayout(DialogView);
		new AlertDialog.Builder(FlowControlActivity.this)
		.setTitle("请输入转发规则")
		.setIcon(android.R.drawable.ic_dialog_info)  
		.setView(DialogView)
		.setPositiveButton("确定", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton)
            {
				EditText editText =(EditText) DialogView.findViewById(R.id.flow_control_threshod_config);
				Spinner intervalSpinner = (Spinner) DialogView.findViewById(R.id.spinner_category);
				Log.i(TAG,"category editText is " + editText.getText());
				Log.i(TAG,"interval type is " + intervalSpinner.getSelectedItemPosition());
             
				if(editText.getText().toString().trim().length() == 0)
					return;
        		  
				Message msg = new Message();
				Bundle data = new Bundle(); 
				data.putInt("flowCtlType", intervalSpinner.getSelectedItemPosition());         
				data.putInt("maxCount", Integer.parseInt(editText.getText().toString().trim()));         
				msg.setData(data);
				msg.what = EVENT_FLOW_CONTROL_UPDATE;
				mHandler.removeMessages(EVENT_FLOW_CONTROL_UPDATE);
				mHandler.sendMessage(msg);
            }
		}) 
		.setNegativeButton("取消", null)
		.show();  
    }

	@Override
	public boolean onPreferenceClick(Preference arg0) {
		// TODO Auto-generated method stub
  	    Log.i(TAG, "onPreferenceClick start");
  	    showDialog();
    	return false;
	}

	@Override
	public boolean onPreferenceChange(Preference arg0, Object arg1) {
		// TODO Auto-generated method stub
  	    Log.i(TAG, "onPreferenceChange start");
		return false;
	}

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,  
            Preference preference) {  
        // 对控件进行操作  
        return false;  
    }  

    private void updateFlowControlPreference(int type, int maxCount) {
        if(mConfFlowCtlPreference != null) {
        	switch(type) {
        	case smsCreater.BLOCK_FLOW_CONTROL_BY_YEAR:
            	mConfFlowCtlPreference.setTextDesc("每年最多转发" + maxCount + "条短信");
        		break;
        	case smsCreater.BLOCK_FLOW_CONTROL_BY_MONTH:
            	mConfFlowCtlPreference.setTextDesc("每月最多转发" + maxCount + "条短信");
        		break;
        	case smsCreater.BLOCK_FLOW_CONTROL_BY_DAY:
            	mConfFlowCtlPreference.setTextDesc("每日最多转发" + maxCount + "条短信");
        		break;
        	}
        }
        if(mCurrentCountPreference != null) {
    		Calendar date = Calendar.getInstance();
            int day = date.get(Calendar.DAY_OF_MONTH);
            int month = date.get(Calendar.MONTH);
            int year = date.get(Calendar.YEAR);
	        int currentCnt = mAccessor.getFlowCtlCurrent(type, Integer.toString(year), Integer.toString(month), Integer.toString(day));
    		mCurrentCountPreference.setTextDesc("目前已转发" + currentCnt + "条短信");
        }
    }
    
    public void notifyFlowControlUpdate(){
    	if(SmsRteActivity.resultHandler == null){
            Log.i(TAG,"resultHandler is null");
        	return;
    	}
        Log.i(TAG,"send EVENT_FLOW_CONTROL_CHG");
        Message msg = new Message();
        msg.what = SmsRteActivity.EVENT_FLOW_CONTROL_CHG;
        SmsRteActivity.resultHandler.removeMessages(SmsRteActivity.EVENT_FLOW_CONTROL_CHG);
        SmsRteActivity.resultHandler.sendMessage(msg);
        
        // Notify Service
        notifyServiceAboutFlowCtlUpdate();
    }

    public void notifyServiceAboutFlowCtlUpdate()
    {            
         Intent intent = new Intent();//创建Intent对象
         intent.setAction("weibo4andriod.focusme.weiboService");
         intent.putExtra("cmd", smsRteService.CMD_UPDATE_FLOW_CONTROL);
         sendBroadcast(intent);
    }


    class flowControlHandler extends Handler {
		public flowControlHandler() {
			super();
		}
		
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			Bundle data;
			int type = -1;
			int max = -1;
			
			switch (msg.what) {			
			case EVENT_FLOW_CONTROL_UPDATE:
		        Log.i(TAG,"recv EVENT_FLOW_CONTROL_UPDATE");
		        data = msg.getData(); 
		        type = data.getInt("flowCtlType");
		        max = data.getInt("maxCount");
		        mAccessor.insertFlowCtlRecord(type, max);
		        updateFlowControlPreference(type, max);
		        notifyFlowControlUpdate();
                break;
			case EVENT_FLOW_CONTROL_DISPLAY:
		        Log.i(TAG,"recv EVENT_FLOW_CONTROL_DISPLAY");
		        data = msg.getData(); 
		        type = data.getInt("flowCtlType");
		        max = data.getInt("maxCount");
		        updateFlowControlPreference(type, max);
		        mToggleServPreference.setImageButtonOn();
                break;
			}
	        Log.i(TAG,"handle message done");
		}
	}
}
