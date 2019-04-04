package com.kutumbita.app.viewmodel;

import android.app.Application;

import com.kutumbita.app.repository.SettingsRepository;
import com.kutumbita.app.utility.PreferenceUtility;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class SettingsViewModel extends AndroidViewModel {


    LiveData<Boolean> loggedOut;
    PreferenceUtility preferenceUtility;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        preferenceUtility = new PreferenceUtility(application.getApplicationContext());
        loggedOut = SettingsRepository.getInstance().logout(preferenceUtility.getMe().getAccessToken());


    }

    public LiveData<Boolean> isLoggedOut() {

        return loggedOut;
    }
}
