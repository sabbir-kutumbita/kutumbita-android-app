package com.kutumbita.app.fragment.authentication;


import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kutumbita.app.R;
import com.kutumbita.app.utility.Utility;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment implements View.OnClickListener {


    public SignInFragment() {
        // Required empty public constructor
    }

    View v;

    TextInputLayout idInput, passInput;
    MaterialButton mbLogin, mbForgotPass, mbReqAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_sign_in, container, false);
        idInput = v.findViewById(R.id.emailWrapper);
        passInput = v.findViewById(R.id.passWraper);
        mbLogin = v.findViewById(R.id.bSignIn);
        mbForgotPass = v.findViewById(R.id.bForgotPassword);
        mbReqAccount = v.findViewById(R.id.bReqForAccount);
        mbLogin.setOnClickListener(this);
        mbForgotPass.setOnClickListener(this);
        mbReqAccount.setOnClickListener(this);
        return v;
    }

    OnButtonClickListener listener;

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.listener = listener;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bSignIn:

                if (!hasUpError()) {

                    listener.OnSignInClicked(idInput.getEditText().getText().toString(), passInput.getEditText().getText().toString());
                }

                break;

            case R.id.bForgotPassword:

                listener.onForgotPasswordClicked();
                break;

            case R.id.bReqForAccount:
                listener.onRequestButtonClicked();
                break;


        }
    }

    public interface OnButtonClickListener {

        void OnSignInClicked(String emailOrPhone, String password);

        void onForgotPasswordClicked();

        void onRequestButtonClicked();

    }

    private boolean hasUpError() {

        Utility.hideKeyboard(getActivity());

        if (idInput.getEditText().getText().toString().isEmpty()) {
            idInput.setError(getString(R.string.required));
            return true;
        }
        if (passInput.getEditText().getText().toString().isEmpty()) {
            passInput.setError(getString(R.string.required));
            return true;
        }


        idInput.setErrorEnabled(false);
        passInput.setErrorEnabled(false);

        return false;
    }

}
