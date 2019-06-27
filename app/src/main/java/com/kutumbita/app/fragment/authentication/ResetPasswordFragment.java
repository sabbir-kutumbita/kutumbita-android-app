package com.kutumbita.app.fragment.authentication;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.kutumbita.app.R;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.Utility;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPasswordFragment extends Fragment {


    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    MaterialButton bDone;
    View v;
    TextInputLayout newPassWrapper, confirmPassWrapper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_reset_password, container, false);
        newPassWrapper = v.findViewById(R.id.passWrapper);
        confirmPassWrapper = v.findViewById(R.id.confirmPassWrapper);
        bDone= v.findViewById(R.id.bDone);
        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!hasUpError()) {

                    listener.OnResetButtonClicked(newPassWrapper.getEditText().getText().toString(), getArguments().getString(Constant.EXTRA_ACCESS_KEY));

                }


            }
        });
        return v;
    }

    OnResetButtonClickListener listener;

    public void setOnResetButtonClickListener(OnResetButtonClickListener listener) {

        this.listener = listener;

    }

    public interface OnResetButtonClickListener {

        void OnResetButtonClicked(String password, String accessKey);


    }

    private boolean hasUpError() {

        Utility.hideKeyboard(getActivity());

        if (newPassWrapper.getEditText().getText().toString().isEmpty()) {
            newPassWrapper.setError(getString(R.string.required));
            return true;
        }
        if (confirmPassWrapper.getEditText().getText().toString().isEmpty()) {
            confirmPassWrapper.setError(getString(R.string.required));
            return true;
        }

        if (!newPassWrapper.getEditText().getText().toString().contentEquals(confirmPassWrapper.getEditText().getText().toString())) {

            S.T(getActivity(), getResources().getString(R.string.password_doesnt_match));
            return true;
        }

        newPassWrapper.setErrorEnabled(false);
        confirmPassWrapper.setErrorEnabled(false);

        return false;
    }

}
