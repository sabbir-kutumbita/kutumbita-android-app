package com.kutumbita.app.repository;

import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kutumbita.app.GlobalData;
import com.kutumbita.app.SplashActivity;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.UrlConstant;

import org.json.JSONException;
import org.json.JSONObject;

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

    public LiveData<Boolean> logout(final String accessToken) {

        final MutableLiveData<Boolean> isSucceed = new MutableLiveData<>();

        StringRequest loginRequest = new StringRequest(Request.Method.GET, UrlConstant.URL_LOGOUT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                S.L(response);


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
}
