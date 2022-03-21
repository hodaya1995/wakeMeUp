package me.jfenn.wakeMeUp.fragments;

import me.jfenn.wakeMeUp.data.SoundData;
import me.jfenn.wakeMeUp.interfaces.SoundChooserListener;

public abstract class BaseSoundChooserFragment extends BasePagerFragment implements SoundChooserListener {

    private SoundChooserListener listener;

    public void setListener(SoundChooserListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSoundChosen(SoundData sound) {
        if (listener != null)
            listener.onSoundChosen(sound);
    }
}
