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
    LiveData<Boolean> languageData;
    SettingsRepository settingsRepository;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        preferenceUtility = new PreferenceUtility(application.getApplicationContext());
        settingsRepository = SettingsRepository.getInstance();


    }

//    public void performLogout() {
//
//        loggedOut = settingsRepository.logoutLiveData
//                (preferenceUtility.getMe().getAccessToken());
//
//    }

    public LiveData<Boolean> isLoggedOut() {
        loggedOut = settingsRepository.logoutLiveData
                (preferenceUtility.getMe().getAccessToken());
        return loggedOut;
    }

    public LiveData<Boolean> setLanguage(String language) {
        languageData = settingsRepository.languageLiveData(language, (preferenceUtility.getMe().getAccessToken()));
        return languageData;
    }
}
