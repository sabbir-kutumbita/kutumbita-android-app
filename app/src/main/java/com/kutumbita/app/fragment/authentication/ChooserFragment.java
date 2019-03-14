package com.kutumbita.app.fragment.authentication;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.kutumbita.app.R;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooserFragment extends Fragment {



    public ChooserFragment() {
        // Required empty public constructor
    }

    View v;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_chooser, container, false);
        v.findViewById(R.id.bSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnSignInButtonClicked();
            }
        });
        v.findViewById(R.id.bReqForAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnRequestButtonClicked();
            }
        });

        return v;
    }


    OnButtonClickListener listener;

    public void setOnButtonClickListener(OnButtonClickListener listener){
        this.listener=listener;

    }

    public interface OnButtonClickListener{

        void OnRequestButtonClicked();
        void OnSignInButtonClicked();


    }

}
