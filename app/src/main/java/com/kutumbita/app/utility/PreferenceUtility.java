package com.kutumbita.app.utility;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.kutumbita.app.model.Me;

public class PreferenceUtility {

    private SharedPreferences pref;
    private Context c;

    public PreferenceUtility(Context c) {

        this.c = c;
        pref = c.getSharedPreferences(Constant.PREF_FILE_NAME, Context.MODE_PRIVATE);
    }


    public void setMe(Me me) {

        SharedPreferences.Editor edit = pref.edit();
        Gson gson = new Gson();
        String userString = gson.toJson(me);
        edit.putString(Constant.PREF_ME, userString);
        edit.commit();

    }


    public void deleteUser(Me me) {


        SharedPreferences.Editor editor = pref.edit();
        editor.remove(Constant.PREF_ME);
        editor.commit();

    }

    public Me getMe() {

        Gson gson = new Gson();
        String json = pref.getString(Constant.PREF_ME, "");
        Me me = gson.fromJson(json, Me.class);
        return me;

    }

    public void setFcmToken(String id) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(Constant.KEY_TOKEN, id);
        edit.commit();
    }

    public String getFcmToken() {

        return pref.getString(Constant.KEY_TOKEN, "");

    }

    public void setString(String key, String value) {
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public String getString(String key) {

        return pref.getString(key, "en");

    }

    public void deleteString(String key) {

        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();

    }

}
