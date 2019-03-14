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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kutumbita.app.adapter.ChatAdapter;
import com.kutumbita.app.chat.Dialog;
import com.kutumbita.app.chat.Survey;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatBotActivity extends AppCompatActivity {

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


    View layout;
    PreferenceUtility preferenceUtility;
    ArrayList<Dialog> dialogs = new ArrayList<>();
    LinearLayout linearLayoutRg, linearLayoutEt;
    RecyclerView rcv;
    ArrayList<Survey> surveys;
    EditText etAnswer;
    ChatAdapter adapter;
    Survey tempSurvey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot);
        preferenceUtility = new PreferenceUtility(this);
        layout = findViewById(R.id.header);
        ((TextView) layout.findViewById(R.id.tvTbTitle)).setText("Survey");
        linearLayoutRg = findViewById(R.id.ll);
        linearLayoutEt = findViewById(R.id.ll2);
        rcv = findViewById(R.id.rcv);
        etAnswer = findViewById(R.id.etMessage);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        rcv.setLayoutManager(manager);

        surveys = new ArrayList<>();
        adapter = new ChatAdapter(this, dialogs);
        adapter.setOnReloadItemClickListener(new ChatAdapter.OnReloadItemClickListener() {
            @Override
            public void onReloadClick() {

                int termination = dialogs.size() < 4 ? dialogs.size() : 4;
                for (int i = 0; i < termination; i++) {
                    dialogs.remove(dialogs.size() - 1);
                }

                surveys.remove(surveys.size() - 1);

               // adapter.notifyDataSetChanged();
                tempSurvey = surveys.get(surveys.size() - 1);
                sendMessage();
            }
        });
        rcv.setAdapter(adapter);
        socket = GlobalData.getInstance().getmSocket();
        socketSetup(true);


    }

    Emitter.Listener OnSocketConnected = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {


            if (socket.connected()) {

                socket.emit(EMMIT_SURVEY_INIT);
            }

        }
    };

    Emitter.Listener OnSurveyInitiated = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        S.L("socket init", args[0].toString());
                        JSONObject obj = (JSONObject) args[0];
                        tempSurvey = new Survey();
                        tempSurvey.setQuestion(obj.getString("question"));
                        tempSurvey.setType(obj.getString("type"));
                        tempSurvey.setAnswer_type(obj.getString("answer_type"));
                        ArrayList<Survey.Answer> tempAnswers = new ArrayList<>();
                        if (obj.has("answers")) {

                            JSONArray answerArray = obj.getJSONArray("answers");
                            tempAnswers.clear();
                            for (int i = 0; i < answerArray.length(); i++) {
                                JSONObject answerObj = answerArray.getJSONObject(i);

                                tempAnswers.add(new Survey.Answer(answerObj.getString("title"),
                                        //we set event as score here
                                        answerObj.getString("event"),

                                        //we set surveyUUID as next here
                                        answerObj.has("surveyUUID") ? answerObj.getString("surveyUUID") : "0"));
                            }


                        }
                        tempSurvey.setAnswers(tempAnswers);
                        //  surveys.add(tempSurvey);
                        loadChatMessage();
                    } catch (Exception e) {

                        e.printStackTrace();

                        Toast.makeText(getApplicationContext(), "Json exception", Toast.LENGTH_LONG).show();
                    }


                }
            });

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
                        tempSurvey = new Survey();
                        tempSurvey.setId(obj.getString("id"));
                        tempSurvey.setEnd(obj.getBoolean("end"));

                        tempSurvey.setSurvey_uuid(obj.getString("survey_uuid"));
                        tempSurvey.setQuestion_no(obj.getString("question_no"));
                        tempSurvey.setQuestion(obj.getString("question"));
                        tempSurvey.setType(obj.getString("type"));
                        tempSurvey.setWeight(obj.has("weight") ? obj.getString("weight") : "0");
                        tempSurvey.setAnswer_type(obj.getString("answer_type"));
                        if (tempSurvey.isEnd()) {
                            Dialog tempDialog = new Dialog();
                            tempDialog.setSender(Dialog.SENDER_BOT);
                            tempDialog.setQuestion(tempSurvey.getQuestion());
                            tempDialog.setAnswerType(Dialog.SENDER_BOT);
                            refreshRecycleView(tempDialog);
                            // surveys.add(tempSurvey);


                            loadFinishRadioButton();
                            return;
                        }
                        ArrayList<Survey.Answer> tempAnswers = new ArrayList<>();
                        if (obj.has("answers")) {

                            JSONArray answerArray = obj.getJSONArray("answers");
                            tempAnswers.clear();
                            for (int i = 0; i < answerArray.length(); i++) {
                                JSONObject answerObj = answerArray.getJSONObject(i);
                                tempAnswers.add(new Survey.Answer(answerObj.getString("title"), answerObj.getString("score"), answerObj.getString("next")));
                            }

                        }

                        tempSurvey.setAnswers(tempAnswers);
                        // surveys.add(tempSurvey);
                        loadChatMessage();
                    } catch (Exception e) {

                        e.printStackTrace();

                        Toast.makeText(getApplicationContext(), "Json exception", Toast.LENGTH_LONG).show();
                    }


                }
            });

        }
    };

    private void refreshRecycleView(Dialog d) {

        dialogs.add(d);
        //adapter.notifyItemInserted(dialogs.size()-1);
        adapter.notifyDataSetChanged();
        rcv.scrollToPosition(dialogs.size() - 1);
    }


    private void loadChatMessage() {

        if (tempSurvey.getType().toLowerCase().contentEquals("bot")) {
            if (tempSurvey.getAnswer_type().toLowerCase().contentEquals("none")) {

                loadNoneAnswerTypeMessage();
                return;
            }
        }

        loadNormalMessage();

    }

    private void loadNoneAnswerTypeMessage() {

        // makeEditable(false);
        Dialog tempDialog = new Dialog(Dialog.SENDER_BOT, tempSurvey.getQuestion(), Dialog.SENDER_BOT, null);
        refreshRecycleView(tempDialog);


        loadFinishRadioButton();


    }


    private void loadNormalMessage() {


        final ArrayList<Survey.Answer> userAnswers = new ArrayList<>();


        Dialog d = new Dialog(Dialog.SENDER_BOT, tempSurvey.getQuestion(), tempSurvey.getAnswer_type(), tempSurvey.getAnswers());

        refreshRecycleView(d);
        linearLayoutRg.removeAllViews();

        switch (tempSurvey.getAnswer_type().toLowerCase()) {


//            case "none":
////                linearLayout.setVisibility(View.INVISIBLE);
////                socketSetup(false);
//                break;

            case "free_text":


                makeEditable(true);
                break;

            case "radio":

                makeEditable(false);
                userAnswers.clear();
                RadioGroup rg = new RadioGroup(this);
                rg.setOrientation(RadioGroup.VERTICAL);

                for (int i = 0; i < tempSurvey.getAnswers().size(); i++) {

                    RadioButton radioButton = new RadioButton(this);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            110);


                    radioButton.setLayoutParams(params);
                    radioButton.setId(i);

                    radioButton.setTag(tempSurvey.getAnswers().get(i));
                    radioButton.setTextColor(getResources().getColor(R.color.primaryColor));
                    radioButton.setText(tempSurvey.getAnswers().get(i).getTitle());


                    radioButton.setTextSize(16);
                    Drawable dr = getResources().getDrawable(R.drawable.rectangle);
                    radioButton.setBackground(dr);

                    radioButton.setGravity(Gravity.CENTER);

                    radioButton.setButtonDrawable(new StateListDrawable());
                    rg.addView(radioButton);
                }


                linearLayoutRg.addView(rg);

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {


                        userAnswers.add((Survey.Answer) ((RadioButton) group.findViewById(checkedId)).getTag());

                        tempSurvey.setUser_answer(userAnswers);
                        surveys.add(tempSurvey);
                        sendMessage();
                    }
                });


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


        //findViewById(R.id.rlMain).invalidate();

    }

    private void sendMessage() {

        //surveys.add(tempSurvey);
        Dialog tempDialog = new Dialog();
        tempDialog.setSender(Dialog.SENDER_USER);
        tempDialog.setQuestion(tempSurvey.getUser_answer().get(0).getTitle());
        tempDialog.setAnswerType(Dialog.SENDER_USER);
        refreshRecycleView(tempDialog);


        if (socket.connected()) {

            if (tempSurvey.getType().toLowerCase().contentEquals("bot")) {


                JSONObject object = new JSONObject();
                try {

                    object.put("surveyUUID", tempSurvey.getUser_answer().get(0).getNextOrSurveyId());
                } catch (JSONException e) {

                    e.printStackTrace();
                }
                if (tempSurvey.getUser_answer().get(0).getTitle().toLowerCase().contentEquals("yes"))
                    socket.emit(tempSurvey.getUser_answer().get(0).getScoreOrEvent(), object);
                else if (tempSurvey.getUser_answer().get(0).getTitle().toLowerCase().contentEquals("no"))
                    socket.emit(tempSurvey.getUser_answer().get(0).getScoreOrEvent());


            } else {

                JSONObject object = new JSONObject();
                try {

                    object.put("survey_uuid", tempSurvey.getSurvey_uuid());
                    object.put("id", tempSurvey.getId());
                    object.put("question_no", tempSurvey.getQuestion_no());
                    object.put("question", tempSurvey.getQuestion());
                    object.put("weight", tempSurvey.getWeight());
                    object.put("answer_type", tempSurvey.getAnswer_type());

                    JSONArray array = new JSONArray();

                    JSONObject answerObject = new JSONObject();
                    answerObject.put("title", tempSurvey.getUser_answer().get(0).getTitle());
                    answerObject.put("score", tempSurvey.getUser_answer().get(0).getScoreOrEvent());
                    answerObject.put("next", tempSurvey.getUser_answer().get(0).getNextOrSurveyId());

                    array.put(answerObject);
                    object.put("user_answer", array);


                } catch (JSONException e) {

                    e.printStackTrace();
                }

                if (tempSurvey.isEnd())
                    socket.emit(EMMIT_END_ANSWER, object);
                else
                    socket.emit(EMMIT_NEXT_ANSWER, object);

            }
        }
        etAnswer.setText("");

        //findViewById(R.id.rlMain).invalidate();

    }

    public void sendClick(View view) {


        if (etAnswer.getText().toString().isEmpty())
            return;
        tempSurvey.getAnswers().get(0).setTitle(etAnswer.getText().toString());
        tempSurvey.setUser_answer(tempSurvey.getAnswers());
        surveys.add(tempSurvey);
        sendMessage();
        Utility.hideKeyboard(ChatBotActivity.this);

    }


    private void socketSetup(boolean connect) {
        if (connect) {
            socket.connect();
            socket.on(Socket.EVENT_CONNECT, OnSocketConnected);
            socket.on(RECEIVE_SURVEY_ACTIVE, OnSurveyInitiated);
            socket.on(RECEIVE_SURVEY_DEACTIVE, OnSurveyInitiated);
            socket.on(RECEIVE_FIRST_QUESTION, getQuestion);
            socket.on(RECEIVE_NEXT_QUESTION, getQuestion);
            socket.on(RECEIVE_END_QUESTION, getQuestion);

        } else {
            socket.off(Socket.EVENT_CONNECT, OnSocketConnected);
            socket.off(RECEIVE_SURVEY_ACTIVE, OnSurveyInitiated);
            socket.off(RECEIVE_SURVEY_DEACTIVE, OnSurveyInitiated);
            socket.off(RECEIVE_FIRST_QUESTION, getQuestion);
            socket.off(RECEIVE_NEXT_QUESTION, getQuestion);
            socket.off(RECEIVE_END_QUESTION, getQuestion);
            socket.disconnect();
        }
    }

    private void makeEditable(boolean b) {
        if (b) {
            linearLayoutEt.setVisibility(View.VISIBLE);
            linearLayoutRg.setVisibility(View.GONE);


        } else {

            linearLayoutRg.setVisibility(View.VISIBLE);
            linearLayoutEt.setVisibility(View.GONE);

        }
    }

    private void loadFinishRadioButton() {

        linearLayoutRg.removeAllViews();
        RadioGroup rg = new RadioGroup(this);


        rg.setOrientation(RadioGroup.VERTICAL);


        RadioButton radioButton = new RadioButton(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                110);


        radioButton.setLayoutParams(params);
        radioButton.setId(0);


        radioButton.setTextColor(getResources().getColor(R.color.primaryColor));
        radioButton.setText("Finish");


        radioButton.setTextSize(16);
        Drawable dr = getResources().getDrawable(R.drawable.rectangle);
        radioButton.setBackground(dr);

        radioButton.setGravity(Gravity.CENTER);

        radioButton.setButtonDrawable(new StateListDrawable());
        rg.addView(radioButton);


        linearLayoutRg.addView(rg);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                finish();
            }
        });


        //linearLayoutRg.setVisibility(View.VISIBLE);

        makeEditable(false);
        // findViewById(R.id.rlMain).invalidate();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketSetup(false);
    }


}
