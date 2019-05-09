package com.kutumbita.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kutumbita.app.fragment.ChatFragment;
import com.kutumbita.app.fragment.HomeFragment;
import com.kutumbita.app.fragment.InboxFragment;
import com.kutumbita.app.fragment.MeFragment;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.UrlConstant;
import com.kutumbita.app.utility.Utility;
import com.kutumbita.app.viewmodel.SettingsViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity {


    Fragment fr;
    BottomNavigationView bnv;
    Fragment currentFragment;
    PreferenceUtility preferenceUtility;
    boolean shouldShow;
    BroadcastReceiver receiver, langReceiver;
    SettingsViewModel settingsViewModel;
    public static int TAB_POSITION = 0;
    public static int TAB_LAST_POSITION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setOrientation(this, GlobalData.getInstance().getOrientation());
        setContentView(R.layout.activity_main);
        Utility.setFullScreen(this);
        preferenceUtility = new PreferenceUtility(this);
        GlobalData.getInstance().setTouchTime(System.currentTimeMillis());
        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        bnv = findViewById(R.id.bottom_navigation);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                String tag = null;
//                Fragment fragment = null;
//                if (currentFragment == null) {
//                    currentFragment = getSupportFragmentManager().findFragmentById(R.id.frMain);
//                }
//
//
//                switch (menuItem.getItemId()) {
//
//                    case R.id.item_home:
//
//                        if (!(currentFragment instanceof HomeFragment)) {
//                            fragment = getSupportFragmentManager().findFragmentByTag("home");
//                            if (fragment == null) {
//                                fragment = new HomeFragment();
//                                tag = "home";
//                                shouldShow = false;
//                            } else {
//
//                                shouldShow = true;
//                            }
//                        }
//                        break;
//                    case R.id.item_chat:
//
//                        if (!(currentFragment instanceof ChatFragment)) {
//                            fragment = getSupportFragmentManager().findFragmentByTag("chat");
//                            if (fragment == null) {
//                                fragment = new ChatFragment();
//                                tag = "chat";
//                                shouldShow = false;
//                            } else {
//
//                                shouldShow = true;
//                            }
//                        }
//                        break;
//
//                    case R.id.item_inbox:
//
//                        if (!(currentFragment instanceof InboxFragment)) {
//                            fragment = getSupportFragmentManager().findFragmentByTag("inbox");
//                            if (fragment == null) {
//                                fragment = new InboxFragment();
//                                tag = "inbox";
//                                shouldShow = false;
//                            } else {
//
//                                shouldShow = true;
//                            }
//                        }
//                        break;
//
//                    case R.id.item_me:
//                        if (!(currentFragment instanceof MeFragment)) {
//                            fragment = getSupportFragmentManager().findFragmentByTag("me");
//                            if (fragment == null) {
//                                fragment = new MeFragment();
//                                tag = "me";
//                                shouldShow = false;
//                            } else {
//
//                                shouldShow = true;
//                            }
//                        }
//                        break;
//
//                }
//                if (fragment != null) {
//                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//
//                    if (shouldShow) {
//
//                        fragmentTransaction.hide(currentFragment);
//                        fragmentTransaction.show(fragment);
//                    } else {
//                        fragmentTransaction.hide(currentFragment);
//                        fragmentTransaction.add(R.id.frMain, fragment, tag);
//                    }
//                    fragmentTransaction.commit();
//                    currentFragment = fragment;
//                }


                switch (menuItem.getItemId()) {

                    case R.id.item_home:

                        TAB_POSITION = 0;
                        loadHomeFragment();


                        break;


                    case R.id.item_chat:
                        TAB_POSITION = 1;
                        loadChatFragment();

                        break;

                    case R.id.item_inbox:
                        TAB_POSITION = 2;
                        loadInboxFragment();

                        break;

                    case R.id.item_me:
                        TAB_POSITION = 3;
                        loadMeFragment();

                        break;

                }
                return true;
            }
        });

        bnv.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {


            }
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                finish();
            }
        };
        loadHomeFragment();
        if (GlobalData.getInstance().getOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
            putToken();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_BROADCAST_LOGOUT);
        filter.addAction(Constant.ACTION_BROADCAST_LANGUAGE_CHANGE);
        registerReceiver(receiver, filter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    private void loadInboxFragment() {

        fr = new InboxFragment();
        animateFragment();
        TAB_LAST_POSITION = 2;
    }

    private void animateFragment() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (TAB_POSITION > TAB_LAST_POSITION)
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
        else if (TAB_POSITION < TAB_LAST_POSITION)
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);

        fragmentTransaction.replace(R.id.frMain, fr).commitAllowingStateLoss();
    }

    private void loadChatFragment() {
        fr = new ChatFragment();
        animateFragment();

        TAB_LAST_POSITION = 1;
    }

    private void loadHomeFragment() {


        fr = new HomeFragment();

        animateFragment();
        TAB_LAST_POSITION = 0;
    }

    private void loadMeFragment() {

        fr = new MeFragment();
        animateFragment();
        TAB_LAST_POSITION = 3;

    }

    private void putToken() {


        S.L("accessToken", preferenceUtility.getMe().getAccessToken());
        S.L("fcm_token", preferenceUtility.getFcmToken());
        S.L("user_id", preferenceUtility.getMe().getUuId());
        JSONObject object = new JSONObject();
        try {
            object.put("user_uuid", preferenceUtility.getMe().getUuId());
            object.put("device_token", preferenceUtility.getFcmToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String body = object.toString();

        StringRequest loginRequest = new StringRequest(Request.Method.PUT, UrlConstant.URL_TOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                S.L("fcm", response);
            }

        }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
//                S.L("error: " + error.networkResponse.statusCode);

                try {
                    String str = new String(error.networkResponse.data, "UTF-8");
                    JSONObject object = new JSONObject(str);
                    JSONObject errorObject = object.getJSONObject("error");
                    S.T(getApplicationContext(), errorObject.getString("message"));
                } catch (Exception e) {
                    S.T(MainActivity.this, "Something went wrong!");
                    e.printStackTrace();
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + preferenceUtility.getMe().getAccessToken());
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
    public void onUserInteraction() {
        super.onUserInteraction();

        if (GlobalData.getInstance().getOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
            if (System.currentTimeMillis() > GlobalData.getInstance().getTouchTime() + Constant.MAXIMUM_UN_TOUCHED_TIME) {
                settingsViewModel.isLoggedOut().observe(this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        if (aBoolean) {


                            preferenceUtility.deleteUser(preferenceUtility.getMe());
//                            Intent intent = new Intent(Constant.ACTION_BROADCAST_LOGOUT);
//                            sendBroadcast(intent);

                            Intent goSplash = new Intent(MainActivity.this, SplashActivity.class);
                            startActivity(goSplash);
                            finish();
                        }
                    }
                });

            }

            GlobalData.getInstance().setTouchTime(System.currentTimeMillis());


        }
    }


}
