package com.kutumbita.app;

import android.content.Intent;

import android.content.pm.ActivityInfo;
import android.os.Bundle;


import com.google.gson.Gson;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.Utility;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    PreferenceUtility preferenceUtility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_splash);
        Utility.setFullScreen(this);
        preferenceUtility = new PreferenceUtility(this);
        Thread thread = new Thread(new WaitThread());
        thread.start();

    }


    class WaitThread implements Runnable {


        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (Utility.isNetworkAvailable(SplashActivity.this)) {

                    if (preferenceUtility.getMe() == null) {
                        Intent goAuth = new Intent(SplashActivity.this, AuthenticationActivity.class);
                        startActivity(goAuth);
                    } else {

                        Intent goMain = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(goMain);
                    }

                } else {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            S.T(SplashActivity.this, "Internet not available");
                        }
                    });


                }

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
