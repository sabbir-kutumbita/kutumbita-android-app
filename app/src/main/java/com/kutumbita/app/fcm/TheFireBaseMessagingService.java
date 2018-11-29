package com.kutumbita.app.fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;

public class TheFireBaseMessagingService extends FirebaseMessagingService {

    PreferenceUtility preferenceUtility;



    @Override
    public void onNewToken(String s) {
        preferenceUtility= new PreferenceUtility(this);
        S.L("token", s);
        preferenceUtility.setFcmToken(s);

    }
}
