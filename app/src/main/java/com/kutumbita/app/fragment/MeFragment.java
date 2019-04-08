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
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceUtility = new PreferenceUtility(getActivity());
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
        // mbLogOut = v.findViewById(R.id.bLogout);
        // mbLogOut.setOnClickListener(this);

        loadProfileData();
        parseMe();


        return v;

    }

    private void parseMe() {
        S.L("accessToken", preferenceUtility.getMe().getAccessToken());
        loginRequest = new StringRequest(Request.Method.GET, UrlConstant.URL_ME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                S.L(response);
                try {

                    JSONObject userObject = new JSONObject(response);


                    Me me = new Me(preferenceUtility.getMe().getAccessToken()
                            , preferenceUtility.getMe().getRefreshToken(), userObject.getString("id"), userObject.getString("uuid"), userObject.getString("name"), "Star Group",
                            //userObject.getString("company"),
                            userObject.getString("factory"), userObject.getString("department"), userObject.getString("position"),
                            userObject.getString("phone"), userObject.getString("gender"),
                            userObject.getString("location"),
                            userObject.getString("emergency_contact_name"), userObject.getString("emergency_contact_phone"),
                            userObject.getString("avatar"), "O+", userObject.getString("national_id"), userObject.getString("joined_at"), userObject.getString("job_type"),"");

                    preferenceUtility.setMe(me);

                    loadProfileData();


                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                S.L("error: " + error.networkResponse.statusCode);

                try {
                    String str = new String(error.networkResponse.data, "UTF-8");
                    JSONObject object = new JSONObject(str);
                    JSONObject errorObject = object.getJSONObject("error");
                    S.T(getActivity(), errorObject.getString("message"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + preferenceUtility.getMe().getAccessToken());
                return params;
            }

        };

        loginRequest.setRetryPolicy(new DefaultRetryPolicy(

                Constant.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        GlobalData.getInstance().addToRequestQueue(loginRequest);

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

//    private void switchView(View nameLayout) {
//
//        if ((nameLayout.findViewById(R.id.value)).getVisibility() == View.VISIBLE) {
//
//            nameLayout.findViewById(R.id.value).setVisibility(View.GONE);
//            ((ImageView) nameLayout.findViewById(R.id.expand)).setImageResource(R.drawable.ic_navigate_next_black_24dp);
//
//        } else {
//
//            nameLayout.findViewById(R.id.value).setVisibility(View.VISIBLE);
//            ((ImageView) nameLayout.findViewById(R.id.expand)).setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
//        }
//    }


    @Override

    public void onClick(View v) {


//        switch (v.getId()) {
//
//            case R.id.bLogout:
//
//                StringRequest loginRequest = new StringRequest(Request.Method.GET, UrlConstant.URL_LOGOUT, new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        S.L(response);
//
//
//                        try {
//                            JSONObject object = new JSONObject(response);
//                            if (object.getBoolean("success")) {
//                                preferenceUtility.deleteUser(preferenceUtility.getMe());
//                                Intent goSplash = new Intent(getActivity(), SplashActivity.class);
//                                startActivity(goSplash);
//                                getActivity().finish();
//
//                            }
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        S.L("error: " + error.getMessage());
//
//                    }
//                }) {
//
//                    @Override
//                    public Map<String, String> getHeaders() throws AuthFailureError {
//                        Map<String, String> params = new HashMap<String, String>();
//                        params.put("Authorization", "Bearer " + preferenceUtility.getMe().getAccessToken());
//                        return params;
//                    }
//
//                };
//
//                loginRequest.setRetryPolicy(new DefaultRetryPolicy(
//                        Constant.TIME_OUT,
//                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                GlobalData.getInstance().addToRequestQueue(loginRequest);
//
//
//                break;
//
//        }





    }

    @Override
    public void onPause() {
        super.onPause();
        if (loginRequest != null)
            loginRequest.cancel();
    }
}
