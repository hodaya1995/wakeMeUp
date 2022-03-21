package me.jfenn.wakeMeUp.addedByMe;

import android.app.AlarmManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nhaarman.supertooltips.ToolTipView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.TooltipCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import io.reactivex.disposables.Disposable;
import me.jfenn.timedatepickers.utils.ConversionUtils;
import me.jfenn.timedatepickers.views.LinearTimePickerView;
import me.jfenn.wakeMeUp.Alarmio;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.data.AlarmData;
import me.jfenn.wakeMeUp.data.PreferenceData;
import me.jfenn.wakeMeUp.data.SoundData;
import me.jfenn.wakeMeUp.fragments.AlarmsFragment;
import me.jfenn.wakeMeUp.receivers.NetworkBroadcastReceiverForSkuAndId;
import me.jfenn.wakeMeUp.utils.DoubleToLongConverter;
import me.jfenn.wakeMeUp.utils.FormatUtils;
import me.jfenn.wakeMeUp.views.AestheticSwitchView;
import me.jfenn.wakeMeUp.views.DecimalPicker;

import static me.jfenn.wakeMeUp.fragments.BaseFragment.getAlarmio;

public class AddPhotoBottomDialogFragment extends BottomSheetDialogFragment {
    View view;
    int maxTime;
    private boolean isClicked;
    DecimalPicker numberPicker;
    TextView ok;
    LinearTimePickerView timePickerView;
    //ScrollableNumberPicker scrollableNumberPicker;
    private TextView cancel;
    private ImageView okImage,cancelImage;
    private ToolTipView myToolTipView;
    public static boolean alarmAdded;
    private DecimalPicker decimalPicker;
    private TextView coin;
    private NetworkBroadcastReceiverForSkuAndId broadcast;

    static final String TAG = "AddPhotoBottom";
    static final String SKU_PRICE = "price01";
    static final int RC_REQUEST = 10001;
    private boolean registered;
    private Disposable colorAccentSubscription;
    private Paint textPrimaryPaint;
    private AestheticSwitchView punish;
    private ConstraintLayout moneyLayout,maxttwLayout;


    public static AddPhotoBottomDialogFragment newInstance() {
        return new AddPhotoBottomDialogFragment();
    }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        view = inflater.inflate(R.layout.bottom_sheet, container, false);
        TooltipCompat.setTooltipText(((TextView)view.findViewById(R.id.money_desc)), "Tooltip text");

        punish=(AestheticSwitchView)view.findViewById(R.id.punish);
        maxttwLayout=(ConstraintLayout)view.findViewById(R.id.maxttwLayout);
        moneyLayout=(ConstraintLayout)view.findViewById(R.id.moneyLayout);

        ((TextView)view.findViewById(R.id.money_desc)).setText("Money: ");
        ((TextView)view.findViewById(R.id.ttw_desc)).setText("Max minutes to wake ");



       String tag= ((TextView)view.findViewById(R.id.money_desc)).getTag().toString();
        decimalPicker=view.findViewById(R.id.decimal_picker);
        coin=(TextView)view.findViewById(R.id.coin);
        SharedPreferences pref = view.getContext().getSharedPreferences("threshold", Context.MODE_PRIVATE);


        textPrimaryPaint = new Paint();
        textPrimaryPaint.setTextAlign(Paint.Align.LEFT);
        textPrimaryPaint.setTextSize(ConversionUtils.spToPx(16));
        textPrimaryPaint.setColor(ContextCompat.getColor(getContext(), R.color.timedatepicker_textColorPrimary));
        textPrimaryPaint.setAntiAlias(true);
        textPrimaryPaint.setDither(true);

