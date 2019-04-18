package com.kutumbita.app.viewmodel;

import android.app.Application;

import com.kutumbita.app.chat.ChatBot;
import com.kutumbita.app.repository.ChatFragmentRepository;
import com.kutumbita.app.utility.PreferenceUtility;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class ChatFragmentViewModel extends AndroidViewModel {

    PreferenceUtility preferenceUtility;
    ChatFragmentRepository chatFragmentRepository;

    public ChatFragmentViewModel(@NonNull Application application) {
        super(application);
        preferenceUtility = new PreferenceUtility(application.getApplicationContext());
        chatFragmentRepository = ChatFragmentRepository.getInstance();

    }

    public LiveData<ArrayList<ChatBot>> getAvailableBot() {
        return chatFragmentRepository.chatLiveData(preferenceUtility.getMe().getAccessToken(), preferenceUtility.getMe().getLanguage());

    }
}
