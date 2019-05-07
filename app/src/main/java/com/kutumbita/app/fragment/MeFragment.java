package com.kutumbita.app.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.button.MaterialButton;
import com.kutumbita.app.GlobalData;
import com.kutumbita.app.R;
import com.kutumbita.app.SettingsActivity;
import com.kutumbita.app.model.Me;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.DateUtility;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.UrlConstant;
import com.kutumbita.app.viewmodel.AuthenticationViewModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment implements View.OnClickListener {


    public MeFragment() {
        // Required empty public constructor
    }

    MaterialButton mbLogOut;
    View v;
    PreferenceUtility preferenceUtility;
    View layout;
    View titleLayout, positionLayout, companyNameLayout,
            mblNumberLayout, addressLayout, nIdLayout, joiningDateLayout, jobTypeLayout,
            emergencyLayout, bloodGroupLayout;

    TextView textViewAccountName;
    ImageView avatar;
    StringRequest loginRequest;
    AuthenticationViewModel authenticationViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceUtility = new PreferenceUtility(getActivity());
        authenticationViewModel = ViewModelProviders.of(this).get(AuthenticationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_me, container, false);
        layout = v.findViewById(R.id.header);
        ((TextView) layout.findViewById(R.id.tvTbTitle)).setText(getString(R.string.me));
        layout.findViewById(R.id.ivSettings).setVisibility(View.VISIBLE);
        layout.findViewById(R.id.ivSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goSettings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(goSettings);
            }
        });


        loadProfileData();

        authenticationViewModel.meLiveData(preferenceUtility.getMe().getAccessToken(), preferenceUtility.getMe().getRefreshToken()).observe(this, new Observer<Me>() {
            @Override
            public void onChanged(Me me) {
                preferenceUtility.setMe(me);
                loadProfileData();
            }
        });

        return v;

    }

    private void loadProfileData() {
        avatar = v.findViewById(R.id.profile_image);
        titleLayout = v.findViewById(R.id.tvTitleField);
        positionLayout = v.findViewById(R.id.tvPositionField);
        companyNameLayout = v.findViewById(R.id.tvCompanyNameField);
        mblNumberLayout = v.findViewById(R.id.tvMobileNum);
        addressLayout = v.findViewById(R.id.tvAddress);
        jobTypeLayout = v.findViewById(R.id.tvJobType);
        nIdLayout = v.findViewById(R.id.tvNationalId);
        joiningDateLayout = v.findViewById(R.id.tvJoiningDate);
        emergencyLayout = v.findViewById(R.id.tvEmergencyContact);
        bloodGroupLayout = v.findViewById(R.id.tvBloodGroup);

        textViewAccountName = v.findViewById(R.id.tvName);

        textViewAccountName.setText(preferenceUtility.getMe().getName());
        Picasso.get().load(preferenceUtility.getMe().getAvatar()).into(avatar);

        ((TextView) titleLayout.findViewById(R.id.key)).setText(getString(R.string.position));
        ((TextView) titleLayout.findViewById(R.id.value)).setText(preferenceUtility.getMe().getPosition());
        ((ImageView) titleLayout.findViewById(R.id.icon)).setImageResource(R.drawable.designation);

        ((TextView) positionLayout.findViewById(R.id.key)).setText(getString(R.string.department));
        ((TextView) positionLayout.findViewById(R.id.value)).setText(preferenceUtility.getMe().getDepartment());
        ((ImageView) positionLayout.findViewById(R.id.icon)).setImageResource(R.drawable.position);

        ((TextView) companyNameLayout.findViewById(R.id.key)).setText(getString(R.string.company));
        ((TextView) companyNameLayout.findViewById(R.id.value)).setText(preferenceUtility.getMe().getFactory());
        ((ImageView) companyNameLayout.findViewById(R.id.icon)).setImageResource(R.drawable.company_name);

        ((TextView) mblNumberLayout.findViewById(R.id.key)).setText(getString(R.string.phone));
        ((TextView) mblNumberLayout.findViewById(R.id.value)).setText(preferenceUtility.getMe().getPhone());
        ((ImageView) mblNumberLayout.findViewById(R.id.icon)).setImageResource(R.drawable.mobile_number);

        ((TextView) addressLayout.findViewById(R.id.key)).setText(getString(R.string.address));
        ((TextView) addressLayout.findViewById(R.id.value)).setText(preferenceUtility.getMe().getAddress());
        ((ImageView) addressLayout.findViewById(R.id.icon)).setImageResource(R.drawable.present_address);

        ((TextView) nIdLayout.findViewById(R.id.key)).setText(getString(R.string.nid));
        ((TextView) nIdLayout.findViewById(R.id.value)).setText(preferenceUtility.getMe().getnId());
        ((ImageView) nIdLayout.findViewById(R.id.icon)).setImageResource(R.drawable.nid);

        ((TextView) joiningDateLayout.findViewById(R.id.key)).setText(getString(R.string.joining_date));
        ((TextView) joiningDateLayout.findViewById(R.id.value)).setText(DateUtility.changeDateFormat("yyyy-MM-dd'T'HH:mm:SS'Z'", "dd MMMM yyyy",
                preferenceUtility.getMe().getJoinedDate()));
        ((ImageView) joiningDateLayout.findViewById(R.id.icon)).setImageResource(R.drawable.joining_date);

        ((TextView) jobTypeLayout.findViewById(R.id.key)).setText(getString(R.string.job_type));
        ((TextView) jobTypeLayout.findViewById(R.id.value)).setText(preferenceUtility.getMe().getJobType());
        ((ImageView) jobTypeLayout.findViewById(R.id.icon)).setImageResource(R.drawable.job_type);

        ((TextView) emergencyLayout.findViewById(R.id.key)).setText(getString(R.string.emergency_phone));
        ((TextView) emergencyLayout.findViewById(R.id.value)).setText(preferenceUtility.getMe().getEmergencyPhone());
        ((ImageView) emergencyLayout.findViewById(R.id.icon)).setImageResource(R.drawable.emergency_contact);

        ((TextView) bloodGroupLayout.findViewById(R.id.key)).setText(getString(R.string.blood_group));
        ((TextView) bloodGroupLayout.findViewById(R.id.value)).setText(preferenceUtility.getMe().getBloodGroup());
        ((ImageView) bloodGroupLayout.findViewById(R.id.icon)).setImageResource(R.drawable.blood_group);

    }
    @Override

    public void onClick(View v) {





    }

    @Override
    public void onPause() {
        super.onPause();
        if (loginRequest != null)
            loginRequest.cancel();
    }
}
