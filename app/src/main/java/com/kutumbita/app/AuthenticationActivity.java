package com.kutumbita.app;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.kutumbita.app.fragment.authentication.ChooserFragment;
import com.kutumbita.app.fragment.authentication.ForgotPasswordFragment;
import com.kutumbita.app.fragment.authentication.RequestForAccountFragment;
import com.kutumbita.app.fragment.authentication.ResetPasswordFragment;
import com.kutumbita.app.fragment.authentication.SignInFragment;
import com.kutumbita.app.fragment.authentication.VerifyFragment;
import com.kutumbita.app.model.Me;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.Utility;
import com.kutumbita.app.viewmodel.AuthenticationViewModel;
import com.kutumbita.app.viewmodel.SettingsViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class AuthenticationActivity extends AppCompatActivity {

    AuthenticationViewModel authenticationViewModel;
    Fragment fr;
    PreferenceUtility preferenceUtility;
    PrettyDialog pDialog;
    public static String emailOrPhone = "";

    boolean shouldLoadSign;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
        Utility.setFullScreen(this);
        shouldLoadSign = getIntent().getBooleanExtra(Constant.EXTRA_SHOW_SIGN_IN, false);
        preferenceUtility = new PreferenceUtility(this);
        authenticationViewModel = ViewModelProviders.of(AuthenticationActivity.this).get(AuthenticationViewModel.class);

        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT | shouldLoadSign) {
            loadSignInFragment();
        } else {
            // code for landscape mode
            loadChooserFragment();
        }

        ;


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).addToBackStack(null).replace(R.id.fr, fr).commitAllowingStateLoss();

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

                                            Intent intent = new Intent(Constant.ACTION_BROADCAST_USER_SESSION);
                                            sendBroadcast(intent);

                                            Intent goMain = new Intent(AuthenticationActivity.this, MainActivity.class);
                                            startActivity(goMain);
                                            finish();

                                        } else {

                                            S.T(AuthenticationActivity.this, getResources().getString(R.string.unable));
                                        }
                                    }
                                });

                            } catch (JSONException e) {
                                e.printStackTrace();
                                S.T(AuthenticationActivity.this, getResources().getString(R.string.unable));
                            }

                        } else {
                            S.T(AuthenticationActivity.this, getResources().getString(R.string.unable));
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
        if ((getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) | shouldLoadSign) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).replace(R.id.fr, fr).commitAllowingStateLoss();
            shouldLoadSign = false;

        } else {

            getSupportFragmentManager().beginTransaction().
                    setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).addToBackStack(null).replace(R.id.fr, fr)
                    .commitAllowingStateLoss();
        }
    }


    private void loadForgotPassFragment() {

        fr = new ForgotPasswordFragment();
        ((ForgotPasswordFragment) fr).setOnButtonClickListener(new ForgotPasswordFragment.OnButtonClickListener() {
            @Override
            public void OnSendCodeClicked(String emailOrPhone) {
                AuthenticationActivity.this.emailOrPhone = emailOrPhone;
                authenticationViewModel.otpCodeLiveData(emailOrPhone).observe(AuthenticationActivity.this, new Observer<JSONObject>() {
                    @Override
                    public void onChanged(JSONObject jsonObject) {

                        if (jsonObject != null) {

                            pDialog = new PrettyDialog(AuthenticationActivity.this)
                                    .setTitle(getResources().getString(R.string.code))
                                    .setMessage(getResources().getString(R.string.code_sent))
                                    .setIcon(R.drawable.k).addButton(getResources().getString(R.string.ok), R.color.primaryTextColor,
                                            R.color.secondaryColor, new PrettyDialogCallback() {
                                                @Override
                                                public void onClick() {

                                                    pDialog.cancel();
                                                    loadVerifyFragment();


                                                }
                                            });
                            pDialog.setCancelable(false);
                            pDialog.show();

                        } else {

                            pDialog = new PrettyDialog(AuthenticationActivity.this)
                                    .setTitle(getResources().getString(R.string.error))
                                    .setMessage(getResources().getString(R.string.something_went_wrong))
                                    .setIcon(R.drawable.ic_error_black_24dp).addButton(getResources().getString(R.string.ok), R.color.primaryTextColor,
                                            R.color.secondaryColor, new PrettyDialogCallback() {
                                                @Override
                                                public void onClick() {

                                                    pDialog.cancel();

                                                }
                                            });
                            pDialog.setCancelable(false);
                            pDialog.show();

                        }

                    }
                });

            }
        });
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).
                addToBackStack(null).replace(R.id.fr, fr).commitAllowingStateLoss();
    }

    private void loadVerifyFragment() {


        fr = new VerifyFragment();

        ((VerifyFragment) fr).setOnPinVerifySuccessful(new VerifyFragment.onPinVerifySuccessful() {
            @Override
            public void onSuccess(String access_key) {
                loadResetPasswordFragment(access_key);
            }
        });

        getSupportFragmentManager().beginTransaction().
                setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).addToBackStack(null).
                replace(R.id.fr, fr).commitAllowingStateLoss();

    }

    private void loadResetPasswordFragment(final String access_key) {

        fr = new ResetPasswordFragment();
        Bundle b = new Bundle();
        b.putString(Constant.EXTRA_ACCESS_KEY, access_key);
        fr.setArguments(b);
        ((ResetPasswordFragment) fr).setOnResetButtonClickListener(new ResetPasswordFragment.OnResetButtonClickListener() {
            @Override
            public void OnResetButtonClicked(String password, String accessKey) {

                authenticationViewModel.forgotPasswordSetNew(password, access_key).observe(AuthenticationActivity.this, new Observer<JSONObject>() {
                    @Override
                    public void onChanged(JSONObject jsonObject) {

                        pDialog = new PrettyDialog(AuthenticationActivity.this)
                                .setTitle(getResources().getString(R.string.congrats))
                                .setMessage(getResources().getString(R.string.password_updated)).setIcon(R.drawable.k).addButton(getResources().getString(R.string.ok), R.color.primaryTextColor,
                                        R.color.secondaryColor, new PrettyDialogCallback() {
                                            @Override
                                            public void onClick() {

                                                pDialog.cancel();
                                                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                                                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                                                }


                                                Intent goSignIn = new Intent(AuthenticationActivity.this, AuthenticationActivity.class);
                                                goSignIn.putExtra(Constant.EXTRA_SHOW_SIGN_IN, true);
                                                startActivity(goSignIn);
                                                finish();

                                            }
                                        });
                        pDialog.setCancelable(false);
                        pDialog.show();
                    }
                });
            }
        });
        getSupportFragmentManager().beginTransaction().
                setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right).addToBackStack(null).replace(R.id.fr, fr).commitAllowingStateLoss();


    }
}
