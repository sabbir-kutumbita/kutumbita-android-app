package com.kutumbita.app.fragment.settings;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.kutumbita.app.R;
import com.kutumbita.app.SplashActivity;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.Utility;

/**
 * A simple {@link Fragment} subclass.
 */
public class LanguageFragment extends Fragment {


    public LanguageFragment() {
        // Required empty public constructor
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

                    preferenceUtility.setString(Constant.LANGUAGE_SETTINGS, "bn");
                    Utility.detectLanguage("bn", getContext());

                } else {

                    preferenceUtility.setString(Constant.LANGUAGE_SETTINGS, "en");
                    Utility.detectLanguage("en", getContext());
                }
                Intent intent = new Intent(Constant.ACTION_BROADCAST_LANGUAGE_CHANGE);
                getActivity().sendBroadcast(intent);

                Intent goSplash = new Intent(getActivity(), SplashActivity.class);
                startActivity(goSplash);

                getActivity().finish();
            }
        });

        return v;
    }


}
