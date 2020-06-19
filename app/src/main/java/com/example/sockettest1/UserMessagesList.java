package com.example.sockettest1;

import android.content.Context;

import java.util.ArrayList;

public class UserMessagesList {

    private User expeditor;
    private ArrayList<Message> messagesList;
    private UserMessageAdapter adapter;

    public UserMessagesList(Context context, User user) {
        expeditor = user;
        messagesList = new ArrayList<Message>();
        adapter = new UserMessageAdapter(context, R.layout.user_message_item, messagesList);
    }

    public UserMessagesList(Context context, User user, Message msg) {
        expeditor = user;
        messagesList = new ArrayList<Message>();
        messagesList.add(msg);
        adapter = new UserMessageAdapter(context, R.layout.user_message_item, messagesList);
    }

    public User getExpeditor() {
        return expeditor;
    }

    public ArrayList<Message> getMessagesList() {
        return messagesList;
    }

    public UserMessageAdapter getAdapter() {
        return adapter;
    }

    public void addMessage(Message message) {
        messagesList.add(message);
    }

}
