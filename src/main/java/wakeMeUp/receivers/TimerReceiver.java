package me.jfenn.wakeMeUp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import me.jfenn.wakeMeUp.Alarmio;
import me.jfenn.wakeMeUp.activities.MainActivity;
import me.jfenn.wakeMeUp.activities.TimerActivity;
import me.jfenn.wakeMeUp.data.TimerData;
/*The origin code have been changed */
public class TimerReceiver extends BroadcastReceiver {

    public static final String EXTRA_TIMER_ID = "james.alarmio.EXTRA_TIMER_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        Alarmio alarmio = (Alarmio) context.getApplicationContext();
        Log.d(MainActivity.TAG,"timers size "+alarmio.getTimers().size());

        TimerData timer = alarmio.getTimers().get(intent.getIntExtra(EXTRA_TIMER_ID, 0));
        alarmio.removeTimer(timer);

        Intent ringer = new Intent(context, TimerActivity.class);
        ringer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ringer.putExtra(TimerActivity.EXTRA_TIMER, timer);
        context.startActivity(ringer);
    }
}
