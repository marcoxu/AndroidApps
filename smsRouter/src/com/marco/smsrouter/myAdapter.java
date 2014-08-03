package com.marco.smsrouter;

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
	private static final String TAG = "smsrouter.myAdapter";
	private Context mContext;
	private final int SUB_STRING_LENGTH = 100;
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
		convertView = mInflater.inflate(R.layout.sms_rte_history_item, null);//取出每项的模板
        boolean needAdd = false;
        
		if(position < itemList.size()){
		    holder = (ViewHolder)itemList.get(position);
		}else{
		    holder = new ViewHolder();
		    holder.setChecked(false);
		    needAdd = true;
		}
		final int pos = position;  
		holder.setmFromNo((TextView) convertView.findViewById(R.id.from_number));//模板里的组件
		holder.setmToNo((TextView) convertView.findViewById(R.id.to_number));
		holder.mContent = (TextView) convertView.findViewById(R.id.sms_content);
		holder.mChk = (CheckBox)convertView.findViewById(R.id.blacklist_check);
		holder.mDate = (TextView) convertView.findViewById(R.id.sms_date);
		holder.mChk.setOnClickListener(new View.OnClickListener() {  
            public void onClick(View v) {  
                CheckBox cb = (CheckBox)v;  
                // 记录checkbox状态
                if(cb.isChecked()){
            		Log.i(TAG, "set checkbox checked " + pos);
            		((ViewHolder)itemList.get(pos)).setChecked(true);
                }else{
            		Log.i(TAG, "set checkbox unchecked " + pos);
            		((ViewHolder)itemList.get(pos)).setChecked(false);
                }
            }  
        });
		
		if(needAdd){
			// 如果是新Item，添加到List中
			convertView.setTag(holder);  
	        itemList.add(position, holder);
		}
		// 设置Item中各控件的状态
        holder.mChk.setChecked(((ViewHolder)itemList.get(position)).isChecked());  
        holder.getmFromNo().setText("");  
        //if(dataList.get(position).get("FromNo") != null){
        //    holder.mFromNo.setText(dataList.get(position).get("FromNo").toString());  
        //}else{
        //    holder.mFromNo.setText(dataList.get(position).get("FromNo").toString());  
        //}
        //holder.mToNo.setText("");  
        if(dataList.get(position).get("ToNo") != null){
            holder.getmToNo().setText(dataList.get(position).get("ToNo").toString());  
        }else{
            holder.getmToNo().setText("");
        }
        if(dataList.get(position).get("date") != null){
            holder.mDate.setText(dataList.get(position).get("date").toString());  
        }else{
            holder.mDate.setText("");  
        }
        if(dataList.get(position).get("content") != null){
        	String sContent = dataList.get(position).get("content").toString();
        	if(sContent.length() > SUB_STRING_LENGTH)
        		holder.mContent.setText(sContent.substring(0, SUB_STRING_LENGTH));
        	else
        		holder.mContent.setText(sContent);
        }else{
            holder.mContent.setText("");  
        }
         
        return convertView;  
	}
	
	public class ViewHolder {
		private TextView mFromNo;
		private TextView mToNo;
		TextView mContent;
		TextView mDate;
		CheckBox mChk;
		private boolean isChecked = false;
		public TextView getmFromNo() {
			return mFromNo;
		}
		public void setmFromNo(TextView mFromNo) {
			this.mFromNo = mFromNo;
		}
		public TextView getmToNo() {
			return mToNo;
		}
		public void setmToNo(TextView mToNo) {
			this.mToNo = mToNo;
		}
		public boolean isChecked() {
			return isChecked;
		}
		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}
	}
}
