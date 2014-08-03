package com.marco.smsrouter.preference;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceManager.OnActivityDestroyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

public abstract class BasePopConfirmDialogPreference extends Preference implements
    DialogInterface.OnClickListener, DialogInterface.OnDismissListener, OnActivityDestroyListener {
    private int mTogglePopLayoutResId;
    private String mDialogTitle;
    private String mDialogMessage;
    private Drawable mDialogIcon;
    private String mPositiveButtonText;
    private String mNegativeButtonText;
    private DialogInterface.OnClickListener mPositiveButtonListenr = null;
    private DialogInterface.OnClickListener mNegtiveButtonListenr = null;
    private boolean mDialogEnalbed = true;
	private static final String TAG = "smsRouter.SmsFlowControlConfigPreference";
    private AlertDialog.Builder mBuilder;
    private Dialog mDialog;
    private int mWhichButtonClicked;

	public BasePopConfirmDialogPreference(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}

	public BasePopConfirmDialogPreference(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}

	public void setDialogEnabled() {
		mDialogEnalbed = true;
	}

	public void setDialogDisabled() {
		mDialogEnalbed = false;
	}

	@Override
    protected void onClick() {
		Log.i(TAG, "BasePopConfirmDialogPreference onClick");
    	if(mDialogEnalbed)
    		showDialog();
    }

    public void setDialogTitle(String dialogTitle) {
        mDialogTitle = dialogTitle;
    }

    public void setDialogMessage(String dialogMessage) {
        mDialogMessage = dialogMessage;
    }

    public void setDialogIcon(Drawable dialogIcon) {
        mDialogIcon = dialogIcon;
    }

    public void setPositiveButtonText(String positiveButtonText) {
        mPositiveButtonText = positiveButtonText;
    }

    public void setNegativeButtonText(String negativeButtonText) {
        mNegativeButtonText = negativeButtonText;
    }

    public void setPositiveButtonListenr(DialogInterface.OnClickListener positiveButtonListenr) {
    	mPositiveButtonListenr = positiveButtonListenr;
    }

    public void setNegativeButtonListenr(DialogInterface.OnClickListener negativeButtonListener) {
    	mNegtiveButtonListenr = negativeButtonListener;
    }

    @Override
	public void onClick(DialogInterface arg0, int which) {
		// TODO Auto-generated method stub
        mWhichButtonClicked = which;
		Log.i(TAG, "BasePopConfirmDialogPreference onClick 2");
	}

    public void setTogglePopLayoutResource(int toggleLayoutResId) {
    	mTogglePopLayoutResId = toggleLayoutResId;
    }
    
    public int getTogglePopLayoutResource() {
        return mTogglePopLayoutResId;
    }

    protected View onCreateToggleView() {
        if (mTogglePopLayoutResId == 0) {
            return null;
        }
        
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(mTogglePopLayoutResId, null);
    }
    
    protected void showDialog() {
        Context context = getContext();

		final View contentView = onCreateToggleView();     
		if(contentView != null) {
			mBuilder = new AlertDialog.Builder(context);  
			mBuilder.setTitle(mDialogTitle)  
			.setIcon(mDialogIcon)  
			.setView(contentView)
			.setPositiveButton(mPositiveButtonText, mPositiveButtonListenr) 
			.setNegativeButton(mNegativeButtonText, mNegtiveButtonListenr)
			.show();  
		}
		
		mDialog = mBuilder.create();
    }
    
    public void onDismiss(DialogInterface dialog) {
		Log.i(TAG, "BasePopConfirmDialogPreference onDismiss");
        //getPreferenceManager().unregisterOnActivityDestroyListener(this);
        
        mDialog = null;
        onDialogClosed(mWhichButtonClicked == DialogInterface.BUTTON_POSITIVE);
    }

    protected void onDialogClosed(boolean positiveResult) {
    }

    public Dialog getDialog() {
        return mDialog;
    }

    public void onActivityDestroy() {
        
		Log.i(TAG, "BasePopConfirmDialogPreference onActivityDestroy");
        if (mDialog == null || !mDialog.isShowing()) {
            return;
        }
        
        mDialog.dismiss();
    }
}
