package me.jfenn.wakeMeUp.dialogs;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.adapters.SoundsAdapter;
import me.jfenn.wakeMeUp.data.SoundData;
import me.jfenn.wakeMeUp.fragments.BaseSoundChooserFragment;

public class AlarmLoudChooserFragment extends BaseSoundChooserFragment {

    private SoundsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sound_chooser_list_regular, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<SoundData> sounds = new ArrayList<>();
        Uri s1=Uri.parse("android.resource://"+getContext().getPackageName()+"/raw/s1");
        Uri s2=Uri.parse("android.resource://"+getContext().getPackageName()+"/raw/s2");
        Uri s3=Uri.parse("android.resource://"+getContext().getPackageName()+"/raw/s3");
        Uri s4=Uri.parse("android.resource://"+getContext().getPackageName()+"/raw/s4");
        Uri s5=Uri.parse("android.resource://"+getContext().getPackageName()+"/raw/s5");
        Uri s7=Uri.parse("android.resource://"+getContext().getPackageName()+"/raw/s7");
        Uri s8=Uri.parse("android.resource://"+getContext().getPackageName()+"/raw/s8");
        Uri s10=Uri.parse("android.resource://"+getContext().getPackageName()+"/raw/s10");
        Uri s11=Uri.parse("android.resource://"+getContext().getPackageName()+"/raw/s11");
        Uri s12=Uri.parse("android.resource://"+getContext().getPackageName()+"/raw/s12");

        sounds.add(new SoundData("loud ringtone 1", s1.toString()));
        sounds.add(new SoundData("loud ringtone 2", s2.toString()));
        sounds.add(new SoundData("loud ringtone 3", s3.toString()));
        sounds.add(new SoundData("loud ringtone 4", s4.toString()));
        sounds.add(new SoundData("loud ringtone 5", s5.toString()));
         sounds.add(new SoundData("loud ringtone 6", s7.toString()));
        sounds.add(new SoundData("loud ringtone 7", s8.toString()));
        sounds.add(new SoundData("loud ringtone 8", s10.toString()));
        sounds.add(new SoundData("loud ringtone 9", s11.toString()));
        sounds.add(new SoundData("loud ringtone 10", s12.toString()));

        adapter = new SoundsAdapter(getAlarmio(), sounds);
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.title_loud_alarms);
    }

}
