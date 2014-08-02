package marco.Android.PwdBox;

import java.util.HashMap;

import marco.Android.PwdBox.PwdBoxActivity.loginEnventHandler;

import com.snda.woa.android.OpenAPI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.ExpandableListContextMenuInfo;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

public class PwdDisplayActivity extends Activity{
    private static final String TAG = "marco.Android.PwdDisplay";
    ExpandableListView expandablelistview; 
	public static dataAccessor dbHdlr = null;
	public static ExpandableListAdapter dbAdapter = null;
	public static final int EVENT_TAB_CHANGE   = 0;
	public static tabEnventHandler tabeventHdlr = null;
	MenuItem currentSelectedItem = null; 

	class tabEnventHandler extends Handler {
		public tabEnventHandler(Activity act) {
			super();
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {			
			case EVENT_TAB_CHANGE:
				expandablelistviewUpdate();
                break;
			}
		}
	}

    /** Called when the activity is first created. */
    public void expandablelistviewUpdate() {
    	((BuddyAdapter) PwdDisplayActivity.dbAdapter).updateCategoryList();
    	((BuddyAdapter) PwdDisplayActivity.dbAdapter).updateCategoryItemListAll();
        expandablelistview.setAdapter(dbAdapter); 
    }

    public void onResume() {
    	super.onResume();  
    }

    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
        setContentView(R.layout.pwddislaypage);
        
        tabeventHdlr = new tabEnventHandler(this);
        dbHdlr = tabActivity.dbHdlr; //new dataAccessor(getApplicationContext(), tabActivity.dbPath+tabActivity.dbName);
    	dbAdapter = new BuddyAdapter(this, dbHdlr); 
    	
    	expandablelistview= (ExpandableListView) findViewById(R.id.pwdList); 
        expandablelistview.setAdapter(dbAdapter); 
        expandablelistview.setGroupIndicator(null);
        //分组展开  
        expandablelistview.setOnGroupExpandListener(new OnGroupExpandListener(){ 
            public void onGroupExpand(int groupPosition) { 
            	expandablelistview.scrollTo(0,0);
            } 
        }); 
        //分组关闭  
        expandablelistview.setOnGroupCollapseListener(new OnGroupCollapseListener(){ 
            public void onGroupCollapse(int groupPosition) { 
            } 
        }); 
        
        expandablelistview.setOnItemLongClickListener(new OnItemLongClickListener(){
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				expandablelistview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
						public void onCreateContextMenu(ContextMenu menu,
								View arg1, ContextMenuInfo info) {
							// TODO Auto-generated method stub
                            menu.add(0,0,0,"删除记录");  
						}  
                });  
				return false;
			}
        	
        });
        
        //子项单击  
        expandablelistview.setOnChildClickListener(new OnChildClickListener(){ 
            public boolean onChildClick(ExpandableListView arg0, View arg1, 
                    int groupPosition, int childPosition, long arg4) { 
                Toast.makeText(PwdDisplayActivity.this,   
                		"密码: " + ((BuddyAdapter) dbAdapter).getPwdItem(groupPosition, childPosition).get("pwd").toString(),   
                        Toast.LENGTH_SHORT).show(); 
                return false; 
            }
        }); 
    }

    private void doDeleteSeletecItem(){
        ExpandableListContextMenuInfo info = (ExpandableListContextMenuInfo) currentSelectedItem.getMenuInfo();

        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        //setTitle("点击了长按菜单里面的第"+item.getItemId()+"个项目")
        if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
            int childPos = ExpandableListView.getPackedPositionChild(info.packedPosition); 
            //Log.i(TAG, ": Child " + childPos + " clicked in group " + groupPos);
            int groupId = dbHdlr.getCategoryIndex(dbAdapter.getGroup(groupPos).toString());
            HashMap<String, Object> pwdItem = ((BuddyAdapter) dbAdapter).getPwdItem(groupPos, childPos);
            dbHdlr.removePwdList(groupId, pwdItem.get("desc").toString(), pwdItem.get("account").toString());
            
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            int groupPos = ExpandableListView.getPackedPositionGroup(info.packedPosition); 
            //Log.i(TAG, ": Group " + groupPos + " clicked");
            
            int childNum = ((BuddyAdapter) dbAdapter).getChildrenCount(groupPos);

            int groupId = dbHdlr.getCategoryIndex(dbAdapter.getGroup(groupPos).toString());
            //Log.i(TAG,"getCategoryIndex: group " + dbAdapter.getGroup(groupPos).toString() + " is " + groupId);
            for(int i=0;i<childNum;i++){
                HashMap<String, Object> pwdItem = ((BuddyAdapter) dbAdapter).getPwdItem(groupPos, i);
                dbHdlr.removePwdList(groupId, pwdItem.get("desc").toString(), pwdItem.get("account").toString());
            }
            dbHdlr.removeCategory(groupId);
        }

		pwdConfigActivity.need_update_data = true;
        expandablelistviewUpdate();
    }
    // 长按菜单响应函数  
    public boolean onContextItemSelected(MenuItem item) {
            currentSelectedItem = item;

			new AlertDialog.Builder(this) 
			.setTitle("确认")
			.setMessage("记录删除将无法恢复，确认吗？")
			.setPositiveButton("是",  new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int whichButton)
                {
    				doDeleteSeletecItem();
    				currentSelectedItem = null;
                }
    		})
			.setNegativeButton("否", new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int whichButton)
                {
    				currentSelectedItem = null;
                }
    		})
			.show();
			
            return super.onContextItemSelected(item);  
    }  

}
