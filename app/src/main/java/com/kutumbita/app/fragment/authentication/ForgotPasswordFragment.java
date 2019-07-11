package com.kutumbita.app.fragment.authentication;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.kutumbita.app.R;
import com.kutumbita.app.utility.Utility;

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
    MaterialButton mButton;
    TextInputLayout pLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        v = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        mButton = v.findViewById(R.id.bSendCode);
        pLayout = v.findViewById(R.id.emailWrapper);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!hasUpError()) {


                    listener.OnSendCodeClicked(pLayout.getEditText().getText().toString());
                }
            }
        });
        return v;
    }

    private boolean hasUpError() {

        Utility.hideKeyboard(getActivity());

        if (pLayout.getEditText().getText().toString().isEmpty()) {

            return true;
        }


        pLayout.setErrorEnabled(false);


        return false;
    }

    OnButtonClickListener listener;

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.listener = listener;

    }

    public interface OnButtonClickListener {

        void OnSendCodeClicked(String emailOrPhone);


    }

}
