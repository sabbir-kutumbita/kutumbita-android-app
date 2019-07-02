package com.kutumbita.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.kutumbita.app.model.Inbox;
import com.kutumbita.app.repository.InboxRepository;
import com.kutumbita.app.utility.PreferenceUtility;

import java.util.ArrayList;

public class InboxViewModel extends AndroidViewModel {

    LiveData<ArrayList<Inbox>> inboxLiveData;
    PreferenceUtility preferenceUtility;
    InboxRepository inboxRepository;

    public InboxViewModel(@NonNull Application application) {
        super(application);
        preferenceUtility = new PreferenceUtility(application.getApplicationContext());
        inboxRepository = InboxRepository.getInstance();
    }

    public LiveData<ArrayList<Inbox>> getInboxLiveData(int pageNumber, int itemInASinglePage) {
        inboxLiveData = inboxRepository.getInboxLiveData(pageNumber, itemInASinglePage,preferenceUtility.getMe().getAccessToken());
        return inboxLiveData;
    }
}
