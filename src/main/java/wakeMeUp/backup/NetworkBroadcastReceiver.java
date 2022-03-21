package me.jfenn.wakeMeUp.backup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class NetworkBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = NetworkUtil.getConnectivityStatusString(context);
         if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                Log.d(MyPrefsBackupAgent.TAG, "NetworkUtil.NETWORK_STATUS_NOT_CONNECTE");

                //new ForceExitPause(context).execute();
            } else {
                Log.d(MyPrefsBackupAgent.TAG, "success");
                SharedPreferences pref=context.getSharedPreferences("needToPay", Context.MODE_PRIVATE);
                int code=pref.getInt("code",2);
                Log.d(MyPrefsBackupAgent.TAG, "code "+code);

                SharedPreferences pref2=context.getSharedPreferences("threshold", Context.MODE_PRIVATE);
                int id=pref2.getInt("id",-1);
                boolean needToPay=pref.getBoolean("needToPay",false);
                int money=pref.getInt("moneyToPay",0);
                if(code==1&&id!=-1)MyPrefsBackupAgent.getId(context);
                else if(code==2&&id!=-1)MyPrefsBackupAgent.updateDatabase(id,needToPay,money,context);

            }
        }
    }
}