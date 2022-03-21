package me.jfenn.wakeMeUp.activities;

import android.Manifest;
import android.app.backup.BackupManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.afollestad.aesthetic.AestheticActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.util.Arrays;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import me.jfenn.wakeMeUp.Alarmio;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.addedByMe.AddPhotoBottomDialogFragment;
import me.jfenn.wakeMeUp.backup.MyPrefsBackupAgent;
import me.jfenn.wakeMeUp.backup.NetworkBroadcastReceiver;
import me.jfenn.wakeMeUp.fragments.BaseFragment;
import me.jfenn.wakeMeUp.fragments.HomeFragment;
import me.jfenn.wakeMeUp.fragments.StopwatchFragment;
import me.jfenn.wakeMeUp.fragments.TimerFragment;
import me.jfenn.wakeMeUp.interfaces.ConnectListener;
import me.jfenn.wakeMeUp.receivers.NetworkBroadcastReceiverForSkuAndId;
import me.jfenn.wakeMeUp.receivers.TimerReceiver;
import me.jfenn.wakeMeUp.utils.DoubleToLongConverter;
import me.jfenn.wakeMeUp.utils.MyUtils;
import me.jfenn.wakeMeUp.utils.util.IabBroadcastReceiver;
import me.jfenn.wakeMeUp.utils.util.IabHelper;
import me.jfenn.wakeMeUp.utils.util.IabResult;
import me.jfenn.wakeMeUp.utils.util.Inventory;
/*The origin code have been changed */
/*
*                            Apache License
                           Version 2.0, January 2004
                        http://www.apache.org/licenses/

   TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION

   1. Definitions.

      "License" shall mean the terms and conditions for use, reproduction,
      and distribution as defined by Sections 1 through 9 of this document.

      "Licensor" shall mean the copyright owner or entity authorized by
      the copyright owner that is granting the License.

      "Legal Entity" shall mean the union of the acting entity and all
      other entities that control, are controlled by, or are under common
      control with that entity. For the purposes of this definition,
      "control" means (i) the power, direct or indirect, to cause the
      direction or management of such entity, whether by contract or
      otherwise, or (ii) ownership of fifty percent (50%) or more of the
      outstanding shares, or (iii) beneficial ownership of such entity.

      "You" (or "Your") shall mean an individual or Legal Entity
      exercising permissions granted by this License.

      "Source" form shall mean the preferred form for making modifications,
      including but not limited to software source code, documentation
      source, and configuration files.

      "Object" form shall mean any form resulting from mechanical
      transformation or translation of a Source form, including but
      not limited to compiled object code, generated documentation,
      and conversions to other media types.

      "Work" shall mean the work of authorship, whether in Source or
      Object form, made available under the License, as indicated by a
      copyright notice that is included in or attached to the work
      (an example is provided in the Appendix below).

      "Derivative Works" shall mean any work, whether in Source or Object
      form, that is based on (or derived from) the Work and for which the
      editorial revisions, annotations, elaborations, or other modifications
      represent, as a whole, an original work of authorship. For the purposes
      of this License, Derivative Works shall not include works that remain
      separable from, or merely link (or bind by name) to the interfaces of,
      the Work and Derivative Works thereof.

      "Contribution" shall mean any work of authorship, including
      the original version of the Work and any modifications or additions
      to that Work or Derivative Works thereof, that is intentionally
      submitted to Licensor for inclusion in the Work by the copyright owner
      or by an individual or Legal Entity authorized to submit on behalf of
      the copyright owner. For the purposes of this definition, "submitted"
      means any form of electronic, verbal, or written communication sent
      to the Licensor or its representatives, including but not limited to
      communication on electronic mailing lists, source code control systems,
      and issue tracking systems that are managed by, or on behalf of, the
      Licensor for the purpose of discussing and improving the Work, but
      excluding communication that is conspicuously marked or otherwise
      designated in writing by the copyright owner as "Not a Contribution."

      "Contributor" shall mean Licensor and any individual or Legal Entity
      on behalf of whom a Contribution has been received by Licensor and
      subsequently incorporated within the Work.

   2. Grant of Copyright License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      copyright license to reproduce, prepare Derivative Works of,
      publicly display, publicly perform, sublicense, and distribute the
      Work and such Derivative Works in Source or Object form.

   3. Grant of Patent License. Subject to the terms and conditions of
      this License, each Contributor hereby grants to You a perpetual,
      worldwide, non-exclusive, no-charge, royalty-free, irrevocable
      (except as stated in this section) patent license to make, have made,
      use, offer to sell, sell, import, and otherwise transfer the Work,
      where such license applies only to those patent claims licensable
      by such Contributor that are necessarily infringed by their
      Contribution(s) alone or by combination of their Contribution(s)
      with the Work to which such Contribution(s) was submitted. If You
      institute patent litigation against any entity (including a
      cross-claim or counterclaim in a lawsuit) alleging that the Work
      or a Contribution incorporated within the Work constitutes direct
      or contributory patent infringement, then any patent licenses
      granted to You under this License for that Work shall terminate
      as of the date such litigation is filed.

   4. Redistribution. You may reproduce and distribute copies of the
      Work or Derivative Works thereof in any medium, with or without
      modifications, and in Source or Object form, provided that You
      meet the following conditions:

      (a) You must give any other recipients of the Work or
          Derivative Works a copy of this License; and

      (b) You must cause any modified files to carry prominent notices
          stating that You changed the files; and

      (c) You must retain, in the Source form of any Derivative Works
          that You distribute, all copyright, patent, trademark, and
          attribution notices from the Source form of the Work,
          excluding those notices that do not pertain to any part of
          the Derivative Works; and

      (d) If the Work includes a "NOTICE" text file as part of its
          distribution, then any Derivative Works that You distribute must
          include a readable copy of the attribution notices contained
          within such NOTICE file, excluding those notices that do not
          pertain to any part of the Derivative Works, in at least one
          of the following places: within a NOTICE text file distributed
          as part of the Derivative Works; within the Source form or
          documentation, if provided along with the Derivative Works; or,
          within a display generated by the Derivative Works, if and
          wherever such third-party notices normally appear. The contents
          of the NOTICE file are for informational purposes only and
          do not modify the License. You may add Your own attribution
          notices within Derivative Works that You distribute, alongside
          or as an addendum to the NOTICE text from the Work, provided
          that such additional attribution notices cannot be construed
          as modifying the License.

      You may add Your own copyright statement to Your modifications and
      may provide additional or different license terms and conditions
      for use, reproduction, or distribution of Your modifications, or
      for any such Derivative Works as a whole, provided Your use,
      reproduction, and distribution of the Work otherwise complies with
      the conditions stated in this License.

   5. Submission of Contributions. Unless You explicitly state otherwise,
      any Contribution intentionally submitted for inclusion in the Work
      by You to the Licensor shall be under the terms and conditions of
      this License, without any additional terms or conditions.
      Notwithstanding the above, nothing herein shall supersede or modify
      the terms of any separate license agreement you may have executed
      with Licensor regarding such Contributions.

   6. Trademarks. This License does not grant permission to use the trade
      names, trademarks, service marks, or product names of the Licensor,
      except as required for reasonable and customary use in describing the
      origin of the Work and reproducing the content of the NOTICE file.

   7. Disclaimer of Warranty. Unless required by applicable law or
      agreed to in writing, Licensor provides the Work (and each
      Contributor provides its Contributions) on an "AS IS" BASIS,
      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
      implied, including, without limitation, any warranties or conditions
      of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A
      PARTICULAR PURPOSE. You are solely responsible for determining the
      appropriateness of using or redistributing the Work and assume any
      risks associated with Your exercise of permissions under this License.

   8. Limitation of Liability. In no event and under no legal theory,
      whether in tort (including negligence), contract, or otherwise,
      unless required by applicable law (such as deliberate and grossly
      negligent acts) or agreed to in writing, shall any Contributor be
      liable to You for damages, including any direct, indirect, special,
      incidental, or consequential damages of any character arising as a
      result of this License or out of the use or inability to use the
      Work (including but not limited to damages for loss of goodwill,
      work stoppage, computer failure or malfunction, or any and all
      other commercial damages or losses), even if such Contributor
      has been advised of the possibility of such damages.

   9. Accepting Warranty or Additional Liability. While redistributing
      the Work or Derivative Works thereof, You may choose to offer,
      and charge a fee for, acceptance of support, warranty, indemnity,
      or other liability obligations and/or rights consistent with this
      License. However, in accepting such obligations, You may act only
      on Your own behalf and on Your sole responsibility, not on behalf
      of any other Contributor, and only if You agree to indemnify,
      defend, and hold each Contributor harmless for any liability
      incurred by, or claims asserted against, such Contributor by reason
      of your accepting any such warranty or additional liability.

   END OF TERMS AND CONDITIONS

   APPENDIX: How to apply the Apache License to your work.

      To apply the Apache License to your work, attach the following
      boilerplate notice, with the fields enclosed by brackets "[]"
      replaced with your own identifying information. (Don't include
      the brackets!)  The text should be enclosed in the appropriate
      comment syntax for the file format. We also recommend that a
      file or class name and description of purpose be included on the
      same "printed page" as the copyright notice for easier
      identification within third-party archives.

   Copyright 2018 James Fenn

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
public class MainActivity extends AestheticActivity implements ConnectListener,FragmentManager.OnBackStackChangedListener, Alarmio.ActivityListener, IabBroadcastReceiver.IabBroadcastListener
{
    public static boolean toAttach=false;
    public static boolean exitApp=false;

    public static String TAG="AestheticActivity";
    public static final String EXTRA_FRAGMENT = "james.alarmio.MainActivity.EXTRA_FRAGMENT";
    public static final int FRAGMENT_TIMER = 0;
    public static final int FRAGMENT_STOPWATCH = 2;
    private static boolean performAd;
    private Alarmio alarmio;
    private BaseFragment fragment;

    private static final int REQUEST_READ_PHONE_PERMISSIONS = 1;

    private static MainActivity mainctivityRunningInstance;
    private boolean uploaded;
    private SharedPreferences pref;
    private NetworkBroadcastReceiver broadcast;
    private boolean registered;
    private InterstitialAd mInterstitialAd;
    IabHelper mHelper;


    IabBroadcastReceiver mBroadcastReceiver;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            Log.d(TAG,"onQueryInventoryFinished");
            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                return;
            }


            if(inventory==null) Log.d(TAG, "inventory is null.");

            me.jfenn.wakeMeUp.utils.util.SkuDetails sku=inventory.getSkuDetails("price01");
            me.jfenn.wakeMeUp.utils.util.SkuDetails sku2=inventory.getSkuDetails("price02");
            me.jfenn.wakeMeUp.utils.util.SkuDetails sku3=inventory.getSkuDetails("price03");
            me.jfenn.wakeMeUp.utils.util.SkuDetails sku4=inventory.getSkuDetails("price04");
            me.jfenn.wakeMeUp.utils.util.SkuDetails sku5=inventory.getSkuDetails("price05");
            me.jfenn.wakeMeUp.utils.util.SkuDetails sku6=inventory.getSkuDetails("price06");
            me.jfenn.wakeMeUp.utils.util.SkuDetails sku7=inventory.getSkuDetails("price07");
            me.jfenn.wakeMeUp.utils.util.SkuDetails sku8=inventory.getSkuDetails("price08");
            me.jfenn.wakeMeUp.utils.util.SkuDetails sku9=inventory.getSkuDetails("price09");
            me.jfenn.wakeMeUp.utils.util.SkuDetails sku10=inventory.getSkuDetails("price010");

            if(sku==null) Log.d(TAG, "sku is null.");

            long price=sku.getPriceAmountMicros();
            String coinSymball=sku.getPriceCurrencyCode();
            double priceInDouble=price*1.0/1000000*1.0;

            SharedPreferences.Editor editor = MainActivity.this.getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
            editor.putLong("price01", DoubleToLongConverter.doubleToLong(priceInDouble));
            editor.putLong("price02",DoubleToLongConverter.doubleToLong(sku2.getPriceAmountMicros()*1.0/1000000*1.0));
            editor.putLong("price03",DoubleToLongConverter.doubleToLong(sku3.getPriceAmountMicros()*1.0/1000000*1.0));
            editor.putLong("price04",DoubleToLongConverter.doubleToLong(sku4.getPriceAmountMicros()*1.0/1000000*1.0));
            editor.putLong("price05",DoubleToLongConverter.doubleToLong(sku5.getPriceAmountMicros()*1.0/1000000*1.0));
            editor.putLong("price06",DoubleToLongConverter.doubleToLong(sku6.getPriceAmountMicros()*1.0/1000000*1.0));
            editor.putLong("price07",DoubleToLongConverter.doubleToLong(sku7.getPriceAmountMicros()*1.0/1000000*1.0));
            editor.putLong("price08",DoubleToLongConverter.doubleToLong(sku8.getPriceAmountMicros()*1.0/1000000*1.0));
            editor.putLong("price09",DoubleToLongConverter.doubleToLong(sku9.getPriceAmountMicros()*1.0/1000000*1.0));
            editor.putLong("price010",DoubleToLongConverter.doubleToLong(sku10.getPriceAmountMicros()*1.0/1000000*1.0));
            editor.putString("coinSymball",coinSymball);
            editor.apply();

        }
    };
    String licence="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk29RREpIa6f51EUK4hf3qyECdkmgR8bcxdCulw5gN1/jSRMnuL2GucDQO0DIVs/mUn+INiaorbZvDCNiYWC24fm9ccq6IAYicrLE6+Vpi7igkmpodpIr3m/5SWtfROzjiBsGgON/DnQ8aDbxq4C1/faFgReR5j6RQLo+JBx9SeIOiKguvBcfNBcUIIoaOclssg9+BfugVcFThY4F5p3so4v/4wc7QVpcDieBpI/0SGOYg36a53BLL7MJXAT7dwhQ943B6Nd3Kxl15Db4cZfzRb22CoXsrEFkWPaMbVQf+2IOPcU6KEx4+avjEhXqB4/K0bLym0d2+06sXChmSq882QIDAQAB";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }


    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            Log.d(TAG,"Error querying inventory. Another async operation in progress.");
        }
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean isGmailInstalled(){
        PackageManager pm = getPackageManager();
        return isPackageInstalled("com.google.android.gm",pm);
    }


    public static void openGmail(Context context) {
        performAd=true;
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage("com.google.android.gm");
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);

    }

    public static void openCalandar(Context context) {
        performAd=true;
        long startMillis = System.currentTimeMillis();
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, startMillis);
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
        context.startActivity(intent);
    }

    public static void turnWifiOn(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean wifiEnabled = wifiManager.isWifiEnabled();
        if(!wifiEnabled) wifiManager.setWifiEnabled(true);
    }

        public static MainActivity  getInstance(){
        return mainctivityRunningInstance;
    }




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        MyUtils.startPowerSaverIntent(this);

        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        setOnCreate(savedInstanceState, true);


        registerListener();


    }

    private void registerListener() {
        mHelper = new IabHelper(MainActivity.this, licence);
        mHelper.enableDebugLogging(true);

        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    Log.d(TAG, "!result.isSuccess().");
                    return;
                }

                if (mHelper == null) return;
                mBroadcastReceiver = new IabBroadcastReceiver(MainActivity.this);
                registered=true;
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                MainActivity.this.registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                   String[] moreSkus = {"price01","price02","price03","price04","price05","price06","price07","price08","price09","price010"};
                   MainActivity.this.runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           try {
                               mHelper.queryInventoryAsync(true, Arrays.asList(moreSkus), null, mGotInventoryListener);
                           } catch (IabHelper.IabAsyncInProgressException e) {
                               e.printStackTrace();
                           }
                       }
                   });


            }
        });

        registerListenerForId(this);
    }

    @Override
    protected void onStart() {
        super.onStart();


        Log.d(TAG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        if(performAd){
            if(mInterstitialAd!=null) {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                performAd = false;
            }
        }
        if(!uploaded&&!registered){
                IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
                broadcast = new NetworkBroadcastReceiver();
                this.registerReceiver(broadcast, intentFilter);
        }

        getTheNewIntent();

    }

    private void getTheNewIntent() {
        Intent intent=getIntent();
        Log.d(TAG,"getTheNewIntent "+(intent==null));
        if(intent!=null) {
            boolean openF2 = false;
            Bundle extras = intent.getExtras();

            if (extras != null && extras.containsKey("openF2"))
                openF2 = extras.getBoolean("openF2");
            boolean refresh = false;
            if (extras != null && extras.containsKey("refresh"))
                refresh = extras.getBoolean("refresh");

            if (refresh) {
                HomeFragment home = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, home).commit();
            }
            if (openF2) {
                refreshActivity(extras);


            }
            if (intent.hasExtra(EXTRA_FRAGMENT)) {
                boolean shouldBackStack = fragment instanceof HomeFragment;

                int fragmentId = intent.getIntExtra(EXTRA_FRAGMENT, -1);
                if (fragmentId == FRAGMENT_TIMER && intent.hasExtra(TimerReceiver.EXTRA_TIMER_ID)) {
                    int id = intent.getIntExtra(TimerReceiver.EXTRA_TIMER_ID, 0);
                    if (alarmio.getTimers().size() <= id || id < 0)
                        return;

                    Bundle args = new Bundle();
                    args.putParcelable(TimerFragment.EXTRA_TIMER, alarmio.getTimers().get(id));

                    fragment = new TimerFragment();
                    fragment.setArguments(args);
                } else if (fragmentId == FRAGMENT_STOPWATCH) {
                    if (fragment instanceof StopwatchFragment)
                        return;
                    fragment = new StopwatchFragment();
                } else return;

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_up_sheet, R.anim.slide_out_up_sheet, R.anim.slide_in_down_sheet, R.anim.slide_out_down_sheet)
                        .replace(R.id.fragment, fragment);

                if (shouldBackStack)
                    transaction.addToBackStack(null);

                transaction.commit();
            }

        }

    }


    private void setOnCreate(Bundle savedInstanceState, boolean setAlarmio) {
        Log.d(TAG,"onCreate");

        if(setAlarmio){
            alarmio = (Alarmio) getApplicationContext();
            alarmio.setListener(this);
        }
        if (savedInstanceState == null) {
            Log.d(TAG,"savedInstanceState==NULL");

            fragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).commit();
        } else {
            Log.d(TAG,"savedInstanceState!=NULL");

            if (fragment == null) {
                Log.d(TAG,"fragment == null");
                fragment = new HomeFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);




    }


    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    private void add() {
        AddPhotoBottomDialogFragment addPhotoBottomDialogFragment =
                AddPhotoBottomDialogFragment.newInstance();
        addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                "add_photo_dialog_fragment");
    }


    @Override
    protected void onStop() {
        // allow me.jfenn.wakeMeUp.backup authorized devices only
        //readphoneState();
        super.onStop();
    }



    public void getUserPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_PERMISSIONS);
            return;
        }else{
            //initIdentifyProvider();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_READ_PHONE_PERMISSIONS:
                if (grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED)
                {
                    getUserPermission();
                }

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //initIdentifyProvider();
                }
                return;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        Log.d(TAG,"onNewIntent");



    }

    public void refreshActivity(Bundle args) {
        Log.d(TAG,"refreshActivity");


        HomeFragment home=new HomeFragment();
        home.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, home).commit();
        alarmio.setAlarms();
    }

    @Override
    protected void onDestroy() {
        if (mBroadcastReceiver != null) {
            this.unregisterReceiver(mBroadcastReceiver);
        }
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }

        if (alarmio != null) {
            alarmio.setListener(null);

        }
        super.onDestroy();
    }



    @Override
    protected void onPause() {
        super.onPause();
        if(broadcast!=null&&registered){
            this.unregisterReceiver(broadcast);
            registered=false;
        }
        alarmio.stopCurrentSound();
    }


    public void uploadId(Context context){
        SharedPreferences pref=context.getSharedPreferences("threshold",Context.MODE_PRIVATE);
        boolean idIsUploaded=pref.getBoolean("idIsUploaded",false);
        if(!idIsUploaded){
           registerListenerForId(context);
        }
    }

    private void registerListenerForId(Context context) {
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        NetworkBroadcastReceiverForSkuAndId broadcast=new NetworkBroadcastReceiverForSkuAndId();
        context.registerReceiver(broadcast, intentFilter);
        broadcast.setListener(this);

    }

    @Override
    public void onBackStackChanged() {
        Log.d(TAG,"onBackStackChanged");

        fragment = (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    @Override
    public void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, 0);
    }

    @Override
    public FragmentManager gettFragmentManager() {

        return getSupportFragmentManager();
    }


    @Override
    public void onConnected() {
        Log.d(TAG,"onConnected");
        SharedPreferences pref=this.getSharedPreferences("needToPay",Context.MODE_PRIVATE);
        SharedPreferences pre2 = this.getSharedPreferences("threshold", Context.MODE_PRIVATE);


        if(pre2.getInt("id",-1)==-1) {
            int id = MyPrefsBackupAgent.getId(this);
            Log.d(TAG, "ID: " + id);
            MyPrefsBackupAgent.updateDatabase(id, pref.getBoolean("needToPay", false), pref.getInt("moneyToPay", 0), this);
            SharedPreferences.Editor pref2 = this.getSharedPreferences("threshold", Context.MODE_PRIVATE).edit();
            pref2.putBoolean("idIsUploaded", true);
            pref2.putInt("id", id);
            pref2.apply();
            new BackupManager(this).dataChanged();
        }
    }
}
