package com.marco.smsrouter.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import com.marco.smsrouter.smsCreater;
import com.marco.smsrouter.dataaccessor.dataAccessor;
import com.marco.smsrouter.flowctlhandler.smsFlowCtlHandler;
import com.marco.smsrouter.rulehandler.smsRuleHandler;

import android.content.Context;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class smsForwardProcesser {
	private static final String TAG = "smsRouter.smsProcesser";
	private String mForwardNumber = null;
	private static int LONG_SMS_LENGTH = 100;

	public class smsFormat {
		private ArrayList<String> smsMultiParts;
		private String destName;
		private String destNo;
		private String srcContactName;
		private String srcContactNo;
		private String date;
		public smsFormat() {			
		}
		public String getSrcContactName() {
			return srcContactName;
		}
		public void setSrcContactName(String srcContactName) {
			this.srcContactName = srcContactName;
		}
		public String getSrcContactNo() {
			return srcContactNo;
		}
		public void setSrcContactNo(String srcContactNo) {
			this.srcContactNo = srcContactNo;
		}
		public String getDestName() {
			return destName;
		}
		public void setDestName(String destName) {
			this.destName = destName;
		}
		public String getDestNo() {
			return destNo;
		}
		public void setDestNo(String destNo) {
			this.destNo = destNo;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public ArrayList<String> getSmsMultiParts() {
			return smsMultiParts;
		}
		public void setSmsMultiParts(ArrayList<String> smsMultiParts) {
			this.smsMultiParts = smsMultiParts;
		}
	}
	
	private dataAccessor mAccessor        = null;
	private smsRuleHandler mRuleHdl       = null;
	private smsFlowCtlHandler mFlowctlHdl = null;
	
	public smsFormat createSmsFormat() {
		return new smsFormat();
	}
	
	public void setMobileNo(String callno) {
		Log.i(TAG, "setMobileNo mobile_number is " + callno);
		if(mAccessor != null && callno != null && callno.length() > 0){
			String date = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINESE) .format(Calendar.getInstance().getTime()); 
			Log.i(TAG, "setMobileNo date " + date);
			if(mAccessor.insertForwardNo(callno, date, 1) == 0)
				mForwardNumber = callno;
		}
		Log.i(TAG, "setMobileNo done ");
	}

	public void setDefaultMobileNo() {
		if(mAccessor != null){
			ArrayList<HashMap<String, Object>> callNo = mAccessor.getForwardNo();
	        Log.i(TAG,"callNo size " + callNo.size());
			if(callNo != null && callNo.size() > 0)
				mForwardNumber = callNo.get(0).get("number").toString();
		}
		Log.i(TAG, "setDefaultMobileNo done " + mForwardNumber);
	}

	public void updateRulesControl(){
    	// 1. configure the sms rules
		if(mRuleHdl == null)
			mRuleHdl = new smsRuleHandler();
		mRuleHdl.configureRuleList(mAccessor);
		
		// 2. configure flow controls
		if(mFlowctlHdl == null)
			mFlowctlHdl = new smsFlowCtlHandler();
		mFlowctlHdl.configureFlowCtlList(mAccessor);
	}
	
	public smsForwardProcesser(Context cont) {
		mAccessor = smsCreater.createDataAccessor(smsCreater.SQLITE_DB_TYPE, cont);
		HashMap<String, Object> smsRules = new HashMap<String, Object>(); //mAccessor
		updateRulesControl();
	}

	public void handleSmsForward(SmsMessage[] sms, int length) {
		smsForwardProcesser.smsFormat smsFormat = new smsFormat();
		smsFormat.setSmsMultiParts(new ArrayList<String>());
		smsFormat.setDestNo(mForwardNumber);
        int count = 0;
        
		for (SmsMessage message : sms) {  
			String content = message.getMessageBody();// 得到短信内容  
            Log.i(TAG, "SMS received: " + content);           			
            smsFormat.setSrcContactNo(message.getOriginatingAddress());// 得到发信息的号码  
            Log.i(TAG, "SMS received from: " + smsFormat.getSrcContactNo()); 
            
            smsFormat.setSrcContactName(mAccessor.getContactName(smsFormat.getSrcContactNo()));
            
            if(count == 0)
            	smsFormat.getSmsMultiParts().add("From [" + smsFormat.getSrcContactName() + "]:");
            
            count ++;
            smsFormat.getSmsMultiParts().add(content);

        	Log.i(TAG, "SMS received info: " + content);
		}
		
		handleSmsForward(smsFormat);
	}
	
	public void handleSmsForward(smsFormat sms) {
    	Log.i(TAG, "SMS forward info: " + sms.getDestNo() + sms.getSmsMultiParts().toString());

    	// 2. check the rules and determine if the sms can be forwarded
		if(mRuleHdl.handleRules(sms) == false) {
			// return false and do not forward the sms
        	Log.i(TAG, "SMS is not forwarded due to Rules");
			return;
		}
		
    	// 3. check the flow control and determine if the sms can be forwarded
		if(mFlowctlHdl.handleFlowCtl(sms) == false) {
			// return false and do not forward the sms
        	Log.i(TAG, "SMS is not forwarded due to Flow Control");
			return;
		}

		// 4. forward the sms
		if(sms.getSmsMultiParts().toString().length() > LONG_SMS_LENGTH){
			SmsManager.getDefault().sendMultipartTextMessage(sms.getDestNo(), null, sms.getSmsMultiParts(), null, null);
			updateForwardSmsCount(sms.getSmsMultiParts().size());
		} else {
			String shortSms = null;
			for(int i=0;i<sms.getSmsMultiParts().size();i++){
				if(shortSms != null)
					shortSms += sms.getSmsMultiParts().get(i).toString();
				else
					shortSms = sms.getSmsMultiParts().get(i).toString();
			}
        	Log.i(TAG, "SMS short info: " + shortSms);
			SmsManager.getDefault().sendTextMessage(sms.getDestNo(), null, shortSms, null, null);
		}
		sms.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE) .format(Calendar.getInstance().getTime())); 
		mAccessor.insertForwardHistory(sms);
		updateForwardSmsCount(1);
	}
	
	private void updateForwardSmsCount(int count) {
		int type = mFlowctlHdl.getCurrentFlowControlType();
		if(smsCreater.isTypeValie(type)) {
    		Calendar date = Calendar.getInstance();
            int day = date.get(Calendar.DAY_OF_MONTH);
            int month = date.get(Calendar.MONTH);
            int year = date.get(Calendar.YEAR);
            
            for(int i=0;i<count;i++) {
            	mAccessor.increaseFlowCtlCurrent(type, Integer.toString(year), Integer.toString(month), Integer.toString(day));
            }
		}
	}
}
