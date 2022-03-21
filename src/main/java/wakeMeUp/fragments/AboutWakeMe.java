package me.jfenn.wakeMeUp.fragments;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.activities.AboutActivity;
import me.jfenn.wakeMeUp.data.preference.BasePreferenceData;

public class AboutWakeMe extends BasePreferenceData<AboutWakeMe.ViewHolder> {



    @Override
    public AboutWakeMe.ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new AboutWakeMe.ViewHolder(inflater.inflate(R.layout.item_about, parent, false));
    }

    @Override
    public void bindViewHolder(ViewHolder holder) {
        holder.about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.getContext(),AboutActivity.class);
                holder.getContext().startActivity(intent);
            }
        });

    }




    public static class ViewHolder extends BasePreferenceData.ViewHolder {

        private LinearLayout about;
       public ViewHolder(View v) {
            super(v);
           about = v.findViewById(R.id.about);
       }
    }
}
