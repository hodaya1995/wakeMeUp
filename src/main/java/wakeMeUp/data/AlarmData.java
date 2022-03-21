package me.jfenn.wakeMeUp.data;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.Nullable;
import me.jfenn.wakeMeUp.activities.MainActivity;
import me.jfenn.wakeMeUp.adapters.AlarmsAdapter;
import me.jfenn.wakeMeUp.receivers.AlarmReceiver;
/*The origin code have been changed */
public class AlarmData implements Parcelable{

    private static final String TAG = "AlarmData";
    private Context context;
    private int id;
    public String name;
    public Calendar time;
    public boolean isEnabled = true;
    public boolean[] days = new boolean[7];
    public boolean isVibrate = true;
    public SoundData sound;
    int timeToWake;
    int timeToWakeP;
    double money;
    boolean needVisibilty;
    int position;
    Long constantTime;
    boolean wasEnabled;
    int volume;
    private int step;
    boolean isPunished;

    public int getVolume() {
        return volume;
    }

    public boolean getNeedVisibilty() {
        return needVisibilty;
    }

    public boolean isPunished() {
        return isPunished;
    }

    public boolean wasEnabled(){
        return wasEnabled;
    }


    public int getTimeToWake() {
        return timeToWake;
    }

    public int getStep() {
        return step;
    }


    public int getTimeToWakeP() {
        return timeToWakeP;
    }

    public double getMoney() {
        return money;
    }

    public long getConstantTime() {
        return constantTime;
    }
    public int getPosition() {
        return position;
    }

    public AlarmData(int id, Calendar time) {
        Log.d(AlarmsAdapter.TAG2,"AlarmData Calendar id "+id);
        this.id = id;
        this.time = time;

    }


    public AlarmData(int id, Context context) {

        this.id = id;
        this.context=context;
        name = PreferenceData.ALARM_NAME.getSpecificOverriddenValue(context, getName(context), id);

        time = Calendar.getInstance();
        time.setTimeInMillis((long) PreferenceData.ALARM_TIME.getSpecificValue(context, id));
        Log.d(AlarmsAdapter.TAG2,"AlarmData Context id "+id+" time: "+time.get(Calendar.HOUR_OF_DAY));

        isEnabled = PreferenceData.ALARM_ENABLED.getSpecificValue(context, id);
        for (int i = 0; i < 7; i++) {
            days[i] = PreferenceData.ALARM_DAY_ENABLED.getSpecificValue(context, id, i);
        }
        isVibrate = PreferenceData.ALARM_VIBRATE.getSpecificValue(context, id);
       // sound = SoundData.fromString(PreferenceData.ALARM_SOUND.getSpecificOverriddenValue(context, PreferenceData.ALARM_RINGTONE.getValue(context, ""), id))==null?SoundData.fromString(PreferenceData.ALARM_SOUND.getSpecificOverriddenValue(context, PreferenceData.DEFAULT_ALARM_RINGTONE.getValue(context, ""), id)):SoundData.fromString(PreferenceData.ALARM_SOUND.getSpecificOverriddenValue(context, PreferenceData.ALARM_RINGTONE.getValue(context, ""), id));
        sound =SoundData.fromString(PreferenceData.ALARM_SOUND.getSpecificOverriddenValue(context, PreferenceData.ALARM_RINGTONE.getValue(context, ""), id));
        timeToWake=PreferenceData.TIME_TO_WAKE.getSpecificOverriddenValue(context,PreferenceData.TIME_TO_WAKE.getValue(context,10),id);
        timeToWakeP=PreferenceData.TIME_TO_WAKE_P.getSpecificOverriddenValue(context,PreferenceData.TIME_TO_WAKE_P.getValue(context,10),id);

        money= Double.valueOf(PreferenceData.MONEY.getSpecificOverriddenValue(context,PreferenceData.MONEY.getValue(context,""+7.2),id));
        needVisibilty=PreferenceData.NEED_VISIBILITY.getSpecificOverriddenValue(context,false,id);
        position=PreferenceData.POSITION.getSpecificOverriddenValue(context,id,id);
        constantTime=PreferenceData.CONSTANT_TIME.getSpecificValue(context,id);
        wasEnabled = PreferenceData.ALARM_WAS_ENABLED.getSpecificValue(context, id);
        volume=PreferenceData.VOLUME.getSpecificOverriddenValue(context,PreferenceData.VOLUME.getValue(context,80),id);
        step=PreferenceData.STEP.getSpecificOverriddenValue(context,PreferenceData.STEP.getValue(context,2),id);

        isPunished = PreferenceData.IS_PUNISHED.getSpecificValue(context, id);
    }

