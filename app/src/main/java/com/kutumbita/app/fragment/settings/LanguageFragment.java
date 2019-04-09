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


    public MutableLiveData<String> languageStringData;

    public LanguageFragment() {
        // Required empty public constructor

        languageStringData = new MutableLiveData<>();
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



        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_language, container, false);

        radioGroup = v.findViewById(R.id.rgLang);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rbBang) {


                    languageStringData.setValue("bn");

//                    preferenceUtility.setString(Constant.LANGUAGE_SETTINGS, "bn");
//                    Utility.detectLanguage("bn", getContext());

                } else {

                    languageStringData.setValue("en");
//                    preferenceUtility.setString(Constant.LANGUAGE_SETTINGS, "en");
//                    Utility.detectLanguage("en", getContext());

                }

//                Intent intent = new Intent(Constant.ACTION_BROADCAST_LANGUAGE_CHANGE);
//                getActivity().sendBroadcast(intent);
//
//                Intent goSplash = new Intent(getActivity(), SplashActivity.class);
//                startActivity(goSplash);
//
//                getActivity().finish();
            }
        });

        return v;
    }


}
