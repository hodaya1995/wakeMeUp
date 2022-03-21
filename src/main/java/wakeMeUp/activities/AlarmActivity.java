package me.jfenn.wakeMeUp.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.backup.BackupManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.aesthetic.Aesthetic;
import com.afollestad.aesthetic.AestheticActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.jfenn.wakeMeUp.Alarmio;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.adapters.AlarmsAdapter;
import me.jfenn.wakeMeUp.backup.NetworkBroadcastReceiver;
import me.jfenn.wakeMeUp.data.AlarmData;
import me.jfenn.wakeMeUp.data.PreferenceData;
import me.jfenn.wakeMeUp.data.SoundData;
import me.jfenn.wakeMeUp.fragments.AlarmsFragment;
import me.jfenn.wakeMeUp.utils.FormatUtils;
import me.jfenn.wakeMeUp.views.ProgressTextViewSmall;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static me.jfenn.wakeMeUp.receivers.AlarmReceiver.EXTRA_ALARM_ID;
/*The origin code have been changed */
public class AlarmActivity extends AestheticActivity {
    private static AlarmActivity alarmActivityRunningInstance;
     private int snoozeDuration;
     private int shutUpRingAfter;
     private boolean isSlowWakeUp;
     private boolean useInBuiltInSpeaker,overrideSystemVolume;
     private int SNOOZE_TIME;
     private boolean stop;
     private double money;
     private boolean isAllowedToShutUpWithButtons;
     private boolean preview;
     private boolean dismissClicked;
     private boolean snooze;
     private int timeToWakeTemp;
     private boolean stopHandler=false;
     private NetworkBroadcastReceiver broadcast;
    private InterstitialAd mInterstitialAd;
    private TextView textClock;
    private ImageView muteImage,snoozeImage,ttwImage,moneyImage;
    private int textColorPrimary;
    private AlarmData tempAlarm;
    private ImageView alarmView;
    private boolean firstTurnLeft,firstTurnRight=true;
    private boolean doneRight;
    private int counter;



String TAG="HomeFragment";
    public static final String EXTRA_ALARM = "james.alarmio.AlarmActivity.EXTRA_ALARM";
     public static final String FIRST_ALARM = "james.alarmio.AlarmActivity.FIRST_ALARM";


     private TextView date;
      private Alarmio alarmio;
    private Vibrator vibrator;

     private Disposable textColorPrimarySubscription;

     private boolean isAlarm;
    private long triggerMillis;
    private AlarmData alarm;
    private SoundData sound;
    private boolean isVibrate;

    private boolean isSlowWake;
    private long slowWakeMillis;

    private Handler handler;
    private Runnable runnable;
    private boolean isWoken;
    private PowerManager.WakeLock wakeLock;
    private Disposable textColorPrimaryInverseSubscription;
    private Disposable isDarkSubscription;
    private boolean isDark;
     Context context=this;
     boolean startWalk=true;
     //Button purchase;
     SharedPreferences preferences;

     private long endTime;
     private ProgressTextViewSmall time;
     private Handler handler2;
     private Runnable runnable2;
     private boolean isRunning = true;
      private boolean timeIsNull;
     private int timeToWake;
     private Handler handler3;
     private Runnable runnable3;
     private boolean isPunished;
    private Animation animClockVibrate;
    private Handler alarmRingHandler;
    private View background;
    private Animation animMoveToTop;
    private ImageView sun,moon;
    private FrameLayout sunMoon;


