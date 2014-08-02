/*
 * Copyright (c) 2012 Shanda Corporation. All rights reserved.
 *
 * Created on 2012-01-30.
 */
package com.snda.myPhone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class myAdapter extends SimpleAdapter {
	private static final String TAG = "com.snda.myPhone.myAdapter";
	private Context mContext;
    //自己定义集合去保存ListView的数据
	private ArrayList<Object> itemList = new ArrayList<Object>();
	private ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();

	public myAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource,
			String[] from, int[] to) {
		super(context, data, resource, from, to);
		dataList = (ArrayList<HashMap<String, Object>>)data;
		// TODO Auto-generated constructor stub
		mContext = context;
	}

	public ArrayList<Object> getList() {
		return itemList;
	}
	
    // ListView的Item填充
	public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
		ViewHolder holder;
		LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
		convertView = mInflater.inflate(R.layout.blacklist_select_item, null);//取出每项的模板
        boolean needAdd = false;
        
		if(position < itemList.size()){
		    holder = (ViewHolder)itemList.get(position);
		}else{
		    holder = new ViewHolder();
		    holder.isChecked = false;
		    needAdd = true;
		}
		final int pos = position;  
		holder.mName = (TextView) convertView.findViewById(R.id.blacklist_name);//模板里的组件
		holder.mNumbr = (TextView) convertView.findViewById(R.id.blacklist_number);
		holder.mLocation = (TextView) convertView.findViewById(R.id.blacklist_location);
		holder.mChk = (CheckBox)convertView.findViewById(R.id.blacklist_check);
		holder.mChk.setOnClickListener(new View.OnClickListener() {  
            public void onClick(View v) {  
                CheckBox cb = (CheckBox)v;  
                // 记录checkbox状态
                if(cb.isChecked()){
            		Log.i(TAG, "set checkbox checked " + pos);
            		((ViewHolder)itemList.get(pos)).isChecked = true;
                }else{
            		Log.i(TAG, "set checkbox unchecked " + pos);
            		((ViewHolder)itemList.get(pos)).isChecked = false;
                }
            }  
        });
		
		if(needAdd){
			// 如果是新Item，添加到List中
			convertView.setTag(holder);  
	        itemList.add(position, holder);
		}
		// 设置Item中各控件的状态
        holder.mChk.setChecked(((ViewHolder)itemList.get(position)).isChecked);  
        if(dataList.get(position).get("name") != null){
            holder.mName.setText(dataList.get(position).get("name").toString());  
        }else{
            holder.mName.setText(dataList.get(position).get("showname").toString());  
        }
        if(dataList.get(position).get("number") != null){
            holder.mNumbr.setText(dataList.get(position).get("number").toString());  
        }else{
            holder.mNumbr.setText("");  
        }
        if(dataList.get(position).get("location") != null){
            holder.mLocation.setText(dataList.get(position).get("location").toString());  
        }else{
            holder.mLocation.setText("");  
        }
         
        return convertView;  
	}
	
	public class ViewHolder {
		TextView mName;
		TextView mNumbr;
		TextView mLocation;
		CheckBox mChk;
		boolean isChecked = false;
	}
}