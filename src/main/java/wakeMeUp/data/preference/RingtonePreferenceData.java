package me.jfenn.wakeMeUp.data.preference;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.data.PreferenceData;
import me.jfenn.wakeMeUp.data.SoundData;
import me.jfenn.wakeMeUp.dialogs.SoundChooserDialog;
import me.jfenn.wakeMeUp.interfaces.SoundChooserListener;

public class RingtonePreferenceData extends CustomPreferenceData {

    private PreferenceData preference;

    public RingtonePreferenceData(PreferenceData preference, int name) {
        super(name);
        this.preference = preference;
    }

    @Override
    public String getValueName(ViewHolder holder) {
        String sound = preference.getValue(holder.getContext().getApplicationContext(), "");
        Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(holder.getContext().getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
        Ringtone defaultRingtone = RingtoneManager.getRingtone(holder.getContext(), defaultRingtoneUri);
        SoundData soundData = new SoundData(defaultRingtone.getTitle(holder.getContext()),defaultRingtoneUri.toString(),defaultRingtone);

        return sound != null && sound.length() > 0 ? SoundData.fromString(sound).getName() : soundData.getName();//holder.getContext().getString(R.string.title_sound_none);
    }

    @Override
    public void onClick(final ViewHolder holder) {
        SoundChooserDialog dialog = new SoundChooserDialog();
        dialog.setListener(new SoundChooserListener() {
            @Override
            public void onSoundChosen(SoundData sound) {
                preference.setValue(holder.getContext(), sound != null ? sound.toString() : null);
                bindViewHolder(holder);
            }
        });
        dialog.show(holder.getAlarmio().getFragmentManager(), null);
    }
}
