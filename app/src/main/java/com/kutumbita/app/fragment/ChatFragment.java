package com.kutumbita.app.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kutumbita.app.SurveyBotActivity;
import com.kutumbita.app.GlobalData;
import com.kutumbita.app.IssueBotActivity;
import com.kutumbita.app.R;
import com.kutumbita.app.adapter.ChatBotAdapter;
import com.kutumbita.app.chat.ChatBot;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.GridSpacingItemDecoration;
import com.kutumbita.app.viewmodel.ChatFragmentViewModel;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    public ChatFragment() {
        // Required empty public constructor
    }

    View v, layout;
    RecyclerView rcv;
    ChatBotAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipeRefreshLayout.setRefreshing(true);
            chatFragmentViewModel.getAvailableBot().observe(ChatFragment.this, new Observer<ArrayList<ChatBot>>() {
                @Override
                public void onChanged(ArrayList<ChatBot> chatBots) {
                    swipeRefreshLayout.setRefreshing(false);
                    if (chatBots != null) {
                        adapter = new ChatBotAdapter(getActivity(), chatBots);
                        adapter.inBoxLiveData.observe(ChatFragment.this, new Observer<ChatBot>() {
                            @Override
                            public void onChanged(ChatBot chatBot) {

                                goChat(chatBot.getSocket_key());

                            }
                        });

                        rcv.setAdapter(adapter);
                    }
                }
            });
        }
    };

    ChatFragmentViewModel chatFragmentViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_chat, container, false);
        layout = v.findViewById(R.id.header);
        ((TextView) layout.findViewById(R.id.tvTbTitle)).setText(getString(R.string.chat));
        chatFragmentViewModel = ViewModelProviders.of(this).get(ChatFragmentViewModel.class);

        rcv = v.findViewById(R.id.rcvChat);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rcv.setLayoutManager(gridLayoutManager);
        rcv.setItemAnimator(new DefaultItemAnimator());
        rcv.addItemDecoration(new GridSpacingItemDecoration(3, 50, true));
        swipeRefreshLayout = v.findViewById(R.id.srl);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.primaryColor,
                R.color.primaryTextColor);
        swipeRefreshLayout.setOnRefreshListener(listener);
        listener.onRefresh();


//        surveyView = v.findViewById(R.id.tapSurvey);
//        surveyView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                Intent goSurvey = new Intent(getActivity(), SurveyBotActivity.class);
////                startActivity(goSurvey);
//
//                goChat(Constant.EVENT_SURVEY);
//            }
//        });
//        issueView = v.findViewById(R.id.relIssue);
//        issueView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //goChat(Constant.EVENT_ISSUE);
//
//                Intent goChat = new Intent(getActivity(), IssueBotActivity.class);
//                goChat.putExtra(Constant.EXTRA_EVENT, Constant.EVENT_ISSUE);
//                startActivity(goChat);
//            }
//        });

        return v;
    }

    private void goChat(String socket_key) {

        Intent goChat = null;
        switch (socket_key) {

            case "issue":
                goChat = new Intent(getActivity(), IssueBotActivity.class);
                break;
            case "survey":
                goChat = new Intent(getActivity(), SurveyBotActivity.class);
                break;

        }

        if (goChat != null) {
            goChat.putExtra(Constant.EXTRA_EVENT, socket_key);
            startActivity(goChat);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalData.getInstance().getmSocket().connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalData.getInstance().getmSocket().disconnect();
    }

}
