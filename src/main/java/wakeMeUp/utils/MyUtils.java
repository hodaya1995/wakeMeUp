package me.jfenn.wakeMeUp.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.activities.CautionActivity;
import me.jfenn.wakeMeUp.dialogs.AlertDialog;

public class MyUtils {

    public static void startPowerSaverIntent(Context context) {
        SharedPreferences settings = context.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        boolean skipMessage = settings.getBoolean("skipProtectedAppCheck", false);
        if (!skipMessage) {
            new me.jfenn.wakeMeUp.dialogs.AlertDialog(context,false,false)
                    .setTitle(Build.MANUFACTURER + " Protected Apps")
                    .setContent(String.format("%s requires to be enabled in 'Protected Apps' to function properly.\n also, make sure to enable wake me please in your device related to battery or memory management.%n", context.getString(R.string.app_name)))
                    .setListener(new AlertDialog.Listener() {
                        @Override
                        public void onDismiss(AlertDialog dialog, boolean ok) {
                            context.startActivity(new Intent(context, CautionActivity.class));
                            final SharedPreferences.Editor editor = settings.edit();
                            editor.putBoolean("skipProtectedAppCheck", true);
                            editor.apply();

                        }
                    })
                    .show();


        }
    }


}
