package com.kutumbita.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kutumbita.app.fragment.inbox.AdminDetailsFragment;
import com.kutumbita.app.fragment.inbox.AnnouncementDetailsFragment;
import com.kutumbita.app.fragment.inbox.EventDetailsFragment;
import com.kutumbita.app.fragment.inbox.NewsDetailsFragment;
import com.kutumbita.app.model.Inbox;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.UrlConstant;
import com.kutumbita.app.utility.Utility;
import com.kutumbita.app.viewmodel.SettingsViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class InboxDetailsActivity extends AppCompatActivity {


    StringRequest messageDetailsRequest;
    String uuID;
    Inbox inboxDetails;
    SwipeRefreshLayout swipeRefreshLayout;
    SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            swipeRefreshLayout.setRefreshing(true);
            parseDetails();
        }
    };
    PreferenceUtility preferenceUtility;
    View layout;

//    SettingsViewModel settingsViewModel;

//    @Override
//    public void onUserInteraction() {
//        super.onUserInteraction();

//        if (GlobalData.getInstance().getOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
//            if (System.currentTimeMillis() > GlobalData.getInstance().getTouchTime() + Constant.MAXIMUM_UN_TOUCHED_TIME) {
//                settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
//                settingsViewModel.isLoggedOut().observe(this, new Observer<Boolean>() {
//                    @Override
//                    public void onChanged(Boolean aBoolean) {
//                        if (aBoolean) {
//                            preferenceUtility.setString(Constant.LANGUAGE_SETTINGS, "en");
//                            Utility.detectLanguage("en", InboxDetailsActivity.this);
//                            preferenceUtility.deleteUser(preferenceUtility.getMe());
//                            Intent intent = new Intent(Constant.ACTION_BROADCAST_LOGOUT);
//                            sendBroadcast(intent);
//                            Intent goSplash = new Intent(InboxDetailsActivity.this, SplashActivity.class);
//                            startActivity(goSplash);
//                            finish();
//                        }
//                    }
//                });
//
//            } else {
//
//                GlobalData.getInstance().setTouchTime(System.currentTimeMillis());
//            }
//
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_details);
        Utility.setFullScreen(this);
        uuID = getIntent().getStringExtra(Constant.EXTRA_UUID);
        S.L("exUUid", uuID);
        preferenceUtility = new PreferenceUtility(this);
        GlobalData.getInstance().setTouchTime(System.currentTimeMillis());
        layout = findViewById(R.id.header);

        swipeRefreshLayout = findViewById(R.id.srl);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.primaryColor,
                R.color.primaryTextColor);

        swipeRefreshLayout.setOnRefreshListener(listener);

        listener.onRefresh();

    }

    @Override
    protected void onPause() {



        super.onPause();
        if (messageDetailsRequest != null)
            messageDetailsRequest.cancel();
    }

    private void parseDetails() {

        S.L("link", UrlConstant.URL_INBOX_DETAILS + uuID);


        messageDetailsRequest = new StringRequest(Request.Method.GET, UrlConstant.URL_INBOX_DETAILS + uuID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                swipeRefreshLayout.setRefreshing(false);
                try {
                    JSONObject object = new JSONObject(response);

                    JSONObject messageTypeObject = object.getJSONObject("message_type");
                    inboxDetails = new Inbox(object.getString("uuid"), object.getString("title"), object.getString("message_body"),
                            object.getString("sent_at"), object.getString("timezone"),
                            object.getString("company_uuid"), object.getString("link"),
                            object.getString("venue"), object.getString("start_date_time"), object.getString("start_date_time"),
                            object.getString("image"),null, new Inbox.MessageType(messageTypeObject.getString("uuid"), messageTypeObject.getString("title"),
                            messageTypeObject.getString("icon")));


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                S.L(response);
                loadDetailsFragment();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                swipeRefreshLayout.setRefreshing(false);
                try {
                    String str = new String(error.networkResponse.data, "UTF-8");
                    JSONObject object = new JSONObject(str);
                    JSONObject errorObject = object.getJSONObject("error");
                    S.T(InboxDetailsActivity.this, errorObject.getString("message"));
                } catch (Exception e) {
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
        messageDetailsRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GlobalData.getInstance().addToRequestQueue(messageDetailsRequest);


    }

    private void loadDetailsFragment() {


        Fragment fragment = null;
        if (inboxDetails.getMessageType().getTitle().toLowerCase().contentEquals("event")) {

            ((TextView) findViewById(R.id.tvTbTitle)).setText("Event");
            fragment = new EventDetailsFragment();
        } else if (inboxDetails.getMessageType().getTitle().toLowerCase().contentEquals("news")) {
            ((TextView) findViewById(R.id.tvTbTitle)).setText("News");
            fragment = new NewsDetailsFragment();

        } else if (inboxDetails.getMessageType().getTitle().toLowerCase().contentEquals("admin")) {
            ((TextView) findViewById(R.id.tvTbTitle)).setText("Admin");
            fragment = new AdminDetailsFragment();
        } else if (inboxDetails.getMessageType().getTitle().toLowerCase().contentEquals("announcement")) {
            ((TextView) findViewById(R.id.tvTbTitle)).setText("Announcement");
            fragment = new AnnouncementDetailsFragment();
        }

        Bundle b = new Bundle();
        b.putSerializable(Constant.EXTRA_MESSAGE, inboxDetails);
        fragment.setArguments(b);
        getSupportFragmentManager().beginTransaction().replace(R.id.frInbox, fragment).commitAllowingStateLoss();

    }
}
