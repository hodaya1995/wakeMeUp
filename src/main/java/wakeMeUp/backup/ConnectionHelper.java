package me.jfenn.wakeMeUp.backup;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {
    String ip,db,DBUserNameStr,DBPasswordStr;


    @SuppressLint("NewApi")
    public Connection connectionclasss()
    {

        // Declaring Server ip, username, database name and password
        ip="den1.mssql8.gear.host";
        db = "contries";
        DBUserNameStr = "contries";
        DBPasswordStr = "Sr491G~6rB!6";
        // Declaring Server ip, username, database name and password


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String ConnectionURL = null;
        try
        {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnectionURL = "jdbc:jtds:sqlserver://" + ip +";databaseName="+ db + ";user=" + DBUserNameStr+ ";password=" + DBPasswordStr + ";";
            connection = DriverManager.getConnection(ConnectionURL);
        }
        catch (SQLException se)
        {
            Log.d(MyPrefsBackupAgent.TAG, "error sql"+se.getMessage());
        }

        catch (ClassNotFoundException e)
        {
            Log.d(MyPrefsBackupAgent.TAG, "error class"+e.getMessage());
        }
        catch (Exception e)
        {
            Log.d(MyPrefsBackupAgent.TAG, "error general"+e.getMessage());
        }
        return connection;
    }
}
