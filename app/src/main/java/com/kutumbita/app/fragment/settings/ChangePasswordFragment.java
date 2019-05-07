package com.kutumbita.app.fragment.settings;


import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.kutumbita.app.R;
import com.kutumbita.app.repository.SettingsRepository;
import com.kutumbita.app.utility.Utility;
import com.kutumbita.app.viewmodel.SettingsViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends Fragment {


    View v;
    TextInputLayout cPassWrapper, oPassWrapper;
    Button bDone;
    SettingsViewModel settingsViewModel;
    public MutableLiveData<Boolean> passChanged;

    public ChangePasswordFragment() {
        passChanged = new MutableLiveData<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_change_password, container, false);
        cPassWrapper = v.findViewById(R.id.passWrapper);
        oPassWrapper = v.findViewById(R.id.confirmPassWrapper);

        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        bDone = v.findViewById(R.id.bDone);
        bDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!hasUpError()) {

                    settingsViewModel.changePassword(cPassWrapper.getEditText().getText().toString(), oPassWrapper.getEditText().getText().toString()).observe(ChangePasswordFragment.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {


                            passChanged.setValue(aBoolean);


                        }
                    });
                }
            }
        });
        return v;
    }

    private boolean hasUpError() {

        Utility.hideKeyboard(getActivity());

        if (cPassWrapper.getEditText().getText().toString().isEmpty()) {
            cPassWrapper.setError(getString(R.string.required));
            return true;
        }
        if (oPassWrapper.getEditText().getText().toString().isEmpty()) {
            oPassWrapper.setError(getString(R.string.required));
            return true;
        }


        cPassWrapper.setErrorEnabled(false);
        oPassWrapper.setErrorEnabled(false);

        return false;
    }
}
