package me.jfenn.wakeMeUp.data.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.aesthetic.Aesthetic;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.widget.CompoundButtonCompat;
import io.reactivex.functions.Consumer;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.data.PreferenceData;

public class BooleanPreferenceDataApps extends BasePreferenceData<BooleanPreferenceDataApps.ViewHolder> {

    public BooleanPreferenceDataApps() {

    }

    @Override
    public BasePreferenceData.ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new BooleanPreferenceDataApps.ViewHolder(inflater.inflate(R.layout.item_preference_boolean_apps, parent, false));
    }

    @Override
    public void bindViewHolder(final BooleanPreferenceDataApps.ViewHolder holder) {
        int title1= R.string.open_calandar;
        int title2= R.string.open_gmail;
        int title3= R.string.open_wifi;

        int description1= R.string.desc_calandar;
        int description2=R.string.desc_gmail;
        int description3=R.string.desc_wifi;

        holder.title1.setText(title1);
        holder.description1.setText(description1);
        holder.toggle1.setOnCheckedChangeListener(null);

        holder.title2.setText(title2);
        holder.description2.setText(description2);
        holder.toggle2.setOnCheckedChangeListener(null);

        holder.title3.setText(title3);
        holder.description3.setText(description3);
        holder.toggle3.setOnCheckedChangeListener(null);


        Boolean value1 = PreferenceData.CALANDAR.getValue(holder.itemView.getContext());
        Boolean value2 = PreferenceData.GMAIL.getValue(holder.itemView.getContext());
        Boolean value3 = PreferenceData.WIFI.getValue(holder.itemView.getContext());

        SharedPreferences pref = holder.getContext().getSharedPreferences("threshold", Context.MODE_PRIVATE);
        boolean snoozed = pref.getBoolean("snoozed", false);

        holder.toggle1.setChecked(value1 != null ? value1 : false);
        holder.toggle2.setChecked(value2 != null ? value2 : true);
        holder.toggle3.setChecked(value2 != null ? value2 : true);

        if(holder.toggle2.isChecked()){
            holder.wifiLayout.setVisibility(View.VISIBLE);
            holder.wifiView.setVisibility(View.VISIBLE);

        }else{
            holder.wifiLayout.setVisibility(View.GONE);
            holder.wifiView.setVisibility(View.GONE);

        }
        if(!snoozed) {
            holder.toggle1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b) {
                        PreferenceData.CALANDAR.setValue(compoundButton.getContext(), b);
                        PreferenceData.GMAIL.setValue(compoundButton.getContext(), !b);
                        holder.toggle2.setChecked(!b);
                    }else{
                        PreferenceData.CALANDAR.setValue(compoundButton.getContext(), b);
                    }

                }
            });
            holder.toggle2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(b) {
                        PreferenceData.GMAIL.setValue(compoundButton.getContext(), b);
                        PreferenceData.WIFI.setValue(compoundButton.getContext(), b);
                        holder.toggle3.setChecked(b);

                        holder.wifiLayout.setVisibility(View.VISIBLE);
                        holder.wifiView.setVisibility(View.VISIBLE);


                        PreferenceData.CALANDAR.setValue(compoundButton.getContext(), !b);
                        holder.toggle1.setChecked(!b);
                    }else{
                        PreferenceData.GMAIL.setValue(compoundButton.getContext(), b);
                        holder.wifiLayout.setVisibility(View.GONE);
                        holder.wifiView.setVisibility(View.GONE);


                    }

                }
            });
            holder.toggle3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    PreferenceData.WIFI.setValue(compoundButton.getContext(), b);
                }
            });
        }else{
            holder.toggle1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    holder.toggle1.setChecked(!b);
                    Toast.makeText(holder.getContext(),"edit prefernces is not available",Toast.LENGTH_LONG).show();


                }
            });
            holder.toggle2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    holder.toggle2.setChecked(!b);
                    Toast.makeText(holder.getContext(),"edit prefernces is not available",Toast.LENGTH_LONG).show();
                }
            });
            holder.toggle3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    holder.toggle3.setChecked(!b);
                    Toast.makeText(holder.getContext(),"edit prefernces is not available",Toast.LENGTH_LONG).show();
                }
            });

        }
        Aesthetic.Companion.get()
                .colorAccent()
                .take(1)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(final Integer colorAccent) throws Exception {
                        Aesthetic.Companion.get()
                                .textColorPrimary()
                                .take(1)
                                .subscribe(new Consumer<Integer>() {
                                    @Override
                                    public void accept(Integer textColorPrimary) throws Exception {
                                        ColorStateList colorStateList = new ColorStateList(
                                                new int[][]{new int[]{-android.R.attr.state_checked}, new int[]{android.R.attr.state_checked}},
                                                new int[]{
                                                        Color.argb(100, Color.red(textColorPrimary), Color.green(textColorPrimary), Color.blue(textColorPrimary)),
                                                        colorAccent
                                                }
                                        );

                                        CompoundButtonCompat.setButtonTintList(holder.toggle1, colorStateList);
                                        CompoundButtonCompat.setButtonTintList(holder.toggle2, colorStateList);
                                        CompoundButtonCompat.setButtonTintList(holder.toggle3, colorStateList);

                                        holder.toggle1.setTextColor(textColorPrimary);
                                        holder.toggle2.setTextColor(textColorPrimary);
                                        holder.toggle3.setTextColor(textColorPrimary);
                                    }
                                });
                    }
                });
    }

    public class ViewHolder extends BasePreferenceData.ViewHolder {

        private TextView title1;
        private TextView description1;
        private SwitchCompat toggle1;
        private TextView title2;
        private TextView description2;
        private SwitchCompat toggle2;
        private TextView title3;
        private TextView description3;
        private SwitchCompat toggle3;
        private View wifiView;
        private LinearLayout wifiLayout;

        public ViewHolder(View v) {
            super(v);
            title1 = v.findViewById(R.id.title1);
            description1 = v.findViewById(R.id.description1);
            toggle1 = v.findViewById(R.id.toggle1);
            title2 = v.findViewById(R.id.title2);
            description2 = v.findViewById(R.id.description2);
            toggle2 = v.findViewById(R.id.toggle2);
            title3 = v.findViewById(R.id.title3);
            description3 = v.findViewById(R.id.description3);
            toggle3 = v.findViewById(R.id.toggle3);
            wifiView=v.findViewById(R.id.wifi_layout2);
            wifiLayout=v.findViewById(R.id.wifi_layout);
        }

    }

}
