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

import com.kutumbita.app.adapter.SurveyDialogAdapter;
import com.kutumbita.app.chat.Dialog;
import com.kutumbita.app.chat.Survey;
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

public class SurveyBotActivity extends AppCompatActivity {

    //Socket socket;


    private static String EMMIT_SURVEY_ACTIVATE = ":bot_activate";

    private static String TYPE = "";

    private static String EMMIT_SURVEY_START = ":start";
    private static String EMMIT_NEXT_ANSWER = ":next_answer";
    private static String EMMIT_END_ANSWER = ":end_answer";

    private static String RECEIVE_SURVEY_ACTIVE = ":bot_activate_response";
    private static String RECEIVE_FIRST_QUESTION = ":first_question";
    private static String RECEIVE_NEXT_QUESTION = ":next_question";
    private static String RECEIVE_END_QUESTION = ":end_question";

    private static String RECEIVE_SURVEY_DEACTIVE = ":bot_deactivate_response";


    View layout;
    PreferenceUtility preferenceUtility;

    ArrayList<Dialog> dialogs = new ArrayList<>();

    LinearLayout linearLayoutRg, linearLayoutEt;
    RecyclerView rcv;
    ArrayList<Survey> surveys;

   // ArrayList<JSONObject> surveyObjects;
    EditText etAnswer;
    SurveyDialogAdapter adapter;
   Survey tempSurvey;

    JSONObject tempObject;
    JSONArray tempAnswerArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        Utility.setOrientation(this, GlobalData.getInstance().getOrientation());
        setContentView(R.layout.activity_survey_bot);
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

