package com.kutumbita.app.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kutumbita.app.GlobalData;
import com.kutumbita.app.model.Inbox;
import com.kutumbita.app.utility.Constant;
import com.kutumbita.app.utility.S;
import com.kutumbita.app.utility.UrlConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InboxRepository {

    private static InboxRepository inboxRepository;

    private InboxRepository() {


    }

    public synchronized static InboxRepository getInstance() {


        if (inboxRepository == null)
            inboxRepository = new InboxRepository();
        return inboxRepository;

    }

    public LiveData<ArrayList<Inbox>> getInboxLiveData(int pageNumber, int numberOfItemInASinglePage, final String accessToken) {

        final MutableLiveData<ArrayList<Inbox>> inboxLiveData = new MutableLiveData<>();

        StringRequest inboxRequest = new StringRequest(Request.Method.GET, UrlConstant.URL_INBOX + "?page=" + pageNumber + "&page_size=" + numberOfItemInASinglePage, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<Inbox> inboxes = new ArrayList<>();
                S.L(response);

                try {
                    JSONObject object = new JSONObject(response);
                    JSONObject paginatorObject = object.getJSONObject("paginator");
                    JSONArray jsonArray = object.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject resultObject = jsonArray.getJSONObject(i);
                        JSONObject messageTypeObject = resultObject.getJSONObject("message_type");
                        inboxes.add(new Inbox(resultObject.getString("uuid"), resultObject.getString("title"), resultObject.getString("message_body"),
                                resultObject.getString("sent_at"), resultObject.getString("timezone"),
                                resultObject.getString("company_uuid"), resultObject.getString("link"),
                                resultObject.getString("venue"), resultObject.getString("start_date_time"), resultObject.getString("start_date_time"),
                                resultObject.getString("image"),
                                new Inbox.Paginator(paginatorObject.getInt("total_count"), paginatorObject.getInt("page_size"), paginatorObject.getInt("total_pages"), paginatorObject.getInt("current_page")),
                                new Inbox.MessageType(messageTypeObject.getString("uuid"), messageTypeObject.getString("title"),
                                messageTypeObject.getString("icon"))));

                    }

                    inboxLiveData.setValue(inboxes);

                } catch (JSONException e) {

                    inboxLiveData.setValue(null);

                    e.printStackTrace();

                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                inboxLiveData.setValue(null);
                try {
                    String str = new String(error.networkResponse.data, "UTF-8");
                    JSONObject object = new JSONObject(str);
                    JSONObject errorObject = object.getJSONObject("error");
                    //S.T(getActivity(), errorObject.getString("message"));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + accessToken);
                return params;
            }


        };

        inboxRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        GlobalData.getInstance().addToRequestQueue(inboxRequest);

        return inboxLiveData;

    }
}
