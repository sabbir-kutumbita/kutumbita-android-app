package com.kutumbita.app.model;

import java.io.Serializable;

public class ServerResponse implements Serializable {

    private boolean isSuccess;
    private String message;

    public ServerResponse(boolean isSuccess, String message) {

        this.isSuccess = isSuccess;
        this.message = message;


    }

    public boolean isSucceess() {
        return isSuccess;
    }

    public void setSucceess(boolean succeess) {
        isSuccess = succeess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
