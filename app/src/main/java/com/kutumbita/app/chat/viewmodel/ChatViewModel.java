package com.kutumbita.app.chat.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class ChatViewModel extends AndroidViewModel {


    public ChatViewModel(@NonNull Application application) {
        super(application);
    }


    public class MyObserver implements LifecycleObserver {



        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        public void startMusic() {
//            if (lifecycle.getCurrentState().isAtLeast(Lifecycle.State.CREATED)) {
//
//
//
//
//            }

        }


        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        public void stopMusic() {



        }

    }
}
