package me.jfenn.wakeMeUp.data.preference;

import android.content.DialogInterface;

import java.util.Locale;
import java.util.TimeZone;

import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.data.PreferenceData;
import me.jfenn.wakeMeUp.dialogs.TimeZoneChooserDialog;

public class TimeZonesPreferenceData extends CustomPreferenceData {

    private PreferenceData preference;

    public TimeZonesPreferenceData(PreferenceData preference, int title) {
        super(title);
        this.preference = preference;
    }

    @Override
    public String getValueName(ViewHolder holder) {
        int i = 0;
        for (String id : TimeZone.getAvailableIDs()) {
            if (preference.getSpecificValue(holder.getContext(), id))
                i++;
        }

        return String.format(Locale.getDefault(), holder.getContext().getString(R.string.msg_time_zones_selected), i);
    }

    @Override
    public void onClick(final ViewHolder holder) {
        TimeZoneChooserDialog dialog = new TimeZoneChooserDialog(holder.getContext());
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bindViewHolder(holder);
            }
        });
        dialog.show();
    }
}
