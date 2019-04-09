package com.kutumbita.app.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kutumbita.app.ChatBotActivity;
import com.kutumbita.app.IssueBotActivity;
import com.kutumbita.app.R;
import com.kutumbita.app.utility.Constant;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    public ChatFragment() {
        // Required empty public constructor
    }

    View v;
    View layout, surveyView;
    RelativeLayout issueView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_chat, container, false);
        layout = v.findViewById(R.id.header);
        ((TextView) layout.findViewById(R.id.tvTbTitle)).setText(getString(R.string.chat));

        surveyView = v.findViewById(R.id.tapSurvey);
        surveyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent goSurvey = new Intent(getActivity(), SurveyBotActivity.class);
//                startActivity(goSurvey);

                goChat(Constant.EVENT_SURVEY);
            }
        });
        issueView = v.findViewById(R.id.relIssue);
        issueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goChat(Constant.EVENT_ISSUE);

                Intent goChat = new Intent(getActivity(), IssueBotActivity.class);
                goChat.putExtra(Constant.EXTRA_EVENT, Constant.EVENT_ISSUE);
                startActivity(goChat);
            }
        });

        return v;
    }

    private void goChat(String event) {

        Intent goChat = new Intent(getActivity(), ChatBotActivity.class);
        goChat.putExtra(Constant.EXTRA_EVENT, event);
        startActivity(goChat);
    }

}
