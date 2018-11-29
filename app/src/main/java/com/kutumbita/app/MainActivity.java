package com.kutumbita.app;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kutumbita.app.fragment.ChatFragment;
import com.kutumbita.app.fragment.HomeFragment;
import com.kutumbita.app.fragment.InboxFragment;
import com.kutumbita.app.fragment.MeFragment;
import com.kutumbita.app.utility.Utility;

public class MainActivity extends AppCompatActivity {


    Fragment fr;
    BottomNavigationView bnv;
    Toolbar toolbar;
    Fragment currentFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utility.setFullScreen(this);

        bnv = findViewById(R.id.bottom_navigation);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                String tag = null;
                Fragment fragment = null;
                if (currentFragment == null) {
                    currentFragment = getSupportFragmentManager().findFragmentById(R.id.frMain);
                }
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

        bnv.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {

            }
        });

        bnv.setSelectedItemId(R.id.item_home);
    }

    @Override
    protected void onStart() {
        super.onStart();
        putToken();
    }

    private void putToken() {



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
