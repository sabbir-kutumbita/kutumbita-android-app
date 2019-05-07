package com.kutumbita.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kutumbita.app.repository.AuthenticationRepository;

import org.json.JSONObject;

public class AuthenticationViewModel extends AndroidViewModel {

    AuthenticationRepository authenticationRepository;

    public AuthenticationViewModel(@NonNull Application application) {
        super(application);
        authenticationRepository = AuthenticationRepository.getInstance();
    }


    public LiveData<JSONObject> signInLiveData(String email, String password) {

        return authenticationRepository.signInLiveData(email, password);
    }
}
