package com.example.sockettest1;

import java.util.Date;

public class Message {

    private String msg;
    private String date;

    // 0 = mesaj trimis
    // 1 = mesaj primit
    private int type;

    Message(String msg, String date, int type){
        this.msg = msg;
        this.date = date;
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public String getDate() {
        return date;
    }

    public int getType() {
        return type;
    }
}
