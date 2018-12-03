package com.kutumbita.app.fragment.inbox;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kutumbita.app.R;
import com.kutumbita.app.databinding.FragmentAnnouncementDetailsBinding;
import com.kutumbita.app.databinding.FragmentEventDetailsBinding;
import com.kutumbita.app.model.Inbox;
import com.kutumbita.app.utility.Constant;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventDetailsFragment extends Fragment {


    public EventDetailsFragment() {
        // Required empty public constructor
    }

    View v;
    Inbox inbox;
    FragmentEventDetailsBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inbox = (Inbox) getArguments().getSerializable(Constant.EXTRA_MESSAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_details, container, false);
        v = binding.getRoot();
        binding.setInbox(inbox);
        Picasso.get().load(inbox.getImage()).into(binding.ivImage);
        return v;
    }

}
