package com.kutumbita.app.fragment.inbox;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.kutumbita.app.R;
import com.kutumbita.app.databinding.FragmentNewsDetailsBinding;
import com.kutumbita.app.model.Inbox;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.DateUtility;
import com.kutumbita.app.utility.Utility;
import com.squareup.picasso.Picasso;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

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

        if (inbox.getImage().isEmpty()) {

            binding.ivNewsImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            binding.ivNewsImage.setImageResource(R.drawable.kutumbita_with_logo);


        } else {


            Picasso.get().load(inbox.getImage()).into(binding.ivNewsImage);

        }

        //binding.tvDaysAgo.setText(TimeAgo.using(Utility.getMilliFromDate(inbox.getSentTime(), "yyyy-MM-dd'T'HH:mm:ss'Z'")));

        binding.tvDaysAgo.setText(DateUtility.changeDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd MMM",
                inbox.getSentTime()));


//        if (binding.tvWeb.getText().toString().isEmpty()) {
//
//            binding.tvRead.setVisibility(View.INVISIBLE);
//        }
        binding.tvWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "";
                if (binding.tvWeb.getText().toString().startsWith("http://")| binding.tvWeb.getText().toString().startsWith("https://"))
                    url = binding.tvWeb.getText().toString();
                else
                    url = "http://" + binding.tvWeb.getText().toString();

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);

            }
        });

        return v;
    }

}
