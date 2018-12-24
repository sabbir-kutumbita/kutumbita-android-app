package com.kutumbita.app;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.kutumbita.app.adapter.SurveyAdapter;
import com.kutumbita.app.bot.survey.Message;
import com.kutumbita.app.bot.survey.Survey;
import com.kutumbita.app.bot.survey.SurveyResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SurveyBotActivity extends AppCompatActivity {


    public static final String URL = "https://9258cdee-766b-4546-8e14-cda0332699af.mock.pstmn.io/123";
    RecyclerView rcv;
    SurveyAdapter adapter;
    int questionPosition;
    Survey survey;
    ArrayList<Message> messages = new ArrayList<>();
    LinearLayout linearLayout;
    Message submittedMessage;
    ArrayList<SurveyResult> surveyResults = new ArrayList<>();
    ArrayList<String> answerArray = new ArrayList<>();
    ArrayList<Integer> positions = new ArrayList<>();
    SurveyResult result = new SurveyResult();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_bot);
        survey = new Survey();
        linearLayout = findViewById(R.id.ll);
        rcv = findViewById(R.id.rcv);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        rcv.setLayoutManager(manager);

        adapter = new SurveyAdapter(this, messages);
        rcv.setAdapter(adapter);
        parseSurvey();
    }

    private void parseSurvey() {

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    survey.setName(response.getString("name"));
                    JSONArray surveyArray = response.getJSONArray("survey");
                    ArrayList<Survey.Content> tempContents = new ArrayList();


                    for (int i = 0; i < surveyArray.length(); i++) {

                        JSONObject object = surveyArray.getJSONObject(i);
                        JSONArray answerArray = object.getJSONArray("answers");
                        ArrayList<Survey.Content.Answer> answers = new ArrayList();

                        for (int j = 0; j < answerArray.length(); j++) {


                            answers.add(new Survey.Content.Answer(answerArray.getString(j)));


                        }


                        tempContents.add(new Survey.Content(object.getString("question"),
                                object.getString("answer_type"), object.getString("question_uuid"), answers));


                    }

                    survey.setContents(tempContents);
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    loadChatMessage(new Message("bot", survey.getContents().get(questionPosition).getQuestion()));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Exception", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });

        GlobalData.getInstance().addToRequestQueue(objectRequest);

    }

    private void loadChatMessage(Message m) {


        messages.add(m);
        adapter.notifyItemInserted(messages.size());
        rcv.scrollToPosition(messages.size());
        Survey.Content tempContent = survey.getContents().get(questionPosition);
        switch (tempContent.getAnswerType()) {

            case "radiogroup":

                result = new SurveyResult();
                RadioGroup rg = new RadioGroup(this);
                rg.setOrientation(RadioGroup.VERTICAL);
                LinearLayout.LayoutParams RadioGroupParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                RadioGroupParams.gravity = Gravity.LEFT;
                RadioGroupParams.setMargins(20, 20, 20, 20);

                result.setQuestion_uuid(tempContent.getUuid());
                for (int i = 0; i < tempContent.getAnswers().size(); i++) {

                    RadioButton radioButton = new RadioButton(this);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(250,
                            130);


                    radioButton.setLayoutParams(params);
                    radioButton.setId(i);

                    radioButton.setTextColor(Color.parseColor("#000000"));
                    radioButton.setText(tempContent.getAnswers().get(i).getTitle());
                    //radioButton.setTextSize(30);

                    //Drawable d = getResources().getDrawable(R.drawable.buttonstate);
                    //radioButton.setBackground(d);
                    //radioButton.setGravity(Gravity.CENTER);
                    //radioButton.setButtonDrawable(new StateListDrawable());
                    //radioButton.setButtonDrawable(R.drawable.radiocustomize);
                    rg.addView(radioButton);
                }


                linearLayout.addView(rg, RadioGroupParams);

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        result.setAnswers(new int[]{checkedId});
                        submittedMessage = new Message("user", ((RadioButton) group.findViewById(checkedId)).getText().toString());

                    }
                });


                break;

            case "checkbox":

                result = new SurveyResult();
                result.setQuestion_uuid(tempContent.getUuid());

                for (int i = 0; i < tempContent.getAnswers().size(); i++) {
                    final int pos = i;
                    final CheckBox checkBox = new CheckBox(this);
                    LinearLayout.LayoutParams CheckBoxParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    CheckBoxParams.gravity = Gravity.LEFT;
                    CheckBoxParams.topMargin = 5;
                    checkBox.setText(tempContent.getAnswers().get(i).getTitle());
                    //checkBox.setTextSize(40);
                    //checkBox.setButtonDrawable(R.drawable.checkboxcustomize);
                    // checkBox.setTextAppearance(c, android.R.style.TextAppearance_Large);
                    linearLayout.addView(checkBox, CheckBoxParams);
                    //childLayout.setBackgroundColor(Color.WHITE);


                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                            StringBuilder builder = new StringBuilder();

                            if (isChecked) {
                                positions.add(pos);
                                answerArray.add(checkBox.getText().toString());
                            } else {
                                positions.remove(pos);
                                answerArray.remove(checkBox.getText().toString());
                            }
                            result.setAnswers(new int[answerArray.size()]);

                            for (int i = 0; i < answerArray.size(); i++) {

                                result.getAnswers()[i] = positions.get(i);
                                if (i > 0)
                                    builder.append(", ");
                                builder.append(answerArray.get(i));


                            }

                            //result.setPositions(positions.toArray(new int[positions.size()]));
                            submittedMessage = new Message("user", builder.toString());

                        }
                    });

                }


                break;


        }

    }

    public void sendClick(View view) {

        if (submittedMessage != null) {
            surveyResults.add(result);
            loadChatMessage(submittedMessage);

            linearLayout.removeAllViews();
            questionPosition++;

            if (questionPosition < survey.getContents().size()) {

                loadChatMessage(new Message("bot", survey.getContents().get(questionPosition).getQuestion()));

            } else {

                Gson gson = new Gson();
                String s = gson.toJson(surveyResults);
                Log.i("results", s);
                postReview();
            }
            submittedMessage = null;
        }

    }

    private void postReview() {


        final String body = "{ \"survey_result\" :" + new Gson().toJson(surveyResults) + "}";

        Log.i("result", body);
        StringRequest loginRequest = new StringRequest(Request.Method.POST, "https://9258cdee-766b-4546-8e14-cda0332699af.mock.pstmn.io/1b241101-e2bb-4255-8caf-4136c566a962",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("survey", response);
                        try {

                            JSONObject object = new JSONObject(response);


                            Toast.makeText(getApplicationContext(), object.getString("status"), Toast.LENGTH_LONG).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json exception", Toast.LENGTH_LONG).show();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("error", "" + error.networkResponse.statusCode);

                try {
                    String str = new String(error.networkResponse.data, "UTF-8");
                    JSONObject object = new JSONObject(str);
                    JSONObject errorObject = object.getJSONObject("error");
                    Toast.makeText(getApplicationContext(), errorObject.getString("message"), Toast.LENGTH_LONG).show();

                } catch (UnsupportedEncodingException e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();

                    e.printStackTrace();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return body == null ? null : body.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                            body, "utf-8");
                    return null;
                }
            }

        };
        loginRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GlobalData.getInstance().addToRequestQueue(loginRequest);

    }
}
