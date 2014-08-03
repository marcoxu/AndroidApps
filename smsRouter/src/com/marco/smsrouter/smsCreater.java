package com.marco.smsrouter;

import java.util.HashMap;

import com.marco.smsrouter.dataaccessor.dataAccessor;
import com.marco.smsrouter.dataaccessor.sqliteDataAccessor;
import com.marco.smsrouter.flowctlhandler.smsFlowCtlBlockByDay;
import com.marco.smsrouter.flowctlhandler.smsFlowCtlBlockByMonth;
import com.marco.smsrouter.flowctlhandler.smsFlowCtlBlockByYear;
import com.marco.smsrouter.flowctlhandler.smsFlowctl;
import com.marco.smsrouter.rulehandler.smsRule;
import com.marco.smsrouter.rulehandler.smsRuleBlockContent;
import com.marco.smsrouter.rulehandler.smsRuleBlockNumber;

import android.content.Context;

public class smsCreater {
	public static final int BLOCK_NUMBER_RULE  = 0;
	public static final int BLOCK_CONTENT_RULE = 1;
	public static final int SQLITE_DB_TYPE     = 2;
	public static final int BLOCK_FLOW_CONTROL_BY_YEAR  = 0;
	public static final int BLOCK_FLOW_CONTROL_BY_MONTH = 1;
	public static final int BLOCK_FLOW_CONTROL_BY_DAY   = 2;
	
	public static boolean isTypeValie(int type) {
		switch(type) {
		case BLOCK_FLOW_CONTROL_BY_YEAR:
		case BLOCK_FLOW_CONTROL_BY_MONTH:
		case BLOCK_FLOW_CONTROL_BY_DAY:
			return true;
		default:
			return false;
		}
	}
	
    public static smsRule createRule(int type, dataAccessor accessor) {
    	smsRule rule = null;
    	
    	switch(type) {
    	case BLOCK_NUMBER_RULE:
    		rule = new smsRuleBlockNumber(accessor);
    		break;
    	case BLOCK_CONTENT_RULE:
    		rule = new smsRuleBlockContent(accessor);
    		break;
    	}
		return rule;
    }
    	
    public static smsFlowctl createFlowCtl(int type, HashMap<String, Object> record, dataAccessor accessor) {
    	smsFlowctl flowctl = null;
    	
    	switch(type) {
    	case BLOCK_FLOW_CONTROL_BY_YEAR:
    		flowctl = new smsFlowCtlBlockByYear(record, accessor);
    		break;
    	case BLOCK_FLOW_CONTROL_BY_MONTH:
    		flowctl = new smsFlowCtlBlockByMonth(record, accessor);
    		break;
    	case BLOCK_FLOW_CONTROL_BY_DAY:
    		flowctl = new smsFlowCtlBlockByDay(record, accessor);
    		break;
    	}
		return flowctl;
    }

    public static dataAccessor createDataAccessor(int type, Context cont) {
	    	dataAccessor accessor = null;
	    	
	    	switch(type) {
	    	case SQLITE_DB_TYPE:
	    		accessor = new sqliteDataAccessor(cont);
	    		break;
	    	}
			return accessor;
    }
}
