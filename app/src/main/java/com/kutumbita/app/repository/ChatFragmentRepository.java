package com.kutumbita.app.repository;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kutumbita.app.GlobalData;
import com.kutumbita.app.chat.ChatBot;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.UrlConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ChatFragmentRepository {

    private static ChatFragmentRepository chatFragmentRepository;

    private ChatFragmentRepository() {


    }

    public synchronized static ChatFragmentRepository getInstance() {


        if (chatFragmentRepository == null) {

            chatFragmentRepository = new ChatFragmentRepository();
        }

        return chatFragmentRepository;

    }

    public LiveData<ArrayList<ChatBot>> chatLiveData(final String accessToken, final String lan) {

        final MutableLiveData<ArrayList<ChatBot>> chatBotListLiveData = new MutableLiveData<>();


        StringRequest request = new StringRequest(Request.Method.GET, UrlConstant.URL_AVAILABLE_BOTS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                S.L("chatbot", response);
                ArrayList<ChatBot> chatBots = new ArrayList<>();

                try {

                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);
                        ChatBot chatBot = new ChatBot();
                        chatBot.setIcon(object.getString("icon"));
                        chatBot.setSocket_key(object.getString("socket_key"));
                        JSONObject nameObject = object.getJSONObject("name");
                        chatBot.setName(nameObject.getString(lan.toLowerCase()));
                        chatBots.add(chatBot);
                    }

                    chatBotListLiveData.setValue(chatBots);

                } catch (JSONException e) {
                    e.printStackTrace();
                    chatBotListLiveData.setValue(null);
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                S.L("error getting chat data ");
                chatBotListLiveData.setValue(null);

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + accessToken);
                return params;
            }

        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GlobalData.getInstance().addToRequestQueue(request);

        return chatBotListLiveData;

    }
}
