package com.kutumbita.app;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.kutumbita.app.fragment.SettingsFragment;
import com.kutumbita.app.fragment.settings.ChangePasswordFragment;
import com.kutumbita.app.fragment.settings.LanguageFragment;
import com.kutumbita.app.model.Me;
import com.kutumbita.app.model.ServerResponse;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.Utility;
import com.kutumbita.app.viewmodel.AuthenticationViewModel;
import com.kutumbita.app.viewmodel.SettingsViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

public class SettingsActivity extends AppCompatActivity {

    SettingsFragment fragment;
    View layout;

    PreferenceUtility preferenceUtility;
    AuthenticationViewModel authenticationViewModel;
    SettingsViewModel settingsViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        preferenceUtility = new PreferenceUtility(this);
        GlobalData.getInstance().setTouchTime(System.currentTimeMillis());
        settingsViewModel = ViewModelProviders.of(SettingsActivity.this).get(SettingsViewModel.class);
        authenticationViewModel = ViewModelProviders.of(SettingsActivity.this).get(AuthenticationViewModel.class);
        setFragment();

    }

    private void setFragment() {
        layout = findViewById(R.id.header);
        ((TextView) layout.findViewById(R.id.tvTbTitle)).setText(getString(R.string.settings));
        fragment = new SettingsFragment();
        fragment.logoutClicked.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                if (aBoolean) {

                    settingsViewModel.isLoggedOut().observe(SettingsActivity.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            if (aBoolean) {


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
                    Fragment languageFragment = new LanguageFragment();

                    ((LanguageFragment) languageFragment).languageStringLiveData.observe(SettingsActivity.this, new Observer<String>() {
                        @Override
                        public void onChanged(final String s) {

                            settingsViewModel.setLanguage(s).observe(SettingsActivity.this, new Observer<Boolean>() {
                                @Override
                                public void onChanged(Boolean aBoolean) {
                                    if (aBoolean) {

                                        authenticationViewModel.meLiveData(preferenceUtility.getMe().getAccessToken(), preferenceUtility.getMe().getRefreshToken()).observe(SettingsActivity.this, new Observer<Me>() {
                                            @Override
                                            public void onChanged(Me me) {
                                                if (me != null) {
                                                    preferenceUtility.setMe(me);

                                                    Intent intent = new Intent(Constant.ACTION_BROADCAST_LANGUAGE_CHANGE);
                                                    sendBroadcast(intent);

                                                    Intent goSplash = new Intent(SettingsActivity.this, SplashActivity.class);
                                                    startActivity(goSplash);

                                                    finish();

                                                } else {

                                                    S.T(SettingsActivity.this, "Failed to update language");
                                                }
                                            }
                                        });

                                    }
                                }
                            });

                        }
                    });

                    ((TextView) layout.findViewById(R.id.tvTbTitle)).setText(getString(R.string.language));
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .addToBackStack(null).replace(R.id.frPref, languageFragment).commit();
                }
            }
        });


        fragment.changePassClicked.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Fragment changePassFr = new ChangePasswordFragment();
                    ((ChangePasswordFragment) changePassFr).passChanged.observe(SettingsActivity.this, new Observer<ServerResponse>() {
                        @Override
                        public void onChanged(ServerResponse serverResponse) {

                            S.T(SettingsActivity.this, serverResponse.getMessage());
                            if (serverResponse.isSucceess()) {

                                getSupportFragmentManager().popBackStack();
                            }
                        }
                    });


                    ((TextView) layout.findViewById(R.id.tvTbTitle)).setText(getString(R.string.change_password));
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .addToBackStack(null).replace(R.id.frPref, changePassFr).commit();
                }


            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.frPref, fragment).commit();
    }
}
