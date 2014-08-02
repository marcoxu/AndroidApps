package com.marco.smsrouter.service;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;


public class BootReceiver extends BroadcastReceiver {
	private static final String TAG = "smsRouter.BootReceiver";
    private PendingIntent mAlarmSender;

    @Override

    public void onReceive(Context context, Intent intent) {
        // �����������ɵ��£�����һ��Service��Activity�ȣ�������������һ����ʱ���ȳ���ÿ30��������һ��Serviceȥ��������
        mAlarmSender = PendingIntent.getService(context, 0, new Intent(context, smsRteService.class), 0);
        long firstTime = SystemClock.elapsedRealtime();

        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
  		Log.i(TAG, "BootReceiver onReceive");

        am.cancel(mAlarmSender);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,  30 * 60 * 1000, mAlarmSender);
    }

}
