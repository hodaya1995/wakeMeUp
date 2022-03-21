package me.jfenn.wakeMeUp.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.afollestad.aesthetic.Aesthetic;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.functions.Consumer;
import me.jfenn.wakeMeUp.Alarmio;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.adapters.SimplePagerAdapter;
import me.jfenn.wakeMeUp.data.SoundData;
import me.jfenn.wakeMeUp.fragments.AlarmMusicChooserFragment;
import me.jfenn.wakeMeUp.fragments.AlarmSoundChooserFragment;
import me.jfenn.wakeMeUp.fragments.RingtoneSoundChooserFragment;
import me.jfenn.wakeMeUp.interfaces.SoundChooserListener;
/*The origin code have been changed */
public class SoundChooserDialog extends DialogFragment implements SoundChooserListener {

    private SoundChooserListener listener;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                if (params != null) {
                    params.windowAnimations = R.style.SlideDialogAnimation;
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(R.layout.dialog_sound_chooser, container, false);

        Aesthetic.Companion.get()
                .colorPrimary()
                .take(1)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        view.setBackgroundColor(integer);
                    }
                });

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.viewPager);

        AlarmSoundChooserFragment alarmFragment = new AlarmSoundChooserFragment();
        RingtoneSoundChooserFragment ringtoneFragment = new RingtoneSoundChooserFragment();

        //TODO ADD SOME SOUNDS
        AlarmMusicChooserFragment musicFragment=new AlarmMusicChooserFragment();
        AlarmLoudChooserFragment loudFragment=new AlarmLoudChooserFragment();

        //RadioSoundChooserFragment radioFragment = new RadioSoundChooserFragment();

        alarmFragment.setListener(this);
        ringtoneFragment.setListener(this);
        loudFragment.setListener(this);
        musicFragment.setListener(this);
        //radioFragment.setListener(this);
        SimplePagerAdapter pagerAdapter=new SimplePagerAdapter(getContext(), getChildFragmentManager(),musicFragment,loudFragment, alarmFragment, ringtoneFragment);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {

                getActivity().invalidateOptionsMenu();
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        invalidateOptionsMenu(0,pagerAdapter);
        getActivity().invalidateOptionsMenu();
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    private void invalidateOptionsMenu(int position,SimplePagerAdapter pagerAdapter) {
            Fragment fragment = pagerAdapter.getItem(2);
            fragment.setHasOptionsMenu(true);
    }

    public void setListener(SoundChooserListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSoundChosen(SoundData sound) {
        if (listener != null)
            listener.onSoundChosen(sound);

        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (view != null)
            ((Alarmio) view.getContext().getApplicationContext()).stopCurrentSound();
    }

}
