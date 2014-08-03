package com.marco.smsrouter.preference;

import com.marco.smsrouter.R;
import com.marco.smsrouter.R.id;
import com.marco.smsrouter.R.layout;
import com.marco.smsrouter.R.string;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TextDisplayPreference extends Preference  {
	private static final String TAG = "smsRouter.TextDisplayPreference";
	private TextView mServiceDesc = null;
	private TextView mServiceName = null;
	private static String FLOW_CONTROL_NAME   = "转发流量监控";
	private static String SERVICE_DESC_IF_OFF = "流量监控未启动,全部转发";
	private static String SERVICE_DESC_IF_ON  = "流量监控已启动";

	public TextDisplayPreference(Context context, AttributeSet attrs) {
		    super(context, attrs);
	  }

	  public TextDisplayPreference(Context context, AttributeSet attrs, int defStyle) {
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
	  
	  public void setTextDesc(String desc) {
		  if(mServiceDesc != null)
			  mServiceDesc.setText(desc);
	  }
	
	  @Override    
      protected View onCreateView(ViewGroup parent) {
		   Log.i(TAG, "onCreateView start");

	       Context context = getContext();
		   LayoutInflater layout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		   View viewGroup = layout.inflate(R.layout.text_preference, null);
		   RelativeLayout frame = (RelativeLayout) viewGroup.findViewById(R.id.frame);
		   mServiceDesc = (TextView) viewGroup.findViewById(R.id.current_count_desc);
		   mServiceName = (TextView) viewGroup.findViewById(R.id.current_count_name);
		   mServiceName.setText(context.getString(R.string.flow_control_disp_count_key));
		   
		   return frame;
      }
}
