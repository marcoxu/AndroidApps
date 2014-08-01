package com.training.MediaPlayer;

import java.util.HashMap;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class MusicInfoFragment extends Fragment {

	private static final String TAG = "MediaPlayer";
	private TextView mTitle;
	private TextView mArtist;
	//private TextView mAlbum;
	private TextView mSize;
	private TextView mDuration;
	private ImageButton mWeiboBtn;
	//private TextView mName;
	private static View mInfoView;
		
	public void showMedia(Context context, String media) {
		if(media == null) {
	 	    if(mTitle == null) {
	 	    	// Fragment is not displayed, return
	 	    	return;
	 	    }
			mTitle.setText("");
			mArtist.setText("");
			mSize.setText("");
			mDuration.setText("");
			return;
		}
		HashMap<String, String> info = MusicLRCHandler.mInfo; //MusicLRCHandler.getMusicInfo(context, media);
		
		if(info == null || info.size() == 0) {
			info = MusicLRCHandler.getMusicInfo(context, media);
		}
 	    Log.i(TAG, "id " + info.get("Id"));
 	    Log.i(TAG, "title " + info.get("Title"));
 	    Log.i(TAG, "artist " + info.get("Artist"));
 	    Log.i(TAG, "album " + info.get("Album"));
 	    Log.i(TAG, "size" + info.get("Size"));
 	    Log.i(TAG, "duration " + info.get("Duration"));
 	    Log.i(TAG, "url" + info.get("Url"));
 	    Log.i(TAG, "name " + info.get("Name"));
 	    
 	    if(mTitle == null) {
 	    	// Fragment is not displayed, return
 	    	return;
 	    }
		mTitle.setText(info.get("Title").toString());
		mArtist.setText(info.get("Artist").toString());
		mSize.setText(info.get("Size").toString());
		mDuration.setText(info.get("Duration").toString());
		//mName.setText(info.get("Name"));
	}

	private void getTextViews(){
		mTitle = (TextView)mInfoView.findViewById(R.id.media_title_content);
		mArtist = (TextView)mInfoView.findViewById(R.id.media_artist_name);
		mSize = (TextView)mInfoView.findViewById(R.id.media_size_value);
		mDuration = (TextView)mInfoView.findViewById(R.id.media_duration_value);
		//mName = (TextView)mInfoView.findViewById(R.id.media_title);
		mWeiboBtn = (ImageButton)mInfoView.findViewById(R.id.sinaweibo_icon);

        try {
            BitmapFactory.Options opts = new BitmapFactory.Options(); 
            opts.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.logo_sinaweibo, opts);
    		mWeiboBtn.setImageBitmap(bitmap);
    		mWeiboBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
			 	    Log.i(TAG, "Share OnClick");
				}
    		});
        } catch (OutOfMemoryError err) {
            Log.e(TAG, err.toString());
        }

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.i(TAG, "Entered MusicInfoFragment onCreateView()");
		mInfoView = inflater.inflate(R.layout.musicinfo, container, false);
		getTextViews();
		return mInfoView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Read in all Twitter feeds 
		Log.i(TAG, "Entered MusicInfoFragment onActivityCreated()");
	}


	// Display Twitter feed for selected feed

	void updateFeedDisplay(int position) {

		Log.i(TAG, "Entered updateFeedDisplay()");

	}
}
