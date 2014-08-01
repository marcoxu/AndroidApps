package com.training.MediaPlayer;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LrcFragment extends Fragment {

	private static final String TAG = "MediaPlayer";

	public MusicLRCView mLRCView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.i(TAG, "Entered FeedFragment onCreateView()");
		View feedView = inflater.inflate(R.layout.feed, container, false);
		mLRCView = (MusicLRCView) feedView.findViewById(R.id.lrc);
		
		if(mLRCView == null) {
			Log.i(TAG, "Entered onCreateView(): mLRCView not found");
		}
		return feedView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Read in all Twitter feeds 
		mLRCView = (MusicLRCView) getView().findViewById(R.id.lrc);
	}


	// Display Twitter feed for selected feed

	void updateFeedDisplay(int position) {

		Log.i(TAG, "Entered updateFeedDisplay()");
				
		mLRCView = (MusicLRCView) getView().findViewById(R.id.lrc);

	}

}
