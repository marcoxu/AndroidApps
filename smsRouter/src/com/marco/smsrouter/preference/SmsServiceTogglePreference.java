package com.marco.smsrouter.preference;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marco.smsrouter.R;
import com.marco.smsrouter.dbAccessor;
import com.marco.smsrouter.service.smsReceiver;

public class SmsServiceTogglePreference extends BaseTogglePreference {
	private static final String TAG = "smsRouter.SmsServiceTogglePreference";
	private TextView mServiceDesc = null;
	private static String mServiceDefaultDesc = "启动后自动转发到";
	private static String mServiceOnDesc = "指定号码";

	public SmsServiceTogglePreference(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}

	public SmsServiceTogglePreference(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}
	  
	public void updateServiceDescWithCallNo(String CallNo){
	  Log.i(TAG, "updateServiceDescWithCallNo");
	  if(mServiceDesc != null) {
		  if(CallNo != null)
			  mServiceDesc.setText(mServiceDefaultDesc + CallNo);
		  else
			  mServiceDesc.setText(mServiceDefaultDesc + mServiceOnDesc);
	  }
	}

	@Override    
    protected View onCreateView(ViewGroup parent) {
	   Log.i(TAG, "onCreateView start");
	   RelativeLayout frame = (RelativeLayout) super.onCreateView(parent);
       Context context = getContext();
	   View viewGroup = getToggleView();
	   mServiceDesc = (TextView) viewGroup.findViewById(R.id.service_desc); 
	   dbAccessor dbQuery = new dbAccessor(context, smsReceiver.dbPath+smsReceiver.dbName);
		   
	   if(isToggleOn()){
		  ArrayList<HashMap<String, Object>> callNo = dbQuery.getForwardNo();
		  if(callNo != null && callNo.size() > 0)
		  updateServiceDescWithCallNo(callNo.get(0).get("number").toString());
	   }
		   
	   Log.i(TAG, "onCreateView done");
	   return frame;
    }
}
