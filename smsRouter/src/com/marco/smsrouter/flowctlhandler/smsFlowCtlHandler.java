package com.marco.smsrouter.flowctlhandler;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

import com.marco.smsrouter.smsCreater;
import com.marco.smsrouter.dataaccessor.dataAccessor;
import com.marco.smsrouter.dataaccessor.smsRouterDB;
import com.marco.smsrouter.rulehandler.smsRule;
import com.marco.smsrouter.service.smsForwardProcesser;

public class smsFlowCtlHandler {
	private static final String TAG = "smsRouter.smsFlowCtlHandler";
	private ArrayList<Object> flowCtlList = new ArrayList<Object>();

	@SuppressWarnings("unused")
	public void configureFlowCtlList(dataAccessor accessor) {
		ArrayList<HashMap<String, Object>> flowCtlRecords = accessor.getFlowCtlRecord();
		
    	Log.i(TAG, "Do configureFlowCtlList");
		flowCtlList = new ArrayList<Object>();
		if(flowCtlRecords != null){
	    	Log.i(TAG, "Curernt flow control " + flowCtlRecords.size());
			for(int i=0;i<flowCtlRecords.size();i++){
				int type = (Integer) flowCtlRecords.get(i).get(smsRouterDB.SMS_FLOWCTL_TYPE);
				flowCtlList.add(smsCreater.createFlowCtl(type, flowCtlRecords.get(i), accessor));
			}
		}
		return;
	}
	
	public boolean handleFlowCtl(smsForwardProcesser.smsFormat sms) {
		// loop over all rules and check if sms should be blocked
		for(int i=0; i<flowCtlList.size(); i++) {
			if (((smsFlowctl)flowCtlList.get(i)).checkFlowctl() == false)
				return false;
		}
		return true;
	}
	
	public int getCurrentFlowControlType() {
		if(flowCtlList != null && flowCtlList.size() > 0) {
			return ((smsFlowctl)flowCtlList.get(0)).getType();
		}
		return -1;
	}
}
