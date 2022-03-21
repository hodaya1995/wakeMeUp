package me.jfenn.wakeMeUp.receivers;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import me.jfenn.wakeMeUp.Alarmio;
import me.jfenn.wakeMeUp.activities.AlarmActivity;
import me.jfenn.wakeMeUp.data.AlarmData;
/*The origin code have been changed */

public class AlarmReceiver extends BroadcastReceiver {

    public static final String EXTRA_ALARM_ID = "james.alarmio.EXTRA_ALARM_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Alarmio alarmio = (Alarmio) context.getApplicationContext();
        AlarmData alarm = alarmio.getUnsortedAlarms().get(intent.getIntExtra(EXTRA_ALARM_ID, 0));
        Log.d("AddPhotoBottom","alarm reciever: "+intent.getIntExtra(EXTRA_ALARM_ID, 0));
        if (alarm.isRepeat()) alarm.set(context, manager);
        /*else{
            alarm.setEnabled(alarmio, manager, true);
            //alarm.setEnabled(alarmio, manager, false);
        }*/
        alarmio.onAlarmsChanged();

        Intent ringer = new Intent(context, AlarmActivity.class);
        ringer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ringer.putExtra(AlarmActivity.EXTRA_ALARM, alarm);
        context.startActivity(ringer);
    }
}
