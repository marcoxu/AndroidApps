package com.marco.smsrouter.flowctlhandler;

import java.util.Calendar;
import java.util.HashMap;

import android.util.Log;

import com.marco.smsrouter.smsCreater;
import com.marco.smsrouter.dataaccessor.dataAccessor;
import com.marco.smsrouter.dataaccessor.smsRouterDB;

public class smsFlowCtlBlockByMonth  implements smsFlowctl {
	private static final String TAG = "smsRouter.smsFlowCtlBlockByMonth";
	private dataAccessor mAccessor = null;
	private int threshold = -1;
	
	public int getType() {
		return smsCreater.BLOCK_FLOW_CONTROL_BY_MONTH;
	}

	public boolean checkFlowctl() {
		// 1. get current counter
		Calendar date = Calendar.getInstance();
        int month = date.get(Calendar.MONTH);
		int current = mAccessor.getFlowCtlCurrent(smsCreater.BLOCK_FLOW_CONTROL_BY_MONTH, "0", Integer.toString(month), "0");
        Log.i(TAG,"checkFlowctl:" + current + ":" + threshold);

        if(current < threshold) {
			return true;
		} else {
			return false;
		}
	}

	public smsFlowCtlBlockByMonth(HashMap<String, Object> record, dataAccessor accessor){
		mAccessor = accessor;
		threshold = (Integer) record.get(smsRouterDB.SMS_FLOWCTL_THRESHOLD);
        Log.i(TAG,"Create smsFlowCtlBlockByMonth:" + threshold);
	}
}
