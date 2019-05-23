package com.kutumbita.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.kutumbita.app.fragment.authentication.ChooserFragment;
import com.kutumbita.app.fragment.authentication.ForgotPasswordFragment;
import com.kutumbita.app.fragment.authentication.RequestForAccountFragment;
import com.kutumbita.app.fragment.authentication.SignInFragment;
import com.kutumbita.app.model.Me;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.Utility;
import com.kutumbita.app.viewmodel.AuthenticationViewModel;
import com.kutumbita.app.viewmodel.SettingsViewModel;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthenticationActivity extends AppCompatActivity {

    AuthenticationViewModel authenticationViewModel;
    Fragment fr;
    PreferenceUtility preferenceUtility;
    SettingsViewModel settingsViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.setOrientation(this, GlobalData.getInstance().getOrientation());
        setContentView(R.layout.activity_authentication);
        Utility.setFullScreen(this);
        preferenceUtility = new PreferenceUtility(this);
        authenticationViewModel = ViewModelProviders.of(AuthenticationActivity.this).get(AuthenticationViewModel.class);
        if (GlobalData.getInstance().getOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
            loadChooserFragment();
        else
            loadSignInFragment();
    }

    private void loadChooserFragment() {

        fr = new ChooserFragment();
        ((ChooserFragment) fr).setOnButtonClickListener(new ChooserFragment.OnButtonClickListener() {
            @Override
            public void OnRequestButtonClicked() {

                loadRequestForAccountFragment();
            }

            @Override
            public void OnSignInButtonClicked() {

                loadSignInFragment();

            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fr, fr).commitAllowingStateLoss();
    }

    private void loadRequestForAccountFragment() {


        fr = new RequestForAccountFragment();
        ((RequestForAccountFragment) fr).setOnButtonClickListener(new RequestForAccountFragment.OnButtonClickListener() {
            @Override
            public void OnRequestClicked(String name, String emailOrPhone, String companyName) {

            }

            @Override
            public void OnCancelClicked() {

            }
        });
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.fr, fr).addToBackStack(null).commitAllowingStateLoss();

    }

    private void loadSignInFragment() {

        fr = new SignInFragment();
        ((SignInFragment) fr).setOnButtonClickListener(new SignInFragment.OnButtonClickListener() {

            @Override
            public void OnSignInClicked(String emailOrPhone, String password) {

                authenticationViewModel.signInLiveData(emailOrPhone, password).observe(AuthenticationActivity.this, new Observer<JSONObject>() {
                    @Override
                    public void onChanged(JSONObject jsonObject) {
                        if (jsonObject != null) {
                            try {
                                authenticationViewModel.meLiveData(jsonObject.getString("access_token"), "refresh").observe(AuthenticationActivity.this, new Observer<Me>() {
                                    @Override
                                    public void onChanged(Me me) {
                                        if (me != null) {

                                            preferenceUtility.setMe(me);
                                            GlobalData.getInstance().initializeSocket();
                                            Utility.detectLanguage(preferenceUtility.getMe().getLanguage(), AuthenticationActivity.this);
                                            Intent goMain = new Intent(AuthenticationActivity.this, MainActivity.class);
                                            startActivity(goMain);
                                            finish();

                                        } else {

                                            S.T(AuthenticationActivity.this, "Something went wrong");
                                        }
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                                S.T(AuthenticationActivity.this, "Something went wrong");
                            }

                        } else {

                            S.T(AuthenticationActivity.this, "Something went wrong");
                        }
                    }
                });

            }

            @Override
            public void onForgotPasswordClicked() {

                loadForgotPassFragment();

            }

            @Override
            public void onRequestButtonClicked() {
                loadRequestForAccountFragment();
            }
        });
        if (GlobalData.getInstance().getOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.fr, fr).commitAllowingStateLoss();
        else
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.fr, fr).addToBackStack(null).commitAllowingStateLoss();

    }

    private void loadForgotPassFragment() {

        fr = new ForgotPasswordFragment();
        ((ForgotPasswordFragment) fr).setOnButtonClickListener(new ForgotPasswordFragment.OnButtonClickListener() {
            @Override
            public void OnSendCodeClicked(String emailOrPhone) {


            }

            @Override
            public void OnCancelClicked() {
                getSupportFragmentManager().popBackStack();
            }
        });
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.fr, fr).addToBackStack(null).commitAllowingStateLoss();
    }

}
