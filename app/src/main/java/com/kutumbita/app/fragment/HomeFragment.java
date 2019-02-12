package com.kutumbita.app.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kutumbita.app.R;
import com.kutumbita.app.utility.PreferenceUtility;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    View v;
    PreferenceUtility preferenceUtility;
    TextView tvWelcome;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceUtility = new PreferenceUtility(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, container, false);
        tvWelcome = v.findViewById(R.id.tvWelcome);
        tvWelcome.setText(getString(R.string.welcome) + preferenceUtility.getMe().getName().split(" ")[0] + ", " + getString(R.string.how_can_i_help_you));
        return v;
    }

}
