package me.jfenn.wakeMeUp.data.preference;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;

import androidx.annotation.StringRes;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.CompoundButtonCompat;
import io.reactivex.functions.Consumer;
import me.jfenn.wakeMeUp.Alarmio;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.data.preference.BasePreferenceData.ViewHolder;
import me.jfenn.wakeMeUp.views.AestheticSwitchView;

public class Title extends BasePreferenceData<Title.ViewHolder>{

    private final int title;

    public Title(@StringRes int title) {
        this.title = title;
    }

    @Override
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new Title.ViewHolder(inflater.inflate(R.layout.item_title, parent, false));
    }

    @Override
    public void bindViewHolder(ViewHolder holder) {
        holder.title.setText(title);
        //holder.title.setTextColor(Color.BLUE);
        Aesthetic.Companion.get()
                .colorAccent()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        holder.title.setTextColor(integer);
                    }
                });


    }


    public class ViewHolder extends BasePreferenceData.ViewHolder {
        private TextView title;

        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
          }

    }
}
