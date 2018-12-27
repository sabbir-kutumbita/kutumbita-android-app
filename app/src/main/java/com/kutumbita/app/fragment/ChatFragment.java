package com.kutumbita.app.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kutumbita.app.ChatBotActivity;
import com.kutumbita.app.R;
import com.kutumbita.app.SurveyBotActivity;

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
        ((TextView) layout.findViewById(R.id.tvTbTitle)).setText("Chat");
        surveyView = v.findViewById(R.id.tapSurvey);
        surveyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goSurvey = new Intent(getActivity(), SurveyBotActivity.class);
                startActivity(goSurvey);
            }
        });
        issueView = v.findViewById(R.id.relIssue);
        issueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goChat = new Intent(getActivity(), ChatBotActivity.class);
                startActivity(goChat);
            }
        });

        return v;
    }

}
