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
        category_list.add("����·���");
        mySpinner = (Spinner)findViewById(R.id.spinner_category);     
        //�ڶ�����Ϊ�����б���һ����������������õ���ǰ�涨���list��     
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, category_list);     
        //��������Ϊ���������������б�����ʱ�Ĳ˵���ʽ��     
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     
        //���Ĳ�������������ӵ������б���     
        mySpinner.setAdapter(adapter);     
   	
    }
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);// ��������
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
        //��ťClick�¼�����
        saveBtn.setOnClickListener(new buttonClickListener());

        category_list = conf_dbHdlr.getCategoryList();
        category_list.add("����·���");
        encrymod_list.add("�����ܣ����ı���");
        encrymod_list.add("AES����");
        dispmod_list.add("��ȫ��ʾ");
        //myTextCategory = (EditText)findViewById(R.id.selected_category);     
        mySpinner = (Spinner)findViewById(R.id.spinner_category);     
        encrymod_Spinner = (Spinner)findViewById(R.id.spinner_encrymod);     
        dispmod_Spinner = (Spinner)findViewById(R.id.spinner_dispmod);     
        //�ڶ�����Ϊ�����б���һ����������������õ���ǰ�涨���list��     
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, category_list);     
        //��������Ϊ���������������б�����ʱ�Ĳ˵���ʽ��     
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     
        //���Ĳ�������������ӵ������б���     
        mySpinner.setAdapter(adapter);     
        //���岽��Ϊ�����б����ø����¼�����Ӧ���������Ӧ�˵���ѡ��     
        mySpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){     
            @SuppressWarnings("unchecked")  
            public void onItemSelected(AdapterView arg0, View arg1, int arg2, long arg3) {     
                // TODO Auto-generated method stub     
                /* ����ѡmySpinner ��ֵ����myTextView ��*/    
            	if(arg2 == category_list.size() - 1){
                	//myTextCategory.setText("");     
                    LayoutInflater inflater_current = LayoutInflater.from(pwdConfigActivity.this);   
            		final View DialogView = inflater_current.inflate (R.layout.category_add, null);       
            		new AlertDialog.Builder(pwdConfigActivity.this)   
            		.setTitle("�������·�����")  
            		.setIcon(android.R.drawable.ic_dialog_info)  
            		.setView(DialogView)  
            		.setPositiveButton("ȷ��", new DialogInterface.OnClickListener(){
            			public void onClick(DialogInterface dialog, int whichButton)
                        {
            	             EditText editText =(EditText) DialogView.findViewById(R.id.add_category);
            	             //Log.i(TAG,"category editText is " + editText.getText());
            	             pwdConfigActivity.conf_dbHdlr.insertCategory(editText.getText().toString());
            	             updateCategoryList();
                        }
            		}) 
            		.setNegativeButton("ȡ��", null)  
            		.show();              	    
            		return;
            	}
            	//myTextCategory.setText(adapter.getItem(arg2));     
                /* ��mySpinner ��ʾ*/    
                //arg0.setVisibility(View.VISIBLE);     
            }     
            @SuppressWarnings("unchecked")  
            public void onNothingSelected(AdapterView arg0) {     
                // TODO Auto-generated method stub     
            	//myTextCategory.setText("");     
                //arg0.setVisibility(View.VISIBLE);     
            }     
        });     
        /*�����˵�����������ѡ����¼�����*/    
        mySpinner.setOnTouchListener(new Spinner.OnTouchListener(){     
			public boolean onTouch(View v, MotionEvent arg1) {
                // TODO Auto-generated method stub     
                /* ��mySpinner ���أ�������Ҳ���ԣ����Լ�����*/    
                //v.setVisibility(View.INVISIBLE);     
            	//myTextCategory.setText(adapter.getItem(0));     
                return false;     
			}     
        });     
        /*�����˵�����������ѡ���ı��¼�����*/    
        mySpinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){     
        public void onFocusChange(View v, boolean hasFocus) {     
        // TODO Auto-generated method stub     
            //v.setVisibility(View.VISIBLE);     
        }     
        });   
        
        //�ڶ�����Ϊ�����б���һ����������������õ���ǰ�涨���list��     
        encymod_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, encrymod_list);     
        //��������Ϊ���������������б�����ʱ�Ĳ˵���ʽ��     
        encymod_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     
        //���Ĳ�������������ӵ������б���     
        encrymod_Spinner.setAdapter(encymod_adapter);     
        //���岽��Ϊ�����б����ø����¼�����Ӧ���������Ӧ�˵���ѡ��     
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
        /*�����˵�����������ѡ����¼�����*/    
        encrymod_Spinner.setOnTouchListener(new Spinner.OnTouchListener(){     
			public boolean onTouch(View v, MotionEvent arg1) {
                // TODO Auto-generated method stub     
                /* ��mySpinner ���أ�������Ҳ���ԣ����Լ�����*/    
                //v.setVisibility(View.INVISIBLE);     
                return false;     
			}     
        });     
        /*�����˵�����������ѡ���ı��¼�����*/    
        encrymod_Spinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){     
        public void onFocusChange(View v, boolean hasFocus) {     
        // TODO Auto-generated method stub     
            //v.setVisibility(View.VISIBLE);     
        }     
        });   
        encrymod_Spinner.setSelection(1);

        //�ڶ�����Ϊ�����б���һ����������������õ���ǰ�涨���list��     
        dispmod_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, dispmod_list);     
        //��������Ϊ���������������б�����ʱ�Ĳ˵���ʽ��     
        dispmod_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);     
        //���Ĳ�������������ӵ������б���     
        dispmod_Spinner.setAdapter(dispmod_adapter);     
        //���岽��Ϊ�����б����ø����¼�����Ӧ���������Ӧ�˵���ѡ��     
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
        /*�����˵�����������ѡ����¼�����*/    
        dispmod_Spinner.setOnTouchListener(new Spinner.OnTouchListener(){     
			public boolean onTouch(View v, MotionEvent arg1) {
                // TODO Auto-generated method stub     
                /* ��mySpinner ���أ�������Ҳ���ԣ����Լ�����*/    
                //v.setVisibility(View.INVISIBLE);     
                return false;     
			}     
        });     
        /*�����˵�����������ѡ���ı��¼�����*/    
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
	    			.setMessage("��ѡ�����")
	    			.setPositiveButton("ȷ��",  null)
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
	    			.setMessage("����д��ע")
	    			.setPositiveButton("ȷ��",  null)
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
	    			.setMessage("����д�˺�")
	    			.setPositiveButton("ȷ��",  null)
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
	    			.setMessage("�������벻ƥ��,����������")
	    			.setPositiveButton("ȷ��",  null)
	    			.show();
    	            return;
    			}
    			
    	        int retcode = PwdDisplayActivity.dbHdlr.insertPwdList(categoryId, desc, account, hint, dispmod, encrymod, pwd);
    		    if(retcode != 1){
                    Toast.makeText(pwdConfigActivity.this,   
                    		"��¼��ӳɹ�",   
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
                    		"��¼���ʧ��",   
                            Toast.LENGTH_SHORT).show(); 
    		    }
    		}

    	}
    }

}
