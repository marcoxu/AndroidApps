package com.marco.smsrouter.flowctlhandler;

import java.util.Calendar;
import java.util.HashMap;

import com.marco.smsrouter.smsCreater;
import com.marco.smsrouter.dataaccessor.dataAccessor;
import com.marco.smsrouter.dataaccessor.smsRouterDB;
import com.marco.smsrouter.service.smsForwardProcesser;

public class smsFlowCtlBlockByYear implements smsFlowctl {
	private dataAccessor mAccessor = null;
	private int threshold = -1;
	
	public int getType() {
		return smsCreater.BLOCK_FLOW_CONTROL_BY_YEAR;
	}

	public boolean checkFlowctl() {
		Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
		int current = mAccessor.getFlowCtlCurrent(smsCreater.BLOCK_FLOW_CONTROL_BY_YEAR, Integer.toString(year), "0", "0");
		
		if(current < threshold) {
			return true;
		} else {
			return false;
		}
	}

	public smsFlowCtlBlockByYear(HashMap<String, Object> record, dataAccessor accessor){
		mAccessor = accessor;
		threshold = (Integer) record.get(smsRouterDB.SMS_FLOWCTL_THRESHOLD);
	}

}
