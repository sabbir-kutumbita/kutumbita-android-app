package com.kutumbita.app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatBotActivity extends AppCompatActivity {


    Socket socket;
    private static final String RECEIVE_SMS = "server response";
    private static final String SEND_SMS = "app event";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        socket = GlobalData.getInstance().getmSocket();
        socketSetup(true);
    }


    Emitter.Listener receiveMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject obj = (JSONObject) args[0];

                        String yourMsg = obj.toString();

                        Toast.makeText(getApplicationContext(), yourMsg, Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Json exception", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    };


    Emitter.Listener OnSocketConnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {


            if (socket.connected()) {


                JSONObject object = new JSONObject();
                try {

                    object.put("data", "Hello, I am here");
                    object.put("user_id", "Sabbir");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                socket.emit(SEND_SMS, object);
            }


        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketSetup(false);
    }

    private void socketSetup(boolean connect) {
        if (connect) {


            socket.on(Socket.EVENT_CONNECT, OnSocketConnected);
            socket.on(RECEIVE_SMS, receiveMessage);
            socket = socket.connect();
        } else {
            socket.off(Socket.EVENT_CONNECT, OnSocketConnected);
            socket.off(RECEIVE_SMS, receiveMessage);
            socket.disconnect();

        }
    }
}
