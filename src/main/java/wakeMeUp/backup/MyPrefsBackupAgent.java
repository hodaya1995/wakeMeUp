package me.jfenn.wakeMeUp.backup;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.BackupManager;
import android.app.backup.FullBackupDataOutput;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class MyPrefsBackupAgent extends BackupAgentHelper {
    public static String TAG = "MyPrefsBackupAgent";
    // The names of the SharedPreferences groups that the application maintains.  These
    // are the same strings that are passed to Context#getSharedPreferences(String, int).
    static final String CALIB_PREF = "threshold";

    // An arbitrary string used within the BackupAgentHelper implementation to
    // identify the SharedPreferenceBackupHelper's data.
    static final String MY_PREFS_BACKUP_KEY = "myprefs";
    private static BroadcastReceiver mReciever = new NetworkBroadcastReceiver();


    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(getApplicationContext(), CALIB_PREF);
        addHelper(MY_PREFS_BACKUP_KEY, helper);

          Log.d(TAG, "on create backup");




    }


    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
        super.onBackup(oldState, data, newState);
        Log.d(TAG, "on backup" );
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
        super.onRestore(data, appVersionCode, newState);
         SharedPreferences pref = getApplicationContext().getSharedPreferences("threshold", getApplicationContext().MODE_PRIVATE);
        Log.d(TAG, "on restore"+pref.getInt("id", -1));
        //getDataFromDatabase(getApplicationContext());


    }

    private void getDataFromDatabase(Context context) {
        Connection connect;
        String ConnectionResult = "";
        Boolean isSuccess = false;

        SharedPreferences prefs = context.getSharedPreferences("threshold", Context.MODE_PRIVATE);

        int id = prefs.getInt("id", -1);
        Log.d(TAG,"getDataFromDatabase "+id);
        if (id != -1){

            try {
                ConnectionHelper conStr = new ConnectionHelper();
                connect = conStr.connectionclasss();        // Connect to database
                if (connect == null) {
                     ConnectionResult = "Check Your Internet Access!";
                    Log.d(TAG, "ConnectionResult: " + ConnectionResult);

                } else {
                    connect = conStr.connectionclasss();        // Connect to database
                    String queryCount = "select * from dbo.Users where Id=" + id;
                    Statement stmt2 = connect.createStatement();

                    SharedPreferences.Editor editor = context.getSharedPreferences("needToPay", Context.MODE_PRIVATE).edit();
                    //boolean needToPay = prefs.getBoolean("needToPay", false);
                    //int money = prefs.getInt("moneyToPay", 0);
                    ResultSet rsCount = stmt2.executeQuery(queryCount);
                    while (rsCount.next()) {
                        int money = rsCount.getInt(3);
                        String needToPay = rsCount.getString(2);
                        editor.putInt("moneyToPay",money);
                        editor.putBoolean("needToPay",needToPay.equals("t")?true:false);
                        editor.apply();
                        Log.d(TAG, "MONEY: " + money + " needToPay? " + needToPay);
                    }
                    connect.close();
                    ConnectionResult = " successful";
                    Log.d(TAG, ConnectionResult);


                }

                Log.d(TAG, "ConnectionResult: " + ConnectionResult);

            } catch (Exception ex) {
                isSuccess = false;
                ConnectionResult = ex.getMessage();
                Log.d(TAG, "ConnectionResult: " + ConnectionResult);
            }
        Log.d(TAG, "ConnectionResult: " + ConnectionResult);
    }

    }


    @Override
    public void onFullBackup(FullBackupDataOutput data) throws IOException {
        super.onFullBackup(data);


        Log.d(TAG, "on backup full");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("threshold", Context.MODE_PRIVATE);
        if (pref.getInt("id", -1) == -1) {
            getId(this.getApplicationContext());

            new BackupManager(getApplicationContext()).dataChanged();
        }


    }

    @Override
    public void onRestore(BackupDataInput data, long appVersionCode, ParcelFileDescriptor newState) throws IOException {
        super.onRestore(data, appVersionCode, newState);

         Log.d(TAG, "on restore full");
        //getDataFromDatabase(getApplicationContext());
    }


    public static int getId(Context context) {
        Connection connect;
        String ConnectionResult = "";
        Boolean isSuccess = false;

        int id = -1;
        try {
            ConnectionHelper conStr = new ConnectionHelper();
            connect = conStr.connectionclasss();        // Connect to database
            if (connect == null) {
                SharedPreferences.Editor editor2 = context.getSharedPreferences("needToPay", context.MODE_PRIVATE).edit();
                editor2.putBoolean("uploaded", false);
                editor2.apply();
                IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
                context.getApplicationContext().registerReceiver(mReciever, intentFilter);
                SharedPreferences.Editor editor = context.getSharedPreferences("needToPay", context.MODE_PRIVATE).edit();
                editor.putInt("code", 1);
                editor.apply();
                ConnectionResult = "Check Your Internet Access!";
                Log.d(TAG, "ConnectionResult: " + ConnectionResult);

            } else {
                connect = conStr.connectionclasss();        // Connect to database
                String queryCount = "select COUNT(*) from dbo.Users";
                Statement stmt2 = connect.createStatement();
                //int count = rsCount.getInt(1);
                int count = 0;

                SharedPreferences prefs = context.getSharedPreferences("needToPay", context.MODE_PRIVATE);
                boolean needToPay = prefs.getBoolean("needToPay", false);
                int money = prefs.getInt("moneyToPay", 0);
                String query = " insert into dbo.Users (ID,TF,M)" + " values (?,?,?)";
                PreparedStatement preparedStmt = connect.prepareStatement(query);
                preparedStmt.setString(2, needToPay ? "t" : "f");
                preparedStmt.setInt(3, money);
                ResultSet rsCount = stmt2.executeQuery(queryCount);
                while (rsCount.next()) count = rsCount.getInt(1);
                count++;
                preparedStmt.setInt(1, count);
                preparedStmt.execute();
                connect.close();
                id = count;

                ConnectionResult = " successful";


                Log.d(TAG, ConnectionResult);

                SharedPreferences.Editor editor2 = context.getSharedPreferences("threshold", context.MODE_PRIVATE).edit();
                Log.d(TAG, "id " + id);
                editor2.putInt("id",id);
                editor2.apply();
                new BackupManager(context).dataChanged();

                SharedPreferences.Editor editor = context.getSharedPreferences("needToPay", context.MODE_PRIVATE).edit();
                editor.putBoolean("uploaded", true);
                editor.apply();

                ConnectionResult = " successful";
                id = count;

                //insertIdIntoDatabase(id,needToPay,money,context);
                isSuccess = true;


            }

            Log.d(TAG, "ConnectionResult: " + ConnectionResult);

        } catch (Exception ex) {
            isSuccess = false;
            ConnectionResult = ex.getMessage();
            Log.d(TAG, "ConnectionResult: " + ConnectionResult);
            id = -1;
        }
        Log.d(TAG, "ConnectionResult: " + ConnectionResult);

        return id;
    }



    public static void updateDatabase(int id, boolean needToPay, int money, Context context) {
        Log.d(TAG, "updateDatabase id " + id + "need to pay " + needToPay + " money: " + money);

        SharedPreferences p = context.getSharedPreferences("needToPay", Context.MODE_PRIVATE);
        boolean uploaded = p.getBoolean("uploaded", false);
        if(!uploaded){
            Log.d(TAG,"NOT UPLOADED");
        Connection connect;
        String ConnectionResult = "";
        Boolean isSuccess = false;

        try {
            ConnectionHelper conStr = new ConnectionHelper();
            connect = conStr.connectionclasss();        // Connect to database
            if (connect == null) {
                SharedPreferences.Editor editor = context.getSharedPreferences("needToPay", Context.MODE_PRIVATE).edit();
                editor.putInt("code", 2);
                editor.apply();
                Log.d(TAG, "connect==null");
                SharedPreferences.Editor editor2 = context.getSharedPreferences("needToPay", Context.MODE_PRIVATE).edit();
                editor2.putBoolean("uploaded", false);
                editor2.apply();
                IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
                if (mReciever == null) mReciever = new NetworkBroadcastReceiver();
                context.getApplicationContext().registerReceiver(mReciever, intentFilter);
                ConnectionResult = "Check Your Internet Access!";
            } else {

                Log.d(TAG, "connect!=null");
                SharedPreferences.Editor editor = context.getSharedPreferences("needToPay", Context.MODE_PRIVATE).edit();
                editor.putBoolean("uploaded", true);
                editor.apply();
                String tf = needToPay ? "t" : "f";
                String query = "UPDATE dbo.Users SET TF = '" + tf + "', M= " + money + "WHERE ID = " + id;
                PreparedStatement preparedStmt = connect.prepareStatement(query);
                /*preparedStmt.setInt (1, id);
                preparedStmt.setString (2, needToPay?"t":"f");
                preparedStmt.setInt (3,money);*/
                preparedStmt.execute();
                ConnectionResult = " successful";
                isSuccess = true;
                connect.close();
                if (mReciever == null) mReciever = new NetworkBroadcastReceiver();
                context.getApplicationContext().unregisterReceiver(mReciever);
            }
        } catch (Exception ex) {
            isSuccess = false;
            ConnectionResult = ex.getMessage();
        }

        Log.d(TAG, "ConnectionResult " + ConnectionResult);
        Log.d(TAG, "updateDatabase DONE");


    }
}

}