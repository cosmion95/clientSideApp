package com.example.sockettest1;

import java.util.Date;

public class Message {

    private User user;
    private String msg;
    private String date;

    // 0 = mesaj trimis
    // 1 = mesaj primit
    private int type;

    private String read;

   /* Message(String msg, String date, int type){
        this.msg = msg;
        this.date = date;
        this.type = type;
    }*/

    public void setRead(String read) {
        this.read = read;
    }

    Message(User user, String msg, String date, int type, String read) {
        this.msg = msg;
        this.date = date;
        this.type = type;
        this.user = user;
        this.read = read;
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

    public User getUser() {
        return user;
    }

    public String getRead() {
        return read;
    }
}
