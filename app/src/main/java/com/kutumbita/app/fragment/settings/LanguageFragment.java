package com.kutumbita.app.fragment.settings;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.kutumbita.app.R;
import com.kutumbita.app.SplashActivity;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.Utility;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

/**
 * A simple {@link Fragment} subclass.
 */
public class LanguageFragment extends Fragment {


    public MutableLiveData<String> languageStringLiveData;

    public LanguageFragment() {
        // Required empty public constructor

        languageStringLiveData = new MutableLiveData<>();
    }

    View v;

    PreferenceUtility preferenceUtility;
    RadioGroup radioGroup;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        preferenceUtility = new PreferenceUtility(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        v = inflater.inflate(R.layout.fragment_language, container, false);

        radioGroup = v.findViewById(R.id.rgLang);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rbBang) {


                    languageStringLiveData.setValue("bn");



                } else {

                    languageStringLiveData.setValue("en");


                }

            }
        });

        return v;
    }


}
