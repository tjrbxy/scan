package com.example.scan;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018-10-09.
 */

public class Res {
    int status;
    String message;

    public Res() {
    }

    public Res(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Res{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
