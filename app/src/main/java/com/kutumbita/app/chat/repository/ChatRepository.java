package com.kutumbita.app.chat.repository;

import com.kutumbita.app.GlobalData;


import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatRepository {

    private static ChatRepository chatRepository;

    private static Socket socket;

    private ChatRepository() {



    }

    public synchronized static ChatRepository getInstance() {


        if (chatRepository == null) {
            socket = GlobalData.getInstance().getmSocket();
            chatRepository = new ChatRepository();
        }

        return chatRepository;


    }

    public Emitter.Listener OnSocketConnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {


            if (socket.connected()) {

               // socket.emit(TYPE + EMMIT_SURVEY_INIT);
            }

        }
    };

}
