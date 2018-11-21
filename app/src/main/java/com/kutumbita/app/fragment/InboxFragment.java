package com.kutumbita.app.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.kutumbita.app.AuthenticationActivity;
import com.kutumbita.app.GlobalData;
import com.kutumbita.app.InboxDetailsActivity;
import com.kutumbita.app.MainActivity;
import com.kutumbita.app.R;
import com.kutumbita.app.adapter.InboxAdapter;
import com.kutumbita.app.model.Inbox;
import com.kutumbita.app.model.Me;
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
        parseInbox();
        return v;
    }

    private void parseInbox() {

        StringRequest loginRequest = new StringRequest(Request.Method.GET, UrlConstant.URL_INBOX, new Response.Listener<String>() {
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

                loadRecycleView();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                S.L("error: " + error.networkResponse.statusCode);

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
                params.put("Authorization", "Bearer " + preferenceUtility.getMe().getToken());
                return params;
            }


        };

        loginRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GlobalData.getInstance().addToRequestQueue(loginRequest);

    }

    private void loadRecycleView() {

        adapter = new InboxAdapter(getActivity(), inboxes);
        adapter.setOnRecycleViewItemClickListener(new InboxAdapter.OnRecycleViewItemClickListener() {
            @Override
            public void onRecycleViewItemClick(View v, List<Inbox> model, int position) {

                Intent goDetails = new Intent(getActivity(), InboxDetailsActivity.class);

                startActivity(goDetails);

            }
        });
        rcv.setAdapter(adapter);

    }


}
