package me.jfenn.wakeMeUp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.aesthetic.Aesthetic;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.activities.CautionActivity;
import me.jfenn.wakeMeUp.adapters.PreferenceAdapter;
import me.jfenn.wakeMeUp.data.PreferenceData;
import me.jfenn.wakeMeUp.data.preference.BasePreferenceData;
import me.jfenn.wakeMeUp.data.preference.BooleanPreferenceData;
import me.jfenn.wakeMeUp.data.preference.BooleanPreferenceDataApps;
import me.jfenn.wakeMeUp.data.preference.RingtonePreferenceData;
import me.jfenn.wakeMeUp.data.preference.ThemePreferenceData;
import me.jfenn.wakeMeUp.data.preference.TimePreferenceData;
import me.jfenn.wakeMeUp.data.preference.Title;

/*The origin code have been changed */
public class SettingsFragment extends BasePagerFragment implements Consumer {

    private RecyclerView recyclerView;

    private PreferenceAdapter preferenceAdapter;

    private Disposable colorPrimarySubscription;
    private Disposable textColorPrimarySubscription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recycler, container, false);
        recyclerView = v.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));

        ArrayList<BasePreferenceData> list;
        String manufacturer = android.os.Build.MANUFACTURER;
        if (CautionActivity.deviceIsChinese(getContext(),false)){
            list = new ArrayList<BasePreferenceData>(Arrays.asList(
                    new Caution(),
                    new Title(R.string.title_alarm_pref),
                    new TimePreferenceData(PreferenceData.SNOOZE_DURATION,R.string.title_snooze_duration),
                    new RingtonePreferenceData(PreferenceData.DEFAULT_ALARM_RINGTONE, R.string.title_default_alarm_ringtone),
                    new TimePreferenceData(PreferenceData.RING_DURATION,R.string.title_shut_up_ring_after),
                    new BooleanPreferenceData(PreferenceData.SNOOZE_NOTIFICATION,R.string.title_show_snooze_notification,R.string.desc_show_snooze_notification),
                    new BooleanPreferenceData(PreferenceData.ALARM_NOTIFICATION,R.string.title_show_alarm_notification,R.string.desc_show_alarm_notification),

                    new Title(R.string.title_volume_pref),
                    new BooleanPreferenceData(PreferenceData.SLOW_WAKE_UP, R.string.title_slow_wake_up, R.string.desc_slow_wake_up),
                    new BooleanPreferenceData(PreferenceData.ALLOW_SHUT_UP_WITH_BUTTONS, R.string.allow_shut_up_with_buttons, R.string.desc_allow_shut_up_with_buttons),


                    new Title(R.string.title_wake_up_prefernce),
                    new BooleanPreferenceDataApps(),
                    // new BooleanPreferenceData(PreferenceData.WIFI, R.string.open_wifi, R.string.desc_wifi),
                    new Title(R.string.title_general_pref),

                    new ThemePreferenceData(),
                    new AboutWakeMe()
                    //new LanguagePreferenceData(PreferenceData.LANGUAGE,"language")
            )
            );
        }else{
            list = new ArrayList<BasePreferenceData>(Arrays.asList(
                    new Title(R.string.title_alarm_pref),
                    new TimePreferenceData(PreferenceData.SNOOZE_DURATION,R.string.title_snooze_duration),
                    new RingtonePreferenceData(PreferenceData.DEFAULT_ALARM_RINGTONE, R.string.title_default_alarm_ringtone),
                    new TimePreferenceData(PreferenceData.RING_DURATION,R.string.title_shut_up_ring_after),
                    new BooleanPreferenceData(PreferenceData.SNOOZE_NOTIFICATION,R.string.title_show_snooze_notification,R.string.desc_show_snooze_notification),
                    new BooleanPreferenceData(PreferenceData.ALARM_NOTIFICATION,R.string.title_show_alarm_notification,R.string.desc_show_alarm_notification),

                    new Title(R.string.title_volume_pref),
                    new BooleanPreferenceData(PreferenceData.SLOW_WAKE_UP, R.string.title_slow_wake_up, R.string.desc_slow_wake_up),
                    new BooleanPreferenceData(PreferenceData.ALLOW_SHUT_UP_WITH_BUTTONS, R.string.allow_shut_up_with_buttons, R.string.desc_allow_shut_up_with_buttons),


                    new Title(R.string.title_wake_up_prefernce),
                    new BooleanPreferenceDataApps(),
                    // new BooleanPreferenceData(PreferenceData.WIFI, R.string.open_wifi, R.string.desc_wifi),
                    new Title(R.string.title_general_pref),

                    new ThemePreferenceData(),
                    new AboutWakeMe()
                    //new LanguagePreferenceData(PreferenceData.LANGUAGE,"language")
            )
            );
        }



/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) list.add(new BatteryOptimizationPreferenceData());

        list.add(new AboutPreferenceData());
*/
        preferenceAdapter = new PreferenceAdapter(list);
        recyclerView.setAdapter(preferenceAdapter);

        colorPrimarySubscription = Aesthetic.Companion.get()
                .colorPrimary()
                .subscribe(this);

       textColorPrimarySubscription = Aesthetic.Companion.get()
                .textColorPrimary()
                .subscribe(this);


        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        colorPrimarySubscription.dispose();
        textColorPrimarySubscription.dispose();
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.title_settings);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (preferenceAdapter != null)
            preferenceAdapter.notifyDataSetChanged();
    }

    @Override
    public void accept(Object o) throws Exception {
        if (preferenceAdapter != null)
            preferenceAdapter.notifyDataSetChanged();
    }



    public static void invalidate(){

    }
}
