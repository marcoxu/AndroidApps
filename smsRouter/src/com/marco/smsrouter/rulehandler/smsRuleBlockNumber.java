package com.marco.smsrouter.rulehandler;

import java.util.HashMap;

import com.marco.smsrouter.dataaccessor.dataAccessor;
import com.marco.smsrouter.service.smsForwardProcesser;
import com.marco.smsrouter.service.smsForwardProcesser.smsFormat;

public class smsRuleBlockNumber implements smsRule {
	private dataAccessor mAccessor = null;

	public smsRuleBlockNumber (dataAccessor accessor){
		mAccessor = accessor;
	}

	@Override
	public boolean checkRule(smsForwardProcesser.smsFormat sms) {
		// TODO Auto-generated method stub
		return true;
	}

}
