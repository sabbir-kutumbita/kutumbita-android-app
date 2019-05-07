package com.kutumbita.app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.kutumbita.app.AuthenticationActivity;
import com.kutumbita.app.GlobalData;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.UrlConstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationRepository {


    private static AuthenticationRepository authenticationRepository;

    private AuthenticationRepository() {

    }

    public synchronized static AuthenticationRepository getInstance() {


        if (authenticationRepository == null) {

            authenticationRepository = new AuthenticationRepository();
        }

        return authenticationRepository;

    }


    public LiveData<JSONObject> signInLiveData(String emailOrPhone, String password) {

        final MutableLiveData<JSONObject> jsonObject = new MutableLiveData<>();

        JSONObject object = new JSONObject();
        try {
            object.put("username", emailOrPhone);
            object.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String body = object.toString();


        StringRequest loginRequest = new StringRequest(Request.Method.POST, UrlConstant.URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                S.L("sign in", response);
                try {

                    JSONObject object = new JSONObject(response);

                    jsonObject.setValue(object);
                    // getMe(object.getString("access_token"), object.getString("refresh_token"));
                    //getMe(object.getString("access_token"), "refresh");


                } catch (JSONException e) {
                    e.printStackTrace();
                    jsonObject.setValue(null);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                try {
                    S.L("error: " + error.networkResponse.statusCode);
                    String str = new String(error.networkResponse.data, "UTF-8");
                    JSONObject object = new JSONObject(str);
                    JSONObject errorObject = object.getJSONObject("error");
                    jsonObject.setValue(errorObject);

                } catch (Exception e) {
                    jsonObject.setValue(null);
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

        return jsonObject;
    }
}