package com.marco.smsrouter;

import java.util.ArrayList;
import java.util.HashMap;

import com.marco.smsrouter.FlowControlActivity.flowControlHandler;
import com.marco.smsrouter.dataaccessor.dataAccessor;
import com.marco.smsrouter.preference.BaseTextDisplayPreference;
import com.marco.smsrouter.preference.SmsFlowControlTogglePreference;
import com.marco.smsrouter.service.smsRteService;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.util.Log;
import android.view.View;

public class ForwardRuleActivity extends PreferenceActivity {
	private static final String TAG                                = "smsRouter.SmsRteActivity";
	private SmsFlowControlTogglePreference mToggleServPreference   = null;
	private BaseTextDisplayPreference mCurrentCountPreference      = null;
	private BaseTextDisplayPreference mConfFlowCtlPreference       = null;
	private PreferenceGroup mBase                                  = null;
	private ForwardRuleActivity thisAct                            = null;
	private dataAccessor mAccessor                                 = null;
	private static final int EVENT_FLOW_CONTROL_UPDATE             = 0;
	private static final int EVENT_FLOW_CONTROL_DISPLAY            = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
  	  addPreferencesFromResource(R.xml.forward_rule_config_preference);    
  	
  	  thisAct = this;
  	  mAccessor = SmsRteActivity.getmAccessor();
  	  mBase = ((PreferenceGroup) findPreference(getString(R.string.config_title))); 
  	  mToggleServPreference = 
              (SmsFlowControlTogglePreference) getPreferenceScreen().findPreference(getString(R.string.config_forward_rule_key)); //取得我们自定义的preference
  	  mCurrentCountPreference = 
              (BaseTextDisplayPreference) getPreferenceScreen().findPreference(getString(R.string.forward_rule_whitelist_key));
  	  mConfFlowCtlPreference =
            (BaseTextDisplayPreference) getPreferenceScreen().findPreference(getString(R.string.forward_rule_blacklist_key));

  	  setDefaultPreferenceConfig();
  	
  	  View.OnClickListener listener = new View.OnClickListener() {
          @Override
          public void onClick(View v) {
        	Log.i(TAG, "forwardRuleListener onClick");
	    	if(mToggleServPreference.isToggleOn()){
	        	Log.i(TAG, "forwardRuleListener off");
	        	toggleOffForwardRule();
	    	} else {
	        	Log.i(TAG, "forwardRuleListener on");
	        	toggleOnForwardRule();
	    	}
          }
      };

      if(mToggleServPreference != null) {
		  mToggleServPreference.setToggleButtonId(R.id.switch_service);
		  mToggleServPreference.setToggleResourceLayout(R.layout.toggle_preference);
		  mToggleServPreference.setDialogDisabled();
		  mToggleServPreference.setToggleClickListener(listener);
		  mToggleServPreference.setSummaryText(getString(R.string.config_forward_rule_off_summary));
		  mToggleServPreference.setTitleText(getString(R.string.config_forward_rule_title));		  
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
 	      mToggleServPreference.setImageButtonOn();
		  mToggleServPreference.setSummaryText(getString(R.string.config_forward_rule_on_summary));
      } else {
    	  Log.i(TAG, "getFlowCtlRecord returns null or 0");
 	      mToggleServPreference.setImageButtonOff();
		  mToggleServPreference.setSummaryText(getString(R.string.config_forward_rule_off_summary));
 	      mBase.removePreference(mCurrentCountPreference);
 	      mBase.removePreference(mConfFlowCtlPreference);
      }

	}
	
	private void setDefaultPreferenceConfig() {
	      if(mConfFlowCtlPreference != null) {
	    	  mConfFlowCtlPreference.setTextLayoutResource(R.layout.text_preference);
	    	  mConfFlowCtlPreference.setTextDescId(R.id.current_count_desc);
	    	  mConfFlowCtlPreference.setTextNameId(R.id.current_count_name);
	    	  mConfFlowCtlPreference.setTextNameStringId(R.string.forward_rule_blacklist_key);
	    	  mConfFlowCtlPreference.setTextDescStringId(R.string.forward_rule_blacklist_summary);
	      }

	      if(mCurrentCountPreference != null) {
	    	  mCurrentCountPreference.setTextLayoutResource(R.layout.text_preference);
	    	  mCurrentCountPreference.setTextDescId(R.id.current_count_desc);
	    	  mCurrentCountPreference.setTextNameId(R.id.current_count_name);
	    	  mCurrentCountPreference.setTextNameStringId(R.string.forward_rule_whitelist_key);
	    	  mCurrentCountPreference.setTextDescStringId(R.string.forward_rule_whitelist_summary);
	      }
	}
	
	private void toggleOffForwardRule() {
		setDefaultPreferenceConfig();
		notifyFlowControlUpdate();
		//mAccessor.delAllFlowCtlCurrent();
		//mAccessor.delAllFlowCtlRecord();
		mToggleServPreference.setSummaryText(getString(R.string.config_forward_rule_off_summary));
		mToggleServPreference.setImageButtonOff();
		mBase.removePreference(mCurrentCountPreference);
		mBase.removePreference(mConfFlowCtlPreference);
	}
	
	private void toggleOnForwardRule() {
		mToggleServPreference.setSummaryText(getString(R.string.config_forward_rule_on_summary));
		mToggleServPreference.setImageButtonOn();
		mBase.addPreference(mCurrentCountPreference);
		mBase.addPreference(mConfFlowCtlPreference);
	}

    public void notifyFlowControlUpdate(){
        // Notify Service
        notifyServiceAboutFlowCtlUpdate();
    }
    
    public void notifyServiceAboutFlowCtlUpdate()
    {            
         Intent intent = new Intent();
         intent.setAction("weibo4andriod.focusme.weiboService");
         intent.putExtra("cmd", smsRteService.CMD_UPDATE_RULE);
         sendBroadcast(intent);
    }
}
