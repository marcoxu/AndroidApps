package marco.Android.PwdBox;

import marco.Android.PwdBox.pwdConfigActivity.taConfbEventHandler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class PwdAppConfigActivity extends PreferenceActivity{
	private static final String TAG = "marco.Android.PwdBox.PwdAppConfigActivity";
	public static int DIALOG_ABOUT = 0;
	public static int DIALOG_EMAIL = 1;

    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	//requestWindowFeature(Window.FEATURE_NO_TITLE);// ÃÓ≥‰±ÍÃ‚¿∏
        //setContentView(R.layout.pwd_app_conf);

    	addPreferencesFromResource(R.xml.config_preference);

        //Try and find app version number
        String version;
        PackageManager pm = this.getPackageManager();
        try {
          //Get version number, not sure if there is a better way to do this
          version = " v" +
          pm.getPackageInfo(
        		  PwdAppConfigActivity.class.getPackage().getName(), 0).versionName;
        } catch (NameNotFoundException e) {
          version = "";
        }

        final PreferenceScreen pagePref =
      	      (PreferenceScreen) findPreference(getString(R.string.config_page_key));
        pagePref.setTitle(getString(R.string.app_name) + version);
        
        final DialogPreference aboutPref =
        	      (DialogPreference) findPreference(getString(R.string.config_about_key));
        aboutPref.setDialogType(DIALOG_ABOUT);
	    aboutPref.setDialogTitle(getString(R.string.app_name) + version);
	    aboutPref.setDialogLayoutResource(R.layout.about);

        final DialogPreference emailPref =
         	      (DialogPreference) findPreference(getString(R.string.config_email_key));
        emailPref.setDialogType(DIALOG_EMAIL);
        emailPref.setDialogTitle(getString(R.string.app_name) + version);
        emailPref.setDialogLayoutResource(R.layout.email);
        emailPref.setNegativeButtonText("∑Ò");
        emailPref.setPositiveButtonText(" «");
}

}
