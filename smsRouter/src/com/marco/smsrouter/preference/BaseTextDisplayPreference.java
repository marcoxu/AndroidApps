package com.marco.smsrouter.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.marco.smsrouter.R;

public class BaseTextDisplayPreference extends Preference {
	private static final String TAG = "smsRouter.BaseTextDisplayPreference";
	private TextView mTextDesc      = null;
	private TextView mTexteName     = null;
	private int mTextDescId         = 0;
	private int mTextNameId         = 0;
	private int mTextNameStringId   = 0;
	private int mTextNameDescId     = 0;
    private int mTextLayoutResId    = 0;
    private String mTextNameDesc    = null;

	public BaseTextDisplayPreference(Context context, AttributeSet attrs) {
		    super(context, attrs);
	  }

	  public BaseTextDisplayPreference(Context context, AttributeSet attrs, int defStyle) {
		    super(context, attrs, defStyle);
	  }
	  
	  public void setTextLayoutResource(int layoutResId) {
		  mTextLayoutResId = layoutResId;
	  }
    
	  public int getTogglePopLayoutResource() {
		  return mTextLayoutResId;
	  }
	    
	  public void setTextDescId(int id) {
		  mTextDescId = id;
	  }
    
	  public void setTextNameId(int id) {
		  mTextNameId = id;
	  }

	  public void setTextNameStringId(int id) {
		  mTextNameStringId = id;
	  }

	  public void setTextDescStringId(int id) {
		  mTextNameDescId = id;
	      Context context = getContext();
		  mTextNameDesc = context.getString(mTextNameDescId);
	  }

	  @SuppressWarnings("unused")
	  public void setTextDesc(String desc){
		  mTextNameDesc = desc;
		  if(mTextDesc != null){
			  mTextDesc.setText(mTextNameDesc);
		  }
	  }
	
	  @Override    
      protected View onCreateView(ViewGroup parent) {
		   Log.i(TAG, "onCreateView start");

	       Context context = getContext();
	       RelativeLayout frame = null;
		   LayoutInflater layout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		   View viewGroup = layout.inflate(mTextLayoutResId, null);
		   if(viewGroup != null){
			   frame = (RelativeLayout) viewGroup.findViewById(R.id.frame);
			   mTextDesc = (TextView) viewGroup.findViewById(mTextDescId);
			   mTexteName = (TextView) viewGroup.findViewById(mTextNameId);
			   if(mTexteName != null && mTextNameStringId != 0)
				   mTexteName.setText(context.getString(mTextNameStringId));
			   if(mTextDesc != null && mTextNameDesc.length() > 0)
				   mTextDesc.setText(mTextNameDesc);
		   }
		   
		   return frame;
      }

}
