package com.kutumbita.app.chat;

import java.io.Serializable;

public class Dialog implements Serializable {


    private String questionOrPhotoPath, type;
    private int sender;

    private boolean isEnd;

    public static final int SENDER_USER = 1;
    public static final int SENDER_BOT = 2;
    public static final int SENDER_USER_WITH_PHOTO = 3;

    public Dialog(int sender, String questionOrPhotoPath, String type) {
        this.sender = sender;
        this.questionOrPhotoPath = questionOrPhotoPath;
        this.type = type;
        isEnd = false;


    }





    public boolean isEnd() {
        return isEnd;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public String getQuestionOrPhotoPath() {
        return questionOrPhotoPath;
    }

    public void setQuestionOrPhotoPath(String questionOrPhotoPath) {
        this.questionOrPhotoPath = questionOrPhotoPath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
