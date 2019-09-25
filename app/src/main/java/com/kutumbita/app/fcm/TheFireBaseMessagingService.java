package com.kutumbita.app.fcm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kutumbita.app.InboxDetailsActivity;
import com.kutumbita.app.R;
import com.kutumbita.app.SplashActivity;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;

import androidx.core.app.NotificationCompat;

public class TheFireBaseMessagingService extends FirebaseMessagingService {

    PreferenceUtility preferenceUtility;


    @Override
    public void onNewToken(String s) {
        preferenceUtility = new PreferenceUtility(this);
        S.L("token", s);
        preferenceUtility.setFcmToken(s);


    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        S.L("push", remoteMessage.toString());

        preferenceUtility = new PreferenceUtility(this);
        if (preferenceUtility.getMe() != null)


            sendNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), remoteMessage.getData().get("exUuid"));

    }

    public void sendNotification(String title, String message, String uuId) {

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);


        //If on Oreo then notification required a notification channel.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        Intent goDetails;
        if (uuId != null) {
            goDetails = new Intent(this, InboxDetailsActivity.class);
            goDetails.putExtra(Constant.EXTRA_UUID, uuId);
        } else
            goDetails = new Intent(this, SplashActivity.class);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, goDetails, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setContentTitle(title).setContentIntent(pendingIntent).setAutoCancel(true)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_home);

        notificationManager.notify(1, notification.build());
        Intent intent = new Intent(Constant.ACTION_BROADCAST);
        sendBroadcast(intent);
    }
}
