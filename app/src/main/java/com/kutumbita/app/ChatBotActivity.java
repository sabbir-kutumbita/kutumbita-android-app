package com.kutumbita.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kutumbita.app.bot.chat.Dialog;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatBotActivity extends AppCompatActivity {


    Socket socket;
    private static final String RECEIVE_SMS = "server response";
    private static final String SEND_SMS = "app event";
    View layout;
    PreferenceUtility preferenceUtility;
    ArrayList<Dialog> dialogs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        preferenceUtility = new PreferenceUtility(this);
        layout = findViewById(R.id.header);
        ((TextView) layout.findViewById(R.id.tvTbTitle)).setText("Issue");
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
                        S.L("socket response", yourMsg);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketSetup(false);
    }
}
