package com.kutumbita.app.utility;

public class UrlConstant {

    //local
    public static final String URL_BASE = "http://10.100.1.205:5100/android/api/v1/";

    //global
    //public static final String URL_BASE = "http://52.221.241.27/android/api/v1/";


    public static final String URL_LOGIN = URL_BASE + "auth/login";
    public static final String URL_LOGOUT = URL_BASE + "auth/logout";
    public static final String URL_INBOX = URL_BASE + "inbox";


    public static final String URL_ME = URL_BASE + "me";
    public static final String URL_INBOX_DETAILS = URL_BASE + "inbox/";
    public static final String URL_TOKEN = URL_BASE + "token";

    public static final String URL_SURVEY_SINGLE = URL_BASE + "surveys/5c372cae60b272198f3c0548";
    public static final String URL_SURVEY_ANSWER = URL_BASE + "surveys/";
    public static final String URL_SOCKET = "http://10.100.1.77:5200?token=";
}
