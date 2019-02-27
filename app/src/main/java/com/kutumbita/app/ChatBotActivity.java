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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kutumbita.app.adapter.ChatAdapter;
import com.kutumbita.app.bot.chat.Dialog;
import com.kutumbita.app.bot.survey.Survey;
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
    private static final String SURVEY_START = "survey:start";
    private static final String NEXT_ANSWER = "survey:next_answer";
    private static final String END_ANSWER = "survey:end_answer";

    private static final String RECEIVE_FIRST_QUESTION = "survey:first_question";
    private static final String RECEIVE_NEXT_QUESTION = "survey:next_question";
    private static final String RECEIVE_END_QUESTION = "survey:end_question";
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


    private void loadChatMessage(final Survey s) {
        final ArrayList<Survey.Answer> userAnswers = new ArrayList<>();
        makeEditable(false);
        etAnswer.setText("");
        Dialog d = new Dialog(Dialog.SENDER_BOT, s.getQuestion(), s.getAnswer_type(), s.getAnswers());
        dialogs.add(d);
        adapter.notifyItemInserted(dialogs.size());
        rcv.scrollToPosition(dialogs.size());
        switch (s.getAnswer_type().toLowerCase()) {


            case "none":
                linearLayout.removeAllViews();
                linearLayout.setVisibility(View.INVISIBLE);
                socketSetup(false);
                break;

            case "free_text":
                linearLayout.removeAllViews();
                linearLayout.setVisibility(View.INVISIBLE);
                makeEditable(true);
                break;

            case "radio":
                linearLayout.removeAllViews();
                linearLayout.setVisibility(View.VISIBLE);
                userAnswers.clear();
                RadioGroup rg = new RadioGroup(this);
                rg.setOrientation(RadioGroup.VERTICAL);

                for (int i = 0; i < s.getAnswers().size(); i++) {

                    RadioButton radioButton = new RadioButton(this);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            110);


                    radioButton.setLayoutParams(params);
                    radioButton.setId(i);

                    radioButton.setTag(s.getAnswers().get(i));
                    radioButton.setTextColor(getResources().getColor(R.color.primaryColor));
                    radioButton.setText(s.getAnswers().get(i).getTitle());


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
                        userAnswers.add((Survey.Answer) ((RadioButton) group.findViewById(checkedId)).getTag());

                        s.setUser_answer(userAnswers);

                        sendMessage(s);
                    }
                });

                linearLayout.setVisibility(View.VISIBLE);
                break;