        punish.setChecked(true);
        punish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked){
                   moneyLayout.setVisibility(View.GONE);
                    maxttwLayout.setVisibility(View.GONE);
                }else{
                    moneyLayout.setVisibility(View.VISIBLE);
                    maxttwLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        ;

        long moneyVal=pref.getLong("price02",0);
        String coinSymball=pref.getString("coinSymball","");
        if(moneyVal==0){
            //there is no value in pref and need to update
            coin.setText("USD (estimated) ");

        }else{
            //updated
            coin.setText(coinSymball+" ");


        }

        ((ImageView)view.findViewById(R.id.money_info)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg="Fine yourself for wake up late";
                setTooltip(view.findViewById(R.id.money_desc),msg);

            }
        });

        ((TextView)view.findViewById(R.id.money_desc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg="Fine yourself for wake up late";
                setTooltip(view.findViewById(R.id.money_desc),msg);

            }
        });

        ((ImageView)view.findViewById(R.id.ttw_info)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg="The maximum time for wake up";
                setTooltip(view.findViewById(R.id.ttw_desc),msg);
            }
        });

        ((TextView)view.findViewById(R.id.ttw_desc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg="The maximum time for wake up";
                setTooltip(view.findViewById(R.id.ttw_desc),msg);
            }
        });
        timePickerView=(LinearTimePickerView)view.findViewById(R.id.linear_time_picker);
        Calendar calendar=Calendar.getInstance();
        timePickerView.setTime(calendar.get(Calendar.HOUR_OF_DAY)+8,calendar.get(Calendar.MINUTE));
        ok=(TextView) view.findViewById(R.id.okText);
        okImage=(ImageView)view.findViewById(R.id.okImage);
        cancel=(TextView) view.findViewById(R.id.cancelText);
        cancelImage=(ImageView)view.findViewById(R.id.cancelImage);
        /*scrollableNumberPicker=(ScrollableNumberPicker) view.findViewById(R.id.number_picker_horizontal);

        scrollableNumberPicker.findViewById(R.id.)*/
        decimalPicker = (DecimalPicker) view.findViewById(R.id.decimal_picker);
        decimalPicker.isMoneyPicker(true);



        double value=decimalPicker.getMoneyValue();
        double num= DoubleToLongConverter.longToDouble(pref.getLong("price02",0));

        decimalPicker.setFormat("%.3f");//Weight format
        if(num>0) {
            decimalPicker.setStepNumber(2);
            decimalPicker.setNumber(num);
            decimalPicker.setFinalNumber(DoubleToLongConverter.longToDouble(pref.getLong("price010",0)));
        }
        else decimalPicker.setNumber(value*2);
       decimalPicker.setOnValueChangeListener(new DecimalPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(DecimalPicker picker, double oldValue, double newValue) {
                /*if(coinVal!=0){
                    ((TextView)view.findViewById(R.id.coin)).setText(""+new DecimalFormat("##.##").format((coinVal*value))+""+getCoin()+"");
                }*/
            }
        });




        numberPicker = (DecimalPicker) view.findViewById(R.id.number_picker);
        cancel.setFocusableInTouchMode(false);
        cancel.setFocusable(false);
        cancel.setClickable(true);
        cancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    v.performClick();
                    AddPhotoBottomDialogFragment.this.dismiss();
                }
                return true;
            }
        });


        cancelImage.setFocusableInTouchMode(false);
        cancelImage.setFocusable(false);
        cancelImage.setClickable(true);
        cancelImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    v.performClick();
                    AddPhotoBottomDialogFragment.this.dismiss();
                }
                return true;
            }
        });



        ok.setFocusableInTouchMode(false);
        ok.setFocusable(false);
        ok.setClickable(true);
        ok.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        v.performClick();
                        setAlarm();

                    }
                    return true;
                }
        });


        okImage.setFocusableInTouchMode(false);
        okImage.setFocusable(false);
        okImage.setClickable(true);
        okImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    v.performClick();
                    setAlarm();
                }
                return true;
            }
        });



        return view;

    }


    // Listener that's called when we finish querying the items and subscriptions we own








    private void setInternetListener() {
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        broadcast=new NetworkBroadcastReceiverForSkuAndId();
        getContext().registerReceiver(broadcast, intentFilter);
       // broadcast.setListener(this);
    }

    private String removeDots(String number) {
        StringBuilder builder=new StringBuilder("");
        for(char c:number.toCharArray()){
          if(c!='.') builder.append(c);
          else      break;
        }
        return builder.toString();
    }

    private void setAlarm() {
        AddPhotoBottomDialogFragment.this.dismiss();

        double money=decimalPicker.getNumber();
        int maxTime=(int)numberPicker.getNumber();
        Alarmio alarmio = getAlarmio();

        AlarmManager manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        AlarmData alarm = alarmio.newAlarm();
        alarm.time.set(Calendar.HOUR_OF_DAY, timePickerView.getHourOfDay());
        alarm.time.set(Calendar.MINUTE, timePickerView.getMinute());
        alarm.setTime(getAlarmio(), manager, alarm.time.getTimeInMillis());
        alarm.setEnabled(getContext(), manager, true);

        boolean punishUser=punish.isChecked();
        Log.d(TAG,"punishUser? "+punishUser);
        if(punishUser) {
            alarm.setMoney(getContext(), manager, money);
            alarm.setMoneyInDollars(getContext(), manager, decimalPicker.getStep());
            alarm.setTimeToWakeP(getContext(), manager, maxTime);
            alarm.setTimeToWake(getContext(), manager, maxTime);
            alarm.setPunished(getContext(),true);
        }else{
            alarm.setMoney(getContext(), manager, 0);
            alarm.setMoneyInDollars(getContext(), manager, decimalPicker.getStep());
            alarm.setTimeToWakeP(getContext(), manager, 0);
            alarm.setTimeToWake(getContext(), manager, 0);
            alarm.setPunished(getContext(),false);
        }



        Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(getActivity().getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
        Ringtone defaultRingtone = RingtoneManager.getRingtone(getActivity(), defaultRingtoneUri);
        SoundData sound = new SoundData(defaultRingtone.getTitle(getContext()),defaultRingtoneUri.toString(),defaultRingtone);
        SoundData defaultSound=SoundData.fromString(PreferenceData.DEFAULT_ALARM_RINGTONE.getSpecificOverriddenValue(getContext(), PreferenceData.DEFAULT_ALARM_RINGTONE.getValue(getContext(), ""), alarm.getId()));
        if(defaultSound==null){
            alarm.setSound(getContext(),sound);
            PreferenceData.DEFAULT_ALARM_RINGTONE.setValue(getContext(), sound != null ? sound.toString() : null);

        }
        else {
            alarm.setSound(getContext(),defaultSound);
            //PreferenceData.DEFAULT_ALARM_RINGTONE.setValue(getContext(), defaultSound != null ? defaultSound.toString() : null);

        }
        alarm.setConstantTime( alarm.time.getTimeInMillis(),getContext(),manager);
        alarmio.addUnsortedAlarm(alarm);
        List<AlarmData>a=new ArrayList<>();
        a.addAll(alarmio.getAlarms());
         Collections.sort(a, new Comparator<AlarmData>() {
            @Override
            public int compare(AlarmData lhs, AlarmData rhs) {
                int r=(rhs.time.get(Calendar.HOUR_OF_DAY)*60+rhs.time.get(Calendar.MINUTE));
                if(!rhs.isEnabled)r+=1440;
                int l=(lhs.time.get(Calendar.HOUR_OF_DAY)*60+lhs.time.get(Calendar.MINUTE));
                if(!lhs.isEnabled)l+=1440;

                if(l>r){
                     return 1;
                }
                else if(l<r){
                    return -1;
                }
                return 0;
            }
        });

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                alarmio.setAlarms(a);
                alarmio.onAlarmsChanged();
                AlarmsFragment.getNextAlarmString();
                AlarmsFragment.setNextAlarmNotification(alarmio.getApplicationContext(),alarm.getId(),alarm.getNext()!=null? FormatUtils.format(alarm.getNext().getTime(), "d MMMM HH:mm"):"",false);
            }
        });


    }



    @Override
    public void onPause() {
        super.onPause();
        if(broadcast!=null&&registered){
            getContext().unregisterReceiver(broadcast);
            registered=false;
        }
    }

    private void setTooltip(View view, String msg) {
        final int[] screenPos = new int[2]; // origin is device display
        final Rect displayFrame = new Rect(); // includes decorations (e.g. status bar)
        view.getLocationOnScreen(screenPos);
        view.getWindowVisibleDisplayFrame(displayFrame);

        final Context context = view.getContext();
        final int viewWidth = view.getWidth();
        final int viewHeight = view.getHeight();
        final int viewCenterX = screenPos[0] + viewWidth / 2;
        final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        final int estimatedToastHeight = (int) (1 * context.getResources().getDisplayMetrics().density);

        Toast toolTipToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        boolean showBelow = screenPos[1] < estimatedToastHeight;
        //if (showBelow) {
            // Show below
            // Offsets are after decorations (e.g. status bar) are factored in
            toolTipToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                    viewCenterX - screenWidth / 2,
                    screenPos[1] - displayFrame.top + viewHeight);
        //}
        /*else {
            // Show above
            // Offsets are after decorations (e.g. status bar) are factored in
            // NOTE: We can't use Gravity.BOTTOM because when the keyboard is up
            // its height isn't factored in.
            toolTipToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                    viewCenterX - screenWidth / 2,
                    screenPos[1] - displayFrame.top - estimatedToastHeight);
        }*/

        toolTipToast.show();
    }




}
