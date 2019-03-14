package com.kutumbita.app.fragment.authentication;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.kutumbita.app.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends Fragment {


    public ForgotPasswordFragment() {
        // Required empty public constructor
    }



    View v;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        v = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        return v;
    }

    OnButtonClickListener listener;

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.listener = listener;

    }

    public interface OnButtonClickListener {

        void OnSendCodeClicked(String emailOrPhone);

        void OnCancelClicked();


    }

}
