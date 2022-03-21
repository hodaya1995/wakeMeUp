package me.jfenn.wakeMeUp.data.preference;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;

import io.reactivex.functions.Consumer;
import me.jfenn.wakeMeUp.R;

public class Blank extends BasePreferenceData<Blank.ViewHolder>{
    @Override
    public ViewHolder getViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new Blank.ViewHolder(inflater.inflate(R.layout.item_blank, parent, false));
    }

    @Override
    public void bindViewHolder(ViewHolder holder) {
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

                                        /*CompoundButtonCompat.setButtonTintList(holder.toggle, colorStateList);
                                        holder.toggle.setTextColor(textColorPrimary);*/
                                    }
                                });
                    }
                });
    }

    public class ViewHolder extends BasePreferenceData.ViewHolder {

        public ViewHolder(View v) {
            super(v);
        }

    }
}
