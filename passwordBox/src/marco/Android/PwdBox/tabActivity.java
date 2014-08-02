package marco.Android.PwdBox;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;


public class tabActivity extends ActivityGroup {
    private static final String TAG = "marco.Android.PwdBox";
    private TabHost mTabHost;  
    //public static final String dbPath = "/data/data/marco.Android.PwdBox/databases/";
    public static final String dbPath = "/mnt/sdcard/pwdBox/databases/";
    public static final String dbName = "pwd.db";
    public static int count_onResume = 0;
    public static String global_key;
    public static String DEAFULT_KEY = "123456";
    public static String user_key = null;
    public static SimpleCrypto  crypto = null;
	public static dataAccessor dbHdlr = null;

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

    private String getCurrentTime(){
    	Calendar cal = Calendar.getInstance();
    	return cal.getTime().toLocaleString();
    }
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);   
    	requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
    	count_onResume = 0;
    	pwdConfigActivity.need_update_data = true;
    	crypto = new SimpleCrypto();
    	global_key = getCurrentTime();
        dbHdlr = new dataAccessor(getApplicationContext(), tabActivity.dbPath+tabActivity.dbName);

        LayoutInflater inflater_current = LayoutInflater.from(tabActivity.this);   
		final View DialogView = inflater_current.inflate (R.layout.input_encry_key, null);    
		
    	user_key = DEAFULT_KEY;
		if(user_key == null){
		new AlertDialog.Builder(tabActivity.this)   
		.setTitle("请输入用于加密及解密的密码：")  
		.setIcon(android.R.drawable.ic_dialog_info)  
		.setView(DialogView)  
		.setPositiveButton("确定", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton)
            {
	             EditText editText =(EditText) DialogView.findViewById(R.id.add_key);
	             //Log.i(TAG,"category editText is " + editText.getText());
	             try {
					user_key = crypto.encrypt(tabActivity.global_key, editText.getText().toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	             loadPwdDB();
	             
	             //ArrayList<HashMap<String, Object>> list = dbHdlr.queryPwdList("encrymod=1");
	             //if(list.size() > 0){
	             //     for(int i=0;i<list.size();i++){
		         //   	 if(list.get(i).get("pwd").toString().length()>0){
		    	 //            try {
		    	//					String clearpwd = crypto.decrypt(editText.getText().toString(), list.get(i).get("pwd").toString());
		    	//				} catch (Exception e) {
		    	//					// TODO Auto-generated catch block
		    	//					e.printStackTrace();
		    	//					tabActivity.this.finish();
		    	//					return;
		    	//				}
		         //   	 }
	            //	 }
	             //}
	             onCreate2();
            }
		}) 
		.show();     
		}else{
            try {
				user_key = crypto.encrypt(tabActivity.global_key, user_key);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            loadPwdDB();
            onCreate2();
		}
    }
   
    /*
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
    */
    
    public void onCreate2() {   
        setContentView(R.layout.tabmain);   
        mTabHost = (TabHost)findViewById(R.id.tabhost1); 
        mTabHost.setup(this.getLocalActivityManager());       
        
        View addTab = (View) LayoutInflater.from(this).inflate(R.layout.tabmini, null);  
        TextView text1 = (TextView) addTab.findViewById(R.id.tab_label);  
        ImageView imgV1 = (ImageView) addTab.findViewById(R.id.tab_img);  
        imgV1.setBackgroundResource(R.drawable.tab_icon_add);
        text1.setText("添加");  
        View confTab = (View) LayoutInflater.from(this).inflate(R.layout.tabmini, null);  
        TextView text2 = (TextView) confTab.findViewById(R.id.tab_label);  
        ImageView imgV2 = (ImageView) confTab.findViewById(R.id.tab_img);  
        imgV2.setBackgroundResource(R.drawable.tab_icon_config);
        text2.setText("设置");  
        View niTab = (View) LayoutInflater.from(this).inflate(R.layout.tabmini, null);  
        TextView text0 = (TextView) niTab.findViewById(R.id.tab_label);  
        ImageView imgV3 = (ImageView) niTab.findViewById(R.id.tab_img);  
        imgV3.setBackgroundResource(R.drawable.tab_icon_all);
        text0.setText("全部");  
        mTabHost.addTab(mTabHost.newTabSpec("TAB1").setIndicator(niTab).setContent(new Intent(this,PwdDisplayActivity.class)));  
        mTabHost.addTab(mTabHost.newTabSpec("TAB2").setIndicator(addTab).setContent(new Intent(this,pwdConfigActivity.class)));    
        mTabHost.addTab(mTabHost.newTabSpec("TAB3").setIndicator(confTab).setContent(new Intent(this,PwdAppConfigActivity.class)));   
        
        mTabHost.getCurrentTabView().findViewById(R.id.tab_relativelayout).setBackgroundColor(0xFF2A2A2A);
        
        mTabHost.setOnTabChangedListener(new OnTabChangeListener(){
            public void onTabChanged(String tabId) {
                // TODO Auto-generated method stub
                RelativeLayout rlayout = (RelativeLayout)mTabHost.getCurrentTabView().findViewById(R.id.tab_relativelayout);
                rlayout.setBackgroundColor(0xFF2A2A2A);
                
                for(int i=0;i<mTabHost.getTabWidget().getTabCount();i++){
                	TextView textV = (TextView)mTabHost.getTabWidget().getChildAt(i).findViewById(R.id.tab_label);
                	TextView ctextV = (TextView)mTabHost.getCurrentTabView().findViewById(R.id.tab_label);
                	if(textV.getText() != ctextV.getText()){
                	    mTabHost.getTabWidget().getChildAt(i).findViewById(R.id.tab_relativelayout).setBackgroundColor(0xFF3D3F44);
                	}
                }
                
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
        mTabHost.setup();
        mTabHost.setCurrentTab(0); 
    }

	public void onCheckedChanged(RadioGroup group, int checkedId) {
        //Log.i(TAG, "=======> tabActivity onCheckedChanged " + checkedId);
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