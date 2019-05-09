package com.kutumbita.app;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kutumbita.app.adapter.DialogAdapter;
import com.kutumbita.app.chat.ChatBot;
import com.kutumbita.app.chat.Dialog;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.Utility;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.socket.emitter.Emitter;

public class BotActivity extends AppCompatActivity {


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

    ChatBot gotChatBot;


    ArrayList<Dialog> dialogs = new ArrayList<>();

    LinearLayout linearLayoutOthers, linearLayoutEt;
    RecyclerView rcv;
    EditText etAnswer;
    DialogAdapter adapter;
    Dialog tempDialog;

    JSONObject tempObject;
    ArrayList<JSONObject> jsonObjects = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        Utility.setOrientation(this, GlobalData.getInstance().getOrientation());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_bot);
        GlobalData.getInstance().setTouchTime(System.currentTimeMillis());
        gotChatBot = (ChatBot) getIntent().getSerializableExtra(Constant.EXTRA_CHAT_BOT);
        TYPE = gotChatBot.getSocket_key();
        preferenceUtility = new PreferenceUtility(this);
        layout = findViewById(R.id.header);
        ((TextView) layout.findViewById(R.id.tvTbTitle)).setText(gotChatBot.getName());

        linearLayoutOthers = findViewById(R.id.ll);
        linearLayoutEt = findViewById(R.id.ll2);
        rcv = findViewById(R.id.rcv);
        etAnswer = findViewById(R.id.etMessage);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        rcv.setLayoutManager(manager);


        jsonObjects = new ArrayList<>();
        adapter = new DialogAdapter(this, dialogs);

        adapter.liveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {


                    int termination = dialogs.size() < 4 ? dialogs.size() : 4;
                    for (int i = 0; i < termination; i++) {
                        dialogs.remove(dialogs.size() - 1);
                    }

                    jsonObjects.remove(jsonObjects.size() - 1);
                    tempObject = jsonObjects.get(jsonObjects.size() - 1);


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

            handleBotData(args[0]);

        }
    };

    Emitter.Listener OnStartConfirmation = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {

            handleBotData(args[0]);


        }
    };

    Emitter.Listener getIssueQuestion = new Emitter.Listener() {


        @Override
        public void call(final Object... args) {

            handleBotData(args[0]);

        }
    };


    private void handleBotData(final Object arg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    tempObject = (JSONObject) arg;
                    S.L("bot json", tempObject.toString());
                    if (tempObject.has("end") ? tempObject.getBoolean("end") : false) {
                        loadNoneAnswerTypeMessage();
                        return;
                    }

                    loadChatMessage(tempObject.getString("type"), tempObject.getString("answer_type"));

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json exception", Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    private void refreshRecycleView(Dialog d) {

        dialogs.add(d);
        adapter.notifyDataSetChanged();
        //adapter.notifyItemInserted(dialogs.size()-1);
        rcv.scrollToPosition(dialogs.size() - 1);
    }


    private void loadChatMessage(String type, String answerType) {

        try {
            if (type.toLowerCase().contentEquals("bot")) {


                if (answerType.toLowerCase().contentEquals("none")) {

                    loadNoneAnswerTypeMessage();
                    return;

                }
            }

            loadNormalMessage();
        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

    private void loadNoneAnswerTypeMessage() throws JSONException {


        tempDialog = new Dialog(Dialog.SENDER_BOT, tempObject.getString("question"), tempObject.getString("type"));
        tempDialog.setEnd(true);
        refreshRecycleView(tempDialog);
        loadFinishRadioButton();


    }


    private void loadNormalMessage() throws JSONException {


        tempDialog = new Dialog(Dialog.SENDER_BOT, tempObject.getString("question"), tempObject.getString("type"));
        refreshRecycleView(tempDialog);
        linearLayoutOthers.removeAllViews();

        switch (tempObject.getString("answer_type").toLowerCase()) {


            case "image":
                makeEditable(false);
                loadImageUploadingLayout();

                break;


            case "free_text":

                makeEditable(true);

                break;

            case "radio":

                makeEditable(false);
                linearLayoutOthers.removeAllViews();
                RadioGroup rg = new RadioGroup(this);
                rg.setOrientation(RadioGroup.VERTICAL);

                for (int i = 0; i < tempObject.getJSONArray("answers").length(); i++) {


                    RadioButton radioButton = new RadioButton(this);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            110);

                    if (GlobalData.getInstance().getOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT)
                        params.height = 110;
                    else
                        params.height = 80;
                    radioButton.setLayoutParams(params);
                    radioButton.setId(i);

                    radioButton.setTextColor(getResources().getColor(R.color.primaryColor));
                    radioButton.setText(tempObject.getJSONArray("answers").getJSONObject(i).getString("title"));

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


                linearLayoutOthers.addView(rg);

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {


                        try {
                            tempObject.getJSONArray("user_answer").put(tempObject.getJSONArray("answers").get(checkedId));
                            jsonObjects.add(tempObject);
                            sendMessage();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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


    }


    static final int CAMERA_CAPTURE_REQUEST = 1;
    final int GALLERY_IMAGE_REQUEST = 2;

    private Uri picUri;

    String mCurrentPhotoPath;

    private void loadImageUploadingLayout() {
        View uploaderView = ((LayoutInflater) getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.image_uploader, null);

        uploaderView.findViewById(R.id.tvCam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //use standard intent to capture an image
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/picture.jpg";
                    File imageFile = new File(imageFilePath);
                    picUri = Uri.fromFile(imageFile); // convert path to Uri
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                    startActivityForResult(takePictureIntent, CAMERA_CAPTURE_REQUEST);
                } catch (ActivityNotFoundException anfe) {

                    String errorMessage = "Whoops - your device doesn't support capturing images!";
                    Toast.makeText(BotActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        uploaderView.findViewById(R.id.tvUpload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, GALLERY_IMAGE_REQUEST);
            }
        });
        linearLayoutOthers.addView(uploaderView);
        linearLayoutOthers.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == CAMERA_CAPTURE_REQUEST) {


            }
        }
    }

    private void sendMessage() {


        if (GlobalData.getInstance().getmSocket().connected()) {

            try {
                tempDialog = new Dialog(Dialog.SENDER_USER, tempObject.getJSONArray("user_answer").getJSONObject(0).getString("title"), tempObject.getString("type"));
                refreshRecycleView(tempDialog);
                if (tempObject.getString("type").toLowerCase().contentEquals("bot")) {
                    GlobalData.getInstance().getmSocket().emit(tempObject.getJSONArray("user_answer").getJSONObject(0).getString("event"), new JSONObject(tempObject.toString()));
                } else {
                    if (tempObject.getBoolean("end"))
                        GlobalData.getInstance().getmSocket().emit(TYPE + EMMIT_END_ANSWER, new JSONObject(tempObject.toString()));
                    else
                        GlobalData.getInstance().getmSocket().emit(TYPE + EMMIT_NEXT_ANSWER, new JSONObject(tempObject.toString()));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        etAnswer.setText("");


    }

    public void sendClick(View view) {


        if (etAnswer.getText().toString().isEmpty())
            return;


        try {
            tempObject.getJSONArray("answers").getJSONObject(0).put("title", etAnswer.getText().toString());
            tempObject.getJSONArray("user_answer").put(tempObject.getJSONArray("answers").get(0));
            jsonObjects.add(tempObject);
            sendMessage();
            Utility.hideKeyboard(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void makeEditable(boolean b) {


        if (b) {


            linearLayoutEt.setVisibility(View.VISIBLE);
            linearLayoutOthers.setVisibility(View.GONE);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rcv.getLayoutParams();

            layoutParams.addRule(RelativeLayout.ABOVE, R.id.ll2);

            rcv.setLayoutParams(layoutParams);


        } else {


            linearLayoutOthers.setVisibility(View.VISIBLE);
            linearLayoutEt.setVisibility(View.GONE);


            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) rcv.getLayoutParams();

            layoutParams.addRule(RelativeLayout.ABOVE, R.id.ll);

            rcv.setLayoutParams(layoutParams);

        }


    }

    private void loadFinishRadioButton() {
        makeEditable(false);
        linearLayoutOthers.removeAllViews();
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
        linearLayoutOthers.addView(rg);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {


                finish();
            }
        });


    }

    private void socketSetup(boolean connect) {
        if (connect) {


            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_BOT_ACTIVE, OnBotActivated);
            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_BOT_DEACTIVE, OnBotActivated);
            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_FIRST_QUESTION, getIssueQuestion);
            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_NEXT_QUESTION, getIssueQuestion);
            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_END_QUESTION, getIssueQuestion);

            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_FEEDBACK_QUESTION, OnBotActivated);
            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_START_CONFIRMATION, OnStartConfirmation);
            GlobalData.getInstance().getmSocket().on(TYPE + RECEIVE_CATEGORY_ANSWER, OnStartConfirmation);


            GlobalData.getInstance().getmSocket().emit(TYPE + EMMIT_BOT_ACTIVATE);


        } else {

            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_BOT_ACTIVE, OnBotActivated);
            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_BOT_DEACTIVE, OnBotActivated);
            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_FIRST_QUESTION, getIssueQuestion);
            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_NEXT_QUESTION, getIssueQuestion);
            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_END_QUESTION, getIssueQuestion);

            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_FEEDBACK_QUESTION, OnBotActivated);
            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_CATEGORY_ANSWER, OnStartConfirmation);
            GlobalData.getInstance().getmSocket().off(TYPE + RECEIVE_START_CONFIRMATION, OnStartConfirmation);


        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socketSetup(false);
    }


}
