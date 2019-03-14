package com.kutumbita.app.fragment.inbox;



import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kutumbita.app.R;
import com.kutumbita.app.databinding.FragmentAnnouncementDetailsBinding;
import com.kutumbita.app.model.Inbox;
import com.kutumbita.app.utility.Constant;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnnouncementDetailsFragment extends Fragment {


    public AnnouncementDetailsFragment() {
        // Required empty public constructor
    }

    View v;
    Inbox inbox;
    FragmentAnnouncementDetailsBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inbox = (Inbox) getArguments().getSerializable(Constant.EXTRA_MESSAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_announcement_details, container, false);
        v = binding.getRoot();
        binding.setInbox(inbox);

        return v;
    }

}
