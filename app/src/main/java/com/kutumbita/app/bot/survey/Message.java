package com.kutumbita.app.bot.survey;

import java.io.Serializable;

public class Message implements Serializable {


    String type;
    String msg;


    public Message(String type, String msg) {


        this.type = type;
        this.msg = msg;

    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