        surveys = new ArrayList<>();
       // surveyObjects= new ArrayList<>();
        adapter = new SurveyDialogAdapter(this, dialogs);
        adapter.liveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {

                    int termination = dialogs.size() < 4 ? dialogs.size() : 4;
                    for (int i = 0; i < termination; i++) {
                        dialogs.remove(dialogs.size() - 1);
                    }

                 //   surveys.remove(surveys.size() - 1);



                    // adapter.notifyDataSetChanged();
                  //  tempSurvey = surveys.get(surveys.size() - 1);
                    sendMessage(0);

                }
            }
        });

        rcv.setAdapter(adapter);
        if (GlobalData.getInstance().getmSocket().connected())
            socketSetup(true);


    }

    Emitter.Listener OnSurveyInitiated = new Emitter.Listener() {


        @Override
        public void call(final Object... args) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        S.L("OnSurveyInitiated json", args[0].toString());
                        tempObject = (JSONObject) args[0];
                        tempSurvey = new Survey();
                        tempSurvey.setQuestion(tempObject.getString("question"));
                        tempSurvey.setType(tempObject.getString("type"));
                        tempSurvey.setAnswer_type(tempObject.getString("answer_type"));
                        ArrayList<Survey.Answer> tempAnswers = new ArrayList<>();
                        if (tempObject.has("answers")) {

                            tempAnswerArray = tempObject.getJSONArray("answers");
                            tempAnswers.clear();
                            for (int i = 0; i < tempAnswerArray.length(); i++) {


                                JSONObject answerObj = tempAnswerArray.getJSONObject(i);

                                tempAnswers.add(new Survey.Answer(answerObj.getString("title"),
                                        //we set event as score here
                                        answerObj.getString("event"),

                                        //we set surveyUUID as next here
                                        answerObj.has("surveyUUID") ? answerObj.getString("surveyUUID") : "0"));
                            }


                        }
                        tempSurvey.setAnswers(tempAnswers);
                        loadChatMessage(tempObject.getString("type"), tempObject.getString("answer_type"));
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
                        S.L("getQuestion json", args[0].toString());



                        tempObject = (JSONObject) args[0];
                        tempSurvey = new Survey();
                        tempSurvey.setId(tempObject.getString("id"));
                        tempSurvey.setEnd(tempObject.getBoolean("end"));

                        tempSurvey.setSurvey_uuid(tempObject.getString("survey_uuid"));
                        tempSurvey.setQuestion_no(tempObject.getString("question_no"));
                        tempSurvey.setQuestion(tempObject.getString("question"));
                        tempSurvey.setType(tempObject.getString("type"));
                        tempSurvey.setWeight(tempObject.has("weight") ? tempObject.getString("weight") : "0");
                        tempSurvey.setAnswer_type(tempObject.getString("answer_type"));
                        if (tempSurvey.isEnd()) {
                            Dialog tempDialog = new Dialog();
                            tempDialog.setSender(Dialog.SENDER_BOT);
                            tempDialog.setQuestion(tempSurvey.getQuestion());
                            //tempDialog.setAnswerType(Dialog.SENDER_BOT);
                            tempDialog.setEnd(tempSurvey.isEnd());
                            refreshRecycleView(tempDialog);
                            loadFinishRadioButton();
                            return;
                        }


                        ArrayList<Survey.Answer> tempAnswers = new ArrayList<>();
                        if (tempObject.has("answers")) {


                            tempAnswerArray = tempObject.getJSONArray("answers");
                            tempAnswers.clear();
                            for (int i = 0; i < tempAnswerArray.length(); i++) {
                                JSONObject answerObj = tempAnswerArray.getJSONObject(i);
                                tempAnswers.add(new Survey.Answer(answerObj.getString("title"), answerObj.getString("score"), answerObj.getString("next")));
                            }

                        }

                        tempSurvey.setAnswers(tempAnswers);
                        loadChatMessage(tempObject.getString("type"), tempObject.getString("answer_type"));
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


    private void loadChatMessage(String type, String answerType) {

        if (type.toLowerCase().contentEquals("bot")) {


            if (answerType.toLowerCase().contentEquals("none")) {

                loadNoneAnswerTypeMessage();
                return;
            }
        }

        loadNormalMessage();

    }

    private void loadNoneAnswerTypeMessage() {

        // makeEditable(false);
        Dialog tempDialog = new Dialog(Dialog.SENDER_BOT, tempSurvey.getQuestion(), Dialog.SENDER_BOT);
        refreshRecycleView(tempDialog);


        loadFinishRadioButton();


    }


    private void loadNormalMessage() {


        final ArrayList<Survey.Answer> userAnswers = new ArrayList<>();


        Dialog d = new Dialog(Dialog.SENDER_BOT, tempSurvey.getQuestion(), tempSurvey.getAnswer_type());

        refreshRecycleView(d);
        linearLayoutRg.removeAllViews();

        switch (tempSurvey.getAnswer_type().toLowerCase()) {



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

                    if (GlobalData.getInstance().getOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
                        params.height = 110;
                    else
                        params.height = 80;
                    radioButton.setLayoutParams(params);
                    radioButton.setId(i);

                    radioButton.setTag(tempSurvey.getAnswers().get(i));
                    radioButton.setTextColor(getResources().getColor(R.color.primaryColor));
                    radioButton.setText(tempSurvey.getAnswers().get(i).getTitle());

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


                        userAnswers.add((Survey.Answer) ((RadioButton) group.findViewById(checkedId)).getTag());

                        tempSurvey.setUser_answer(userAnswers);
                        surveys.add(tempSurvey);
                        sendMessage(checkedId);
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

    private void sendMessage(int index) {


        Dialog tempDialog = new Dialog();
        tempDialog.setSender(Dialog.SENDER_USER);
        tempDialog.setQuestion(tempSurvey.getUser_answer().get(0).getTitle());
        //tempDialog.setAnswerType(Dialog.SENDER_USER);
        refreshRecycleView(tempDialog);

        if (GlobalData.getInstance().getmSocket().connected()) {

            if (tempSurvey.getType().toLowerCase().contentEquals("bot")) {
                try {
                    // if(tempObject.has("user_answer"))
                    tempObject.getJSONArray("user_answer").put(tempObject.getJSONArray("answers").get(index));
//                    else{
//
//                        JsonArray jsonArray = new JsonArray("")
//
//                    }

                    S.L("emmit", tempObject.toString());
                    GlobalData.getInstance().getmSocket().emit(tempSurvey.getUser_answer().get(0).getScoreOrEvent(), new JSONObject(tempObject.toString()));

                } catch (JSONException e) {

                    e.printStackTrace();
                }

            } else {


                try {

                    tempObject.getJSONArray("user_answer").put(tempObject.getJSONArray("answers").get(index));
                    S.L("emmit", tempObject.toString());
                    if (tempSurvey.isEnd())

                        GlobalData.getInstance().getmSocket().emit(TYPE + EMMIT_END_ANSWER, new JSONObject(tempObject.toString()));

                    else
                        GlobalData.getInstance().getmSocket().emit(TYPE + EMMIT_NEXT_ANSWER, new JSONObject(tempObject.toString()));


                } catch (JSONException e) {

                    e.printStackTrace();
                }


            }
        }
        etAnswer.setText("");


        //surveys.add(tempSurvey);
//        Dialog tempDialog = new Dialog();
//        tempDialog.setSender(Dialog.SENDER_USER);
//        tempDialog.setQuestion(tempSurvey.getUser_answer().get(0).getTitle());
//        tempDialog.setAnswerType(Dialog.SENDER_USER);
//        refreshRecycleView(tempDialog);
//
//
//        if (GlobalData.getInstance().getmSocket().connected()) {
//
//            if (tempSurvey.getType().toLowerCase().contentEquals("bot")) {
//
//
//                JSONObject object = new JSONObject();
//                try {
//
//                    object.put("surveyUUID", tempSurvey.getUser_answer().get(0).getNextOrSurveyId());
//                } catch (JSONException e) {
//
//                    e.printStackTrace();
//                }
//                if (tempSurvey.getUser_answer().get(0).getTitle().toLowerCase().contentEquals("yes"))
//                    GlobalData.getInstance().getmSocket().emit(tempSurvey.getUser_answer().get(0).getScoreOrEvent(), object);
//                else if (tempSurvey.getUser_answer().get(0).getTitle().toLowerCase().contentEquals("no"))
//                    GlobalData.getInstance().getmSocket().emit(tempSurvey.getUser_answer().get(0).getScoreOrEvent());
//
//
//            } else {
//
//                JSONObject object = new JSONObject();
//                try {
//
//                    object.put("survey_uuid", tempSurvey.getSurvey_uuid());
//                    object.put("id", tempSurvey.getId());
//                    object.put("question_no", tempSurvey.getQuestion_no());
//                    object.put("question", tempSurvey.getQuestion());
//                    object.put("weight", tempSurvey.getWeight());
//                    object.put("answer_type", tempSurvey.getAnswer_type());
//
//                    JSONArray array = new JSONArray();
//
//                    JSONObject answerObject = new JSONObject();
//                    answerObject.put("title", tempSurvey.getUser_answer().get(0).getTitle());
//                    answerObject.put("score", tempSurvey.getUser_answer().get(0).getScoreOrEvent());
//                    answerObject.put("next", tempSurvey.getUser_answer().get(0).getNextOrSurveyId());
//
//                    array.put(answerObject);
//                    object.put("user_answer", array);
//
//
//                } catch (JSONException e) {
//
//                    e.printStackTrace();
//                }
//
//                if (tempSurvey.isEnd())
//                    GlobalData.getInstance().getmSocket().emit(TYPE + EMMIT_END_ANSWER, object);
//                else
//                    GlobalData.getInstance().getmSocket().emit(TYPE + EMMIT_NEXT_ANSWER, object);
//
//            }
//        }
//        etAnswer.setText("");

        //findViewById(R.id.rlMain).invalidate();

    }

    public void sendClick(View view) {


        if (etAnswer.getText().toString().isEmpty())
            return;
        try {
            tempSurvey.getAnswers().get(0).setTitle(etAnswer.getText().toString());
            tempSurvey.setUser_answer(tempSurvey.getAnswers());
            surveys.add(tempSurvey);
             tempObject.getJSONArray("answers").getJSONObject(0).put("title", etAnswer.getText().toString());

            S.L("temp json",tempObject.toString());
            sendMessage(0);
            Utility.hideKeyboard(SurveyBotActivity.this);
        } catch (JSONException e) {

            e.printStackTrace();
        }

    }


    private void socketSetup(boolean connect) {
        if (connect) {

            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_SURVEY_ACTIVE, OnSurveyInitiated);
            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_SURVEY_DEACTIVE, OnSurveyInitiated);
            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_FIRST_QUESTION, getQuestion);
            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_NEXT_QUESTION, getQuestion);
            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_END_QUESTION, getQuestion);

            GlobalData.getInstance().getmSocket().emit(TYPE + EMMIT_SURVEY_ACTIVATE);

        } else {

            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_SURVEY_ACTIVE, OnSurveyInitiated);
            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_SURVEY_DEACTIVE, OnSurveyInitiated);
            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_FIRST_QUESTION, getQuestion);
            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_NEXT_QUESTION, getQuestion);
            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_END_QUESTION, getQuestion);

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