    /**
     * Moves this AlarmData's preferences to another "id".
     *
     * @param id            The new id to be assigned
     * @param context       An active context instance.
     */
    public void onIdChanged(int id, Context context,boolean remove) {
        Log.d(AlarmsAdapter.TAG2,"onIdChanged id "+id+" "+(time != null ? time.get(Calendar.HOUR_OF_DAY):-1));

        PreferenceData.ALARM_NAME.setValue(context, getName(context), id);
        PreferenceData.ALARM_TIME.setValue(context, time != null ? time.getTimeInMillis() : null, id);
        PreferenceData.ALARM_ENABLED.setValue(context, isEnabled, id);
        for (int i = 0; i < 7; i++) {
            PreferenceData.ALARM_DAY_ENABLED.setValue(context, days[i], id, i);
        }
        PreferenceData.ALARM_VIBRATE.setValue(context, isVibrate, id);
        PreferenceData.ALARM_SOUND.setValue(context, sound != null ? sound.toString() : null, id);

        PreferenceData.ALARM_RINGTONE.setValue(context,  sound != null ? sound.toString() : null, id);

        PreferenceData.TIME_TO_WAKE.setValue(context, timeToWake, id);
        PreferenceData.TIME_TO_WAKE_P.setValue(context, timeToWakeP, id);

        PreferenceData.MONEY.setValue(context, ""+money, id);
        PreferenceData.VOLUME.setValue(context, volume, id);

        PreferenceData.NEED_VISIBILITY.setValue(context,needVisibilty,id);
        PreferenceData.POSITION.setValue(context,position,id);
        PreferenceData.CONSTANT_TIME.setValue(context,constantTime,id);
        PreferenceData.ID.setValue(context,id,id);
        PreferenceData.ALARM_WAS_ENABLED.setValue(context, wasEnabled, id);
        PreferenceData.STEP.setValue(context,step,id);

        PreferenceData.IS_PUNISHED.setValue(context,isPunished,id);

        onRemoved(context);


        this.id = id;

        //if(remove) {
            if (isEnabled) set(context, (AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
       // }
    }

    /**
     * Removes this AlarmData's preferences.
     *
     * @param context       An active context instance.
     */
    public void onRemoved(Context context) {
        Log.d(AlarmsAdapter.TAG2,"onRemoved id "+id);

        cancel(context, (AlarmManager) context.getSystemService(Context.ALARM_SERVICE));

        PreferenceData.ALARM_NAME.setValue(context, null, id);
        PreferenceData.ALARM_TIME.setValue(context, null, id);
        PreferenceData.ALARM_ENABLED.setValue(context, null, id);
        for (int i = 0; i < 7; i++) {
            PreferenceData.ALARM_DAY_ENABLED.setValue(context, null, id, i);
        }
        PreferenceData.ALARM_VIBRATE.setValue(context, null, id);
        PreferenceData.ALARM_SOUND.setValue(context, null, id);

        PreferenceData.ALARM_RINGTONE.setValue(context, null, id);

        PreferenceData.TIME_TO_WAKE.setValue(context, null, id);
        PreferenceData.TIME_TO_WAKE_P.setValue(context, null, id);

        PreferenceData.MONEY.setValue(context, null, id);
        PreferenceData.VOLUME.setValue(context, null, id);

        PreferenceData.NEED_VISIBILITY.setValue(context,false,id);
        PreferenceData.POSITION.setValue(context,null,id);
        PreferenceData.CONSTANT_TIME.setValue(context,null,id);
        PreferenceData.ID.setValue(context,null,id);
        PreferenceData.ALARM_WAS_ENABLED.setValue(context, null, id);

        PreferenceData.STEP.setValue(context,null,id);

        PreferenceData.IS_PUNISHED.setValue(context,null,id);

    }

    /**
     * Returns the user-defined "name" of the alarm, defaulting to
     * "Alarm (1..)" if unset.
     *
     * @param context       An active context instance.
     * @return              The alarm name, as a string.
     */
    public String getName(Context context) {
        if (name != null)
            return name;
       // else return context.getString(R.string.title_alarm, id + 1);
        else return "Wake-up";
    }

    /**
     * Returns whether the alarm should repeat on a set interval
     * or not.
     *
     * @return              If repeat is enabled for this alarm.
     */
    public boolean isRepeat() {
        for (boolean day : days) {
            if (day)
                return true;
        }

        return false;
    }

    /**
     * Sets the user-defined "name" of the alarm.
     *
     * @param context       An active context instance.
     * @param name          The new name to be set.
     */
    public void setName(Context context, String name) {
        this.name = name;
        PreferenceData.ALARM_NAME.setValue(context, name, id);
    }

    /**
     * Change the scheduled alarm time,
     *
     * @param context       An active context instance.
     * @param manager       An AlarmManager to schedule the alarm on.
     * @param timeMillis    The UNIX time (in milliseconds) that the alarm should ring at.
     *                      This is independent to days; if the time correlates to 9:30 on
     *                      a Tuesday when the alarm should only repeat on Wednesdays and
     *                      Thursdays, then the alarm will next ring at 9:30 on Wednesday.
     */
    public void setTime(Context context, AlarmManager manager, long timeMillis) {
        time.setTimeInMillis(timeMillis);
        PreferenceData.ALARM_TIME.setValue(context, timeMillis, id);
        //if (isEnabled) set(context, manager);
    }

    public void setTimeToWake(Context context, AlarmManager manager, int timeToWake) {
        this.timeToWake = timeToWake;
        PreferenceData.TIME_TO_WAKE.setValue(context, timeToWake, id);
        //if (isEnabled) set(context, manager);
    }


    public void setTimeToWakeP(Context context, AlarmManager manager, int timeToWakeP) {
        this.timeToWakeP = timeToWakeP;
        PreferenceData.TIME_TO_WAKE_P.setValue(context, timeToWakeP, id);
        //if (isEnabled) set(context, manager);
    }


    public void setMoney(Context context, AlarmManager manager, double money) {
        this.money = money;
        PreferenceData.MONEY.setValue(context, ""+money, id);
        //if (isEnabled) set(context, manager);
    }
    public void setMoneyInDollars(Context context, AlarmManager manager, int step) {
        this.step = step;
        PreferenceData.STEP.setValue(context, step, id);
        //if (isEnabled) set(context, manager);
    }
    public void setVolume(Context context, AlarmManager manager, int volume) {
        this.volume = volume;
        PreferenceData.VOLUME.setValue(context, volume, id);
        //if (isEnabled) set(context, manager);
    }

    public void setPosition(Context context, AlarmManager manager, int position) {
        this.position = position;
        PreferenceData.POSITION.setValue(context, position, id);
       // if (isEnabled) set(context, manager);
    }

    public void setVisibilty(Context context, AlarmManager manager, boolean needVisibilty) {
        this.needVisibilty = needVisibilty;
        PreferenceData.NEED_VISIBILITY.setValue(context, needVisibilty, id);
        //if (isEnabled) set(context, manager);
    }
    /**
     * Set whether the alarm is enabled.
     *
     * @param context       An active context instance.
     * @param manager       An AlarmManager to schedule the alarm on.
     * @param isEnabled     Whether the alarm is enabled.
     */
    public void setEnabled(Context context, AlarmManager manager, boolean isEnabled) {
        this.isEnabled = isEnabled;
        this.wasEnabled=isEnabled;
        PreferenceData.ALARM_ENABLED.setValue(context, isEnabled, id);
        PreferenceData.ALARM_WAS_ENABLED.setValue(context, isEnabled, id);

        if (isEnabled) set(context, manager);
        else cancel(context, manager);
    }

    public void setWasEnabled(Context context, AlarmManager manager, boolean wasEnabled) {
        this.wasEnabled = wasEnabled;
        PreferenceData.ALARM_WAS_ENABLED.setValue(context, wasEnabled, id);
        //if (isEnabled) set(context, manager);
        //else cancel(context, manager);
    }
    public void setPunished(Context context,  boolean isPunished) {
        this.isPunished = isPunished;
        PreferenceData.IS_PUNISHED.setValue(context, isPunished, id);
    }
    /**
     * Sets the days of the week that the alarm should ring on. If
     * no days are specified, the alarm will act as a one-time alert
     * and will not repeat.
     *
     * @param context       An active context instance.
     * @param days          A boolean array, with a length of 7 (seven days of the week)
     *                      specifying whether repeat is enabled for that day.
     */
    public void setDays(Context context, boolean[] days) {
        this.days = days;

        for (int i = 0; i < 7; i++) {
            PreferenceData.ALARM_DAY_ENABLED.setValue(context, days[i], id, i);
        }
    }

    /**
     * Set whether the alarm should vibrate.
     *
     * @param context       An active context instance.
     * @param isVibrate     Whether the alarm should vibrate.
     */
    public void setVibrate(Context context, boolean isVibrate) {
        this.isVibrate = isVibrate;
        PreferenceData.ALARM_VIBRATE.setValue(context, isVibrate, id);
    }



    /**
     * Return whether the alarm has a sound or not.
     *
     * @return              A boolean defining whether a sound has been set
     *                      for the alarm.
     */
    public boolean hasSound() {
        return sound != null;
    }







    /**
     * Get the [SoundData](./SoundData) sound specified for the alarm.
     *
     * @return              An instance of SoundData describing the sound that
     *                      the alarm should make (or null).
     */
    @Nullable
    public SoundData getSound() {
        return sound;
    }


    /**
     * Set the sound that the alarm should make.
     *
     * @param context       An active context instance.
     * @param sound         A [SoundData](./SoundData) defining the sound that
     *                      the alarm should make.
     */
    public void setSound(Context context, @Nullable SoundData sound) {
        this.sound = sound;
        PreferenceData.ALARM_SOUND.setValue(context, sound != null ? sound.toString() : null, id);
    }

    /**
     * Get the next time that the alarm should wring.
     *
     * @return              A Calendar object defining the next time that the alarm should ring at.
     * @see                 [java.util.Calendar Documentation](https://developer.android.com/reference/java/util/Calendar)
     */
    @Nullable
    public Calendar getNext() {
        if (isEnabled) {
            Calendar now = Calendar.getInstance();
            Calendar next = Calendar.getInstance();
            next.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
            next.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
            next.set(Calendar.SECOND, 0);

            while (now.after(next))
                next.add(Calendar.DATE, 1);

            if (isRepeat()) {
                int nextDay = next.get(Calendar.DAY_OF_WEEK) - 1; // index on 0-6, rather than the 1-7 returned by Calendar

                for (int i = 0; i < 7 && !days[nextDay]; i++) {
                    nextDay++;
                    nextDay %= 7;
                }

                next.set(Calendar.DAY_OF_WEEK, nextDay + 1); // + 1 = back to 1-7 range

                while (now.after(next))
                    next.add(Calendar.DATE, 7);
            }


            return next;
        }

        return null;
    }

    /**
     * Set the next time for the alarm to ring.
     *
     * @param context       An active context instance.
     * @param manager       The AlarmManager to schedule the alarm on.
     * @return              The next [Date](https://developer.android.com/reference/java/util/Date)
     *                      at which the alarm will ring.
     */
    public Date set(Context context, AlarmManager manager) {

        Calendar nextTime = getNext();

        setAlarm(context, manager, nextTime.getTimeInMillis());
        return nextTime.getTime();
    }

    /**
     * Schedule a time for the alarm to ring at.
     *
     * @param context       An active context instance.
     * @param manager       The AlarmManager to schedule the alarm on.
     * @param timeMillis    A UNIX timestamp specifying the next time for the alarm to ring.
     */
    private void setAlarm(Context context, AlarmManager manager, long timeMillis) {




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    timeMillis, getIntent(context));
        }


        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            manager.setAlarmClock(
                    new AlarmManager.AlarmClockInfo(
                            timeMillis,
                            PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0)
                    ),
                    getIntent(context)
            );
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                manager.setExact(AlarmManager.RTC_WAKEUP, timeMillis, getIntent(context));

            }

            else{
                manager.set(AlarmManager.RTC_WAKEUP, timeMillis, getIntent(context));

            }


            Intent intent = new Intent("android.intent.action.ALARM_CHANGED");
            intent.putExtra("alarmSet", true);
            context.sendBroadcast(intent);
        }

       // manager.set(AlarmManager.RTC_WAKEUP,timeMillis - (long) PreferenceData.SLEEP_REMINDER_TIME.getValue(context),PendingIntent.getService(context, 0, new Intent(context, SleepReminderService.class), 0));

        //SleepReminderService.refreshSleepTime(context);
    }

    /**
     * Cancel the next time for the alarm to ring.
     *
     * @param context       An active context instance.
     * @param manager       The AlarmManager that the alarm was scheduled on.
     */
    public void cancel(Context context, AlarmManager manager) {
        manager.cancel(getIntent(context));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent("android.intent.action.ALARM_CHANGED");
            intent.putExtra("alarmSet", false);
            context.sendBroadcast(intent);
        }
    }

    /**
     * The intent to fire when the alarm should ring.
     *
     * @param context       An active context instance.
     * @return              A PendingIntent that will open the alert screen.
     */
    private PendingIntent getIntent(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        intent.putExtra(AlarmReceiver.EXTRA_ALARM_ID, id);
        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    protected AlarmData(Parcel in) {
        id = in.readInt();
        name = in.readString();
        time = Calendar.getInstance();
        time.setTimeInMillis(in.readLong());
        isEnabled = in.readByte() != 0;
        days = in.createBooleanArray();
        isVibrate = in.readByte() != 0;
        if (in.readByte() == 1) sound = SoundData.fromString(in.readString());
        timeToWake=in.readInt();
        timeToWakeP=in.readInt();

        money=in.readDouble();
        needVisibilty=(in.readInt() == 0) ? false : true;
        position=in.readInt();
        constantTime=in.readLong();
        volume=in.readInt();
        step=in.readInt();

        isPunished=(in.readInt() == 0) ? false : true;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeLong(time.getTimeInMillis());
        parcel.writeByte((byte) (isEnabled ? 1 : 0));
        parcel.writeBooleanArray(days);
        parcel.writeByte((byte) (isVibrate ? 1 : 0));
        parcel.writeByte((byte) (sound != null ? 1 : 0));
        if (sound != null) parcel.writeString(sound.toString());
        parcel.writeInt(timeToWake);
        parcel.writeInt(timeToWakeP);
        parcel.writeDouble(money);
        parcel.writeInt(needVisibilty ? 1 : 0);
        parcel.writeInt(position);
        parcel.writeLong(constantTime);
        parcel.writeInt(volume);
        parcel.writeInt(step);
        parcel.writeInt(isPunished ? 1 : 0);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AlarmData> CREATOR = new Creator<AlarmData>() {
        @Override
        public AlarmData createFromParcel(Parcel in) {
            return new AlarmData(in);
        }

        @Override
        public AlarmData[] newArray(int size) {
            return new AlarmData[size];
        }
    };


    public void setNeedVisibility(boolean needVisibility) {
        this.needVisibilty = needVisibility;
    }


    public void setConstantTime(long constantTime,Context context, AlarmManager manager) {
        this.constantTime = constantTime;
        PreferenceData.CONSTANT_TIME.setValue(context,constantTime,id);
        //if (isEnabled) set(context, manager);
    }

    public int getId() {
        return id;
    }


    public void setId(int id,Context context) {
        this.id=id;
        PreferenceData.ID.setValue(context,id,id);
    }

}
