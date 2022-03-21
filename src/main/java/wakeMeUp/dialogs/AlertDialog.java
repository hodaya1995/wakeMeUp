package me.jfenn.wakeMeUp.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;

import io.reactivex.disposables.Disposable;
import me.jfenn.wakeMeUp.R;
/*The origin code have been changed */
public class AlertDialog extends AestheticDialog implements View.OnClickListener {

    private final boolean showCancel;
    private boolean ignoreAestetic;
    private Disposable isDarkSubscription;
    private String title, content;
    private Listener listener;
    private boolean isDark;

    public AlertDialog(Context context,boolean showCancel,boolean ignoreAestetic) {
        super(context);
        this.showCancel=showCancel;
        this.ignoreAestetic=ignoreAestetic;
            isDarkSubscription = Aesthetic.Companion.get()
                    .isDark()
                    .subscribe(aBoolean -> isDark = aBoolean);

    }

    public AlertDialog(Context context) {
        super(context);
        this.showCancel=true;
        isDarkSubscription = Aesthetic.Companion.get()
                .isDark()
                .subscribe(aBoolean -> isDark = aBoolean);
    }

    public AlertDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public AlertDialog setContent(String content) {
        this.content = content;
        return this;
    }

    public AlertDialog setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alert);
        LinearLayout ll=findViewById(R.id.dialog_alert_main);
        TextView titleView = findViewById(R.id.title);
        TextView bodyView = findViewById(R.id.body);
        TextView okView = findViewById(R.id.ok);
        TextView cancelView = findViewById(R.id.cancel);


        if(ignoreAestetic){
            //ll.setTag(R.);
        }
        if(!showCancel) {
            cancelView.setVisibility(View.GONE);
            //bodyView.setTextColor(getContext().getResources().getColor(R.color.black));
        }
        if (title != null)
            titleView.setText(title);
        else titleView.setVisibility(View.GONE);

        bodyView.setText(content);

        okView.setOnClickListener(this);
        if (listener != null)
            cancelView.setOnClickListener(this);
        else cancelView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null)
            listener.onDismiss(this, v.getId() == R.id.ok);
    }

    public interface Listener {
        void onDismiss(AlertDialog dialog, boolean ok);
    }
}
