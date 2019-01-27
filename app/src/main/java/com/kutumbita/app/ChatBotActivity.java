package com.kutumbita.app;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kutumbita.app.adapter.ChatAdapter;
import com.kutumbita.app.bot.chat.Dialog;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;

import org.json.JSONArray;
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
    LinearLayout linearLayout;
    RecyclerView rcv;
    EditText etAnswer;
    ChatAdapter adapter;
    ArrayList<String> answerArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        preferenceUtility = new PreferenceUtility(this);
        layout = findViewById(R.id.header);
        ((TextView) layout.findViewById(R.id.tvTbTitle)).setText("Issue");
        linearLayout = findViewById(R.id.ll);
        rcv = findViewById(R.id.rcv);
        etAnswer = findViewById(R.id.etMessage);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        rcv.setLayoutManager(manager);

        adapter = new ChatAdapter(this, dialogs);
        rcv.setAdapter(adapter);
        socket = GlobalData.getInstance().getmSocket();
        socketSetup(true);
    }


    private void loadChatMessage(Dialog d) {
        makeEditable(false);
        etAnswer.setText("");
        dialogs.add(d);
        adapter.notifyItemInserted(dialogs.size());
        rcv.scrollToPosition(dialogs.size());
        switch (d.getAnswerType().toLowerCase()) {


            case "none":
                linearLayout.removeAllViews();
                linearLayout.setVisibility(View.INVISIBLE);
                socketSetup(false);
                break;

            case "string":
                linearLayout.removeAllViews();
                linearLayout.setVisibility(View.INVISIBLE);
                makeEditable(true);
                break;

            case "radiogroup":

                RadioGroup rg = new RadioGroup(this);
                rg.setOrientation(RadioGroup.VERTICAL);

                for (int i = 0; i < d.getAnswers().size(); i++) {

                    RadioButton radioButton = new RadioButton(this);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            110);


                    radioButton.setLayoutParams(params);
                    radioButton.setId(i);

                    radioButton.setTextColor(getResources().getColor(R.color.primaryColor));
                    radioButton.setText(d.getAnswers().get(i));


                    radioButton.setTextSize(16);
                    Drawable dr = getResources().getDrawable(R.drawable.rectangle);
                    radioButton.setBackground(dr);

                    radioButton.setGravity(Gravity.CENTER);
                    radioButton.setButtonDrawable(new StateListDrawable());
                    rg.addView(radioButton);
                }


                linearLayout.addView(rg);

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                       // etAnswer.setText(((RadioButton) group.findViewById(checkedId)).getText().toString());
                        sendMessage(((RadioButton) group.findViewById(checkedId)).getText().toString());
                    }
                });

                linearLayout.setVisibility(View.VISIBLE);
                break;

            case "checkbox":

                for (int i = 0; i < d.getAnswers().size(); i++) {
                    final int pos = i;
                    final CheckBox checkBox = new CheckBox(this);


                    LinearLayout.LayoutParams CheckBoxParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            110);


                    checkBox.setLayoutParams(CheckBoxParams);
                    checkBox.setId(i);

                    checkBox.setTextColor(getResources().getColor(R.color.primaryColor));
                    checkBox.setText(d.getAnswers().get(i));


                    checkBox.setTextSize(16);
                    Drawable dr = getResources().getDrawable(R.drawable.rectangle);
                    checkBox.setBackground(dr);

                    checkBox.setGravity(Gravity.CENTER);
                    checkBox.setButtonDrawable(new StateListDrawable());


                    checkBox.setText(d.getAnswers().get(i));

                    linearLayout.addView(checkBox, CheckBoxParams);


                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                            StringBuilder builder = new StringBuilder();

                            if (isChecked) {

                                answerArray.add(checkBox.getText().toString());
                            } else {
                                answerArray.remove(checkBox.getText().toString());
                            }


                            for (int i = 0; i < answerArray.size(); i++) {


                                if (i > 0)
                                    builder.append(", ");
                                builder.append(answerArray.get(i));


                            }

                           // etAnswer.setText(builder.toString());
                            sendMessage(builder.toString());

                        }
                    });

                }


                linearLayout.setVisibility(View.VISIBLE);
                break;


        }

    }

    public void sendClick(View view) {

        sendMessage(etAnswer.getText().toString());

    }

    private void sendMessage(String msg) {

        Dialog tempDialog = new Dialog();
        tempDialog.setSender(Dialog.SENDER_USER);
        tempDialog.setQuestion(msg);
        tempDialog.setAnswerType(Dialog.SENDER_USER);
        dialogs.add(tempDialog);
        adapter.notifyItemInserted(dialogs.size());
        rcv.scrollToPosition(dialogs.size());

        if (socket.connected()) {

            JSONObject object = new JSONObject();
            try {

                object.put("message", msg);


            } catch (JSONException e) {

                e.printStackTrace();
            }

            socket.emit(SEND_SMS, object);


        }

        etAnswer.setText("");
    }


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

    Emitter.Listener receiveMessage = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    try {
                        S.L("socket receive", args[0].toString());
                        JSONObject obj = (JSONObject) args[0];

                        JSONObject dialogObject = obj.getJSONObject("dialog");
                        Dialog tempDialog = new Dialog();
                        tempDialog.setSender(Dialog.SENDER_BOT);
                        tempDialog.setQuestion(dialogObject.getString("question"));
                        tempDialog.setAnswerType(dialogObject.getString("answer_type"));
                        if (dialogObject.has("answers")) {
                            JSONArray answers = dialogObject.getJSONArray("answers");
                            ArrayList<String> answersArray = new ArrayList<>();
                            for (int i = 0; i < answers.length(); i++) {
                                answersArray.add(answers.getString(i));
                            }
                            tempDialog.setAnswers(answersArray);
                        }

                        loadChatMessage(tempDialog);
                    } catch (Exception e) {
                        e.printStackTrace();

                        Toast.makeText(getApplicationContext(), "Json exception", Toast.LENGTH_LONG).show();
                    }
                }
            });

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

    private void makeEditable(boolean b) {
        if (b) {

            etAnswer.setEnabled(true);
            etAnswer.setClickable(true);
            etAnswer.setFocusable(true);
            etAnswer.setFocusableInTouchMode(true);

        } else {

            linearLayout.setVisibility(View.VISIBLE);
            etAnswer.setEnabled(false);
            etAnswer.setClickable(false);
            etAnswer.setFocusable(false);
            etAnswer.setFocusableInTouchMode(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketSetup(false);
    }


}
