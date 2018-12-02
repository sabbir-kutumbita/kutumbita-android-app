package com.kutumbita.app.fragment.inbox;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kutumbita.app.R;
import com.kutumbita.app.model.Inbox;
import com.kutumbita.app.utility.Constant;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsDetailsFragment extends Fragment {


    public NewsDetailsFragment() {
        // Required empty public constructor
    }

    View v;
    Inbox inbox;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inbox = (Inbox) getArguments().getSerializable(Constant.EXTRA_MESSAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_news_details, container, false);
        return v;
    }

}
