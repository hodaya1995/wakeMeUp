package me.jfenn.wakeMeUp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.aesthetic.AestheticActivity;

import java.util.List;

import androidx.annotation.Nullable;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.utils.Constants;
import me.jfenn.wakeMeUp.utils.RomUtils;


public class CautionActivity extends AestheticActivity {
    private TextView osActivity;
    private TextView phoneManufactor;
    private LinearLayout osLayout;
    private static final String TAG = "MiuiUtils";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caution);

        osLayout=(LinearLayout)findViewById(R.id.os_layout);

        osActivity=(TextView)findViewById(R.id.os_activity);
        phoneManufactor=(TextView)findViewById(R.id.phone_manufactor);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M&&!pm.isIgnoringBatteryOptimizations(getPackageName())) {
            osLayout.setVisibility(View.VISIBLE);
        }else{
            turnOnDozeMode(CautionActivity.this);
        }



        if(deviceIsChinese(this,true)){
            phoneManufactor.setText(getTextManufactor());
            boolean foundCorrectIntent = false;
            List<Intent> POWERMANAGER_INTENTS =new Constants(this).getConstants();
            for (Intent intent : POWERMANAGER_INTENTS) {
                if (isCallable(this, intent)) {
                    foundCorrectIntent = true;
                    startActivity(intent);
                    break;
                }
            }
            /*if (!foundCorrectIntent) {
                editor.putBoolean("skipProtectedAppCheck", true);
                editor.apply();
            }*/
        }else{
            phoneManufactor.setVisibility(View.GONE);
        }



    }
    private static boolean isCallable(Context context, Intent intent) {
        try {
            if (intent == null || context == null) {
                return false;
            } else {
                List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
                return list.size() > 0;
            }
        } catch (Exception ignored) {
            return false;
        }
    }
    private String getTextManufactor() {
        String manufacturer = android.os.Build.MANUFACTURER;
        String ans="";
        if("asus".equalsIgnoreCase(manufacturer)){
           ans="1. Open your device SETTINGS.\n\n\n2. Tap POWER MANAGEMENT, then tap AUTO-START MANAGER.\n\n\n3. on the DOWNLOADED tab, tap DENY next to WAKE ME PLEASE to change this setting to ALLOW.\n\n\n4. Tap ALLOW to confirm.  ";
        }
        else if("huawei".equalsIgnoreCase(manufacturer)){
          ans="For Huawei- EMUI 4.9 and earlier:\n\n\n1. Tap the PHONE MANAGER icon on he main screen of your device.\n\n\n2.Go to SETTINGS and select the PROTECTED APPS tab.\n\n\n3. Turn on the protection for WAKE ME PLEASE.\n\n\n\n\n\nFor Huawei- EMUI 5.0 and later:\n\n\n1.Tap the PHONE MANAGER icon on the main screen of your device (or open SETTINGS+BATTERY).\n\n\n2. tap LOCK SCREEN CLEANUP.\n\n\n3. Ensure the slider next to WAKE ME PLEASE is gray (off) and the app is set to DON'T CLOSE.";
        }
        else if("lenovo".equalsIgnoreCase(manufacturer)){
            ans="Follow steps in either option (A),(B) or (C) below, depending what matches your device navigation:\n\n\n(A):\n\n\n1.Open your device SETTINGS.\n\n\n2.select BACKGROUND APP MANAGEMENT.\n\n\n3. deselect WAKE ME PLEASE.\n\n\n\n\n\n(B):\n\n\n1. Open your device SETTINGS and select APPS\n\n\n2.choose WAKE ME PLEASE, then tap BATTERY.\n\n\n3. disable BATTERY OPTIMIZATION.\n\n\n\n\n\n(C):\n\n\n1.Open ADVANCED SETTINGS and select BATTER.\n\n\n2.Tap BATTERY OPTIMIZATIONS.\n\n\n3. Tap WAKE ME PLEASE and select OFF.";
        }
        else if("nokia".equalsIgnoreCase(manufacturer)){
            ans="1.Open your device's SYSTEM SETTINGS.\n\n\n2. Tap APPS + WAKE ME PLEASE.\n\n\n3. Tap BATTERY and select DON'T OPTIMIZE.\n\n\n\n\n\nIf you continue experience issues ,ensure that BATTERY SAVER is not enabled./n1. Open your device's SYSTEM SETTINGS and select battery.\n\n\n2. Ensure BATTERY SAVER is disabled.";
        }
        else if("oneplus".equalsIgnoreCase(manufacturer)){
           ans="1. Go to SYSTEM SETTINGS->APPS, and tap the gear icon. Choose SPECIAL ACCESS-> BATTERY OPTIMIZATION and disable this feature.\n\n\n2.Go to PHONE SETTINGS->BATTERY->BATTERY OPTIMIZATION. Choose ALL APPS in the menu at the top of the screen, hen tap WAKE ME PLEASE and select DON'T OPTIMIZE\n\n\n\n\n\n on older OnePlus devices, follow the alternative steps below:\n\n\n1. Open your device SETTINGS and select BATTERY->BATTERY OPTIMIZATION\n\n\n2. Tap MENU (3 dots), then disable ENHANCED OPTIMIZATION (or ADVANCED OPTIMIZATION).\n\n\n\n\n\nFurther recommendations:\n\n\n certain OnePlus devices include the APP AUTO-LAUNCH feature, which prevents apps from running in the background. To disable this feature:\n\n\n1. Open SYSTEM SETTINGS on your OnePlus device and go to APPS->APP AUTO-LAUNCH.\n\n\n2.Tap the slider next to WAKE ME PLEASE so that it turns gray (OFF)";        }
        else if("samsung".equalsIgnoreCase(manufacturer)){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&Build.VERSION.SDK_INT<Build.VERSION_CODES.P)
            ans="1. Open your device SETTINGS and tap DEVICE MAINTENANCE\n\n\n2. Tap BATTERY, then scroll down and select UNMONITORED APPS\n\n\n3. Tap ADD APPS, then select WAKE ME PLEASE . Tap DONE. ";
            else if(Build.VERSION.SDK_INT==Build.VERSION_CODES.P)
                ans="1.Open your device SETTINGS and tap APPS\n\n\n2. Tap MENU (3 dots) in the top-right corner, then select SPECIAL ACCESS\n\n\n3. Select OPTIMIZE BATTERY USAGE, then tap the drop-down menu at the top of the screen and select ALL\n\n\n4. Tap the blue (ON) slider next to WAKE ME PLEASE so it turns white (OFF).";
        }
        else if("xiaomi".equalsIgnoreCase(manufacturer)){
           ans="For MIUI 9 or 10:\n\n\n1. Open either your device SETTINGS, INSTALLED APPS, or APP MANAGEMENT\n\n\n2. Tap WAKE ME PLEASE and ensure AUTOSTART is enabled.\n\n\n3. Tap OTHER PREMISSIOMS\n\n\n4. Ensure SHOW ON LOCK SCREEN and START IN BACKGROUND are enabled\n\n\n5. Tap the back arrow, then tap BATTERY SAVER and select NO RESTRICTIONS.\n\n\n\n\n\nFor MIUI 8:\n\n\n1.Open either your device SETTINGS, INSTALLED APPS, or APP MANAGEMENT\n\n\n2. Tap WAKE ME PLEASE->OTHER PERMISSIONS\n\n\n3. Ensure SHOW ON LOCK SCREEN nad START IN BACKGROUND are enabled.\n\n\n4.Return to your main device SETTINGS screen.\n\n\n5. Select BATTERY->MANAGE APPS BATTERY USAGE\n\n\n6.Tap CHOOSE APPS->WAKE ME PLEASE->NO RESTRICTIONS\n\n\n\n\n\nFurther recommendations:\n\n\nif you continue to experience issues on XIAOMI (MIUI 10),try the relevant troubleshooting steps below from the device SETTINGS screen:\n\n\n-Go to ADVANCE SETTINGS->BATTERY MANAGER. Ensure the POWER PLAN is set to PERFORMANCE.\n\n\n-Go to ADVANCED SETTINGS->BATTERY MANAGER->PROTECTED APPS. Ensure WAKE ME PLEASE is set to PROTECTED.\n\n\n- Go to APPS->WAKE ME PLEASE->BATTERY. Enable POWER-INTENSIVE PROMPT and KEEP RUNNING AFTER SCREEN OFF.\n\n\n- Go to ADDITIONAL SETTINGS->BATTERY&PERFORMANCE->MANAGE APPS BATTERY USAGE, then performance the following actions:\n\n\n\t1. Ensure all options under POWER SAVING MODES are set to OFF.\n\n\n\t2. Tap SAVING POWER IN THE BACKGROUND->CHOOSE APPS->WAKE ME PLEASE->BACKGROUND SETTINGS. Select NO RESTRICTIONS.   ";
        }
        else if("xperia".equalsIgnoreCase(manufacturer)){
           ans="1. open your device SETTINGS and tap BATTERY.\n\n\n2. Tap: MENU (3 dots) in top-right corner, then select BATTERY OPTIMIZATION or POWER SAVING EXCEPTIONS.\n\n\n3. Tap the APPS tab and check WAKE ME PLEASE";
        }
        return ans;
    }

    public static boolean deviceIsChinese(Context context,boolean open) {
        boolean ans=false;
        String manufacturer = android.os.Build.MANUFACTURER;
        if("asus".equalsIgnoreCase(manufacturer)){

           ans=true;
        }
        else if("huawei".equalsIgnoreCase(manufacturer)){

            ans=true;
        }
        else if("lenovo".equalsIgnoreCase(manufacturer)){
            ans=true;
        }
        else if("nokia".equalsIgnoreCase(manufacturer)){
            ans=true;
        }
        else if("oneplus".equalsIgnoreCase(manufacturer)){
            ans=true;
        }
        else if("samsung".equalsIgnoreCase(manufacturer)){
            ans=true;
        }
        else if("xiaomi".equalsIgnoreCase(manufacturer)){
            if(open) {
                Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName("com.miui.securitycenter",
                        "com.miui.permcenter.permissions.PermissionsEditorActivity");
                intent.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(intent);

                applyMiuiPermission(context);
            }
            ans= true;
        }
        else if("sonyxperia".equalsIgnoreCase(manufacturer)){
          ans=true;
        }
        return ans;
    }

    public static void applyMiuiPermission(Context context) {
        int versionCode = getMiuiVersion();
        if (versionCode == 8||versionCode==9) {
            goToMiuiPermissionActivity_V8(context);
        }else if (versionCode == 10) {
            goToMiuiPermissionActivity_V10(context);
        } else {
             Log.e(TAG, "this is a special MIUI rom version, its version code " + versionCode);
        }
    }

    private static void goToMiuiPermissionActivity_V8(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            Log.e(TAG, "Intent is not available!");
        }
    }


    private static void goToMiuiPermissionActivity_V10(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getMiuiVersion() {
        String version = RomUtils.getSystemProperty("ro.miui.ui.version.name");
        if (version != null) {
            try {
                return Integer.parseInt(version.substring(1));
            } catch (Exception e) {
                Log.e(TAG, "get miui version code error, version : " + version);
            }
        }
        return -1;
    }





    private static boolean isIntentAvailable(Intent intent, Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (intent.resolveActivity(packageManager) != null) return true;
        return false;

    }




    private void turnOnDozeMode(Context context) {
           Intent intent = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }

    }

}
