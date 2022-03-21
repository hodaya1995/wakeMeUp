package me.jfenn.wakeMeUp.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.afollestad.aesthetic.AestheticActivity;

import androidx.annotation.Nullable;
import me.jfenn.wakeMeUp.BuildConfig;
import me.jfenn.wakeMeUp.R;

public class AboutActivity extends AestheticActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        String version = BuildConfig.VERSION_NAME;
        StringBuilder sb=new StringBuilder();
        sb.append("-Sound effects obtained from https://www.zapsplat.com\n\n");
        sb.append("-Alarm icon at dismiss screen made by Freepik from www.flaticon.com\n\n");

        ((TextView) findViewById(R.id.about_text)).setText(sb.toString());
        ((TextView) findViewById(R.id.version)).setText(version+"v");

    }

}
