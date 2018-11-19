package com.kutumbita.app;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.kutumbita.app.fragment.ChatFragment;
import com.kutumbita.app.fragment.HomeFragment;
import com.kutumbita.app.fragment.InboxFragment;
import com.kutumbita.app.fragment.MeFragment;
import com.kutumbita.app.utility.Utility;

public class MainActivity extends AppCompatActivity {


    Fragment fr;
    BottomNavigationView bnv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utility.setFullScreen(this);

        bnv = findViewById(R.id.bottom_navigation);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {


                    case R.id.item_home:

                        loadHomeFragment();

                        break;


                    case R.id.item_chat:

                        loadChatFragment();
                        break;

                    case R.id.item_inbox:

                        loadInboxFragment();

                        break;

                    case R.id.item_me:

                        loadMeFragment();

                        break;

                }
                return true;
            }
        });

        bnv.setSelectedItemId(R.id.item_home);
    }

    private void loadInboxFragment() {

        fr = new InboxFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frMain, fr).commitAllowingStateLoss();
    }

    private void loadChatFragment() {

        fr = new ChatFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frMain, fr).commitAllowingStateLoss();
    }

    private void loadHomeFragment() {

        fr = new HomeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frMain, fr).commitAllowingStateLoss();
    }

    private void loadMeFragment() {
        fr = new MeFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frMain, fr).commitAllowingStateLoss();

    }


}
