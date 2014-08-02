package com.marco.smsrouter.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.marco.smsrouter.R;

public class SmsFlowControlTogglePreference extends BaseTogglePreference {
	private static final String TAG = "smsRouter.SmsFlowControlTogglePreference";
	private TextView mServiceDesc = null;
	private TextView mServiceName = null;
	private static String FLOW_CONTROL_NAME   = "转发流量监控";
	private static String SERVICE_DESC_IF_OFF = "流量监控未启动,全部转发";
	private static String SERVICE_DESC_IF_ON  = "流量监控已启动";
	private String mServiceNameText           = null;
	private String mServiceDescText           = null;

	public SmsFlowControlTogglePreference(Context context, AttributeSet attrs) {
		    super(context, attrs);
	  }

	  public SmsFlowControlTogglePreference(Context context, AttributeSet attrs, int defStyle) {
		    super(context, attrs, defStyle);
	  }
	  
	  private void updateServiceDescriptionIfOn(){
		  if(mServiceDesc != null){
			  mServiceDesc.setText(SERVICE_DESC_IF_ON);
		  }
	  }
	  
	  private void updateServiceDescriptionIfOff(){
		  if(mServiceDesc != null){
			  mServiceDesc.setText(SERVICE_DESC_IF_OFF);
		  }
	  }

	  public void setImageButtonOn(){
		  super.setImageButtonOn();
		  updateServiceDescriptionIfOn();
	  }
	  
	  public void setImageButtonOff(){
		  super.setImageButtonOff();
		  updateServiceDescriptionIfOff();
	  }

	  public void setSummaryText(String summary) {
		  mServiceDescText = summary;
		  if(mServiceDesc != null){
			  mServiceDesc.setText(mServiceDescText);
		  }
	  }
	  
	  public void setTitleText(String title) {
		  mServiceNameText = title;
		  if(mServiceName != null){
			  mServiceName.setText(mServiceNameText);
		  }
	  }

	  @Override    
      protected View onCreateView(ViewGroup parent) {
		   Log.i(TAG, "onCreateView start");
		   RelativeLayout frame = (RelativeLayout) super.onCreateView(parent);
		   View viewGroup = getToggleView();
		   if(viewGroup != null) {
			   mServiceDesc = (TextView) viewGroup.findViewById(R.id.service_desc); 
			   mServiceName = (TextView) viewGroup.findViewById(R.id.service_name);
			   if(mServiceName != null && mServiceNameText != null && mServiceNameText.length() > 0) {
				   mServiceName.setText(mServiceNameText);
			   } else if (mServiceName != null && (mServiceNameText == null || mServiceNameText.length() == 0)){
				   mServiceName.setText(FLOW_CONTROL_NAME);
			   }
			   
			   if(mServiceDesc != null && mServiceDescText != null && mServiceDescText.length() > 0)
				   mServiceDesc.setText(mServiceDescText);				   
		   }
		   Log.i(TAG, "onCreateView done");
		   return frame;
	  }
}
