package me.jfenn.wakeMeUp.activities;

import android.app.backup.BackupManager;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.afollestad.aesthetic.AestheticActivity;

import cdflynn.android.library.checkview.CheckView;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.walk.Threshold;

public class CalibrationActivity  extends AestheticActivity {

     private Threshold threshold;
    private CheckView checkView;
    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        threshold = new Threshold(this, this);



    }


    @Override
    protected void onResume() {
        super.onResume();
        if(threshold!=null)threshold.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(threshold!=null)threshold.onStart();

    }

    @Override
    protected void onPause() {
        if(threshold!=null)threshold.onPause();
       super.onPause();

    }

    @Override
    protected void onStop() {
        if(threshold!=null)threshold.onStop();
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        if(threshold!=null)threshold.onStop();
        prefs = getSharedPreferences("threshold", MODE_PRIVATE );
        if(Double.longBitsToDouble(prefs.getLong("min", 0))!=0)   new BackupManager(this).dataChanged();
        super.onDestroy();
    }
}