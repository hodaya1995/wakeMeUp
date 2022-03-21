package me.jfenn.wakeMeUp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.aesthetic.Aesthetic;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import jahirfiquitiva.libs.fabsmenu.FABsMenu;
import jahirfiquitiva.libs.fabsmenu.FABsMenuListener;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;
import me.jfenn.wakeMeUp.Alarmio;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.activities.MainActivity;
import me.jfenn.wakeMeUp.adapters.SimplePagerAdapter;
import me.jfenn.wakeMeUp.addedByMe.AddPhotoBottomDialogFragment;
import me.jfenn.wakeMeUp.data.PreferenceData;
import me.jfenn.wakeMeUp.dialogs.TimerDialog;
/*The origin code have been changed */
public class HomeFragment extends BaseFragment {
    public static boolean purchaseIsPressed;

    private static final String TAG = "HomeFragment";
    private View view;
    static  ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPager timePager;
    //private PageIndicatorView timeIndicator;
    private View bottomSheet;
    //private ImageView background;
    //private View overlay;
    private FABsMenu menu;
    private TitleFAB stopwatchFab;
    private TitleFAB timerFab;
    private TitleFAB alarmFab;

    private SimplePagerAdapter pagerAdapter;
    private SimplePagerAdapter timeAdapter;

    //private BottomSheetBehavior behavior;
    private boolean shouldCollapseBack;

    private Disposable colorPrimarySubscription;
    private Disposable colorAccentSubscription;
    private Disposable textColorPrimarySubscription;
    private Disposable textColorPrimaryInverseSubscription;
    private TextView move;
    SharedPreferences preferences;
    private boolean snoozed,needToPay;
    private FrameLayout backgroundColor;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        Log.d(MainActivity.TAG,"onCreateView(LayoutInflater inflater,");
        preferences = getContext().getSharedPreferences("bottomSheet",Context.MODE_PRIVATE);

        backgroundColor=(FrameLayout)view.findViewById(R.id.fragment_home_color);
        viewPager = view.findViewById(R.id.viewPager);
        viewPager.setId(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tabLayout);
        bottomSheet = view.findViewById(R.id.bottomSheet);
        menu = view.findViewById(R.id.fabsMenu);
        stopwatchFab = view.findViewById(R.id.stopwatchFab);
        timerFab = view.findViewById(R.id.timerFab);
        alarmFab = view.findViewById(R.id.alarmFab);

        SharedPreferences pref=getContext().getSharedPreferences("threshold",Context.MODE_PRIVATE);
        SharedPreferences pref2=getContext().getSharedPreferences("needToPay",Context.MODE_PRIVATE);
        snoozed=pref.getBoolean("snoozed",false);
        needToPay=pref2.getBoolean("needToPay",false);