//            case "checkbox":
//
//                for (int i = 0; i < d.getAnswers().size(); i++) {
//                    final int pos = i;
//                    final CheckBox checkBox = new CheckBox(this);
//
//
//                    LinearLayout.LayoutParams CheckBoxParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                            110);
//
//                    checkBox.setLayoutParams(CheckBoxParams);
//                    checkBox.setId(i);
//
//                    checkBox.setTextColor(getResources().getColor(R.color.primaryColor));
//                    checkBox.setText(d.getAnswers().get(i));
//
//
//                    checkBox.setTextSize(16);
//                    Drawable dr = getResources().getDrawable(R.drawable.rectangle);
//                    checkBox.setBackground(dr);
//
//                    checkBox.setGravity(Gravity.CENTER);
//                    checkBox.setButtonDrawable(new StateListDrawable());
//
//
//                    checkBox.setText(d.getAnswers().get(i));
//
//                    linearLayout.addView(checkBox, CheckBoxParams);
//
//                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                        @Override
//                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//
//                            StringBuilder builder = new StringBuilder();
//
//                            if (isChecked) {
//
//                                answerArray.add(checkBox.getText().toString());
//                            } else {
//                                answerArray.remove(checkBox.getText().toString());
//                            }
//
//
//                            for (int i = 0; i < answerArray.size(); i++) {
//
//
//                                if (i > 0)
//                                    builder.append(", ");
//                                builder.append(answerArray.get(i));
//
//
//                            }
//
//                            // etAnswer.setText(builder.toString());
//                            sendMessage(builder.toString());
//
//                        }
//                    });
//
//                }
//
//
//                linearLayout.setVisibility(View.VISIBLE);
//                break;


        }

    }

    public void sendClick(View view) {

        // sendMessage(etAnswer.getText().toString());

    }

    private void sendMessage(Survey survey) {

        Dialog tempDialog = new Dialog();
        tempDialog.setSender(Dialog.SENDER_USER);
        tempDialog.setQuestion(survey.getUser_answer().get(0).getTitle());
        tempDialog.setAnswerType(Dialog.SENDER_USER);
        dialogs.add(tempDialog);
        adapter.notifyItemInserted(dialogs.size());
        rcv.scrollToPosition(dialogs.size());

        if (socket.connected()) {

            JSONObject object = new JSONObject();
            try {

                object.put("survey_uuid", survey.getSurvey_uuid());
                object.put("id", survey.getId());
                object.put("question_no", survey.getQuestion_no());
                object.put("question", survey.getQuestion());
                object.put("weight", survey.getWeight());
                object.put("answer_type", survey.getAnswer_type());

                JSONArray array = new JSONArray();

                JSONObject answerObject = new JSONObject();
                answerObject.put("title", survey.getUser_answer().get(0).getTitle());
                answerObject.put("score", survey.getUser_answer().get(0).getScore());
                answerObject.put("next", survey.getUser_answer().get(0).getNext());

                array.put(answerObject);
                object.put("user_answer", array);


            } catch (JSONException e) {

                e.printStackTrace();
            }

            if (survey.isEnd())
                socket.emit(END_ANSWER, object);
            else
                socket.emit(NEXT_ANSWER, object);

        }

        etAnswer.setText("");
    }


    Emitter.Listener OnSocketConnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {


            if (socket.connected()) {

                S.L("socket", "connected");
                JSONObject object = new JSONObject();

                try {

                    object.put("surveyUUID", "5c62904b1bab0a5535e4c44d");
                    //object.put("user_id", "Sabbir");

                } catch (JSONException e) {


                    e.printStackTrace();
                }

                socket.emit(SURVEY_START, object);
            }

        }
    };

    Emitter.Listener getQuestion = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    try {
                        S.L("socket receive", args[0].toString());


                        JSONObject obj = (JSONObject) args[0];
                        Survey tempSurvey = new Survey();
                        tempSurvey.setId(obj.getString("id"));
                        tempSurvey.setSurvey_uuid(obj.getString("survey_uuid"));
                        tempSurvey.setQuestion_no(obj.getString("question_no"));
                        tempSurvey.setQuestion(obj.getString("question"));
                        tempSurvey.setWeight(obj.getString("weight"));
                        tempSurvey.setAnswer_type(obj.getString("answer_type"));
                        ArrayList<Survey.Answer> tempAnswers = new ArrayList<>();
                        if (obj.has("answers")) {

                            JSONArray answerArray = obj.getJSONArray("answers");
                            tempAnswers.clear();
                            for (int i = 0; i < answerArray.length(); i++) {
                                JSONObject answerObj = answerArray.getJSONObject(i);
                                tempAnswers.add(new Survey.Answer(answerObj.getString("title"), answerObj.getString("score"), answerObj.getString("next")));
                            }


                        }
                        tempSurvey.setEnd(obj.getBoolean("end"));
                        tempSurvey.setAnswers(tempAnswers);
                        loadChatMessage(tempSurvey);
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
            socket.connect();
            socket.on(Socket.EVENT_CONNECT, OnSocketConnected);
            socket.on(RECEIVE_FIRST_QUESTION, getQuestion);
            socket.on(RECEIVE_NEXT_QUESTION, getQuestion);
            socket.on(RECEIVE_END_QUESTION, getQuestion);

        } else {
            socket.off(Socket.EVENT_CONNECT, OnSocketConnected);
            socket.off(RECEIVE_FIRST_QUESTION, getQuestion);
            socket.off(RECEIVE_NEXT_QUESTION, getQuestion);
            socket.off(RECEIVE_END_QUESTION, getQuestion);
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


    public void connect(View view) {
        if (socket.connected()) {

//                            JSONObject object = new JSONObject();
//                            try {
//
//                                object.put("message", msg);
//
//
//                            } catch (JSONException e) {
//
//                                e.printStackTrace();
//                            }


            String obj = "{\n" +

                    "  survey_uuid: \"5c62904b1bab0a5535e4c44d\",\n" +
                    "  uuid: \"961aaf94-8373-402d-b0e4-18a476dff7e4\",\n" +
                    "  question_no: \"1\",\n" +
                    "  question: \"how is your company?\",\n" +
                    "  weight: 1,\n" +
                    "  answer_type: \"radio\",\n" +
                    "  user_answer: [\n" +
                    "   {\n" +
                    "    title: \"good\",\n" +
                    "    score: 1,\n" +
                    "    next: \"18c51035-a4ec-443b-a166-0c25052a8444\"\n" +
                    "   }\n" +
                    "  ]\n" +
                    "}";
            JSONObject object = null;
            try {
                object = new JSONObject(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socket.emit(NEXT_ANSWER, object);


        }
    }
}
