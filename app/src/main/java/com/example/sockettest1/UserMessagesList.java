package com.example.sockettest1;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UserMessagesList {

    private User expeditor;
    private ArrayList<Message> messagesList;
    private MessageAdapter adapter;

    public UserMessagesList(Context context, User user) {
        expeditor = user;
        messagesList = new ArrayList<Message>();
        adapter = new MessageAdapter(messagesList);
    }

    public UserMessagesList(Context context, User user, Message msg) {
        expeditor = user;
        messagesList = new ArrayList<Message>();
        messagesList.add(msg);
        adapter = new MessageAdapter(messagesList);
    }

    public UserMessagesList(Context context, User user, ArrayList<Message> messagesList) {
        expeditor = user;
        this.messagesList = messagesList;
        adapter = new MessageAdapter(messagesList);
    }

    public User getExpeditor() {
        return expeditor;
    }

    public void setAdapter(MessageAdapter adapter) {
        this.adapter = adapter;
    }

    public ArrayList<Message> getMessagesList() {
        return messagesList;
    }

    public MessageAdapter getAdapter() {
        return adapter;
    }

    public void addMessage(Message message) {
        messagesList.add(message);
    }

    public Message getLastMessage() {
        if (messagesList != null && !messagesList.isEmpty()) {
            return messagesList.get(messagesList.size() - 1);
        }
        return null;
    }

    public int getUnreadMessages() {
        int counter = 0;
        for (Message m : messagesList) {
            if (m.getType() == 1 && m.getRead().equals("N")) {
                counter++;
            }
        }
        return counter;
    }

}
