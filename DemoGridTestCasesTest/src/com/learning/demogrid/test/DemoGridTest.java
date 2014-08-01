package com.learning.demogrid.test;

import com.learning.demogrid.DemoGrid;
import com.learning.demogrid.GridController;
import com.robotium.solo.*;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;

public class DemoGridTest extends ActivityInstrumentationTestCase2<DemoGrid> {
	private Solo solo;
	private Activity mDemoGridAct;

	public DemoGridTest() {
		super(DemoGrid.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation());
		mDemoGridAct = this.getActivity();
	}
	
	private void doScrollUpnDown() {
		Display display = solo.getCurrentActivity().getWindowManager().getDefaultDisplay();
	    @SuppressWarnings("deprecation")
		int width = display.getWidth();
	    @SuppressWarnings("deprecation")
		int height = display.getHeight();
	    
	    // scroll down
	    solo.drag(width/2, width/2, height-100, height/5, 20);
		assertTrue("onScrollStateChanged() Log Message not found",
				solo.waitForLogMessage("onScrollStateChanged",2000));
		
		solo.sleep(3000);

	    solo.drag(width/2, width/2, height-100, height/5, 20);
		assertTrue("onScrollStateChanged() Log Message not found",
				solo.waitForLogMessage("onScrollStateChanged",2000));
		
		solo.sleep(3000);
		
	    solo.drag(width/2, width/2, height-100, height/5, 20);
		assertTrue("onScrollStateChanged() Log Message not found",
				solo.waitForLogMessage("onScrollStateChanged",2000));
		
		solo.sleep(3000);
		
	    // scroll up
	    solo.drag(width/2, width/2, height/5, height-100, 20);
		assertTrue("onScrollStateChanged() Log Message not found",
				solo.waitForLogMessage("onScrollStateChanged",2000));
		
		solo.sleep(3000);
		
	    solo.drag(width/2, width/2, height/5, height-100, 20);
		assertTrue("onScrollStateChanged() Log Message not found",
				solo.waitForLogMessage("onScrollStateChanged",2000));
		
		solo.sleep(3000);
		
	    solo.drag(width/2, width/2, height/5, height-100, 20);
		assertTrue("onScrollStateChanged() Log Message not found",
				solo.waitForLogMessage("onScrollStateChanged",2000));
		
		solo.sleep(3000);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInitialCondition() {
		assertTrue("DemoGrid not found", solo.waitForActivity(
				DemoGrid.class, 2000));
		
		// Wait for view: 'android.R.id.text1'
	    assertTrue("gridview not found", solo.waitForView(com.learning.demogrid.R.id.gridview));

		// Clear log
		solo.clearLog();
		
		// Press menu back key
		solo.goBack();
	}

	public void testStateOnPause() {
		getInstrumentation().callActivityOnPause(mDemoGridAct);

		assertTrue("onPause() Log Message not found",
				solo.waitForLogMessage("onPause",2000));
    }

	public void testStateOnStart() {
		getInstrumentation().callActivityOnStart(mDemoGridAct);

		assertTrue("onStart() Log Message not found",
				solo.waitForLogMessage("onStart",2000));
    }

	public void testStateOnRestart() {
		getInstrumentation().callActivityOnRestart(mDemoGridAct);

		assertTrue("onRestart() Log Message not found",
				solo.waitForLogMessage("onRestart",2000));
    }

	public void testStateOnResume() {
		getInstrumentation().callActivityOnResume(mDemoGridAct);

		assertTrue("onResume() Log Message not found",
				solo.waitForLogMessage("onResume",2000));
    }

	public void testStateOnStop() {
		getInstrumentation().callActivityOnStop(mDemoGridAct);

		assertTrue("onStop() Log Message not found",
				solo.waitForLogMessage("onStop",2000));
    }
	
	public void testScroll() {
		assertTrue("DemoGrid not found", solo.waitForActivity(
				DemoGrid.class, 2000));
		
		// Wait for view: 'android.R.id.text1'
	    assertTrue("gridview not found", solo.waitForView(com.learning.demogrid.R.id.gridview));

		solo.sleep(2000);
		
		doScrollUpnDown();
		
	}

	public void testSimpleAdapter() {
		assertTrue("DemoGrid not found", solo.waitForActivity(
				DemoGrid.class, 2000));
		
		// Wait for view: 'android.R.id.text1'
	    assertTrue("gridview not found", solo.waitForView(com.learning.demogrid.R.id.gridview));

		solo.sleep(2000);
		
	    // select simpleAdapter
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.spinner_adaptertype));
		solo.sleep(1000);
		solo.clickInList(1);
		assertTrue("onItemSelected() Log Message not found",
				solo.waitForLogMessage("onItemSelected for Adapter",2000));
		solo.sleep(2000);

