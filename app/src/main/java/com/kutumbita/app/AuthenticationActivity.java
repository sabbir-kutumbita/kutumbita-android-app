package com.kutumbita.app;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kutumbita.app.fragment.authentication.ChooserFragment;
import com.kutumbita.app.fragment.authentication.RequestForAccountFragment;
import com.kutumbita.app.fragment.authentication.SignInFragment;
import com.kutumbita.app.model.Me;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.UrlConstant;
import com.kutumbita.app.utility.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationActivity extends AppCompatActivity {


    Fragment fr;
    PreferenceUtility preferenceUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        Utility.setFullScreen(this);
        preferenceUtility = new PreferenceUtility(this);
        loadChooserFragment();
    }

    private void loadChooserFragment() {

        fr = new ChooserFragment();
        ((ChooserFragment) fr).setOnButtonClickListener(new ChooserFragment.OnButtonClickListener() {
            @Override
            public void OnRequestButtonClicked() {

                loadRequestForAccountFragment();
            }

            @Override
            public void OnSignInButtonClicked() {

                loadSignInFragment();

            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fr, fr).commitAllowingStateLoss();
    }

    private void loadRequestForAccountFragment() {


        fr = new RequestForAccountFragment();
        ((RequestForAccountFragment) fr).setOnButtonClickListener(new RequestForAccountFragment.OnButtonClickListener() {
            @Override
            public void OnRequestClicked(String name, String emailOrPhone, String companyName) {

            }

            @Override
            public void OnCancelClicked() {

            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fr, fr).commitAllowingStateLoss();

    }

    private void loadSignInFragment() {

        fr = new SignInFragment();
        ((SignInFragment) fr).setOnButtonClickListener(new SignInFragment.OnButtonClickListener() {
            @Override
            public void OnSignInClicked(String emailOrPhone, String password) {


                JSONObject object = new JSONObject();
                try {
                    object.put("user_id", emailOrPhone);
                    object.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final String body = object.toString();

                StringRequest loginRequest = new StringRequest(Request.Method.POST, UrlConstant.URL_LOGIN, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        S.L(response);


                        try {
                            JSONObject object = new JSONObject(response);
                            JSONObject userObject = object.getJSONObject("user");

                            Me me = new Me(object.getString("token"), userObject.getString("id"), userObject.getString("uuid"), userObject.getString("name"),
                                    userObject.getString("factory"), userObject.getString("department"), userObject.getString("position"),
                                    userObject.getString("phone"), userObject.getString("gender"), userObject.getString("address"),
                                    userObject.getString("emergency_contact"), userObject.getString("emergency_phone"));

                            preferenceUtility.setMe(me);


                            Intent goMain = new Intent(AuthenticationActivity.this, MainActivity.class);
                            startActivity(goMain);

                            finish();


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
                            S.T(getApplicationContext(), errorObject.getString("message"));
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
                        params.put("Content-Type", "application/json");
                        return params;
                    }


                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return body == null ? null : body.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                    body, "utf-8");
                            return null;
                        }
                    }

                };

                loginRequest.setRetryPolicy(new DefaultRetryPolicy(
                        Constant.TIME_OUT,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                GlobalData.getInstance().addToRequestQueue(loginRequest);

            }

            @Override
            public void onForgotPasswordClicked() {

            }

            @Override
            public void onRequestButtonClicked() {

            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fr, fr).commitAllowingStateLoss();
    }

}
