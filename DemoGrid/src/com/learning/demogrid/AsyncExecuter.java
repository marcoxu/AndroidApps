package com.learning.demogrid;

import android.widget.ImageView;

public class AsyncExecuter {
	private AsyncExeType mType;
	private GridController mData = null;

	public enum AsyncExeType {
		THREADPOOL, ASYNCTASK
	}
	
	public AsyncExecuter(AsyncExeType type, GridController data) {
		mType = type;
		mData = data;
	}
	
	// execute the task in threads
	public void execute(String img, ImageView v) {
		if(mType == AsyncExeType.THREADPOOL){
			GridThreadPool.GridRunnable runner = new GridThreadPool.GridRunnable(img, v, mData);
		    GridThreadPool.execute(runner);
		}else if (mType == AsyncExeType.ASYNCTASK) {
			GridAsyncTask newTask = new GridAsyncTask(img, v, mData);
    		newTask.execute();
		}
	}
}
