package me.jfenn.wakeMeUp.receivers;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import me.jfenn.wakeMeUp.Alarmio;
import me.jfenn.wakeMeUp.data.AlarmData;
import me.jfenn.wakeMeUp.data.TimerData;

public class RestoreOnBootReceiver extends BroadcastReceiver {

    private static final String TAG = "RestoreOnBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"onReceive");
          Alarmio alarmio = (Alarmio) context.getApplicationContext();
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Toast.makeText(context, TAG, Toast.LENGTH_LONG);
            for (AlarmData alarm : alarmio.getAlarms()) {
                if (alarm.isEnabled)
                    alarm.set(context, manager);
            }

            for (TimerData timer : alarmio.getTimers()) {
                if (timer.getRemainingMillis() > 0)
                    timer.setAlarm(context, manager);
            }

    }
}
