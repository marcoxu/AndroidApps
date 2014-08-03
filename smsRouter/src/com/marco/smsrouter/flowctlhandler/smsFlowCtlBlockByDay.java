package com.marco.smsrouter.flowctlhandler;

import java.util.Calendar;
import java.util.HashMap;

import com.marco.smsrouter.smsCreater;
import com.marco.smsrouter.dataaccessor.dataAccessor;
import com.marco.smsrouter.dataaccessor.smsRouterDB;

public class smsFlowCtlBlockByDay  implements smsFlowctl {
	private dataAccessor mAccessor = null;
	private int threshold = -1;
	
	public int getType() {
		return smsCreater.BLOCK_FLOW_CONTROL_BY_DAY;
	}
	
	public boolean checkFlowctl() {
		// TODO Auto-generated method stub
		// 1. get current counter
		Calendar date = Calendar.getInstance();
        int day = date.get(Calendar.DAY_OF_MONTH);
		int current = mAccessor.getFlowCtlCurrent(smsCreater.BLOCK_FLOW_CONTROL_BY_DAY, "0", "0", Integer.toString(day));
		
		if(current < threshold) {
			return true;
		} else {
			return false;
		}
	}

	public smsFlowCtlBlockByDay(HashMap<String, Object> record, dataAccessor accessor){
		mAccessor = accessor;
		threshold = (Integer) record.get(smsRouterDB.SMS_FLOWCTL_THRESHOLD);
	}

}
