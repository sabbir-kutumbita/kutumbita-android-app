package com.kutumbita.app.fragment;


import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.kutumbita.app.AuthenticationActivity;
import com.kutumbita.app.GlobalData;
import com.kutumbita.app.MainActivity;
import com.kutumbita.app.R;
import com.kutumbita.app.SplashActivity;
import com.kutumbita.app.model.Me;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.UrlConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

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
    View nameLayout, positionLayout, companyNameLayout,
            mblNumberLayout, addressLayout,
            emergencyLayout;
    TextView textViewAccountName;

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
        ((TextView) layout.findViewById(R.id.tvTbTitle)).setText("Me");
        mbLogOut = v.findViewById(R.id.bLogout);
        mbLogOut.setOnClickListener(this);

        loadProfileData();
       // parseMe();


        return v;

    }

    private void parseMe() {

        StringRequest loginRequest = new StringRequest(Request.Method.GET, UrlConstant.URL_ME, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                S.L(response);
                try {

                    JSONObject userObject = new JSONObject(response);



                    Me me = new Me(preferenceUtility.getMe().getAccessToken(), preferenceUtility.getMe().getRefreshToken(), userObject.getString("id"), userObject.getString("uuid"), userObject.getString("name"),
                            userObject.getString("company"), userObject.getString("factory"), userObject.getString("department"), userObject.getString("position"),
                            userObject.getString("phone"), userObject.getString("gender"), userObject.getString("address"),
                            userObject.getString("emergency_contact"), userObject.getString("emergency_phone"));

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

        nameLayout = v.findViewById(R.id.tvNameField);
        positionLayout = v.findViewById(R.id.tvPositionField);
        companyNameLayout = v.findViewById(R.id.tvCompanyNameField);
        mblNumberLayout = v.findViewById(R.id.tvMobileNum);
        addressLayout = v.findViewById(R.id.tvAddress);

        emergencyLayout = v.findViewById(R.id.tvEmergencyContact);

        textViewAccountName = v.findViewById(R.id.tvName);

        textViewAccountName.setText(preferenceUtility.getMe().getName());

        ((TextView) nameLayout.findViewById(R.id.key)).setText("Name");
        ((TextView) nameLayout.findViewById(R.id.value)).setText(preferenceUtility.getMe().getName());
        ((ImageView) nameLayout.findViewById(R.id.icon)).setImageResource(R.drawable.name);

        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchView(nameLayout);

            }
        });


        ((TextView) positionLayout.findViewById(R.id.key)).setText("Position");
        ((TextView) positionLayout.findViewById(R.id.value)).setText(preferenceUtility.getMe().getPosition());
        ((ImageView) positionLayout.findViewById(R.id.icon)).setImageResource(R.drawable.position);
        positionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchView(positionLayout);

            }
        });

        ((TextView) companyNameLayout.findViewById(R.id.key)).setText("Company Name");
        ((TextView) companyNameLayout.findViewById(R.id.value)).setText(preferenceUtility.getMe().getCompany());
        ((ImageView) companyNameLayout.findViewById(R.id.icon)).setImageResource(R.drawable.company_name);
        companyNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchView(companyNameLayout);

            }
        });


        ((TextView) mblNumberLayout.findViewById(R.id.key)).setText("Mobile Number");
        ((TextView) mblNumberLayout.findViewById(R.id.value)).setText(preferenceUtility.getMe().getPhone());
        ((ImageView) mblNumberLayout.findViewById(R.id.icon)).setImageResource(R.drawable.mobile_number);
        mblNumberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchView(mblNumberLayout);

            }
        });

        ((TextView) addressLayout.findViewById(R.id.key)).setText("Address");
        ((TextView) addressLayout.findViewById(R.id.value)).setText(preferenceUtility.getMe().getAddress());
        ((ImageView) addressLayout.findViewById(R.id.icon)).setImageResource(R.drawable.present_address);
        addressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchView(addressLayout);

            }
        });


        ((TextView) emergencyLayout.findViewById(R.id.key)).setText("Emergency Contact");
        ((TextView) emergencyLayout.findViewById(R.id.value)).setText(preferenceUtility.getMe().getEmergencyContact());
        ((ImageView) emergencyLayout.findViewById(R.id.icon)).setImageResource(R.drawable.emergency_contact);
        emergencyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchView(emergencyLayout);

            }
        });


    }

    private void switchView(View nameLayout) {

        if ((nameLayout.findViewById(R.id.value)).getVisibility() == View.VISIBLE) {

            nameLayout.findViewById(R.id.value).setVisibility(View.GONE);
            ((ImageView) nameLayout.findViewById(R.id.expand)).setImageResource(R.drawable.ic_navigate_next_black_24dp);

        } else {

            nameLayout.findViewById(R.id.value).setVisibility(View.VISIBLE);
            ((ImageView) nameLayout.findViewById(R.id.expand)).setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
        }
    }


    @Override

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bLogout:

                StringRequest loginRequest = new StringRequest(Request.Method.POST, UrlConstant.URL_LOGOUT, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        S.L(response);


                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.getBoolean("success")) {
                                preferenceUtility.deleteUser(preferenceUtility.getMe());
                                Intent goSplash = new Intent(getActivity(), SplashActivity.class);
                                startActivity(goSplash);
                                getActivity().finish();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        S.L("error: " + error.getMessage());

                    }
                }) {

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        return params;
                    }


                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {


                        S.L("" + response.statusCode);
                        return super.parseNetworkResponse(response);
                    }
                };

                loginRequest.setRetryPolicy(new DefaultRetryPolicy(
                        Constant.TIME_OUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                GlobalData.getInstance().addToRequestQueue(loginRequest);

                break;

        }
    }
}
