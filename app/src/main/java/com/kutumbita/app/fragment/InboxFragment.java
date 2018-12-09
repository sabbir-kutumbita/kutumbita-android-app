package com.kutumbita.app.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kutumbita.app.GlobalData;
import com.kutumbita.app.InboxDetailsActivity;
import com.kutumbita.app.R;
import com.kutumbita.app.adapter.InboxAdapter;
import com.kutumbita.app.model.Inbox;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.UrlConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InboxFragment extends Fragment {


    public InboxFragment() {
        // Required empty public constructor
    }


    View v;
    PreferenceUtility preferenceUtility;
    ArrayList<Inbox> inboxes = new ArrayList<>();
    RecyclerView rcv;
    InboxAdapter adapter;
    View layout;
    StringRequest inboxRequest;
    SwipeRefreshLayout swipeRefreshLayout;

    SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipeRefreshLayout.setRefreshing(true);
            parseInbox();
        }
    };

    BroadcastReceiver receiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceUtility = new PreferenceUtility(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_inbox, container, false);
        layout = v.findViewById(R.id.header);
        ((TextView) layout.findViewById(R.id.tvTbTitle)).setText("Inbox");
        rcv = v.findViewById(R.id.rcvInbox);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rcv.setLayoutManager(layoutManager);
        rcv.setItemAnimator(new DefaultItemAnimator());
        rcv.addItemDecoration(new DividerItemDecoration(rcv.getContext(), DividerItemDecoration.VERTICAL));
        swipeRefreshLayout = v.findViewById(R.id.srl);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.primaryColor,
                R.color.primaryTextColor);
        swipeRefreshLayout.setOnRefreshListener(listener);
        listener.onRefresh();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                listener.onRefresh();
            }
        };
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(Constant.ACTION_BROADCAST);
        if (getActivity() != null)
            getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (inboxRequest != null) {

            inboxRequest.cancel();
        }
        if (getActivity() != null)
            getActivity().unregisterReceiver(receiver);

    }

    private void parseInbox() {

        inboxRequest = new StringRequest(Request.Method.GET, UrlConstant.URL_INBOX, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                inboxes.clear();
                S.L(response);
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("results");


                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject resultObject = jsonArray.getJSONObject(i);
                        JSONObject messageTypeObject = resultObject.getJSONObject("message_type");
                        inboxes.add(new Inbox(resultObject.getString("uuid"), resultObject.getString("title"), resultObject.getString("message_body"),
                                resultObject.getString("sent_at"), resultObject.getString("timezone"),
                                resultObject.getString("company_uuid"), resultObject.getString("link"),
                                resultObject.getString("venue"), resultObject.getString("date"), resultObject.getString("time"),
                                resultObject.getString("image"), new Inbox.MessageType(messageTypeObject.getString("uuid"), messageTypeObject.getString("title"),
                                messageTypeObject.getString("icon"))));

                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
                loadRecycleView();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                S.L("error: " + error.networkResponse.statusCode);
                swipeRefreshLayout.setRefreshing(false);
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

        inboxRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GlobalData.getInstance().addToRequestQueue(inboxRequest);

    }

    private void loadRecycleView() {

        adapter = new InboxAdapter(getActivity(), inboxes);
        adapter.setOnRecycleViewItemClickListener(new InboxAdapter.OnRecycleViewItemClickListener() {
            @Override
            public void onRecycleViewItemClick(View v, List<Inbox> model, int position) {

                Intent goDetails = new Intent(getActivity(), InboxDetailsActivity.class);
                goDetails.putExtra(Constant.EXTRA_UUID, model.get(position).getUuId());
                startActivity(goDetails);

            }
        });
        rcv.setAdapter(adapter);

    }


}