    public static AlarmActivity  getInstace(){
        return alarmActivityRunningInstance;
    }
     @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_alarm_dismiss);
         alarmio = (Alarmio) getApplicationContext();

         sun=(ImageView) findViewById(R.id.sun_alarm_activity);
         moon=(ImageView) findViewById(R.id.moon_alarm_activity);
         sunMoon=(FrameLayout) findViewById(R.id.sunMoon_alarm_activity);



         background=(View)findViewById(R.id.background_alarm_activity);
         animMoveToTop = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.move);
         if(!this.alarmio.isNight()) {
             background.setBackgroundColor(Color.parseColor("#91fcff"));
             sun.setVisibility(VISIBLE);
             moon.setVisibility(GONE);
             sunMoon.startAnimation(animMoveToTop);
         }else{
             background.setBackgroundColor(Color.parseColor("#070B34"));
             moon.setAlpha(0f);
             moon.setVisibility(VISIBLE);
             sun.setVisibility(GONE);
             moon.animate().alpha(1.0f).setDuration(3000);
         }

         textColorPrimarySubscription = Aesthetic.Companion.get()
                 .textColorPrimary()
                 .subscribe(new Consumer<Integer>() {
                     @Override
                     public void accept(Integer integer) throws Exception {
                         AlarmActivity.this.setTextColorPrimary(integer);

                     }
                 });

         MobileAds.initialize(this, "ca-app-pub-1242332276700800~2357749034");
         mInterstitialAd = new InterstitialAd(this);
         mInterstitialAd.setAdUnitId("ca-app-pub-1242332276700800/6398734698");//ca-app-pub-3940256099942544/1033173712
         mInterstitialAd.loadAd(new AdRequest.Builder().build());


         SharedPreferences p = context.getSharedPreferences("threshold", context.MODE_PRIVATE);
         alarmActivityRunningInstance =this;


         preview= p.getBoolean("preview",false);

         textClock=(TextView)findViewById(R.id.textClock);
         alarmView=(ImageView)findViewById(R.id.alarm_clock_dismiss);


         animClockVibrate = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.move_clock_vibrate);
         alarmView.startAnimation(animClockVibrate);
         animClockVibrate.setAnimationListener(new Animation.AnimationListener() {
             @Override
             public void onAnimationStart(Animation animation) {

             }

             @Override
             public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alarmView.startAnimation(animClockVibrate);

                    }
                },500);
             }

             @Override
             public void onAnimationRepeat(Animation animation) {

             }
         });


         String timeText=Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"+(Calendar.getInstance().get(Calendar.MINUTE)<=9?"0"+(Calendar.getInstance().get(Calendar.MINUTE)):(Calendar.getInstance().get(Calendar.MINUTE)));
         textClock.setText(timeText);
         Handler clockHandler=new Handler();
         clockHandler.postDelayed(new Runnable() {
             @Override
             public void run() {
                 String time=Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+":"+(Calendar.getInstance().get(Calendar.MINUTE)<=9?"0"+(Calendar.getInstance().get(Calendar.MINUTE)):(Calendar.getInstance().get(Calendar.MINUTE)));
                 textClock.setText(time);
                 clockHandler.postDelayed(this,60000);

             }
         },(60-Calendar.getInstance().get(Calendar.SECOND))*1000);



         muteImage=(ImageView)findViewById(R.id.muteImage);
         snoozeImage=(ImageView)findViewById(R.id.snoozeImage);
         moneyImage=(ImageView)findViewById(R.id.moneyImage);
         ttwImage=(ImageView)findViewById(R.id.ttwImage);

         muteImage.setColorFilter(textColorPrimary);
         snoozeImage.setColorFilter(textColorPrimary);
         moneyImage.setColorFilter(textColorPrimary);
         ttwImage.setColorFilter(textColorPrimary);

         SharedPreferences.Editor editor = context.getSharedPreferences("threshold", context.MODE_PRIVATE).edit();
            if(!preview) editor.putBoolean("snoozed", true);
            editor.putBoolean("snoozeClicked",false);
            editor.putBoolean("fromCalibration", false);
            editor.apply();

         preferences = this.getSharedPreferences("bottomSheet", Context.MODE_PRIVATE);
         time = findViewById(R.id.time_progress);

         SharedPreferences pref = AlarmActivity.this.getSharedPreferences("threshold", Context.MODE_PRIVATE);

         double coinVal = Double.longBitsToDouble(preferences.getLong("dollar", Double.doubleToLongBits(0)));
         ((SeekBar) findViewById(R.id.slider)).setThumbOffset(24);
         ((SeekBar) findViewById(R.id.slider)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

             @Override
             public void onStopTrackingTouch(SeekBar seekBar) {
                 if (seekBar.getProgress()<=95){
                     seekBar.setProgress(0);
                     findViewById(R.id.zzz).setVisibility(View.VISIBLE);
                 }
                 if(snooze){
                     stopAnnoyingness();
                     setSnooze();
                 }
             }

             @Override
             public void onStartTrackingTouch(SeekBar seekBar) {


             }

             @Override
             public void onProgressChanged(SeekBar seekBar, int progress,
                                           boolean fromUser) {
                 if (progress > 71) {
                     findViewById(R.id.zzz).setVisibility(View.INVISIBLE);
                 }
                 if (progress > 95) {
                      snooze=true;

                 }

             }
         });


         alarmView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 stop=true;
                 dismissClicked=true;
                 stopAnnoyingness();
                 if(preview){

                     if (mInterstitialAd.isLoaded()) mInterstitialAd.show();
                     goBackWithoutPay(AlarmActivity.this);
                         finish();
                         /*Log.d(MainActivity.TAG,"preview DISMISS CLICKED");
                         Intent intent = new Intent(context, GameActivity.class);
                         intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_TASK_ON_HOME );
                         startActivity(intent);
                         finish();*/
                 }else {
                     if (isPunished) {
                         Intent intent = new Intent(context, GameActivity.class);
                         startActivity(intent);
                         finish();
                     } else {
                         goBackWithoutPay(AlarmActivity.this);
                         finish();
                     }
                 }
             }
         });

          SNOOZE_TIME= (int) (((long)PreferenceData.SNOOZE_DURATION.getValue(this))/60000);
         shutUpRingAfter=(int)(((long)PreferenceData.RING_DURATION.getValue(this))/60000);
         isSlowWakeUp=(boolean)((boolean)PreferenceData.SLOW_WAKE_UP.getValue(this));
         isAllowedToShutUpWithButtons=((boolean)PreferenceData.ALLOW_SHUT_UP_WITH_BUTTONS.getValue(this));



         new Handler().postDelayed(new Runnable() {
             @Override
             public void run() {
                 stopAnnoyingness();
             }
         }, shutUpRingAfter*60*1000);



         isAlarm = getIntent().hasExtra(EXTRA_ALARM);
         if (isAlarm) {
             alarm = getIntent().getParcelableExtra(EXTRA_ALARM);
             SharedPreferences prefNeedToPay = this.getSharedPreferences("threshold", Context.MODE_PRIVATE);
             boolean firstAlarm=false;
             long timeToEnd = prefNeedToPay.getLong("timeToEnd", 0);
             Log.d(TAG,"time to end: "+timeToEnd);
             long now = System.currentTimeMillis();
             if (now > timeToEnd) {
                 SharedPreferences.Editor editorNeedToPay = this.getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
                 editorNeedToPay.putBoolean("isFirst", true);
                 editorNeedToPay.apply();
                 Log.d(TAG,"firstAlarm = true; ");
                 firstAlarm = true;

             }
             if(preview){
                 firstAlarm=true;
             }
             if(firstAlarm) {
                 isPunished=alarm.isPunished();
                 timeToWake=alarm.getTimeToWake();
                 SharedPreferences.Editor editorNeedToPay = this.getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
                 editorNeedToPay.putInt("alarmId", alarm.getId());
                 editorNeedToPay.putInt("moneyToPay", (int)(alarm.getStep()));
                 editorNeedToPay.putLong("timeToEnd", System.currentTimeMillis() + timeToWake * 60 * 1000);
                 editorNeedToPay.apply();

                 if(!preview&&isPunished)setTimeHandler(this,timeToWake, alarm);
             }


             if(!alarm.isRepeat())AlarmsFragment.setNextAlarmNotification(context,alarm.getId(),"",true);
             else   AlarmsFragment.setNextAlarmNotification(context,alarm.getId(),alarm.getNext()!=null? FormatUtils.format(alarm.getNext().getTime(), "d MMMM HH:mm"):"",false);

             AlarmData fAlarm=null;
             int alarmIndex=pref.getInt("alarmId",-1);
             if(alarmIndex!=-1){
                 fAlarm=alarmio.getAlarms().get(alarmIndex);
             }

             if(fAlarm!=null){
                alarm=fAlarm;
             }
             ((TextView) findViewById(R.id.alarm_info)).setText(alarm.getName(this));
             ((TextView) findViewById(R.id.snooze)).setText("" + SNOOZE_TIME + " minutes");
             ((ImageView) findViewById(R.id.muteImage)).setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {

                     stop = true;
                     stopAnnoyingness();
                     (findViewById(R.id.muteImage)).setVisibility(View.GONE);
                 }
             });

             AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
             double prec = alarm.getVolume() * 1.0 / 100;
             final int[] maxVolume = {(int) ((prec) * audioManager.getStreamMaxVolume(audioManager.STREAM_ALARM))};

             if (isSlowWakeUp) {
                 //it increased over 30 seconds
                 final int[] times = {0};
                 double increaseVal = maxVolume[0] *1.0/ 15;

                 Handler handler4 = new Handler();
                 Runnable runnable4 = new Runnable() {
                     @Override
                     public void run() {
                         if (stop) handler4.removeCallbacks(this);
                         if (!stop) {
                             audioManager.setStreamVolume(AudioManager.STREAM_ALARM, (int)(increaseVal * times[0]), 0);

                             if (sound != null && !sound.isPlaying(alarmio)) {
                                 sound.play(alarmio);
                             }
                             times[0]++;
                             if (times[0] <= 15) handler4.postDelayed(this, 2000);
                         }
                     }
                 };
                 handler4.postDelayed(runnable4, 2000);
             } else {
                 audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume[0], 0);

             }


             isVibrate = alarm.isVibrate;
             if (alarm.hasSound()) sound = alarm.getSound();
             //timeToWake = alarm.getTimeToWake();


             ////////////////////////////////////added first alarm
             /*SharedPreferences.Editor editorNeedToPay = this.getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
              editorNeedToPay.putInt("moneyToPay", (int)(alarm.getStep()));
             editorNeedToPay.putInt("alarmId", alarm.getId());
             editorNeedToPay.putLong("timeToEnd", System.currentTimeMillis() + timeToWake * 60 * 1000);
             editorNeedToPay.apply();

             if(!preview)setTimeHandler(timeToWake, alarm);*/
             ////////////////////////////////////////////////////////
             timeToWake=(int)(pref.getLong("timeToEnd",0)-System.currentTimeMillis())/60000>0?(int)((pref.getLong("timeToEnd",0)-System.currentTimeMillis())/60000):alarm.getTimeToWake();
            Log.d(TAG,"minutes: "+timeToWake+" sec: "+(int)(pref.getLong("timeToEnd",0)-System.currentTimeMillis())%60000);
             endTime = System.currentTimeMillis() + timeToWake * 60 * 1000;


             money = alarm.getMoney();
             isPunished=alarm.isPunished();

             //time.setMaxProgress(timeToWake * 60 * 1000, true);
             if(isPunished)time.setMaxProgress(timeToWake * 60 * 1000, true);
             else          time.setVisibility(GONE);
             if(isPunished)((TextView) findViewById(R.id.money)).setText(money + " " + pref.getString("coinSymball",""));
             else{
                 ((TextView) findViewById(R.id.money)).setVisibility(View.GONE);
                 ((ImageView) findViewById(R.id.moneyImage)).setVisibility(View.GONE);
                 ((ImageView) findViewById(R.id.ttwImage)).setVisibility(View.GONE);
             }

             TextView countDownView = findViewById(R.id.count_down);
             if(!isPunished) countDownView.setVisibility(View.GONE);


             else {
                 countDownView.setText(timeToWake + " m to wake");
                 timeToWakeTemp = timeToWake;
                 final int[] timeToWakeSeconds = {60};
                 handler3 = new Handler();
                 runnable3 = new Runnable() {
                     @Override
                     public void run() {
                         if (stopHandler) {
                             handler3.removeCallbacks(this);
                         } else {
                             timeToWakeTemp--;
                             if (timeToWakeTemp <= 0) {
                                 timeToWakeSeconds[0]--;
                                 if (timeToWakeSeconds[0] > 0) {
                                     countDownView.setText(timeToWakeSeconds[0] + " s to wake");
                                     handler3.postDelayed(this, 1000);
                                 } else if (timeToWakeSeconds[0] == 0) {
                                     //Toast.makeText(AlarmActivity.this,"You have not woke up in time, so you need to pay. ",Toast.LENGTH_LONG ).show();
                                     AlarmActivity.this.finish();
                                 }
                             } else {
                                 countDownView.setText(timeToWakeTemp + " m to wake");
                                 handler3.postDelayed(this, 60000);
                             }
                         }

                     }
                 };
                 if (timeToWake == 1) handler3.postDelayed(runnable3, 1000);
                 else handler3.postDelayed(runnable3, 60000);
             }
             /*countDownView.setStartDuration(timeToWake * 60 * 1000);
             countDownView.start();*/

             Long moneyValue=pref.getLong("moneyValue",0);


             }


         vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

         triggerMillis = System.currentTimeMillis();
         handler = new Handler();
         runnable = new Runnable() {
             @Override
             public void run() {
                 long elapsedMillis = System.currentTimeMillis() - triggerMillis;
                 String text = FormatUtils.formatMillis(elapsedMillis,false);
                 if (isVibrate) {
                     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                         vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                     else vibrator.vibrate(500);
                 }

                 if (sound != null && !sound.isPlaying(alarmio)&&!isSlowWakeUp) {
                     sound.play(alarmio);
                 }

                 handler.postDelayed(this, 1000);
             }
         };
         handler.post(runnable);

         if (!isSlowWakeUp&&sound != null) sound.play(alarmio);

             //SleepReminderService.refreshSleepTime(alarmio);
              long timeToEnd = pref.getLong("timeToEnd", 0);

         //time.setMaxProgress(Math.max(endTime - System.currentTimeMillis(), 0));
         if(isPunished) {
             handler2 = new Handler();


             runnable2 = new Runnable() {
                 @Override
                 public void run() {
                     if (isRunning) {
                         time.setProgress(timeToWake * 60 * 1000 - Math.max(endTime - System.currentTimeMillis(), 0));
                         if (timeToEnd - (System.currentTimeMillis() + SNOOZE_TIME * 60 * 1000) < 0) {
                             ((SeekBar) findViewById(R.id.slider)).setVisibility(View.GONE);
                             (findViewById(R.id.zzz)).setVisibility(View.GONE);
                         }
                         handler2.postDelayed(this, 10);

                     }
                 }
             };

             handler2.post(runnable2);

         }
         Handler handler3=new Handler();
         Runnable runnable3 = new Runnable() {
             @Override
             public void run() {
                 if (isRunning) {
                     if(timeToEnd-(System.currentTimeMillis()+SNOOZE_TIME*60*1000)<=0){
                         ((SeekBar) findViewById(R.id.slider)).setVisibility(View.INVISIBLE);
                         (findViewById(R.id.zzz)).setVisibility(View.INVISIBLE);
                         handler3.removeCallbacks(this);
                     }
                     handler3.postDelayed(this, timeToEnd-(System.currentTimeMillis()+SNOOZE_TIME*60*1000));

                 }
             }
         };

         handler3.post(runnable3);


     }

    public void setTextColorPrimary(int colorTextPrimary) {
        this.textColorPrimary = colorTextPrimary;
    }


    private void setTimeHandler(Context context,int timeToWake,AlarmData alarm2) {
         Intent intent = new Intent(getApplicationContext(), CountDownReciever.class);
        intent.putExtra(EXTRA_ALARM_ID,alarm2.getId());
         final PendingIntent pIntent = PendingIntent.getBroadcast(this, CountDownReciever.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
         long firstMillis = System.currentTimeMillis();
         AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, firstMillis+timeToWake*60*1000, pIntent);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            alarmManager.setAlarmClock(
                    new AlarmManager.AlarmClockInfo(firstMillis+timeToWake*60*1000, pIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0)
                    ),
                    pIntent
            );
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
             alarmManager.setExact(AlarmManager.RTC_WAKEUP,firstMillis+timeToWake*60*1000,pIntent);
         else
             alarmManager.set(AlarmManager.RTC_WAKEUP,firstMillis+timeToWake*60*1000,pIntent);


    }





    @Override
     public void onBackPressed() {
         super.onBackPressed();
         stopHandler=true;
         if(isAllowedToShutUpWithButtons)stop=true;
         stopAnnoyingness();
         if(!preview) {
             if(isPunished) {
                 AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
                 alarm.setEnabled(this, manager, true);
                 SharedPreferences.Editor editorNeedToPay = context.getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
                 editorNeedToPay.putBoolean("snoozeClicked", false);

                 editorNeedToPay.apply();
                 if (AlarmActivity.getInstace() != null)
                     AlarmActivity.getInstace().updateActivity(alarm);
             }else{
                 goBackWithoutPay(AlarmActivity.this);

             }
         }else{
             SharedPreferences.Editor editorNeedToPay = AlarmActivity.this.getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
             editorNeedToPay.putBoolean("preview", false);
             editorNeedToPay.putLong("timeToEnd", 0);
             editorNeedToPay.apply();
             if (AlarmActivity.getInstace() != null) AlarmActivity.getInstace().updateActivity(alarm);
         }
     }

    private void goBackWithoutPay(Context context) {
        SharedPreferences.Editor editorNeedToPay=context.getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
        editorNeedToPay.putBoolean("isFirst", true);
        editorNeedToPay.apply();
        SharedPreferences.Editor editor = context.getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
        editor.putBoolean("snoozed", false);
        editor.apply();
        cancelTimeHandler(context);
    }

    private void cancelTimeHandler(Context context) {
        Intent intent = new Intent(context, AlarmActivity.CountDownReciever.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, AlarmActivity.CountDownReciever.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pIntent);
    }

    public static void turnScreenOff(Activity activity){
         WindowManager.LayoutParams params = activity.getWindow().getAttributes();
         params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
         params.screenBrightness = 0;
         activity.getWindow().setAttributes(params);
     }
     public static void exitApp(Activity activity) {
         if (Build.VERSION.SDK_INT >= 16){
             if(Build.VERSION.SDK_INT >= 21){
                 activity.finishAndRemoveTask();
             }else{
                 activity.finishAffinity();
             }
         }

     }

     @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onDestroy() {
         stopHandler=true;

        if(isAllowedToShutUpWithButtons) stop=true;
        stopAnnoyingness();
        isRunning = false;


        super.onDestroy();
        if(preview||!isPunished){

            SharedPreferences.Editor editorNeedToPay=context.getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
           editorNeedToPay.putBoolean("isFirst", true);
            editorNeedToPay.putLong("timeToEnd", 0);
           editorNeedToPay.apply();
        }

    }



     private void stopAnnoyingness() {
         if (handler != null&&stop) handler.removeCallbacks(runnable);

         if (sound != null && sound.isPlaying(alarmio)&&stop) sound.stop(alarmio);



    }

     private void setSnooze() {
         if(isAllowedToShutUpWithButtons) stop=true;
         stopAnnoyingness();
         Toast.makeText(getApplicationContext(),"snoozed for "+SNOOZE_TIME+" minutes",Toast.LENGTH_LONG).show();
        if(!preview) {
             SharedPreferences.Editor editorNeedToPay = AlarmActivity.this.getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
             editorNeedToPay.putBoolean("snoozeClicked", true);
             editorNeedToPay.apply();
             int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
             int mintues = Calendar.getInstance().get(Calendar.MINUTE) + SNOOZE_TIME;
             Alarmio alarmio = (Alarmio) getApplicationContext();
             AlarmManager manager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
             alarm.time.set(Calendar.HOUR_OF_DAY, hour);
             alarm.time.set(Calendar.MINUTE, mintues);
             alarm.setTime(alarmio, manager, alarm.time.getTimeInMillis());
             alarm.setEnabled(this, manager, true);
             alarm.setMoney(this, manager, money);
             alarm.setTimeToWake(this, manager, timeToWake - SNOOZE_TIME);
             alarm.setVisibilty(this, manager, true);
             AlarmsAdapter.onSnooze(AlarmActivity.this,SNOOZE_TIME);
             alarmio.onAlarmsChanged();
             if(AlarmActivity.getInstace()!=null) AlarmActivity.getInstace().updateActivity(alarm);
         }else{
             finish();
         }



     }



    @Override
    protected void onNewIntent(Intent intent) {
        finish();
        startActivity(new Intent(intent));
    }



     public static class CountDownReciever  extends BroadcastReceiver {
         public static final int REQUEST_CODE = 1500;



         @Override
         public void onReceive(Context context, Intent intent) {
             SharedPreferences prefNeedToPay=context.getSharedPreferences("threshold", Context.MODE_PRIVATE);
             int money=prefNeedToPay.getInt("moneyToPay",0);

             int alarmId=prefNeedToPay.getInt("alarmId",-1);

             SharedPreferences.Editor editorNeedToPay=context.getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
             editorNeedToPay.putInt("alarmId", -1);
             editorNeedToPay.putBoolean("isFirst", true);
             editorNeedToPay.putLong("timeToEnd", 0);
             editorNeedToPay.apply();
             SharedPreferences.Editor editor2=context.getSharedPreferences("needToPay", Context.MODE_PRIVATE).edit();

             editor2.putBoolean("needToPay",true);
             editor2.putBoolean("uploaded",false);
             editor2.putInt("moneyToPay",money);
             editor2.apply();
              AlarmData alarm=null;
              alarm=((Alarmio)context.getApplicationContext()).getAlarms().get(intent.getIntExtra(EXTRA_ALARM_ID,0));
              AlarmsFragment.setSnoozeNotification(context,0,true);
             AlarmsFragment.setNextAlarmNotification(context,alarm.getId(),"",true);



             boolean wasEnabled;
             Alarmio alarmio=((Alarmio) context.getApplicationContext());//getAlarmio();
             if(alarmio==null)Log.e("onReceiv ","alarmio is null");
             AlarmManager alarmManager=(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

             List<AlarmData> alarmsData=alarmio.getAlarms();
             for(int i=0;i<alarmsData.size();i++){
                 if(alarmsData.get(i).getId()==alarmId){

                     if(alarmsData.get(i).isRepeat()) wasEnabled=true;
                     else                             wasEnabled=false;
                 }else{
                     wasEnabled=alarmsData.get(i).isEnabled;

                 }
                 alarmsData.get(i).setEnabled(alarmio,alarmManager,false);
                 alarmsData.get(i).setWasEnabled(alarmio,alarmManager,wasEnabled);

             }
             //MyPrefsBackupAgent.updateDatabase(id,true,money,context);

              new BackupManager(context).dataChanged();

             if(AlarmActivity.getInstace()!=null){
                 AlarmActivity.getInstace().updateActivity(alarm);

             }
             else{
                 Intent intent2 = new Intent(context, MainActivity.class);
                 intent2.putExtra("refresh",true);
                 intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|FLAG_ACTIVITY_NEW_TASK);
                 context.startActivity(intent2);
             }


         }

     }

     @Override
     protected void onStop()
     {

         if(broadcast!=null)unregisterReceiver(broadcast);
         super.onStop();
     }

     private void updateActivity(AlarmData alarm) {
         stopAnnoyingness();
         Intent intent = new Intent(context, MainActivity.class);
         intent.putExtra("openF2",true);
         overridePendingTransition(0, 0);
         intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|FLAG_ACTIVITY_NEW_TASK);
           if(alarm!=null)intent.putExtra(EXTRA_ALARM_ID,alarm.getId());
         startActivity(intent);


     }



 }
