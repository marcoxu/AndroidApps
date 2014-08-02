package marco.Android.PwdBox;

import java.util.ArrayList;
import java.util.List;

import marco.Android.PwdBox.PwdDisplayActivity.tabEnventHandler;

import com.snda.woa.android.OpenAPI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class pwdConfigActivity extends Activity {
    private static final String TAG = "marco.Android.pwdConfigActivity";
    private Spinner mySpinner;     
    private ArrayAdapter<String> adapter;     
	public static dataAccessor conf_dbHdlr = null;
	public static ExpandableListAdapter conf_dbAdapter = null;
    private ArrayList<String> category_list = new ArrayList<String>();     
    private ArrayAdapter<String> encymod_adapter;     
    private ArrayAdapter<String> dispmod_adapter;     
    private ArrayList<String> encrymod_list = new ArrayList<String>();     
    private ArrayList<String> dispmod_list = new ArrayList<String>();     
    private Spinner encrymod_Spinner;     
    private Spinner dispmod_Spinner;     
	public static final int EVENT_CONF_TAB_CHANGE   = 0;
	public static taConfbEventHandler tabconfeventHdlr = null;
	public static boolean need_update_data = true;

	class taConfbEventHandler extends Handler {
		public taConfbEventHandler() {
			super();
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {			
			case EVENT_CONF_TAB_CHANGE:
				updateCategoryList();
                break;
			}
		}
	}

    public void updateCategoryList(){
        category_list = conf_dbHdlr.getCategoryList();
        category_list.add("添加新分组");
        mySpinner = (Spinner)findViewById(R.id.spinner_category);     
        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。     
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, category_list);     
        //第三步：为适配器设置下拉列表下拉时的菜单样式。     
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     
        //第四步：将适配器添加到下拉列表上     
        mySpinner.setAdapter(adapter);     
   	
    }
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
        setContentView(R.layout.pwditem_add);
        
        if(PwdDisplayActivity.dbAdapter == null){
        	conf_dbHdlr = new dataAccessor(getApplicationContext(), tabActivity.dbPath+tabActivity.dbName);
        	conf_dbAdapter = new BuddyAdapter(this, conf_dbHdlr); 
        }else{
        	conf_dbAdapter = PwdDisplayActivity.dbAdapter;
        	conf_dbHdlr = PwdDisplayActivity.dbHdlr;
        }

        tabconfeventHdlr = new taConfbEventHandler();
        Button saveBtn = (Button) findViewById(R.id.btnok);
        //按钮Click事件监听
        saveBtn.setOnClickListener(new buttonClickListener());

        category_list = conf_dbHdlr.getCategoryList();
        category_list.add("添加新分组");
        encrymod_list.add("不加密，明文保存");
        encrymod_list.add("AES加密");
        dispmod_list.add("完全显示");
        //myTextCategory = (EditText)findViewById(R.id.selected_category);     
        mySpinner = (Spinner)findViewById(R.id.spinner_category);     
        encrymod_Spinner = (Spinner)findViewById(R.id.spinner_encrymod);     
        dispmod_Spinner = (Spinner)findViewById(R.id.spinner_dispmod);     
        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。     
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, category_list);     
        //第三步：为适配器设置下拉列表下拉时的菜单样式。     
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     
        //第四步：将适配器添加到下拉列表上     
        mySpinner.setAdapter(adapter);     
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中     
        mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){     
            @SuppressWarnings("unchecked")  
            public void onItemSelected(AdapterView arg0, View arg1, int arg2, long arg3) {     
                // TODO Auto-generated method stub     
                /* 将所选mySpinner 的值带入myTextView 中*/    
            	if(arg2 == category_list.size() - 1){
                	//myTextCategory.setText("");     
                    LayoutInflater inflater_current = LayoutInflater.from(pwdConfigActivity.this);   
            		final View DialogView = inflater_current.inflate (R.layout.category_add, null);       
            		new AlertDialog.Builder(pwdConfigActivity.this)   
            		.setTitle("请输入新分组名")  
            		.setIcon(android.R.drawable.ic_dialog_info)  
            		.setView(DialogView)  
            		.setPositiveButton("确定", new DialogInterface.OnClickListener(){
            			public void onClick(DialogInterface dialog, int whichButton)
                        {
            	             EditText editText =(EditText) DialogView.findViewById(R.id.add_category);
            	             //Log.i(TAG,"category editText is " + editText.getText());
            	             pwdConfigActivity.conf_dbHdlr.insertCategory(editText.getText().toString());
            	             updateCategoryList();
                        }
            		}) 
            		.setNegativeButton("取消", null)  
            		.show();              	    
            		return;
            	}
            	//myTextCategory.setText(adapter.getItem(arg2));     
                /* 将mySpinner 显示*/    
                //arg0.setVisibility(View.VISIBLE);     
            }     
            @SuppressWarnings("unchecked")  
            public void onNothingSelected(AdapterView arg0) {     
                // TODO Auto-generated method stub     
            	//myTextCategory.setText("");     
                //arg0.setVisibility(View.VISIBLE);     
            }     
        });     
        /*下拉菜单弹出的内容选项触屏事件处理*/    
        mySpinner.setOnTouchListener(new Spinner.OnTouchListener(){     
			public boolean onTouch(View v, MotionEvent arg1) {
                // TODO Auto-generated method stub     
                /* 将mySpinner 隐藏，不隐藏也可以，看自己爱好*/    
                //v.setVisibility(View.INVISIBLE);     
            	//myTextCategory.setText(adapter.getItem(0));     
                return false;     
			}     
        });     
        /*下拉菜单弹出的内容选项焦点改变事件处理*/    
        mySpinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){     
        public void onFocusChange(View v, boolean hasFocus) {     
        // TODO Auto-generated method stub     
            //v.setVisibility(View.VISIBLE);     
        }     
        });   
        
        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。     
        encymod_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, encrymod_list);     
        //第三步：为适配器设置下拉列表下拉时的菜单样式。     
        encymod_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     
        //第四步：将适配器添加到下拉列表上     
        encrymod_Spinner.setAdapter(encymod_adapter);     
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中     
        encrymod_Spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){     
            @SuppressWarnings("unchecked")  
            public void onItemSelected(AdapterView arg0, View arg1, int arg2, long arg3) {     
                // TODO Auto-generated method stub     
            }     
            @SuppressWarnings("unchecked")  
            public void onNothingSelected(AdapterView arg0) {     
                // TODO Auto-generated method stub     
                //arg0.setVisibility(View.VISIBLE);     
            }     
        });     
        /*下拉菜单弹出的内容选项触屏事件处理*/    
        encrymod_Spinner.setOnTouchListener(new Spinner.OnTouchListener(){     
			public boolean onTouch(View v, MotionEvent arg1) {
                // TODO Auto-generated method stub     
                /* 将mySpinner 隐藏，不隐藏也可以，看自己爱好*/    
                //v.setVisibility(View.INVISIBLE);     
                return false;     
			}     
        });     
        /*下拉菜单弹出的内容选项焦点改变事件处理*/    
        encrymod_Spinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){     
        public void onFocusChange(View v, boolean hasFocus) {     
        // TODO Auto-generated method stub     
            //v.setVisibility(View.VISIBLE);     
        }     
        });   
        encrymod_Spinner.setSelection(1);

        //第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。     
        dispmod_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, dispmod_list);     
        //第三步：为适配器设置下拉列表下拉时的菜单样式。     
        dispmod_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     
        //第四步：将适配器添加到下拉列表上     
        dispmod_Spinner.setAdapter(dispmod_adapter);     
        //第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中     
        dispmod_Spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){     
            @SuppressWarnings("unchecked")  
            public void onItemSelected(AdapterView arg0, View arg1, int arg2, long arg3) {     
                // TODO Auto-generated method stub     
            }     
            @SuppressWarnings("unchecked")  
            public void onNothingSelected(AdapterView arg0) {     
                // TODO Auto-generated method stub     
                //arg0.setVisibility(View.VISIBLE);     
            }     
        });     
        /*下拉菜单弹出的内容选项触屏事件处理*/    
        dispmod_Spinner.setOnTouchListener(new Spinner.OnTouchListener(){     
			public boolean onTouch(View v, MotionEvent arg1) {
                // TODO Auto-generated method stub     
                /* 将mySpinner 隐藏，不隐藏也可以，看自己爱好*/    
                //v.setVisibility(View.INVISIBLE);     
                return false;     
			}     
        });     
        /*下拉菜单弹出的内容选项焦点改变事件处理*/    
        dispmod_Spinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){     
        public void onFocusChange(View v, boolean hasFocus) {     
        // TODO Auto-generated method stub     
            //v.setVisibility(View.VISIBLE);     
        }     
        });             
    
    }
    
    private class buttonClickListener implements OnClickListener{
    	public synchronized void onClick(View v) {
    		switch(v.getId()){
    		case R.id.btnok:
    			String category = mySpinner.getSelectedItem().toString();
    	        //Log.i(TAG,"pwdConfigActivity save category " + category);
    			int categoryId = conf_dbHdlr.getCategoryIndex(category);
    			if(categoryId < 0){
	    			new AlertDialog.Builder(pwdConfigActivity.this) 
	    			.setMessage("请选择分组")
	    			.setPositiveButton("确定",  null)
	    			.show();
    	            return;
    			}
    			
    	        //Log.i(TAG,"pwdConfigActivity save categoryId " + categoryId);
    			int encrymod = encrymod_Spinner.getSelectedItemPosition();
    	        //Log.i(TAG,"pwdConfigActivity save encrymod " + encrymod);
    	        int dispmod = encrymod_Spinner.getSelectedItemPosition();
    	        //Log.i(TAG,"pwdConfigActivity save dispmod " + dispmod);
    	        EditText input_desc = (EditText)findViewById(R.id.input_desc);     
    			String desc = input_desc.getText().toString();
    			desc = desc.trim();
    			if(desc.trim().length() == 0){
	    			new AlertDialog.Builder(pwdConfigActivity.this) 
	    			.setMessage("请填写备注")
	    			.setPositiveButton("确定",  null)
	    			.show();
    	            return;
    			}
    			
    	        //Log.i(TAG,"pwdConfigActivity save desc " + desc);
    	        EditText input_hint = (EditText)findViewById(R.id.input_hint);     
    			String hint = input_hint.getText().toString();
    	        //Log.i(TAG,"pwdConfigActivity save hint " + hint);
    	        EditText input_account = (EditText)findViewById(R.id.input_account);     
    			String account = input_account.getText().toString();
    			account = account.trim();
    			if(account.trim().length() == 0){
	    			new AlertDialog.Builder(pwdConfigActivity.this) 
	    			.setMessage("请填写账号")
	    			.setPositiveButton("确定",  null)
	    			.show();
    	            return;
    			}

    			//Log.i(TAG,"pwdConfigActivity save account " + account);
    	        EditText input_pwd = (EditText)findViewById(R.id.input_pwd);     
    	        EditText input_pwd_repeat = (EditText)findViewById(R.id.input_pwd_repeat);     
    			String pwd = input_pwd.getText().toString();
    			String pwd_repeat = input_pwd_repeat.getText().toString();
    			if(!pwd_repeat.equals(pwd)){
	    			new AlertDialog.Builder(pwdConfigActivity.this) 
	    			.setMessage("密码输入不匹配,请重新输入")
	    			.setPositiveButton("确定",  null)
	    			.show();
    	            return;
    			}
    			
    	        int retcode = PwdDisplayActivity.dbHdlr.insertPwdList(categoryId, desc, account, hint, dispmod, encrymod, pwd);
    		    if(retcode != 1){
                    Toast.makeText(pwdConfigActivity.this,   
                    		"记录添加成功",   
                            Toast.LENGTH_SHORT).show(); 
                    input_pwd.setText("");
                    input_pwd_repeat.setText("");
                    input_account.setText("");
                    input_hint.setText("");
                    input_desc.setText("");
                    encrymod_Spinner.setSelection(1);
                    need_update_data = true;
    		    } else {
                    Toast.makeText(pwdConfigActivity.this,   
                    		"记录添加失败",   
                            Toast.LENGTH_SHORT).show(); 
    		    }
    		}

    	}
    }

}
