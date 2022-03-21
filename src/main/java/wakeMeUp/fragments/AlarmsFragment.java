package me.jfenn.wakeMeUp.fragments;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;
import com.alexfu.countdownview.CountDownView;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.jfenn.wakeMeUp.Alarmio;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.activities.GameActivity;
import me.jfenn.wakeMeUp.activities.MainActivity;
import me.jfenn.wakeMeUp.adapters.AlarmsAdapter;
import me.jfenn.wakeMeUp.addedByMe.MyBoolean;
import me.jfenn.wakeMeUp.backup.MyPrefsBackupAgent;
import me.jfenn.wakeMeUp.data.AlarmData;
import me.jfenn.wakeMeUp.data.PreferenceData;
import me.jfenn.wakeMeUp.utils.FormatUtils;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static me.jfenn.wakeMeUp.receivers.AlarmReceiver.EXTRA_ALARM_ID;
/*Alarmio-The origin code have been changed */
/*
*countdownview
* MIT License

Copyright (c) 2017 Alex Fu

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 */
public class AlarmsFragment extends BasePagerFragment implements BillingProcessor.IBillingHandler{

    private static final String TAG = "AlarmsFragment";
    private static final String TAG2 = "AlarmsFragment2";
    public static MyBoolean purchaseListner;
    public static RecyclerView recyclerView;
    public static Handler handler;
    public static Runnable runnable;
    public static boolean toStopHandler;
    private View empty;
    int SNOOZE_TIME;

    public AlarmsAdapter alarmsAdapter;

    private Disposable colorAccentSubscription;
    private Disposable colorForegroundSubscription;
    private Disposable textColorPrimarySubscription;
    public boolean needToPay;
    public static int money;
    private Button needToPayButtom;
    private TextView paymentDesc;
    int counter=0;
    public static Context mContext;

