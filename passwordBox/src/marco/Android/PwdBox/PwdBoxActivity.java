package marco.Android.PwdBox;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class PwdBoxActivity extends Activity {
    private static final String TAG = "marco.Android.PwdBox";
    private static final String AppId = "909";
    private static loginEnventHandler eventHandler = null;
    private String clientId = "0trswTLaGB6hN820M30Brbhx";
    private String clientSecret = "EAm5zdHvGkQiD4E8tbu7Tqrk8Esrfy90";

    public static String SessionId = null;
    public static String UserId = null;
    public static String DeviceId = null;
    public static boolean skip_auth = true;
    public static final int EVENT_LOGIN_DONE = 0;

    //private Baidu baidu = null;

    private void startSndaLogin(){
    	try {
    		// 登陆盛大通行证
    		//OpenAPI.pwdLogin(PwdBoxActivity.this, AppId, PwdBoxActivity.this, null);
    	} catch (Exception e) {
    		Log.i(TAG, "盛大通行证登录错误： ", e);
    	}
    }

	class loginEnventHandler extends Handler {
		private Activity mAct = null;
		public loginEnventHandler(Activity act) {
			super();
			mAct = act;
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {			
			case EVENT_LOGIN_DONE:
				//登录成功
	            Intent startAct = new Intent(getApplicationContext(), tabActivity.class);	
	            startAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            getApplicationContext().startActivity(startAct);
	            if(mAct != null){
	            	mAct.finish();
	            }
                break;
			}
		}
	}
    
    
    /** Called when the activity is first created. */
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);// 填充标题栏
        setContentView(R.layout.main);

        eventHandler = new loginEnventHandler(this);
        Button regBtn = (Button) findViewById(R.id.btnRegister);
        Button loginBtn = (Button) findViewById(R.id.btnFastLogin);
    
        //按钮Click事件监听
        regBtn.setOnClickListener(new ButtonClickListener());
        loginBtn.setOnClickListener(new ButtonClickListener());

    }

    private void showTokenInfo() {
        Intent intent = new Intent(PwdBoxActivity.this,TabActivity.class);
        //intent.putExtra("baidu", baidu);
        startActivity(intent);
    }

    private void logInSuccess() {
        // 登陆成功
        //SessionId = OpenAPI.getSessionId();
        //UserId = OpenAPI.getUserId();
        //DeviceId = OpenAPI.getDeviceId();
        Message msg = new Message();
        msg.what = PwdBoxActivity.EVENT_LOGIN_DONE;
        PwdBoxActivity.eventHandler.removeMessages(PwdBoxActivity.EVENT_LOGIN_DONE);
        PwdBoxActivity.eventHandler.sendMessage(msg);
    }
    
	public void doCallBack() {
        //if (OpenAPI.getStatus() == 0) {
           
            // 登陆成功
		    logInSuccess();
        //}else{
        //	if(OpenAPI.getStatus() == -10801017 || OpenAPI.getStatus() == -10801304){
        //	}else if(OpenAPI.getStatus() != -10801001){
        //        startSndaLogin();
        //	}
        //}
		
	}

    //监听处理按钮点击事件
    private class ButtonClickListener implements OnClickListener{
    	public synchronized void onClick(View v) {
    		switch(v.getId()){
    		case R.id.btnRegister:
                break;
    		case R.id.btnFastLogin:
                Log.i(TAG, " btnFastLogin is onclick!");
    	        //baidu = new Baidu(clientId, clientSecret, PwdBoxActivity.this);
                Log.i(TAG, " btnFastLogin new baidu!");
                Log.i(TAG, " btnFastLogin baidu logout done!");
    	        //baidu.authorize(PwdBoxActivity.this, new BaiduDialogListener() {

    	        //    @Override
    	        //    public void onComplete(Bundle values) {
    	        //        showTokenInfo();
    	        //    }

    	        //    @Override
    	        //    public void onBaiduException(BaiduException e) {

    	        //    }

    	        //    @Override
    	        //    public void onError(BaiduDialogError e) {

    	        //    }

    	        //    @Override
    	        //    public void onCancel() {

    	        //    }
    	        //});
                Log.i(TAG, " btnFastLogin baidu.authorize done!");

    			if(skip_auth){
    			    logInSuccess();
    			} else {
                    // 初始化无线OA
        	        //OpenAPI.init(PwdBoxActivity.this, AppId, "channel_id", "product_id");
        	        Toast.makeText(PwdBoxActivity.this, "初始化无线OA", Toast.LENGTH_SHORT).show();

        	        try {
        	            // 检查可用网络
        	            ConnectivityManager connectivityManager = (ConnectivityManager) PwdBoxActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        	            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
    	                //Log.i(TAG, " connectivityManager.getActiveNetworkInfo Done!");
        	            if(activeNetInfo == null){
        	                Log.i(TAG, "无可用网络 ");
        	                Toast.makeText(PwdBoxActivity.this, "当前无可用网络", Toast.LENGTH_SHORT).show();
        	                
        	    			new AlertDialog.Builder(PwdBoxActivity.this) 
        	    			.setMessage("请检查当前网络连接！")
        	    			.setPositiveButton("确定",  null)
        	    			.show();
        	                
        	                return;
        	            }else if(activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI){
        	                Log.i(TAG, "可用网络: WIFI");
        	            }else if(activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
        	                TelephonyManager tm = (TelephonyManager) PwdBoxActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
        	                int type = tm.getNetworkType();
        	                if (type == TelephonyManager.NETWORK_TYPE_UMTS) {
        	                    Log.i(TAG, "可用网络: UMTS");
        	                } else if (type == TelephonyManager.NETWORK_TYPE_GPRS) {
        	                    Log.i(TAG, "可用网络粶: GPRS");
        	                } else if (type == TelephonyManager.NETWORK_TYPE_EDGE) {
        	                    Log.i(TAG, "可用网络: EDGE");
        	                } else {
        	                    Log.i(TAG, "可用网络: UIM");
        	                }
        	            }
        	        } catch (Exception e) {
        	            Log.i(TAG, "无可用网络 ", e);
        	            Toast.makeText(PwdBoxActivity.this, "当前无可用网络", Toast.LENGTH_SHORT).show();
    	    			new AlertDialog.Builder(PwdBoxActivity.this) 
    	    			.setMessage("请检查当前网络连接！")
    	    			.setPositiveButton("确定",  null)
    	    			.show();
        	            return;
        	        }

        	        // 登陆盛大通行证
        	        startSndaLogin();  
                }
    	}
    	}
    }
}