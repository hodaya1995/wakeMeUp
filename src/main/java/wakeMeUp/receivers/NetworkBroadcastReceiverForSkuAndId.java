package me.jfenn.wakeMeUp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import me.jfenn.wakeMeUp.backup.MyPrefsBackupAgent;
import me.jfenn.wakeMeUp.backup.NetworkUtil;
import me.jfenn.wakeMeUp.interfaces.ConnectListener;

public class NetworkBroadcastReceiverForSkuAndId extends BroadcastReceiver{

    private ConnectListener listener;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = NetworkUtil.getConnectivityStatusString(context);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (status != NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {

                Log.d(MyPrefsBackupAgent.TAG, "success to NetworkBroadcastReceiverForSkuAndId");
                  if (listener != null) listener.onConnected();
             }
        }
    }

    public void setListener(ConnectListener listener) {
        this.listener = listener;
    }


}