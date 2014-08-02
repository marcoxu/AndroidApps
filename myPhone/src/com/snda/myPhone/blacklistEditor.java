/*
 * Copyright (c) 2012 Shanda Corporation. All rights reserved.
 *
 * Created on 2012-01-30.
 */
package com.snda.myPhone;
  
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.snda.myPhone.myPhoneActivity;

import android.app.Activity;  
import android.os.Bundle;  
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
  
//添加黑名单页面
public class blacklistEditor extends Activity {
	private static final String TAG = "com.snda.myPhone.blacklist_editor";
	public List<HashMap<String, Object>> data_array_for_add = new ArrayList<HashMap<String,Object>>();
	private SimpleAdapter listItemAdapter = null;
	private int blacklistType = -1; //黑名单类型  3：归属地  4：号码
	
    private class ButtonClickListener implements OnClickListener{
    	public synchronized void onClick(View v) {
    		switch(v.getId()){
    		case R.id.blacklist_add:
    	        // 记录要添加的新黑名单号码或归属地，并显示到列表中
    	        EditText txt_add = (EditText) findViewById(R.id.blacklist_input);  
    	        ListView edit_blacklist = (ListView) findViewById(R.id.blacklist_editor);  
    	        if(txt_add != null && txt_add.getText().length() > 0){
        			HashMap<String, Object> map = new HashMap<String, Object>();
        			map.put("content", txt_add.getText().toString());
        			data_array_for_add.add(map);
        			
                    //生成适配器的Item和动态数组对应的元素   
            		listItemAdapter = new SimpleAdapter(getApplicationContext(), data_array_for_add,
                                                    R.layout.blacklist_item,
                                                    new String[] {"content"},    
        				                            new int[] {R.id.blacklist_number});
                    //添加并且显示   
                    if(edit_blacklist.getAdapter() == null){
                    	edit_blacklist.addHeaderView(new LinearLayout(getApplicationContext()));
                    	edit_blacklist.addFooterView(new LinearLayout(getApplicationContext()));
                    }
                    edit_blacklist.setAdapter(listItemAdapter); 
                    txt_add.setText("");
    	        }
    		    break;
    		case R.id.blacklist_add_submit:
    	        // 将列表中的黑名单项添加的数据库中，完成黑名单添加
    			if(data_array_for_add != null && data_array_for_add.size() > 0){
    				for(int i=0;i<data_array_for_add.size();i++){
    					if(blacklistType == 3){
            				myPhoneActivity.dbHandler.insertBlacklist(0, "", data_array_for_add.get(i).get("content").toString());
            				blacklistManager.notifyBlacklistUpdate();
    					}else if(blacklistType == 4){
    						myPhoneActivity.dbHandler.insertBlacklist(1, "", data_array_for_add.get(i).get("content").toString());
    						blacklistManager.notifyBlacklistUpdate();
    					}
    				}
    			}

    			blacklistEditor.this.finish();
    		    break;
    		case R.id.blacklist_add_cancel:
    	        // 取消黑名单添加操作
    			blacklistEditor.this.finish();
                break;
    		}
    	}
    }

    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.blacklist_editor);
        
        int addType = getIntent().getIntExtra("ImportFrom", 0);
        ListView blacklist = (ListView) findViewById(R.id.blacklist_editor);  

        Button btn_add = (Button) findViewById(R.id.blacklist_add);  
        Button btn_ok = (Button) findViewById(R.id.blacklist_add_submit);  
        Button btn_cancel = (Button) findViewById(R.id.blacklist_add_cancel);  
        if(blacklist == null || btn_ok == null || btn_cancel == null){
        	return;
        }

        btn_add.setOnClickListener(new ButtonClickListener());
        btn_ok.setOnClickListener(new ButtonClickListener());
        btn_cancel.setOnClickListener(new ButtonClickListener());
        
        Log.i(TAG, "addType is " + addType);
        blacklistType = addType;        
    }  
}