package me.jfenn.wakeMeUp.adapters;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import io.reactivex.functions.Consumer;
import me.jfenn.androidutils.DimenUtils;
import me.jfenn.wakeMeUp.Alarmio;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.activities.AlarmActivity;
import me.jfenn.wakeMeUp.data.AlarmData;
import me.jfenn.wakeMeUp.data.SoundData;
import me.jfenn.wakeMeUp.data.TimerData;
import me.jfenn.wakeMeUp.dialogs.AlertDialog;
import me.jfenn.wakeMeUp.dialogs.SoundChooserDialog;
import me.jfenn.wakeMeUp.fragments.AlarmsFragment;
import me.jfenn.wakeMeUp.interfaces.SoundChooserListener;
import me.jfenn.wakeMeUp.utils.DoubleToLongConverter;
import me.jfenn.wakeMeUp.utils.FormatUtils;
import me.jfenn.wakeMeUp.views.DaySwitch;
import me.jfenn.wakeMeUp.views.DecimalPicker;
import me.jfenn.wakeMeUp.views.ProgressLineView;

import static android.view.View.GONE;

/*The origin code have been changed */
public class AlarmsAdapter extends RecyclerView.Adapter {

    public static final String TAG2 = "sortAlarms";
    public  String TAG="AlarmsAdapter";
    private static Alarmio alarmio;
    private static RecyclerView recycler;
    private SharedPreferences prefs;
    private AlarmManager alarmManager;
    private static FragmentManager fragmentManager;
    private List<TimerData> timers;
    private List<AlarmData> alarms;
    private int colorAccent = Color.WHITE;
    private int colorForeground = Color.TRANSPARENT;
    private int textColorPrimary = Color.WHITE;
    boolean setListenrOnExpandImage;
    SharedPreferences preferences;
    private int expandedPosition = -1;
    public static AlarmViewHolder alarmViewHolder;
    static AlarmData alarmData;
    private int position;
    private RecyclerView.ViewHolder holder;
    private boolean changed;
    private boolean refresh=true;
    private CompoundButton.OnCheckedChangeListener enableListener;
    private boolean toEnable;
    private boolean found;
    private int lastPosition;
    private int step;
    private static boolean ttwImageClicked;
    private static boolean moneyImageClicked;


    public AlarmsAdapter(Alarmio alarmio, RecyclerView recycler, FragmentManager fragmentManager) {
        Log.d(TAG,"AlarmsAdapter(Alarmio alarmio, RecyclerView recycler, FragmentManager fragmentManager)");
        this.alarmio = alarmio;
        this.recycler = recycler;
        this.prefs = alarmio.getPrefs();
        this.fragmentManager = fragmentManager;
        alarmManager = (AlarmManager) alarmio.getSystemService(Context.ALARM_SERVICE);
        timers = alarmio.getTimers();
        alarms = alarmio.getAlarms();
        for(AlarmData alarm: alarms){
            Log.d(TAG2,"alarm: "+alarm.getName(alarmio.getApplicationContext())+" id: "+alarm.getId()+"time: "+alarm.time.get(Calendar.HOUR_OF_DAY));
        }
        preferences = alarmio.getSharedPreferences("bottomSheet",Context.MODE_PRIVATE);


     }



    public static void dataChanged(){
        new AlarmsAdapter(alarmio,recycler,fragmentManager);
    }



    public void setColorAccent(int colorAccent) {
        this.colorAccent = colorAccent;
        notifyDataSetChanged();
    }


    public void setColorForeground(int colorForeground) {
        this.colorForeground = colorForeground;
        if (expandedPosition > 0)
            notifyItemChanged(expandedPosition);
    }

    public void setTextColorPrimary(int colorTextPrimary) {
        this.textColorPrimary = colorTextPrimary;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG," onCreateViewHolder(ViewGroup parent, int viewType)");

