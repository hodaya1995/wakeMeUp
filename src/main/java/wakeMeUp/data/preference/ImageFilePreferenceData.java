package me.jfenn.wakeMeUp.data.preference;

import android.content.Intent;

import me.jfenn.wakeMeUp.activities.FileChooserActivity;
import me.jfenn.wakeMeUp.data.PreferenceData;

public class ImageFilePreferenceData extends CustomPreferenceData {

    private PreferenceData preference;

    public ImageFilePreferenceData(PreferenceData preference, int name) {
        super(name);
        this.preference = preference;
    }

    @Override
    public String getValueName(ViewHolder holder) {
        return "";
    }

    @Override
    public void onClick(final ViewHolder holder) {
        Intent intent = new Intent(holder.getContext(), FileChooserActivity.class);
        intent.putExtra(FileChooserActivity.EXTRA_PREFERENCE, preference);
        intent.putExtra(FileChooserActivity.EXTRA_TYPE, "image/*");
        holder.getContext().startActivity(intent);
    }
}
