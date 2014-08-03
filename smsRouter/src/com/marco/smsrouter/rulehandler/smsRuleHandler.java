package com.marco.smsrouter.rulehandler;

import java.util.ArrayList;
import java.util.HashMap;

import com.marco.smsrouter.smsCreater;
import com.marco.smsrouter.dataaccessor.dataAccessor;
import com.marco.smsrouter.service.smsForwardProcesser;
import com.marco.smsrouter.service.smsForwardProcesser.smsFormat;

public class smsRuleHandler {
	private ArrayList<Object> ruleList = new ArrayList<Object>();

	@SuppressWarnings("unused")
	public void configureRuleList(dataAccessor accessor) {
		ruleList = new ArrayList<Object>();
		ruleList.add(smsCreater.createRule(smsCreater.BLOCK_NUMBER_RULE, accessor));
		ruleList.add(smsCreater.createRule(smsCreater.BLOCK_CONTENT_RULE, accessor));
		return;
	}
	
	public boolean handleRules(smsForwardProcesser.smsFormat sms) {
		// loop over all rules and check if sms should be blocked
		for(int i=0; i<ruleList.size(); i++) {
			if (((smsRule)ruleList.get(i)).checkRule(sms) == false)
				return false;
		}
		return true;
	}
}