        if (viewType == 0)
            return new TimerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timer, parent, false));
        else {
            FrameLayout v = (FrameLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
            //View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm, parent, false);
            AlarmViewHolder alarmViewHolder = new AlarmViewHolder(v, parent.getContext());
            this.alarmViewHolder=alarmViewHolder;

             return alarmViewHolder;
        }

    }

    protected void postAndNotifyAdapter(final Handler handler, final RecyclerView recyclerView, final RecyclerView.Adapter adapter) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!recyclerView.isComputingLayout()) {
                    adapter.notifyDataSetChanged();
                } else {
                    postAndNotifyAdapter(handler, recyclerView, adapter);
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //Log.d(TAG,"onBindViewHolder "+position);

       this.holder=holder;
        this.position=position;
       SharedPreferences prefs = alarmio.getSharedPreferences("threshold", alarmio.MODE_PRIVATE );
        boolean snoozed=prefs.getBoolean("snoozed",false);

        if (getItemViewType(position) == 0) {
            final TimerViewHolder timerHolder = (TimerViewHolder) holder;

            if (timerHolder.runnable != null)
                timerHolder.handler.removeCallbacks(timerHolder.runnable);

            timerHolder.runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d(TAG,"***************************************************");
                        Log.d(TAG,"timerHolder.runnable run()");
                        TimerData timer = getTimer(timerHolder.getAdapterPosition());
                        String text = FormatUtils.formatMillis(timer.getRemainingMillis(),true);
                        timerHolder.time.setText(text.substring(0, text.length() - 3));
                        timerHolder.progress.update(1 - ((float) timer.getRemainingMillis() / timer.getDuration()));
                        timerHolder.handler.postDelayed(this, 1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            timerHolder.stop.setColorFilter(textColorPrimary);
            timerHolder.stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TimerData timer = getTimer(timerHolder.getAdapterPosition());
                    alarmio.removeTimer(timer);
                }
            });

            timerHolder.handler.post(timerHolder.runnable);
        } else {
            final AlarmViewHolder alarmHolder = (AlarmViewHolder) holder;
            final boolean isExpanded = position == expandedPosition;
            AlarmData alarm = getAlarm(position);
            SharedPreferences pref2=alarmio.getSharedPreferences("needToPay", Context.MODE_PRIVATE);
            SharedPreferences pref=alarmio.getSharedPreferences("threshold", Context.MODE_PRIVATE);

            boolean needToPay=pref2.getBoolean("needToPay",false);
             if(needToPay){
                 //alarm.setEnabled(alarmio,alarmManager,false);
                 //alarm.setWasEnabled(alarmio,alarmManager,wasEnabled);
                 alarmHolder.enable.setChecked(false);

            }
           if (snoozed) {
               AlarmsFragment.makeRemainingTimeGone();
                alarmViewHolder.lock.setVisibility(View.VISIBLE);
                alarmViewHolder.snoozeFrame.setVisibility(View.VISIBLE);
            }
            int volume=alarm.getVolume();
            if(volume==0)volume=80;
            alarmHolder.volumeSeekBar.setProgress(volume);
            alarm.setVolume(alarmio,alarmManager,volume);
            alarmHolder.name.setFocusableInTouchMode(isExpanded);
            alarmHolder.name.setCursorVisible(false);
            alarmHolder.name.clearFocus();
            alarmHolder.nameUnderline.setVisibility(isExpanded ? View.VISIBLE : GONE);
            alarmHolder.name.setText(alarm.getName(alarmio));
            alarmHolder.name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    getAlarm(alarmHolder.getAdapterPosition()).setName(alarmio, alarmHolder.name.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });
            alarmHolder.name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    alarmHolder.name.setCursorVisible(hasFocus && alarmHolder.getAdapterPosition() == expandedPosition);
                }
            });


            alarmHolder.enable.setOnCheckedChangeListener(null);
            alarmHolder.enable.setChecked(alarm.isEnabled);

            if (!snoozed) {
                alarmHolder.enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(AlarmsFragment.handler!=null&&AlarmsFragment.runnable!=null)AlarmsFragment.handler.removeCallbacks(AlarmsFragment.runnable);
                        alarm.setEnabled(alarmio, alarmManager, b);
                        AlarmsFragment.getNextAlarmString();
                        alarmHolder.enable.setChecked(b);
                        new Handler().post(new Runnable() {
                            @Override
                                public void run() {
                                alarmio.onAlarmsChanged();
                                alarmio.setAlarms();
                                AlarmsAdapter.this.notifyDataSetChanged();
                                AlarmsFragment.getNextAlarmString();

                            }
                        });

                    }
                });


            }else{
                boolean checked=alarm.isEnabled;
                alarmHolder.enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        alarmHolder.enable.setChecked(checked);
                    }
                });
            }



            Log.d("CONSTANT_TIME"," cons5: "+alarm.getConstantTime());
            alarmHolder.time.setText(FormatUtils.formatShort(alarmio, new Date(alarm.getConstantTime())));
            alarmHolder.nextTime.setVisibility(alarm.isEnabled ? View.VISIBLE : GONE);

            Calendar nextAlarm = alarm.getNext();
            if (alarm.isEnabled && nextAlarm != null) {
                Date nextAlarmTime = alarm.getNext().getTime();
                int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(nextAlarm.getTimeInMillis() - Calendar.getInstance().getTimeInMillis());
                alarmHolder.nextTime.setText(String.format(alarmio.getString(R.string.title_alarm_next), FormatUtils.format(nextAlarmTime, "MMMM d"), FormatUtils.formatUnit(alarmio, minutes)));
            }

            alarmHolder.indicators.setVisibility(isExpanded ? GONE : View.VISIBLE);
            if (isExpanded) {
                    if (!snoozed) {
                    Log.d("ALARM_ADAPTER","repeat6");
                    alarmHolder.repeat.setChecked(alarm.isRepeat());
                    alarmHolder.repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            alarmHolder.repeat.setChecked(b);
                            AlarmData alarm = getAlarm(alarmHolder.getAdapterPosition());
                            for (int i = 0; i < 7; i++) {
                                alarm.days[i] = b;
                            }
                            alarm.setDays(alarmio, alarm.days);
                            Transition transition = new AutoTransition();
                            transition.setDuration(150);
                            transition.excludeChildren(recycler,true);

                            TransitionManager.beginDelayedTransition(recycler, transition);

                            Handler handler=new Handler();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    notifyDataSetChanged();
                                }
                            });

                        }
                    });
                }else{
                    boolean b=getAlarm(alarmHolder.getAdapterPosition()).isRepeat();

                    alarmHolder.repeat.setChecked(b);
                    Log.d("ALARM_ADAPTER","repeat3 "+b);
                     alarmHolder.repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Log.d("ALARM_ADAPTER","repeat9 "+b);
                            alarmHolder.repeat.setChecked(!isChecked);
                        }
                    });
                }
                alarmHolder.days.setVisibility(alarm.isRepeat() ? View.VISIBLE : GONE);
                DaySwitch.OnCheckedChangeListener listener = new DaySwitch.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(DaySwitch daySwitch, boolean b) {
                        Log.d("ALARM_ADAPTER","dayswitch3");

                        AlarmData alarm = getAlarm(alarmHolder.getAdapterPosition());
                        alarm.days[alarmHolder.days.indexOfChild(daySwitch)] = b;
                        alarm.setDays(alarmio, alarm.days);

                        AlarmsFragment.getNextAlarmString();
                        AlarmsFragment.setNextAlarmNotification(alarmio.getApplicationContext(),alarm.getId(),alarm.getNext()!=null? FormatUtils.format(alarm.getNext().getTime(), "d MMMM HH:mm"):"",true);
                        AlarmsFragment.setNextAlarmNotification(daySwitch.getContext(),alarm.getId(),alarm.getNext()!=null? FormatUtils.format(alarm.getNext().getTime(), "d MMMM HH:mm"):"",false);

                        if (!alarm.isRepeat()) {
                            notifyItemChanged(alarmHolder.getAdapterPosition());
                        } else {
                            // if the view isn't going to change size in the recycler,
                            //   then I can just do this (prevents the background flickering as
                            //   the recyclerview attempts to smooth the transition)
                            bindViewHolder(alarmHolder, alarmHolder.getAdapterPosition());
                        }
                    }
                };
                //}
                for (int i = 0; i < 7; i++) {
                    Log.d("ALARM_ADAPTER","dayswitch4");

                    DaySwitch daySwitch = (DaySwitch) alarmHolder.days.getChildAt(i);
                    daySwitch.setChecked(alarm.days[i]);
                    if (!snoozed) {
                        daySwitch.setOnCheckedChangeListener(listener);
                    }
                    switch (i) {
                        case 0:
                        case 6:
                            daySwitch.setText("S");
                            break;
                        case 1:
                            daySwitch.setText("M");
                            break;
                        case 2:
                        case 4:
                            daySwitch.setText("T");
                            break;
                        case 3:
                            daySwitch.setText("W");
                            break;
                        case 5:
                            daySwitch.setText("F");

                    }
                }
                alarmHolder.preview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!snoozed) {
                            Intent intent = new Intent(v.getContext(), AlarmActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra(AlarmActivity.EXTRA_ALARM, alarm);
                            SharedPreferences.Editor p = v.getContext().getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
                            p.putBoolean("preview", true);
                            p.apply();
                            v.getContext().startActivity(intent);
                        }

                    }
                });
                alarmHolder.ringtoneImage.setImageResource(alarm.hasSound() ? R.drawable.ic_ringtone : R.drawable.ic_ringtone_disabled);
                alarmHolder.ringtoneImage.setAlpha(alarm.hasSound() ? 1 : 0.333f);
                alarmHolder.ringtoneText.setText(alarm.hasSound() ? alarm.getSound().getName() : alarmio.getString(R.string.title_sound_none));
                if (!snoozed) {
                    alarmHolder.ringtone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SoundChooserDialog dialog = new SoundChooserDialog();
                            dialog.setListener(new SoundChooserListener() {
                                @Override
                                public void onSoundChosen(SoundData sound) {

                                    int position = alarmHolder.getAdapterPosition();
                                    AlarmData alarm = getAlarm(position);
                                    alarm.setSound(alarmio, sound);
                                    notifyItemChanged(position);
                                }
                            });
                            dialog.show(fragmentManager, null);
                        }
                    });
                }

                AnimatedVectorDrawableCompat vibrateDrawable = AnimatedVectorDrawableCompat.create(alarmio, alarm.isVibrate ? R.drawable.ic_vibrate_to_none : R.drawable.ic_none_to_vibrate);
                alarmHolder.vibrateImage.setImageDrawable(vibrateDrawable);
                alarmHolder.vibrateImage.setAlpha(alarm.isVibrate ? 1 : 0.333f);
                if (!snoozed) {

                    alarmHolder.vibrate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlarmData alarm = getAlarm(alarmHolder.getAdapterPosition());
                            alarm.setVibrate(alarmio, !alarm.isVibrate);

                            AnimatedVectorDrawableCompat vibrateDrawable = AnimatedVectorDrawableCompat.create(alarmio, alarm.isVibrate ? R.drawable.ic_none_to_vibrate : R.drawable.ic_vibrate_to_none);
                            if (vibrateDrawable != null) {
                                alarmHolder.vibrateImage.setImageDrawable(vibrateDrawable);
                                vibrateDrawable.start();
                            } else
                                alarmHolder.vibrateImage.setImageResource(alarm.isVibrate ? R.drawable.ic_vibrate : R.drawable.ic_vibrate_none);

                            alarmHolder.vibrateImage.animate().alpha(alarm.isVibrate ? 1 : 0.333f).setDuration(250).start();
                            if (alarm.isVibrate)
                                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                        }
                    });
                }
            } else {
                alarmHolder.repeatIndicator.setAlpha(alarm.isRepeat() ? 1 : 0.333f);
                alarmHolder.soundIndicator.setAlpha(alarm.hasSound() ? 1 : 0.333f);
                alarmHolder.vibrateIndicator.setAlpha(alarm.isVibrate ? 1 : 0.333f);
            }

            alarmHolder.expandImage.animate().rotationX(isExpanded ? 180 : 0).start();
            alarmHolder.delete.setVisibility(isExpanded ? View.VISIBLE : GONE);
           if (!snoozed) {
                alarmHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlarmData alarm = getAlarm(alarmHolder.getAdapterPosition());
                        new AlertDialog(view.getContext())
                                .setContent(alarmio.getString(R.string.msg_delete_confirmation, alarm.getName(alarmio)))
                                .setListener(new AlertDialog.Listener() {
                                    @Override
                                    public void onDismiss(AlertDialog dialog, boolean ok) {
                                        if (ok) {
                                            alarmio.removeAlarm(getAlarm(alarmHolder.getAdapterPosition()));
                                            AlarmsFragment.getNextAlarmString();
                                            AlarmsFragment.setNextAlarmNotification(alarmio.getApplicationContext(),alarm.getId(),alarm.getNext()!=null? FormatUtils.format(alarm.getNext().getTime(), "d MMMM HH:mm"):"",true);

                                        }
                                    }
                                })
                                .show();
                    }
                });
            }
            /*alarmHolder.repeat.setTextColor(textColorPrimary);
            alarmHolder.delete.setTextColor(textColorPrimary);
            alarmHolder.ringtoneImage.setColorFilter(textColorPrimary);
            alarmHolder.vibrateImage.setColorFilter(textColorPrimary);
            alarmHolder.expandImage.setColorFilter(textColorPrimary);
            alarmHolder.repeatIndicator.setColorFilter(textColorPrimary);
            alarmHolder.soundIndicator.setColorFilter(textColorPrimary);
            alarmHolder.vibrateIndicator.setColorFilter(textColorPrimary);
            alarmHolder.nameUnderline.setBackgroundColor(textColorPrimary);

            alarmHolder.moneyImage.setColorFilter(textColorPrimary);
            alarmHolder.ttwImage.setColorFilter(textColorPrimary);
            alarmHolder.preview.setColorFilter(textColorPrimary);
            alarmHolder.lock.setColorFilter(textColorPrimary);
            */
            boolean isPunished=alarm.isPunished();
            final double[] money = {alarm.getMoney()};
            final int[] ttw = {alarm.getTimeToWakeP()};
            if(!isPunished) {
                alarmHolder.ttwLayout.setVisibility(GONE);
                alarmHolder.moneyLayout.setVisibility(GONE);
            }else{
                alarmHolder.ttwLayout.setVisibility(View.VISIBLE);
                alarmHolder.moneyLayout.setVisibility(View.VISIBLE);
            }
            double moneyVal= DoubleToLongConverter.longToDouble(pref.getLong("moneyValue",0));
            alarmHolder.moneyDesc.setText(pref.getString("coinSymball",""));
            alarmHolder.moneyPicker.setVisibility(GONE);
            alarmHolder.moneyPicker.isMoneyPicker(true);
            if(isPunished)alarmHolder.moneyPicker.setNumber(money[0]);
           alarmHolder.moneyPicker.setStepNumber(alarm.getStep());
            alarmHolder.moneyPicker.setMoneyValue(moneyVal);
            alarmHolder.moneyPicker.setInitialNumber(1);
            step=alarm.getStep();
            alarmHolder.moneyPicker.setFinalNumber(DoubleToLongConverter.longToDouble(pref.getLong("price010",0)));
            alarmHolder.ttwPicker.isMoneyPicker(false);
            alarmHolder.ttwPicker.setNumber(ttw[0]);
             if (!snoozed) {
                 Log.d(TAG,"m!snoozed ");

                 alarmHolder.moneyPicker.setOnValueChangeListener(new DecimalPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(DecimalPicker view, double oldValue, double newValue) {
                        Log.d(TAG,"moneyPicker onValueChange "+newValue);
                        money[0] = newValue;
                        alarm.setMoney(alarmio, alarmManager, newValue);
                        alarm.setMoneyInDollars(alarmio, alarmManager, oldValue<newValue?++step:--step);
                        moneyImageClicked=false;

                    }


                });
                 alarmHolder.ttwPicker.setOnValueChangeListener(new DecimalPicker.OnValueChangeListener() {
                     @Override
                     public void onValueChange(DecimalPicker view, double oldValue, double newValue) {
                         ttw[0] = (int)newValue;
                         alarm.setTimeToWakeP(alarmio, alarmManager, (int)newValue);
                         alarm.setTimeToWake(alarmio, alarmManager, (int)newValue);
                         ttwImageClicked=false;
                     }
                 });
                 alarmHolder.volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                     @Override
                     public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                     }

                     @Override
                     public void onStartTrackingTouch(SeekBar seekBar) {

                     }

                     @Override
                     public void onStopTrackingTouch(SeekBar seekBar) {
                         alarm.setVolume(alarmio, alarmManager, seekBar.getProgress());
                         alarmHolder.volumeSeekBar.setProgress(seekBar.getProgress());
                     }
                 });
            }
             else{
                 Log.d(TAG,"snoozed ");

             }
            alarmHolder.moneyText.setVisibility(View.VISIBLE);
            alarmHolder.moneyText.setText(String.valueOf(alarmHolder.moneyPicker.getNumber()));
            alarmHolder.ttwText.setText(String.valueOf((int)alarmHolder.ttwPicker.getNumber()));


            if(!snoozed){
            alarmHolder.moneyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moneyImageClicked=true;
                    if (alarmHolder.moneyText.getVisibility() == View.VISIBLE) {

                        alarmHolder.itemView.setOnClickListener(null);
                        alarmHolder.expandImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alarmHolder.ttwText.setText(String.valueOf((int)ttw[0]));
                                alarmHolder.moneyText.setText(String.valueOf(money[0]));

                                expandedPosition = isExpanded ? -1 : alarmHolder.getAdapterPosition();

                                Transition transition = new AutoTransition();
                                transition.setDuration(250);
                                transition.excludeChildren(recycler,true);

                                TransitionManager.beginDelayedTransition(recycler, transition);

                                notifyDataSetChanged();


                            }
                        });

                        alarmHolder.moneyPicker.setVisibility(View.VISIBLE);
                        alarmHolder.moneyText.setVisibility(View.INVISIBLE);
                    } else {
                        alarmHolder.ttwText.setText(String.valueOf((int)ttw[0]));
                        alarmHolder.moneyText.setText(String.valueOf(money[0]));
                        //TODO

                        int position = alarmHolder.getAdapterPosition();
                        AlarmData alarm = getAlarm(position);
                        //alarm.setTimeToWakeP(alarmio, alarmManager, ttw[0]);
                        //alarm.setTimeToWake(alarmio, alarmManager, ttw[0]);

                        notifyItemChanged(position);

                        alarmHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                expandedPosition = isExpanded ? -1 : alarmHolder.getAdapterPosition();

                                Transition transition = new AutoTransition();
                                transition.setDuration(250);
                                transition.excludeChildren(recycler,true);

                                TransitionManager.beginDelayedTransition(recycler, transition);

                                notifyDataSetChanged();
                            }
                        });
                        alarmHolder.moneyPicker.setVisibility(View.INVISIBLE);
                        alarmHolder.moneyText.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

            alarmHolder.expandImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alarmHolder.ttwText.setText(String.valueOf((int)ttw[0]));
                    alarmHolder.moneyText.setText(String.valueOf(money[0]));
                    expandedPosition = isExpanded ? -1 : alarmHolder.getAdapterPosition();

                    Transition transition = new AutoTransition();
                    transition.setDuration(250);
                    transition.excludeChildren(recycler,true);

                    TransitionManager.beginDelayedTransition(recycler, transition);

                    notifyDataSetChanged();


                }
            });
                alarmHolder.ttwPicker.setVisibility(GONE);
                alarmHolder.ttwText.setVisibility(View.VISIBLE);

                if(!snoozed) {
                    alarmHolder.ttwImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ttwImageClicked=true;
                            if (alarmHolder.ttwText.getVisibility() == View.VISIBLE) {
                                alarmHolder.itemView.setOnClickListener(null);
                                alarmHolder.expandImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        alarmHolder.ttwText.setText(String.valueOf((int)ttw[0]));
                                        alarmHolder.moneyText.setText(String.valueOf(money[0]));

                                        expandedPosition = isExpanded ? -1 : alarmHolder.getAdapterPosition();

                                        Transition transition = new AutoTransition();
                                        transition.setDuration(250);
                                        transition.excludeChildren(recycler,true);

                                        TransitionManager.beginDelayedTransition(recycler, transition);

                                        notifyDataSetChanged();


                                    }
                                });
                                alarmHolder.ttwPicker.setVisibility(View.VISIBLE);
                                alarmHolder.ttwText.setVisibility(View.INVISIBLE);
                            } else {

                                alarmHolder.ttwText.setText(String.valueOf((int)ttw[0]));
                                alarmHolder.moneyText.setText(String.valueOf(money[0]));
                                //TODO
                                int position = alarmHolder.getAdapterPosition();
                                AlarmData alarm = getAlarm(position);
                                //alarm.setTimeToWake(alarmio, alarmManager, ttw[0]);
                                //alarm.setTimeToWakeP(alarmio, alarmManager, ttw[0]);

                                notifyItemChanged(position);

                                alarmHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        expandedPosition = isExpanded ? -1 : alarmHolder.getAdapterPosition();

                                        Transition transition = new AutoTransition();
                                        transition.setDuration(250);
                                        transition.excludeChildren(recycler,true);

                                        TransitionManager.beginDelayedTransition(recycler, transition);

                                        notifyDataSetChanged();
                                    }
                                });
                                alarmHolder.ttwPicker.setVisibility(View.INVISIBLE);
                                alarmHolder.ttwText.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                }
                int visibility = isExpanded ? View.VISIBLE : GONE;

            Aesthetic.Companion.get()
                    .colorPrimary()
                    .take(1)
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Exception {
                            ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), isExpanded ? integer : colorForeground, isExpanded ? colorForeground : integer);
                           boolean snoozed=prefs.getBoolean("snoozed",false);

                            if (snoozed) {
                                alarmHolder.itemView.setBackgroundColor(Color.GRAY);
                            } else {
                                alarmHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
                            }
                        }
                    });
                if (visibility != alarmHolder.extra.getVisibility()) {
                    alarmHolder.extra.setVisibility(visibility);
                    alarmHolder.preview.setVisibility(visibility);
                    Aesthetic.Companion.get()
                            .colorPrimary()
                            .take(1)
                            .subscribe(new Consumer<Integer>() {
                                @Override
                                public void accept(Integer integer) throws Exception {
                                    ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), isExpanded ? integer : colorForeground, isExpanded ? colorForeground : integer);


                                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                        @Override
                                        public void onAnimationUpdate(ValueAnimator animation) {
                                            alarmHolder.itemView.setBackgroundColor((int) animation.getAnimatedValue());
                                        }
                                    });
                                    animator.addListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {
                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            alarmHolder.itemView.setBackgroundColor(isExpanded ? colorForeground : Color.TRANSPARENT);
                                           // alarmHolder.itemView.setBackgroundColor(colorForeground);

                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {
                                        }
                                    });
                                    animator.start();
                                }
                            });

                    ValueAnimator animator = ValueAnimator.ofFloat(isExpanded ? 0 : DimenUtils.dpToPx(2), isExpanded ? DimenUtils.dpToPx(2) : 0);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            ViewCompat.setElevation(alarmHolder.itemView, (float) animation.getAnimatedValue());
                        }
                    });
                    animator.start();
                } else {
                    alarmHolder.itemView.setBackgroundColor(isExpanded ? colorForeground : Color.TRANSPARENT);
                    //alarmHolder.itemView.setBackgroundColor(colorForeground);
                    ViewCompat.setElevation(alarmHolder.itemView, isExpanded ? DimenUtils.dpToPx(2) : 0);
                }


                alarmHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        expandedPosition = isExpanded ? -1 : alarmHolder.getAdapterPosition();

                        Transition transition = new AutoTransition();
                        transition.setDuration(250);
                        transition.excludeChildren(recycler,true);
                        TransitionManager.beginDelayedTransition(recycler, transition);
                        notifyDataSetChanged();
                    }
                });

            }
        Log.d("ALARM_ADAPTER","on exit BindViewHolder()");

    }


    @Override
    public int getItemViewType(int position) {
        return position < timers.size() ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return timers.size() + alarmio.getAlarms().size();
    }

    private TimerData getTimer(int position) {
        return timers.get(position);
    }

    private AlarmData getAlarm(int position) {
       return  alarmio.getAlarms().get(position - timers.size());
        //return alarms.get(position - timers.size());
    }

    public static void onSnooze(Context context,int snoozeTime) {
        setVisibilityForViews();
        cancelOnClickListener();
        AlarmsFragment.setSnoozeNotification(context,snoozeTime,false);

    }

    private static void cancelOnClickListener() {
        //alarmViewHolder.enable.setChecked(alarmData.isEnabled);
        if(alarmViewHolder!=null) {
            boolean checked = alarmViewHolder.enable.isChecked();
            alarmViewHolder.enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    alarmViewHolder.enable.setChecked(checked);
                }
            });

            boolean checked2 = alarmViewHolder.repeat.isChecked();

            alarmViewHolder.repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    alarmViewHolder.repeat.setChecked(checked2);
                }
            });
            for (int i = 0; i < 7; i++) {


                DaySwitch daySwitch = (DaySwitch) alarmViewHolder.days.getChildAt(i);
                boolean bi = daySwitch.isChecked();
                daySwitch.setOnCheckedChangeListener(new DaySwitch.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(DaySwitch daySwitch, boolean b) {

                        daySwitch.setChecked(bi);
                    }
                });
            }
            alarmViewHolder.ringtone.setOnClickListener(null);
            alarmViewHolder.vibrate.setOnClickListener(null);
            alarmViewHolder.delete.setOnClickListener(null);
            alarmViewHolder.ttwImage.setOnClickListener(null);
            alarmViewHolder.moneyImage.setOnClickListener(null);
            alarmViewHolder.volumeSeekBar.setOnSeekBarChangeListener(null);
            if (!moneyImageClicked && !ttwImageClicked) {
                alarmViewHolder.volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    int originalProgress = alarmViewHolder.volumeSeekBar.getProgress();

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        originalProgress = seekBar.getProgress();
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int arg1, boolean fromUser) {

                        if (fromUser == true) {
                            seekBar.setProgress(originalProgress);
                        }
                    }
                });
            }

        }
    }

    private static void setInvisibilityForViews() {
        alarmViewHolder.lock.setVisibility(View.INVISIBLE);
        alarmViewHolder.snoozeFrame.setVisibility(View.INVISIBLE);

    }

    private static void setVisibilityForViews() {
        if(alarmViewHolder!=null) {
            alarmViewHolder.lock.setVisibility(View.VISIBLE);
            alarmViewHolder.snoozeFrame.setVisibility(View.VISIBLE);
        }

    }

    public static class TimerViewHolder extends RecyclerView.ViewHolder {

        private final Handler handler = new Handler();
        private Runnable runnable;

        private TextView time;
        private ImageView stop;
        private ProgressLineView progress;

        public TimerViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            stop = itemView.findViewById(R.id.stop);
            progress = itemView.findViewById(R.id.progress);
        }
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {

        private final View view;
          public View nameContainer;
        private EditText name;
        private View nameUnderline;
        private SwitchCompat enable;
        private TextView time;
        private TextView nextTime;
        private View extra;
        private AppCompatCheckBox repeat;
        private LinearLayout days;
        private View ringtone;
        private ImageView ringtoneImage;
        private TextView ringtoneText;
        private View vibrate;
        private ImageView vibrateImage;
        private ImageView expandImage;
        private TextView delete;
        private View indicators;
        private ImageView repeatIndicator;
        private ImageView soundIndicator;
        private ImageView vibrateIndicator;

        private ImageView preview;
        private ImageView moneyImage;
        private TextView moneyText;
        private TextView moneyDesc;
       private DecimalPicker moneyPicker;

        private ImageView ttwImage;
        private TextView ttwText;
        private TextView ttwDesc;
        private DecimalPicker ttwPicker;


        private ImageView lock;
        private FrameLayout snoozeFrame;
        private Context context;
        private SeekBar volumeSeekBar;


        private LinearLayout moneyLayout,ttwLayout;

        public AlarmViewHolder(View v,Context context) {
            super(v);
            this.view=v;
            this.context=context;
             SharedPreferences prefs = context.getSharedPreferences("threshold", context.MODE_PRIVATE );
             SharedPreferences pref2=context.getSharedPreferences("needToPay", Context.MODE_PRIVATE);

            boolean snoozed=prefs.getBoolean("snoozed",false);

            Log.d("ALARM_ADAPTER","AlarmViewHolder(View v)");
            nameContainer = (View) v.findViewById(R.id.nameContainer);
            name = (EditText) v.findViewById(R.id.name);
            nameUnderline = (View) v.findViewById(R.id.underline);
            enable = (SwitchCompat) v.findViewById(R.id.enable);
            time = (TextView) v.findViewById(R.id.time);
            nextTime = (TextView) v.findViewById(R.id.nextTime);
            extra = (View) v.findViewById(R.id.extra);
            repeat = (AppCompatCheckBox) v.findViewById(R.id.repeat);
            days = (LinearLayout) v.findViewById(R.id.days);
            ringtone = (View) v.findViewById(R.id.ringtone);
            ringtoneImage = (ImageView) v.findViewById(R.id.ringtoneImage);
            ringtoneText = v.findViewById(R.id.ringtoneText);
            vibrate = v.findViewById(R.id.vibrate);
            vibrateImage = v.findViewById(R.id.vibrateImage);
            expandImage = v.findViewById(R.id.expandImage);
            delete = v.findViewById(R.id.delete);
            indicators = v.findViewById(R.id.indicators);
            repeatIndicator = v.findViewById(R.id.repeatIndicator);
            soundIndicator = v.findViewById(R.id.soundIndicator);
            vibrateIndicator = v.findViewById(R.id.vibrateIndicator);
            volumeSeekBar=(SeekBar)v.findViewById(R.id.volume);
            moneyText=v.findViewById(R.id.moneyText);
            moneyDesc=v.findViewById(R.id.moneyDesc);
            moneyPicker=v.findViewById(R.id.moneyPicker);
            moneyImage=v.findViewById(R.id.moneyImage);

            moneyLayout=v.findViewById(R.id.money);
            ttwLayout=v.findViewById(R.id.ttw);

            preview=v.findViewById(R.id.preview);
            ttwText=v.findViewById(R.id.ttwText);
            ttwDesc=v.findViewById(R.id.ttwTextDesc);
            ttwPicker=v.findViewById(R.id.ttwPicker);
            ttwImage=v.findViewById(R.id.ttwImage);
            lock=v.findViewById(R.id.lock);
            snoozeFrame=v.findViewById(R.id.snooze_frame);
                if(snoozed) {
                    setVisibilityForViewsInAlarmViewHolder();
                }else{
                    setInvisibilityForViewsInAlarmViewHolder();
                }


          boolean needToPay=pref2.getBoolean("needToPay",false);
            //if(needToPay)enable.setChecked(false);

        }

        private void setInvisibilityForViewsInAlarmViewHolder() {
            lock.setVisibility(View.INVISIBLE);
            snoozeFrame.setVisibility(View.INVISIBLE);

        }

        public void setVisibilityForViewsInAlarmViewHolder(){
            lock.setVisibility(View.VISIBLE);
            snoozeFrame.setVisibility(View.VISIBLE);
            //enable.setChecked(alarmData.isEnabled);
            boolean checked=enable.isChecked();
            enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    enable.setChecked(checked);
                }
            });

            boolean repeatCheked=repeat.isChecked();


            /*repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d("ALARM_ADAPTER","repeat11");
                    repeat.setChecked(repeatCheked);
                }
            });*/
            for (int i = 0; i < 7; i++) {

                DaySwitch daySwitch = (DaySwitch) days.getChildAt(i);
                daySwitch.setOnCheckedChangeListener(new DaySwitch.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(DaySwitch daySwitch, boolean b) {
                        boolean dayChecked=daySwitch.isChecked();
                         daySwitch.setChecked(!b);
                    }
                });
            }
            preview.setOnClickListener(null);
            ringtone.setOnClickListener(null);
            volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                int originalProgress=volumeSeekBar.getProgress();

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                     originalProgress = seekBar.getProgress();
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int arg1, boolean fromUser) {

                    if(fromUser == true){
                        seekBar.setProgress(originalProgress);
                    }
                }
            });
            vibrate.setOnClickListener(null);
            delete.setOnClickListener(null);
            ttwImage.setOnClickListener(null);
            moneyImage.setOnClickListener(null);
            volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                int originalProgress=volumeSeekBar.getProgress();

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                     originalProgress = seekBar.getProgress();
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int arg1, boolean fromUser) {

                    if(fromUser == true){
                        seekBar.setProgress(originalProgress);
                    }
                }
            });

        }

    }



}
