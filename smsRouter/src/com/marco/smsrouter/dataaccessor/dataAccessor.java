package com.marco.smsrouter.dataaccessor;

import java.util.ArrayList;
import java.util.HashMap;

import com.marco.smsrouter.service.smsForwardProcesser;
import com.marco.smsrouter.service.smsForwardProcesser.smsFormat;

import android.content.Context;
import android.database.Cursor;

public abstract class dataAccessor {
	public static final String TAG = "smsrouter.dataAccessor";
	public Context mContext = null;
	public String mfilePath = null;
	
	public abstract ArrayList<HashMap<String, Object>> getForwardHistory();

	public abstract ArrayList<HashMap<String, Object>> getForwardNo();
	
	public abstract int delAllForwardNo();

	public abstract int insertForwardNo(String number, String time, int type) ;
	
	public abstract int insertForwardHistory(smsForwardProcesser.smsFormat forwardhistory); 

	public abstract int delForwardHistory(smsForwardProcesser.smsFormat forwardhistory);
	
	public abstract String getContactName(String number);

	//获取指定号码对应的联系人记录
	public abstract Cursor queryContactByNumber(Context cont, String number);


	public abstract int insertFlowCtlRecord(int type, int max) ;
	public abstract ArrayList<HashMap<String, Object>> getFlowCtlRecord() ;
	public abstract int delAllFlowCtlRecord();
	public abstract int increaseFlowCtlCurrent(int type, String year, String month, String day) ;
	public abstract int delAllFlowCtlCurrent();
	public abstract int getFlowCtlCurrent(int type, String year, String month, String day) ;
}
