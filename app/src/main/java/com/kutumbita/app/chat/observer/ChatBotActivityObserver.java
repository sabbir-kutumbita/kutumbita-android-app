package com.kutumbita.app.chat.observer;

import com.kutumbita.app.GlobalData;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import io.socket.client.Socket;

public class ChatBotActivityObserver implements LifecycleObserver {


    Socket socket;

    private static final String EMMIT_SURVEY_INIT = "survey:bot_activate";

    private static final String EMMIT_SURVEY_START = "survey:start";
    private static final String EMMIT_NEXT_ANSWER = "survey:next_answer";
    private static final String EMMIT_END_ANSWER = "survey:end_answer";

    private static final String RECEIVE_SURVEY_ACTIVE = "survey:bot_activate_response";
    private static final String RECEIVE_FIRST_QUESTION = "survey:first_question";
    private static final String RECEIVE_NEXT_QUESTION = "survey:next_question";
    private static final String RECEIVE_END_QUESTION = "survey:end_question";

    private static final String RECEIVE_SURVEY_DEACTIVE = "survey:bot_deactivate_response";

    public ChatBotActivityObserver() {

        socket = GlobalData.getInstance().getmSocket();

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    public void createEvent() {


    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void destroyEvent() {


    }

    private void socketSetup(boolean connect) {
//        if (connect) {
//            socket.connect();
//            socket.on(Socket.EVENT_CONNECT, ChatBotActivity.OnSocketConnected);
//            socket.on(RECEIVE_SURVEY_ACTIVE, OnSurveyInitiated);
//            socket.on(RECEIVE_SURVEY_DEACTIVE, OnSurveyInitiated);
//            socket.on(RECEIVE_FIRST_QUESTION, getQuestion);
//            socket.on(RECEIVE_NEXT_QUESTION, getQuestion);
//            socket.on(RECEIVE_END_QUESTION, getQuestion);
//
//        } else {
//            socket.off(Socket.EVENT_CONNECT, OnSocketConnected);
//            socket.off(RECEIVE_SURVEY_ACTIVE, OnSurveyInitiated);
//            socket.off(RECEIVE_SURVEY_DEACTIVE, OnSurveyInitiated);
//            socket.off(RECEIVE_FIRST_QUESTION, getQuestion);
//            socket.off(RECEIVE_NEXT_QUESTION, getQuestion);
//            socket.off(RECEIVE_END_QUESTION, getQuestion);
//            socket.disconnect();
//        }
    }
}
