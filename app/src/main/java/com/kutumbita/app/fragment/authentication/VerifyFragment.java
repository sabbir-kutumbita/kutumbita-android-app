package com.kutumbita.app.fragment.authentication;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.kutumbita.app.AuthenticationActivity;
import com.kutumbita.app.R;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.viewmodel.AuthenticationViewModel;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.json.JSONException;
import org.json.JSONObject;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

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
    AuthenticationViewModel aViewModel;
    PrettyDialog pDialog;

    public interface onPinVerifySuccessful {

        void onSuccess(String access_key);


    }

    onPinVerifySuccessful listener;

    public void setOnPinVerifySuccessful(onPinVerifySuccessful listener) {

        this.listener = listener;

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aViewModel = ViewModelProviders.of(getActivity()).get(AuthenticationViewModel.class);
    }

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

                aViewModel.otpCodeVerifyLiveData(AuthenticationActivity.emailOrPhone, otpView.getText().toString()).observe(VerifyFragment.this, new Observer<JSONObject>() {
                    @Override
                    public void onChanged(JSONObject jsonObject) {

                        if (jsonObject != null) {

                            try {
                                listener.onSuccess(jsonObject.getString("access_key"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

            }
        });


        bResend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                aViewModel.otpCodeLiveData(AuthenticationActivity.emailOrPhone).observe(VerifyFragment.this, new Observer<JSONObject>() {
                    @Override
                    public void onChanged(JSONObject jsonObject) {

                        if (jsonObject != null) {

                            pDialog = new PrettyDialog(getActivity())
                                    .setTitle(getResources().getString(R.string.code))
                                    .setMessage(getResources().getString(R.string.code_sent))
                                    .setIcon(R.drawable.k).addButton(getResources().getString(R.string.ok), R.color.primaryTextColor,
                                            R.color.secondaryColor, new PrettyDialogCallback() {
                                                @Override
                                                public void onClick() {

                                                    pDialog.cancel();
                                                    timer.start();
                                                    bResend.setEnabled(false);


                                                }
                                            });
                            pDialog.show();

                        } else {

                            S.T(getActivity(), getResources().getString(R.string.something_went_wrong));

                        }
                    }
                });
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
