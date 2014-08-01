package com.learning.demogrid;

import java.util.ArrayList;

import com.learning.demogrid.GridViewBinderCreator.GridViewAdapterType;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AbsListView.OnScrollListener;

public class DemoGrid extends Activity implements OnScrollListener {

    private String TAG = "DemoGrid";
    public GridController mCommData = null;
    private Spinner mAdapterSpinner = null;     
    private Spinner mAsyncSpinner = null;     
    private ArrayList<String> adapterType_list = new ArrayList<String>();     
    private ArrayList<String> asyncType_list = new ArrayList<String>();     
    private GridViewAdapterType mAdaptertype = GridViewAdapterType.SIMPLE;;
    private AsyncExecuter.AsyncExeType mAsyncType = AsyncExecuter.AsyncExeType.THREADPOOL;;
    private Button mBtnLoad = null;
    private Activity mAct = null;

    public void onCreate(Bundle savedInstanceState) {   
        requestWindowFeature( Window.FEATURE_NO_TITLE ); //无标题 
        super.onCreate(savedInstanceState);    
          
        setContentView(R.layout.activity_demo_grid);
        Log.i(TAG,"onCreate");
        
        mAct = this;
        mAdapterSpinner = (Spinner)findViewById(R.id.spinner_adaptertype);     
        mAsyncSpinner   = (Spinner)findViewById(R.id.spinner_asnyctype);
        mBtnLoad        = (Button)findViewById(R.id.btnStartLoad);
        adapterType_list.add("SimpleAdapter");
        adapterType_list.add("BaseAdapter");
        asyncType_list.add("ThreadPool");
        asyncType_list.add("AsyncTask");

        // Initial mAdapterSpinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, adapterType_list);     
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     
        mAdapterSpinner.setAdapter(adapter);     
        mAdapterSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){     

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
		        Log.i(TAG,"onItemSelected for Adapter");
				switch(arg2)
				{
				case 0:
					mAdaptertype = GridViewAdapterType.SIMPLE;
					break;
				case 1:
					mAdaptertype = GridViewAdapterType.BASE;
					break;
				}
            	return;				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				mAdaptertype = GridViewAdapterType.SIMPLE;
			}
        });

        // Initial mAsyncSpinner
        ArrayAdapter<String> asyncTypeadapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, asyncType_list);     
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     
        mAsyncSpinner.setAdapter(asyncTypeadapter);     
        mAsyncSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){     

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
		        Log.i(TAG,"onItemSelected for AsyncType");
				switch(arg2)
				{
				case 0:
					mAsyncType = AsyncExecuter.AsyncExeType.THREADPOOL;
					break;
				case 1:
					mAsyncType = AsyncExecuter.AsyncExeType.ASYNCTASK;
					break;
				}
            	return;				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				mAsyncType = AsyncExecuter.AsyncExeType.THREADPOOL;
			}
        });
        
        mBtnLoad.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
		        Log.i(TAG,"onClick to load image");
		        // Initialize private members
		        mCommData = new GridController(mAct, mAdaptertype, mAsyncType);

		        // Start to load pictures
		        mCommData.LoadPicture();
			}
        });

        // Initialize private members
        mCommData = new GridController(this, mAdaptertype, mAsyncType);

        // Start to load pictures
        mCommData.LoadPicture();
    }  
   	
	@Override
	public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
		
	}   
	
	public void onPause() {
        super.onPause();    
        Log.i(TAG,"onPause");
	}

	public void onStop() {
        super.onStop();    
        Log.i(TAG,"onStop");
	}

	public void onStart() {
        super.onStart();    
        Log.i(TAG,"onStart");
	}

	public void onRestart() {
        super.onRestart();    
        Log.i(TAG,"onRestart");
	}

	public void onDestroy() {
        super.onDestroy();  
        mCommData.releaseAllResources();
        adapterType_list.clear();
        asyncType_list.clear();
        Log.i(TAG,"onDestroy");
	}

	public void onResume() {
        super.onResume();
        Log.i(TAG,"onResume");
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
		  super.onConfigurationChanged(newConfig);

		  Log.i(TAG, "onConfigurationChanged: " + newConfig.orientation);
          mCommData = new GridController(mAct, mAdaptertype, mAsyncType);

		  // Start to load pictures
	      mCommData.LoadPicture();
	      
		  if(newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER)
		  {
		      // do nothing
		  }
		  else if(newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
		  {
		      // do nothing
		  }
    }

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// do nothing
		  Log.i(TAG, "onScrollStateChanged");
		
	}
}