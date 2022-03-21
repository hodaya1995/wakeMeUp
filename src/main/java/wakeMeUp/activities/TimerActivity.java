package me.jfenn.wakeMeUp.activities;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.aesthetic.AestheticActivity;

import androidx.annotation.Nullable;
import me.jfenn.wakeMeUp.Alarmio;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.data.PreferenceData;
import me.jfenn.wakeMeUp.data.SoundData;
import me.jfenn.wakeMeUp.data.TimerData;
import me.jfenn.wakeMeUp.views.ProgressTextView;
/*The origin code have been changed */
public class TimerActivity extends AestheticActivity {
    public static final String EXTRA_TIMER = "james.alarmio.AlarmActivity.EXTRA_TIMER";
    private TimerData timer;
    private boolean isVibrate;
    private SoundData sound;
    private int shutUpRingAfter;
    private Handler handler;
    Alarmio alarmio;
    private Vibrator vibrator;
    private Runnable runnable;
    private boolean isRunning,stopHandler;
    private ProgressTextView timerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);


        alarmio=((Alarmio) this.getApplicationContext());

        if (getIntent().hasExtra(EXTRA_TIMER)) {
            timer = getIntent().getParcelableExtra(EXTRA_TIMER);
            isVibrate = timer.isVibrate;
            if (timer.hasSound())
                sound = timer.getSound();
        } else finish();

        shutUpRingAfter=(int)(((long) PreferenceData.RING_DURATION.getValue(this))/60000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(MainActivity.TAG,"Handler");
                stopAnnoyingness();
            }
        }, shutUpRingAfter*60*1000);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                 if (isVibrate) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    else vibrator.vibrate(500);
                }

                if (sound != null && !sound.isPlaying(alarmio)) {
                    sound.play(alarmio);
                }

                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);
        if (sound != null) sound.play(alarmio);


        timerView=(ProgressTextView)findViewById(R.id.timer);
        timerView.setText("Dismiss");
        timerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TimerActivity.this,"Timer dismissed",Toast.LENGTH_LONG).show();
                stopHandler = true;
                stopAnnoyingness();
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopHandler = true;
        stopAnnoyingness();
    }


    @Override
    protected void onDestroy() {
        stopHandler=true;
        stopAnnoyingness();
        isRunning = false;
        super.onDestroy();
    }


    private void stopAnnoyingness() {
        if (handler != null) handler.removeCallbacks(runnable);
        if (sound != null && sound.isPlaying(alarmio)) sound.stop(alarmio);

    }













    }
