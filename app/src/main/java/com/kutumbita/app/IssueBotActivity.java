package com.kutumbita.app;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kutumbita.app.adapter.ChatAdapter;
import com.kutumbita.app.chat.Dialog;
import com.kutumbita.app.chat.Issue;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.socket.emitter.Emitter;

public class IssueBotActivity extends AppCompatActivity {


    private static String EMMIT_BOT_ACTIVATE = ":bot_activate";
    private static String TYPE = "";
    private static String EMMIT_NEXT_ANSWER = ":next_answer";
    private static String EMMIT_END_ANSWER = ":end_answer";


    private static String RECEIVE_BOT_ACTIVE = ":bot_activate_response";
    private static String RECEIVE_FIRST_QUESTION = ":first_question";
    private static String RECEIVE_NEXT_QUESTION = ":next_question";
    private static String RECEIVE_END_QUESTION = ":end_question";
    private static String RECEIVE_START_CONFIRMATION = ":start_confirmation";
    private static String RECEIVE_FEEDBACK_QUESTION = ":feedback_question";
    private static String RECEIVE_CATEGORY_ANSWER = ":category_answer";
    private static String RECEIVE_BOT_DEACTIVE = ":bot_deactivate_response";


    View layout;
    PreferenceUtility preferenceUtility;

    ArrayList<Dialog> dialogs = new ArrayList<>();

    LinearLayout linearLayoutRg, linearLayoutEt;
    RecyclerView rcv;
    ArrayList<Issue> issues;
    EditText etAnswer;
    ChatAdapter adapter;
    Issue tempIssue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        Utility.setOrientation(this, GlobalData.getInstance().getOrientation());
        setContentView(R.layout.activity_chat_bot);
        // getLifecycle().addObserver(new ChatBotActivityObserver());
        TYPE = getIntent().getStringExtra(Constant.EXTRA_EVENT);
        preferenceUtility = new PreferenceUtility(this);
        GlobalData.getInstance().setTouchTime(System.currentTimeMillis());
        //
        layout = findViewById(R.id.header);
        ((TextView) layout.findViewById(R.id.tvTbTitle)).setText(TYPE.substring(0, 1).toUpperCase() + TYPE.substring(1));
        linearLayoutRg = findViewById(R.id.ll);


        linearLayoutEt = findViewById(R.id.ll2);
        rcv = findViewById(R.id.rcv);
        etAnswer = findViewById(R.id.etMessage);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        rcv.setLayoutManager(manager);

