package me.jfenn.wakeMeUp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import be.rijckaert.tim.animatedvector.FloatingMusicActionButton;
import cdflynn.android.library.checkview.CheckView;
import me.jfenn.wakeMeUp.R;
import me.jfenn.wakeMeUp.activities.CalibrationActivity;
import me.jfenn.wakeMeUp.activities.GameActivity;

public class CalibrationFragment extends BasePagerFragment {


    private CheckView checkView;
    private boolean needCalibration;
    private String TAG = "CalibrationFragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");


        View v = inflater.inflate(R.layout.fragment_caliberation_new, container, false);

        FloatingMusicActionButton playFab = (FloatingMusicActionButton) v.findViewById(R.id.fab);

        LinearLayout calibrated = (LinearLayout) v.findViewById(R.id.calibrated);
        LinearLayout notCalibrated = (LinearLayout) v.findViewById(R.id.not_calibrated);

        SharedPreferences prefs = getContext().getSharedPreferences("threshold", getContext().MODE_PRIVATE);
        needCalibration = prefs.getBoolean("needCalibration", true);
        if (needCalibration) {
            Log.d(TAG, "needCalibration");
            notCalibrated.setVisibility(View.VISIBLE);
            calibrated.setVisibility(View.GONE);
        } else {

            Log.d(TAG, "notneedCalibration");
            notCalibrated.setVisibility(View.GONE);
            calibrated.setVisibility(View.VISIBLE);
        }

        checkView = (CheckView) v.findViewById(R.id.check);
        checkView.check();
        Button test = (Button) v.findViewById(R.id.test);
        //Button recalibrate = (Button) v.findViewById(R.id.recalibrate);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor prefs = getContext().getSharedPreferences("threshold", getContext().MODE_PRIVATE ).edit();
                prefs.putBoolean("fromCalibration",true);
                prefs.apply();
                startActivity(new Intent(getActivity(), GameActivity.class));
            }
        });

        ImageView step1 = (ImageView) v.findViewById(R.id.step_1);
        ImageView step2 = (ImageView) v.findViewById(R.id.step_2);
        ImageView step3 = (ImageView) v.findViewById(R.id.step_3);

        HorizontalStepView setpview5 = (HorizontalStepView) v.findViewById(R.id.step_view);
        List<StepBean> stepsBeanList = new ArrayList<>();
        StepBean stepBean0 = new StepBean("1", 1);
        StepBean stepBean1 = new StepBean("2", -1);
        StepBean stepBean2 = new StepBean("3", -1);

        stepsBeanList.add(stepBean0);
        stepsBeanList.add(stepBean1);
        stepsBeanList.add(stepBean2);


        setpview5.setStepViewTexts(stepsBeanList)
                .setTextSize(12)
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(getActivity(), android.R.color.holo_blue_dark))
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(getActivity(), android.R.color.holo_blue_dark))
                .setStepViewComplectedTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_blue_dark))
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_blue_dark))
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ok))
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getActivity(), R.drawable.circle2))
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getActivity(), R.drawable.attention));


        Button nextBtn = (Button) v.findViewById(R.id.next);
        TextView instruction = (TextView) v.findViewById(R.id.instruction);
        instruction.setText("Hold your phone vertically");
        final int[] i = {0};

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i[0]++;
                if (i[0] == 1) {
                    instruction.setText("Walk 10 steps forward");
                    step1.setVisibility(View.GONE);
                    step2.setVisibility(View.VISIBLE);
                    stepBean1.setState(1);
                    setpview5.setStepViewTexts(stepsBeanList);
                } else if (i[0] == 2) {
                    instruction.setText("Phone is calibrated");
                    step2.setVisibility(View.GONE);
                    step3.setVisibility(View.VISIBLE);
                    nextBtn.setText("ok,got it");
                    stepBean2.setState(1);
                    setpview5.setStepViewTexts(stepsBeanList);

                } else {
                    getActivity().startActivity(new Intent(getContext(), CalibrationActivity.class));
                    i[0] = 0;
                    instruction.setText("Hold your phone vertically");
                    nextBtn.setText("next");
                    step1.setVisibility(View.VISIBLE);
                    step2.setVisibility(View.GONE);
                    step3.setVisibility(View.GONE);
                    stepBean1.setState(-1);
                    stepBean2.setState(-1);

                }
            }
        });

        return v;
    }


    @Override
    public String getTitle(Context context) {
        return "Calibration";
    }

}