        AlarmsFragment alarmsFragmnet=new AlarmsFragment();
        alarmsFragmnet.setArguments(getArguments());
        pagerAdapter = new SimplePagerAdapter(getContext(), getChildFragmentManager(), alarmsFragmnet, new SettingsFragment(),new CalibrationFragment());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() > 0) {
                   // shouldCollapseBack = behavior.getState() != BottomSheetBehavior.STATE_EXPANDED;
                   // behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    menu.hide();
                } else {
                    setClockFragments();
                    menu.show();
                    if (shouldCollapseBack) {
                        //behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        shouldCollapseBack = false;
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        setClockFragments();

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //behavior.setPeekHeight(view.getMeasuredHeight() / 2);
                //view.findViewById(R.id.timeContainer).setLayoutParams(new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, view.getMeasuredHeight() / 2));
            }
        });




        colorPrimarySubscription = Aesthetic.Companion.get()
                .colorPrimary()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        bottomSheet.setBackgroundColor(integer);
                        //overlay.setBackgroundColor(integer);
                    }
                });

        colorAccentSubscription = Aesthetic.Companion.get()
                .colorAccent()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        menu.setMenuButtonColor(integer);

                        int color = ContextCompat.getColor(getContext(), getAlarmio().getActivityTheme() == Alarmio.THEME_AMOLED ? R.color.textColorPrimary : R.color.textColorPrimaryNight);
                        menu.getMenuButton().setColorFilter(color);
                        stopwatchFab.setColorFilter(color);
                        timerFab.setColorFilter(color);
                        alarmFab.setColorFilter(color);

                        stopwatchFab.setBackgroundColor(integer);
                        timerFab.setBackgroundColor(integer);
                        alarmFab.setBackgroundColor(integer);
                    }
                });

        textColorPrimarySubscription = Aesthetic.Companion.get()
                .textColorPrimary()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        stopwatchFab.setTitleTextColor(integer);
                        timerFab.setTitleTextColor(integer);
                        alarmFab.setTitleTextColor(integer);
                    }
                });

        textColorPrimaryInverseSubscription = Aesthetic.Companion.get()
                .textColorPrimaryInverse()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        alarmFab.setTitleBackgroundColor(integer);
                        stopwatchFab.setTitleBackgroundColor(integer);
                        timerFab.setTitleBackgroundColor(integer);
                    }
                });

        stopwatchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!snoozed&& !needToPay) {
                    menu.collapseImmediately();
                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_up_sheet, R.anim.slide_out_up_sheet, R.anim.slide_in_down_sheet, R.anim.slide_out_down_sheet)
                            .replace(R.id.fragment, new StopwatchFragment())
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        timerFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!snoozed&& !needToPay) {
                    new TimerDialog(getContext(), getFragmentManager(), true).show();
                    menu.collapse();
                }
            }
        });


        menu.setMenuListener(new FABsMenuListener() {
            @Override
            public void onMenuExpanded(FABsMenu fabsMenu) {
                SharedPreferences pref = getContext().getSharedPreferences("threshold", Context.MODE_PRIVATE);
                boolean needCalibration=pref.getBoolean("needCalibration",true);
                if(needCalibration){
                    viewPager.setCurrentItem(2,true);
                }else {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED)
                                requestPermissions(new String[]{Manifest.permission.FOREGROUND_SERVICE}, 0);
                            else fabsMenu.collapseImmediately();
                        }

                }

            }
        });



        alarmFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!snoozed&& !needToPay) {
                    AddPhotoBottomDialogFragment addPhotoBottomDialogFragment = AddPhotoBottomDialogFragment.newInstance();
                    addPhotoBottomDialogFragment.setCancelable(false);
                    addPhotoBottomDialogFragment.show(getFragmentManager(), "add_photo_dialog_fragment");
                    menu.collapse();
                }


            }
        });
        return view;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Update the time zones displayed in the clock fragments pager.
     */
    private void setClockFragments() {
        if (timePager != null)
        {
            List<ClockFragment> fragments = new ArrayList<>();

            ClockFragment fragment = new ClockFragment();
            fragments.add(fragment);

            for (String id : TimeZone.getAvailableIDs()) {
                if (PreferenceData.TIME_ZONE_ENABLED.getSpecificValue(getContext(), id)) {
                    Bundle args = new Bundle();
                    args.putString(ClockFragment.EXTRA_TIME_ZONE, id);
                    fragment = new ClockFragment();
                    fragment.setArguments(args);
                    fragments.add(fragment);
                }
            }

            timeAdapter = new SimplePagerAdapter(getContext(), getChildFragmentManager(), fragments.toArray(new ClockFragment[0]));
            timePager.setAdapter(timeAdapter);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(MainActivity.TAG,"onResume");
        if(purchaseIsPressed){
                AlarmsFragment.purchaseListner.setBoolean(true);
        }


    }

    @Override
    public void onDestroyView() {
        Log.d(MainActivity.TAG,"onDestroyView");

        colorPrimarySubscription.dispose();
        colorAccentSubscription.dispose();
        textColorPrimarySubscription.dispose();
        textColorPrimaryInverseSubscription.dispose();
        super.onDestroyView();
    }








}
