package com.kutumbita.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kutumbita.app.model.Me;
import com.kutumbita.app.repository.AuthenticationRepository;
import com.kutumbita.app.utility.PreferenceUtility;

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


    public LiveData<Me> meLiveData(String accessToken, String refreshToken) {

        return authenticationRepository.getMeLiveData(accessToken, refreshToken);
    }

    public LiveData<JSONObject> otpCodeLiveData(String emailOrPhone) {

        return authenticationRepository.forgotPasswordCodeGenerator(emailOrPhone);
    }

    public LiveData<JSONObject> otpCodeVerifyLiveData(String emailOrPhone, String secretCode) {

        return authenticationRepository.forgotPasswordCodeVerifier(emailOrPhone, secretCode);
    }

    public LiveData<JSONObject> forgotPasswordSetNew(String password, String accessKey) {

        return authenticationRepository.forgotPasswordSetNew(password, accessKey);
    }
}
