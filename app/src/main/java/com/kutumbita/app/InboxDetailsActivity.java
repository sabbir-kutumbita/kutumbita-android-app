package com.kutumbita.app;

import android.databinding.DataBindingUtil;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.kutumbita.app.databinding.ActivityInboxDetailsBinding;
import com.kutumbita.app.model.Inbox;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.UrlConstant;

public class InboxDetailsActivity extends AppCompatActivity {


    StringRequest messageDetailsRequest;
    Inbox inbox;
    SwipeRefreshLayout swipeRefreshLayout;
    SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipeRefreshLayout.setRefreshing(true);
            parseDetails();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox_details);
        inbox = (Inbox) getIntent().getSerializableExtra(Constant.EXTRA_MESSAGE);
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

        messageDetailsRequest = new StringRequest(Request.Method.GET, UrlConstant.URL_INBOX_DETAILS + inbox.getUuId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                swipeRefreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                swipeRefreshLayout.setRefreshing(false);

            }
        });

        GlobalData.getInstance().addToRequestQueue(messageDetailsRequest);

    }
}
