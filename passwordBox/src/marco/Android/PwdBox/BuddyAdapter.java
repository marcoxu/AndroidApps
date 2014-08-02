package marco.Android.PwdBox;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BuddyAdapter extends BaseExpandableListAdapter {     
	private static final String TAG = "marco.Android.PwdBox.BuddyAdapter";
	private ArrayList<String> categoryGroupList = null;
	private ArrayList<ArrayList<HashMap<String, Object>>> groupItemList = new ArrayList<ArrayList<HashMap<String, Object>>>();
    LayoutInflater inflater; 
	public dataAccessor dbHandler = null;
    
	public void updateCategoryList(){
        categoryGroupList = dbHandler.getCategoryList();
	}

	public void updateCategoryItemListAll(){
		if(pwdConfigActivity.need_update_data){
			for(int i=0;i<getGroupCount();i++){
				updateCategoryItemList(i);
			}
		}
		pwdConfigActivity.need_update_data = false;
	}

	public HashMap<String, Object> getPwdItem(int groupPosition, int childPosition){
		return groupItemList.get(groupPosition).get(childPosition);
        //int category = (Integer) groupItemList.get(groupPosition).get(childPosition).get("category");
        //String desc = groupItemList.get(groupPosition).get(childPosition).get("desc").toString();
        //String account = groupItemList.get(groupPosition).get(childPosition).get("account").toString();
        //String pwd = groupItemList.get(groupPosition).get(childPosition).get("pwd").toString();

        //HashMap<String, Object> map = new HashMap<String, Object>();
        //map.put("category", category);   
        //Log.i(TAG,"getPwdList: category is " + cursor.getInt(0));
        //map.put("desc", desc); 
        //Log.i(TAG,"getPwdList: desc is " + cursor.getString(1).toString());
        //map.put("account", account); 
        //Log.i(TAG,"getPwdList: account is " + cursor.getString(2).toString());
        //map.put("pwd", pwd); 
        //Log.i(TAG,"getPwdList: pwd is " + cursor.getString(6).toString());
        //Log.i(TAG,"getPwdList: category is " + cursor.getString(0).toString() + ", desc is " + cursor.getString(1).toString());
        //return map;
	}
	
	public void initCategoryItemList(int groupIdx){
	}

	public void updateCategoryItemList(int groupIdx){
		int categoryId = dbHandler.getCategoryIndex(categoryGroupList.get(groupIdx));
        //Log.i(TAG,"2 initCategoryItemList categoryId [" + groupIdx + ":" + categoryId + "], size " + groupItemList.size());

    	if(groupIdx > groupItemList.size()-1){
            //Log.i(TAG,"1.1 initCategoryItemList categoryId " + categoryId + ", size " + groupItemList.size());
    		for(int i=0;i<groupIdx-groupItemList.size()-2;i++){
        		groupItemList.add(new ArrayList<HashMap<String, Object>>());
    		}
    		groupItemList.add(dbHandler.queryPwdList("category="+categoryId));
    	}else{
		    groupItemList.set(groupIdx, dbHandler.queryPwdList("category="+categoryId));
    	}
        //Log.i(TAG,"2 initCategoryItemList categoryId [" + groupIdx + ":" + categoryId + "], size " + groupItemList.size());
        //Log.i(TAG,"3 check children count " + groupItemList.get(groupIdx).size());
	}

    public BuddyAdapter(Context context, dataAccessor hdlr){ 
        inflater = LayoutInflater.from(context); 
        //this.group=group; 
        //this.buddy=buddy; 
        this.dbHandler = hdlr;
        //ArrayList<HashMap<String, Object>> pwdlist_show = null;
        //pwdlist_show = dbHandler.getPwdList();
        updateCategoryList();
    	updateCategoryItemListAll();
    } 
    
    public Object getChild(int groupPosition, int childPosition) { 
    	//updateCategoryItemList(groupPosition);
    	updateCategoryItemListAll();
    	if(groupItemList.size() <= groupPosition || groupItemList.get(groupPosition).isEmpty()){
    		return "";
    	}

        String desc = groupItemList.get(groupPosition).get(childPosition).get("desc").toString();
        String account = groupItemList.get(groupPosition).get(childPosition).get("account").toString();
        return desc + ": [ " + account + " ]"; 
    } 

    public Object getChildHint(int groupPosition, int childPosition) { 
    	if(groupItemList.size() > groupPosition && groupItemList.get(groupPosition).isEmpty()){
    		initCategoryItemList(groupPosition);
    	}
    	if(groupItemList.size() <= groupPosition || groupItemList.get(groupPosition).isEmpty()){
    		return null;
    	}

        String hint = groupItemList.get(groupPosition).get(childPosition).get("hint").toString();
        return "提示: " + hint; 
    } 

    public int getChildEncrymod(int groupPosition, int childPosition) { 
    	if(groupItemList.size() > groupPosition && groupItemList.get(groupPosition).isEmpty()){
    		initCategoryItemList(groupPosition);
    	}
    	if(groupItemList.size() <= groupPosition || groupItemList.get(groupPosition).isEmpty()){
    		return 0;
    	}

        int encrymod = (Integer) groupItemList.get(groupPosition).get(childPosition).get("encrymod");
        return encrymod; 
    } 

    public long getChildId(int groupPosition, int childPosition) { 
        return childPosition; 
    } 
 
    public View getChildView(int groupPosition, int childPosition, boolean arg2, View convertView, 
            ViewGroup arg4) { 
        convertView = inflater.inflate(R.layout.pwd_item, null); 
        TextView nickTextView=(TextView) convertView.findViewById(R.id.buddy_listview_child_nick); 
        TextView hintTextView=(TextView) convertView.findViewById(R.id.buddy_listview_child_trends); 
        ImageView itemImg = (ImageView) convertView.findViewById(R.id.buddy_listview_child_avatar); 
        nickTextView.setText(getChild(groupPosition, childPosition).toString()); 
        hintTextView.setText(getChildHint(groupPosition, childPosition).toString()); 
        
        int encrymod = getChildEncrymod(groupPosition, childPosition);
        if(encrymod == 0){
            itemImg.setBackgroundResource( R.drawable.pwd_unlock);
        }else{
            itemImg.setBackgroundResource(R.drawable.pwd_lock);
        }
        return convertView; 
    } 
 
    public int getChildrenCount(int groupPosition) { 
    	//updateCategoryItemList(groupPosition);
    	updateCategoryItemListAll();


    	if(groupItemList.get(groupPosition) == null){
    		return 0;
    	}
        return groupItemList.get(groupPosition).size(); 
    } 
 
    public Object getGroup(int groupPosition) { 
    	if(categoryGroupList == null){
    		return "";
    	}
        return categoryGroupList.get(groupPosition); 
    } 
 
    public int getGroupCount() { 
    	if(categoryGroupList == null){
            Log.i(TAG,"getGroupCount: 0 ");
    		return 0;
    	}
        return categoryGroupList.size(); 
    } 
 
    public long getGroupId(int groupPosition) { 
        return groupPosition; 
    } 
 
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup arg3) { 
        convertView = inflater.inflate(R.layout.category_group_item, null); 
        TextView groupNameTextView=(TextView) convertView.findViewById(R.id.buddy_listview_group_name); 
        TextView groupNumTextView=(TextView) convertView.findViewById(R.id.buddy_listview_group_num); 
        if(categoryGroupList == null){
            updateCategoryList();
        }
        
        groupNameTextView.setText(getGroup(groupPosition).toString()); 
        //Log.i(TAG,"getGroupView: group num " + getChildrenCount(groupPosition));
        groupNumTextView.setText("" + getChildrenCount(groupPosition) + ""); 
        //groupNameTextView.setText(categoryGroupList.get(groupPosition)); 
        ImageView image = (ImageView) convertView.findViewById(R.id.buddy_listview_image); 
        //image.setImageResource(R.drawable.group_unfold_arrow); 
        image.setBackgroundResource(R.drawable.group_unfold_arrow);
        //更换展开分组图片  
        if(!isExpanded){ 
            //image.setImageResource(R.drawable.group_fold_arrow); 
            image.setBackgroundResource(R.drawable.group_fold_arrow);
        } 
        return convertView; 
    } 
 
    public boolean hasStableIds() { 
        return true; 
    } 
    // 子选项是否可以选择   
    public boolean isChildSelectable(int arg0, int arg1) { 
        // TODO Auto-generated method stub  
        return true; 
    } 
} 