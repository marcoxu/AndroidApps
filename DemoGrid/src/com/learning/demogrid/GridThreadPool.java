package com.learning.demogrid;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.widget.ImageView;

public class GridThreadPool {
	    
	    private static int CORE_POOL_SIZE = 5;
	    private static int MAX_POOL_SIZE = 10;
	    private static int KEEP_ALIVE_TIME = 10000;
	    private static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(10);

	    private static ThreadFactory threadFactory = new ThreadFactory() {
		    private final AtomicInteger integer = new AtomicInteger();
	        @Override
	        public Thread newThread(Runnable r) {
	            return new Thread(r, "myThreadPool thread:" + integer.getAndIncrement());
	        }
	    };
	    
	    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue,
                threadFactory);

	    private GridThreadPool(){
	    }
        
	    	    
		public static class GridRunnable implements Runnable{
	    	private String image_name;
	    	private ImageView imgV;
			private GridController mData = null;
	    	
			public GridRunnable(String img, ImageView v, GridController data){
	    		image_name = img;
	    		imgV = v;
	    		mData = data;
	    	}
	    	
	        @Override
	        public void run() {
	    		
	    		// load particular image in thread
	        	mData.loadSpecificImage(image_name, imgV);
	        }
	    }

	    public static void execute(Runnable runnable){
			// load particular image in thread
	        threadPool.execute(runnable);
	    }
}
