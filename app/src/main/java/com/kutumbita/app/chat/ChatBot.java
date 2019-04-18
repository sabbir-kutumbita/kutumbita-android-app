package com.kutumbita.app.chat;

import java.io.Serializable;

public class ChatBot implements Serializable {

    private String icon, socket_key, name;


    public ChatBot() {
    }

    public ChatBot(String icon, String socket_key, String name) {

        this.icon = icon;
        this.socket_key = socket_key;
        this.name = name;

    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSocket_key() {
        return socket_key;
    }

    public void setSocket_key(String socket_key) {
        this.socket_key = socket_key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
