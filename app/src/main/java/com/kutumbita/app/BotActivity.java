package com.kutumbita.app;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.kutumbita.app.adapter.DialogAdapter;
import com.kutumbita.app.chat.ChatBot;
import com.kutumbita.app.chat.Dialog;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.PreferenceUtility;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.UrlConstant;
import com.kutumbita.app.utility.Utility;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import dmax.dialog.SpotsDialog;
import io.socket.emitter.Emitter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

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

    private volatile boolean running = true;
    View layout;
    PreferenceUtility preferenceUtility;
    Thread uploadThread;
    RunUpload uploadRunnable;
    ChatBot gotChatBot;


    ArrayList<Dialog> dialogs = new ArrayList<>();
    AlertDialog alertDialog;
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
        alertDialog = new SpotsDialog.Builder().setContext(this).build();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(getResources().getString(R.string.uploading));
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


                    sendMessage(Dialog.SENDER_USER);


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
                            ViewGroup.LayoutParams.WRAP_CONTENT);


                    radioButton.setLayoutParams(params);
                    radioButton.setId(i);

                    radioButton.setTextColor(getResources().getColor(R.color.primaryColor));
                    radioButton.setText(tempObject.getJSONArray("answers").getJSONObject(i).getString("title"));

                    radioButton.setTextSize(16);
                    radioButton.setPadding(25, 25, 25, 25);

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
                            sendMessage(Dialog.SENDER_USER);
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
    static final int CROP_PIC_REQUEST = 3;
    public static String currentPhotoPath = "";

    private void loadImageUploadingLayout() {
        View uploaderView = ((LayoutInflater) getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.image_uploader, null);

        uploaderView.findViewById(R.id.tvCam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                    File photoFile = null;
                    try {
                        photoFile = Utility.createImageFile(BotActivity.this);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }

                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(BotActivity.this,
                                "com.kutumbita.app.fileprovider",
                                photoFile);

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, CAMERA_CAPTURE_REQUEST);
                    }
                }


            }
        });

        uploaderView.findViewById(R.id.tvUpload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_IMAGE_REQUEST);
            }
        });
        linearLayoutOthers.addView(uploaderView);
        linearLayoutOthers.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case CAMERA_CAPTURE_REQUEST:


                    uploadImage();


                    break;


                case GALLERY_IMAGE_REQUEST:

                    break;
            }


        } else {

            S.T(BotActivity.this, getString(R.string.something_went_wrong));
        }

    }


    private void sendMessage(int senderType) {

        if (senderType == Dialog.SENDER_USER_WITH_PHOTO)
            alertDialog.dismiss();

        if (GlobalData.getInstance().getmSocket().connected()) {

            try {

                tempDialog = new Dialog(senderType, tempObject.getJSONArray("user_answer").getJSONObject(0).getString("title"), tempObject.getString("type"));

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


    private void uploadImage() {

        try {

            uploadRunnable = new RunUpload();
            uploadThread = new Thread(uploadRunnable);
            uploadThread.start();

        } catch (Exception e) {
            S.T(BotActivity.this, getString(R.string.something_went_wrong));
            e.printStackTrace();
        }


    }


    public void sendClick(View view) {


        if (etAnswer.getText().toString().isEmpty())
            return;


        try {
            tempObject.getJSONArray("answers").getJSONObject(0).put("title", etAnswer.getText().toString());
            tempObject.getJSONArray("user_answer").put(tempObject.getJSONArray("answers").get(0));
            jsonObjects.add(tempObject);
            sendMessage(Dialog.SENDER_USER);
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
        if (uploadThread != null)
            uploadThread.interrupt();
    }


    private class RunUpload implements Runnable {


        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alertDialog.show();
                }
            });

            if (!uploadThread.isInterrupted()) {
                Bitmap ResizedBitmap = Utility.getResizedBitmap(BitmapFactory.decodeFile(currentPhotoPath), 1000);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ResizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), ResizedBitmap, "issue_android_image", null);
                Cursor cursor = getContentResolver().query(Uri.parse(path), null, null, null, null);
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                String photoPath = cursor.getString(idx);

                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("avatar", "issue_image_android",
                        RequestBody.create(MediaType.parse("image/jpeg"), new File(photoPath))).build();
                final okhttp3.Request uploadRequest = new okhttp3.Request.Builder().url(UrlConstant.IMAGE_UPLOAD).
                        header("Authorization", "Bearer " + preferenceUtility.getMe().getAccessToken())
                        .header("Content-Type", "multipart/form-data")
                        .header("Content-Type", "application/octet-stream").header("Connection", "close").header("Accept-Encoding", "identity").post(body)
                        .build();


                try {
                    final Response response = GlobalData.getInstance().okHttpClient.newCall(uploadRequest).execute();
                    if (!response.isSuccessful()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                                S.T(BotActivity.this, getString(R.string.something_went_wrong));
                            }
                        });

                    } else {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
//                                    tempDialog = new Dialog(Dialog.SENDER_USER_WITH_PHOTO, jsonObject.getString("imageUrl"), tempObject.getString("type"));
//                                    refreshRecycleView(tempDialog);
                                    tempObject.getJSONArray("answers").getJSONObject(0).put("title", jsonObject.getString("imageUrl"));
                                    tempObject.getJSONArray("user_answer").put(tempObject.getJSONArray("answers").get(0));
                                    sendMessage(Dialog.SENDER_USER_WITH_PHOTO);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    alertDialog.dismiss();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    alertDialog.dismiss();
                                }

                            }
                        });

                    }
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertDialog.dismiss();
                            S.T(BotActivity.this, getString(R.string.something_went_wrong));
                        }
                    });

                    e.printStackTrace();
                }
            }
        }

    }


}
