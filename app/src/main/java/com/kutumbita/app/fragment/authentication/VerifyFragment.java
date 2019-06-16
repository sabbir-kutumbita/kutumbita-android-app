package com.kutumbita.app.fragment.authentication;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.kutumbita.app.R;
import com.kutumbita.app.utility.Constant;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class VerifyFragment extends Fragment {


    public VerifyFragment() {
        // Required empty public constructor
    }


    View v;
    OtpView otpView;
    TextView tvTimeValue;
    CounterTimer timer;
    MaterialButton bResend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_verify, container, false);
        otpView = v.findViewById(R.id.otp_view);
        tvTimeValue = v.findViewById(R.id.tvSecondsValue);
        bResend = v.findViewById(R.id.bResend);
        timer = new CounterTimer(61 * Constant.ONE_SECOND, Constant.ONE_SECOND);
        timer.start();
        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {

                //tvTimeValue.setEnabled(true);
            }
        });

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (timer != null) {

            timer.cancel();
        }
    }

    private class CounterTimer extends CountDownTimer {


        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public CounterTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            tvTimeValue.setText(String.valueOf(millisUntilFinished / 1000));

        }

        @Override
        public void onFinish() {
            tvTimeValue.setText(String.valueOf(0));
            bResend.setEnabled(true);

        }
    }

}
