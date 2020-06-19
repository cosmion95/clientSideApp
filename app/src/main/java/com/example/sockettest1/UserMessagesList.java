package com.example.sockettest1;

import java.util.ArrayList;

public class UserMessagesList {

    private User expeditor;
    private ArrayList<Message> messagesList;

    public UserMessagesList(User user) {
        expeditor = user;
    }

    public User getExpeditor() {
        return expeditor;
    }

    public ArrayList<Message> getMessagesList() {
        return messagesList;
    }

    public void addMessage(Message message) {
        messagesList.add(message);
    }
}
