package com.learning.demogrid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.RejectedExecutionException;

import com.learning.demogrid.GridViewBinderCreator.GridViewAdapterType;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class GridController {
    private String TAG                                 = "DemoGrid";
    public final static int EVENT_LOAD_PIC_WITH_NAME   = 1;
    public final static int EVENT_ON_SCROLL_CHANGE     = 2;
    public final static int EVENT_SET_IMAGE_VIEW       = 3;
	private int width                                  = 0;
	private int height                                 = 0;
    private Activity mAct                              = null;
    private GridAdapterInterface mGridAdapter          = null;
    private LruCache<String, Bitmap>              mMemoryCache;
    public DemoGridHandler mHandler                    = new DemoGridHandler();;
    public ArrayList<HashMap<String, String>> srcTable = new ArrayList<HashMap<String, String>>();; 
    public ArrayList<Integer> picListId                = new ArrayList<Integer>();; 
    public BaseAdapter saTable                         = null;
    public GridView gridview                           = null;
    public AsyncExecuter mAsyncExer                    = null;
    public int LastVisiblePosition                     = 0;
    public int columns                                 = 0;
    public int items_per_screen                        = 0;
    
    public void addBitmapToCache(String img, Bitmap bm) {
    	if(bm == null) {
    		return;
    	}
    	
    	if(mMemoryCache.get(img) == null) {
    		mMemoryCache.put(img, bm);
    	}
    }
    
    public Bitmap getBitmapFromCache(String img) {
    	return mMemoryCache.get(img);
    }

    class ItemClickListener implements OnItemClickListener {    
        public void onItemClick(AdapterView<?> arg0,View arg1,int arg2,long arg3){    
	        Log.i(TAG,"OnItemClickListener");

	        String img = (String)picListId.get(arg2).toString();
            Toast.makeText(mAct.getApplicationContext(),img,Toast.LENGTH_SHORT).show(); 
        }
    }

    public void LoadPicture(){        
        saTable.notifyDataSetChanged();
     }
    
    // show the specified image
    public void loadSpecificImage(String img, ImageView v)
     {
        try {
             BitmapFactory.Options opts = new BitmapFactory.Options(); 
             opts.inSampleSize = 4;
             if(v == null) {
     	         Log.i(TAG,"loadSpecificPic: can't find imageview for " + img);
                 return;
             }

             Bitmap bitmap = getRes(img, opts);
      		 addBitmapToCache(img, bitmap);
             sendEventToSetView(img, v);
         } catch (OutOfMemoryError err) {
             Log.e(TAG, err.toString());
         }
     }

    public void sendMessageTohandler(Message msg, int delay)
    {
     	if(delay > 0) {
     		mHandler.sendMessageDelayed(msg, delay);
     	} else {
     		mHandler.sendMessage(msg);
     	}
    }

 	// Send event to load bitmap asynchronously
    public void sendEventToLoadBitmap(String value, Object obj, int delay) {
 		Message msg = new Message();
   	    Bundle data = new Bundle();
   	    data.putString("image_file", value);
   	    msg.obj = obj;
 		msg.setData(data);
 		msg.what = GridController.EVENT_LOAD_PIC_WITH_NAME;
 		sendMessageTohandler(msg, delay);
    }

    // Send event to load bitmap asynchronously
    public void sendEventToSetView(String img, ImageView v) {
 		Message msg = new Message();
 		Bundle data = new Bundle();
 		data.putString("image_file", img);
 		msg.setData(data);
 		msg.obj    = v;
 		msg.what   = EVENT_SET_IMAGE_VIEW;
 		sendMessageTohandler(msg, 0);
     }

    public GridController(Activity act, GridViewAdapterType type, AsyncExecuter.AsyncExeType asyncType) {
    	mAct         = act;
        mAsyncExer   = new AsyncExecuter(asyncType, this);
        mGridAdapter = GridViewBinderCreator.createGridAdapter(type);
		width        = act.getResources().getInteger(R.integer.imageview_width);
		height       = act.getResources().getInteger(R.integer.imageview_height);

        // Initialize image resources
        java.lang.reflect.Field[] fields = R.drawable.class.getDeclaredFields();
        for(java.lang.reflect.Field field:fields) {
        	int resid = mAct.getResources().getIdentifier("com.learning.demogrid:drawable/"+field.getName(), null, null);
      	    picListId.add(picListId.size(), resid);
        }

        // Initialize columns and screen related info
        getScreenAttribute();

        // Get max available VM memory, exceeding this amount will throw an
	    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
	    // int in its constructor.
	    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 8;

	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	            return bitmap.getByteCount() / 1024;
	        }
	    };

        // Initialize location info
        if(items_per_screen <= picListId.size()) {
        	LastVisiblePosition = items_per_screen - 1;
        } else {
        	LastVisiblePosition = picListId.size() - 1;
        }

        // Initialize gridview
        gridview = (GridView) act.findViewById(R.id.gridview);
        gridview.setNumColumns(columns);
        saTable = (BaseAdapter) mGridAdapter.bind(gridview, mAct, picListId, this);
                  
        gridview.setOnScrollListener((OnScrollListener)mAct); 
        gridview.setOnItemClickListener(new ItemClickListener());
    }
    
    public void getScreenAttribute() {
        // Initialize columns and screen related info
        WindowManager wm = (WindowManager) mAct.getSystemService(Context.WINDOW_SERVICE);
        @SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth();
        @SuppressWarnings("deprecation")
		int heigth = wm.getDefaultDisplay().getHeight();

        columns = width/mAct.getResources().getInteger(R.integer.imageview_width);
        int rows_per_screen = heigth/mAct.getResources().getInteger(R.integer.imageview_height) + 1;
        items_per_screen = rows_per_screen * columns;
    }

    public void startLoadImage(String value, ImageView v)
    {
    	if(v == null) {
    		return;
    	}
    	
    	if(getBitmapFromCache(value) != null) {
			v.setImageBitmap(getBitmapFromCache(value));
			return;
    	} else {
        	// clear the bitmap in the imageView before loading the image
        	v.setImageBitmap(null);
        }
        
        android.view.ViewGroup.LayoutParams para = v.getLayoutParams();
        para.width = width;
        para.height = height;
        v.setLayoutParams(para);
        
        // start to load bitmap for specified image
        sendEventToLoadBitmap(value, v, 0);
    }
    
    public ArrayList<HashMap<String, String>> updateSrcTable(ArrayList<Integer> list) {
        srcTable.clear();
        
        for(Integer strTxt:list){
     	    String strText = strTxt.toString();
            HashMap<String, String> map = new HashMap<String, String>(); 
            map.put("image_file",strText);
            srcTable.add(map);
        }
        return srcTable;
    }

    private void setGridImageView(String img, ImageView v, Bitmap bm)
    {
        if(bm != null) {
 	        //Log.i(TAG,"setGridImageView: set Bitmap for " + img);
            v.setImageBitmap(bm);
        } else {
	       Log.i(TAG,"setGridImageView: can't find Bitmap for " + img);
           v.setImageBitmap(null);
           v.setBackgroundColor(Color.BLACK);
        }
    }

    @SuppressLint("HandlerLeak")
	public
	class DemoGridHandler extends Handler {
		@SuppressLint("HandlerLeak")
		public DemoGridHandler() {
			super();
		}
		
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message msg) {
			Bundle data;
			String image_name;
			ImageView v = null;

			switch (msg.what) {			
			case EVENT_LOAD_PIC_WITH_NAME:
		        data = msg.getData(); 
		        image_name = data.getString("image_file");
		        Log.i(TAG,"EVENT_LOAD_PIC_WITH_NAME for " + image_name);
		        v = (ImageView) msg.obj;
	            if(v != null) {
	            	try {
	            		mAsyncExer.execute(image_name, v);
	            	} catch (RejectedExecutionException localRejectedExecutionException){  
	            		sendEventToLoadBitmap(image_name, v, 100);
	            	}
	            }
				break;
			case EVENT_SET_IMAGE_VIEW:
		        data = msg.getData(); 
		        image_name = data.getString("image_file");
		        ImageView view = (ImageView) msg.obj;
		        Log.i(TAG,"EVENT_SET_IMAGE_VIEW for " + image_name);
				setGridImageView(image_name, view, getBitmapFromCache(image_name));
		        break;
			}
		}
    }

	// Get bitmap for specified image
    public Bitmap getRes(String name, BitmapFactory.Options opts) {
    	ApplicationInfo appInfo = mAct.getApplicationInfo();
    	int resID = mAct.getResources().getIdentifier(name, "drawable", appInfo.packageName);
        if(resID != 0) {
        	if(opts != null) {
        	    return BitmapFactory.decodeResource(mAct.getResources(), resID, opts);
        	} else {
        	    return BitmapFactory.decodeResource(mAct.getResources(), resID);
        	}
        }
        return null;
    }

    // get resource id for specified image name
    public int getResId(String name) {
    	ApplicationInfo appInfo = mAct.getApplicationInfo();
    	return mAct.getResources().getIdentifier(name, "drawable", appInfo.packageName);
    }
    
    // release all resources
    public void releaseAllResources(){
    	srcTable.clear();
    	picListId.clear();
    }
}
