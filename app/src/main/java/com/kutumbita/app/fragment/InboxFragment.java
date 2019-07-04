package com.kutumbita.app.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import com.google.android.material.button.MaterialButton;
import com.kutumbita.app.GlobalData;
import com.kutumbita.app.InboxDetailsActivity;
import com.kutumbita.app.R;
import com.kutumbita.app.adapter.InboxAdapter;
import com.kutumbita.app.model.Inbox;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.UrlConstant;
import com.kutumbita.app.viewmodel.InboxViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import libs.mjn.prettydialog.PrettyDialog;


public class InboxFragment extends Fragment {


    public InboxFragment() {
        // Required empty public constructor
    }


    View v;
    PreferenceUtility preferenceUtility;
    //ArrayList<Inbox> inboxes = new ArrayList<>();
    RecyclerView rcv;
    InboxAdapter adapter;
    View layout;
    MaterialButton bLoadMore;
    int totalItemInASinglePage = 0;
    int currentPage = 0;
    // StringRequest inboxRequest;
    SwipeRefreshLayout swipeRefreshLayout;
    BroadcastReceiver receiver;
    InboxViewModel inboxViewModel;
    SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            currentPage = 0;
            arrInbox.clear();
            requestInbox();

        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceUtility = new PreferenceUtility(getActivity());
        inboxViewModel = ViewModelProviders.of(getActivity()).get(InboxViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        v = inflater.inflate(R.layout.fragment_inbox, container, false);
        layout = v.findViewById(R.id.header);
        ((TextView) layout.findViewById(R.id.tvTbTitle)).setText(getString(R.string.inbox));

        bLoadMore = v.findViewById(R.id.bLoadMore);
        rcv = v.findViewById(R.id.rcvInbox);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rcv.setLayoutManager(layoutManager);
        rcv.setItemAnimator(new DefaultItemAnimator());
        rcv.addItemDecoration(new DividerItemDecoration(rcv.getContext(), DividerItemDecoration.VERTICAL));
        swipeRefreshLayout = v.findViewById(R.id.srl);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.primaryColor,
                R.color.primaryTextColor);
        swipeRefreshLayout.setOnRefreshListener(listener);
        swipeRefreshLayout.setRefreshing(true);
        bLoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestInbox();


            }
        });
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                listener.onRefresh();
            }
        };
        listener.onRefresh();
        return v;

    }

    private void requestInbox() {

        swipeRefreshLayout.setRefreshing(true);

        currentPage = currentPage + 1;
        inboxViewModel.getInboxLiveData(currentPage, 10).observe(getActivity(), new Observer<ArrayList<Inbox>>() {
            @Override
            public void onChanged(ArrayList<Inbox> inboxes) {
                swipeRefreshLayout.setRefreshing(false);
                if (inboxes != null) {
                    arrInbox.addAll(inboxes);
                    if (currentPage <= arrInbox.get(0).getPaginator().getTotalPage())
                        loadRecycleView();
                }

            }
        });
    }

    ArrayList<Inbox> arrInbox = new ArrayList<>();

    private void loadRecycleView() {


        if (arrInbox.size() > 0) {
            adapter = new InboxAdapter(getActivity(), arrInbox);
            adapter.inBoxLiveData.observe(this, new Observer<Inbox>() {
                @Override
                public void onChanged(Inbox inbox) {
                    Intent goDetails = new Intent(getActivity(), InboxDetailsActivity.class);
                    goDetails.putExtra(Constant.EXTRA_UUID, inbox.getUuId());
                    startActivity(goDetails);
                }
            });

            rcv.setAdapter(adapter);
            if(currentPage!=1)
                rcv.smoothScrollToPosition(arrInbox.size());

            if(currentPage==arrInbox.get(0).getPaginator().getTotalPage()){

                bLoadMore.setVisibility(View.GONE);
            }else{

                bLoadMore.setVisibility(View.VISIBLE);
            }

        } else {

            new PrettyDialog(getActivity())
                    .setTitle(getResources().getString(R.string.empty_inbox))
                    .setMessage(getResources().getString(R.string.empty_inbox_details))
                    .setIcon(R.drawable.ic_error_outline_black_24dp)
                    .show();
        }
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
        if (getActivity() != null)
            getActivity().unregisterReceiver(receiver);

    }


}
