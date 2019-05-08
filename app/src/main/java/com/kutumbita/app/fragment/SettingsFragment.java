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
import com.kutumbita.app.GlobalData;
import com.kutumbita.app.MainActivity;
import com.kutumbita.app.R;
import com.kutumbita.app.SplashActivity;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.UrlConstant;
import com.kutumbita.app.viewmodel.SettingsViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;


public class SettingsFragment extends Fragment implements View.OnClickListener {


    public MutableLiveData<Boolean> faqClicked;
    public MutableLiveData<Boolean> languageClicked;
    public MutableLiveData<Boolean> termsConditionClicked;
    public MutableLiveData<Boolean> logoutClicked;

    public MutableLiveData<Boolean> changePassClicked;

    public SettingsFragment() {
        // Required empty public constructor
        faqClicked = new MutableLiveData<>();
        termsConditionClicked = new MutableLiveData<>();
        languageClicked = new MutableLiveData<>();
        logoutClicked = new MutableLiveData<>();
        changePassClicked = new MutableLiveData<>();
    }

    SettingsViewModel settingsViewModel;
    View v;
    View langLayout, termsLayout, faqLayout, changePassLayout,
            logOutLayout;

    PreferenceUtility preferenceUtility;
    StringRequest loginRequest;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceUtility = new PreferenceUtility(getActivity());
        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_settings, container, false);


        langLayout = v.findViewById(R.id.tvLanguage);
        changePassLayout = v.findViewById(R.id.tvchangePass);
        // termsLayout = v.findViewById(R.id.tvTermsAndCondition);
        // faqLayout = v.findViewById(R.id.tvFaq);
        logOutLayout = v.findViewById(R.id.tvLogout);

        ((TextView) langLayout.findViewById(R.id.value)).setText(getString(R.string.title_language));
        ((ImageView) langLayout.findViewById(R.id.icon)).setImageResource(R.drawable.language);
        langLayout.findViewById(R.id.ivNext).setVisibility(View.VISIBLE);
        langLayout.setOnClickListener(this);


        ((TextView) changePassLayout.findViewById(R.id.value)).setText(getString(R.string.change_password));
        ((ImageView) changePassLayout.findViewById(R.id.icon)).setImageResource(R.drawable.password_change);
        changePassLayout.findViewById(R.id.ivNext).setVisibility(View.VISIBLE);
        changePassLayout.setOnClickListener(this);

//        ((TextView) termsLayout.findViewById(R.id.value)).setText(getString(R.string.terms_condition));
//        ((ImageView) termsLayout.findViewById(R.id.icon)).setImageResource(R.drawable.terms_conditions);
//        termsLayout.findViewById(R.id.ivNext).setVisibility(View.VISIBLE);
//        termsLayout.setOnClickListener(this);


//        ((TextView) faqLayout.findViewById(R.id.value)).setText(getString(R.string.faq));
//        ((ImageView) faqLayout.findViewById(R.id.icon)).setImageResource(R.drawable.faq);
//        faqLayout.findViewById(R.id.ivNext).setVisibility(View.VISIBLE);
//        faqLayout.setOnClickListener(this);


        ((TextView) logOutLayout.findViewById(R.id.value)).setText(getString(R.string.logout));
        ((ImageView) logOutLayout.findViewById(R.id.icon)).setImageResource(R.drawable.log_out);
        logOutLayout.findViewById(R.id.ivNext).setVisibility(View.VISIBLE);
        logOutLayout.setOnClickListener(this);


        return v;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (loginRequest != null)
            loginRequest.cancel();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tvLanguage:


                //listener.OnLanguageClicked();

                languageClicked.setValue(true);


                break;
//            case R.id.tvTermsAndCondition:
//
//                termsConditionClicked.setValue(true);
//
//                break;
//            case R.id.tvFaq:
//
//                faqClicked.setValue(true);
//
//                break;
            case R.id.tvLogout:

                logoutClicked.setValue(true);

//                loginRequest = new StringRequest(Request.Method.GET, UrlConstant.URL_LOGOUT, new ServerResponse.Listener<String>() {
//
//                    @Override
//                    public void onResponse(String response) {
//
//                        S.L(response);
//
//
//                        try {
//
//                            JSONObject object = new JSONObject(response);
//                            if (object.getBoolean("success")) {
//                                preferenceUtility.deleteUser(preferenceUtility.getMe());
//                                Intent intent = new Intent(Constant.ACTION_BROADCAST_LOGOUT);
//                                getActivity().sendBroadcast(intent);
//                                Intent goSplash = new Intent(getActivity(), SplashActivity.class);
//                                startActivity(goSplash);
//                                getActivity().finish();
//
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
//                }, new ServerResponse.ErrorListener() {
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
                break;

            case R.id.tvchangePass:

                changePassClicked.setValue(true);

                break;


        }
    }


//    OnSettingEventListener listener;
//
//    public void setOnSettingEventListener(OnSettingEventListener listener) {
//
//        this.listener = listener;
//    }
//
//    public interface OnSettingEventListener {
//
//        void OnLanguageClicked();
//
//        void OnFaqClicked();
//
//        void OnTermsConditionClicked();
//    }
}
