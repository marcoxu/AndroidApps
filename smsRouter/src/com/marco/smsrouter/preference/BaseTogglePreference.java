package com.marco.smsrouter.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import com.marco.smsrouter.R;

public abstract class BaseTogglePreference extends BasePopConfirmDialogPreference {
	private static final String TAG = "smsRouter.TogglePreference";
	private ImageButton mToggleButton = null;
	private int mToggleState = 0;
	private View.OnClickListener mToggleListener = null;
	private int mOnBtnBackground = R.drawable.on_btn;
	private int mOffBtnBackground = R.drawable.off_btn;
	private int mToggleButtonId = 0;
	private int mToggleLayoutId = 0;
	private View mToggleViiew = null;

	public BaseTogglePreference(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}

	public BaseTogglePreference(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}
	  
    public void setImageButtonOn(){
    	mToggleState = 1;
        if(mToggleButton != null){
            Context context = getContext();
        	mToggleButton.setImageDrawable(context.getResources().getDrawable(mOnBtnBackground));
		}
	}
	  
	public void setImageButtonOff(){
	  Log.i(TAG, "setImageButtonOff");
	  mToggleState = 0;
	  if(mToggleButton != null){
          Context context = getContext();
		  mToggleButton.setImageDrawable(context.getResources().getDrawable(mOffBtnBackground));
	  }
	}
	
	public void setToggleImageButton(ImageButton btn){
		  Log.i(TAG, "setToggleImageButton");
		  mToggleButton = btn;
	}

	public ImageButton getToggleImageButton(){
		  Log.i(TAG, "getToggleImageButton");
		  return mToggleButton;
	}

	public void setToggleImageButtonOnDrwableId(int onBtnId){
		  Log.i(TAG, "setToggleImageButtonOnDrwableId");
		  mOnBtnBackground = onBtnId;
	}

	public void setToggleImageButtonOffDrwableId(int offBtnId){
		  Log.i(TAG, "setToggleImageButtonOffDrwableId");
		  mOffBtnBackground = offBtnId;
	}

	public void setToggleResourceLayout(int layout){
		  Log.i(TAG, "setToggleResourceLayout");
		  mToggleLayoutId = layout;
	}

	public void setToggleButtonId(int id){
		  Log.i(TAG, "setToggleButtonId");
		  mToggleButtonId = id;
	}

	public boolean isToggleOn(){
		  Log.i(TAG, "isToggleOn");
		  return (mToggleState == 1 && mToggleButton != null);
	}

	public View getToggleView(){
		  Log.i(TAG, "getToggleView");
		  return mToggleViiew;
	}

	@Override    
    protected View onCreateView(ViewGroup parent) {
	   Log.i(TAG, "onCreateView start");

       Context context = getContext();
       RelativeLayout frame = null;
	   LayoutInflater layout = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
	   if(mToggleLayoutId != 0) {
		   mToggleViiew = layout.inflate(mToggleLayoutId, null);
		   
		   if(mToggleViiew != null) {
			   frame = (RelativeLayout) mToggleViiew.findViewById(R.id.frame);
			   mToggleButton = (ImageButton) mToggleViiew.findViewById(mToggleButtonId);
			   
			   if(mToggleButton != null && mToggleListener != null)
				   mToggleButton.setOnClickListener(mToggleListener);
				   
			   if(mToggleState == 0 && mToggleButton != null) {
				  setImageButtonOff();
			   }
			   else if(mToggleState == 1 && mToggleButton != null){
				  setImageButtonOn();
			   }
		   }
	   }
		   
	   Log.i(TAG, "onCreateView done");
	   return frame;
    }

	public void setToggleClickListener(View.OnClickListener mOkListener) {
		mToggleListener = mOkListener;
	}
}
