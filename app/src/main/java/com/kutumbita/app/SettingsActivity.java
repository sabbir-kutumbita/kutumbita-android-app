package com.kutumbita.app;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.kutumbita.app.fragment.SettingsFragment;
import com.kutumbita.app.fragment.settings.LanguageFragment;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.Utility;
import com.kutumbita.app.viewmodel.SettingsViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

public class SettingsActivity extends AppCompatActivity {

    SettingsFragment fragment;
    View layout;

    PreferenceUtility preferenceUtility;

    SettingsViewModel settingsViewModel;
//    @Override
//    public void onUserInteraction() {
//        super.onUserInteraction();
//
//        if (GlobalData.getInstance().getOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
//            if (System.currentTimeMillis() > GlobalData.getInstance().getTouchTime() + Constant.MAXIMUM_UN_TOUCHED_TIME) {
//                settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
//                settingsViewModel.isLoggedOut().observe(this, new Observer<Boolean>() {
//                    @Override
//                    public void onChanged(Boolean aBoolean) {
//                        if (aBoolean) {
//
//                            preferenceUtility.deleteUser(preferenceUtility.getMe());
//                            Intent intent = new Intent(Constant.ACTION_BROADCAST_LOGOUT);
//                            sendBroadcast(intent);
//                            Intent goSplash = new Intent(SettingsActivity.this, SplashActivity.class);
//                            startActivity(goSplash);
//                            finish();
//                        }
//                    }
//                });
//
//            } else {
//
//                GlobalData.getInstance().setTouchTime(System.currentTimeMillis());
//            }
//
//        }
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setOrientation(this, GlobalData.getInstance().getOrientation());
        setContentView(R.layout.activity_settings);
        preferenceUtility = new PreferenceUtility(this);
        GlobalData.getInstance().setTouchTime(System.currentTimeMillis());
        settingsViewModel = ViewModelProviders.of(SettingsActivity.this).get(SettingsViewModel.class);

        setFragment();

    }

    private void setFragment() {
        layout = findViewById(R.id.header);
        ((TextView) layout.findViewById(R.id.tvTbTitle)).setText(getString(R.string.settings));
        fragment = new SettingsFragment();
        //fragment.setEnterTransition(new Slide(Gravity.RIGHT));
        //fragment.setExitTransition(new Slide(Gravity.LEFT));
        fragment.logoutClicked.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {

                    settingsViewModel.isLoggedOut().observe(SettingsActivity.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            if (aBoolean) {
                                preferenceUtility.setString(Constant.LANGUAGE_SETTINGS, "en");
                                Utility.detectLanguage("en", SettingsActivity.this);
                                preferenceUtility.deleteUser(preferenceUtility.getMe());
                                Intent intent = new Intent(Constant.ACTION_BROADCAST_LOGOUT);
                                sendBroadcast(intent);
                                Intent goSplash = new Intent(SettingsActivity.this, SplashActivity.class);
                                startActivity(goSplash);
                                finish();
                            }
                        }
                    });
                }

            }
        });


        fragment.languageClicked.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    ((TextView) layout.findViewById(R.id.tvTbTitle)).setText(getString(R.string.language));
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .addToBackStack(null).replace(R.id.frPref, new LanguageFragment()).commit();
                }
            }
        });


//        fragment.setOnSettingEventListener(new SettingsFragment.OnSettingEventListener() {

//            @Override
//            public void OnLanguageClicked() {
//                ((TextView) layout.findViewById(R.id.tvTbTitle)).setText(getString(R.string.language));
//                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
//                        .addToBackStack(null).replace(R.id.frPref, new LanguageFragment()).commit();
//
//            }
//
//            @Override
//            public void OnFaqClicked() {
//
//            }
//
//            @Override
//            public void OnTermsConditionClicked() {
//
//            }
//        });
        getSupportFragmentManager().beginTransaction().replace(R.id.frPref, fragment).commit();
    }
}
