package com.learning.demogrid;

import android.os.AsyncTask;
import android.os.Debug;
import android.widget.ImageView;

public class GridAsyncTask extends AsyncTask<Integer, Integer, String> {

	private String mImageName;
	private GridController mData = null;
	private ImageView mImageView = null;
	private final String asyncTraceFile = "asyncTraceFile";
	
	public GridAsyncTask(String img, ImageView v, GridController data) {
        super();  
        mImageName = img;
        mData = data;
        mImageView = v;
	}
	
	protected void onPreExecute(){
		//Debug.startMethodTracing(asyncTraceFile);
	}
	
	protected void onPostExecute(String result) {
		//Debug.stopMethodTracing();
	}
	
	@Override
	protected String doInBackground(Integer... arg0) {
		
		
		// load particular image in thread
		mData.loadSpecificImage(mImageName, mImageView);
		return null;
	}
}
