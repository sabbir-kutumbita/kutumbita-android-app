package com.kutumbita.app;

import android.app.Application;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.UrlConstant;
import com.kutumbita.app.utility.Utility;

import net.gotev.uploadservice.UploadService;

import java.net.URISyntaxException;


import io.socket.client.IO;
import io.socket.client.Socket;

public class GlobalData extends Application {

    public static final String TAG = GlobalData.class
            .getSimpleName();

    private RequestQueue mRequestQueue;

    PreferenceUtility preferenceUtility;
    private static GlobalData mInstance;
    private Socket mSocket;
    private int orientation;
    private long touchTime;

    @Override
    public void onCreate() {
        super.onCreate();
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        preferenceUtility = new PreferenceUtility(this);
        if (preferenceUtility.getMe() != null) {

            initializeSocket();

        }
        mInstance = this;
        orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        touchTime = System.currentTimeMillis();
    }

    public long getTouchTime() {
        return touchTime;
    }

    public void setTouchTime(long touchTime) {
        this.touchTime = touchTime;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void initializeSocket() {

        try {
            mSocket = IO.socket(UrlConstant.URL_SOCKET + preferenceUtility.getMe().getAccessToken());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket getmSocket() {
        return mSocket;
    }

    public void setmSocket(Socket mSocket) {
        this.mSocket = mSocket;
    }

    public static synchronized GlobalData getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


}
