package com.kutumbita.app;

import android.support.transition.Slide;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.kutumbita.app.fragment.SettingsFragment;
import com.kutumbita.app.fragment.settings.LanguageFragment;

public class SettingsActivity extends AppCompatActivity {

    SettingsFragment fragment;
    View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setFragment();

    }

    private void setFragment() {
        layout = findViewById(R.id.header);
        ((TextView) layout.findViewById(R.id.tvTbTitle)).setText(getString(R.string.settings));
        fragment = new SettingsFragment();
        //fragment.setEnterTransition(new Slide(Gravity.RIGHT));
        //fragment.setExitTransition(new Slide(Gravity.LEFT));
        fragment.setOnSettingEventListener(new SettingsFragment.OnSettingEventListener() {
            @Override
            public void OnLanguageClicked() {
                ((TextView) layout.findViewById(R.id.tvTbTitle)).setText(getString(R.string.language));
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .addToBackStack(null).replace(R.id.frPref, new LanguageFragment()).commit();

            }

            @Override
            public void OnFaqClicked() {

            }

            @Override
            public void OnTermsConditionClicked() {

            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.frPref, fragment).commit();
    }
}