        issues = new ArrayList<>();
        adapter = new ChatAdapter(this, dialogs);
        adapter.liveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {



                if (aBoolean) {

                    int termination = dialogs.size() < 4 ? dialogs.size() : 4;
                    for (int i = 0; i < termination; i++) {
                        dialogs.remove(dialogs.size() - 1);
                    }

                    issues.remove(issues.size() - 1);

                    // adapter.notifyDataSetChanged();
                    tempIssue = issues.get(issues.size() - 1);
                    sendMessage();

                }
            }
        });

        rcv.setAdapter(adapter);
        if (GlobalData.getInstance().getmSocket().connected())
            socketSetup(true);


    }



    Emitter.Listener OnBotActivated = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        S.L("socket init", args[0].toString());
                        JSONObject obj = (JSONObject) args[0];
                        tempIssue = new Issue();
                        tempIssue.setQuestion(obj.getString("question"));
                        tempIssue.setType(obj.getString("type"));
                        tempIssue.setAnswer_type(obj.getString("answer_type"));
                        tempIssue.setWeightOrWorkerId(obj.has("workerIssueUUID") ? obj.getString("workerIssueUUID") : "");
                        ArrayList<Issue.Answer> tempAnswers = new ArrayList<>();
                        if (obj.has("answers")) {

                            JSONArray answerArray = obj.getJSONArray("answers");

                            tempAnswers.clear();
                            for (int i = 0; i < answerArray.length(); i++) {


                                JSONObject answerObj = answerArray.getJSONObject(i);

                                tempAnswers.add(new Issue.Answer(answerObj.getString("title"), answerObj.has("slug") ? answerObj.getString("slug") : "",
                                        //we set event as score here
                                        answerObj.getString("event"),

                                        //we set surveyUUID as next here
                                        answerObj.has("surveyUUID") ? answerObj.getString("surveyUUID") : "0"));
                            }


                        }
                        tempIssue.setAnswers(tempAnswers);
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

    Emitter.Listener getIssueQuestion = new Emitter.Listener() {


        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    try {
                        S.L("socket receive", args[0].toString());


                        JSONObject obj = (JSONObject) args[0];
                        tempIssue = new Issue();
                        tempIssue.setId(obj.getString("id"));
                        tempIssue.setEnd(obj.getBoolean("end"));

                        tempIssue.setSurvey_uuid(obj.getString("issue_uuid"));
                        tempIssue.setQuestion_no(obj.getString("question_no"));
                        tempIssue.setQuestion(obj.getString("question"));
                        tempIssue.setType(obj.getString("type"));
                        tempIssue.setWeightOrWorkerId(obj.has("weight") ? obj.getString("weight") : "0");
                        tempIssue.setAnswer_type(obj.getString("answer_type"));
                        if (tempIssue.isEnd()) {
                            Dialog tempDialog = new Dialog();
                            tempDialog.setSender(Dialog.SENDER_BOT);
                            tempDialog.setQuestion(tempIssue.getQuestion());
                            tempDialog.setAnswerType(Dialog.SENDER_BOT);
                            tempDialog.setEnd(tempIssue.isEnd());
                            refreshRecycleView(tempDialog);
                            // surveys.add(tempSurvey);
                            loadFinishRadioButton();
                            return;
                        }
                        ArrayList<Issue.Answer> tempAnswers = new ArrayList<>();
                        if (obj.has("answers")) {


                            JSONArray answerArray = obj.getJSONArray("answers");
                            tempAnswers.clear();
                            for (int i = 0; i < answerArray.length(); i++) {
                                JSONObject answerObj = answerArray.getJSONObject(i);
                                tempAnswers.add(new Issue.Answer(answerObj.getString("title"), answerObj.has("slug") ? answerObj.getString("slug") : "", answerObj.has("score") ? answerObj.getString("score") : "", answerObj.getString("next")));
                            }

                        }

                        tempIssue.setAnswers(tempAnswers);
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


    Emitter.Listener OnStartConfirmation = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        S.L("socket init", args[0].toString());
                        JSONObject obj = (JSONObject) args[0];
                        tempIssue = new Issue();
                        tempIssue.setQuestion(obj.getString("question"));
                        tempIssue.setType(obj.getString("type"));
                        tempIssue.setAnswer_type(obj.getString("answer_type"));
                        ArrayList<Issue.Answer> tempAnswers = new ArrayList<>();
                        if (obj.has("answers")) {

                            JSONArray answerArray = obj.getJSONArray("answers");
                            tempAnswers.clear();
                            for (int i = 0; i < answerArray.length(); i++) {


                                JSONObject answerObj = answerArray.getJSONObject(i);

                                tempAnswers.add(new Issue.Answer(answerObj.getString("title"), answerObj.has("slug") ? answerObj.getString("slug") : "",
                                        //we set event as score here
                                        answerObj.getString("event"),

                                        //we set surveyUUID as next here
                                        answerObj.has("surveyUUID") ? answerObj.getString("surveyUUID") : "0"));
                            }


                        }
                        tempIssue.setAnswers(tempAnswers);
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

    private void refreshRecycleView(Dialog d) {

        dialogs.add(d);
        //adapter.notifyItemInserted(dialogs.size()-1);
        adapter.notifyDataSetChanged();
        rcv.scrollToPosition(dialogs.size() - 1);
    }


    private void loadChatMessage() {

        if (tempIssue.getType().toLowerCase().contentEquals("bot")) {
            if (tempIssue.getAnswer_type().toLowerCase().contentEquals("none")) {

                loadNoneAnswerTypeMessage();
                return;
            }
        }

        loadNormalMessage();

    }

    private void loadNoneAnswerTypeMessage() {


        // makeEditable(false);
        Dialog tempDialog = new Dialog(Dialog.SENDER_BOT, tempIssue.getQuestion(), Dialog.SENDER_BOT, null);
        refreshRecycleView(tempDialog);


        loadFinishRadioButton();


    }


    private void loadNormalMessage() {


        final ArrayList<Issue.Answer> userAnswers = new ArrayList<>();


        Dialog d = new Dialog(Dialog.SENDER_BOT, tempIssue.getQuestion(), tempIssue.getAnswer_type(), tempIssue.getAnswers());

        refreshRecycleView(d);
        linearLayoutRg.removeAllViews();

        switch (tempIssue.getAnswer_type().toLowerCase()) {


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

                for (int i = 0; i < tempIssue.getAnswers().size(); i++) {

                    RadioButton radioButton = new RadioButton(this);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            110);

                    if (GlobalData.getInstance().getOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
                        params.height = 110;
                    else
                        params.height = 80;
                    radioButton.setLayoutParams(params);
                    radioButton.setId(i);

                    radioButton.setTag(tempIssue.getAnswers().get(i));
                    radioButton.setTextColor(getResources().getColor(R.color.primaryColor));
                    radioButton.setText(tempIssue.getAnswers().get(i).getTitle());

                    if (GlobalData.getInstance().getOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
                        radioButton.setTextSize(16);
                    else
                        radioButton.setTextSize(20);
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


                        userAnswers.add((Issue.Answer) ((RadioButton) group.findViewById(checkedId)).getTag());

                        tempIssue.setUser_answer(userAnswers);

                        issues.add(tempIssue);

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
        tempDialog.setQuestion(tempIssue.getUser_answer().get(0).getTitle());
        tempDialog.setAnswerType(Dialog.SENDER_USER);
        refreshRecycleView(tempDialog);


        if (GlobalData.getInstance().getmSocket().connected()) {

            if (tempIssue.getType().toLowerCase().contentEquals("bot")) {


                JSONObject object = new JSONObject();
                try {
                    JSONArray array = new JSONArray();
                    JSONObject answerObject = new JSONObject();
                    answerObject.put("title", tempIssue.getUser_answer().get(0).getTitle());
                    answerObject.put("slug", tempIssue.getUser_answer().get(0).getSlug());
                    answerObject.put("event", tempIssue.getUser_answer().get(0).getScoreOrEvent());
                    answerObject.put("workerIssueUUID", tempIssue.getWeightOrWorkerId());

                    array.put(answerObject);
                    object.put("user_answer", array);
                } catch (JSONException e) {


                    e.printStackTrace();
                }
                // if (tempSurvey.getUser_answer().get(0).getTitle().toLowerCase().contentEquals("yes"))
                GlobalData.getInstance().getmSocket().emit(tempIssue.getUser_answer().get(0).getScoreOrEvent(), object);
                // else if (tempSurvey.getUser_answer().get(0).getTitle().toLowerCase().contentEquals("no"))
                //  socket.emit(tempSurvey.getUser_answer().get(0).getScoreOrEvent());


            } else {

                JSONObject object = new JSONObject();
                try {

                    object.put("issue_uuid", tempIssue.getSurvey_uuid());
                    object.put("id", tempIssue.getId());
                    object.put("question_no", tempIssue.getQuestion_no());
                    object.put("question", tempIssue.getQuestion());
                    object.put("weight", tempIssue.getWeightOrWorkerId());
                    object.put("answer_type", tempIssue.getAnswer_type());

                    JSONArray array = new JSONArray();

                    JSONObject answerObject = new JSONObject();
                    answerObject.put("title", tempIssue.getUser_answer().get(0).getTitle());
                    answerObject.put("score", tempIssue.getUser_answer().get(0).getScoreOrEvent());
                    answerObject.put("next", tempIssue.getUser_answer().get(0).getNextOrSurveyId());

                    array.put(answerObject);
                    object.put("user_answer", array);


                } catch (JSONException e) {

                    e.printStackTrace();
                }

                if (tempIssue.isEnd())

                    GlobalData.getInstance().getmSocket().emit(TYPE + EMMIT_END_ANSWER, object);

                else
                    GlobalData.getInstance().getmSocket().emit(TYPE + EMMIT_NEXT_ANSWER, object);


            }
        }
        etAnswer.setText("");

        //findViewById(R.id.rlMain).invalidate();

    }

    public void sendClick(View view) {


        if (etAnswer.getText().toString().isEmpty())
            return;
        tempIssue.getAnswers().get(0).setTitle(etAnswer.getText().toString());
        tempIssue.setUser_answer(tempIssue.getAnswers());
        issues.add(tempIssue);
        sendMessage();
        Utility.hideKeyboard(IssueBotActivity.this);

    }


    private void socketSetup(boolean connect) {
        if (connect) {


            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_BOT_ACTIVE, OnBotActivated);
            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_BOT_DEACTIVE, OnBotActivated);
            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_FIRST_QUESTION, getIssueQuestion);
            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_NEXT_QUESTION, getIssueQuestion);
            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_END_QUESTION, getIssueQuestion);

            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_START_CONFIRMATION, OnStartConfirmation);
            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_FEEDBACK_QUESTION, OnBotActivated);
            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_CATEGORY_ANSWER, OnStartConfirmation);

            if (GlobalData.getInstance().getmSocket().connected()) {

                GlobalData.getInstance().getmSocket().emit(TYPE + EMMIT_BOT_ACTIVATE);
            }

        } else {

            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_BOT_ACTIVE, OnBotActivated);
            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_BOT_DEACTIVE, OnBotActivated);
            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_FIRST_QUESTION, getIssueQuestion);
            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_NEXT_QUESTION, getIssueQuestion);
            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_END_QUESTION, getIssueQuestion);

            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_START_CONFIRMATION, OnStartConfirmation);
            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_FEEDBACK_QUESTION, OnBotActivated);
            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_CATEGORY_ANSWER, OnStartConfirmation);


        }
    }

    private void makeEditable(boolean b) {


        if (b) {


            linearLayoutEt.setVisibility(View.VISIBLE);
            linearLayoutRg.setVisibility(View.GONE);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rcv.getLayoutParams();

            layoutParams.addRule(RelativeLayout.ABOVE, R.id.ll2);

            rcv.setLayoutParams(layoutParams);


        } else {


            linearLayoutRg.setVisibility(View.VISIBLE);
            linearLayoutEt.setVisibility(View.GONE);


            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rcv.getLayoutParams();

            layoutParams.addRule(RelativeLayout.ABOVE, R.id.ll);

            rcv.setLayoutParams(layoutParams);

        }


    }

    private void loadFinishRadioButton() {

        linearLayoutRg.removeAllViews();
        RadioGroup rg = new RadioGroup(this);
        rg.setOrientation(RadioGroup.VERTICAL);
        RadioButton radioButton = new RadioButton(this);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                110);
        if (GlobalData.getInstance().getOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
            params.height = 110;
        else
            params.height = 80;
        radioButton.setLayoutParams(params);
        radioButton.setId(0);
        radioButton.setTextColor(getResources().getColor(R.color.primaryColor));
        radioButton.setText("Finish");
        if (GlobalData.getInstance().getOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
            radioButton.setTextSize(16);
        else
            radioButton.setTextSize(20);
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


        makeEditable(false);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketSetup(false);
    }


}
