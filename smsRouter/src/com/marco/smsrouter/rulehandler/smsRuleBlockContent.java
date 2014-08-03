package com.marco.smsrouter.rulehandler;

import java.util.HashMap;

import com.marco.smsrouter.dataaccessor.dataAccessor;
import com.marco.smsrouter.service.smsForwardProcesser;
import com.marco.smsrouter.service.smsForwardProcesser.smsFormat;

public class smsRuleBlockContent implements smsRule {
	private dataAccessor mAccessor = null;
	
	public smsRuleBlockContent (dataAccessor accessor){
		mAccessor = accessor;
	}

	@Override
	public boolean checkRule(smsForwardProcesser.smsFormat sms) {
		// TODO Auto-generated method stub
		return true;
	}

}
