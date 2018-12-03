package com.kutumbita.app.fragment.inbox;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.kutumbita.app.R;
import com.kutumbita.app.databinding.FragmentNewsDetailsBinding;
import com.kutumbita.app.model.Inbox;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.Utility;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsDetailsFragment extends Fragment {


    public NewsDetailsFragment() {
        // Required empty public constructor
    }

    View v;
    Inbox inbox;
    FragmentNewsDetailsBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inbox = (Inbox) getArguments().getSerializable(Constant.EXTRA_MESSAGE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_news_details, container, false);
        v = binding.getRoot();
        binding.setInbox(inbox);
        Picasso.get().load(inbox.getImage()).into(binding.ivNewsImage);
        //binding.tvDaysAgo.setText(TimeAgo.using(Utility.getMilliFromDate(inbox.getSentTime(), "yyyy-MM-dd'T'HH:mm:ss'Z'")));
        return v;
    }

}
