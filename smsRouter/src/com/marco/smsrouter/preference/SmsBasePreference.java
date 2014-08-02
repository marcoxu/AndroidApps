package com.marco.smsrouter.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public abstract class SmsBasePreference  extends Preference {
	private Context mContext;

	public SmsBasePreference(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    mContext = context;
	}

	public SmsBasePreference(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    mContext = context;
	}
	  
	public abstract RelativeLayout doCreateView();
	
    protected abstract View onCreateView(ViewGroup parent);
}
