package com.kutumbita.app.fragment;


import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        return v;

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