		// select thread pool
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.spinner_asnyctype));
		solo.sleep(1000);
		solo.clickInList(1);
		assertTrue("onItemSelected() Log Message not found",
				solo.waitForLogMessage("onItemSelected for AsyncType",2000));
		solo.sleep(2000);

		// start to load images
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.btnStartLoad));
		assertTrue("onClick() Log Message not found",
				solo.waitForLogMessage("onClick to load image",2000));
		solo.sleep(3000);

		doScrollUpnDown();

		// select asyncTask
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.spinner_asnyctype));
		solo.sleep(1000);
		solo.clickInList(2);
		assertTrue("onItemSelected() Log Message not found",
				solo.waitForLogMessage("onItemSelected for AsyncType",2000));
		solo.sleep(2000);

		// start to load images
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.btnStartLoad));
		assertTrue("onClick() Log Message not found",
				solo.waitForLogMessage("onClick to load image",2000));
		solo.sleep(3000);

		doScrollUpnDown();
	}

	public void testBaseAdapter() {
		assertTrue("DemoGrid not found", solo.waitForActivity(
				DemoGrid.class, 2000));
		
		// Wait for view: 'android.R.id.text1'
	    assertTrue("gridview not found", solo.waitForView(com.learning.demogrid.R.id.gridview));

		solo.sleep(2000);
		
	    // select BaseAdapter
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.spinner_adaptertype));
		solo.sleep(1000);
		solo.clickInList(2);
		assertTrue("onItemSelected() Log Message not found",
				solo.waitForLogMessage("onItemSelected for Adapter",2000));

		solo.sleep(2000);

		// select thread pool
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.spinner_asnyctype));
		solo.sleep(1000);
		solo.clickInList(1);
		assertTrue("onItemSelected() Log Message not found",
				solo.waitForLogMessage("onItemSelected for AsyncType",2000));
		solo.sleep(2000);

		// start to load images
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.btnStartLoad));
		assertTrue("onClick() Log Message not found",
				solo.waitForLogMessage("onClick to load image",2000));

		solo.sleep(3000);

		doScrollUpnDown();

		// select asyncTask
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.spinner_asnyctype));
		solo.sleep(1000);

		solo.clickInList(2);
		assertTrue("onItemSelected() Log Message not found",
				solo.waitForLogMessage("onItemSelected for AsyncType",2000));

		solo.sleep(2000);

		// start to load images
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.btnStartLoad));
		assertTrue("onClick() Log Message not found",
				solo.waitForLogMessage("onClick to load image",2000));

		solo.sleep(3000);

		doScrollUpnDown();
    }
	
	public void testScreenLandScapeWithBaseAdapter() {
		assertTrue("DemoGrid not found", solo.waitForActivity(
				DemoGrid.class, 2000));
		
		// Wait for view: 'android.R.id.text1'
	    assertTrue("gridview not found", solo.waitForView(com.learning.demogrid.R.id.gridview));

		solo.sleep(2000);
		
	    // change orientation
		solo.getCurrentActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		assertTrue("onConfigurationChanged() Log Message not found",
				solo.waitForLogMessage("onConfigurationChanged",2000));

		solo.sleep(3000);

	    // select BaseAdapter
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.spinner_adaptertype));
		solo.sleep(1000);
		solo.clickInList(2);
		assertTrue("onItemSelected() Log Message not found",
				solo.waitForLogMessage("onItemSelected for Adapter",2000));

		solo.sleep(2000);

		// select thread pool
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.spinner_asnyctype));
		solo.sleep(1000);
		solo.clickInList(1);
		assertTrue("onItemSelected() Log Message not found",
				solo.waitForLogMessage("onItemSelected for AsyncType",2000));
		solo.sleep(2000);

		// start to load images
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.btnStartLoad));
		assertTrue("onClick() Log Message not found",
				solo.waitForLogMessage("onClick to load image",2000));

		solo.sleep(3000);

		doScrollUpnDown();

		// select asyncTask
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.spinner_asnyctype));
		solo.sleep(1000);

		solo.clickInList(2);
		assertTrue("onItemSelected() Log Message not found",
				solo.waitForLogMessage("onItemSelected for AsyncType",2000));

		solo.sleep(2000);

		// start to load images
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.btnStartLoad));
		assertTrue("onClick() Log Message not found",
				solo.waitForLogMessage("onClick to load image",2000));

		solo.sleep(3000);

		doScrollUpnDown();

	    // change orientation back to portrait
		solo.getCurrentActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		assertTrue("onConfigurationChanged() Log Message not found",
				solo.waitForLogMessage("onConfigurationChanged",2000));

		solo.sleep(2000);

	}

	public void testScreenLandScapeWithSimpleAdapter() {
		assertTrue("DemoGrid not found", solo.waitForActivity(
				DemoGrid.class, 2000));
		
		// Wait for view: 'android.R.id.text1'
	    assertTrue("gridview not found", solo.waitForView(com.learning.demogrid.R.id.gridview));

		solo.sleep(2000);
		
	    // change orientation
		solo.getCurrentActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		assertTrue("onConfigurationChanged() Log Message not found",
				solo.waitForLogMessage("onConfigurationChanged",2000));

		solo.sleep(3000);

	    // select SimpleAdapter
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.spinner_adaptertype));
		solo.sleep(1000);
		solo.clickInList(1);
		assertTrue("onItemSelected() Log Message not found",
				solo.waitForLogMessage("onItemSelected for Adapter",2000));

		solo.sleep(2000);

		// select thread pool
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.spinner_asnyctype));
		solo.sleep(1000);
		solo.clickInList(1);
		assertTrue("onItemSelected() Log Message not found",
				solo.waitForLogMessage("onItemSelected for AsyncType",2000));
		solo.sleep(2000);

		// start to load images
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.btnStartLoad));
		assertTrue("onClick() Log Message not found",
				solo.waitForLogMessage("onClick to load image",2000));

		solo.sleep(3000);

		doScrollUpnDown();

		// select asyncTask
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.spinner_asnyctype));
		solo.sleep(1000);

		solo.clickInList(2);
		assertTrue("onItemSelected() Log Message not found",
				solo.waitForLogMessage("onItemSelected for AsyncType",2000));

		solo.sleep(2000);

		// start to load images
		solo.clickOnView(solo.getView(com.learning.demogrid.R.id.btnStartLoad));
		assertTrue("onClick() Log Message not found",
				solo.waitForLogMessage("onClick to load image",2000));

		solo.sleep(3000);

		doScrollUpnDown();

	    // change orientation back to portrait
		solo.getCurrentActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		assertTrue("onConfigurationChanged() Log Message not found",
				solo.waitForLogMessage("onConfigurationChanged",2000));

		solo.sleep(2000);

	}
	
	public void testGridController() {
		assertTrue("DemoGrid not found", solo.waitForActivity(
				DemoGrid.class, 2000));
		
		// Wait for view: 'android.R.id.text1'
	    assertTrue("gridview not found", solo.waitForView(com.learning.demogrid.R.id.gridview));

		solo.sleep(2000);
		
	    // get controller
		GridController controller = ((DemoGrid)mDemoGridAct).mCommData;
		
		controller.startLoadImage("Test", null);
		
		controller.getScreenAttribute();
		
		controller.loadSpecificImage("Test", null);
		
	}
}
