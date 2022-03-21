package me.jfenn.wakeMeUp.data.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import me.jfenn.wakeMeUp.data.PreferenceData;
import me.jfenn.wakeMeUp.dialogs.TimeChooserDialog;
import me.jfenn.wakeMeUp.utils.FormatUtils;

public class TimePreferenceData extends CustomPreferenceData {

    private PreferenceData preference;

    public TimePreferenceData(PreferenceData preference, int name) {
        super(name);
        this.preference = preference;
    }

    @Override
    public String getValueName(ViewHolder holder) {
        String str = FormatUtils.formatMillis((long) preference.getValue(holder.getContext()),false);
        //return str.substring(0, str.length() - 3);
        return str;
    }

    @Override
    public void onClick(final ViewHolder holder) {
        SharedPreferences pref = holder.getContext().getSharedPreferences("threshold", Context.MODE_PRIVATE);
        boolean snoozed = pref.getBoolean("snoozed", false);

        if (!snoozed) {
            int seconds = (int) TimeUnit.MILLISECONDS.toSeconds((long) preference.getValue(holder.getContext()));
            int minutes = (int) TimeUnit.SECONDS.toMinutes(seconds);
            int hours = (int) TimeUnit.MINUTES.toHours(minutes);
            minutes %= TimeUnit.HOURS.toMinutes(1);
            seconds %= TimeUnit.MINUTES.toSeconds(1);

            TimeChooserDialog dialog = new TimeChooserDialog(holder.getContext());
            dialog.setDefault(hours, minutes, seconds);
            dialog.setListener(new TimeChooserDialog.OnTimeChosenListener() {
                @Override
                public void onTimeChosen(int minutes) {
                    //seconds += TimeUnit.HOURS.toSeconds(hours);
                    //seconds += TimeUnit.MINUTES.toSeconds(minutes);
                    //preference.setValue(holder.getContext(), TimeUnit.SECONDS.toMillis(seconds));
                    if (minutes > 59) minutes = 59;
                    preference.setValue(holder.getContext(), TimeUnit.MINUTES.toMillis(minutes));

                    bindViewHolder(holder);
                }
            });
            dialog.show();
        }else{
            Toast.makeText(holder.getContext(),"edit prefernces is not available",Toast.LENGTH_LONG).show();
        }
    }

}
