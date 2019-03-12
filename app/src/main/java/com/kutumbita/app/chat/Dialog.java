package com.kutumbita.app.chat;

import com.kutumbita.app.chat.Survey;

import java.io.Serializable;
import java.util.ArrayList;

public class Dialog implements Serializable {


    private String question, answerType, sender;
    private ArrayList<Survey.Answer> answers;

    public static final String SENDER_USER = "user";
    public static final String SENDER_BOT = "bot";

    public Dialog() {

    }

    public Dialog(String sender, String question, String answerType, ArrayList<Survey.Answer> answers) {
        this.sender = sender;
        this.question = question;
        this.answerType = answerType;
        this.answers = answers;

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

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public ArrayList<Survey.Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Survey.Answer> answers) {
        this.answers = answers;
    }
}
