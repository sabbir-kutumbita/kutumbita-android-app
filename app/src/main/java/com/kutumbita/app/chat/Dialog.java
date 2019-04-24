package com.kutumbita.app.chat;

import com.kutumbita.app.chat.Survey;

import java.io.Serializable;
import java.util.ArrayList;

public class Dialog implements Serializable {


    private String question, type, sender;


    private boolean isEnd = false;

    public static final String SENDER_USER = "user";
    public static final String SENDER_BOT = "bot";

    public Dialog() {

    }

    public Dialog(String sender, String question, String type) {
        this.sender = sender;
        this.question = question;
        this.type = type;


    }


    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {

        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