    BillingProcessor bp;
    String licence="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk29RREpIa6f51EUK4hf3qyECdkmgR8bcxdCulw5gN1/jSRMnuL2GucDQO0DIVs/mUn+INiaorbZvDCNiYWC24fm9ccq6IAYicrLE6+Vpi7igkmpodpIr3m/5SWtfROzjiBsGgON/DnQ8aDbxq4C1/faFgReR5j6RQLo+JBx9SeIOiKguvBcfNBcUIIoaOclssg9+BfugVcFThY4F5p3so4v/4wc7QVpcDieBpI/0SGOYg36a53BLL7MJXAT7dwhQ943B6Nd3Kxl15Db4cZfzRb22CoXsrEFkWPaMbVQf+2IOPcU6KEx4+avjEhXqB4/K0bLym0d2+06sXChmSq882QIDAQAB";
    private Button calibrateButton;
    private boolean needToCalibrate,readyToPurchase;
    private CountDownView countDown;
    private LinearLayout payment;
    private boolean purchased;
    private static LayoutInflater inflater;
    static ViewGroup container;
    static Bundle savedInstanceState;
    private Button snoozeButton,dismissButton;
    private AlarmData alarm;
    public static TextView remainingTime;
    public static LinearLayout remainingTimeLinearLayout;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private TextView calibrationText,paymentText;
     private ImageView sun,moon;
    private Animation animMoveToTop;
    private View background;
    private FrameLayout sunMoon;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
         mContext = context;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recycler, container, false);
        Log.d(MainActivity.TAG,"onCreateView alarm fragment");

        remainingTime=(TextView) v.findViewById(R.id.remaining);
        sun=(ImageView) v.findViewById(R.id.sun);
        moon=(ImageView) v.findViewById(R.id.moon);
        sunMoon=(FrameLayout) v.findViewById(R.id.sunMoon);

        background=(View) v.findViewById(R.id.background_home);
        animMoveToTop = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.move);
        if(isDay(getContext())) {
            background.setBackgroundColor(Color.parseColor("#91fcff"));
            sun.setVisibility(VISIBLE);
            moon.setVisibility(GONE);
            //sunMoon.startAnimation(animMoveToTop);
            sun.animate().alpha(1.0f).setDuration(2000);
        }else{
            background.setBackgroundColor(Color.parseColor("#070B34"));
            moon.setAlpha(0f);
            moon.setVisibility(VISIBLE);
            sun.setVisibility(GONE);
            moon.animate().alpha(1.0f).setDuration(3000);

        }

        remainingTimeLinearLayout=(LinearLayout)v.findViewById(R.id.remaining_time);
        mAdView=(AdView)v.findViewById(R.id.alarms_ad);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        SharedPreferences pref =getContext().getSharedPreferences("threshold",Context.MODE_PRIVATE);
         String manufacturer = android.os.Build.MANUFACTURER;


        MobileAds.initialize(getContext(), "ca-app-pub-1242332276700800~2357749034");
        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-1242332276700800/6398734698");//ca-app-pub-3940256099942544/1033173712
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInterstitialAd.isLoaded())
                    mInterstitialAd.show();
            }
        },6*60000);


        handler=new Handler();

        snoozeButton=(Button)v.findViewById(R.id.snooze_button);
        SNOOZE_TIME=  (int) (((long) PreferenceData.SNOOZE_DURATION.getValue(getContext()))/60000);

        SharedPreferences prefNeedToPay=mContext.getSharedPreferences("threshold", Context.MODE_PRIVATE);
        long timeToEnd=prefNeedToPay.getLong("timeToEnd",0);
        Handler handler3=new Handler();
        Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                if(timeToEnd-(System.currentTimeMillis()+SNOOZE_TIME*60*1000)<=1000){
                    snoozeButton.setVisibility(GONE);
                    handler3.removeCallbacks(this);
                }
                handler3.postDelayed(this, timeToEnd-(System.currentTimeMillis()+SNOOZE_TIME*60*1000));
            }
        };
        handler3.post(runnable3);
        Bundle bundle=getArguments();
        if(bundle!=null){
            int index=bundle.getInt(EXTRA_ALARM_ID);
             if(index<getAlarmio().getAlarms().size()) {
                AlarmData alarm = getAlarmio().getAlarms().get(index);
                this.alarm = alarm;
            }else{
                 Log.e(TAG,"index is bigger than ");

             }
        }

        recyclerView = v.findViewById(R.id.recycler);
        empty = v.findViewById(R.id.empty);

        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 1));
        alarmsAdapter = new AlarmsAdapter(getAlarmio(), recyclerView, getFragmentManager());
        recyclerView.setAdapter(alarmsAdapter);



        calibrateButton=(Button)v.findViewById(R.id.calibrationButton);



        payment=(LinearLayout)v.findViewById(R.id.payment);
        SharedPreferences pref2 = getContext().getSharedPreferences("needToPay", Context.MODE_PRIVATE);

        needToPay=pref2.getBoolean("needToPay",false);
             Log.d(MyPrefsBackupAgent.TAG,"alarms frag needToPay "+needToPay);
            money=pref2.getInt("moneyToPay",0);
            needToCalibrate=prefNeedToPay.getBoolean("needCalibration",true);
            if(needToCalibrate)remainingTimeLinearLayout.setVisibility(GONE);
        boolean snoozeClicked=prefNeedToPay.getBoolean("snoozeClicked",false);
        Log.d(MainActivity.TAG,"snooze clicked? "+snoozeClicked);

        //if(snoozeClicked) snoozeButton.setVisibility(View.GONE);
        if(snoozeClicked) snoozeButton.setVisibility(GONE);
        boolean snoozed=prefNeedToPay.getBoolean("snoozed", false);
        countDown=(CountDownView)v.findViewById(R.id.count_down_main);;

        if(!snoozed&&!needToPay)getNextAlarmString();
        int id=prefNeedToPay.getInt("id",-1);
        if(timeToEnd-System.currentTimeMillis()<0)Log.d(TAG2,"LOWER THAN ZERO");
        if((v.findViewById(R.id.dismiss_frame)).getVisibility()==View.VISIBLE)Log.d(TAG2,"DISMISS FRAME IS VISIBLE");
        if((v.findViewById(R.id.count_down_main)).getVisibility()==View.VISIBLE)Log.d(TAG2," COUNTDOWN IS VISIBLE");

        if(timeToEnd!=0&&timeToEnd-System.currentTimeMillis()<0&&((v.findViewById(R.id.count_down_main)).getVisibility()==View.VISIBLE)){
            SharedPreferences.Editor editor = mContext.getSharedPreferences("threshold", mContext.MODE_PRIVATE).edit();
            editor.putBoolean("snoozed", false);
            editor.apply();

            Alarmio alarmio=getAlarmio();
            AlarmManager alarmManager=(AlarmManager) alarmio.getSystemService(Context.ALARM_SERVICE);
            for(AlarmData alarm:alarmio.getAlarms()){
                alarm.setEnabled(alarmio,alarmManager,alarm.wasEnabled());
                if(alarm.wasEnabled()) AlarmsFragment.setNextAlarmNotification(getContext(),alarm.getId(),alarm.getNext()!=null? FormatUtils.format(alarm.getNext().getTime(), "d MMMM HH:mm"):"",false);
            }
            SharedPreferences.Editor editor2=mContext.getSharedPreferences("needToPay", Context.MODE_PRIVATE).edit();
            editor2.putBoolean("needToPay",false);
            editor2.putInt("moneyToPay",0);
            editor2.apply();
            SharedPreferences.Editor editorNeedToPay=mContext.getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
            editorNeedToPay.putLong("timeToEnd",0);
            editorNeedToPay.apply();
            payment.setVisibility(GONE);
            (v.findViewById(R.id.dismiss_frame)).setVisibility(GONE);
            counter=0;
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false);
            }
            ft.detach(this).attach(this).commit();
        }
       else if(snoozed&&id!=-1){
           v.findViewById(R.id.dismiss_frame).setVisibility(View.VISIBLE);
           countDown.setStartDuration(timeToEnd-System.currentTimeMillis());
           countDown.start();



           dismissButton=(Button)v.findViewById(R.id.dismiss_button);
           snoozeButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if(timeToEnd-(System.currentTimeMillis()+SNOOZE_TIME*60*1000)>=0) {
                       SharedPreferences.Editor editorNeedToPay = getContext().getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
                       editorNeedToPay.putBoolean("snoozeClicked", true);
                       editorNeedToPay.apply();
                       int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                       int mintues = Calendar.getInstance().get(Calendar.MINUTE) + SNOOZE_TIME;
                       Alarmio alarmio = (Alarmio) getAlarmio();
                       AlarmManager manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                       alarm.time.set(Calendar.HOUR_OF_DAY, hour);
                       alarm.time.set(Calendar.MINUTE, mintues);
                       alarm.setTime(alarmio, manager, alarm.time.getTimeInMillis());
                       // alarm.setTimeToWake(getContext(),manager,((int)(timeToEnd-System.currentTimeMillis())/60000));
                       alarm.setTimeToWake(getContext(), manager, alarm.getTimeToWake() - SNOOZE_TIME);
                       alarm.isEnabled = true;
                       alarm.setMoney(getContext(), manager, money);
                       alarm.setVisibilty(getContext(), manager, true);
                       alarm.setEnabled(getContext(), manager, true);
                       alarmio.onAlarmsChanged();
                       snoozeButton.setVisibility(GONE);
                       setSnoozeNotification(getContext(),SNOOZE_TIME,false);
                   }
               }
           });
           dismissButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(getContext(), GameActivity.class);
                   getContext().startActivity(intent);

               }
           });
       }



        purchaseListner=new MyBoolean();
        purchaseListner.setListener(new MyBoolean.ChangeListener() {
            @Override
            public void onChange() {
                counter++;
                Log.d("payment Error","readyToPurchase.setListener");
                if(bp!=null){
                   if(counter==1)bp.purchase(getActivity(), "price0" + money);
                }
            }
        });
        if(needToCalibrate){
            (v.findViewById(R.id.calibration)).setVisibility(View.VISIBLE);
            calibrateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    HomeFragment.viewPager.setCurrentItem(2,true);


                }
            });

        }else{
            (v.findViewById(R.id.calibration)).setVisibility(GONE);
        }
        if(needToPay){
            v.findViewById(R.id.dismiss_frame).setVisibility(GONE);
            if(money>0) {
                new BackupManager(mContext).dataChanged();
                bp = BillingProcessor.newBillingProcessor(mContext, licence, this);
                bp.initialize();
                needToPayButtom = (Button) v.findViewById(R.id.needToPayButton);
                paymentDesc = (TextView) v.findViewById(R.id.payment_description);
                v.findViewById(R.id.payment).setVisibility(View.VISIBLE);
                needToPayButtom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        purchased = false;
                        counter = 0;
                        HomeFragment.purchaseIsPressed = true;

                        if (!readyToPurchase) {
                            Log.d("payment Error", "!readyToPurchase");
                            return;
                        }
                        purchased = bp.purchase(getActivity(), "price0" + money);

                    }
                });
            }else {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("threshold", mContext.MODE_PRIVATE).edit();
                editor.putBoolean("snoozed", false);
                editor.apply();

                Alarmio alarmio=getAlarmio();
                AlarmManager alarmManager=(AlarmManager) alarmio.getSystemService(Context.ALARM_SERVICE);
                for(AlarmData alarm:alarmio.getAlarms()){
                    alarm.setEnabled(alarmio,alarmManager,alarm.wasEnabled());

                }
                SharedPreferences.Editor editor2=mContext.getSharedPreferences("needToPay", Context.MODE_PRIVATE).edit();
                editor2.putBoolean("needToPay",false);
                editor2.putInt("moneyToPay",0);
                editor2.apply();
                SharedPreferences.Editor editorNeedToPay=mContext.getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
                editorNeedToPay.putLong("timeToEnd",0);
                editorNeedToPay.apply();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(this).attach(this).commit();
            }
        }else{
            v.findViewById(R.id.payment).setVisibility(GONE);

        }
         colorAccentSubscription = Aesthetic.Companion.get()
                .colorAccent()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        alarmsAdapter.setColorAccent(integer);
                        payment.setBackgroundColor(integer);
                        (v.findViewById(R.id.calibration)).setBackgroundColor(integer);
                        if(remainingTime.getVisibility()==VISIBLE){
                           remainingTimeLinearLayout.setBackgroundColor(integer);
                        }

                    }
                });

        colorForegroundSubscription = Aesthetic.Companion.get()
                .colorCardViewBackground()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        alarmsAdapter.setColorForeground(integer);
                    }
                });

        textColorPrimarySubscription = Aesthetic.Companion.get()
                .textColorPrimary()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        alarmsAdapter.setTextColorPrimary(integer);

                    }
                });



        onChanged();


        return v;
    }

    private boolean isDay(Context context) {
        return !getAlarmio().isNight();
    }






    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void setSnoozeNotification(Context context,int snoozeTime,boolean cancel) {
        boolean toShow=((boolean)PreferenceData.SNOOZE_NOTIFICATION.getValue(context));

         if(toShow) {
             NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

             if(cancel){
                 mNotificationManager.cancel(1);
             }
            final int[] snooze = {snoozeTime};
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (snooze[0] > 0) {
                    String idChannel = "my_channel_01";
                    Intent mainIntent;

                    mainIntent = new Intent(context, MainActivity.class);

                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);


                    NotificationChannel mChannel = null;
                    // The id of the channel.

                    int importance = NotificationManager.IMPORTANCE_HIGH;

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, idChannel);
                    builder.setContentTitle(context.getString(R.string.app_name))
                            .setSmallIcon(R.drawable.snooze)
                            .setOngoing(true)
                            .setContentIntent(pendingIntent)
                            .setContentText("[snoozed] next alarm: " + snooze[0] + " minutes");

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        mChannel = new NotificationChannel(idChannel, context.getString(R.string.app_name), importance);
                        // Configure the notification channel.
                        mChannel.setDescription("[snoozed] next alarm: " + snooze[0] + " minutes");
                        mChannel.enableLights(true);
                        mChannel.setLightColor(Color.RED);
                        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                        mNotificationManager.createNotificationChannel(mChannel);
                    } else {
                        builder.setContentTitle(context.getString(R.string.app_name))
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setVibrate(new long[]{100, 250})
                                .setOngoing(true)
                                .setLights(Color.YELLOW, 500, 5000)
                                .setAutoCancel(true);
                    }
                    mNotificationManager.notify(1, builder.build());
                    snooze[0]--;
                    handler.postDelayed(this, 60000);
                } else {
                    handler.removeCallbacks(this);

                    mNotificationManager.cancel(1);
                }
            }
        }, (60 - Calendar.getInstance().get(Calendar.SECOND) * 1000));
    }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void setNextAlarmNotification(Context context,int alarmId,String nextAlarm,boolean cancel) {
        boolean toShow=((boolean)PreferenceData.ALARM_NOTIFICATION.getValue(context));

        if(toShow) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (cancel) mNotificationManager.cancel(alarmId);
            else {
                String idChannel = "my_channel_02";
                Intent mainIntent;

                mainIntent = new Intent(context, MainActivity.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, mainIntent, 0);


                NotificationChannel mChannel = null;
                // The id of the channel.

                int importance = NotificationManager.IMPORTANCE_HIGH;

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, idChannel);
                builder.setContentTitle(context.getString(R.string.app_name))
                        .setSmallIcon(R.drawable.snooze)
                        .setOngoing(true)
                        .setContentIntent(pendingIntent)
                        .setContentText(" next alarm: " + nextAlarm);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mChannel = new NotificationChannel(idChannel, context.getString(R.string.app_name), importance);
                    // Configure the notification channel.
                    mChannel.setDescription(" next alarm: " + nextAlarm);
                    mChannel.enableLights(true);
                    mChannel.setLightColor(Color.RED);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    mNotificationManager.createNotificationChannel(mChannel);
                } else {
                    builder.setContentTitle(context.getString(R.string.app_name))
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setVibrate(new long[]{100, 250})
                            .setOngoing(true)
                            .setLights(Color.YELLOW, 500, 5000)
                            .setAutoCancel(true);
                }
                mNotificationManager.notify(alarmId, builder.build());
            }
        }
    }


    public static void getNextAlarmString() {
        SharedPreferences prefNeedToPay=getAlarmio().getApplicationContext().getSharedPreferences("threshold", Context.MODE_PRIVATE);
        boolean needToCalibrate=prefNeedToPay.getBoolean("needCalibration",true);
        if(remainingTimeLinearLayout.getVisibility()== GONE&&!needToCalibrate)remainingTimeLinearLayout.setVisibility(View.VISIBLE);
         List<AlarmData> alarmsData=getAlarmio().getAlarms();
        if(alarmsData.size()==0){
            remainingTime.setText("No active alarms");
            toStopHandler=true;
        }
        else if(alarmsData.get(0).getNext()==null){
            remainingTime.setText("No active alarms");
            toStopHandler=true;
        }else{
            long min=alarmsData.get(0).getNext().getTime().getTime();
            AlarmData alarmMin=alarmsData.get(0);
            for(AlarmData alarm:alarmsData){
                if(!alarm.isEnabled) break;
                if(alarm.getNext().getTime().getTime()<min){
                    min=alarm.getNext().getTime().getTime();
                    alarmMin=alarm;
                }

            }
            Calendar nextAlarm = alarmMin.getNext();
            if (nextAlarm != null) {
                final int[] minutes = {(int) TimeUnit.MILLISECONDS.toMinutes(nextAlarm.getTimeInMillis() - Calendar.getInstance().getTimeInMillis())};
                remainingTime.setText("Next alarm: "+FormatUtils.formatUnit(getAlarmio(), minutes[0])+" from now");
                Log.d(TAG,""+FormatUtils.formatUnit(getAlarmio(), minutes[0]));
                AlarmData finalAlarmMin = alarmMin;
                runnable=new Runnable() {
                   @Override
                   public void run() {
                       if(toStopHandler){
                           handler.removeCallbacks(this);
                       }else {
                           minutes[0]--;
                           remainingTime.setText("Next alarm: " + FormatUtils.formatUnit(getAlarmio(), minutes[0]) + " from now");
                           handler.postDelayed(this, 60000);
                       }

                   }
               };
               handler.postDelayed(runnable,(60-Calendar.getInstance().get(Calendar.SECOND))*1000);


            }

        }




        //return ans;
    }

    public static void makeRemainingTimeGone(){
        remainingTimeLinearLayout.setVisibility(GONE);
    }

    private boolean thereIsNoNonRepeatAlarms() {
        for(AlarmData alarm:getAlarmio().getAlarms()){
            if(alarm.isEnabled&&!alarm.isRepeat())return false;
            if(!alarm.isEnabled) return true;
        }
        return true;
    }

    private void setRemainingTime(Calendar calendar, AlarmData alarm) {
        if(calendar.get(Calendar.HOUR_OF_DAY)<alarm.time.get(Calendar.HOUR_OF_DAY)) {
            Calendar nextAlarm = alarm.getNext();
            if (nextAlarm != null) {
                int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(nextAlarm.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
                remainingTime.setText("Next alarm: "+FormatUtils.formatUnit(getAlarmio(), minutes)+" from now");
            }
        }else if(calendar.get(Calendar.HOUR_OF_DAY)==alarm.time.get(Calendar.HOUR_OF_DAY)){
            if (calendar.get(Calendar.MINUTE) <= alarm.time.get(Calendar.MINUTE)) {
                Calendar nextAlarm = alarm.getNext();
                if (nextAlarm != null) {
                    int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(nextAlarm.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
                    remainingTime.setText("Next alarm: "+FormatUtils.formatUnit(getAlarmio(), minutes)+" from now");
                }
            }
        }
    }



    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
        readyToPurchase = true;
     }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
        HomeFragment.purchaseIsPressed=false;
        Log.d("payment Error","onProductPurchased(String productId, TransactionDetails details)");
        SharedPreferences pref = mContext.getSharedPreferences("threshold", mContext.MODE_PRIVATE);
        int id=pref.getInt("id",-1);
            SharedPreferences.Editor editor = mContext.getSharedPreferences("threshold", mContext.MODE_PRIVATE).edit();

            editor.putBoolean("snoozed", false);
        editor.apply();

        Alarmio alarmio=getAlarmio();
        AlarmManager alarmManager=(AlarmManager) alarmio.getSystemService(Context.ALARM_SERVICE);
        for(AlarmData alarm:alarmio.getAlarms()){
            alarm.setEnabled(alarmio,alarmManager,alarm.wasEnabled());
           if(alarm.wasEnabled()) AlarmsFragment.setNextAlarmNotification(getContext(),alarm.getId(),alarm.getNext()!=null? FormatUtils.format(alarm.getNext().getTime(), "d MMMM HH:mm"):"",false);

            /*if(alarm.getId()==id) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        alarmio.onAlarmsChanged();
                        alarmio.setAlarms();
                        AlarmsFragment.getNextAlarmString();
                    }
                });
            }*/


        }
        bp.consumePurchase("price0" + money);
        SharedPreferences.Editor editor2=mContext.getSharedPreferences("needToPay", Context.MODE_PRIVATE).edit();
        editor2.putBoolean("needToPay",false);
        editor2.putInt("moneyToPay",0);
        editor2.apply();
        if(id!=-1)MyPrefsBackupAgent.updateDatabase(id,false,0,this.getContext());
        SharedPreferences.Editor editorNeedToPay=mContext.getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
        editorNeedToPay.putLong("timeToEnd",0);
        editorNeedToPay.apply();
        payment.setVisibility(GONE);
        counter=0;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false);
        }
        ft.detach(this).attach(this).commit();
    }




    @Override
    public void onBillingError(int errorCode, Throwable error) {
        /*
         * Called when some error occurred. See Constants class for more details
         *
         * Note - this includes handling the case where the user canceled the buy dialog:
         * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
         */
        Log.d("payment Error","error " +error);
    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
        Log.d("payment Error","onPurchaseHistoryRestored ");

     }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            PowerManager pm = (PowerManager)getContext().getSystemService(Context.POWER_SERVICE);
            boolean isIgnoringBatteryOptimizations = pm.isIgnoringBatteryOptimizations(getContext().getPackageName());
            if(isIgnoringBatteryOptimizations){
                // Ignoring battery optimization
            }else{
                // Not ignoring battery optimization
            }
        }

        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onDestroyView() {
        if (bp != null) {
            bp.release();
        }
        colorAccentSubscription.dispose();
        colorForegroundSubscription.dispose();
        textColorPrimarySubscription.dispose();

        super.onDestroyView();
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.title_alarms);
    }

    @Override
    public void onAlarmsChanged() {
        if (alarmsAdapter != null) {
            alarmsAdapter.notifyDataSetChanged();
            onChanged();
        }
    }

    @Override
    public void onTimersChanged() {
        if (alarmsAdapter != null) {
            alarmsAdapter.notifyDataSetChanged();
            onChanged();
        }
    }

    private void onChanged() {
        if (empty != null && alarmsAdapter != null)
            empty.setVisibility(alarmsAdapter.getItemCount() > 0 ? GONE : View.VISIBLE);
    }


}
