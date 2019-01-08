package com.kutumbita.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kutumbita.app.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().beginTransaction().replace(R.id.frPref, new SettingsFragment()).commit();
    }
}
