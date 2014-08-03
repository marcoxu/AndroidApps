package com.marco.smsrouter.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marco.smsrouter.R;

public class SmsFlowControlConfigPreference extends BaseTogglePreference {
	private static final String TAG                  = "smsRouter.SmsFlowControlConfigPreference";
	private TextView mServiceDesc                    = null;
	private TextView mServiceName                    = null;
	private static String FLOW_CONTROL_NAME          = "流量控制规则配置";
	private static String FLOW_CONTROL_DESC          = "未配置流量控制规则";

	public SmsFlowControlConfigPreference(Context context, AttributeSet attrs) {
		    super(context, attrs);
	  }

	  public SmsFlowControlConfigPreference(Context context, AttributeSet attrs, int defStyle) {
		    super(context, attrs, defStyle);
	  }
	  
	  @Override    
      protected View onCreateView(ViewGroup parent) {
		   Log.i(TAG, "onCreateView start");
		   
		   RelativeLayout frame = (RelativeLayout) super.onCreateView(parent);
		   View viewGroup = getToggleView();
		   if(viewGroup != null) {
			   mServiceDesc = (TextView) viewGroup.findViewById(R.id.service_desc); 
			   mServiceName = (TextView) viewGroup.findViewById(R.id.service_name);
			   
			   if(mServiceName != null)
				   mServiceName.setText(FLOW_CONTROL_NAME);
			   if(mServiceDesc != null)
				   mServiceDesc.setText(FLOW_CONTROL_DESC);
		   }
			   
		   Log.i(TAG, "onCreateView done");
		   return frame;
      }
}
