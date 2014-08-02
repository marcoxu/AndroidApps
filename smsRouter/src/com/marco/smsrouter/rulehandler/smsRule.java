package com.marco.smsrouter.rulehandler;

import java.util.HashMap;

import com.marco.smsrouter.service.smsForwardProcesser;
import com.marco.smsrouter.service.smsForwardProcesser.smsFormat;

public interface smsRule {
	boolean checkRule(smsForwardProcesser.smsFormat sms);
}