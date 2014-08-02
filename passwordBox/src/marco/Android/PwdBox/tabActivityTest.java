package marco.Android.PwdBox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;


public class tabActivityTest extends ActivityGroup  {
    private static final String TAG = "marco.Android.PwdBox";
    private TabHost mTabHost;  
    private RadioGroup radioderGroup;  
    //public static final String dbPath = "/data/data/marco.Android.PwdBox/databases/";
    public static final String dbPath = "/mnt/sdcard/pwdBox/databases/";
    public static final String dbName = "pwd.db";
    public static int count_onResume = 0;

    public void onResume() {
    	super.onResume();  

    	if(count_onResume > 0){
            Log.i(TAG, "=======> tabActivity onResume ");
            Intent startAct = new Intent(getApplicationContext(), PwdBoxActivity.class);	
            startAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startActivity(startAct);
            this.finish();
            count_onResume = 0;
    	} else {
        	count_onResume = count_onResume + 1;
    	}
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);   
    	requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
    	count_onResume = 0;
        loadPwdDB();
        onCreate2();
    }
   
    public void onCreate1() {   
        //setContentView(R.layout.tabmain);  
    	//mTabHost = getTabHost();
        //mTabHost.setup();
        LayoutInflater inflater_tab1 = LayoutInflater.from(this);   
        inflater_tab1.inflate(R.layout.pwddislaypage, mTabHost.getTabContentView());  
        inflater_tab1.inflate(R.layout.pwditem_add, mTabHost.getTabContentView());   
        inflater_tab1.inflate(R.layout.tab3, mTabHost.getTabContentView());   
        mTabHost.addTab(mTabHost.newTabSpec("TAB1").setIndicator("全部", getResources().getDrawable(R.drawable.ic_launcher)).setContent(new Intent(this,PwdDisplayActivity.class)));  
        mTabHost.addTab(mTabHost.newTabSpec("TAB2").setIndicator("添加").setContent(new Intent(this,pwdConfigActivity.class)));    
        mTabHost.addTab(mTabHost.newTabSpec("TAB3").setIndicator("设置").setContent(new Intent(this,PwdDisplayActivity.class)));   
        mTabHost.setBackgroundColor(0x000000);

        mTabHost.setCurrentTab(0); 
        mTabHost.setOnTabChangedListener(new OnTabChangeListener(){
            public void onTabChanged(String tabId) {
                // TODO Auto-generated method stub
                Log.i(TAG, "=======> tabActivity onTabChanged " + tabId);
                if(tabId.equals("TAB1")){
                    Message msg = new Message();
                    msg.what = PwdDisplayActivity.EVENT_TAB_CHANGE;
                    PwdDisplayActivity.tabeventHdlr.removeMessages(PwdDisplayActivity.EVENT_TAB_CHANGE);
                    PwdDisplayActivity.tabeventHdlr.sendMessage(msg);
                }else if (tabId.equals("TAB2")){
                    Message msg = new Message();
                    msg.what = PwdDisplayActivity.EVENT_TAB_CHANGE;
                    pwdConfigActivity.tabconfeventHdlr.removeMessages(pwdConfigActivity.EVENT_CONF_TAB_CHANGE);
                    pwdConfigActivity.tabconfeventHdlr.sendMessage(msg);
                }
            }            
        });
        //radioderGroup = (RadioGroup) findViewById(R.id.main_radio);  
        //radioderGroup.setOnCheckedChangeListener(this);  
    }
    
    public void onCreate2() {   
        setContentView(R.layout.tabmain);   
        mTabHost = (TabHost)findViewById(R.id.tabhost1); 
        mTabHost.setup();
        mTabHost.setup(this.getLocalActivityManager());       
        
        LayoutInflater inflater_tab1 = LayoutInflater.from(this);   
        inflater_tab1.inflate(R.layout.pwddislaypage, mTabHost.getTabContentView(), true);  
        inflater_tab1.inflate(R.layout.tab2, mTabHost.getTabContentView());   
        inflater_tab1.inflate(R.layout.tab3, mTabHost.getTabContentView());   
        mTabHost.addTab(mTabHost.newTabSpec("TAB1").setIndicator("全部").setContent(new Intent(this,PwdDisplayActivity.class)));  
        mTabHost.addTab(mTabHost.newTabSpec("TAB2").setIndicator("添加").setContent(new Intent(this,pwdConfigActivity.class)));    
        mTabHost.addTab(mTabHost.newTabSpec("TAB3").setIndicator("设置").setContent(new Intent(this,PwdDisplayActivity.class)));   
        mTabHost.setBackgroundColor(0x000000);

        mTabHost.setCurrentTab(0); 

        mTabHost.setOnTabChangedListener(new OnTabChangeListener(){
            public void onTabChanged(String tabId) {
                // TODO Auto-generated method stub
                Log.i(TAG, "=======> tabActivity onTabChanged " + tabId);
                if(tabId.equals("TAB1")){
                    Message msg = new Message();
                    msg.what = PwdDisplayActivity.EVENT_TAB_CHANGE;
                    PwdDisplayActivity.tabeventHdlr.removeMessages(PwdDisplayActivity.EVENT_TAB_CHANGE);
                    PwdDisplayActivity.tabeventHdlr.sendMessage(msg);
                }else if (tabId.equals("TAB2")){
                    Message msg = new Message();
                    msg.what = PwdDisplayActivity.EVENT_TAB_CHANGE;
                    pwdConfigActivity.tabconfeventHdlr.removeMessages(pwdConfigActivity.EVENT_CONF_TAB_CHANGE);
                    pwdConfigActivity.tabconfeventHdlr.sendMessage(msg);
                }
            }            
        });

    }

	public void onCheckedChanged(RadioGroup group, int checkedId) {
        Log.i(TAG, "=======> tabActivity onCheckedChanged " + checkedId);
        /*
        switch(checkedId){  
        case R.id.radio_button0:  
        	mTabHost.setCurrentTabByTag("TAB1");  
            Log.i(TAG, "=======> tabActivity onCheckedChanged radio_button0 ");
        	((BuddyAdapter) PwdDisplayActivity.dbAdapter).updateCategoryList();
            break;  
        case R.id.radio_button1:  
            Log.i(TAG, "=======> tabActivity onCheckedChanged radio_button1 ");
        	mTabHost.setCurrentTabByTag("TAB2");  
            break;  
        case R.id.radio_button2:  
            Log.i(TAG, "=======> tabActivity onCheckedChanged radio_button2 ");
        	mTabHost.setCurrentTabByTag("TAB3");  
            break;  
        }    
        */     
	}

    //拷贝Assets下的号码归属地数据库文件
    private void loadPwdDB(){
        // 拷贝号码归属地数据库到/data/data/marco.Android.PwdBox/databases
    	String filePath = dbPath + dbName;
		
		File dbdir = new File(dbPath); 
        if (!dbdir.exists()) { 
              try { 
              	  Log.i(TAG, "create db dir " + dbPath);
                  //在指定的文件夹中创建文件 
            	  boolean result = dbdir.mkdirs(); 
              	  Log.i(TAG, "create db dir returns " + result);
            } catch (Exception e) { 
    			e.printStackTrace();
            } 
        } 
    	Log.i(TAG, "get pwdbfile" + filePath);
		File pwdbfile = new File(filePath); 
        if (!pwdbfile.exists()) { 
        	try { 
        		//在指定的文件夹中创建文件 
        		pwdbfile.createNewFile(); 
            	Log.i(TAG, "Finish create DB " + filePath);
        	} catch (Exception e) { 
        		e.printStackTrace();
        	} 
        }
    }
}
