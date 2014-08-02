package com.marco.smsrouter.flowctlhandler;

import com.marco.smsrouter.service.smsForwardProcesser;

public interface smsFlowctl {
	boolean checkFlowctl();
	int getType();
}
