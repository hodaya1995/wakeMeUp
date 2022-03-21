package me.jfenn.wakeMeUp.fragments;

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
/*The origin code have been changed */
public class AlarmMusicChooserFragment extends BaseSoundChooserFragment implements SearchView.OnQueryTextListener {


    private static final String TAG ="MusicChooserFragment" ;
    RecyclerView recyclerView;
    public SoundsAdapter adapter;
    private List<SoundData> sounds;
    private SearchView searchView;
    private ArrayList<SoundData> soundsCopy;
    int prev=-1;

    @Override
    public boolean onQueryTextChange(String query) {
        Log.d(TAG,"onQueryTextChange");
        if(prev>query.length()) sounds=new ArrayList<>(soundsCopy);
        prev=query.length();
        this.filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG,"onQueryTextSubmit");
        this.filter(query);
        clearFocus();
        return true;
    }

    private void clearFocus() {
        if(searchView!=null){
            searchView.clearFocus();
            searchView.onActionViewCollapsed();
        }
       else   Log.d(TAG,"searchView==null");

    }

    public void filter(String text) {
        List<SoundData>itemsCopy=sounds;
        if(itemsCopy!=null) {
            Log.d(TAG, "items copy size: " + itemsCopy.size());
            Log.d(TAG, "text: " + text);

            if (text.isEmpty()) {
                Log.d(TAG, "text.isEmpty() ");

                adapter = new SoundsAdapter(getAlarmio(), itemsCopy);
                adapter.setListener(this);
                recyclerView.setAdapter(adapter);
                sounds = itemsCopy;
            } else {
                Log.d(TAG, "text.is not Empty() ");

                text = text.toLowerCase();
                Log.d(TAG, "items copy size: " + itemsCopy.size());
                List<SoundData> soundsTemp = new ArrayList<>();

                for (SoundData item : itemsCopy) {
                    if (item.getName().toLowerCase().contains(text)) {
                        soundsTemp.add(item);
                    }
                }
                sounds.clear();
                sounds.addAll(0, soundsTemp);
                adapter = new SoundsAdapter(getAlarmio(), itemsCopy);
                adapter.setListener(this);
                recyclerView.setAdapter(adapter);

            }
            notifyDataSetChanged();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       super.onCreateView(inflater,container,savedInstanceState);
        Log.d(TAG,"onCreateView");
         View view = inflater.inflate(R.layout.fragment_sound_chooser_list, container, false);
        SearchView searchView =  view.findViewById(R.id.searchView);
        searchView.setIconified(true);
        this.searchView=searchView;
        searchView.setOnQueryTextListener(this);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1234);
        } else {
          readMusic();
        }


        return view;
    }


    @Override
    public String getTitle(Context context) {
        return "music";
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case 1234:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    readMusic();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void readMusic() {
        sounds = new ArrayList<>();
        RingtoneManager ringtoneManager=new RingtoneManager(getContext());
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = contentResolver.query(songUri, null, selection, null, sortOrder);
        int count = cursor.getCount();
        if (count > 0 && cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String url=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                SoundData sound=new SoundData(name, url);
                sounds.add(sound);
            } while (cursor.moveToNext());
        }
        soundsCopy=new ArrayList<>(sounds);

        adapter = new SoundsAdapter(getAlarmio(), sounds);
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);
    }

}
