package com.marco.smsrouter.preference;



import com.marco.smsrouter.SmsRteActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;

public class DialogPreference extends android.preference.DialogPreference {
	private static final String TAG = "smsRouter.DialogPreference";
	  private int dialog_type;
	  public DialogPreference(Context context, AttributeSet attrs) {
		    super(context, attrs);
	  }

	  public DialogPreference(Context context, AttributeSet attrs, int defStyle) {
		    super(context, attrs, defStyle);
	  }
	  
	  public void setDialogType(int type){
		  dialog_type = type;
	  }
	  
	  @Override
	  public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			super.onClick(dialog, which);
	        Log.i(TAG,"DialogPreference: onClick " + dialog.toString() + ", " + which + ", " + dialog.BUTTON_NEGATIVE);
	        
	        if(which == dialog.BUTTON_POSITIVE && dialog_type == SmsRteActivity.DIALOG_TOGGLE){
		        Log.i(TAG,"DialogPreference: onClick " + which + ", Start/stop service");
	        }	        
	  }  
}

