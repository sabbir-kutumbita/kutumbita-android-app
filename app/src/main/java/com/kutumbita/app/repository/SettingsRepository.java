package com.kutumbita.app.repository;

import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonObject;
import com.kutumbita.app.GlobalData;
import com.kutumbita.app.SplashActivity;
import com.kutumbita.app.model.Me;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.UrlConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class SettingsRepository {

    private static SettingsRepository settingsRepository;

    private SettingsRepository() {


    }

    public synchronized static SettingsRepository getInstance() {


        if (settingsRepository == null) {

            settingsRepository = new SettingsRepository();
        }

        return settingsRepository;

    }

    public LiveData<Boolean> logoutLiveData(final String accessToken) {

        final MutableLiveData<Boolean> isSucceed = new MutableLiveData<>();


        StringRequest loginRequest = new StringRequest(Request.Method.GET, UrlConstant.URL_LOGOUT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                S.L("Logout", response);


                try {

                    JSONObject object = new JSONObject(response);
                    if (object.getBoolean("success")) {
                        isSucceed.setValue(true);


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    isSucceed.setValue(false);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                S.L("error: " + error.getMessage());
                isSucceed.setValue(false);

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + accessToken);
                return params;
            }

        };

        loginRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GlobalData.getInstance().addToRequestQueue(loginRequest);

        return isSucceed;

    }

    public LiveData<Me> languageLiveData(final String language, final String accessToken, final String refreshToken) {

        final MutableLiveData<Me> updatedMe = new MutableLiveData<>();


        JSONObject object = new JSONObject();
        try {
            object.put("language", language);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String body = object.toString();


        S.L("languagebody json", body);
        S.L("URL lang", UrlConstant.URL_UPDATE_LANGUAGE);
        StringRequest languageRequest = new StringRequest(Request.Method.PUT, UrlConstant.URL_UPDATE_LANGUAGE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                S.L("me json", response);


                try {
                    JSONObject userObject = new JSONObject(response);


                    Me me = new Me(accessToken
                            , refreshToken, userObject.getString("id"), userObject.getString("uuid"), userObject.getString("name"), "Star Group",
                            userObject.getString("factory"), userObject.getString("department"), userObject.getString("position"),
                            userObject.getString("phone"), userObject.getString("gender"),
                            userObject.getString("location"),
                            userObject.getString("emergency_contact_name"), userObject.getString("emergency_contact_phone"),
                            userObject.getString("avatar"), "O+", userObject.getString("national_id"), userObject.getString("joined_at"), userObject.getString("job_type"), userObject.getString("language").toLowerCase());


                    updatedMe.setValue(me);
                } catch (JSONException e) {
                    e.printStackTrace();
                    updatedMe.setValue(null);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                S.L("error: " + error.getMessage());
                updatedMe.setValue(null);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + accessToken);

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

        languageRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GlobalData.getInstance().addToRequestQueue(languageRequest);

        return updatedMe;

    }
}
