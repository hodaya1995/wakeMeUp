package me.jfenn.wakeMeUp.fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;

import io.reactivex.functions.Consumer;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.activities.CautionActivity;
import me.jfenn.wakeMeUp.data.preference.BasePreferenceData;

public class Caution extends BasePreferenceData<Caution.ViewHolder> {



    @Override
    public Caution.ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new Caution.ViewHolder(inflater.inflate(R.layout.item_caution, parent, false));
    }

    @Override
    public void bindViewHolder(Caution.ViewHolder holder) {

        holder.background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent( holder.getContext(), CautionActivity.class);
                holder.getContext().startActivity(intent2);
            }
        });

        Aesthetic.Companion.get()
                .colorAccent()
                .take(1)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(final Integer colorAccent) throws Exception {
                        Aesthetic.Companion.get()
                                .colorAccent()
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


                                        holder.background.setBackgroundColor(textColorPrimary);
                                    }
                                });
                    }
                });
    }




    public static class ViewHolder extends BasePreferenceData.ViewHolder {

        private TextView caution;
        private LinearLayout background;
        public ViewHolder(View v) {
            super(v);
            caution = v.findViewById(R.id.caution);
            background=v.findViewById(R.id.background);
        }
    }
}
