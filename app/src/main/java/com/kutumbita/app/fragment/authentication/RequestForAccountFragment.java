package com.kutumbita.app.fragment.authentication;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.kutumbita.app.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestForAccountFragment extends Fragment {


    public RequestForAccountFragment() {
        // Required empty public constructor
    }

    View v;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_request_for_account, container, false);
        return v;
    }


    OnButtonClickListener listener;

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.listener = listener;

    }

    public interface OnButtonClickListener {

        void OnRequestClicked(String name, String emailOrPhone, String companyName);

        void OnCancelClicked();


    }

}
